# Music Home 🎶🛡️

**Music Home transforms Android devices into dedicated music appliances.** Rather than behaving like a traditional Android launcher, Music Home presents a dedicated, distraction-free music environment where playback is always the primary experience.

It strips away the distractions of a standard phone interface and replaces it with a high-fidelity, music-centric environment inspired by classic high-end audio equipment like Sony Walkmans, vintage amplifiers, and modern high-end DAPs (Digital Audio Players).

---

## 📸 Screenshots

| Library | Player | Queue |
| :---: | :---: | :---: |
| ![Library](docs/screenshots/library.png) | ![Player](docs/screenshots/player.png) | ![Queue](docs/screenshots/queue.png) |

| Apps | Sound (EQ) |
| :---: | :---: |
| ![Apps](docs/screenshots/apps.png) | ![Sound](docs/screenshots/sound.png) |

---

## 🏛️ Philosophy

Music Home is built on a simple idea: **Old Android devices deserve a second life.**

Instead of becoming e-waste, they can become beautiful, dedicated music players that remain focused on one purpose—enjoying your music without distractions. Music Home aims to make Android disappear, leaving only the music.

---

## 🚧 Project Status (v1.1 "Direct Access")

Music Home has evolved into a comprehensive **Open-Source DAP Platform**.

**Current focus:**
- [x] Home Launcher registration
- [x] "Walkman Orange" Hardware UI Identity
- [x] Real-time 16-band Spectrum Visualizer
- [x] Professional Multi-band Equalizer
- [x] Intelligent Artwork Caching (Persistent 512px JPGs)
- [x] **Direct Access**: Manual path selection & recursive folder browsing
- [x] **Appliance Memory**: Persistent playback position & queue recovery
- [x] **Hardware Integration**: Physical volume interception & custom HUDs

---

## 🎯 Target Devices

Music Home is specifically optimized for dedicated "appliance" use cases on **Android 7.0+ (Nougat)**:
- **Old Android Tablets**: Transform them into wall-mounted control panels or bedside hi-fi hubs.
- **Spare Android Smartphones**: Create a permanent, distraction-free pocket player.
- **Android-based DAPs**: A custom OS feel for offline-first listening.
- **Android TV Boxes**: Turn them into dedicated media hub appliances.

---

## ✨ Features

- **🏠 Dedicated Appliance Mode**: Registers as the system home screen. The "Home" button is a "Return to Music" button.
- **🎧 Hardware-Inspired UI**: A premium dark-themed interface with metallic accents, brushed textures, and vibrant "Walkman Orange" glows.
- **📊 Real-time Visualization**: Integrated 16-band spectrum visualizer optimized for legacy hardware (30Hz refresh).
- **🎚️ Pro Audio Controls**: High-fidelity vertical EQ sliders with system preset support (Rock, Pop, Bass Boost).
- **📂 Flexible Library**: Mix system MediaStore results with **Manual Watched Folders** for total control over your collection.
- **🕒 Hi-Fi Display**: Persistent clock, battery status, and bitrate information integrated like a high-end audio deck.
- **🎵 Offline First**: Built around local music libraries. Your collection stays on your device and remains fully functional without an Internet connection.

---

## 🏗️ Architecture

The project follows a modularized **Clean Architecture** approach.

```mermaid
graph TD
    subgraph "Music Home"
        app[":app (Entry & Manifest)"]
        ui[":ui (Compose & ViewModels)"]
        core[":core (Business Logic & Data)"]
        media[":media (Media3 Service)"]

        app --> ui
        app --> core
        app --> media
        ui --> media
        ui --> core
        media --> core
    end
```

- **`:app`**: Entry point. Handles `MainActivity` (Launcher), hardware key interception, and manifest permissions.
- **`:ui`**: The visual layer. Built with **Jetpack Compose** using custom hardware-inspired components.
- **`:core`**: The heart of the app. Houses **Manual Scanning**, **Artwork Caching**, and **Room Persistence**.
- **`:media`**: Audio engine using **AndroidX Media3**, **AudioFX (EQ)**, and **Visualizer API**.

---

## 🛠️ Tech Stack

- **Language**: Kotlin 2.4.0
- **UI Framework**: Jetpack Compose (Material 3 Adaptive)
- **Audio Processing**: Android AudioFX + Visualizer API
- **Media Engine**: AndroidX Media3 / ExoPlayer
- **Local Storage**: Room Persistence Library (Migration v3)
- **Image Handling**: Coil + Palette API (Dynamic Backgrounds)

---

## 🗺️ Roadmap

- [x] Launcher replacement functionality
- [x] Immersive Mode & Boot-start support
- [x] Walkman UI Theme refinement
- [x] **Real-time MediaStore synchronization** (Throttled Observer)
- [x] **Full Local Library browsing** (Artists, Albums, Folders)
- [x] **Hardware volume integration** & custom HUDs
- [x] **Adaptive Appliance Layouts** (Phone/Tablet/Focus modes)
- [x] **Album artwork fetching & caching**
- [x] **Folder browsing & Equalizer**
- [ ] **USB DAC support** (Direct bit-perfect output)
- [ ] **Internet Radio integration** (Shoutcast/TuneIn)

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
