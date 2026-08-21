# 🎨 UI & UX Specification — Software-Defined DAP Experience

This specification defines the UX architecture, visual hierarchy, screen navigation, and telemetry design based on the canonical interactive prototype.

---

## 🏛️ Screen Navigation & Hierarchy

The DAP experience is structured into a dedicated screen hierarchy:

```text
                        ┌────────────────────────┐
                        │      Status Bar        │ (Time, Storage, Direct Mode, Battery)
                        └───────────┬────────────┘
                                    │
         ┌──────────────────────────┼──────────────────────────┐
         │                          │                          │
         ▼                          ▼                          ▼
┌─────────────────┐        ┌─────────────────┐        ┌─────────────────┐
│    DAP Home     │        │  Local Library  │        │   Now Playing   │
│  (Walkman Home) │        │ (Category/List) │        │ (Core Appliance)│
└────────┬────────┘        └────────┬────────┘        └────────┬────────┘
         │                          │                          │
         └──────────────────────────┴──────────────────────────┘
                                    │
                        ┌───────────▼────────────┐
                        │ Persistent Mini Player │
                        ├────────────────────────┤
                        │ 5-Button Hardware Dock │ (Library, Player, Explore, Sound, Settings)
                        └────────────────────────┘
```

---

## 📱 View Specifications

### 1. Status Bar (`DeviceState`)
- **Always Visible**: Clock, SD Card storage indicator, `DIR` (Direct Audio Routing badge), and battery percentage.
- **Brand Identity**: Subtle centered `MUSIC HOME` watermark.

### 2. DAP Home Screen
- **Primary Grid**: 2x2 hardware tiles (Library, Storage, Playlists, Audio Engine).
- **Recently Added**: Quick access cards with format resolution badges (`192kHz`, `FLAC`).
- **System Stats Footer**: Real-time internal storage and microSD card usage breakdown.

### 3. Now Playing Screen (Core Appliance View)
- **Direct Mode Switch**: Hardware-style `DIRECT` audio routing toggle bypassing Android mixer.
- **Album Artwork**: Rounded artwork container with subtle drop shadow and dark glow.
- **Telemetry Card**: High-honesty format badges (`DSD 11.2MHz`, `1Bit`, `11.289 MHz`, `Source`, `Output`).
- **Tactile Controls**: Hardware play/pause button, skip track, shuffle/repeat, and custom gold progress scrub bar.

### 4. App Drawer Overlay
- Slide-up launcher overlay providing access to additional Android utilities (Files, Settings, Third-party apps) while keeping MusicHome as the core appliance environment.

---

## 🎨 Theme & Typography Design System

| Token | Color / Style | Purpose |
|---|---|---|
| `dap-bg` | `#09090b` (Zinc 950) | Deep black appliance background |
| `dap-panel` | `#18181b` (Zinc 900) | Card and tile surface background |
| `dap-border` | `#27272a` (Zinc 800) | Subtle hardware border lines |
| `dap-gold` | `#d4af37` | Classic DAP gold accent & hi-res highlight |
| `font-sans` | `Inter` | Clean UI typography |
| `font-mono` | `Roboto Mono` | High-honesty audio telemetry & technical specs |
