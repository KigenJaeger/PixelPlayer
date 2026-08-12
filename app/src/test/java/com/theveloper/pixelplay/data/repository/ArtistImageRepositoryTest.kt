package com.theveloper.pixelplay.data.repository

import com.theveloper.pixelplay.data.database.MusicDao
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ArtistImageRepositoryTest {

    @Test
    fun `calculateCustomImageSampleSize keeps small bitmaps at full resolution`() {
        assertEquals(1, ArtistImageRepository.calculateCustomImageSampleSize(1024, 1024))
    }

    @Test
    fun `calculateCustomImageSampleSize aggressively downsamples oversized inputs`() {
        val sampleSize = ArtistImageRepository.calculateCustomImageSampleSize(12000, 8000)

        assertTrue(sampleSize >= 4)
        assertEquals(8, sampleSize)
    }

    @Test
    fun `returns null when no local artist image is cached`() = runTest {
        val musicDao = mockk<MusicDao>()
        val repository = ArtistImageRepository(musicDao)

        coEvery { musicDao.getArtistIdByNormalizedName("Artist Name") } returns 42L
        coEvery { musicDao.getArtistImageUrl(42L) } returns null
        coEvery { musicDao.getArtistImageUrlByNormalizedName("Artist Name") } returns null

        assertNull(repository.getArtistImageUrl("Artist Name", 42L))
    }

    @Test
    fun `returns locally cached artist image`() = runTest {
        val musicDao = mockk<MusicDao>()
        val repository = ArtistImageRepository(musicDao)
        val cachedUrl = "file:///artist/cache.jpg"

        coEvery { musicDao.getArtistIdByNormalizedName("Artist Name") } returns 42L
        coEvery { musicDao.getArtistImageUrl(42L) } returns cachedUrl

        val imageUrl = repository.getArtistImageUrl("Artist Name", 42L)

        assertEquals(cachedUrl, imageUrl)
    }
}
