package com.lemonsquad.musichome.ui.viewmodels

import android.net.Uri
import com.lemonsquad.musichome.core.domain.model.Album
import com.lemonsquad.musichome.core.domain.model.Song

sealed interface AlbumDetailUiState {
    object Loading : AlbumDetailUiState
    data class Success(
        val album: Album,
        val songs: List<Song>,
        val artworkUri: Uri?,
        val dominantColor: Int? = null
    ) : AlbumDetailUiState
    object Error : AlbumDetailUiState
}
