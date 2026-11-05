# Hypixel Speed Builders Trainer - NeoForge 1.21.1 port

Port of KKSEAM's Fabric mod "spbtrainer" (1.21.4) to **NeoForge 1.21.1**.
Original: https://github.com/KKSEAM/spbtrainer (license file shipped as CC0 1.0, see LICENSE_spbtrainer).
All 355 builds, commands, keybinds and behaviour are the same. The original source was not public, so
this was reconstructed from the decompiled jar and then rewritten for NeoForge / Mojang mappings.

## Build
Needs JDK 21.

    ./gradlew build          (Windows: gradlew.bat build)

The jar ends up in `build/libs/spbtrainer-1.0.0.jar`. Test with `./gradlew runClient`.

If CurseMaven is unreachable, put the WorldEdit jar in `libs/` and use the `files(...)` lines
noted in `build.gradle`.

## Install
1. NeoForge 1.21.1
2. **WorldEdit for NeoForge 1.21.1** (7.3.5 or newer, 7.3.8 recommended) - Fabric API is NOT needed
3. Put `spbtrainer-1.0.0.jar` in `mods/`
4. Old times: copy `config/spbtrainer.json` from your Fabric instance (same format).
   Custom builds: copy `config/spbtrainer/custom_builds/`.

## Changes from the Fabric version
- Fabric API events -> NeoForge events (commands, chat, server tick, right click block, block break).
- `FabricAdapter` -> WorldEdit's `NeoForgeAdapter`.
- Keybinds registered through `RegisterKeyMappingsEvent` with translatable names (still Grave = clear, F6 = restart).
- Player break-speed / canHarvest mixins replaced by `PlayerEvent.BreakSpeed` / `HarvestCheck` events.
- Removed the `breakDeltaClient` mixin: it targeted a method returning `int` but forced a `Float`,
  which would crash if ever reached (it never was, since build mode is creative).
- Removed `Thread.sleep(10)` on the client thread in the sign-screen suppressor and the per-call debug spam.
- Only one mixin remains: `FlowableFluidMixin` (targets `FlowingFluid#spreadTo`).

## v1.1.0 additions
- "BUILD!" title when the countdown ends (preview countdown and /spb restart).
- Blocks broken during the preview countdown just disappear (nothing is given back).
- Live stopwatch on the action bar (above the hotbar) from the moment the build starts.
- On completion: custom title in the middle of the screen + final time as the subtitle.
  `/spb finish text <text>` (supports &-codes), `/spb finish color <name|#RRGGBB>`, `/spb finish test`, `/spb finish reset`.
