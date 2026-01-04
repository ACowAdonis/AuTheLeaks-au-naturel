# Project Context

## Overview

AuTheLeaks-au-naturel is a fork of AllTheLeaks, a Minecraft Forge mod that fixes memory leaks in mods, Minecraft, and Forge itself.

## Technical Details

- **Minecraft Version**: 1.20.1
- **Mod Loader**: Forge (with Sinytra Connector for Fabric mod compatibility)
- **Build System**: Gradle with NeoForge MDK
- **Language**: Java 17

## Project Structure

- `src/main/java/dev/uncandango/alltheleaks/` - Main source code
  - `annotation/` - @Issue annotation system for conditional mixin application
  - `config/` - ATLProperties configuration management
  - `events/` - Forge event handlers (client, common, server)
  - `feature/` - Optional features (deduplication, memory monitoring)
  - `fix/` - Mod-specific fixes
  - `leaks/` - Memory leak fixes for specific mods
  - `mixin/` - Mixin classes for bytecode modification
  - `report/` - ReportManager task scheduling system

## Key Concepts

### @Issue Annotation
Controls which mixins are applied based on:
- `modId` - Target mod
- `versionRange` - Maven version range
- `config` - Config flag in ATLProperties
- `mixins` - List of mixin classes to apply

### Configuration
Config file: `config/alltheleaks.json`

Key options:
- `enableLeakTracking` - Enable/disable tracking infrastructure
- `showSummaryOnDebugScreen` - Show leak summary on F3 screen
- `memoryUsageWarningPercentage` - Memory warning threshold

## Building

```bash
./gradlew build
```

Output: `build/libs/alltheleaks-*.jar`

## Fork-Specific Changes

This fork adds `enableLeakTracking` config option and fixes internal memory retention issues in ReportManager, DebugThreadsHooks, Trackable, and IssueManager.
