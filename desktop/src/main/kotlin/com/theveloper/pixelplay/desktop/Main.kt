package com.theveloper.pixelplay.desktop

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode as AnimationRepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Album
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Folder
import androidx.compose.material.icons.rounded.GraphicEq
import androidx.compose.material.icons.rounded.FullscreenExit
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.CropSquare
import androidx.compose.material.icons.rounded.QueueMusic
import androidx.compose.material.icons.rounded.Minimize
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.RepeatOne
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.decodeToImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberDialogState
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.awt.ComposeDialog
import androidx.compose.ui.awt.ComposeWindow
import javafx.application.Platform
import javafx.scene.media.EqualizerBand
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.util.Duration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jaudiotagger.audio.AudioFileIO
import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag
import org.jaudiotagger.tag.TagTextField
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.awt.Window as AwtWindow
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.util.Base64
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import javax.imageio.ImageIO
import javax.swing.JFileChooser
import javax.swing.SwingUtilities
import com.sun.jna.Pointer
import com.sun.jna.ptr.IntByReference
import com.sun.jna.platform.win32.Kernel32
import com.sun.jna.platform.win32.WinBase
import com.sun.jna.platform.win32.WinNT
import com.sun.jna.platform.win32.User32
import com.sun.jna.platform.win32.WinDef
import com.sun.jna.platform.win32.WinUser
import kotlin.io.path.extension
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension
import kotlin.math.abs
import kotlin.math.roundToInt

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFAB47BC),
    onPrimary = Color.White,
    primaryContainer = Color(0xFF4E275F),
    onPrimaryContainer = Color(0xFFFFD6FF),
    secondary = Color(0xFFF06292),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF6C2C45),
    onSecondaryContainer = Color(0xFFFFD9E6),
    tertiary = Color(0xFFFF8A65),
    onTertiary = Color(0xFF351100),
    tertiaryContainer = Color(0xFF6E331D),
    onTertiaryContainer = Color(0xFFFFDBCF),
    background = Color(0xFF1E1234),
    onBackground = Color(0xFFF4E8FF),
    surface = Color(0xFF2A1F40),
    onSurface = Color(0xFFEBDDF5),
    surfaceVariant = Color(0xFF4D4165),
    onSurfaceVariant = Color(0xFFD4C4DE),
    outline = Color(0xFF9D8CB1)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6C4FF5),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE3DBFF),
    onPrimaryContainer = Color(0xFF23005C),
    secondary = Color(0xFFAD3F6D),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFD9E7),
    onSecondaryContainer = Color(0xFF3F001D),
    tertiary = Color(0xFF9A4E18),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDBC7),
    onTertiaryContainer = Color(0xFF321200),
    background = Color(0xFFF7F2FF),
    onBackground = Color(0xFF1E1237),
    surface = Color(0xFFFBF8FF),
    onSurface = Color(0xFF1E1237),
    surfaceVariant = Color(0xFFE8DEF9),
    onSurfaceVariant = Color(0xFF4D4165),
    outline = Color(0xFF78659A)
)

private val DesktopTypography = androidx.compose.material3.Typography().run {
    copy(
        displayMedium = displayMedium.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, letterSpacing = 0.sp),
        headlineMedium = headlineMedium.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp),
        headlineSmall = headlineSmall.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, letterSpacing = 0.sp),
        titleLarge = titleLarge.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, letterSpacing = 0.sp),
        titleMedium = titleMedium.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, letterSpacing = 0.sp),
        titleSmall = titleSmall.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, letterSpacing = 0.sp),
        bodyLarge = bodyLarge.copy(fontFamily = FontFamily.SansSerif, letterSpacing = 0.sp),
        bodyMedium = bodyMedium.copy(fontFamily = FontFamily.SansSerif, letterSpacing = 0.sp),
        bodySmall = bodySmall.copy(fontFamily = FontFamily.SansSerif, letterSpacing = 0.sp),
        labelLarge = labelLarge.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, letterSpacing = 0.sp),
        labelMedium = labelMedium.copy(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Medium, letterSpacing = 0.sp),
        labelSmall = labelSmall.copy(fontFamily = FontFamily.SansSerif, letterSpacing = 0.sp)
    )
}

private const val DesktopVersion = "0.2.0"
private const val DesktopUpdatedAt = "2026-07-19"
private val MiniPlayerHeight = 72.dp
private val MiniPlayerBottomSpacer = 10.dp

private val musicExtensions = setOf(
    "mp3", "m4a", "aac", "wav", "flac", "ogg", "oga", "opus", "wma",
    "aif", "aiff", "mp4", "m4b", "alac", "ape", "wv", "tta", "tak",
    "mka", "webm", "ac3", "dts", "amr", "mp2", "mp1", "mpc", "mpp",
    "mp+", "ofr", "ofs", "spx", "caf", "au", "snd", "ra", "rm",
    "rmvb", "3gp", "3g2", "dsf", "dff", "gsm", "mid", "midi",
    "mod", "xm", "s3m", "it"
)
private val javaFxPlayableExtensions = setOf("mp3", "m4a", "aac", "wav", "aif", "aiff", "mp4")
private val mpvPlayableExtensions = musicExtensions
private val eqFrequencies = listOf(60.0, 170.0, 310.0, 600.0, 1_000.0, 3_000.0, 6_000.0, 12_000.0, 14_000.0, 16_000.0)
private const val MaxEmbeddedArtworkBytes = 8 * 1024 * 1024
private val desktopLyricTimestampRegex = Regex("""\[(\d{1,2}):(\d{2})(?:[.:](\d{1,3}))?]""")
private val embeddedLyricsFieldIds = setOf("LYRICS", "UNSYNCEDLYRICS", "UNSYNCED LYRICS", "USLT", "SYLT", "LYR")
private const val LibraryCacheMagic = "PPLAYER_LIBRARY_CACHE_V1"
private const val EmptyCacheValue = "-"
private const val WindowsExToolWindow = 0x00000080
private const val WindowsExAppWindow = 0x00040000
private val cacheBase64Encoder = Base64.getUrlEncoder().withoutPadding()
private val cacheBase64Decoder = Base64.getUrlDecoder()

private enum class DesktopRoute(val title: String, val icon: ImageVector) {
    Library("音乐库", Icons.Rounded.LibraryMusic),
    Settings("设置", Icons.Rounded.Settings),
    Equalizer("均衡器", Icons.Rounded.GraphicEq),
    Stats("统计", Icons.Rounded.Info),
    About("关于", Icons.Rounded.Info)
}

private enum class LibraryTab(val label: String, val icon: ImageVector) {
    Songs("歌曲", Icons.Rounded.MusicNote),
    Albums("专辑", Icons.Rounded.Album),
    Artists("艺术家", Icons.Rounded.Person),
    Folders("文件夹", Icons.Rounded.Folder),
    Favorites("喜欢", Icons.Rounded.Favorite),
    Queue("队列", Icons.Rounded.QueueMusic)
}

private enum class SortMode(val label: String) {
    TitleAsc("标题 A-Z"),
    ArtistAsc("艺术家 A-Z"),
    AlbumAsc("专辑 A-Z"),
    Newest("最近修改"),
    FileNameAsc("文件名 A-Z")
}

private enum class PlaybackRepeatMode {
    Off,
    All,
    One
}

private enum class SettingsCategory(val title: String, val subtitle: String, val icon: ImageVector) {
    Library("音乐管理", "扫描目录、搜索、排序和音乐库状态", Icons.Rounded.LibraryMusic),
    Appearance("外观", "主题、圆角、滚动条和播放器色彩", Icons.Rounded.Palette),
    Playback("播放", "音量、随机、循环和播放内核", Icons.Rounded.MusicNote),
    Behavior("行为", "启动扫描、展开播放器和窗口歌词", Icons.Rounded.Settings),
    Backup("备份与恢复", "桌面端本地配置和播放列表迁移", Icons.Rounded.Folder),
    About("关于", "版本、更新日期和应用信息", Icons.Rounded.Info)
}

private enum class FloatingLyricsAlignment(val label: String) {
    Start("左对齐"),
    Center("居中"),
    End("右对齐")
}

private enum class FloatingLyricsColorMode(val label: String) {
    ThemePrimary("莫奈主色"),
    ThemeSecondary("莫奈辅色"),
    ThemeTertiary("莫奈强调色"),
    AlbumReadable("按歌曲取色")
}

private data class DesktopSong(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val albumArtist: String,
    val path: Path,
    val sizeBytes: Long,
    val modifiedAtMs: Long,
    val durationMs: Long,
    val artwork: DesktopArtwork? = null,
    val embeddedLyricsRaw: String? = null
) {
    val displayArtist: String get() = artist.ifBlank { "未知艺术家" }
    val displayAlbum: String get() = album.ifBlank { "本地音乐" }
    val fileName: String get() = path.name
}

private data class DesktopArtwork(
    val data: ByteArray,
    val mimeType: String?,
    val previewData: ByteArray = data,
    val paletteColors: List<Color>? = null
) {
    val cacheKey: Int = data.contentHashCode()
    val previewCacheKey: Int = previewData.contentHashCode()
}

private object ArtworkBitmapCache {
    private const val MaxEntries = 160
    private val cache = object : LinkedHashMap<Int, ImageBitmap>(MaxEntries, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Int, ImageBitmap>?): Boolean = size > MaxEntries
    }

    @Synchronized
    fun getOrDecode(key: Int, data: ByteArray): ImageBitmap? {
        cache[key]?.let { return it }
        return runCatching { data.decodeToImageBitmap() }.getOrNull()?.also { cache[key] = it }
    }
}

private data class DesktopAlbum(
    val title: String,
    val artist: String,
    val songs: List<DesktopSong>
) {
    val displayTitle: String get() = title.ifBlank { "未知专辑" }
    val displayArtist: String get() = artist.ifBlank { songs.firstOrNull()?.displayArtist ?: "未知艺术家" }
}

private data class DesktopArtist(
    val name: String,
    val songs: List<DesktopSong>
)

private data class DesktopFolder(
    val path: Path,
    val name: String,
    val songs: List<DesktopSong>
)

private data class DesktopLyrics(
    val synced: List<DesktopLyricLine> = emptyList(),
    val plain: List<String> = emptyList()
)

private data class DesktopLyricLine(
    val timeMs: Long,
    val text: String
)

private data class PlayerPalette(
    val primary: Color,
    val secondary: Color,
    val tertiary: Color,
    val container: Color,
    val onContainer: Color
)

private class DesktopPlayerController {
    var route by mutableStateOf(DesktopRoute.Library)
    var drawerOpen by mutableStateOf(false)
    var selectedSettingsCategory by mutableStateOf<SettingsCategory?>(null)

    var libraryRoot by mutableStateOf<Path?>(cachedLibraryRoot() ?: defaultMusicPath())
    var libraryTab by mutableStateOf(LibraryTab.Songs)
    var searchVisible by mutableStateOf(false)
    var searchQuery by mutableStateOf("")
    var sortMode by mutableStateOf(SortMode.TitleAsc)
    var isAlbumGrid by mutableStateOf(true)
    var isFolderPlaylistView by mutableStateOf(false)
    var isScanning by mutableStateOf(false)
    var statusMessage by mutableStateOf("选择音乐文件夹，或直接扫描系统音乐目录。")
    var songs by mutableStateOf<List<DesktopSong>>(emptyList())

    var playbackQueue by mutableStateOf<List<DesktopSong>>(emptyList())
    var queueSourceName by mutableStateOf("音乐库")
    var currentSong by mutableStateOf<DesktopSong?>(null)
    var currentLyrics by mutableStateOf<DesktopLyrics?>(null)
    var isPlaying by mutableStateOf(false)
    var durationMs by mutableStateOf(0L)
    var positionMs by mutableStateOf(0L)
    var volume by mutableStateOf(0.86f)
    var shuffleEnabled by mutableStateOf(false)
    var repeatMode by mutableStateOf(PlaybackRepeatMode.Off)
    var favoriteIds by mutableStateOf<Set<String>>(emptySet())

    var playerExpanded by mutableStateOf(false)
    var fullPlayerShowsLyrics by mutableStateOf(false)
    var showQueueOverlay by mutableStateOf(false)

    var isDarkTheme by mutableStateOf(false)
    var useSmoothCorners by mutableStateOf(true)
    var showScrollbar by mutableStateOf(true)
    var albumThemeEnabled by mutableStateOf(true)

    var floatingLyricsEnabled by mutableStateOf(false)
    var floatingLyricsLocked by mutableStateOf(false)
    var floatingLyricsBackgroundEnabled by mutableStateOf(true)
    var floatingLyricsTextScale by mutableStateOf(0.86f)
    var floatingLyricsTwoLine by mutableStateOf(true)
    var floatingLyricsAlignment by mutableStateOf(FloatingLyricsAlignment.Center)
    var floatingLyricsColorMode by mutableStateOf(FloatingLyricsColorMode.AlbumReadable)

    var equalizerEnabled by mutableStateOf(false)
    var equalizerGains by mutableStateOf(List(eqFrequencies.size) { 0f })

    private var mediaPlayer: MediaPlayer? = null
    private var mpvPlayer: MpvAudioPlayer? = null

    val visibleSongs: List<DesktopSong>
        get() = sortedSongs(filteredByQuery(songs), sortMode)

    val favoriteSongs: List<DesktopSong>
        get() = sortedSongs(filteredByQuery(songs.filter { it.id in favoriteIds }), sortMode)

    val albums: List<DesktopAlbum>
        get() = filteredByQuery(songs)
            .groupBy { it.displayAlbum.lowercase(Locale.getDefault()) to it.displayArtist.lowercase(Locale.getDefault()) }
            .values
            .map { group ->
                val first = group.first()
                DesktopAlbum(first.displayAlbum, first.displayArtist, sortedSongs(group, SortMode.TitleAsc))
            }
            .sortedBy { it.displayTitle.lowercase(Locale.getDefault()) }

    val artists: List<DesktopArtist>
        get() = filteredByQuery(songs)
            .groupBy { it.displayArtist.lowercase(Locale.getDefault()) }
            .values
            .map { group -> DesktopArtist(group.first().displayArtist, sortedSongs(group, SortMode.TitleAsc)) }
            .sortedBy { it.name.lowercase(Locale.getDefault()) }

    val folders: List<DesktopFolder>
        get() = filteredByQuery(songs)
            .groupBy { it.path.parent ?: it.path }
            .map { (path, folderSongs) ->
                DesktopFolder(
                    path = path,
                    name = path.fileName?.toString() ?: path.toString(),
                    songs = sortedSongs(folderSongs, sortMode)
                )
            }
            .sortedBy { it.name.lowercase(Locale.getDefault()) }

    val currentTabSongs: List<DesktopSong>
        get() = when (libraryTab) {
            LibraryTab.Songs -> visibleSongs
            LibraryTab.Albums -> albums.flatMap { it.songs }
            LibraryTab.Artists -> artists.flatMap { it.songs }
            LibraryTab.Folders -> folders.flatMap { it.songs }
            LibraryTab.Favorites -> favoriteSongs
            LibraryTab.Queue -> playbackQueue.ifEmpty { visibleSongs }
        }

    suspend fun scanDefaultLibrary() {
        val root = libraryRoot ?: return
        scanLibrary(root)
    }

