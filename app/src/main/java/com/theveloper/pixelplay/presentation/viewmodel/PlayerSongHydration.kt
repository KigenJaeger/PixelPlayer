package com.theveloper.pixelplay.presentation.viewmodel

import com.theveloper.pixelplay.data.model.Lyrics
import com.theveloper.pixelplay.data.model.Song
import com.theveloper.pixelplay.data.provider.SharedArtworkContentProvider
import com.theveloper.pixelplay.utils.LocalArtworkUri
import com.theveloper.pixelplay.utils.LyricsUtils

internal fun Song.withRepositoryHydration(repositorySong: Song): Song {
    if (id != repositorySong.id) return this

    val hydratedArtworkUri = when {
        repositorySong.albumArtUriString.isNullOrBlank() -> albumArtUriString
        albumArtUriString.isNullOrBlank() -> repositorySong.albumArtUriString
        areEquivalentArtworkUrisForSong(id, albumArtUriString, repositorySong.albumArtUriString) ->
            albumArtUriString
        else -> repositorySong.albumArtUriString
    }

    return repositorySong.copy(
        contentUriString = repositorySong.contentUriString.ifBlank { contentUriString },
        albumArtUriString = hydratedArtworkUri,
        duration = repositorySong.duration.takeIf { it > 0L } ?: duration,
        lyrics = repositorySong.lyrics ?: lyrics
    )
}

internal fun areEquivalentArtworkUrisForSong(
    songId: String,
    firstUri: String?,
    secondUri: String?
): Boolean {
    if (firstUri == secondUri) return true
    if (firstUri.isNullOrBlank() || secondUri.isNullOrBlank()) return false

    val targetSongId = songId.toLongOrNull() ?: return false

    fun resolveUriSongId(uri: String): Long? {
        return LocalArtworkUri.parseSongId(uri)
            ?: SharedArtworkContentProvider.parseSongId(uri)
    }

    val firstSongId = resolveUriSongId(firstUri)
    val secondSongId = resolveUriSongId(secondUri)
    return firstSongId == targetSongId && secondSongId == targetSongId
}

internal fun Song.improvesLyricsLookupComparedTo(previousSong: Song): Boolean {
    return (previousSong.lyrics.isNullOrBlank() && !lyrics.isNullOrBlank()) ||
        (previousSong.path.isBlank() && path.isNotBlank()) ||
        (previousSong.contentUriString.isBlank() && contentUriString.isNotBlank())
}

internal fun parsePersistedLyrics(rawLyrics: String?): Lyrics? {
    val normalizedLyrics = rawLyrics?.trim()?.takeIf { it.isNotBlank() } ?: return null
    val parsedLyrics = LyricsUtils.parseLyrics(normalizedLyrics)
    return parsedLyrics.takeIf {
        !it.synced.isNullOrEmpty() || !it.plain.isNullOrEmpty()
    }
}
