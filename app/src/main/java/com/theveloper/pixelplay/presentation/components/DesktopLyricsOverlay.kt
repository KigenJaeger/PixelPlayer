package com.theveloper.pixelplay.presentation.components

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.SystemClock
import android.provider.Settings
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.theveloper.pixelplay.data.model.Lyrics
import com.theveloper.pixelplay.data.model.Song
import com.theveloper.pixelplay.data.preferences.AlbumArtColorAccuracy
import com.theveloper.pixelplay.data.preferences.AlbumArtPaletteStyle
import com.theveloper.pixelplay.data.preferences.AppThemeMode
import com.theveloper.pixelplay.data.preferences.DEFAULT_DESKTOP_LYRICS_TEXT_SCALE
import com.theveloper.pixelplay.data.preferences.DesktopLyricsAlignment
import com.theveloper.pixelplay.data.preferences.DesktopLyricsColorSource
import com.theveloper.pixelplay.data.preferences.DesktopLyricsMonetColor
import com.theveloper.pixelplay.data.preferences.MAX_DESKTOP_LYRICS_TEXT_SCALE
import com.theveloper.pixelplay.data.preferences.MIN_DESKTOP_LYRICS_TEXT_SCALE
import com.theveloper.pixelplay.data.preferences.ThemePreferencesRepository
import com.theveloper.pixelplay.data.preferences.UserPreferencesRepository
import com.theveloper.pixelplay.data.preferences.dataStore
import com.theveloper.pixelplay.data.repository.MusicRepository
import com.theveloper.pixelplay.presentation.viewmodel.ColorSchemePair
import com.theveloper.pixelplay.presentation.viewmodel.ColorSchemeProcessor
import com.theveloper.pixelplay.ui.theme.PixelPlayTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

private const val OVERLAY_WINDOW_WIDTH_DP = 340
private const val OVERLAY_WINDOW_HEIGHT_DP = 86
private const val OVERLAY_WINDOW_MARGIN_DP = 12
private const val DESKTOP_LYRICS_BASE_TEXT_SCALE = 0.5f
private const val DESKTOP_LYRICS_SONG_LOOKUP_ATTEMPTS = 4
private const val DESKTOP_LYRICS_SONG_LOOKUP_TIMEOUT_MS = 650L
private const val DESKTOP_LYRICS_SONG_REFRESH_TIMEOUT_MS = 220L
private const val DESKTOP_LYRICS_RETRY_DELAY_MS = 160L
private const val DESKTOP_LYRICS_STORED_LYRICS_ATTEMPTS = 4
private const val DESKTOP_LYRICS_SYNCED_PROGRESS_POLL_MS = 450L
private const val DESKTOP_LYRICS_ACTIVE_IDLE_POLL_MS = 1_200L
private const val DESKTOP_LYRICS_PAUSED_POLL_MS = 2_000L
private const val DESKTOP_LYRICS_DISABLED_POLL_MS = 2_500L

data class DesktopLyricsOverlayState(
    val appThemeMode: String = AppThemeMode.FOLLOW_SYSTEM,
    val currentSong: Song? = null,
    val lyrics: Lyrics? = null,
    val albumArtColorSchemePair: ColorSchemePair? = null,
    val lyricsSyncOffsetMs: Int = 0,
    val isPlaying: Boolean = false,
    val positionLocked: Boolean = false,
    val overlayBackgroundEnabled: Boolean = true,
    val desktopLyricsTextScale: Float = DEFAULT_DESKTOP_LYRICS_TEXT_SCALE,
    val desktopLyricsColorSource: String = DesktopLyricsColorSource.ALBUM_ART,
    val desktopLyricsAlignment: String = DesktopLyricsAlignment.CENTER,
    val monetColorStyle: String = DesktopLyricsMonetColor.PRIMARY
)

