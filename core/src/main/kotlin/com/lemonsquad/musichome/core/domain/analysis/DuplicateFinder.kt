package com.lemonsquad.musichome.core.domain.analysis

import com.lemonsquad.musichome.core.domain.model.Song

class DuplicateFinder {

    fun findDuplicates(songs: List<Song>): List<DuplicateGroup> {
        return songs.groupBy { "${it.title.lowercase()}|${it.duration / 1000}|${it.size}" }
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
    val songs: List<Song>
)
