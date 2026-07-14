package com.lemonsquad.musichome.ui.viewmodels

import com.lemonsquad.musichome.core.domain.model.Album
import com.lemonsquad.musichome.core.domain.model.Artist
import com.lemonsquad.musichome.core.domain.model.Song

sealed interface MusicUiState {
    object Loading : MusicUiState
    object Empty : MusicUiState
    data class Success(
        val songs: List<Song>,
        val albums: List<Album> = emptyList(),
        val artists: List<Artist> = emptyList()
    ) : MusicUiState
}