@OptIn(UnstableApi::class)
class DesktopLyricsOverlayController(
    private val context: Context,
    private val scope: CoroutineScope,
    private val playerProvider: () -> Player,
    private val musicRepository: MusicRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val themePreferencesRepository: ThemePreferencesRepository,
    private val colorSchemeProcessor: ColorSchemeProcessor
) {
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val _state = MutableStateFlow(DesktopLyricsOverlayState())

    val state: StateFlow<DesktopLyricsOverlayState> = _state.asStateFlow()
    val playbackPositionFlow = MutableStateFlow(0L)

    private var monitorJob: Job? = null
    private var overlayPollJob: Job? = null
    private var immediateSyncJob: Job? = null
    private var lyricsLoadJob: Job? = null
    private var syncOffsetJob: Job? = null
    private var albumArtColorSchemeJob: Job? = null
    private var observedPlayer: Player? = null
    private var overlayView: ComposeView? = null
    private var overlayParams: WindowManager.LayoutParams? = null
    private var overlayLifecycleOwner: OverlayLifecycleOwner? = null
    private var started = false
    private var overlayEnabled = false
    private var overlayLocked = false
    private var savedOverlayX = -1
    private var savedOverlayY = -1
    private var lastFullSyncRealtimeMs = 0L
    @Volatile
    private var currentSongId: String? = null
    @Volatile
    private var currentPaletteStyle: AlbumArtPaletteStyle = AlbumArtPaletteStyle.default
    @Volatile
    private var currentPaletteAccuracy: Int = AlbumArtColorAccuracy.DEFAULT
    @Volatile
    private var currentColorSource: String = DesktopLyricsColorSource.ALBUM_ART
    @Volatile
    private var currentAlignment: String = DesktopLyricsAlignment.CENTER
    private val playerListener = object : Player.Listener {
        override fun onEvents(player: Player, events: Player.Events) {
            if (
                events.contains(Player.EVENT_MEDIA_ITEM_TRANSITION) ||
                events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED) ||
                events.contains(Player.EVENT_IS_PLAYING_CHANGED) ||
                events.contains(Player.EVENT_PLAY_WHEN_READY_CHANGED) ||
                events.contains(Player.EVENT_POSITION_DISCONTINUITY) ||
                events.contains(Player.EVENT_TIMELINE_CHANGED)
            ) {
                requestImmediateSync()
            }
        }
    }

    fun start() {
        if (started) return
        started = true

        monitorJob = scope.launch {
            launch {
                userPreferencesRepository.desktopLyricsOverlayEnabledFlow
                    .distinctUntilChanged()
                    .collect { enabled ->
                        overlayEnabled = enabled
                        if (enabled) {
                            requestImmediateSync()
                        } else {
                            detachPlayerListener()
                            clearPlaybackState()
                            detachOverlay()
                        }
                    }
            }

            launch {
                userPreferencesRepository.desktopLyricsOverlayLockedFlow
                    .distinctUntilChanged()
                    .collect { locked ->
                        overlayLocked = locked
                        _state.update { it.copy(positionLocked = locked) }
                        updateOverlayTouchability()
                    }
            }

            launch {
                userPreferencesRepository.desktopLyricsOverlayBackgroundEnabledFlow
                    .distinctUntilChanged()
                    .collect { enabled ->
                        _state.update { it.copy(overlayBackgroundEnabled = enabled) }
                    }
            }

            launch {
                userPreferencesRepository.desktopLyricsTextScaleFlow
                    .distinctUntilChanged()
                    .collect { scale ->
                        _state.update { it.copy(desktopLyricsTextScale = scale) }
                    }
            }

            launch {
                userPreferencesRepository.desktopLyricsColorSourceFlow
                    .distinctUntilChanged()
                    .collect { source ->
                        currentColorSource = source
                        _state.update { it.copy(desktopLyricsColorSource = source) }
                        refreshAlbumArtColorScheme()
                    }
            }

            launch {
                userPreferencesRepository.desktopLyricsAlignmentFlow
                    .distinctUntilChanged()
                    .collect { alignment ->
                        currentAlignment = alignment
                        _state.update { it.copy(desktopLyricsAlignment = alignment) }
                        snapOverlayToAlignedEdge()
                    }
            }

            launch {
                userPreferencesRepository.desktopLyricsMonetColorFlow
                    .distinctUntilChanged()
                    .collect { color ->
                        _state.update { it.copy(monetColorStyle = color) }
                    }
            }

            launch {
                themePreferencesRepository.albumArtPaletteStyleFlow
                    .distinctUntilChanged()
                    .collect { style ->
                        currentPaletteStyle = style
                        refreshAlbumArtColorScheme()
                    }
            }

            launch {
                themePreferencesRepository.albumArtColorAccuracyFlow
                    .distinctUntilChanged()
                    .collect { accuracy ->
                        currentPaletteAccuracy = accuracy
                        refreshAlbumArtColorScheme()
                    }
            }

            launch {
                userPreferencesRepository.desktopLyricsOverlayPositionFlow
                    .distinctUntilChanged()
                    .collect { (x, y) ->
                        savedOverlayX = x
                        savedOverlayY = y
                        applySavedOverlayPosition()
                    }
            }

            launch {
                themePreferencesRepository.appThemeModeFlow
                    .distinctUntilChanged()
                    .collect { themeMode ->
                        _state.update { it.copy(appThemeMode = themeMode) }
                    }
            }

            overlayPollJob = launch {
                while (isActive) {
                    val now = SystemClock.elapsedRealtime()
                    val hasOverlayPermission = Settings.canDrawOverlays(context)
                    if (overlayEnabled && hasOverlayPermission) {
                        updateObservedPlayer()
                    } else {
                        detachPlayerListener()
                    }

                    if (shouldUpdateOnlyPlaybackPosition(hasOverlayPermission, now)) {
                        updatePlaybackPositionFromPlayer()
                    } else {
                        syncFromPlayer(hasOverlayPermission)
                        lastFullSyncRealtimeMs = now
                    }

                    delay(nextOverlayPollDelay())
                }
            }
        }
    }

    fun stop() {
        monitorJob?.cancel()
        overlayPollJob?.cancel()
        immediateSyncJob?.cancel()
        lyricsLoadJob?.cancel()
        syncOffsetJob?.cancel()
        albumArtColorSchemeJob?.cancel()
        detachPlayerListener()
        detachOverlay()
        started = false
    }

    fun clearPlaybackState() {
        currentSongId = null
        lyricsLoadJob?.cancel()
        syncOffsetJob?.cancel()
        albumArtColorSchemeJob?.cancel()
        playbackPositionFlow.value = 0L
        _state.update {
            it.copy(
                currentSong = null,
                lyrics = null,
                albumArtColorSchemePair = null,
                lyricsSyncOffsetMs = 0,
                isPlaying = false
            )
        }
    }

    fun moveOverlayBy(deltaX: Float, deltaY: Float) {
        if (overlayLocked) return
        val view = overlayView ?: return
        val params = overlayParams ?: return
        val width = view.width.takeIf { it > 0 } ?: overlayWidthPx()
        val height = view.height.takeIf { it > 0 } ?: dpToPx(OVERLAY_WINDOW_HEIGHT_DP)
        val displayMetrics = context.resources.displayMetrics
        val horizontalMargin = horizontalOverlayMargin()
        val verticalMargin = dpToPx(OVERLAY_WINDOW_MARGIN_DP)
        val maxX = (displayMetrics.widthPixels - width - horizontalMargin).coerceAtLeast(horizontalMargin)
        val maxY = (displayMetrics.heightPixels - height - verticalMargin).coerceAtLeast(verticalMargin)

        params.x = (params.x + deltaX.roundToInt()).coerceIn(horizontalMargin, maxX)
        params.y = (params.y + deltaY.roundToInt()).coerceIn(verticalMargin, maxY)

        runCatching {
            windowManager.updateViewLayout(view, params)
        }.onFailure { error ->
            Timber.tag("DesktopLyricsOverlay").w(error, "Failed to move desktop lyrics overlay")
        }
    }

    fun persistOverlayPosition() {
        val params = overlayParams ?: return
        scope.launch {
            userPreferencesRepository.setDesktopLyricsOverlayPosition(params.x, params.y)
        }
    }

    private fun snapOverlayToAlignedEdge() {
        val view = overlayView ?: return
        val params = overlayParams ?: return
        val width = view.width.takeIf { it > 0 } ?: overlayWidthPx()
        val displayWidth = context.resources.displayMetrics.widthPixels
        val nextX = when (DesktopLyricsAlignment.sanitize(currentAlignment)) {
            DesktopLyricsAlignment.START -> 0
            DesktopLyricsAlignment.END -> (displayWidth - width).coerceAtLeast(0)
            else -> params.x.coerceIn(horizontalOverlayMargin(), maxOverlayX(width))
        }
        if (params.x == nextX) return

        params.x = nextX
        runCatching { windowManager.updateViewLayout(view, params) }
        persistOverlayPosition()
    }

    private fun requestImmediateSync() {
        if (!started || !overlayEnabled) return
        if (immediateSyncJob?.isActive == true) return
        immediateSyncJob = scope.launch {
            val hasOverlayPermission = Settings.canDrawOverlays(context)
            if (hasOverlayPermission) {
                updateObservedPlayer()
            } else {
                detachPlayerListener()
            }
            syncFromPlayer(hasOverlayPermission)
            lastFullSyncRealtimeMs = SystemClock.elapsedRealtime()
        }
    }

    private fun updateObservedPlayer() {
        val player = playerProvider()
        if (observedPlayer === player) return

        observedPlayer?.removeListener(playerListener)
        observedPlayer = player
        player.addListener(playerListener)
    }

    private fun detachPlayerListener() {
        observedPlayer?.removeListener(playerListener)
        observedPlayer = null
    }

    private fun nextOverlayPollDelay(): Long {
        if (!overlayEnabled || !Settings.canDrawOverlays(context)) {
            return DESKTOP_LYRICS_DISABLED_POLL_MS
        }

        val state = _state.value
        return when {
            state.isPlaying && !state.lyrics?.synced.isNullOrEmpty() ->
                DESKTOP_LYRICS_SYNCED_PROGRESS_POLL_MS
            state.isPlaying ->
                DESKTOP_LYRICS_ACTIVE_IDLE_POLL_MS
            else ->
                DESKTOP_LYRICS_PAUSED_POLL_MS
        }
    }

    private fun shouldUpdateOnlyPlaybackPosition(
        hasOverlayPermission: Boolean,
        now: Long
    ): Boolean {
        if (lastFullSyncRealtimeMs == 0L) return false

        val state = _state.value
        return overlayEnabled &&
            hasOverlayPermission &&
            state.isPlaying &&
            !state.lyrics?.synced.isNullOrEmpty() &&
            now - lastFullSyncRealtimeMs < DESKTOP_LYRICS_ACTIVE_IDLE_POLL_MS
    }

    private fun updatePlaybackPositionFromPlayer() {
        playbackPositionFlow.value = playerProvider().currentPosition.coerceAtLeast(0L)
    }

    private suspend fun syncFromPlayer(
        hasOverlayPermission: Boolean = Settings.canDrawOverlays(context)
    ) {
        if (!overlayEnabled || !hasOverlayPermission) {
            detachPlayerListener()
            detachOverlay()
            if (!overlayEnabled) clearPlaybackState()
            return
        }

        ensureOverlayAttached()

        val player = playerProvider()
        playbackPositionFlow.value = player.currentPosition.coerceAtLeast(0L)

        val songId = player.currentMediaItem?.mediaId?.takeIf { it.isNotBlank() }
        val isIdle = player.playbackState == Player.STATE_IDLE
        val isPlaying = player.isPlaying

        if (songId == null || isIdle) {
            if (currentSongId != null) {
                clearPlaybackState()
            } else {
                _state.update { it.copy(isPlaying = isPlaying) }
            }
            return
        }

        if (songId != currentSongId) {
            currentSongId = songId
            lyricsLoadJob?.cancel()
            syncOffsetJob?.cancel()
            albumArtColorSchemeJob?.cancel()
            _state.update {
                it.copy(
                    currentSong = null,
                    lyrics = null,
                    albumArtColorSchemePair = null,
                    lyricsSyncOffsetMs = 0,
                    isPlaying = isPlaying
                )
            }

            lyricsLoadJob = scope.launch { loadLyricsForSong(songId) }
            syncOffsetJob = scope.launch {
                userPreferencesRepository.getLyricsSyncOffsetFlow(songId)
                    .distinctUntilChanged()
                    .collect { offset ->
                        _state.update { state ->
                            if (state.currentSong?.id == songId) {
                                state.copy(lyricsSyncOffsetMs = offset)
                            } else {
                                state
                            }
                        }
                    }
            }
        } else {
            _state.update { it.copy(isPlaying = isPlaying) }
            if (_state.value.currentSong == null && lyricsLoadJob?.isActive != true) {
                lyricsLoadJob = scope.launch { loadLyricsForSong(songId) }
            }
        }
    }

    private suspend fun loadLyricsForSong(songId: String) {
        val song = resolveSongForOverlay(songId) ?: return

        if (song.id != currentSongId) return
        _state.update { it.copy(currentSong = song) }
        refreshAlbumArtColorScheme()

        val (latestSong, storedLyrics) = loadStoredLyricsForOverlay(song)

        if (latestSong.id != currentSongId) return
        _state.update {
            it.copy(
                currentSong = latestSong,
                lyrics = storedLyrics
            )
        }

        refreshAlbumArtColorScheme()
    }

    private suspend fun resolveSongForOverlay(songId: String): Song? {
        repeat(DESKTOP_LYRICS_SONG_LOOKUP_ATTEMPTS) { attempt ->
            val song = awaitSongSnapshot(songId, DESKTOP_LYRICS_SONG_LOOKUP_TIMEOUT_MS)

            if (song != null) return song
            if (currentSongId != songId) return null
            if (attempt < DESKTOP_LYRICS_SONG_LOOKUP_ATTEMPTS - 1) {
                delay(DESKTOP_LYRICS_RETRY_DELAY_MS)
            }
        }

        Timber.tag("DesktopLyricsOverlay").d("Song lookup timed out for desktop lyrics: %s", songId)
        return null
    }

    private suspend fun loadStoredLyricsForOverlay(initialSong: Song): Pair<Song, Lyrics?> {
        var latestSong = initialSong

        repeat(DESKTOP_LYRICS_STORED_LYRICS_ATTEMPTS) { attempt ->
            if (currentSongId != initialSong.id) return latestSong to null

            if (attempt > 0) {
                awaitSongSnapshot(initialSong.id, DESKTOP_LYRICS_SONG_REFRESH_TIMEOUT_MS)?.let { refreshedSong ->
                    latestSong = refreshedSong
                }
            }

            val lyrics = withContext(Dispatchers.IO) {
                musicRepository.getStoredLyrics(latestSong)?.first
            }

            if (lyrics != null) return latestSong to lyrics
            if (attempt < DESKTOP_LYRICS_STORED_LYRICS_ATTEMPTS - 1) {
                delay(DESKTOP_LYRICS_RETRY_DELAY_MS)
            }
        }

        return latestSong to null
    }

    private suspend fun awaitSongSnapshot(songId: String, timeoutMs: Long): Song? {
        return withContext(Dispatchers.IO) {
            withTimeoutOrNull(timeoutMs) {
                musicRepository.getSong(songId).firstOrNull { candidate ->
                    candidate?.id == songId
                }
            }
        }
    }

    private fun refreshAlbumArtColorScheme() {
        if (currentColorSource != DesktopLyricsColorSource.ALBUM_ART) {
            albumArtColorSchemeJob?.cancel()
            _state.update { it.copy(albumArtColorSchemePair = null) }
            return
        }

        val song = _state.value.currentSong
        if (song == null) {
            albumArtColorSchemeJob?.cancel()
            _state.update { it.copy(albumArtColorSchemePair = null) }
            return
        }

        val albumArtUri = song.albumArtUriString?.takeIf { it.isNotBlank() }
        if (albumArtUri == null) {
            albumArtColorSchemeJob?.cancel()
            _state.update { it.copy(albumArtColorSchemePair = null) }
            return
        }

        val songId = song.id
        val paletteStyle = currentPaletteStyle
        val colorAccuracyLevel = currentPaletteAccuracy

        albumArtColorSchemeJob?.cancel()
        albumArtColorSchemeJob = scope.launch(Dispatchers.IO) {
            val schemePair = colorSchemeProcessor.getOrGenerateColorScheme(
                albumArtUri = albumArtUri,
                paletteStyle = paletteStyle,
                colorAccuracyLevel = colorAccuracyLevel
            )

            if (
                songId == currentSongId &&
                currentColorSource == DesktopLyricsColorSource.ALBUM_ART &&
                currentPaletteStyle == paletteStyle &&
                currentPaletteAccuracy == colorAccuracyLevel
            ) {
                _state.update { it.copy(albumArtColorSchemePair = schemePair) }
            }
        }
    }

    private fun ensureOverlayAttached() {
        if (overlayView != null) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Settings.canDrawOverlays(context)) {
            return
        }

        val lifecycleOwner = OverlayLifecycleOwner().also { it.create() }
        val composeView = ComposeView(context).apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnDetachedFromWindowOrReleasedFromPool
            )
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeSavedStateRegistryOwner(lifecycleOwner)
            setContent {
                DesktopLyricsOverlayContent(
                    controller = this@DesktopLyricsOverlayController
                )
            }
        }

        val widthPx = overlayWidthPx()
        val params = WindowManager.LayoutParams(
            widthPx,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            overlayFlags(),
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.START or Gravity.TOP
            x = initialOverlayX(widthPx)
            y = initialOverlayY()
            title = "PixelPlay Desktop Lyrics"
        }

        overlayLifecycleOwner = lifecycleOwner
        overlayView = composeView
        overlayParams = params
        runCatching {
            windowManager.addView(composeView, params)
        }.onFailure { error ->
            Timber.tag("DesktopLyricsOverlay").w(error, "Failed to add desktop lyrics overlay")
            overlayView = null
            overlayParams = null
            overlayLifecycleOwner?.destroy()
            overlayLifecycleOwner = null
        }
    }

    private fun detachOverlay() {
        val view = overlayView ?: return
        runCatching { windowManager.removeViewImmediate(view) }
        overlayView = null
        overlayParams = null
        overlayLifecycleOwner?.destroy()
        overlayLifecycleOwner = null
    }

    private fun updateOverlayTouchability() {
        val view = overlayView ?: return
        val params = overlayParams ?: return
        params.flags = overlayFlags()
        runCatching { windowManager.updateViewLayout(view, params) }
    }

    private fun applySavedOverlayPosition() {
        if (savedOverlayX < 0 || savedOverlayY < 0) return
        val view = overlayView ?: return
        val params = overlayParams ?: return
        val width = view.width.takeIf { it > 0 } ?: overlayWidthPx()
        val height = view.height.takeIf { it > 0 } ?: dpToPx(OVERLAY_WINDOW_HEIGHT_DP)
        val horizontalMargin = horizontalOverlayMargin()
        val verticalMargin = dpToPx(OVERLAY_WINDOW_MARGIN_DP)
        val displayMetrics = context.resources.displayMetrics
        val maxX = (displayMetrics.widthPixels - width - horizontalMargin).coerceAtLeast(horizontalMargin)
        val maxY = (displayMetrics.heightPixels - height - verticalMargin).coerceAtLeast(verticalMargin)
        params.x = when (DesktopLyricsAlignment.sanitize(currentAlignment)) {
            DesktopLyricsAlignment.START -> 0
            DesktopLyricsAlignment.END -> maxX
            else -> savedOverlayX.coerceIn(horizontalMargin, maxX)
        }
        params.y = savedOverlayY.coerceIn(verticalMargin, maxY)
        runCatching { windowManager.updateViewLayout(view, params) }
    }

    private fun overlayFlags(): Int =
        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
            (if (overlayLocked) WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE else 0)

    private fun initialOverlayX(widthPx: Int): Int {
        val alignment = DesktopLyricsAlignment.sanitize(currentAlignment)
        if (alignment == DesktopLyricsAlignment.START) return 0
        if (alignment == DesktopLyricsAlignment.END) {
            return (context.resources.displayMetrics.widthPixels - widthPx).coerceAtLeast(0)
        }

        val margin = horizontalOverlayMargin()
        if (savedOverlayX >= 0) {
            val maxX = (context.resources.displayMetrics.widthPixels - widthPx - margin)
                .coerceAtLeast(margin)
            return savedOverlayX.coerceIn(margin, maxX)
        }
        return ((context.resources.displayMetrics.widthPixels - widthPx) / 2)
            .coerceAtLeast(margin)
    }

    private fun initialOverlayY(): Int {
        val margin = dpToPx(OVERLAY_WINDOW_MARGIN_DP)
        if (savedOverlayY >= 0) {
            val maxY = (context.resources.displayMetrics.heightPixels -
                dpToPx(OVERLAY_WINDOW_HEIGHT_DP) - margin).coerceAtLeast(margin)
            return savedOverlayY.coerceIn(margin, maxY)
        }
        return dpToPx(96).coerceAtLeast(margin)
    }

    private fun overlayWidthPx(): Int {
        val displayWidth = context.resources.displayMetrics.widthPixels
        val horizontalMargin = horizontalOverlayMargin() * 2
        val maxWidth = (displayWidth - horizontalMargin).coerceAtLeast(dpToPx(220))
        return min(dpToPx(OVERLAY_WINDOW_WIDTH_DP), maxWidth)
    }

    private fun horizontalOverlayMargin(): Int =
        if (DesktopLyricsAlignment.sanitize(currentAlignment) == DesktopLyricsAlignment.CENTER) {
            dpToPx(OVERLAY_WINDOW_MARGIN_DP)
        } else {
            0
        }

    private fun maxOverlayX(widthPx: Int): Int {
        val margin = horizontalOverlayMargin()
        return (context.resources.displayMetrics.widthPixels - widthPx - margin).coerceAtLeast(margin)
    }

    private fun dpToPx(dp: Int): Int =
        (dp * context.resources.displayMetrics.density).roundToInt()
}

