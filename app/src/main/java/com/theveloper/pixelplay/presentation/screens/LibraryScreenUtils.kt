package com.theveloper.pixelplay.presentation.screens

import com.theveloper.pixelplay.data.model.MusicFolder
import com.theveloper.pixelplay.data.model.Song
import com.theveloper.pixelplay.data.model.SortOption
import java.io.File
import kotlin.math.abs

internal fun positiveMod(value: Int, mod: Int): Int {
    if (mod <= 0) return 0
    return ((value % mod) + mod) % mod
}

internal fun infinitePagerInitialPage(tabCount: Int, selectedTabIndex: Int): Int {
    if (tabCount <= 0) return 0
    val midpoint = Int.MAX_VALUE / 2
    val aligned = midpoint - positiveMod(midpoint, tabCount)
    return aligned + positiveMod(selectedTabIndex, tabCount)
}

internal fun resolveTabIndex(page: Int, tabCount: Int, compactMode: Boolean): Int {
    if (tabCount <= 0) return 0
    return if (compactMode) positiveMod(page, tabCount) else page.coerceIn(0, tabCount - 1)
}

internal fun targetPageForTabIndex(
    currentPage: Int,
    targetTabIndex: Int,
    tabCount: Int,
    compactMode: Boolean
): Int {
    if (tabCount <= 0) return 0
    val safeTarget = positiveMod(targetTabIndex, tabCount)
    if (!compactMode) return safeTarget

    val currentBase = currentPage - positiveMod(currentPage, tabCount)
    val candidate = currentBase + safeTarget
    val prevCandidate = candidate - tabCount
    val nextCandidate = candidate + tabCount

    return listOf(prevCandidate, candidate, nextCandidate)
        .minByOrNull { abs(it - currentPage) }
        ?: candidate
}

internal fun isDescendantFolderPath(ancestorPath: String, candidatePath: String): Boolean {
    val normalizedAncestor = ancestorPath.trimEnd(File.separatorChar)
    val normalizedCandidate = candidatePath.trimEnd(File.separatorChar)
    if (normalizedAncestor == normalizedCandidate) return false
    return normalizedCandidate.startsWith("$normalizedAncestor${File.separatorChar}")
}

internal fun flattenFolders(folders: List<MusicFolder>): List<MusicFolder> {
    return folders.flatMap { folder ->
        val current = if (folder.songs.isNotEmpty()) listOf(folder) else emptyList()
        current + flattenFolders(folder.subFolders)
    }
}

internal fun sortMusicFoldersByOption(folders: List<MusicFolder>, sortOption: SortOption): List<MusicFolder> {
    return when (sortOption) {
        SortOption.FolderNameAZ -> folders.sortedWith(
            compareBy<MusicFolder> { it.name.lowercase() }
                .thenBy { it.path }
        )
        SortOption.FolderNameZA -> folders.sortedWith(
            compareByDescending<MusicFolder> { it.name.lowercase() }
                .thenBy { it.path }
        )
        SortOption.FolderSongCountAsc -> folders.sortedWith(
            compareBy<MusicFolder> { it.totalSongCount }
                .thenBy { it.name.lowercase() }
                .thenBy { it.path }
        )
        SortOption.FolderSongCountDesc -> folders.sortedWith(
            compareByDescending<MusicFolder> { it.totalSongCount }
                .thenBy { it.name.lowercase() }
                .thenBy { it.path }
        )
        SortOption.FolderSubdirCountAsc -> folders.sortedWith(
            compareBy<MusicFolder> { it.totalSubFolderCount }
                .thenBy { it.name.lowercase() }
                .thenBy { it.path }
        )
        SortOption.FolderSubdirCountDesc -> folders.sortedWith(
            compareByDescending<MusicFolder> { it.totalSubFolderCount }
                .thenBy { it.name.lowercase() }
                .thenBy { it.path }
        )
        else -> folders.sortedWith(
            compareBy<MusicFolder> { it.name.lowercase() }
                .thenBy { it.path }
        )
    }
}

internal fun sortSongsForFolderView(songs: List<Song>, sortOption: SortOption): List<Song> {
    return when (sortOption) {
        SortOption.FolderNameZA -> songs.sortedWith(
            compareByDescending<Song> { it.title.lowercase() }
                .thenBy { it.artist.lowercase() }
                .thenBy { it.id }
        )
        else -> songs.sortedWith(
            compareBy<Song> { it.title.lowercase() }
                .thenBy { it.artist.lowercase() }
                .thenBy { it.id }
        )
    }
}

internal fun MusicFolder.collectAllSongs(): List<Song> {
    return songs + subFolders.flatMap { it.collectAllSongs() }
}