    suspend fun loadCachedLibrary() {
        val root = libraryRoot ?: return
        val cached = withContext(Dispatchers.IO) { readMusicCache(root) }
        if (cached.isNotEmpty()) {
            songs = cached
            if (playbackQueue.isEmpty()) {
                playbackQueue = cached
                queueSourceName = "音乐库"
            }
            statusMessage = "已从缓存载入 ${cached.size} 首歌曲，点击重新扫描可更新音乐库。"
        } else {
            statusMessage = "未找到本地缓存，点击重新扫描建立音乐库。"
        }
    }

    suspend fun chooseAndScanFolder() {
        val selected = chooseMusicFolder(libraryRoot)
        if (selected != null) {
            libraryRoot = selected
            scanLibrary(selected)
        }
    }

    suspend fun scanLibrary(root: Path) {
        isScanning = true
        statusMessage = "正在扫描：$root"
        val scanned = withContext(Dispatchers.IO) {
            scanMusicFiles(root).also { saveMusicCache(root, it) }
        }
        songs = scanned
        if (playbackQueue.isEmpty()) {
            playbackQueue = scanned
            queueSourceName = "音乐库"
        }
        isScanning = false
        statusMessage = if (scanned.isEmpty()) {
            "没有找到本地音乐。请换一个包含音频文件的目录。"
        } else {
            "已找到 ${scanned.size} 首歌曲"
        }
    }

    fun openRoute(nextRoute: DesktopRoute) {
        route = nextRoute
        drawerOpen = false
        if (nextRoute != DesktopRoute.Settings) selectedSettingsCategory = null
    }

    fun playSong(song: DesktopSong, queue: List<DesktopSong> = currentTabSongs, sourceName: String = libraryTab.label) {
        val nextQueue = queue.ifEmpty { songs }
        playbackQueue = nextQueue
        queueSourceName = sourceName
        currentSong = song
        currentLyrics = loadLyricsForSong(song)
        positionMs = 0L
        durationMs = song.durationMs
        val extension = song.path.extension.lowercase(Locale.getDefault())
        stopCurrentPlayback()
        if (extension !in javaFxPlayableExtensions) {
            playWithMpv(song, nextQueue, sourceName, extension)
            return
        }
        isPlaying = true
        statusMessage = "正在播放：${song.title}"

        JavaFxAudioRuntime.runOnFx {
            mediaPlayer?.dispose()
            val media = runCatching { Media(song.path.toUri().toString()) }
                .getOrElse { error ->
                    updateOnUi {
                        isPlaying = false
                        statusMessage = "无法加载音频：${error.message ?: song.fileName}"
                    }
                    updateOnUi {
                        playWithMpv(
                            song = song,
                            queue = nextQueue,
                            sourceName = sourceName,
                            extension = extension,
                            fallbackReason = "系统播放内核无法加载：${error.message ?: song.fileName}"
                        )
                    }
                    return@runOnFx
                }

            val nextMediaPlayer = runCatching { MediaPlayer(media) }
                .getOrElse { error ->
                    updateOnUi {
                        playWithMpv(
                            song = song,
                            queue = nextQueue,
                            sourceName = sourceName,
                            extension = extension,
                            fallbackReason = "系统播放内核无法创建播放器：${error.message ?: song.fileName}"
                        )
                    }
                    return@runOnFx
                }

            mediaPlayer = nextMediaPlayer.apply {
                volume = this@DesktopPlayerController.volume.toDouble()
                applyEqualizerTo(this)
                setOnReady {
                    updateOnUi {
                        durationMs = media.duration.toMillis().toDurationMillis().takeIf { it > 0L } ?: song.durationMs
                    }
                }
                setOnEndOfMedia {
                    updateOnUi {
                        when (repeatMode) {
                            PlaybackRepeatMode.One -> seekToStartAndPlay()
                            else -> playNext(fromAutoAdvance = true)
                        }
                    }
                }
                setOnError {
                    updateOnUi {
                        val message = error?.message ?: song.fileName
                        dispose()
                        mediaPlayer = null
                        playWithMpv(
                            song = song,
                            queue = nextQueue,
                            sourceName = sourceName,
                            extension = extension,
                            fallbackReason = "系统播放内核播放失败：$message"
                        )
                    }
                }
                play()
            }
        }
    }

    private fun playWithMpv(
        song: DesktopSong,
        queue: List<DesktopSong>,
        sourceName: String,
        extension: String,
        fallbackReason: String? = null
    ) {
        val executable = findMpvExecutable()
        logMpvLookup(extension, executable)
        if (executable == null || extension !in mpvPlayableExtensions) {
            isPlaying = false
            statusMessage = listOfNotNull(
                fallbackReason,
                "缺少 mpv 广格式后端，无法播放 ${extension.uppercase(Locale.getDefault())}：${song.fileName}。将 mpv.exe 放到 tools/mpv/mpv.exe 或加入 PATH 后可支持 FLAC/APE/OGG/OPUS/WMA/WV/TTA/DSF 等格式。"
            ).joinToString("；")
            return
        }

        val player = MpvAudioPlayer(
            executable = executable,
            song = song,
            onFinished = {
                updateOnUi {
                    when (repeatMode) {
                        PlaybackRepeatMode.One -> playSong(song, queue, sourceName)
                        else -> playNext(fromAutoAdvance = true)
                    }
                }
            },
            onError = { message ->
                updateOnUi {
                    isPlaying = false
                    statusMessage = "mpv 播放失败：$message"
                }
            }
        )

        mpvPlayer = player
        if (player.start(volume)) {
            isPlaying = true
            statusMessage = "正在播放：${song.title}"
        } else {
            mpvPlayer = null
            isPlaying = false
            statusMessage = "无法启动 mpv：${song.fileName}"
        }
    }

    private fun stopCurrentPlayback() {
        mpvPlayer?.stop()
        mpvPlayer = null
        JavaFxAudioRuntime.runOnFx {
            mediaPlayer?.dispose()
            mediaPlayer = null
        }
    }

    fun togglePlayPause() {
        val external = mpvPlayer
        if (external != null) {
            if (external.isPlaying) {
                external.pause()
                isPlaying = false
            } else {
                external.resume()
                isPlaying = true
            }
            return
        }

        val player = mediaPlayer
        val song = currentSong
        if (player == null || song == null) {
            currentTabSongs.firstOrNull()?.let { playSong(it, currentTabSongs, libraryTab.label) }
            return
        }
        JavaFxAudioRuntime.runOnFx {
            if (player.status == MediaPlayer.Status.PLAYING) {
                player.pause()
                updateOnUi { isPlaying = false }
            } else {
                player.play()
                updateOnUi { isPlaying = true }
            }
        }
    }

    fun playNext(fromAutoAdvance: Boolean = false) {
        val queue = playbackQueue.ifEmpty { currentTabSongs }
        if (queue.isEmpty()) return
        val currentIndex = queue.indexOfFirst { it.id == currentSong?.id }
        val next = when {
            shuffleEnabled && queue.size > 1 -> queue.filter { it.id != currentSong?.id }.random()
            currentIndex == -1 -> queue.first()
            currentIndex < queue.lastIndex -> queue[currentIndex + 1]
            repeatMode == PlaybackRepeatMode.All || !fromAutoAdvance -> queue.first()
            else -> {
                isPlaying = false
                seekToRatio(0f)
                return
            }
        }
        playSong(next, queue, queueSourceName)
    }

    fun playPrevious() {
        val queue = playbackQueue.ifEmpty { currentTabSongs }
        if (queue.isEmpty()) return
        if (positionMs > 10_000L) {
            seekToRatio(0f)
            return
        }
        val currentIndex = queue.indexOfFirst { it.id == currentSong?.id }
        val previous = when {
            shuffleEnabled && queue.size > 1 -> queue.filter { it.id != currentSong?.id }.random()
            currentIndex > 0 -> queue[currentIndex - 1]
            repeatMode == PlaybackRepeatMode.All -> queue.last()
            else -> queue.first()
        }
        playSong(previous, queue, queueSourceName)
    }

    fun seekToRatio(ratio: Float) {
        val target = (durationMs * ratio.coerceIn(0f, 1f)).roundToInt().toLong()
        positionMs = target
        mpvPlayer?.seekTo(target)
        JavaFxAudioRuntime.runOnFx {
            mediaPlayer?.seek(Duration.millis(target.toDouble()))
        }
    }

    fun setPlayerVolume(nextVolume: Float) {
        volume = nextVolume.coerceIn(0f, 1f)
        mpvPlayer?.setVolume(volume)
        JavaFxAudioRuntime.runOnFx {
            mediaPlayer?.volume = volume.toDouble()
        }
    }

    fun refreshPlaybackPosition() {
        mpvPlayer?.let { player ->
            positionMs = player.currentPositionMs().coerceAtMost(durationMs.takeIf { it > 0L } ?: Long.MAX_VALUE)
            isPlaying = player.isPlaying
            return
        }
        JavaFxAudioRuntime.runOnFx {
            val player = mediaPlayer ?: return@runOnFx
            val current = player.currentTime.toMillis().toDurationMillis()
            val total = player.totalDuration.toMillis().toDurationMillis()
            updateOnUi {
                positionMs = current
                if (total > 0L) durationMs = total
                isPlaying = player.status == MediaPlayer.Status.PLAYING
            }
        }
    }

    fun toggleFavorite(song: DesktopSong? = currentSong) {
        val target = song ?: return
        favoriteIds = if (target.id in favoriteIds) {
            favoriteIds - target.id
        } else {
            favoriteIds + target.id
        }
    }

    fun toggleFloatingLyrics() {
        floatingLyricsEnabled = !floatingLyricsEnabled
    }

    fun toggleShuffle() {
        shuffleEnabled = !shuffleEnabled
    }

    fun cycleRepeatMode() {
        repeatMode = when (repeatMode) {
            PlaybackRepeatMode.Off -> PlaybackRepeatMode.All
            PlaybackRepeatMode.All -> PlaybackRepeatMode.One
            PlaybackRepeatMode.One -> PlaybackRepeatMode.Off
        }
    }

    fun setEqualizerBand(index: Int, gain: Float) {
        equalizerGains = equalizerGains.toMutableList().also {
            it[index] = gain.coerceIn(-12f, 12f)
        }
        JavaFxAudioRuntime.runOnFx {
            mediaPlayer?.let(::applyEqualizerTo)
        }
    }

    fun resetEqualizer() {
        equalizerGains = List(eqFrequencies.size) { 0f }
        JavaFxAudioRuntime.runOnFx {
            mediaPlayer?.let(::applyEqualizerTo)
        }
    }

    fun updateEqualizerEnabled(enabled: Boolean) {
        equalizerEnabled = enabled
        JavaFxAudioRuntime.runOnFx {
            mediaPlayer?.let(::applyEqualizerTo)
        }
    }

    fun dispose() {
        mpvPlayer?.stop()
        mpvPlayer = null
        JavaFxAudioRuntime.runOnFx {
            mediaPlayer?.dispose()
            mediaPlayer = null
        }
    }

    private fun filteredByQuery(source: List<DesktopSong>): List<DesktopSong> {
        val query = searchQuery.trim()
        if (query.isBlank()) return source
        return source.filter { song ->
            song.title.contains(query, ignoreCase = true) ||
                song.displayArtist.contains(query, ignoreCase = true) ||
                song.displayAlbum.contains(query, ignoreCase = true) ||
                song.fileName.contains(query, ignoreCase = true) ||
                song.path.toString().contains(query, ignoreCase = true)
        }
    }

    private fun seekToStartAndPlay() {
        seekToRatio(0f)
        mpvPlayer?.resume()
        JavaFxAudioRuntime.runOnFx {
            mediaPlayer?.play()
            updateOnUi { isPlaying = true }
        }
    }

    private fun applyEqualizerTo(player: MediaPlayer) {
        val equalizer = player.audioEqualizer
        equalizer.isEnabled = equalizerEnabled
        if (equalizer.bands.size != eqFrequencies.size) {
            equalizer.bands.clear()
            eqFrequencies.forEach { frequency ->
                equalizer.bands.add(EqualizerBand(frequency, frequency / 2.0, 0.0))
            }
        }
        equalizer.bands.forEachIndexed { index, band ->
            band.gain = equalizerGains.getOrElse(index) { 0f }.toDouble()
        }
    }
}

fun main() = application {
    val controller = remember { DesktopPlayerController() }
    val windowState = rememberWindowState(
        placement = WindowPlacement.Floating,
        size = DpSize(1180.dp, 760.dp)
    )
    val closeApplication: () -> Unit = {
        controller.dispose()
        exitApplication()
    }

    LaunchedEffect(Unit) {
        controller.loadCachedLibrary()
    }

    LaunchedEffect(Unit) {
        while (true) {
            controller.refreshPlaybackPosition()
            delay(250)
        }
    }

    Window(
        title = "PPlayer",
        state = windowState,
        icon = painterResource("pplayer_icon.png"),
        undecorated = true,
        transparent = true,
        onCloseRequest = closeApplication
    ) {
        PPlayerDesktopTheme(darkTheme = controller.isDarkTheme) {
            PPlayerDesktopApp(controller, windowState, closeApplication)
        }
    }

    if (controller.floatingLyricsEnabled) {
        FloatingLyricsWindow(controller)
    }
}

@Composable
private fun PPlayerDesktopTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = DesktopTypography,
        content = content
    )
}

@Composable
private fun FrameWindowScope.PPlayerDesktopApp(
    controller: DesktopPlayerController,
    windowState: androidx.compose.ui.window.WindowState,
    onCloseRequest: () -> Unit
) {
    val palette = remember(controller.currentSong?.id, controller.currentSong?.artwork?.cacheKey, controller.isDarkTheme) {
        paletteForSong(controller.currentSong, controller.isDarkTheme)
    }
    val isMaximized = windowState.placement == WindowPlacement.Maximized
    val appShape = RoundedCornerShape(if (isMaximized) 0.dp else 36.dp)

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(if (isMaximized) 0.dp else 2.dp)
            .clip(appShape),
        shape = appShape,
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            PixelPlayBackdrop(controller.currentSong, controller.isDarkTheme)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .padding(bottom = MiniPlayerHeight + MiniPlayerBottomSpacer)
            ) {
                DesktopChromeTopBar(controller, windowState, onCloseRequest)
                Spacer(Modifier.height(8.dp))
                Crossfade(
                    targetState = controller.route,
                    modifier = Modifier.weight(1f),
                    label = "route_crossfade"
                ) { route ->
                    when (route) {
                        DesktopRoute.Library -> LibraryScreen(controller)
                        DesktopRoute.Settings -> SettingsScreen(controller)
                        DesktopRoute.Equalizer -> EqualizerScreen(controller)
                        DesktopRoute.Stats -> StatsScreen(controller)
                        DesktopRoute.About -> AboutScreen(controller)
                    }
                }
            }

            MiniPlayerBar(
                controller = controller,
                palette = palette,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 18.dp, vertical = MiniPlayerBottomSpacer)
            )

            ExpandedPlayerOverlay(controller, palette)
            QueueOverlay(controller)
            AppDrawer(controller)
        }
    }
}

@Composable
private fun PixelPlayBackdrop(song: DesktopSong?, darkTheme: Boolean) {
    val palette = remember(song?.id, song?.artwork?.cacheKey, darkTheme) { paletteForSong(song, darkTheme) }
    val lowerOverlayColor = MaterialTheme.colorScheme.background.copy(alpha = if (darkTheme) 0.28f else 0.14f)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (darkTheme) {
                        listOf(
                            palette.container.copy(alpha = 0.86f),
                            MaterialTheme.colorScheme.background,
                            Color(0xFF141019)
                        )
                    } else {
                        listOf(
                            palette.container.copy(alpha = 0.70f),
                            MaterialTheme.colorScheme.background,
                            Color(0xFFFDF8FF)
                        )
                    }
                )
            )
    )
    Canvas(Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    palette.primary.copy(alpha = if (darkTheme) 0.18f else 0.12f),
                    palette.secondary.copy(alpha = if (darkTheme) 0.12f else 0.08f),
                    Color.Transparent
                ),
                start = Offset(width * 0.10f, 0f),
                end = Offset(width, height)
            )
        )
        drawRect(
            color = lowerOverlayColor,
            topLeft = Offset(0f, height * 0.62f)
        )
    }
}