private class OverlayLifecycleOwner : LifecycleOwner, SavedStateRegistryOwner {
    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle
        get() = lifecycleRegistry

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateController.savedStateRegistry

    fun create() {
        savedStateController.performAttach()
        savedStateController.performRestore(null)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        lifecycleRegistry.currentState = Lifecycle.State.STARTED
        lifecycleRegistry.currentState = Lifecycle.State.RESUMED
    }

    fun destroy() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
    }
}

@Composable
private fun DesktopLyricsOverlayContent(
    controller: DesktopLyricsOverlayController
) {
    val uiState by controller.state.collectAsState()
    val playbackPosition by controller.playbackPositionFlow.collectAsState()
    val context = LocalContext.current

    val useAnimatedLyricsFlow = remember(context) {
        context.dataStore.data.map {
            it[androidx.datastore.preferences.core.booleanPreferencesKey("use_animated_lyrics")] ?: false
        }
    }
    val useAnimatedLyrics by useAnimatedLyricsFlow.collectAsState(initial = false)

    val immersiveLyricsEnabledFlow = remember(context) {
        context.dataStore.data.map {
            it[androidx.datastore.preferences.core.booleanPreferencesKey("immersive_lyrics_enabled")] ?: false
        }
    }
    val immersiveLyricsEnabled by immersiveLyricsEnabledFlow.collectAsState(initial = false)

    val showLyricsTranslationFlow = remember(context) {
        context.dataStore.data.map {
            it[androidx.datastore.preferences.core.booleanPreferencesKey("show_lyrics_translation")] ?: true
        }
    }
    val showLyricsTranslation by showLyricsTranslationFlow.collectAsState(initial = true)

    val showLyricsRomanizationFlow = remember(context) {
        context.dataStore.data.map {
            it[androidx.datastore.preferences.core.booleanPreferencesKey("show_lyrics_romanization")] ?: true
        }
    }
    val showLyricsRomanization by showLyricsRomanizationFlow.collectAsState(initial = true)

    val darkTheme = when (uiState.appThemeMode) {
        AppThemeMode.LIGHT -> false
        AppThemeMode.DARK -> true
        else -> isSystemInDarkTheme()
    }

    PixelPlayTheme(darkTheme = darkTheme) {
        val colorScheme = MaterialTheme.colorScheme
        val lyricColorScheme = when (uiState.desktopLyricsColorSource) {
            DesktopLyricsColorSource.ALBUM_ART -> uiState.albumArtColorSchemePair
                ?.let { if (darkTheme) it.dark else it.light }
                ?: colorScheme
            else -> colorScheme
        }
        val lyricColors = remember(lyricColorScheme, uiState.monetColorStyle, uiState.overlayBackgroundEnabled) {
            resolveReadableDesktopLyricsColors(
                colorScheme = lyricColorScheme,
                style = uiState.monetColorStyle,
                backgroundEnabled = uiState.overlayBackgroundEnabled
            )
        }
        val desktopLyricsAlignment = DesktopLyricsAlignment.sanitize(uiState.desktopLyricsAlignment)
        val containerAlignment = remember(desktopLyricsAlignment) {
            desktopLyricsContainerAlignment(desktopLyricsAlignment)
        }
        val horizontalContentAlignment = remember(desktopLyricsAlignment) {
            desktopLyricsHorizontalAlignment(desktopLyricsAlignment)
        }
        val textAlign = remember(desktopLyricsAlignment) {
            desktopLyricsTextAlign(desktopLyricsAlignment)
        }
        val overlayScale = uiState.desktopLyricsTextScale
            .coerceIn(MIN_DESKTOP_LYRICS_TEXT_SCALE, MAX_DESKTOP_LYRICS_TEXT_SCALE)
        val textScale = DESKTOP_LYRICS_BASE_TEXT_SCALE * overlayScale
        val horizontalPadding = if (
            !uiState.overlayBackgroundEnabled &&
            desktopLyricsAlignment != DesktopLyricsAlignment.CENTER
        ) {
            0.dp
        } else {
            18.dp * overlayScale
        }
        val verticalPadding = 10.dp * overlayScale
        val minBarHeight = if (overlayScale > 1f) 56.dp * overlayScale else 56.dp
        val titleLargeTextStyle = MaterialTheme.typography.titleLarge
        val primaryTextStyle = remember(titleLargeTextStyle, immersiveLyricsEnabled, textScale) {
            val fontScale = textScale * if (immersiveLyricsEnabled) 1.10f else 1f
            titleLargeTextStyle.copy(
                fontSize = titleLargeTextStyle.fontSize * fontScale,
                lineHeight = titleLargeTextStyle.lineHeight * fontScale,
                fontWeight = FontWeight.Bold,
                fontFamily = null
            )
        }
        val bodySmallTextStyle = MaterialTheme.typography.bodySmall
        val secondaryTextStyle = remember(bodySmallTextStyle, textScale) {
            bodySmallTextStyle.copy(
                fontSize = bodySmallTextStyle.fontSize * textScale,
                lineHeight = bodySmallTextStyle.lineHeight * textScale,
                fontWeight = FontWeight.Medium,
                fontFamily = null
            )
        }
        val lyricLine = remember(
            uiState.lyrics,
            playbackPosition,
            uiState.lyricsSyncOffsetMs,
            showLyricsTranslation,
            showLyricsRomanization
        ) {
            resolveDesktopLyricsLine(
                lyrics = uiState.lyrics,
                position = (playbackPosition + uiState.lyricsSyncOffsetMs).coerceAtLeast(0L),
                showTranslation = showLyricsTranslation,
                showRomanization = showLyricsRomanization
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = minBarHeight)
                .background(Color.Transparent)
                .pointerInput(uiState.positionLocked) {
                    if (!uiState.positionLocked) {
                        detectDragGestures(
                            onDragEnd = { controller.persistOverlayPosition() },
                            onDragCancel = { controller.persistOverlayPosition() }
                        ) { change, dragAmount ->
                            change.consume()
                            controller.moveOverlayBy(dragAmount.x, dragAmount.y)
                        }
                    }
                },
            contentAlignment = containerAlignment
        ) {
            AnimatedVisibility(
                visible = uiState.currentSong != null && lyricLine?.primary?.isNotBlank() == true,
                enter = fadeIn(animationSpec = tween(180)) + scaleIn(initialScale = 0.96f),
                exit = fadeOut(animationSpec = tween(120)) + scaleOut(targetScale = 0.96f)
            ) {
                DesktopLyricsFrame(
                    backgroundEnabled = uiState.overlayBackgroundEnabled,
                    backgroundColor = lyricColors.background,
                    contentColor = lyricColors.primary,
                    alignment = desktopLyricsAlignment
                ) {
                    AnimatedContent(
                        targetState = lyricLine,
                        transitionSpec = {
                            if (useAnimatedLyrics) {
                                (fadeIn(tween(180)) + scaleIn(initialScale = 0.98f)) togetherWith
                                    fadeOut(tween(120))
                            } else {
                                fadeIn(tween(120)) togetherWith fadeOut(tween(90))
                            }
                        },
                        label = "desktop_lyrics_line"
                    ) { line ->
                        DesktopLyricLineContent(
                            line = line,
                            primaryColor = lyricColors.primary,
                            secondaryColor = lyricColors.secondary,
                            primaryTextStyle = primaryTextStyle,
                            secondaryTextStyle = secondaryTextStyle,
                            horizontalAlignment = horizontalContentAlignment,
                            textAlign = textAlign,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = horizontalPadding, vertical = verticalPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DesktopLyricsFrame(
    backgroundEnabled: Boolean,
    backgroundColor: Color,
    contentColor: Color,
    alignment: String,
    content: @Composable () -> Unit
) {
    if (backgroundEnabled) {
        Surface(
            color = backgroundColor,
            contentColor = contentColor,
            shape = desktopLyricsCapsuleShape(alignment),
            tonalElevation = 0.dp,
            shadowElevation = 0.dp,
            modifier = Modifier.fillMaxWidth(),
            content = content
        )
    } else {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = desktopLyricsContainerAlignment(alignment)
        ) {
            content()
        }
    }
}

@Composable
private fun DesktopLyricLineContent(
    line: DesktopLyricsLine?,
    primaryColor: Color,
    secondaryColor: Color,
    primaryTextStyle: androidx.compose.ui.text.TextStyle,
    secondaryTextStyle: androidx.compose.ui.text.TextStyle,
    horizontalAlignment: Alignment.Horizontal,
    textAlign: TextAlign,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = line?.primary.orEmpty(),
            style = primaryTextStyle,
            color = primaryColor,
            textAlign = textAlign,
            modifier = Modifier.fillMaxWidth()
        )

        if (!line?.secondary.isNullOrBlank()) {
            Text(
                text = line?.secondary.orEmpty(),
                style = secondaryTextStyle,
                color = secondaryColor,
                textAlign = textAlign,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

private data class DesktopLyricsLine(
    val primary: String,
    val secondary: String? = null
)

private fun desktopLyricsContainerAlignment(alignment: String): Alignment =
    when (DesktopLyricsAlignment.sanitize(alignment)) {
        DesktopLyricsAlignment.START -> Alignment.CenterStart
        DesktopLyricsAlignment.END -> Alignment.CenterEnd
        else -> Alignment.Center
    }

private fun desktopLyricsHorizontalAlignment(alignment: String): Alignment.Horizontal =
    when (DesktopLyricsAlignment.sanitize(alignment)) {
        DesktopLyricsAlignment.START -> Alignment.Start
        DesktopLyricsAlignment.END -> Alignment.End
        else -> Alignment.CenterHorizontally
    }

private fun desktopLyricsTextAlign(alignment: String): TextAlign =
    when (DesktopLyricsAlignment.sanitize(alignment)) {
        DesktopLyricsAlignment.START -> TextAlign.Start
        DesktopLyricsAlignment.END -> TextAlign.End
        else -> TextAlign.Center
    }

private fun desktopLyricsCapsuleShape(alignment: String): RoundedCornerShape =
    when (DesktopLyricsAlignment.sanitize(alignment)) {
        DesktopLyricsAlignment.START -> RoundedCornerShape(
            topStart = 0.dp,
            bottomStart = 0.dp,
            topEnd = 28.dp,
            bottomEnd = 28.dp
        )
        DesktopLyricsAlignment.END -> RoundedCornerShape(
            topStart = 28.dp,
            bottomStart = 28.dp,
            topEnd = 0.dp,
            bottomEnd = 0.dp
        )
        else -> RoundedCornerShape(28.dp)
    }

private fun resolveDesktopLyricsLine(
    lyrics: Lyrics?,
    position: Long,
    showTranslation: Boolean,
    showRomanization: Boolean
): DesktopLyricsLine? {
    val synced = lyrics?.synced.orEmpty()
    if (synced.isNotEmpty()) {
        val resolvedIndex = resolveCurrentLineIndex(lines = synced, position = position)
        val index = when {
            resolvedIndex >= 0 -> resolvedIndex
            position < synced.first().time.toLong() -> 0
            else -> synced.indexOfLast { position >= it.time.toLong() }.coerceAtLeast(0)
        }
        val line = synced.getOrNull(index) ?: return null
        val splitLine = splitDesktopLyricText(line.line)
        val secondaryCandidates = mutableListOf<String>()
        if (showTranslation) {
            line.translation?.let { secondaryCandidates += sanitizeLyricLineText(it) }
        }
        if (showRomanization) {
            line.romanization?.let { secondaryCandidates += sanitizeLyricLineText(it) }
        }
        splitLine.secondary?.let { secondaryCandidates += it }
        return splitLine.copy(
            secondary = secondaryCandidates.firstOrNull { it.isNotBlank() }
        )
    }

    return lyrics?.plain
        .orEmpty()
        .asSequence()
        .map(::splitDesktopLyricText)
        .firstOrNull { it.primary.isNotBlank() }
}

private fun splitDesktopLyricText(raw: String): DesktopLyricsLine {
    val lines = raw
        .split("\n")
        .map { sanitizeLyricLineText(it) }
        .filter { it.isNotBlank() }

    return DesktopLyricsLine(
        primary = lines.firstOrNull().orEmpty(),
        secondary = lines.drop(1).firstOrNull()
    )
}

private data class DesktopLyricsColors(
    val background: Color,
    val primary: Color,
    val secondary: Color
)

private fun resolveReadableDesktopLyricsColors(
    colorScheme: ColorScheme,
    style: String,
    backgroundEnabled: Boolean
): DesktopLyricsColors {
    val role = DesktopLyricsMonetColor.sanitize(style)
    val roleColors = desktopLyricsRoleColors(colorScheme, role)
    val background = if (backgroundEnabled) {
        roleColors.container.copy(alpha = 0.64f)
    } else {
        Color.Transparent
    }

    val contrastBackground = if (backgroundEnabled) {
        background.compositeOver(colorScheme.surface)
    } else {
        colorScheme.surface
    }
    val primary = preferredReadableMonetColor(
        background = contrastBackground,
        candidates = roleColors.textCandidates,
        tonalFallback = roleColors.accent
    )
    val secondary = preferredReadableMonetColor(
        background = contrastBackground,
        candidates = roleColors.secondaryCandidates + primary.copy(alpha = 0.90f),
        tonalFallback = roleColors.accent
    ).copy(alpha = 0.90f)

    return DesktopLyricsColors(
        background = background,
        primary = primary,
        secondary = secondary
    )
}

private data class DesktopLyricsRoleColors(
    val accent: Color,
    val container: Color,
    val textCandidates: List<Color>,
    val secondaryCandidates: List<Color>
)

private fun desktopLyricsRoleColors(
    colorScheme: ColorScheme,
    role: String
): DesktopLyricsRoleColors =
    when (role) {
        DesktopLyricsMonetColor.SECONDARY -> DesktopLyricsRoleColors(
            accent = colorScheme.secondary,
            container = colorScheme.secondaryContainer,
            textCandidates = listOf(
                colorScheme.onSecondaryContainer,
                colorScheme.secondary,
                colorScheme.onSecondaryFixed,
                colorScheme.onSurface
            ),
            secondaryCandidates = listOf(colorScheme.onSurfaceVariant, colorScheme.secondary)
        )
        DesktopLyricsMonetColor.TERTIARY -> DesktopLyricsRoleColors(
            accent = colorScheme.tertiary,
            container = colorScheme.tertiaryContainer,
            textCandidates = listOf(
                colorScheme.onTertiaryContainer,
                colorScheme.tertiary,
                colorScheme.onTertiaryFixed,
                colorScheme.onSurface
            ),
            secondaryCandidates = listOf(colorScheme.onSurfaceVariant, colorScheme.tertiary)
        )
        DesktopLyricsMonetColor.INVERSE_PRIMARY -> DesktopLyricsRoleColors(
            accent = colorScheme.inversePrimary,
            container = colorScheme.inverseSurface,
            textCandidates = listOf(
                colorScheme.inverseOnSurface,
                colorScheme.inversePrimary,
                colorScheme.onPrimaryContainer,
                colorScheme.onSurface
            ),
            secondaryCandidates = listOf(colorScheme.inverseOnSurface, colorScheme.onSurfaceVariant)
        )
        DesktopLyricsMonetColor.NEUTRAL -> DesktopLyricsRoleColors(
            accent = colorScheme.onSurfaceVariant,
            container = colorScheme.surfaceVariant,
            textCandidates = listOf(
                colorScheme.onSurface,
                colorScheme.onSurfaceVariant,
                colorScheme.inverseOnSurface
            ),
            secondaryCandidates = listOf(colorScheme.onSurfaceVariant, colorScheme.onSurface)
        )
        else -> DesktopLyricsRoleColors(
            accent = colorScheme.primary,
            container = colorScheme.primaryContainer,
            textCandidates = listOf(
                colorScheme.onPrimaryContainer,
                colorScheme.primary,
                colorScheme.onPrimaryFixed,
                colorScheme.onSurface
            ),
            secondaryCandidates = listOf(colorScheme.onSurfaceVariant, colorScheme.primary)
        )
    }

private fun preferredReadableMonetColor(
    background: Color,
    candidates: List<Color>,
    tonalFallback: Color,
    minContrastRatio: Double = 5.6
): Color {
    candidates
        .distinct()
        .firstOrNull { contrastRatio(it, background) >= minContrastRatio }
        ?.let { return it }

    val tonalHighContrast = if (background.relativeLuminance() < 0.5) {
        tonalFallback.blendToward(Color.White, 0.78f)
    } else {
        tonalFallback.blendToward(Color.Black, 0.68f)
    }
    if (contrastRatio(tonalHighContrast, background) >= minContrastRatio) {
        return tonalHighContrast
    }

    return if (contrastRatio(Color.White, background) >= contrastRatio(Color.Black, background)) {
        Color.White
    } else {
        Color.Black
    }
}

private fun contrastRatio(foreground: Color, background: Color): Double {
    val foregroundLuminance = foreground.relativeLuminance()
    val backgroundLuminance = background.relativeLuminance()
    val lighter = maxOf(foregroundLuminance, backgroundLuminance)
    val darker = minOf(foregroundLuminance, backgroundLuminance)
    return (lighter + 0.05) / (darker + 0.05)
}

private fun Color.relativeLuminance(): Double {
    val argb = (value shr 32).toInt()
    val red = linearizedChannel((argb shr 16) and 0xFF)
    val green = linearizedChannel((argb shr 8) and 0xFF)
    val blue = linearizedChannel(argb and 0xFF)
    return (0.2126 * red) + (0.7152 * green) + (0.0722 * blue)
}

private fun linearizedChannel(channel: Int): Double {
    val value = channel / 255.0
    return if (value <= 0.03928) {
        value / 12.92
    } else {
        ((value + 0.055) / 1.055).pow(2.4)
    }
}

private fun Color.blendToward(target: Color, amount: Float): Color {
    val clamped = amount.coerceIn(0f, 1f)
    return Color(
        red = red + (target.red - red) * clamped,
        green = green + (target.green - green) * clamped,
        blue = blue + (target.blue - blue) * clamped,
        alpha = alpha + (target.alpha - alpha) * clamped
    )
}
