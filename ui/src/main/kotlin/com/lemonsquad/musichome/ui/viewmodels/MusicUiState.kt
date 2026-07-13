package com.lemonsquad.musichome.ui.viewmodels

import com.lemonsquad.musichome.core.domain.LocalSong

sealed interface MusicUiState {
    object Loading : MusicUiState
    object Empty : MusicUiState
    data class Success(val songs: List<LocalSong>) : MusicUiState
}
