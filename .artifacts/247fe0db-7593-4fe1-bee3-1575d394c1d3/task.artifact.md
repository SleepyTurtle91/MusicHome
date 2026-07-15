# Task: Version 1.2 - Real DAP Experience

## Phase 1 — Core DAP behavior
- [x] Refactor `EqualizerManager` for session lifecycle & settings persistence (`attachToSession`)
- [x] Implement `MediaLibrarySession.Callback` in `MusicPlaybackService` for EQ custom commands
- [x] Add EQ command handling in `MusicPlaybackService`
- [x] Update `MusicViewModel` with EQ StateFlows and command methods
- [x] Connect `SoundScreen` UI to `MusicViewModel` EQ state

## Phase 2 — Persistence & Restoration
- [x] Update `PlaybackStateEntity` and `SongDao` for `lastDestination` and `lastDestinationId`
- [x] Implement `lastDestination` persistence in `LocalMediaRepository`
- [x] Refactor `restorePlaybackState` in `MusicViewModel` (Separate Queue vs Position)
- [x] Implement initial navigation to `lastDestination` in `MusicHomeApp`

## Phase 3 — Appliance feeling & UX
- [x] Refine `MiniPlayer` (Navigation to full player, better state sync)
- [x] Implement DAP-style Back button logic in `MusicHomeApp` (via `BackHandler`)
- [x] Add `SleepTimerManager` and Media3 command integration
- [x] Implement EQ Presets (data-driven)

## Phase 4 — Verification
- [ ] Manual test: EQ audible changes
- [ ] Manual test: Position & screen restoration
- [ ] Manual test: Sleep timer
- [ ] Manual test: Back button behavior
