# Music Home v1.2.1 - The Real DAP Experience

This release transforms Music Home from a functional prototype into a true Digital Audio Player (DAP) experience. We've focused on audio precision, appliance-like persistence, and refining the overall UX to make your device feel like dedicated hardware.

## 🎚️ What's New

### Real Audio Sculpting
- **Precision Equalizer**: The EQ is now fully connected to the Android audio engine. Adjust frequencies in real-time with audible feedback.
- **Session-Aware Processing**: Your audio settings now survive track transitions and player re-creations.
- **Built-in Presets**: Quickly switch between Rock, Pop, Jazz, Bass Boost, and Flat profiles.

### Appliance-Level Persistence
- **Last Screen Restoration**: Music Home now remembers exactly where you were. Whether you're in the middle of a folder search or tweaking the EQ, it reopens precisely there.
- **Independent Playback Restoration**: The app restores your queue and exact playback position independently, ensuring a seamless resume after a reboot or app restart.

### Refined DAP UX
- **DAP Navigation Model**: If music is playing, the "Back" button intelligently returns you to the Now Playing screen instead of exiting the app.
- **Interactive Mini Player**: Control your music from any screen with new skip controls on the mini-bar.
- **Sleep Timer**: Fall asleep to your music with the new Sleep Timer (accessible in Sound settings).
- **Immersive Focus Mode**: Controlled playback directly from the full-screen artwork view.

## 🛠️ Internal Improvements
- Migrated to Media3 custom command architecture for decoupled UI/Service communication.
- Enhanced AudioFX lifecycle management to prevent session detachment issues.
- Optimized Room database schema for UI state persistence.
