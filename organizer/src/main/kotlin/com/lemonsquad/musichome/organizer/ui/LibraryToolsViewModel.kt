package com.lemonsquad.musichome.organizer.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lemonsquad.musichome.organizer.data.OrganizerRepository
import com.lemonsquad.musichome.organizer.data.SongEntity
import com.lemonsquad.musichome.organizer.health.LibraryHealthStats
import com.lemonsquad.musichome.organizer.duplicates.DuplicateFinder
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class LibraryToolsViewModel(private val repository: OrganizerRepository) : ViewModel() {

    private val _isScanning = MutableStateFlow(false)
    val isScanning = _isScanning.asStateFlow()

    val songs = repository.allSongs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val healthStats = repository.healthStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LibraryHealthStats())

    private val duplicateFinder = DuplicateFinder()
    val duplicateGroups = songs.map { songList ->
        duplicateFinder.findDuplicates(songList)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun scanLibrary() {
        viewModelScope.launch {
            _isScanning.value = true
            try {
                repository.refreshLibrary()
            } catch (e: Exception) {
                // Log error
            } finally {
                _isScanning.value = false
            }
        }
    }

    fun updateSong(song: SongEntity) {
        viewModelScope.launch {
            repository.updateSong(song)
        }
    }

    suspend fun getSongByPath(path: String): SongEntity? {
        return repository.allSongs.first().find { it.path == path }
    }
}