@Composable
private fun FrameWindowScope.DesktopChromeTopBar(
    controller: DesktopPlayerController,
    windowState: androidx.compose.ui.window.WindowState,
    onCloseRequest: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val subtitle = when (controller.route) {
        DesktopRoute.Library -> controller.libraryRoot?.toString() ?: "尚未选择音乐目录"
        DesktopRoute.Settings -> "桌面端设置与移动端分类保持一致"
        DesktopRoute.Equalizer -> "JavaFX 播放内核的本地均衡器"
        DesktopRoute.Stats -> "本地音乐库与播放队列概览"
        DesktopRoute.About -> "PPlayer Windows 桌面端"
    }
    val maximized = windowState.placement == WindowPlacement.Maximized

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp)),
        shape = RoundedCornerShape(26.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                WindowDraggableArea(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        IconPillButton(
                            icon = Icons.Rounded.Menu,
                            contentDescription = "菜单",
                            selected = controller.drawerOpen,
                            onClick = { controller.drawerOpen = true }
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = controller.route.title,
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = controller.route == DesktopRoute.Library && controller.searchVisible,
                    enter = fadeIn(tween(180)) + scaleIn(initialScale = 0.96f),
                    exit = fadeOut(tween(120)) + scaleOut(targetScale = 0.96f)
                ) {
                    OutlinedTextField(
                        value = controller.searchQuery,
                        onValueChange = { controller.searchQuery = it },
                        modifier = Modifier.width(280.dp),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Rounded.Search, null) },
                        trailingIcon = {
                            if (controller.searchQuery.isNotBlank()) {
                                IconButton(onClick = { controller.searchQuery = "" }) {
                                    Icon(Icons.Rounded.Close, contentDescription = "清空搜索")
                                }
                            }
                        },
                        placeholder = { Text("搜索本地歌曲") },
                        shape = RoundedCornerShape(22.dp)
                    )
                }

                if (controller.route == DesktopRoute.Library) {
                    IconPillButton(
                        icon = Icons.Rounded.GraphicEq,
                        contentDescription = "悬浮歌词",
                        selected = controller.floatingLyricsEnabled,
                        onClick = controller::toggleFloatingLyrics
                    )
                    IconPillButton(
                        icon = Icons.Rounded.Search,
                        contentDescription = "搜索",
                        selected = controller.searchVisible,
                        onClick = { controller.searchVisible = !controller.searchVisible }
                    )
                    IconPillButton(
                        icon = Icons.Rounded.Folder,
                        contentDescription = "选择文件夹",
                        selected = controller.isScanning,
                        onClick = { scope.launch { controller.chooseAndScanFolder() } }
                    )
                }

                WindowControlButton(
                    icon = Icons.Rounded.Minimize,
                    contentDescription = "最小化",
                    onClick = { windowState.isMinimized = true }
                )
                WindowControlButton(
                    icon = if (maximized) Icons.Rounded.FullscreenExit else Icons.Rounded.CropSquare,
                    contentDescription = if (maximized) "还原窗口" else "最大化",
                    onClick = {
                        windowState.placement = if (maximized) WindowPlacement.Floating else WindowPlacement.Maximized
                    }
                )
                WindowControlButton(
                    icon = Icons.Rounded.Close,
                    contentDescription = "关闭",
                    tint = MaterialTheme.colorScheme.error,
                    onClick = onCloseRequest
                )
            }
            HorizontalDivider(
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.14f),
                thickness = DividerDefaults.Thickness
            )
        }
    }
}

@Composable
private fun DesktopTopBar(controller: DesktopPlayerController) {
    val scope = rememberCoroutineScope()
    val subtitle = when (controller.route) {
        DesktopRoute.Library -> controller.libraryRoot?.toString() ?: "尚未选择音乐目录"
        DesktopRoute.Settings -> "桌面端设置与移动端分类保持一致"
        DesktopRoute.Equalizer -> "JavaFX 播放内核的本地均衡器"
        DesktopRoute.Stats -> "本地音乐库与播放队列概览"
        DesktopRoute.About -> "PPlayer Windows 桌面端"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(76.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        IconPillButton(
            icon = Icons.Rounded.Menu,
            contentDescription = "菜单",
            selected = controller.drawerOpen,
            onClick = { controller.drawerOpen = true }
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = controller.route.title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        AnimatedVisibility(
            visible = controller.route == DesktopRoute.Library && controller.searchVisible,
            enter = fadeIn(tween(180)) + scaleIn(initialScale = 0.96f),
            exit = fadeOut(tween(120)) + scaleOut(targetScale = 0.96f)
        ) {
            OutlinedTextField(
                value = controller.searchQuery,
                onValueChange = { controller.searchQuery = it },
                modifier = Modifier.width(310.dp),
                singleLine = true,
                leadingIcon = { Icon(Icons.Rounded.Search, null) },
                trailingIcon = {
                    if (controller.searchQuery.isNotBlank()) {
                        IconButton(onClick = { controller.searchQuery = "" }) {
                            Icon(Icons.Rounded.Close, contentDescription = "清空搜索")
                        }
                    }
                },
                placeholder = { Text("搜索本地歌曲") },
                shape = RoundedCornerShape(24.dp)
            )
        }

        if (controller.route == DesktopRoute.Library) {
            IconPillButton(
                icon = Icons.Rounded.GraphicEq,
                contentDescription = "悬浮歌词",
                selected = controller.floatingLyricsEnabled,
                onClick = controller::toggleFloatingLyrics
            )
            IconPillButton(
                icon = Icons.Rounded.Search,
                contentDescription = "搜索",
                selected = controller.searchVisible,
                onClick = { controller.searchVisible = !controller.searchVisible }
            )
            IconPillButton(
                icon = Icons.Rounded.Folder,
                contentDescription = "选择文件夹",
                selected = controller.isScanning,
                onClick = { scope.launch { controller.chooseAndScanFolder() } }
            )
        }
    }
}

@Composable
private fun IconPillButton(
    icon: ImageVector,
    contentDescription: String,
    selected: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    FilledIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(48.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = if (selected) colors.primary else colors.surface.copy(alpha = 0.86f),
            contentColor = if (selected) colors.onPrimary else colors.onSurface
        )
    ) {
        Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun WindowControlButton(
    icon: ImageVector,
    contentDescription: String,
    tint: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: () -> Unit
) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier.size(36.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.78f),
            contentColor = tint
        )
    ) {
        Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(18.dp))
    }
}

@Composable
private fun LibraryScreen(controller: DesktopPlayerController) {
    Column(modifier = Modifier.fillMaxSize()) {
        LibraryActionRow(controller)
        Spacer(Modifier.height(10.dp))
        LibraryTabs(controller)
        Spacer(Modifier.height(12.dp))
        Crossfade(
            targetState = controller.libraryTab,
            modifier = Modifier.weight(1f),
            label = "library_tab_crossfade"
        ) { tab ->
            when (tab) {
                LibraryTab.Songs -> SongsTab(
                    songs = controller.visibleSongs,
                    controller = controller,
                    sourceName = "歌曲"
                )
                LibraryTab.Albums -> AlbumsTab(controller)
                LibraryTab.Artists -> ArtistsTab(controller)
                LibraryTab.Folders -> FoldersTab(controller)
                LibraryTab.Favorites -> SongsTab(
                    songs = controller.favoriteSongs,
                    controller = controller,
                    sourceName = "喜欢"
                )
                LibraryTab.Queue -> SongsTab(
                    songs = controller.playbackQueue.ifEmpty { controller.visibleSongs },
                    controller = controller,
                    sourceName = controller.queueSourceName
                )
            }
        }
    }
}

@Composable
private fun LibraryActionRow(controller: DesktopPlayerController) {
    val scope = rememberCoroutineScope()
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
        shape = expressiveShape(28.dp, controller.useSmoothCorners)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (controller.isScanning) "正在扫描本地音乐" else controller.statusMessage,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${controller.songs.size} 首歌曲 · ${controller.albums.size} 张专辑 · ${controller.artists.size} 位艺术家",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            SortMenu(controller)
            FilledTonalButton(
                onClick = { scope.launch { controller.scanDefaultLibrary() } },
                enabled = !controller.isScanning && controller.libraryRoot != null,
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Rounded.LibraryMusic, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (controller.isScanning) "扫描中" else "重新扫描")
            }
        }
    }
}

