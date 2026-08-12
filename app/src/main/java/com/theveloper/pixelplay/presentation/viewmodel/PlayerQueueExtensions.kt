package com.theveloper.pixelplay.presentation.viewmodel

import androidx.media3.common.C
import com.theveloper.pixelplay.data.model.Song
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList

internal fun List<Song>.toPlaybackQueue(): ImmutableList<Song> = when (this) {
    is PersistentList<Song> -> this
    is ImmutableList<Song> -> this
    else -> this.toPersistentList()
}

internal fun ImmutableList<Song>.asPersistentPlaybackQueue(): PersistentList<Song> =
    this as? PersistentList<Song> ?: this.toPersistentList()

internal fun ImmutableList<Song>.replaceSong(updatedSong: Song): ImmutableList<Song> {
    val index = indexOfFirst { it.id == updatedSong.id }
    if (index == -1) return this
    return asPersistentPlaybackQueue().set(index, updatedSong)
}

internal fun ImmutableList<Song>.removeSongById(songId: String): ImmutableList<Song> {
    val index = indexOfFirst { it.id == songId }
    if (index == -1) return this
    return asPersistentPlaybackQueue().removeAt(index)
}

internal fun ImmutableList<Song>.moveSong(fromIndex: Int, toIndex: Int): ImmutableList<Song> {
    if (fromIndex == toIndex || fromIndex !in indices || toIndex !in indices) return this
    val movedSong = this[fromIndex]
    return asPersistentPlaybackQueue()
        .removeAt(fromIndex)
        .add(toIndex, movedSong)
}

internal fun moveQueueIndex(index: Int, fromIndex: Int, toIndex: Int): Int {
    if (index == C.INDEX_UNSET || fromIndex == toIndex) return index
    return when {
        index == fromIndex -> toIndex
        fromIndex < toIndex && index in (fromIndex + 1)..toIndex -> index - 1
        toIndex < fromIndex && index in toIndex until fromIndex -> index + 1
        else -> index
    }
}
