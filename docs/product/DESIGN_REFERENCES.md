# 📖 Design References & Ecosystem Study

This document details external hardware references and product design studies that inform the **MusicHome** DAP environment.

---

## 🎯 Primary External References

### 1. Sony Walkman Ecosystem (NW-ZX700 / NW-A300 Series)
- **Reference**: Sony Walkman Help Guide & Portable Audio Ecosystem.
- **Key Patterns Adopted**:
  - Dedicated **WALKMAN Home** appliance launcher concept.
  - Dedicated hardware dock navigation (Library, Player, Explore, Sound, Settings).
  - Clear hardware signal telemetry displays (Sample rate, bit depth, codec).
  - Desk mode / ambient display for docked devices.

### 2. High-Fidelity Audio Appliance Architecture
- **Key Patterns Adopted**:
  - High-honesty verification levels (`✓ VERIFIED`, `◉ ESTIMATED`, `? UNKNOWN`).
  - Hardware startup volume guard (50% max volume protection on boot).
  - Persist-first queue revisioning for immediate state recovery.

---

## 🛑 What MusicHome Intentionally Does NOT Replicate

- **Proprietary Android Lockdowns**: MusicHome preserves Android's open storage access while acting as a dedicated DAP.
- **Unverified Audio Claims**: MusicHome never displays "Bit-Perfect" or "MQA Master" unless the underlying hardware path is verified.
