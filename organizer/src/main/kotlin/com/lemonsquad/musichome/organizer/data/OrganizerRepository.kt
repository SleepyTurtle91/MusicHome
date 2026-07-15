package com.lemonsquad.musichome.organizer.data

import com.lemonsquad.musichome.organizer.scanner.MediaScanner
import com.lemonsquad.musichome.organizer.duplicates.DuplicateFinder
import com.lemonsquad.musichome.organizer.health.LibraryHealthAnalyzer
import com.lemonsquad.musichome.organizer.health.LibraryHealthStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OrganizerRepository(
    private val musicDao: MusicDao,
    private val mediaScanner: MediaScanner
) {
    val allSongs: Flow<List<SongEntity>> = musicDao.getAllSongs()
    val allAlbums: Flow<List<AlbumEntity>> = musicDao.getAllAlbums()
    val allArtists: Flow<List<ArtistEntity>> = musicDao.getAllArtists()

    private val duplicateFinder = DuplicateFinder()
    private val healthAnalyzer = LibraryHealthAnalyzer()

    val healthStats: Flow<LibraryHealthStats> = allSongs.map { songs ->
        val duplicates = duplicateFinder.findDuplicates(songs).sumOf { it.songs.size - 1 }
        healthAnalyzer.analyze(songs, duplicates)
    }

    suspend fun refreshLibrary() {
        val songs = mediaScanner.scanLocalMedia()
        val (albums, artists) = mediaScanner.extractAlbumsAndArtists(songs)
        
        musicDao.deleteAllSongs()
        musicDao.deleteAllAlbums()
        musicDao.deleteAllArtists()

        musicDao.insertSongs(songs)
        musicDao.insertAlbums(albums)
        musicDao.insertArtists(artists)
    }

    suspend fun updateSong(song: SongEntity) {
        musicDao.updateSong(song)
    }

    suspend fun getSongById(id: Long): SongEntity? {
        return musicDao.getSongById(id)
    }
}
