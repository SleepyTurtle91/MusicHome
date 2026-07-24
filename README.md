# Music Home 🎧

## Software-Defined Digital Audio Player (DAP)

Music Home transforms Android devices into dedicated high-fidelity audio appliances.

Inspired by premium hardware players such as Sony Walkman and Astell&Kern, Music Home combines a hardware-inspired interface, transparent audio telemetry, and a modular audio engine to create a focused listening experience.

> A music device, not just a music app.

---

# Device First Philosophy

Music Home follows a **Device First** design philosophy:

### 🎛 Tactile

The interface behaves like dedicated audio hardware.

* Hardware-style navigation
* Mechanical interaction feedback
* Large physical-style controls
* Purpose-driven screens

### 🔍 Transparent

The device explains what is happening.

Instead of hiding the audio path, Music Home exposes:

```
Source → Engine → Output
```

Users can understand:

* What file is playing
* How it is processed
* Where the audio is being sent

### 💾 Persistent

A dedicated device remembers its user.

Music Home preserves:

* Last playback position
* Active screen
* Volume state
* Listening session information

---

# High-Honesty Audio System

Music Home avoids claiming capabilities without evidence.

Every technical status is classified into three confidence levels:

| Status      | Meaning                                                  |
| ----------- | -------------------------------------------------------- |
| ✓ VERIFIED  | Confirmed by hardware or system information              |
| ◉ ESTIMATED | High-confidence information inferred from available data |
| ? UNKNOWN   | Information unavailable or cannot be confirmed           |

Example:

```
Source:
FLAC 24-bit / 96kHz

Engine:
Media3

Output:
USB DAC

Verification:
✓ VERIFIED
```

For uncertain information:

```
Output:
Bluetooth

Codec:
LDAC

Verification:
◉ ESTIMATED
```

Transparency is preferred over false precision.

---

# Hardware UI Architecture

Music Home uses a dedicated five-button hardware dock:

```
🎵 Library
▶ Player
🔍 Explore
🎚 Sound
⚙ Settings
```

Each destination has a specific purpose:

| Section  | Purpose                             |
| -------- | ----------------------------------- |
| Library  | Manage and browse music collection  |
| Player   | Immersive listening experience      |
| Explore  | Device management and tools         |
| Sound    | Audio tuning and signal information |
| Settings | Device configuration                |

---

# Persistent Playback Strip

The Playback Strip acts as the DAP front-panel display.

Always visible:

* Track title
* Artist
* Format
* Sample rate
* Bit depth
* Playback progress

Example:

```
Hotel California
Eagles

FLAC 24/96

━━━━━━○━━━━ ▶
```

Users can access playback information without leaving their current workflow.

---

# High-Fidelity Sound System

Music Home provides an audiophile-focused audio control environment.

## Signal Chain Card

The Trust Card visualizes the complete audio path:

```
SOURCE
FLAC 24/96

↓

ENGINE
Media3

↓

OUTPUT
USB DAC
✓ VERIFIED
```

---

## Playback Processing

Supported audio features:

✅ Gapless Playback
✅ ReplayGain Track Mode
✅ ReplayGain Album Mode
✅ Dynamic Range Metadata
✅ Audio Offload Support

Designed for:

* Classical recordings
* Live albums
* High-resolution libraries
* Long listening sessions

---

# OLED-Safe Desk Mode

Music Home can transform into a dedicated desktop audio display.

Designed for docked devices:

Features:

* OLED pure black background
* Pixel-shifting protection
* Automatic dimming
* Landscape orientation
* Battery monitoring
* Network status
* Sample rate display

The phone becomes a small desktop music appliance.

---

# Technical Architecture

Music Home uses a modular software-defined DAP architecture.

## DeviceState

A centralized source of truth:

```text
DeviceState

├── Playback
├── Audio Pipeline
├── Output Device
├── Verification Status
├── Device Mode
└── Hardware Capabilities
```

All major UI components consume the same hardware state.

---

## Audio Engine Abstraction

The UI is separated from playback implementation.

Architecture:

```
UI

↓

AudioController
AudioTelemetry
AudioVisualizer

↓

Media3 Audio Engine

↓

Android Audio System
```

Future audio engines can be integrated without rebuilding the application interface.

---

# Project Status

## 🚀 DAP Appliance Foundation Complete

Completed:

✅ Hardware-inspired DAP interface
✅ Five-tab Hardware Dock
✅ Persistent Playback Strip
✅ DeviceState architecture
✅ High-Honesty telemetry
✅ Hardware Dashboard
✅ Desk Mode
✅ Gapless playback
✅ ReplayGain processing
✅ Audio pipeline visualization

Future milestones:

* Advanced DSP engine
* DSD workflow
* Expanded DAC integrations
* Intelligent library analysis
* Advanced audiophile tools

---

## 🚀 Getting Started

1. Clone the repository and open in Android Studio.
2. Build and run the `app` module on your target device (Android 7.0+).
3. Select **Music Home** as your **Home app** when prompted.
4. Go to the **FOLDERS** tab to add your music directories.

---

## 📜 License

This project is licensed under the MIT License.

Created by **Lemon Squad**. Optimized for music lovers.
