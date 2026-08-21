# 🎧 DAP Philosophy — Software-Defined Audio Appliance

> **MusicHome is not an Android music app pretending to be a DAP. It is a software-defined DAP whose Android platform is the underlying execution environment.**

---

## 🏛️ Core Product Principles

MusicHome transforms standard Android devices into dedicated, high-fidelity digital audio appliances inspired by premium hardware players (such as Sony Walkman NW-ZX700 / Astell&Kern).

### 1. 🎛️ Dedicated Appliance Identity (WALKMAN Home Concept)
- **Music-First Focus**: The interface is dedicated to local music ownership, playback, and device controls.
- **Hardware-Style Interaction**: Large physical-style controls, tactile tactile feedback, clear screen hierarchy, and dedicated 5-tab hardware dock.

### 2. 🔍 High-Honesty Audio Telemetry
- **No False Precision**: Audio pipeline metrics are classified into `VERIFIED` (hardware confirmed USB DAC), `ESTIMATED` (Bluetooth/A2DP capabilities), or `UNKNOWN`.
- **Transparent Signal Chain**: Users can inspect exact file formats, sample rates, bit depths, and processing paths.

### 3. 💾 Persistent Hardware Memory
- **Silent Resume**: Preserves queue state, current position timestamp, volume settings, and active screen across app restarts and power cycles.

---

## 📐 Reference Hierarchy

```text
Sony Walkman / Hardware DAPs
        │
        ▼
PRODUCT PHILOSOPHY (DAP Appliance Identity)
        │
        ▼
MusicHome System Architecture (DeviceState, Media Pipeline)
        │
        ▼
Features & Implementation (UDF, Compose, Media3)
```

> **Reference ≠ Specification**: Sony Walkman design patterns inspire product identity, but MusicHome's system specifications (`ROADMAP.md`, `FEATURES.md`, `ARCHITECTURE_DECISIONS.md`) remain authoritative.
