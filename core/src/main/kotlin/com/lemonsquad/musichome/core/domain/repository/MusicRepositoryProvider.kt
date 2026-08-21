package com.lemonsquad.musichome.core.domain.repository

/**
 * L.I.S.A. Workstream A1: Application-Scoped Repository Provider
 *
 * Contract for providing a process-wide singleton [MusicRepository].
 * Implemented by the Application class in :app, consumed via cast in
 * :media (MusicPlaybackService) and :app (MainActivity) without
 * introducing a cyclic Gradle dependency.
 */
interface MusicRepositoryProvider {
    val musicRepository: MusicRepository
}
