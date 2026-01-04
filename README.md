# AuTheLeaks-au-naturel

A fork of [AllTheLeaks](https://github.com/pietro-lopes/AllTheLeaks) by Pietro Lopes (Uncandango), with modifications for the Au Naturel modpack.

## About This Fork

This fork is based on AllTheLeaks for Minecraft Forge 1.20.1. It includes additional configuration options and internal memory optimizations for use in a specific modpack environment.

### Changes from Upstream

**Added:**
- `enableLeakTracking` config option (default: `true`) - when set to `false`, completely disables the tracking infrastructure to reduce AllTheLeaks' own memory footprint

**Fixed:**
- Trackable hash clustering - WeakReferences with garbage-collected referents no longer cluster at hash 0
- Trackable memory retention - aggressive compaction rebuilds sets when >75% of entries are removed
- ReportManager memory retention - tasks map and tick counter now reset on server stop
- DebugThreadsHooks memory retention - thread tracking maps now reset on server stop
- IssueManager memory retention - constructor list cleared after initialization

**Build:**
- Disabled mixins incompatible with target modpack library versions (Flywheel 1.0.x, fabric-networking 1.3.14+)

## Original Project

AllTheLeaks is designed to fix various memory leaks from mods, Minecraft, and (Neo)Forge. Many players suffer from playing their modded modpacks and eventually needing to close the game or crash after some time - a classic symptom of memory leaks.

For the full upstream project, visit:
- GitHub: https://github.com/pietro-lopes/AllTheLeaks
- CurseForge: https://www.curseforge.com/minecraft/mc-mods/alltheleaks

## Configuration

Edit `config/alltheleaks.json`:

```json
{
  "enableLeakTracking": false
}
```

Set `enableLeakTracking` to `false` if you only want AllTheLeaks' memory leak fixes without the tracking/monitoring infrastructure.

## License

This project is licensed under the MIT License - see [LICENSE.txt](LICENSE.txt) for details.

Original work Copyright (c) Pietro Lopes (Uncandango)
