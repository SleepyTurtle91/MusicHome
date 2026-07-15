package com.lemonsquad.musichome.organizer.health

import com.lemonsquad.musichome.organizer.data.SongEntity

data class LibraryHealthStats(
    val totalSongs: Int = 0,
    val totalAlbums: Int = 0,
    val totalArtists: Int = 0,
    val missingArtworkCount: Int = 0,
    val unknownMetadataCount: Int = 0,
    val duplicateCount: Int = 0,
    val score: Int = 0 // 0-100
)

class LibraryHealthAnalyzer {
    
    fun analyze(songs: List<SongEntity>, duplicates: Int): LibraryHealthStats {
        if (songs.isEmpty()) return LibraryHealthStats()

        val totalSongs = songs.size
        val totalAlbums = songs.distinctBy { it.album + it.artist }.size
        val totalArtists = songs.distinctBy { it.artist }.size
        
        val missingArtwork = songs.count { it.artworkPath.isNullOrBlank() }
        val unknownMetadata = songs.count { 
            it.artist.contains("Unknown", ignoreCase = true) || 
            it.album.contains("Unknown", ignoreCase = true) 
        }

        // Calculate score
        // 40% Metadata accuracy, 40% Artwork coverage, 20% Uniqueness
        val metadataScore = ((totalSongs - unknownMetadata).toFloat() / totalSongs) * 40
        val artworkScore = ((totalSongs - missingArtwork).toFloat() / totalSongs) * 40
        val uniquenessScore = if (totalSongs > 0) {
            ((totalSongs - duplicates).toFloat() / totalSongs) * 20
        } else 0f
        
        val totalScore = (metadataScore + artworkScore + uniquenessScore).toInt().coerceIn(0, 100)

        return LibraryHealthStats(
            totalSongs = totalSongs,
            totalAlbums = totalAlbums,
            totalArtists = totalArtists,
            missingArtworkCount = missingArtwork,
            unknownMetadataCount = unknownMetadata,
            duplicateCount = duplicates,
            score = totalScore
        )
    }
}
