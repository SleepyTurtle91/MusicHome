package com.lemonsquad.musichome.organizer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lemonsquad.musichome.core.domain.repository.MusicRepository
import com.lemonsquad.musichome.core.domain.model.LibraryStats
import com.lemonsquad.musichome.core.domain.analysis.DuplicateFinder
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LibraryToolsViewModel(val repository: MusicRepository) : ViewModel() {

    private val _isScanning = repository.scanState.map { it !is com.lemonsquad.musichome.core.domain.model.ScanState.Idle && it !is com.lemonsquad.musichome.core.domain.model.ScanState.Finished }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    val isScanning = _isScanning

    val songs = repository.allSongs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val healthStats = repository.libraryStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LibraryStats())

    private val duplicateFinder = DuplicateFinder()
    val duplicateGroups = songs.map { songList ->
        duplicateFinder.findDuplicates(songList)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun scanLibrary() {
        viewModelScope.launch {
            repository.syncLibrary()
        }
    }

    fun updateSong(song: com.lemonsquad.musichome.core.domain.model.Song) {
        // Implementation in core repository later
    }

    suspend fun getSongByPath(path: String): com.lemonsquad.musichome.core.domain.model.Song? {
        return repository.allSongs.first().find { it.path == path }
    }
}
