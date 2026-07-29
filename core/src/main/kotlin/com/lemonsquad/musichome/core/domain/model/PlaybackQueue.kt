package com.lemonsquad.musichome.core.domain.model

data class PlaybackQueue(
    val songs: List<Song>,
    val currentIndex: Int = 0,
    val source: QueueSource = QueueSource.ALL_SONGS,
    val sourceName: String? = null,
    val shuffleEnabled: Boolean = false,
    val repeatMode: RepeatMode = RepeatMode.NONE,
    val revision: Int = 0
)

enum class RepeatMode {
    NONE, ONE, ALL
}

enum class QueueSource {
    ALL_SONGS,
    ALBUM,
    ARTIST,
    PLAYLIST,
    FOLDER,
    SEARCH
}
