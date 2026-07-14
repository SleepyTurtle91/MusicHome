package com.lemonsquad.musichome.core.domain.model

data class PlaybackQueue(
    val songs: List<Song>,
    val currentIndex: Int = 0,
    val source: QueueSource = QueueSource.ALL_SONGS,
    val sourceName: String? = null
)

enum class QueueSource {
    ALL_SONGS,
    ALBUM,
    ARTIST,
    PLAYLIST,
    FOLDER,
    SEARCH
}