@Composable
private fun SortMenu(controller: DesktopPlayerController) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        FilledTonalButton(
            onClick = { expanded = true },
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(Icons.Rounded.Sort, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(controller.sortMode.label)
            Icon(Icons.Rounded.KeyboardArrowDown, null, modifier = Modifier.size(18.dp))
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            SortMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = { Text(mode.label) },
                    onClick = {
                        controller.sortMode = mode
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun LibraryTabs(controller: DesktopPlayerController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LibraryTab.entries.forEach { tab ->
            val selected = controller.libraryTab == tab
            val containerColor by animateColorAsState(
                targetValue = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface.copy(alpha = 0.70f),
                animationSpec = tween(220),
                label = "tab_color"
            )
            val contentColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            Surface(
                modifier = Modifier
                    .height(44.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .clickable { controller.libraryTab = tab },
                color = containerColor,
                shape = RoundedCornerShape(24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(tab.icon, null, tint = contentColor, modifier = Modifier.size(19.dp))
                    Text(tab.label, color = contentColor, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun SongsTab(
    songs: List<DesktopSong>,
    controller: DesktopPlayerController,
    sourceName: String
) {
    if (songs.isEmpty()) {
        LibraryEmptyState(controller)
        return
    }
    val listState = rememberLazyListState()
    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 18.dp)
        ) {
            items(songs, key = { it.id }) { song ->
                DesktopSongListItem(
                    song = song,
                    isCurrent = song.id == controller.currentSong?.id,
                    isPlaying = controller.isPlaying && song.id == controller.currentSong?.id,
                    isFavorite = song.id in controller.favoriteIds,
                    useSmoothCorners = controller.useSmoothCorners,
                    onFavoriteClick = { controller.toggleFavorite(song) },
                    onClick = { controller.playSong(song, songs, sourceName) }
                )
            }
        }
    }
}

@Composable
private fun LibraryEmptyState(controller: DesktopPlayerController) {
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.widthIn(max = 380.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AlbumArtTile(
                song = null,
                size = 86.dp,
                shape = CircleShape
            )
            Text(
                text = if (controller.searchQuery.isBlank()) "还没有扫描到音乐" else "没有符合搜索的歌曲",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            Text(
                text = if (controller.searchQuery.isBlank()) {
                    "选择一个包含本地音乐的文件夹，桌面端会按歌曲、专辑、艺术家和文件夹整理。"
                } else {
                    "换一个关键词，或清空搜索查看完整音乐库。"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = { scope.launch { controller.chooseAndScanFolder() } },
                shape = RoundedCornerShape(24.dp)
            ) {
                Icon(Icons.Rounded.Folder, null)
                Spacer(Modifier.width(8.dp))
                Text("选择音乐文件夹")
            }
        }
    }
}

@Composable
private fun DesktopSongListItem(
    song: DesktopSong,
    isCurrent: Boolean,
    isPlaying: Boolean,
    isFavorite: Boolean,
    useSmoothCorners: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val highlightProgress by animateFloatAsState(
        targetValue = if (isCurrent) 1f else 0f,
        animationSpec = tween(360, easing = FastOutSlowInEasing),
        label = "song_highlight"
    )
    val rowShape = expressiveShape(
        radius = lerpDp(20.dp, 34.dp, highlightProgress),
        useSmoothCorners = useSmoothCorners
    )
    val containerColor by animateColorAsState(
        targetValue = if (isCurrent) colors.primaryContainer.copy(alpha = 0.86f) else colors.surface.copy(alpha = 0.72f),
        animationSpec = tween(360),
        label = "song_container"
    )
    val contentColor = if (isCurrent) colors.onPrimaryContainer else colors.onSurface

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(rowShape)
            .clickable(onClick = onClick),
        color = containerColor,
        shape = rowShape
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AlbumArtTile(
                song = song,
                size = 50.dp,
                shape = RoundedCornerShape(lerpDp(12.dp, 26.dp, highlightProgress))
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${song.displayArtist} · ${song.displayAlbum}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor.copy(alpha = 0.70f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (isPlaying || isCurrent) {
                PlayingEqIcon(
                    modifier = Modifier.size(width = 22.dp, height = 18.dp),
                    color = contentColor,
                    isPlaying = isPlaying
                )
            } else {
                Text(
                    text = formatDuration(song.durationMs).takeIf { song.durationMs > 0L } ?: formatSize(song.sizeBytes),
                    style = MaterialTheme.typography.labelMedium,
                    color = contentColor.copy(alpha = 0.62f),
                    maxLines = 1
                )
            }
            IconButton(onClick = onFavoriteClick, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = if (isFavorite) "取消喜欢" else "喜欢",
                    tint = if (isFavorite) colors.secondary else contentColor.copy(alpha = 0.70f)
                )
            }
        }
    }
}

@Composable
private fun AlbumsTab(controller: DesktopPlayerController) {
    if (controller.albums.isEmpty()) {
        LibraryEmptyState(controller)
        return
    }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(168.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(controller.albums, key = { it.displayTitle + it.displayArtist }) { album ->
            AlbumGridTile(
                album = album,
                useSmoothCorners = controller.useSmoothCorners,
                onClick = {
                    album.songs.firstOrNull()?.let { controller.playSong(it, album.songs, album.displayTitle) }
                }
            )
        }
    }
}

@Composable
private fun AlbumGridTile(
    album: DesktopAlbum,
    useSmoothCorners: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(expressiveShape(24.dp, useSmoothCorners))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
        shape = expressiveShape(24.dp, useSmoothCorners)
    ) {
        Column(Modifier.padding(12.dp)) {
            AlbumArtTile(
                song = album.songs.firstOrNull(),
                size = 144.dp,
                shape = expressiveShape(20.dp, useSmoothCorners),
                modifier = Modifier.fillMaxWidth().aspectRatio(1f)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = album.displayTitle,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${album.displayArtist} · ${album.songs.size} 首",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ArtistsTab(controller: DesktopPlayerController) {
    if (controller.artists.isEmpty()) {
        LibraryEmptyState(controller)
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 18.dp)
    ) {
        items(controller.artists, key = { it.name }) { artist ->
            LibraryGroupRow(
                icon = Icons.Rounded.Person,
                title = artist.name,
                subtitle = "${artist.songs.size} 首歌曲 · ${artist.songs.map { it.displayAlbum }.distinct().size} 张专辑",
                seedSong = artist.songs.firstOrNull(),
                useSmoothCorners = controller.useSmoothCorners,
                onClick = { artist.songs.firstOrNull()?.let { controller.playSong(it, artist.songs, artist.name) } }
            )
        }
    }
}

@Composable
private fun FoldersTab(controller: DesktopPlayerController) {
    if (controller.folders.isEmpty()) {
        LibraryEmptyState(controller)
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 18.dp)
    ) {
        items(controller.folders, key = { it.path.toString() }) { folder ->
            LibraryGroupRow(
                icon = Icons.Rounded.Folder,
                title = folder.name,
                subtitle = "${folder.songs.size} 首歌曲 · ${folder.path}",
                seedSong = folder.songs.firstOrNull(),
                useSmoothCorners = controller.useSmoothCorners,
                onClick = { folder.songs.firstOrNull()?.let { controller.playSong(it, folder.songs, folder.name) } }
            )
        }
    }
}

@Composable
private fun LibraryGroupRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    seedSong: DesktopSong?,
    useSmoothCorners: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(expressiveShape(22.dp, useSmoothCorners))
            .clickable(onClick = onClick),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
        shape = expressiveShape(22.dp, useSmoothCorners)
    ) {
        Row(
            modifier = Modifier.padding(13.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AlbumArtTile(song = seedSong, size = 54.dp, shape = RoundedCornerShape(16.dp), fallbackIcon = icon)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun MiniPlayerBar(
    controller: DesktopPlayerController,
    palette: PlayerPalette,
    modifier: Modifier = Modifier
) {
    val shape = expressiveShape(34.dp, controller.useSmoothCorners)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(MiniPlayerHeight)
            .clip(shape)
            .clickable { if (controller.currentSong != null) controller.playerExpanded = true },
        color = palette.container,
        contentColor = palette.onContainer,
        shadowElevation = 10.dp,
        shape = shape
    ) {
        Box(Modifier.fillMaxSize()) {
            ProgressFill(
                progress = progressRatio(controller.positionMs, controller.durationMs),
                color = palette.primary.copy(alpha = 0.22f),
                modifier = Modifier.fillMaxSize()
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 12.dp, end = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AlbumArtTile(
                    song = controller.currentSong,
                    size = 50.dp,
                    shape = CircleShape
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = controller.currentSong?.title ?: "PPlayer",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = palette.onContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = currentLyricText(controller.currentLyrics, controller.positionMs)
                            ?: controller.currentSong?.displayArtist
                            ?: controller.statusMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = palette.onContainer.copy(alpha = 0.70f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                MiniControlButton(
                    icon = Icons.Rounded.SkipPrevious,
                    contentDescription = "上一首",
                    containerColor = palette.onContainer.copy(alpha = 0.16f),
                    contentColor = palette.onContainer,
                    enabled = controller.songs.isNotEmpty(),
                    onClick = controller::playPrevious
                )
                MiniControlButton(
                    icon = if (controller.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                    contentDescription = if (controller.isPlaying) "暂停" else "播放",
                    containerColor = palette.primary,
                    contentColor = Color.White,
                    enabled = controller.currentSong != null || controller.currentTabSongs.isNotEmpty(),
                    onClick = controller::togglePlayPause
                )
                MiniControlButton(
                    icon = Icons.Rounded.SkipNext,
                    contentDescription = "下一首",
                    containerColor = palette.onContainer.copy(alpha = 0.16f),
                    contentColor = palette.onContainer,
                    enabled = controller.songs.isNotEmpty(),
                    onClick = controller::playNext
                )
            }
        }
    }
}

@Composable
private fun MiniControlButton(
    icon: ImageVector,
    contentDescription: String,
    containerColor: Color,
    contentColor: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    FilledIconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(40.dp),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.30f),
            disabledContentColor = contentColor.copy(alpha = 0.36f)
        )
    ) {
        Icon(icon, contentDescription = contentDescription, modifier = Modifier.size(22.dp))
    }
}

@Composable
private fun ExpandedPlayerOverlay(controller: DesktopPlayerController, palette: PlayerPalette) {
    AnimatedVisibility(
        visible = controller.playerExpanded && controller.currentSong != null,
        enter = fadeIn(tween(180)),
        exit = fadeOut(tween(180)),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.62f))
                .pointerInput(Unit) {
                    detectTapGestures { controller.playerExpanded = false }
                }
        )
    }

    AnimatedVisibility(
        visible = controller.playerExpanded && controller.currentSong != null,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(dampingRatio = 0.88f, stiffness = 260f)
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(220)
        ) + fadeOut(),
        modifier = Modifier.fillMaxSize()
    ) {
        FullPlayerContent(controller, palette)
    }
}

@Composable
private fun FullPlayerContent(controller: DesktopPlayerController, palette: PlayerPalette) {
    val song = controller.currentSong ?: return
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = palette.container,
        contentColor = palette.onContainer
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            PixelPlayerSurfaceBackground(song, palette)
            val isWide = maxWidth > 900.dp
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { controller.playerExpanded = false }) {
                        Icon(Icons.Rounded.KeyboardArrowDown, contentDescription = "收起", tint = palette.onContainer)
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = controller.queueSourceName,
                            style = MaterialTheme.typography.labelLarge,
                            color = palette.onContainer.copy(alpha = 0.68f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "正在播放",
                            style = MaterialTheme.typography.titleSmall,
                            color = palette.onContainer,
                            maxLines = 1
                        )
                    }
                    IconButton(onClick = { controller.showQueueOverlay = true }) {
                        Icon(Icons.Rounded.QueueMusic, contentDescription = "队列", tint = palette.onContainer)
                    }
                }

                if (isWide) {
                    Row(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(32.dp)
                    ) {
                        PlayerArtworkColumn(song, controller, palette, Modifier.weight(0.86f))
                        FullPlayerMainColumn(controller, palette, Modifier.weight(1f))
                    }
                } else {
                    Column(
                        modifier = Modifier.weight(1f).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        PlayerArtworkColumn(song, controller, palette, Modifier.fillMaxWidth(0.68f))
                        Spacer(Modifier.height(22.dp))
                        FullPlayerMainColumn(controller, palette, Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}

@Composable
private fun PixelPlayerSurfaceBackground(song: DesktopSong, palette: PlayerPalette) {
    Canvas(Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.linearGradient(
                colors = listOf(
                    palette.primary.copy(alpha = 0.28f),
                    palette.secondary.copy(alpha = 0.20f),
                    palette.tertiary.copy(alpha = 0.14f),
                    Color.Transparent
                ),
                start = Offset(size.width * 0.10f, size.height * 0.08f),
                end = Offset(size.width, size.height)
            )
        )
        val seed = abs(song.id.hashCode() % 360) / 360f
        val lineColor = palette.onContainer.copy(alpha = 0.05f + seed * 0.04f)
        for (i in 0..6) {
            val y = size.height * (0.18f + i * 0.11f)
            drawLine(
                color = lineColor,
                start = Offset(0f, y),
                end = Offset(size.width, y + size.height * 0.08f),
                strokeWidth = 2f
            )
        }
    }
}

@Composable
private fun PlayerArtworkColumn(
    song: DesktopSong,
    controller: DesktopPlayerController,
    palette: PlayerPalette,
    modifier: Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AlbumArtTile(
            song = song,
            size = 360.dp,
            shape = expressiveShape(42.dp, controller.useSmoothCorners),
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 420.dp)
                .aspectRatio(1f)
        )
        Spacer(Modifier.height(22.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FullPlayerChip(
                text = "歌词",
                selected = controller.fullPlayerShowsLyrics,
                palette = palette,
                onClick = { controller.fullPlayerShowsLyrics = !controller.fullPlayerShowsLyrics }
            )
            FullPlayerChip(
                text = "悬浮歌词",
                selected = controller.floatingLyricsEnabled,
                palette = palette,
                onClick = controller::toggleFloatingLyrics
            )
            FullPlayerChip(
                text = "队列",
                selected = controller.showQueueOverlay,
                palette = palette,
                onClick = { controller.showQueueOverlay = true }
            )
            FullPlayerChip(
                text = "收藏",
                selected = song.id in controller.favoriteIds,
                palette = palette,
                onClick = { controller.toggleFavorite(song) }
            )
        }
    }
}

@Composable
private fun FullPlayerChip(
    text: String,
    selected: Boolean,
    palette: PlayerPalette,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .height(38.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        color = if (selected) palette.primary else palette.onContainer.copy(alpha = 0.12f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box(Modifier.padding(horizontal = 16.dp), contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = if (selected) Color.White else palette.onContainer,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun FullPlayerMainColumn(
    controller: DesktopPlayerController,
    palette: PlayerPalette,
    modifier: Modifier
) {
    val song = controller.currentSong ?: return
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedContent(
            targetState = controller.fullPlayerShowsLyrics,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            label = "full_player_center"
        ) { showLyrics ->
            if (showLyrics) {
                LyricsPanel(
                    lyrics = controller.currentLyrics,
                    positionMs = controller.positionMs,
                    palette = palette,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = song.title,
                        style = MaterialTheme.typography.displayMedium.copy(fontSize = 42.sp),
                        fontWeight = FontWeight.Bold,
                        color = palette.onContainer,
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = song.displayArtist,
                        style = MaterialTheme.typography.titleLarge,
                        color = palette.onContainer.copy(alpha = 0.72f),
                        textAlign = TextAlign.Center,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(22.dp))
                    Text(
                        text = currentLyricText(controller.currentLyrics, controller.positionMs) ?: song.displayAlbum,
                        style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                        color = readableLyricColor(song, controller.isDarkTheme, palette),
                        textAlign = TextAlign.Center,
                        lineHeight = 32.sp,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
        FullPlayerProgressSection(controller, palette)
        Spacer(Modifier.height(20.dp))
        FullPlayerControls(controller, palette)
    }
}

@Composable
private fun FullPlayerProgressSection(controller: DesktopPlayerController, palette: PlayerPalette) {
    Column(Modifier.fillMaxWidth()) {
        Slider(
            value = progressRatio(controller.positionMs, controller.durationMs),
            onValueChange = controller::seekToRatio,
            enabled = controller.durationMs > 0L
        )
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(formatDuration(controller.positionMs), color = palette.onContainer.copy(alpha = 0.72f), style = MaterialTheme.typography.labelMedium)
            Text(formatDuration(controller.durationMs), color = palette.onContainer.copy(alpha = 0.72f), style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun FullPlayerControls(controller: DesktopPlayerController, palette: PlayerPalette) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = controller::toggleShuffle, modifier = Modifier.size(48.dp)) {
            Icon(
                Icons.Rounded.Shuffle,
                contentDescription = "随机播放",
                tint = if (controller.shuffleEnabled) palette.primary else palette.onContainer.copy(alpha = 0.62f)
            )
        }
        Spacer(Modifier.width(12.dp))
        IconButton(onClick = controller::playPrevious, modifier = Modifier.size(56.dp)) {
            Icon(Icons.Rounded.SkipPrevious, contentDescription = "上一首", tint = palette.onContainer, modifier = Modifier.size(32.dp))
        }
        Spacer(Modifier.width(12.dp))
        FilledIconButton(
            onClick = controller::togglePlayPause,
            modifier = Modifier.size(74.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = palette.primary,
                contentColor = Color.White
            )
        ) {
            Icon(
                if (controller.isPlaying) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                contentDescription = if (controller.isPlaying) "暂停" else "播放",
                modifier = Modifier.size(38.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        IconButton(onClick = controller::playNext, modifier = Modifier.size(56.dp)) {
            Icon(Icons.Rounded.SkipNext, contentDescription = "下一首", tint = palette.onContainer, modifier = Modifier.size(32.dp))
        }
        Spacer(Modifier.width(12.dp))
        IconButton(onClick = controller::cycleRepeatMode, modifier = Modifier.size(48.dp)) {
            Icon(
                if (controller.repeatMode == PlaybackRepeatMode.One) Icons.Rounded.RepeatOne else Icons.Rounded.Repeat,
                contentDescription = "循环模式",
                tint = if (controller.repeatMode != PlaybackRepeatMode.Off) palette.primary else palette.onContainer.copy(alpha = 0.62f)
            )
        }
    }
}

@Composable
private fun LyricsPanel(
    lyrics: DesktopLyrics?,
    positionMs: Long,
    palette: PlayerPalette,
    modifier: Modifier = Modifier
) {
    if (lyrics == null || (lyrics.synced.isEmpty() && lyrics.plain.isEmpty())) {
        Box(modifier, contentAlignment = Alignment.Center) {
            Text(
                text = "没有找到歌词。桌面端会优先读取歌曲同名 .lrc / .txt 文件。",
                color = palette.onContainer.copy(alpha = 0.72f),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center
            )
        }
        return
    }

    val activeIndex = currentLyricIndex(lyrics, positionMs)
    val listState = rememberLazyListState()
    LaunchedEffect(activeIndex) {
        if (activeIndex >= 0) {
            listState.animateScrollToItem((activeIndex - 3).coerceAtLeast(0))
        }
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(vertical = 52.dp, horizontal = 18.dp)
    ) {
        if (lyrics.synced.isNotEmpty()) {
            items(lyrics.synced.size) { index ->
                val line = lyrics.synced[index]
                val active = index == activeIndex
                Text(
                    text = line.text,
                    style = if (active) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                    color = if (active) palette.primary else palette.onContainer.copy(alpha = 0.48f),
                    lineHeight = if (active) 34.sp else 29.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            items(lyrics.plain) { line ->
                Text(
                    text = line,
                    style = MaterialTheme.typography.titleLarge,
                    color = palette.onContainer.copy(alpha = 0.78f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
private fun QueueOverlay(controller: DesktopPlayerController) {
    AnimatedVisibility(
        visible = controller.showQueueOverlay,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.32f))
                .pointerInput(Unit) {
                    detectTapGestures { controller.showQueueOverlay = false }
                },
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 560.dp)
                    .padding(18.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = expressiveShape(32.dp, controller.useSmoothCorners),
                shadowElevation = 16.dp
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("当前队列", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
                            Text(controller.queueSourceName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        IconButton(onClick = { controller.showQueueOverlay = false }) {
                            Icon(Icons.Rounded.Close, contentDescription = "关闭")
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    QueueSongList(
                        songs = controller.playbackQueue.ifEmpty { controller.visibleSongs },
                        controller = controller,
                        sourceName = controller.queueSourceName,
                        modifier = Modifier.heightIn(max = 468.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QueueSongList(
    songs: List<DesktopSong>,
    controller: DesktopPlayerController,
    sourceName: String,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(songs, key = { it.id }) { song ->
            val isCurrent = song.id == controller.currentSong?.id
            QueueSongRow(
                song = song,
                isCurrent = isCurrent,
                isPlaying = controller.isPlaying && isCurrent,
                useSmoothCorners = controller.useSmoothCorners,
                onClick = { controller.playSong(song, songs, sourceName) }
            )
        }
    }
}

@Composable
private fun QueueSongRow(
    song: DesktopSong,
    isCurrent: Boolean,
    isPlaying: Boolean,
    useSmoothCorners: Boolean,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val rowShape = expressiveShape(if (isCurrent) 24.dp else 18.dp, useSmoothCorners)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(rowShape)
            .clickable(onClick = onClick),
        color = if (isCurrent) colors.primaryContainer.copy(alpha = 0.86f) else colors.surfaceVariant.copy(alpha = 0.42f),
        shape = rowShape
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AlbumArtTile(song = song, size = 48.dp, shape = RoundedCornerShape(if (isCurrent) 18.dp else 14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isCurrent) colors.onPrimaryContainer else colors.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${song.displayArtist} · ${song.displayAlbum}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isCurrent) colors.onPrimaryContainer.copy(alpha = 0.72f) else colors.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (isPlaying) {
                PlayingEqIcon(
                    modifier = Modifier.size(width = 22.dp, height = 18.dp),
                    color = colors.primary,
                    isPlaying = true
                )
            } else {
                Text(
                    text = formatDuration(song.durationMs),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isCurrent) colors.onPrimaryContainer.copy(alpha = 0.70f) else colors.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SettingsScreen(controller: DesktopPlayerController) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        SettingsCategoryList(controller)
        SettingsDetail(controller, modifier = Modifier.weight(1f))
    }
}

@Composable
private fun SettingsCategoryList(controller: DesktopPlayerController) {
    val selected = controller.selectedSettingsCategory
    Surface(
        modifier = Modifier.width(320.dp).fillMaxHeight(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
        shape = expressiveShape(28.dp, controller.useSmoothCorners)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SettingsCategory.entries.forEach { category ->
                val active = selected == category
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(if (active) 24.dp else 14.dp))
                        .clickable { controller.selectedSettingsCategory = category },
                    color = if (active) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                    shape = RoundedCornerShape(if (active) 24.dp else 14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            category.icon,
                            null,
                            tint = if (active) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Column {
                            Text(category.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                category.subtitle,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsDetail(controller: DesktopPlayerController, modifier: Modifier = Modifier) {
    val category = controller.selectedSettingsCategory ?: SettingsCategory.Library
    Surface(
        modifier = modifier.fillMaxHeight(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
        shape = expressiveShape(28.dp, controller.useSmoothCorners)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(category.title, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
            Text(category.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            when (category) {
                SettingsCategory.Library -> LibrarySettings(controller)
                SettingsCategory.Appearance -> AppearanceSettings(controller)
                SettingsCategory.Playback -> PlaybackSettings(controller)
                SettingsCategory.Behavior -> BehaviorSettings(controller)
                SettingsCategory.Backup -> BackupSettings()
                SettingsCategory.About -> AboutDetails()
            }
        }
    }
}

@Composable
private fun LibrarySettings(controller: DesktopPlayerController) {
    val scope = rememberCoroutineScope()
    SettingsRow(
        title = "音乐库目录",
        subtitle = controller.libraryRoot?.toString() ?: "未选择",
        trailing = {
            OutlinedButton(onClick = { scope.launch { controller.chooseAndScanFolder() } }, shape = RoundedCornerShape(22.dp)) {
                Icon(Icons.Rounded.Folder, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("更改")
            }
        }
    )
    SettingsRow(
        title = "本地搜索",
        subtitle = "只搜索当前已扫描的本地音乐，没有云端切换。",
        trailing = {
            Switch(checked = controller.searchVisible, onCheckedChange = { controller.searchVisible = it })
        }
    )
    SettingsRow(
        title = "文件夹播放视图",
        subtitle = "按文件夹作为播放列表整理歌曲。",
        trailing = {
            Switch(checked = controller.isFolderPlaylistView, onCheckedChange = { controller.isFolderPlaylistView = it })
        }
    )
}

@Composable
private fun AppearanceSettings(controller: DesktopPlayerController) {
    SettingsRow(
        title = "深色主题",
        subtitle = "使用 PPlayer 移动端同源的紫粉橙 Material 色彩。",
        trailing = { Switch(checked = controller.isDarkTheme, onCheckedChange = { controller.isDarkTheme = it }) }
    )
    SettingsRow(
        title = "平滑圆角",
        subtitle = "播放器、列表和设置保持移动端的柔和圆角感。",
        trailing = { Switch(checked = controller.useSmoothCorners, onCheckedChange = { controller.useSmoothCorners = it }) }
    )
    SettingsRow(
        title = "歌曲取色",
        subtitle = "播放器背景和歌词颜色跟随当前歌曲生成更易读的莫奈色。",
        trailing = { Switch(checked = controller.albumThemeEnabled, onCheckedChange = { controller.albumThemeEnabled = it }) }
    )
}

@Composable
private fun PlaybackSettings(controller: DesktopPlayerController) {
    SettingsRow(
        title = "随机播放",
        subtitle = "下一首会在当前队列内随机选择。",
        trailing = { Switch(checked = controller.shuffleEnabled, onCheckedChange = { controller.shuffleEnabled = it }) }
    )
    SettingsRow(
        title = "循环模式",
        subtitle = when (controller.repeatMode) {
            PlaybackRepeatMode.Off -> "关闭"
            PlaybackRepeatMode.All -> "列表循环"
            PlaybackRepeatMode.One -> "单曲循环"
        },
        trailing = {
            Button(onClick = controller::cycleRepeatMode, shape = RoundedCornerShape(22.dp)) {
                Text("切换")
            }
        }
    )
    SettingsRow(
        title = "播放内核",
        subtitle = "常见格式优先使用系统内核，FLAC/APE/OGG/OPUS/WMA/WV/TTA/DSF 等格式自动交给 mpv。",
        trailing = { Text("JavaFX + mpv", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge) }
    )
}

@Composable
private fun BehaviorSettings(controller: DesktopPlayerController) {
    SettingsRow(
        title = "启动后自动扫描",
        subtitle = "打开桌面播放器时扫描上次或系统音乐目录。",
        trailing = { Text("已开启", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge) }
    )
    SettingsRow(
        title = "窗口歌词",
        subtitle = "Windows 桌面悬浮歌词，颜色跟随莫奈色系。",
        trailing = { Switch(checked = controller.floatingLyricsEnabled, onCheckedChange = { controller.floatingLyricsEnabled = it }) }
    )
    if (controller.floatingLyricsEnabled) {
        SettingsRow(
            title = "锁定窗口歌词",
            subtitle = "锁定后不再拖动歌词窗口。",
            trailing = { Switch(checked = controller.floatingLyricsLocked, onCheckedChange = { controller.floatingLyricsLocked = it }) }
        )
        SettingsRow(
            title = "显示歌词背景",
            subtitle = "关闭后只显示歌词文字。",
            trailing = { Switch(checked = controller.floatingLyricsBackgroundEnabled, onCheckedChange = { controller.floatingLyricsBackgroundEnabled = it }) }
        )
        SettingsRow(
            title = "双行歌词",
            subtitle = "同时显示当前行和下一行，长歌词会自然换行。",
            trailing = { Switch(checked = controller.floatingLyricsTwoLine, onCheckedChange = { controller.floatingLyricsTwoLine = it }) }
        )
        SettingsSliderRow(
            title = "歌词大小",
            subtitle = "${(controller.floatingLyricsTextScale * 100).roundToInt()}%",
            value = controller.floatingLyricsTextScale,
            valueRange = 0.5f..1.6f,
            onValueChange = { controller.floatingLyricsTextScale = it }
        )
        FloatingLyricsAlignmentRow(controller)
        FloatingLyricsColorRow(controller)
    }
}

@Composable
private fun FloatingLyricsAlignmentRow(controller: DesktopPlayerController) {
    SettingsChoiceRow(
        title = "歌词对齐",
        subtitle = controller.floatingLyricsAlignment.label,
        options = FloatingLyricsAlignment.entries.map { it.label },
        selected = controller.floatingLyricsAlignment.label,
        onSelected = { label ->
            controller.floatingLyricsAlignment = FloatingLyricsAlignment.entries.first { it.label == label }
        }
    )
}

@Composable
private fun FloatingLyricsColorRow(controller: DesktopPlayerController) {
    SettingsChoiceRow(
        title = "歌词颜色",
        subtitle = controller.floatingLyricsColorMode.label,
        options = FloatingLyricsColorMode.entries.map { it.label },
        selected = controller.floatingLyricsColorMode.label,
        onSelected = { label ->
            controller.floatingLyricsColorMode = FloatingLyricsColorMode.entries.first { it.label == label }
        }
    )
}

@Composable
private fun BackupSettings() {
    SettingsRow(
        title = "桌面端本地数据",
        subtitle = "当前版本保存运行态设置；播放列表与配置导入导出会作为后续桌面专用功能补齐。",
        trailing = { Text("准备中", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelLarge) }
    )
}

@Composable
private fun AboutDetails() {
    SettingsRow("版本号", DesktopVersion) {
        Text("Windows", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
    }
    SettingsRow("更新日期", DesktopUpdatedAt) {
        Text("当前", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
    }
    SettingsRow("应用信息", "PPlayer 桌面端，面向 Windows 的本地音乐播放器。界面、歌词、队列、设置分类对齐移动端。") {
        Icon(Icons.Rounded.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun SettingsRow(
    title: String,
    subtitle: String,
    trailing: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        trailing()
    }
}

@Composable
private fun SettingsSliderRow(
    title: String,
    subtitle: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    onValueChange: (Float) -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Slider(value = value, onValueChange = onValueChange, valueRange = valueRange)
    }
}

@Composable
private fun SettingsChoiceRow(
    title: String,
    subtitle: String,
    options: List<String>,
    selected: String,
    onSelected: (String) -> Unit
) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEach { option ->
                val active = option == selected
                Surface(
                    modifier = Modifier
                        .height(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onSelected(option) },
                    color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.60f),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Box(Modifier.padding(horizontal = 14.dp), contentAlignment = Alignment.Center) {
                        Text(
                            option,
                            color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EqualizerScreen(controller: DesktopPlayerController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
        shape = expressiveShape(28.dp, controller.useSmoothCorners)
    ) {
        Column(
            modifier = Modifier.padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("均衡器", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface)
                    Text("与移动端入口一致，Windows 端使用本地播放内核可用的频段。", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(checked = controller.equalizerEnabled, onCheckedChange = controller::updateEqualizerEnabled)
            }
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                controller.equalizerGains.forEachIndexed { index, gain ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("${gain.roundToInt()}dB", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Slider(
                            value = gain,
                            onValueChange = { controller.setEqualizerBand(index, it) },
                            valueRange = -12f..12f,
                            modifier = Modifier
                                .height(220.dp)
                                .width(44.dp)
                        )
                        Text(formatFrequency(eqFrequencies[index]), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            OutlinedButton(onClick = controller::resetEqualizer, shape = RoundedCornerShape(22.dp)) {
                Text("重置")
            }
        }
    }
}

@Composable
private fun StatsScreen(controller: DesktopPlayerController) {
    val totalSize = controller.songs.sumOf { it.sizeBytes }
    val knownDuration = controller.songs.sumOf { it.durationMs }
    LazyVerticalGrid(
        columns = GridCells.Adaptive(220.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 18.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { StatTile("歌曲", "${controller.songs.size}", "本地音乐库") }
        item { StatTile("专辑", "${controller.albums.size}", "按元数据归类") }
        item { StatTile("艺术家", "${controller.artists.size}", "按艺术家归类") }
        item { StatTile("喜欢", "${controller.favoriteIds.size}", "当前桌面会话") }
        item { StatTile("队列", "${controller.playbackQueue.size}", controller.queueSourceName) }
        item { StatTile("容量", formatSize(totalSize), "已扫描音频文件") }
        item { StatTile("时长", if (knownDuration > 0L) formatDuration(knownDuration) else "未知", "可读取元数据的歌曲") }
        item { StatTile("目录", "${controller.folders.size}", "包含音乐的文件夹") }
    }
}

@Composable
private fun StatTile(title: String, value: String, subtitle: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun AboutScreen(controller: DesktopPlayerController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
        shape = expressiveShape(28.dp, controller.useSmoothCorners)
    ) {
        Column(
            modifier = Modifier.padding(26.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AlbumArtTile(song = controller.currentSong, size = 96.dp, shape = expressiveShape(28.dp, controller.useSmoothCorners))
            Text("PPlayer", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onSurface)
            Text("Windows 桌面音乐播放器", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            HorizontalDivider(color = DividerDefaults.color.copy(alpha = 0.45f))
            AboutDetails()
        }
    }
}

@Composable
private fun AppDrawer(controller: DesktopPlayerController) {
    AnimatedVisibility(
        visible = controller.drawerOpen,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.28f))
                .pointerInput(Unit) {
                    detectTapGestures { controller.drawerOpen = false }
                }
        )
    }
    AnimatedVisibility(
        visible = controller.drawerOpen,
        enter = slideInHorizontally(initialOffsetX = { -it }, animationSpec = spring(dampingRatio = 0.90f, stiffness = 300f)) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(180)) + fadeOut(),
        modifier = Modifier.fillMaxHeight().wrapContentWidth()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .width(330.dp)
                .padding(14.dp),
            color = MaterialTheme.colorScheme.surface,
            shape = expressiveShape(30.dp, controller.useSmoothCorners),
            shadowElevation = 18.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AlbumArtTile(controller.currentSong, 48.dp, CircleShape)
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("PPlayer", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
                        Text("本地音乐", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { controller.drawerOpen = false }) {
                        Icon(Icons.Rounded.Close, contentDescription = "关闭")
                    }
                }
                Spacer(Modifier.height(12.dp))
                DesktopRoute.entries.forEach { route ->
                    DrawerItem(
                        route = route,
                        selected = controller.route == route,
                        onClick = { controller.openRoute(route) }
                    )
                }
                Spacer(Modifier.weight(1f))
                Text(
                    text = "已移除首页和底栏导航，入口保持音乐库 + 底部播放器。",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DrawerItem(
    route: DesktopRoute,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                route.icon,
                null,
                tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                route.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun FloatingLyricsWindow(controller: DesktopPlayerController) {
    val lyricLines = floatingLyricsLines(
        lyrics = controller.currentLyrics,
        positionMs = controller.positionMs,
        fallback = controller.currentSong?.title ?: "PPlayer",
        twoLine = controller.floatingLyricsTwoLine
    )
    val palette = remember(controller.currentSong?.id, controller.currentSong?.artwork?.cacheKey, controller.isDarkTheme) {
        paletteForSong(controller.currentSong, controller.isDarkTheme)
    }
    val textColor = when (controller.floatingLyricsColorMode) {
        FloatingLyricsColorMode.ThemePrimary -> palette.primary
        FloatingLyricsColorMode.ThemeSecondary -> palette.secondary
        FloatingLyricsColorMode.ThemeTertiary -> palette.tertiary
        FloatingLyricsColorMode.AlbumReadable -> readableLyricColor(controller.currentSong, controller.isDarkTheme, palette)
    }
    val alignment = when (controller.floatingLyricsAlignment) {
        FloatingLyricsAlignment.Start -> Alignment.CenterStart
        FloatingLyricsAlignment.Center -> Alignment.Center
        FloatingLyricsAlignment.End -> Alignment.CenterEnd
    }
    val textAlign = when (controller.floatingLyricsAlignment) {
        FloatingLyricsAlignment.Start -> TextAlign.Start
        FloatingLyricsAlignment.Center -> TextAlign.Center
        FloatingLyricsAlignment.End -> TextAlign.End
    }
    val capsuleShape = when (controller.floatingLyricsAlignment) {
        FloatingLyricsAlignment.Start -> RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 28.dp, bottomEnd = 28.dp)
        FloatingLyricsAlignment.Center -> RoundedCornerShape(28.dp)
        FloatingLyricsAlignment.End -> RoundedCornerShape(topStart = 28.dp, bottomStart = 28.dp, topEnd = 0.dp, bottomEnd = 0.dp)
    }
    val windowWidth = when (controller.floatingLyricsAlignment) {
        FloatingLyricsAlignment.Center -> 820.dp
        else -> 720.dp
    }
    val toolbarVisible = !controller.floatingLyricsLocked
    val clickThroughEnabled = controller.floatingLyricsLocked && !controller.floatingLyricsBackgroundEnabled
    val windowHeight = when {
        !toolbarVisible && controller.floatingLyricsTwoLine -> 116.dp
        !toolbarVisible -> 78.dp
        controller.floatingLyricsTwoLine -> 168.dp
        else -> 122.dp
    }

    DialogWindow(
        title = "PPlayer Lyrics",
        state = rememberDialogState(size = DpSize(windowWidth, windowHeight)),
        icon = painterResource("pplayer_icon.png"),
        undecorated = true,
        transparent = true,
        alwaysOnTop = true,
        resizable = false,
        onCloseRequest = { controller.floatingLyricsEnabled = false }
    ) {
        PPlayerDesktopTheme(darkTheme = controller.isDarkTheme) {
            LaunchedEffect(clickThroughEnabled) {
                delay(120)
                setWindowsToolWindow(window, true)
                setWindowsClickThrough(window, clickThroughEnabled)
            }
            DisposableEffect(Unit) {
                onDispose {
                    setWindowsClickThrough(window, false)
                    setWindowsToolWindow(window, false)
                }
            }
            val content: @Composable () -> Unit = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    contentAlignment = alignment
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = when (controller.floatingLyricsAlignment) {
                            FloatingLyricsAlignment.Start -> Alignment.Start
                            FloatingLyricsAlignment.Center -> Alignment.CenterHorizontally
                            FloatingLyricsAlignment.End -> Alignment.End
                        }
                    ) {
                        if (toolbarVisible) {
                            FloatingLyricsToolbar(controller, palette)
                            Spacer(Modifier.height(6.dp))
                        }
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = if (controller.floatingLyricsBackgroundEnabled) {
                                palette.container.copy(alpha = 0.82f)
                            } else {
                                Color.Transparent
                            },
                            shape = capsuleShape,
                            shadowElevation = if (controller.floatingLyricsBackgroundEnabled) 10.dp else 0.dp
                        ) {
                            Column(
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = if (controller.floatingLyricsTwoLine) 15.dp else 14.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                horizontalAlignment = when (controller.floatingLyricsAlignment) {
                                    FloatingLyricsAlignment.Start -> Alignment.Start
                                    FloatingLyricsAlignment.Center -> Alignment.CenterHorizontally
                                    FloatingLyricsAlignment.End -> Alignment.End
                                }
                            ) {
                                lyricLines.forEachIndexed { index, line ->
                                    Text(
                                        text = line,
                                        modifier = Modifier.fillMaxWidth(),
                                        color = if (index == 0) textColor else textColor.copy(alpha = 0.66f),
                                        style = MaterialTheme.typography.titleLarge.copy(
                                            fontSize = (if (index == 0) 20.sp else 15.sp) * controller.floatingLyricsTextScale,
                                            fontWeight = if (index == 0) FontWeight.Bold else FontWeight.Medium,
                                            lineHeight = (if (index == 0) 27.sp else 21.sp) * controller.floatingLyricsTextScale
                                        ),
                                        textAlign = textAlign,
                                        softWrap = true
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (controller.floatingLyricsLocked) {
                content()
            } else {
                WindowDraggableArea { content() }
            }
        }
    }
}

private fun setWindowsClickThrough(window: AwtWindow, enabled: Boolean) {
    if (!System.getProperty("os.name").orEmpty().contains("windows", ignoreCase = true)) return
    runCatching {
        val handle = composeWindowHandle(window).takeIf { it != 0L } ?: return@runCatching
        val hwnd = WinDef.HWND(Pointer.createConstant(handle))
        val currentStyle = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE)
        val transparentStyle = WinUser.WS_EX_LAYERED or WinUser.WS_EX_TRANSPARENT
        val nextStyle = if (enabled) {
            currentStyle or transparentStyle
        } else {
            currentStyle and WinUser.WS_EX_TRANSPARENT.inv()
        }
        if (nextStyle != currentStyle) {
            User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, nextStyle)
            User32.INSTANCE.SetWindowPos(
                hwnd,
                WinDef.HWND(Pointer.createConstant(0)),
                0,
                0,
                0,
                0,
                WinUser.SWP_NOMOVE or
                    WinUser.SWP_NOSIZE or
                    WinUser.SWP_NOZORDER or
                    WinUser.SWP_NOACTIVATE or
                    WinUser.SWP_FRAMECHANGED
            )
        }
    }
}

private fun setWindowsToolWindow(window: AwtWindow, enabled: Boolean) {
    if (!System.getProperty("os.name").orEmpty().contains("windows", ignoreCase = true)) return
    runCatching {
        val handle = composeWindowHandle(window).takeIf { it != 0L } ?: return@runCatching
        val hwnd = WinDef.HWND(Pointer.createConstant(handle))
        val currentStyle = User32.INSTANCE.GetWindowLong(hwnd, WinUser.GWL_EXSTYLE)
        val nextStyle = if (enabled) {
            (currentStyle or WindowsExToolWindow) and WindowsExAppWindow.inv()
        } else {
            currentStyle and WindowsExToolWindow.inv()
        }
        if (nextStyle != currentStyle) {
            User32.INSTANCE.SetWindowLong(hwnd, WinUser.GWL_EXSTYLE, nextStyle)
            User32.INSTANCE.SetWindowPos(
                hwnd,
                WinDef.HWND(Pointer.createConstant(0)),
                0,
                0,
                0,
                0,
                WinUser.SWP_NOMOVE or
                    WinUser.SWP_NOSIZE or
                    WinUser.SWP_NOZORDER or
                    WinUser.SWP_NOACTIVATE or
                    WinUser.SWP_FRAMECHANGED
            )
        }
    }
}

private fun composeWindowHandle(window: AwtWindow): Long = when (window) {
    is ComposeWindow -> window.windowHandle
    is ComposeDialog -> window.windowHandle
    else -> 0L
}

@Composable
private fun FloatingLyricsToolbar(controller: DesktopPlayerController, palette: PlayerPalette) {
    Row(
        modifier = Modifier
            .height(30.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(
                if (controller.floatingLyricsBackgroundEnabled) {
                    palette.container.copy(alpha = 0.68f)
                } else {
                    Color.Black.copy(alpha = 0.20f)
                }
            )
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FloatingLyricsToolButton(
            icon = if (controller.floatingLyricsLocked) Icons.Rounded.Lock else Icons.Rounded.LockOpen,
            contentDescription = if (controller.floatingLyricsLocked) "解锁悬浮歌词" else "锁定悬浮歌词",
            tint = palette.onContainer,
            onClick = { controller.floatingLyricsLocked = !controller.floatingLyricsLocked }
        )
        FloatingLyricsToolButton(
            icon = Icons.Rounded.Palette,
            contentDescription = "切换背景",
            tint = palette.onContainer,
            onClick = { controller.floatingLyricsBackgroundEnabled = !controller.floatingLyricsBackgroundEnabled }
        )
        FloatingLyricsToolButton(
            icon = Icons.Rounded.Close,
            contentDescription = "关闭悬浮歌词",
            tint = palette.onContainer,
            onClick = { controller.floatingLyricsEnabled = false }
        )
    }
}

@Composable
private fun FloatingLyricsToolButton(
    icon: ImageVector,
    contentDescription: String,
    tint: Color,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(24.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint.copy(alpha = 0.86f),
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun AlbumArtTile(
    song: DesktopSong?,
    size: Dp,
    shape: Shape,
    modifier: Modifier = Modifier,
    fallbackIcon: ImageVector = Icons.Rounded.MusicNote
) {
    val preferOriginalArtwork = size >= 180.dp
    val artworkBitmap = remember(song?.artwork?.cacheKey, song?.artwork?.previewCacheKey, preferOriginalArtwork) {
        song?.artwork?.toImageBitmap(preferOriginal = preferOriginalArtwork)
    }
    val colors = remember(song?.id, song?.artwork?.cacheKey) { albumTileColors(song) }
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center
    ) {
        if (artworkBitmap != null) {
            Image(
                bitmap = artworkBitmap,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Canvas(Modifier.fillMaxSize()) {
                val strokeColor = Color.White.copy(alpha = 0.20f)
                drawCircle(
                    color = strokeColor,
                    radius = this.size.minDimension * 0.34f,
                    center = Offset(this.size.width * 0.70f, this.size.height * 0.30f)
                )
                drawCircle(
                    color = Color.Black.copy(alpha = 0.13f),
                    radius = this.size.minDimension * 0.20f,
                    center = Offset(this.size.width * 0.30f, this.size.height * 0.72f)
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.12f),
                    start = Offset(this.size.width * 0.15f, this.size.height * 0.18f),
                    end = Offset(this.size.width * 0.82f, this.size.height * 0.86f),
                    strokeWidth = this.size.minDimension * 0.035f,
                    cap = StrokeCap.Round
                )
            }
            Icon(
                fallbackIcon,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.92f),
                modifier = Modifier.size(size * 0.42f)
            )
        }
    }
}

private fun DesktopArtwork.toImageBitmap(preferOriginal: Boolean): ImageBitmap? {
    val bytes = if (preferOriginal) data else previewData
    val key = if (preferOriginal) cacheKey else previewCacheKey
    return ArtworkBitmapCache.getOrDecode(key, bytes)
}

@Composable
private fun ProgressFill(progress: Float, color: Color, modifier: Modifier = Modifier) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .background(color)
        )
    }
}

@Composable
private fun PlayingEqIcon(
    modifier: Modifier = Modifier,
    color: Color,
    isPlaying: Boolean
) {
    val infinite = rememberInfiniteTransition(label = "playing_eq")
    val phases = List(3) { index ->
        infinite.animateFloat(
            initialValue = 0.30f + index * 0.12f,
            targetValue = 1f - index * 0.10f,
            animationSpec = infiniteRepeatable(
                animation = tween(420 + index * 90, easing = FastOutSlowInEasing),
                repeatMode = AnimationRepeatMode.Reverse
            ),
            label = "eq_bar_$index"
        )
    }
    Canvas(modifier) {
        val barWidth = size.width / 5f
        val gap = barWidth
        phases.forEachIndexed { index, animated ->
            val level = if (isPlaying) animated.value else 0.34f + index * 0.10f
            val left = index * (barWidth + gap)
            val barHeight = size.height * level.coerceIn(0.18f, 1f)
            drawRoundRect(
                color = color,
                topLeft = Offset(left, size.height - barHeight),
                size = androidx.compose.ui.geometry.Size(barWidth, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(barWidth / 2, barWidth / 2)
            )
        }
    }
}

private object JavaFxAudioRuntime {
    private val started = AtomicBoolean(false)

    fun runOnFx(block: () -> Unit) {
        ensureStarted()
        Platform.runLater(block)
    }

    private fun ensureStarted() {
        if (started.compareAndSet(false, true)) {
            val latch = CountDownLatch(1)
            Platform.startup { latch.countDown() }
            latch.await(5, TimeUnit.SECONDS)
        }
    }
}

private class MpvAudioPlayer(
    private val executable: Path,
    private val song: DesktopSong,
    private val onFinished: () -> Unit,
    private val onError: (String) -> Unit
) {
    private val stopRequested = AtomicBoolean(false)
    private val outputLines = java.util.ArrayDeque<String>()
    private val ipcPath = createMpvIpcPath(song)
    private var process: Process? = null
    private var positionBaseMs = 0L
    private var positionStartedAtMs = 0L
    private var playing = false

    val isPlaying: Boolean
        get() = playing && process?.isAlive == true

    fun start(volume: Float): Boolean {
        return runCatching {
            val nextProcess = ProcessBuilder(
                executable.toString(),
                "--no-config",
                "--no-video",
                "--force-window=no",
                "--input-ipc-server=$ipcPath",
                "--terminal=no",
                "--msg-level=all=warn",
                "--volume=${(volume.coerceIn(0f, 1f) * 100f).roundToInt()}",
                song.path.toString()
            )
                .redirectErrorStream(true)
                .directory(executable.parent?.toFile())
                .start()

            process = nextProcess
            positionBaseMs = 0L
            positionStartedAtMs = System.currentTimeMillis()
            playing = true

            Thread {
                runCatching {
                    nextProcess.inputStream.bufferedReader(Charsets.UTF_8).useLines { lines ->
                        lines.forEach { rememberOutputLine(it) }
                    }
                }
            }.apply {
                isDaemon = true
                name = "pplayer-mpv-output"
                start()
            }

            Thread {
                val exitCode = runCatching { nextProcess.waitFor() }.getOrDefault(-1)
                if (!stopRequested.get()) {
                    playing = false
                    if (exitCode == 0) {
                        onFinished()
                    } else {
                        onError("退出码 $exitCode${lastOutputSuffix()}")
                    }
                }
            }.apply {
                isDaemon = true
                name = "pplayer-mpv-waiter"
                start()
            }
            true
        }.getOrElse { error ->
            onError(error.message ?: song.fileName)
            false
        }
    }

    fun pause() {
        if (!isPlaying) return
        positionBaseMs = currentPositionMs()
        playing = false
        sendCommand(buildMpvJsonCommand("set_property", "pause", true))
    }

    fun resume() {
        if (process?.isAlive != true) return
        positionStartedAtMs = System.currentTimeMillis()
        playing = true
        sendCommand(buildMpvJsonCommand("set_property", "pause", false))
    }

    fun seekTo(positionMs: Long) {
        val clamped = positionMs.coerceAtLeast(0L)
        positionBaseMs = clamped
        positionStartedAtMs = System.currentTimeMillis()
        sendCommand(buildMpvJsonCommand("seek", clamped / 1000.0, "absolute+exact"))
    }

    fun setVolume(volume: Float) {
        sendCommand(buildMpvJsonCommand("set_property", "volume", (volume.coerceIn(0f, 1f) * 100f).roundToInt()))
    }

    fun currentPositionMs(): Long {
        if (!playing) return positionBaseMs
        return positionBaseMs + (System.currentTimeMillis() - positionStartedAtMs).coerceAtLeast(0L)
    }

    fun stop() {
        stopRequested.set(true)
        playing = false
        sendCommand(buildMpvJsonCommand("quit"))
        val currentProcess = process
        if (currentProcess?.isAlive == true) {
            runCatching { currentProcess.waitFor(700, TimeUnit.MILLISECONDS) }
        }
        if (currentProcess?.isAlive == true) {
            currentProcess.destroy()
        }
        process = null
    }

    private fun sendCommand(command: String) {
        if (process?.isAlive != true) return
        repeat(8) { attempt ->
            val sent = runCatching {
                val handle = Kernel32.INSTANCE.CreateFile(
                    ipcPath,
                    WinNT.GENERIC_WRITE,
                    WinNT.FILE_SHARE_READ or WinNT.FILE_SHARE_WRITE,
                    null,
                    WinNT.OPEN_EXISTING,
                    WinNT.FILE_ATTRIBUTE_NORMAL,
                    null
                )
                if (handle == WinBase.INVALID_HANDLE_VALUE) {
                    return@runCatching false
                }
                try {
                    val bytes = command.toByteArray(Charsets.UTF_8)
                    val written = IntByReference()
                    val ok = Kernel32.INSTANCE.WriteFile(handle, bytes, bytes.size, written, null) && written.value == bytes.size
                    if (ok) Kernel32.INSTANCE.FlushFileBuffers(handle)
                    ok
                } finally {
                    Kernel32.INSTANCE.CloseHandle(handle)
                }
            }.getOrDefault(false)
            if (sent) return
            if (attempt < 7) Thread.sleep(40)
        }
    }

    private fun buildMpvJsonCommand(name: String, vararg args: Any): String {
        return buildString {
            append("{\"command\":[")
            append(name.jsonString())
            args.forEachIndexed { index, arg ->
                append(',')
                append(arg.jsonValue())
            }
            append("]}")
            append('\n')
        }
    }

    private fun rememberOutputLine(line: String) {
        synchronized(outputLines) {
            outputLines.addLast(line)
            while (outputLines.size > 8) outputLines.removeFirst()
        }
    }

    private fun lastOutputSuffix(): String {
        val output = synchronized(outputLines) { outputLines.joinToString(" | ") }
        return if (output.isBlank()) "" else "：$output"
    }
}

private fun Any.jsonValue(): String = when (this) {
    is String -> jsonString()
    is Boolean -> if (this) "true" else "false"
    is Int, is Long, is Short, is Byte, is Float, is Double -> toString()
    else -> toString().jsonString()
}

private fun String.jsonString(): String = buildString {
    append('"')
    for (ch in this@jsonString) {
        when (ch) {
            '\\' -> append("\\\\")
            '"' -> append("\\\"")
            '\b' -> append("\\b")
            '\u000C' -> append("\\f")
            '\n' -> append("\\n")
            '\r' -> append("\\r")
            '\t' -> append("\\t")
            else -> append(ch)
        }
    }
    append('"')
}

private fun findMpvExecutable(): Path? {
    return mpvCandidatePaths().firstOrNull { Files.isRegularFile(it) }
}

private fun createMpvIpcPath(song: DesktopSong): String {
    val token = "${System.currentTimeMillis()}-${abs(song.id.hashCode())}"
    return if (System.getProperty("os.name").orEmpty().contains("windows", ignoreCase = true)) {
        "\\\\.\\pipe\\pplayer-mpv-$token"
    } else {
        Paths.get("").toAbsolutePath().normalize().resolve("build").resolve("pplayer-mpv-$token.sock").toString()
    }
}

private fun mpvCandidatePaths(): List<Path> {
    val fileName = if (System.getProperty("os.name").orEmpty().contains("windows", ignoreCase = true)) "mpv.exe" else "mpv"
    val appRoot = Paths.get("").toAbsolutePath().normalize()
    fun Path.withParents(limit: Int = 8): List<Path> = generateSequence(this) { it.parent }.take(limit).toList()
    val runtimeRoot = runCatching {
        Paths.get(DesktopPlayerController::class.java.protectionDomain.codeSource.location.toURI())
            .toAbsolutePath()
            .normalize()
    }.getOrNull()
    val searchRoots = buildList {
        addAll(appRoot.withParents())
        runtimeRoot?.let { addAll(it.withParents()) }
    }.distinct()
    val candidates = buildList {
        System.getenv("PPLAYER_MPV_PATH")
            ?.takeIf { it.isNotBlank() }
            ?.let { configuredPath ->
                val path = Paths.get(configuredPath)
                add(if (Files.isDirectory(path)) path.resolve(fileName) else path)
            }
        searchRoots.forEach { root ->
            add(root.resolve("tools").resolve("mpv").resolve(fileName))
            add(root.resolve("mpv").resolve(fileName))
            add(root.resolve("tools").resolve(fileName))
        }
        System.getenv("PATH").orEmpty()
            .split(java.io.File.pathSeparator)
            .filter { it.isNotBlank() }
            .forEach { add(Paths.get(it).resolve(fileName)) }
        add(Paths.get("C:", "Program Files", "mpv", fileName))
        add(Paths.get("C:", "Program Files (x86)", "mpv", fileName))
        add(Paths.get("D:", "Program Files", "mpv", fileName))
    }
    return candidates.distinct()
}

private fun logMpvLookup(extension: String, executable: Path?) {
    if (extension in javaFxPlayableExtensions) return
    val preview = mpvCandidatePaths()
        .take(8)
        .joinToString(" | ") { "${it.toAbsolutePath().normalize()}=${Files.isRegularFile(it)}" }
    System.err.println(
        "PPlayer mpv lookup: extension=$extension, found=${executable?.toAbsolutePath()?.normalize()}, " +
            "cwd=${Paths.get("").toAbsolutePath().normalize()}, env=${System.getenv("PPLAYER_MPV_PATH").orEmpty()}, candidates=$preview"
    )
}

private fun updateOnUi(block: () -> Unit) {
    SwingUtilities.invokeLater(block)
}

private fun scanMusicFiles(root: Path): List<DesktopSong> {
    if (!Files.exists(root)) return emptyList()
    return Files.walk(root).use { stream ->
        val result = mutableListOf<DesktopSong>()
        stream
            .filter { Files.isRegularFile(it) }
            .filter { it.extension.lowercase(Locale.getDefault()) in musicExtensions }
            .forEach { path -> result += path.toDesktopSong() }
        sortedSongs(result, SortMode.TitleAsc)
    }
}

private fun Path.toDesktopSong(): DesktopSong {
    val absolutePath = toAbsolutePath().normalize()
    val fallbackParts = nameWithoutExtension.split(" - ", limit = 2)
    val fallbackArtist = fallbackParts.getOrNull(0)?.takeIf { fallbackParts.size == 2 }.orEmpty()
    val fallbackTitle = fallbackParts.getOrNull(if (fallbackParts.size == 2) 1 else 0)
        ?.takeIf { it.isNotBlank() }
        ?: nameWithoutExtension
    val size = runCatching { Files.size(this) }.getOrDefault(0L)
    val modified = runCatching { Files.getLastModifiedTime(this).toMillis() }.getOrDefault(0L)

    val audioFile = runCatching { AudioFileIO.read(toFile()) }.getOrNull()
    val tag = audioFile?.tag
    val title = tag?.getFirst(FieldKey.TITLE).orNullIfBlank() ?: fallbackTitle
    val artist = tag?.getFirst(FieldKey.ARTIST).orNullIfBlank() ?: fallbackArtist
    val album = tag?.getFirst(FieldKey.ALBUM).orNullIfBlank() ?: parent?.fileName?.toString().orEmpty()
    val albumArtist = tag?.getFirst(FieldKey.ALBUM_ARTIST).orNullIfBlank() ?: artist
    val duration = audioFile?.audioHeader?.trackLength?.takeIf { it > 0 }?.times(1000L) ?: 0L
    val artwork = extractEmbeddedArtwork(tag)
    val embeddedLyricsRaw = extractEmbeddedLyrics(tag)

    return DesktopSong(
        id = absolutePath.toString(),
        title = title,
        artist = artist,
        album = album,
        albumArtist = albumArtist,
        path = absolutePath,
        sizeBytes = size,
        modifiedAtMs = modified,
        durationMs = duration,
        artwork = artwork,
        embeddedLyricsRaw = embeddedLyricsRaw
    )
}

private fun extractEmbeddedArtwork(tag: Tag?): DesktopArtwork? {
    val artwork = runCatching { tag?.firstArtwork }.getOrNull() ?: return null
    val data = runCatching { artwork.binaryData }.getOrNull()
        ?.takeIf { it.isNotEmpty() && it.size <= MaxEmbeddedArtworkBytes }
        ?: return null
    val copiedData = data.copyOf()
    return DesktopArtwork(
        data = copiedData,
        mimeType = runCatching { artwork.mimeType }.getOrNull(),
        previewData = createArtworkPreviewData(copiedData) ?: copiedData,
        paletteColors = extractArtworkPaletteColors(copiedData)
    )
}

private fun extractEmbeddedLyrics(tag: Tag?): String? {
    tag ?: return null
    val candidates = mutableListOf<String>()

    runCatching { tag.getAll(FieldKey.LYRICS) }
        .getOrDefault(emptyList())
        .forEach { candidates += it }
    runCatching { tag.getFirst(FieldKey.LYRICS) }
        .getOrNull()
        ?.let { candidates += it }

    embeddedLyricsFieldIds.forEach { fieldId ->
        runCatching { tag.getFirst(fieldId) }
            .getOrNull()
            ?.let { candidates += it }
    }

    runCatching { tag.getFields() }
        .getOrNull()
        ?.let { fields ->
            while (fields.hasNext()) {
                val field = fields.next()
                if (isLyricsFieldId(field.id)) {
                    candidates += if (field is TagTextField) field.content else field.toString()
                }
            }
        }

    return candidates.asSequence()
        .mapNotNull(::normalizeEmbeddedLyrics)
        .firstOrNull()
}

private fun isLyricsFieldId(id: String?): Boolean {
    val normalized = id?.uppercase(Locale.getDefault()) ?: return false
    return embeddedLyricsFieldIds.any { normalized == it || normalized.contains(it) } ||
        normalized.contains("LYRIC")
}

private fun normalizeEmbeddedLyrics(raw: String): String? {
    val lines = raw
        .replace("\uFEFF", "")
        .replace('\u0000', '\n')
        .replace("\r\n", "\n")
        .replace('\r', '\n')
        .lineSequence()
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .filterNot { line ->
            val lower = line.lowercase(Locale.getDefault())
            lower == "lyrics" ||
                lower == "unsynchronised lyrics" ||
                lower == "synchronised lyrics" ||
                lower.startsWith("language:") ||
                lower.startsWith("description:")
        }
        .toList()

    val cleaned = lines.joinToString("\n").trim()
    if (cleaned.isBlank()) return null
    val hasLyricsShape = desktopLyricTimestampRegex.containsMatchIn(cleaned) || lines.size > 1 || cleaned.length > 8
    return cleaned.takeIf { hasLyricsShape }
}

private fun sortedSongs(source: List<DesktopSong>, mode: SortMode): List<DesktopSong> {
    val locale = Locale.getDefault()
    return when (mode) {
        SortMode.TitleAsc -> source.sortedWith(compareBy<DesktopSong> { it.title.lowercase(locale) }.thenBy { it.displayArtist.lowercase(locale) })
        SortMode.ArtistAsc -> source.sortedWith(compareBy<DesktopSong> { it.displayArtist.lowercase(locale) }.thenBy { it.title.lowercase(locale) })
        SortMode.AlbumAsc -> source.sortedWith(compareBy<DesktopSong> { it.displayAlbum.lowercase(locale) }.thenBy { it.title.lowercase(locale) })
        SortMode.Newest -> source.sortedByDescending { it.modifiedAtMs }
        SortMode.FileNameAsc -> source.sortedBy { it.fileName.lowercase(locale) }
    }
}

private fun loadLyricsForSong(song: DesktopSong): DesktopLyrics? {
    song.embeddedLyricsRaw
        ?.let(::parseDesktopLyrics)
        ?.takeIf { it.hasContent() }
        ?.let { return it }

    val baseName = song.path.nameWithoutExtension
    val candidates = listOf(
        song.path.resolveSibling("$baseName.lrc"),
        song.path.resolveSibling("$baseName.txt")
    )
    val file = candidates.firstOrNull { Files.exists(it) } ?: return null
    val raw = runCatching { Files.readString(file) }.getOrNull() ?: return null
    return parseDesktopLyrics(raw)
}

private fun parseDesktopLyrics(raw: String): DesktopLyrics {
    val synced = mutableListOf<DesktopLyricLine>()
    val plain = mutableListOf<String>()

    raw.lineSequence()
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .forEach { line ->
            val matches = desktopLyricTimestampRegex.findAll(line).toList()
            if (matches.isEmpty()) {
                if (!line.startsWith("[") || !line.endsWith("]")) plain += line
            } else {
                val text = desktopLyricTimestampRegex.replace(line, "").trim()
                if (text.isNotBlank()) {
                    matches.forEach { match ->
                        val minutes = match.groupValues[1].toLongOrNull() ?: 0L
                        val seconds = match.groupValues[2].toLongOrNull() ?: 0L
                        val fraction = match.groupValues.getOrNull(3).orEmpty()
                        val millis = when (fraction.length) {
                            0 -> 0L
                            1 -> fraction.toLong() * 100L
                            2 -> fraction.toLong() * 10L
                            else -> fraction.take(3).toLong()
                        }
                        synced += DesktopLyricLine((minutes * 60_000L) + (seconds * 1000L) + millis, text)
                    }
                }
            }
        }

    return if (synced.isNotEmpty()) {
        DesktopLyrics(synced = synced.sortedBy { it.timeMs }, plain = synced.map { it.text })
    } else {
        DesktopLyrics(plain = plain)
    }
}

private fun DesktopLyrics.hasContent(): Boolean =
    synced.any { it.text.isNotBlank() } || plain.any { it.isNotBlank() }

private fun currentLyricText(lyrics: DesktopLyrics?, positionMs: Long): String? {
    lyrics ?: return null
    if (lyrics.synced.isNotEmpty()) {
        return lyrics.synced.lastOrNull { positionMs >= it.timeMs }?.text
            ?: lyrics.synced.firstOrNull()?.text
    }
    return lyrics.plain.firstOrNull()
}

private fun floatingLyricsLines(
    lyrics: DesktopLyrics?,
    positionMs: Long,
    fallback: String,
    twoLine: Boolean
): List<String> {
    lyrics ?: return listOf(fallback)
    if (lyrics.synced.isNotEmpty()) {
        val index = currentLyricIndex(lyrics, positionMs).coerceAtLeast(0)
        val current = lyrics.synced.getOrNull(index)?.text
        val next = lyrics.synced.drop(index + 1).firstOrNull { it.text.isNotBlank() }?.text
        return listOfNotNull(current, if (twoLine) next else null)
            .ifEmpty { listOf(fallback) }
    }
    return if (twoLine) {
        lyrics.plain.take(2).ifEmpty { listOf(fallback) }
    } else {
        listOf(lyrics.plain.firstOrNull().orNullIfBlank() ?: fallback)
    }
}

private fun currentLyricIndex(lyrics: DesktopLyrics, positionMs: Long): Int {
    if (lyrics.synced.isEmpty()) return -1
    val index = lyrics.synced.indexOfLast { positionMs >= it.timeMs }
    return if (index >= 0) index else 0
}

private suspend fun chooseMusicFolder(initial: Path?): Path? = withContext(Dispatchers.IO) {
    val latch = CountDownLatch(1)
    var selected: Path? = null
    SwingUtilities.invokeLater {
        val chooser = JFileChooser(initial?.toFile()).apply {
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            dialogTitle = "选择音乐文件夹"
            approveButtonText = "选择"
        }
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            selected = chooser.selectedFile?.toPath()
        }
        latch.countDown()
    }
    latch.await()
    selected
}

private fun defaultMusicPath(): Path? {
    val userHome = System.getProperty("user.home") ?: return null
    return listOf(
        Paths.get(userHome, "Music"),
        Paths.get(userHome, "音乐"),
        Paths.get("D:", "music"),
        Paths.get("D:", "Music")
    ).firstOrNull { Files.exists(it) }
}

private fun cachedLibraryRoot(): Path? {
    val rootFile = libraryRootCacheFile()
    if (!Files.isRegularFile(rootFile)) return null
    return runCatching {
        Paths.get(Files.readString(rootFile).trim())
            .toAbsolutePath()
            .normalize()
            .takeIf { Files.exists(it) }
    }.getOrNull()
}

private fun readMusicCache(root: Path): List<DesktopSong> {
    val cacheFile = libraryCacheFile()
    if (!Files.isRegularFile(cacheFile)) return emptyList()

    return runCatching {
        val lines = Files.readAllLines(cacheFile, Charsets.UTF_8)
        if (lines.size < 2 || lines.firstOrNull() != LibraryCacheMagic) return@runCatching emptyList()

        val expectedRoot = root.toAbsolutePath().normalize()
        val cachedRoot = lines.getOrNull(1)
            ?.removePrefix("root=")
            ?.takeIf { it != lines.getOrNull(1) }
            ?.let(::decodeCacheText)
            ?.let { Paths.get(it).toAbsolutePath().normalize() }
            ?: return@runCatching emptyList()
        if (cachedRoot != expectedRoot) return@runCatching emptyList()

        sortedSongs(
            lines.drop(2).mapNotNull { cachedSongFromRow(it, expectedRoot) },
            SortMode.TitleAsc
        )
    }.getOrDefault(emptyList())
}

private fun saveMusicCache(root: Path, songs: List<DesktopSong>) {
    runCatching {
        val dataDir = portableDataDir()
        Files.createDirectories(dataDir)
        val normalizedRoot = root.toAbsolutePath().normalize()
        Files.writeString(libraryRootCacheFile(), normalizedRoot.toString(), Charsets.UTF_8)

        val cacheFile = libraryCacheFile()
        val tempFile = cacheFile.resolveSibling("${cacheFile.fileName}.tmp")
        Files.newBufferedWriter(tempFile, Charsets.UTF_8).use { writer ->
            writer.appendLine(LibraryCacheMagic)
            writer.appendLine("root=${encodeCacheText(normalizedRoot.toString())}")
            songs.forEach { song -> writer.appendLine(song.toCacheRow()) }
        }
        runCatching {
            Files.move(tempFile, cacheFile, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
        }.getOrElse {
            Files.move(tempFile, cacheFile, StandardCopyOption.REPLACE_EXISTING)
        }
    }
}

private fun cachedSongFromRow(row: String, expectedRoot: Path): DesktopSong? {
    if (row.isBlank()) return null
    val columns = row.split('\t')
    if (columns.size < 12) return null

    return runCatching {
        val path = Paths.get(decodeCacheText(columns[0])).toAbsolutePath().normalize()
        if (!path.startsWith(expectedRoot) || !Files.isRegularFile(path)) return@runCatching null

        val size = columns[5].toLongOrNull() ?: return@runCatching null
        val modified = columns[6].toLongOrNull() ?: return@runCatching null
        if (Files.size(path) != size) return@runCatching null
        if (Files.getLastModifiedTime(path).toMillis() != modified) return@runCatching null

        val artworkData = decodeCacheBytes(columns[9])
        val artwork = artworkData?.let { data ->
            DesktopArtwork(
                data = data,
                mimeType = decodeNullableCacheText(columns[8]),
                previewData = data,
                paletteColors = decodeCacheColors(columns[10])
            )
        }

        DesktopSong(
            id = path.toString(),
            title = decodeCacheText(columns[1]),
            artist = decodeCacheText(columns[2]),
            album = decodeCacheText(columns[3]),
            albumArtist = decodeCacheText(columns[4]),
            path = path,
            sizeBytes = size,
            modifiedAtMs = modified,
            durationMs = columns[7].toLongOrNull() ?: 0L,
            artwork = artwork,
            embeddedLyricsRaw = decodeNullableCacheText(columns[11])
        )
    }.getOrNull()
}

private fun DesktopSong.toCacheRow(): String = listOf(
    encodeCacheText(path.toString()),
    encodeCacheText(title),
    encodeCacheText(artist),
    encodeCacheText(album),
    encodeCacheText(albumArtist),
    sizeBytes.toString(),
    modifiedAtMs.toString(),
    durationMs.toString(),
    encodeNullableCacheText(artwork?.mimeType),
    encodeCacheBytes(artwork?.previewData),
    encodeCacheColors(artwork?.paletteColors),
    encodeNullableCacheText(embeddedLyricsRaw)
).joinToString("\t")

private fun encodeCacheText(value: String): String =
    cacheBase64Encoder.encodeToString(value.toByteArray(Charsets.UTF_8))

private fun decodeCacheText(value: String): String =
    String(cacheBase64Decoder.decode(value), Charsets.UTF_8)

private fun encodeNullableCacheText(value: String?): String =
    value?.let(::encodeCacheText) ?: EmptyCacheValue

private fun decodeNullableCacheText(value: String): String? =
    value.takeIf { it != EmptyCacheValue }?.let(::decodeCacheText)

private fun encodeCacheBytes(value: ByteArray?): String =
    value?.takeIf { it.isNotEmpty() }?.let(cacheBase64Encoder::encodeToString) ?: EmptyCacheValue

private fun decodeCacheBytes(value: String): ByteArray? =
    value.takeIf { it != EmptyCacheValue }?.let { cacheBase64Decoder.decode(it) }

private fun encodeCacheColors(colors: List<Color>?): String =
    colors
        ?.takeIf { it.isNotEmpty() }
        ?.joinToString(",") { color -> "%08X".format(color.toArgb()) }
        ?: EmptyCacheValue

private fun decodeCacheColors(value: String): List<Color>? =
    value
        .takeIf { it != EmptyCacheValue }
        ?.split(',')
        ?.mapNotNull { part ->
            runCatching { Color(part.toLong(16).toInt()) }.getOrNull()
        }
        ?.takeIf { it.isNotEmpty() }

private fun libraryRootCacheFile(): Path = portableDataDir().resolve("library-root.txt")

private fun libraryCacheFile(): Path = portableDataDir().resolve("library-cache.tsv")

private fun portableDataDir(): Path {
    System.getenv("PPLAYER_DATA_DIR")
        ?.takeIf { it.isNotBlank() }
        ?.let { return Paths.get(it).toAbsolutePath().normalize() }

    val appRoot = appSearchRoots().firstOrNull { root ->
        Files.isDirectory(root.resolve("tools")) ||
            Files.isDirectory(root.resolve("runtime")) ||
            Files.isRegularFile(root.resolve("PPlayer.exe")) ||
            Files.isRegularFile(root.resolve("PPlayer Desktop.exe"))
    } ?: Paths.get("").toAbsolutePath().normalize()
    return appRoot.resolve("data")
}

private fun appSearchRoots(): List<Path> {
    fun Path.parents(limit: Int = 8): List<Path> = generateSequence(this) { it.parent }.take(limit).toList()
    val currentRoot = Paths.get("").toAbsolutePath().normalize()
    val runtimeLocation = runCatching {
        Paths.get(DesktopPlayerController::class.java.protectionDomain.codeSource.location.toURI())
            .toAbsolutePath()
            .normalize()
            .let { if (Files.isRegularFile(it)) it.parent else it }
    }.getOrNull()

    return buildList {
        addAll(currentRoot.parents())
        runtimeLocation?.let { addAll(it.parents()) }
    }.distinct()
}

private fun paletteForSong(song: DesktopSong?, darkTheme: Boolean): PlayerPalette {
    val colors = albumTileColors(song)
    val primary = colors[0]
    val secondary = colors[1]
    val tertiary = colors[2]
    val container = if (darkTheme) {
        blend(Color(0xFF20152E), primary, 0.26f)
    } else {
        blend(Color(0xFFFFFBFF), primary, 0.16f)
    }
    val onContainer = if (darkTheme) Color(0xFFFFF7FF) else Color(0xFF211A24)
    return PlayerPalette(primary, secondary, tertiary, container, onContainer)
}

private fun albumTileColors(song: DesktopSong?): List<Color> {
    song?.artwork?.paletteColors?.takeIf { it.size >= 3 }?.let { return it }

    val fallbackPalettes = listOf(
        listOf(Color(0xFFAB47BC), Color(0xFFF06292), Color(0xFFFF8A65)),
        listOf(Color(0xFF6C4FF5), Color(0xFF00A6A6), Color(0xFFFFB74D)),
        listOf(Color(0xFF5C7CFA), Color(0xFF4DB6AC), Color(0xFFE57373)),
        listOf(Color(0xFF8E6AD8), Color(0xFFE05B93), Color(0xFF66BB6A)),
        listOf(Color(0xFF547AA5), Color(0xFFB05C7B), Color(0xFFFFB35C))
    )
    val index = Math.floorMod(song?.id?.hashCode() ?: 0, fallbackPalettes.size)
    return fallbackPalettes[index]
}

private fun extractArtworkPaletteColors(data: ByteArray): List<Color>? {
    val image = runCatching { ImageIO.read(ByteArrayInputStream(data)) }.getOrNull() ?: return null
    if (image.width <= 0 || image.height <= 0) return null

    val stepX = maxOf(1, image.width / 56)
    val stepY = maxOf(1, image.height / 56)
    var weightedRed = 0.0
    var weightedGreen = 0.0
    var weightedBlue = 0.0
    var totalWeight = 0.0

    var y = 0
    while (y < image.height) {
        var x = 0
        while (x < image.width) {
            val argb = image.getRGB(x, y)
            val alpha = (argb ushr 24) and 0xFF
            if (alpha >= 80) {
                val red = (argb ushr 16) and 0xFF
                val green = (argb ushr 8) and 0xFF
                val blue = argb and 0xFF
                val max = maxOf(red, green, blue)
                val min = minOf(red, green, blue)
                val brightness = max / 255.0
                if (brightness in 0.08..0.96) {
                    val saturation = if (max == 0) 0.0 else (max - min).toDouble() / max
                    val readableBias = (1.0 - abs(brightness - 0.58) * 1.4).coerceIn(0.0, 0.35)
                    val weight = 0.35 + saturation * 0.55 + readableBias
                    weightedRed += red * weight
                    weightedGreen += green * weight
                    weightedBlue += blue * weight
                    totalWeight += weight
                }
            }
            x += stepX
        }
        y += stepY
    }

    if (totalWeight <= 0.0) return null
    val base = Color(
        red = (weightedRed / totalWeight / 255.0).toFloat().coerceIn(0f, 1f),
        green = (weightedGreen / totalWeight / 255.0).toFloat().coerceIn(0f, 1f),
        blue = (weightedBlue / totalWeight / 255.0).toFloat().coerceIn(0f, 1f)
    )
    return monetPaletteFrom(base)
}

private fun createArtworkPreviewData(data: ByteArray, maxDimension: Int = 320): ByteArray? {
    val image = runCatching { ImageIO.read(ByteArrayInputStream(data)) }.getOrNull() ?: return null
    if (image.width <= maxDimension && image.height <= maxDimension) return data

    val scale = minOf(
        maxDimension.toDouble() / image.width.toDouble(),
        maxDimension.toDouble() / image.height.toDouble()
    )
    val width = maxOf(1, (image.width * scale).roundToInt())
    val height = maxOf(1, (image.height * scale).roundToInt())
    val resized = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    val graphics = resized.createGraphics()
    try {
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        graphics.drawImage(image, 0, 0, width, height, null)
    } finally {
        graphics.dispose()
    }

    return runCatching {
        ByteArrayOutputStream().use { output ->
            ImageIO.write(resized, "png", output)
            output.toByteArray().takeIf { it.isNotEmpty() && it.size < data.size }
        }
    }.getOrNull()
}

private fun readableLyricColor(song: DesktopSong?, darkTheme: Boolean, palette: PlayerPalette): Color {
    val base = song?.artwork?.paletteColors?.firstOrNull() ?: palette.primary
    val hsl = base.toHslColor()
    val saturation = hsl.saturation.coerceIn(0.28f, 0.58f)
    return if (darkTheme) {
        colorFromHsl(hsl.hue, saturation * 0.72f, 0.84f)
    } else {
        colorFromHsl(hsl.hue, saturation, 0.28f)
    }
}

private data class HslColor(
    val hue: Float,
    val saturation: Float,
    val lightness: Float
)

private fun monetPaletteFrom(base: Color): List<Color> {
    val hsl = base.toHslColor()
    val saturation = hsl.saturation.coerceIn(0.30f, 0.66f)
    val lightness = hsl.lightness.coerceIn(0.40f, 0.62f)
    return listOf(
        colorFromHsl(hsl.hue, saturation, lightness),
        colorFromHsl(hsl.hue + 28f, (saturation * 0.84f).coerceIn(0.24f, 0.56f), (lightness + 0.06f).coerceIn(0.42f, 0.68f)),
        colorFromHsl(hsl.hue - 42f, (saturation * 0.72f).coerceIn(0.22f, 0.50f), (lightness + 0.10f).coerceIn(0.46f, 0.72f))
    )
}

private fun Color.toHslColor(): HslColor {
    val max = maxOf(red, green, blue)
    val min = minOf(red, green, blue)
    val delta = max - min
    val lightness = (max + min) / 2f
    val saturation = if (delta == 0f) {
        0f
    } else {
        delta / (1f - abs(2f * lightness - 1f))
    }
    var hue = when {
        delta == 0f -> 0f
        max == red -> 60f * (((green - blue) / delta) % 6f)
        max == green -> 60f * (((blue - red) / delta) + 2f)
        else -> 60f * (((red - green) / delta) + 4f)
    }
    if (hue < 0f) hue += 360f
    return HslColor(hue, saturation.coerceIn(0f, 1f), lightness.coerceIn(0f, 1f))
}

private fun colorFromHsl(hue: Float, saturation: Float, lightness: Float): Color {
    val normalizedHue = (((hue % 360f) + 360f) % 360f) / 60f
    val c = (1f - abs(2f * lightness - 1f)) * saturation
    val x = c * (1f - abs((normalizedHue % 2f) - 1f))
    val m = lightness - c / 2f
    val (redPrime, greenPrime, bluePrime) = when {
        normalizedHue < 1f -> Triple(c, x, 0f)
        normalizedHue < 2f -> Triple(x, c, 0f)
        normalizedHue < 3f -> Triple(0f, c, x)
        normalizedHue < 4f -> Triple(0f, x, c)
        normalizedHue < 5f -> Triple(x, 0f, c)
        else -> Triple(c, 0f, x)
    }
    return Color(
        red = (redPrime + m).coerceIn(0f, 1f),
        green = (greenPrime + m).coerceIn(0f, 1f),
        blue = (bluePrime + m).coerceIn(0f, 1f)
    )
}

private fun expressiveShape(radius: Dp, useSmoothCorners: Boolean): Shape {
    return RoundedCornerShape(if (useSmoothCorners) radius else radius * 0.55f)
}

private fun lerpDp(start: Dp, stop: Dp, fraction: Float): Dp =
    start + (stop - start) * fraction.coerceIn(0f, 1f)

private fun blend(a: Color, b: Color, fraction: Float): Color {
    val f = fraction.coerceIn(0f, 1f)
    return Color(
        red = a.red + (b.red - a.red) * f,
        green = a.green + (b.green - a.green) * f,
        blue = a.blue + (b.blue - a.blue) * f,
        alpha = a.alpha + (b.alpha - a.alpha) * f
    )
}

private fun lighten(color: Color, fraction: Float): Color = blend(color, Color.White, fraction)
private fun darken(color: Color, fraction: Float): Color = blend(color, Color.Black, fraction)

private fun Double.toDurationMillis(): Long {
    if (!isFinite() || this <= 0.0) return 0L
    return roundToInt().toLong()
}

private fun progressRatio(positionMs: Long, durationMs: Long): Float {
    if (durationMs <= 0L) return 0f
    return (positionMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f)
}

private fun formatDuration(ms: Long): String {
    if (ms <= 0L) return "0:00"
    val totalSeconds = ms / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
        "%d:%02d:%02d".format(hours, minutes, seconds)
    } else {
        "%d:%02d".format(minutes, seconds)
    }
}

private fun formatSize(bytes: Long): String {
    if (bytes <= 0L) return "未知"
    val units = arrayOf("B", "KB", "MB", "GB")
    var value = bytes.toDouble()
    var index = 0
    while (value >= 1024 && index < units.lastIndex) {
        value /= 1024.0
        index++
    }
    return if (index == 0) "${value.roundToInt()} ${units[index]}" else "%.1f %s".format(value, units[index])
}

private fun formatFrequency(value: Double): String =
    if (value >= 1000.0) "${(value / 1000.0).roundToInt()}k" else value.roundToInt().toString()

private fun String?.orNullIfBlank(): String? = this?.trim()?.takeIf { it.isNotBlank() }
