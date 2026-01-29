# UltimateSaver

[![en](https://img.shields.io/badge/lang-English-blue)](README.md) [![ru](https://img.shields.io/badge/lang-Русский-green)](README.ru.md)

Per-weapon ultimate charge storage for Hytale. Each weapon remembers its SignatureEnergy (ultimate) independently.

## The Problem

In vanilla Hytale, **ultimate charge is lost when you switch away from a weapon**. If you charged your ultimate to 80% and switch to another weapon, that charge vanishes. This mod fixes that.

## Features

- **Ultimate charge persists on weapon switch** — each weapon stores its own SignatureEnergy in item metadata
- **Individual charge tracking** — each weapon stores its own charge level
- **Partial charge support** — saves any charge level, not just full ultimate
- **Persistent storage** — charge is saved when you: switch weapons, store in chest, trade
- **Zero performance overhead** — event-based architecture, no tick polling

## Requirements

- **Hyxin** — Mixin loader for Hytale
  - [CurseForge](https://www.curseforge.com/hytale/mods/hyxin)
  - [GitHub](https://github.com/Jenya705/Hyxin)

## Installation

### Single Player / Client-Hosted Server

1. Download `Hyxin.jar` and place it in:
   ```
   UserData/EarlyPlugins/
   ```

2. Download `UltimateSaver-x.x.x.jar` and place it in your **world save folder**:
   ```
   UserData/Saves/<YourWorldName>/earlyplugins
   ```
   (Create the `earlyplugins` folder if it doesn't exist)

3. Launch the game and load the world

> **Linux users:** The folder must be named exactly `earlyplugins` (lowercase). Linux is case-sensitive, so `EarlyPlugins` won't work.

### Dedicated Server

1. Download `Hyxin.jar` and place it in:
   ```
   <ServerRoot>/earlyplugins/
   ```

2. Download `UltimateSaver-x.x.x.jar` and place it in:
   ```
   <ServerRoot>/earlyplugins/
   ```

> **Linux users:** The folder must be named exactly `earlyplugins` (lowercase). Linux is case-sensitive.

3. Start the server

## How It Works

When you switch away from a weapon, the current SignatureEnergy (ultimate charge) is saved to the weapon's item metadata. When you switch back, the charge is restored from metadata.

This allows you to have multiple weapons with different charge levels — perfect for combat loadouts where you want pre-charged ultimates ready to use.

## Compatibility

- Works with any weapon that uses the standard SignatureEnergy stat system
- Compatible with other mods that don't modify weapon ultimate behavior

## Credits

- **Author:** Morgott
- **Mixin Framework:** Hyxin by Jenya705 — [CurseForge](https://www.curseforge.com/hytale/mods/hyxin) | [GitHub](https://github.com/Jenya705/Hyxin)
