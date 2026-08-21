# 🎛️ DeviceState & Telemetry Architecture

This document specifies the central `DeviceState` hardware state machine and the **High-Honesty Audio Telemetry** system.

---

## 🏛️ Central DeviceState Model

`DeviceState` aggregates physical device telemetry into a single, immutable snapshot:

```text
DeviceState
├── PlaybackState       (PLAYING, PAUSED, IDLE, BUFFERING, ERROR)
├── AudioPipelineState  (Source, Engine, Output, VerificationStatus)
├── VolumeState         (CurrentVolume, MaxVolume, IsMuted)
├── OutputDevice        (SPEAKER, WIRED_HEADPHONES, USB_DAC, BLUETOOTH)
├── BatteryState        (Level, IsCharging)
└── AmbientState        (DeskModeActive, Dimmed, PixelShiftOffset)
```

---

## 🔍 High-Honesty Telemetry Rules

To prevent misleading claims regarding high-resolution audio paths, MusicHome classifies every signal chain node into three explicit confidence levels:

| Verification Level | Criterion | UI Display |
|---|---|---|
| `✓ VERIFIED` | Direct hardware query confirmed (e.g. `UsbManager` attached USB DAC). | Green verified badge |
| `◉ ESTIMATED` | High-confidence inference from Android audio capabilities (e.g. A2DP active). | Amber estimated badge |
| `? UNKNOWN` | Android OS audio HAL obscures true hardware sample rate. | Dimmed unknown badge |

---

## 🛡️ Volume Protection Guard

On application startup, `MusicViewModel` evaluates the persisted volume:
- If `persistedVolume > 70%`, it is automatically reset to **50%**.
- Prevents sudden high-volume bursts when connecting sensitive IEMs or headphones.
