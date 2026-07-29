package com.lemonsquad.musichome.core.domain.analysis

import com.lemonsquad.musichome.core.domain.model.Song
import com.lemonsquad.musichome.core.domain.model.LibraryStats

class LibraryHealthAnalyzer {
    
    fun analyze(songs: List<Song>, duplicates: Int): LibraryStats {
        if (songs.isEmpty()) return LibraryStats()

        val totalSongs = songs.size
        val totalAlbums = songs.distinctBy { (it.album ?: "") + it.artist }.size
        val totalArtists = songs.distinctBy { it.artist }.size
        
        val missingArtwork = songs.count { it.artwork == null }
        val unknownMetadata = songs.count { 
            it.artist.contains("Unknown", ignoreCase = true) || 
            (it.album?.contains("Unknown", ignoreCase = true) ?: true)
        }

        val metadataScore = ((totalSongs - unknownMetadata).toFloat() / totalSongs) * 40
        val artworkScore = ((totalSongs - missingArtwork).toFloat() / totalSongs) * 40
        val uniquenessScore = if (totalSongs > 0) {
            ((totalSongs - duplicates).toFloat() / totalSongs) * 20
        } else 0f
        
        val totalScore = (metadataScore + artworkScore + uniquenessScore).toInt().coerceIn(0, 100)

        return LibraryStats(
            totalSongs = totalSongs,
            totalAlbums = totalAlbums,
            totalArtists = totalArtists,
            missingArtworkCount = missingArtwork,
            duplicateCount = duplicates,
            healthScore = totalScore,
            lastScanTimestamp = System.currentTimeMillis()
        )
    }
}
