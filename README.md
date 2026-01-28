# UltimateSaver

Per-weapon ultimate charge storage for Hytale. Each weapon remembers its SignatureEnergy (ultimate) independently.

## The Problem

In vanilla Hytale, **ultimate charge is lost when you switch away from a weapon**. If you charged your ultimate to 80% and switch to another weapon, that charge vanishes. This mod fixes that.

## Features

- **Ultimate charge persists on weapon switch** — the main feature! No more losing your charged ultimate when switching weapons
- **Individual charge tracking** — each weapon stores its own SignatureEnergy in item metadata
- **Partial charge support** — saves any charge level, not just full ultimate
- **Persistent storage** — charge is saved when you:
  - Switch to another weapon
  - Store it in a chest
  - Trade with other players
- **Multiplayer compatible** — works correctly when other players pick up your weapons

## Technical Details

This mod uses an **event-based architecture** with Mixin injection rather than tick-based systems. This means:

- **Zero performance overhead** during normal gameplay
- Code only executes when weapon stats are recalculated (on weapon switch)
- No continuous polling or per-tick checks

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

2. Download `UltimateSaver-*.*.*.jar` and place it in your **world save folder**:
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

2. Download `UltimateSaver-*.*.*.jar` and place it in:
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

## Source Code

This mod is open source. Feel free to learn from or contribute to the codebase.

## Credits

- **Author:** Morgott
- **Mixin Framework:** Hyxin by Jenya705 — [CurseForge](https://www.curseforge.com/hytale/mods/hyxin) | [GitHub](https://github.com/Jenya705/Hyxin)
