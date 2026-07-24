# Music Home Hardware Language

## Device First Philosophy
Music Home is not an "app" that manages music; it is a **dedicated audio appliance**. Every interaction must reinforce the feeling of using a high-end physical Digital Audio Player (DAP).

### Core Principles
- **Hardware-First Navigation**: 5 core buttons that act as physical hardware switches.
- **Audiophile Trust (High-Honesty)**: Never display "Bit Perfect" unless the hardware path is verified. Use "Estimated Direct Path" for software-driven high-quality playback.
- **Glanceable Status**: The multi-state LED communicates the final hardware output path above all else.
- **Intentionality**: Every control should feel like a mechanical calibration, not a playful software slider.

## LED Language (Priority Hierarchy)
The LED reflects the current *hardware* state. If multiple states apply, the one highest in this list wins:

1.  **🟣 Purple (DSD)**: Native DSD playback detected at the output stage.
2.  **🔵 Blue (Wireless)**: Audio is being routed via Bluetooth or Wireless (overrides resolution colors).
3.  **🟠 Orange (Hi-Res)**: Hi-Res audio (24-bit+ / 88.2kHz+) confirmed at output.
4.  **⚪ White (Standard)**: Standard 16-bit / 44.1kHz audio.
5.  **🟡 Pulsing Orange (Activity)**: System busy (e.g., scanning library).
6.  **🌑 Grey (Idle)**: No audio processing active.

## Haptic Vocabulary
- **Soft Click (Virtual Key)**: Used for tab switching and general navigation.
- **Mechanical Click (Heavy)**: Used for "Hardware" toggles (Bit Perfect, Gain Switch, DAC selection).
- **Warning Pulse**: Used for state failures (e.g., DAC disconnected during playback).
