package com.theveloper.pixelplay.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.LruCache
import androidx.core.graphics.scale
import com.theveloper.pixelplay.data.database.MusicDao
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

@Singleton
class ArtistImageRepository @Inject constructor(
    private val musicDao: MusicDao
) {
    companion object {
        private const val TAG = "ArtistImageRepository"
        private const val CACHE_SIZE = 100
        private const val MAX_CUSTOM_IMAGE_SOURCE_BYTES = 24L * 1024L * 1024L
        private const val MAX_CUSTOM_IMAGE_DIMENSION_PX = 16_384
        private const val MAX_CUSTOM_IMAGE_TOTAL_PIXELS = 80_000_000L
        private const val TARGET_CUSTOM_IMAGE_MAX_DIMENSION_PX = 2_048
        private const val TARGET_CUSTOM_IMAGE_MAX_PIXELS = 4_194_304L

        internal fun calculateCustomImageSampleSize(width: Int, height: Int): Int {
            var sampleSize = 1
            while (
                width / sampleSize > TARGET_CUSTOM_IMAGE_MAX_DIMENSION_PX ||
                height / sampleSize > TARGET_CUSTOM_IMAGE_MAX_DIMENSION_PX ||
                (width.toLong() / sampleSize) * (height.toLong() / sampleSize) > TARGET_CUSTOM_IMAGE_MAX_PIXELS
            ) {
                sampleSize = sampleSize shl 1
            }
            return sampleSize.coerceAtLeast(1)
        }
    }

    private val memoryCache = LruCache<String, String>(CACHE_SIZE)

    suspend fun getArtistImageUrl(artistName: String, artistId: Long): String? {
        if (artistName.isBlank()) return null
        val normalizedName = artistName.trim().lowercase()

        memoryCache.get(normalizedName)?.let { return it }

        val cachedUrl = withContext(Dispatchers.IO) {
            val canonicalArtistId = musicDao.getArtistIdByNormalizedName(artistName) ?: artistId
            musicDao.getArtistImageUrl(canonicalArtistId)
                ?: musicDao.getArtistImageUrlByNormalizedName(artistName)
        }

        if (!cachedUrl.isNullOrBlank()) {
            memoryCache.put(normalizedName, cachedUrl)
        }
        return cachedUrl
    }

    suspend fun prefetchArtistImages(artists: List<Pair<Long, String>>) = withContext(Dispatchers.IO) {
    }

    fun clearCache() {
        memoryCache.evictAll()
    }

    suspend fun getEffectiveArtistImageUrl(artistId: Long, artistName: String): String? {
        val customUri = withContext(Dispatchers.IO) { musicDao.getArtistCustomImage(artistId) }
        if (!customUri.isNullOrBlank()) return customUri
        return getArtistImageUrl(artistName, artistId)
    }

    suspend fun setCustomArtistImage(context: Context, artistId: Long, sourceUri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = decodeCustomArtistBitmap(context, sourceUri) ?: return@withContext null
                val scaledBitmap = scaleBitmapIfNeeded(bitmap)
                try {
                    val destFile = File(context.filesDir, "artist_art_${artistId}.jpg")
                    FileOutputStream(destFile).use { out ->
                        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                    }

                    val internalPath = destFile.absolutePath
                    musicDao.updateArtistCustomImage(artistId, internalPath)
                    Timber.tag(TAG).d("Custom artist image saved: $internalPath")
                    internalPath
                } finally {
                    if (scaledBitmap !== bitmap) {
                        bitmap.recycle()
                    }
                    scaledBitmap.recycle()
                }
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Failed to save custom artist image for id=$artistId")
                null
            }
        }
    }

    private fun decodeCustomArtistBitmap(context: Context, sourceUri: Uri): Bitmap? {
        val resolver = context.contentResolver
        val mimeType = resolver.getType(sourceUri)?.lowercase()
        if (mimeType != null && !mimeType.startsWith("image/")) {
            Timber.tag(TAG).w("Rejected custom artist image with unsupported MIME type: $mimeType")
            return null
        }

        runCatching { resolver.openAssetFileDescriptor(sourceUri, "r") }.getOrNull()?.use { descriptor ->
            val declaredLength = descriptor.length
            if (declaredLength > MAX_CUSTOM_IMAGE_SOURCE_BYTES) {
                Timber.tag(TAG).w("Rejected custom artist image larger than allowed source size: $declaredLength")
                return null
            }
        }

        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(sourceUri)?.use { inputStream ->
            BitmapFactory.decodeStream(inputStream, null, bounds)
        }

        val width = bounds.outWidth
        val height = bounds.outHeight
        if (width <= 0 || height <= 0) {
            Timber.tag(TAG).w("Rejected custom artist image with invalid bounds: ${width}x${height}")
            return null
        }
        if (width > MAX_CUSTOM_IMAGE_DIMENSION_PX || height > MAX_CUSTOM_IMAGE_DIMENSION_PX) {
            Timber.tag(TAG).w("Rejected custom artist image with oversized bounds: ${width}x${height}")
            return null
        }
        if (width.toLong() * height.toLong() > MAX_CUSTOM_IMAGE_TOTAL_PIXELS) {
            Timber.tag(TAG).w("Rejected custom artist image with excessive pixel count: ${width}x${height}")
            return null
        }

        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = calculateCustomImageSampleSize(width, height)
            inPreferredConfig = Bitmap.Config.RGB_565
        }

        return try {
            resolver.openInputStream(sourceUri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, decodeOptions)
            }
        } catch (oom: OutOfMemoryError) {
            Timber.tag(TAG).e(oom, "Failed to decode custom artist image due to OOM")
            null
        }
    }

    private fun scaleBitmapIfNeeded(bitmap: Bitmap): Bitmap {
        val longestEdge = max(bitmap.width, bitmap.height)
        if (longestEdge <= TARGET_CUSTOM_IMAGE_MAX_DIMENSION_PX) {
            return bitmap
        }

        val scale = TARGET_CUSTOM_IMAGE_MAX_DIMENSION_PX.toFloat() / longestEdge.toFloat()
        val scaledWidth = (bitmap.width * scale).roundToInt().coerceAtLeast(1)
        val scaledHeight = (bitmap.height * scale).roundToInt().coerceAtLeast(1)
        return bitmap.scale(scaledWidth, scaledHeight)
    }

    suspend fun clearCustomArtistImage(context: Context, artistId: Long) {
        withContext(Dispatchers.IO) {
            try {
                val destFile = File(context.filesDir, "artist_art_${artistId}.jpg")
                if (destFile.exists()) {
                    destFile.delete()
                    Timber.tag(TAG).d("Deleted custom artist image file: ${destFile.absolutePath}")
                }
                musicDao.updateArtistCustomImage(artistId, null)
            } catch (e: Exception) {
                Timber.tag(TAG).e(e, "Failed to clear custom artist image for id=$artistId")
            }
        }
    }
}
