# Implementation Plan - Phase 3: High-Fidelity Signal Path

This phase moves the focus from the DAP shell to the internal audio pipeline. We will enhance the Media3 engine to support audiophile-grade features like gapless playback and ReplayGain, while introducing a formal **Audio Pipeline Model** to ensure high-honesty technical transparency.

## Proposed Changes

### [Component: Core - Data Models]

#### [MODIFY] [Song.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/core/src/main/kotlin/com/lemonsquad/musichome/core/domain/model/Song.kt)
- **Add Audiophile fields**:
    - `val replayGain: Float? = null` (Track gain in dB)
    - `val albumGain: Float? = null` (Album gain in dB)
    - `val replayPeak: Float? = null`
    - `val loudnessRange: Float? = null` (DR - Dynamic Range info, critical for classical/jazz)

### [Component: Core - Metadata Architecture]

#### [NEW] [AudioMetadata.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/core/src/main/kotlin/com/lemonsquad/musichome/ui/models/AudioMetadata.kt)
- A consolidated model for technical file facts (Gain, Peak, DR, Sample Rate, Bit Depth).

#### [NEW] [AudioMetadataReader.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/core/src/main/kotlin/com/lemonsquad/musichome/core/data/media/AudioMetadataReader.kt)
- Abstraction for technical tag parsing to support multiple formats (FLAC, MP3, ALAC).

### [Component: Media - Audio Engine]

#### [MODIFY] [MusicPlaybackService.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/media/src/main/kotlin/com/lemonsquad/musichome/media/player/MusicPlaybackService.kt)
- **ReplayGain Processor**:
    - Implement a listener that retrieves `replayGain` or `albumGain` from the current `Song`.
    - Apply gain correction to the pipeline.
- **Gapless & Offload**:
    - Configure `ExoPlayer` for seamless transitions.
    - Enable `setEnableAudioOffload(true)` for hardware-level battery efficiency.

### [Component: UI - Audio Pipeline Model]

#### [NEW] [AudioPipelineState.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/ui/src/main/kotlin/com/lemonsquad/musichome/ui/models/AudioPipelineState.kt)
- A single source of truth for the entire chain: `Source -> Processing -> Engine -> Output -> Verification`.

#### [MODIFY] [Media3AudioEngine.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/ui/src/main/kotlin/com/lemonsquad/musichome/ui/engine/Media3AudioEngine.kt)
- **USB DAC Verification**: Implement `UsbManager` monitoring for confirmed hardware paths.
- **Bluetooth Codec Heuristics**: Identify LDAC/aptX using system profiles (Status: `ESTIMATED`).

### [Component: UI - Hardware & Sound Controls]

#### [MODIFY] [SoundScreen.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/ui/src/main/kotlin/com/lemonsquad/musichome/ui/screens/SoundScreen.kt)
- **Playback Processing Section**: Add toggles for `Gapless` and `ReplayGain` (modes: Off/Track/Album).

#### [MODIFY] [DeviceDashboardScreen.kt](file:///C:/Users/HP/AndroidStudioProjects/MusicHome/ui/src/main/kotlin/com/lemonsquad/musichome/ui/screens/DeviceDashboardScreen.kt)
- **NEW: Last Listening Session**: A technical summary of the previous session (Duration, Peak Format, Output, Battery Impact).

## Verification Plan

### Manual Verification
- **Pink Floyd Test**: Verify gapless transitions.
- **Dynamics Test**: Play a high-DR classical track and verify ReplayGain "Album Mode" preserves dynamics.
- **Hardware Swap**: Hot-swap between USB DAC and Bluetooth; verify LED and Dashboard update correctly.
- **High-Honesty**: Confirm "Unknown" states are correctly reported when telemetry is missing.
