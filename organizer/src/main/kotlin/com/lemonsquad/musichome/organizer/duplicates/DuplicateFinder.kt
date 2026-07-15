package com.lemonsquad.musichome.organizer.duplicates

import com.lemonsquad.musichome.organizer.data.SongEntity

class DuplicateFinder {

    /**
     * Finds potential duplicates based on title, duration, and file size.
     */
    fun findDuplicates(songs: List<SongEntity>): List<DuplicateGroup> {
        return songs.groupBy { "${it.title.lowercase()}|${it.duration / 1000}|${it.size}" }
            .filter { it.value.size > 1 }
            .map { (key, group) ->
                DuplicateGroup(
                    key = key,
                    songs = group
                )
            }
    }

    /**
     * Advanced matching based on normalized metadata.
     */
    fun findByMetadata(songs: List<SongEntity>): List<DuplicateGroup> {
        return songs.groupBy { "${it.title.lowercase()}|${it.artist.lowercase()}|${it.album.lowercase()}" }
            .filter { it.value.size > 1 }
            .map { (key, group) ->
                DuplicateGroup(
                    key = key,
                    songs = group
                )
            }
    }
}

data class DuplicateGroup(
    val key: String,
    val songs: List<SongEntity>
)
