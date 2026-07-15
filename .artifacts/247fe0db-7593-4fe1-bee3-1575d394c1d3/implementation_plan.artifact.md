# Implementation Plan - Version 1.2: Real DAP Experience

Moving Music Home from a prototype to a real Digital Audio Player experience by connecting the EQ, improving navigation, ensuring persistent state, and adding appliance-like features.

## User Review Required

> [!IMPORTANT]
> **Equalizer Connection**: I will be using Media3's custom commands to communicate EQ changes from the UI to the MediaSession. This avoids complex service binding logic while staying within the Media3 ecosystem.
>
> **Audio Session Lifecycle**: `EqualizerManager` will be re-attached whenever the `ExoPlayer` instance or session changes to ensure settings persist across player re-creations.
>
> **Back Button Behavior**: To achieve the "appliance" feel, I will modify `MainActivity` so that if music is playing, the back button navigates to the Player screen instead of exiting the app (unless already on the Player screen).

## Proposed Changes

### [Component] Audio Engine & Media3 Integration

#### [MODIFY] [MusicPlaybackService.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/media/src/main/kotlin/com/lemonsquad/musichome/media/player/MusicPlaybackService.kt)
- Implement `MediaLibrarySession.Callback` to handle custom commands for Equalizer updates and Sleep Timer.
- Handle "SET_EQ_BAND", "SET_EQ_ENABLED", and "SET_SLEEP_TIMER" commands.
- Re-attach `EqualizerManager` on player initialization or transition if session ID changes.

#### [MODIFY] [EqualizerManager.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/media/src/main/kotlin/com/lemonsquad/musichome/media/player/EqualizerManager.kt)
- Rename `initialize` to `attachToSession(sessionId: Int)`.
- Ensure settings are reapplied when attaching to a new session.

#### [NEW] [SleepTimerManager.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/media/src/main/kotlin/com/lemonsquad/musichome/media/player/SleepTimerManager.kt)
- Logic for countdown and pausing playback.

### [Component] Navigation & UI

#### [MODIFY] [MusicHomeApp.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/ui/src/main/kotlin/com/lemonsquad/musichome/ui/MusicHomeApp.kt)
- Update initial navigation to use `lastDestination` from the database.
- Ensure the Player is easily accessible.

#### [MODIFY] [MiniPlayer.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/ui/src/main/kotlin/com/lemonsquad/musichome/ui/components/MiniPlayer.kt)
- Connect to `MusicViewModel.playbackStatus`.
- Implement navigation to full player on click.

#### [MODIFY] [SoundScreen.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/ui/src/main/kotlin/com/lemonsquad/musichome/ui/screens/SoundScreen.kt)
- Implement data-driven EQ presets.
- Connect sliders and toggle to `MusicViewModel`.

### [Component] State & Persistence

#### [MODIFY] [MusicRepository.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/core/src/main/kotlin/com/lemonsquad/musichome/core/domain/repository/MusicRepository.kt)
- Add methods for `lastDestination`, `lastDestinationId`.
- Add methods for `EqualizerSettings`.

#### [MODIFY] [PlaybackStateEntity.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/core/src/main/kotlin/com/lemonsquad/musichome/core/data/database/PlaybackStateEntity.kt)
- Add `lastDestination: String?` and `lastDestinationId: String?`.

#### [MODIFY] [MusicViewModel.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/ui/src/main/kotlin/com/lemonsquad/musichome/ui/viewmodels/MusicViewModel.kt)
- Implement EQ command sending.
- Implement Sleep Timer trigger.
- Handle state restoration (Queue vs Position).

#### [MODIFY] [MainActivity.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/app/src/main/kotlin/com/lemonsquad/musichome/MainActivity.kt)
- Refine `onBackPressed` logic: if playing and not on player screen -> go to player.

## Verification Plan

### Automated Tests
- Unit tests for `EqualizerManager` lifecycle.
- Persistence tests for new `PlaybackStateEntity` fields.

### Manual Verification
1. **EQ Test**: Verify audible changes and persistence after app restart.
2. **Persistence Test**: Reopen app to the same screen and same playback position.
3. **Sleep Timer Test**: Set a 1-minute timer and verify music pauses.
4. **Back Navigation**: Verify it returns to player when music is active.
