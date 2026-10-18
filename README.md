# Enigmatic Dice — Unofficial Port (1.21.1 / 26.1.2)

> **This is an unofficial, modified port. I am not the author of Enigmatic Dice.**
>
> **Enigmatic Dice** was created by **[JrDemiurge](https://github.com/JrDemiurg)**.
> Original source: **[JrDemiurg/Demis-Enigmatic-Dice](https://github.com/JrDemiurg/Demis-Enigmatic-Dice)** (Minecraft 1.20.1, Forge).
> The original is released under the **MIT License**; this port keeps that license unchanged.

Enigmatic Dice is an addon for Enigmatic Legacy. It adds the Enigmatic Die, which triggers one of many random
events when rolled, along with powerful weapons and curio accessories.

## Credits

| Role | Who |
|---|---|
| Original author of Enigmatic Dice | [JrDemiurge](https://github.com/JrDemiurg) — [Demis-Enigmatic-Dice](https://github.com/JrDemiurg/Demis-Enigmatic-Dice) |
| Unofficial 1.21.1 and 26.1.2 ports (this repository) | [LukeNightstar](https://github.com/LukeNightstar) |

## Branches

| Branch | Minecraft | Loader | Requires |
|---|---|---|---|
| `1.21.1` | 1.21.1 | NeoForge 21.1 | Curios 9.5+, [Enigmatic Legacy Plus](https://github.com/Auviotre/Enigmatic-Legacy-Plus) 1.21.1 |
| `26.1.2` | 26.1.2 | NeoForge 26.1.2 | Curios 15.0.0+26.1.2, [Enigmatic Legacy Plus 26.1.2](https://github.com/LukeNightstar/Enigmatic-Legacy-Plus/tree/26.1.2) |

## Modifications

This repository **modifies** JrDemiurge's Enigmatic Dice (1.3.1-1.20.1). The modifications are:

- Porting the mod from Minecraft 1.20.1 / Forge to **Minecraft 1.21.1 / NeoForge** (branch `1.21.1`).
- Fixes carried into both branches:
  - Data folders renamed to the 1.21 names (`loot_table`, `recipe`) and loot tables/recipe converted to the
    component-based format, so the mimic / dummy chest loot and the Moon recipe load again.
  - The Eternal Binding die event now finds Legacy Plus's `enigmaticlegacyplus:eternal_binding_curse`.
  - The four unfinished placeholder weapons (Kyomu, Phoenix, Fire Crystal Greatsword, Fire Crystal Spear) were already
    unregistered; their leftover die events, models, textures, translations and weapon attributes are removed.
    Die events whose item does not exist (for example from an older config file) are skipped with a warning
    instead of dropping an empty item.
  - Korean translation (`ko_kr.json`), using the terms of the Legacy Plus Korean translation.
  - Giant's Ring and Moai Charm attribute bonuses (health, damage, speed, knockback resistance) are applied again;
    the 1.21.1 port overrode a Curios method that Curios no longer calls. Only one Giant's Ring counts, as in the
    original: a second one cannot be equipped, and removing one ring no longer strips the size bonus of the other.
- Porting the mod from Minecraft 1.21.1 / NeoForge 21.1 to **Minecraft 26.1.2 / NeoForge 26.1.2** (branch `26.1.2`),
  with Curios 15. Notable changes:
  - Swords are plain items with `Item.Properties#sword(ToolMaterial.NETHERITE, ...)`; tooltips, cooldowns, item
    ticking and curio hooks use their 26.1.2 signatures; item model definitions live in `assets/enigmaticdice/items`.
  - Curio attribute bonuses use Curios 15 default curio modifiers.
  - The day-cycle die event advances the overworld world clock (26.1 clocks are synced to clients), so the old
    client-side time packet is gone.
  - Size scaling (Giant's Ring, shrink events) scales the entity render state; player dimensions are adjusted on
    `Avatar`, where 26.1 moved them.
  - Mobs spawned by die events are edited through the value I/O API (mimic / dummy chest loot tables, charged creepers).
  - `@OnlyIn` annotations are removed (NeoForge 26.1 no longer strips members and warns about them).

Every individual change is recorded in the git history of each branch.

## Status

- `26.1.2`: builds; a dedicated server and a client (Korean) start without errors from this mod. Loot tables, the
  die events (forced through `/enigmaticDice <event> true`), curio bonuses, held-item effects and save/reload were
  checked in a dev environment. Melee weapon effects and other player-input features still need in-game play testing.
- `1.21.1`: builds.

## Building

`./gradlew build` produces `build/libs/enigmaticdice-<version>.jar`. The dev runs (`runClient`, `runServer`) on the
`26.1.2` branch expect a Legacy Plus 26.1.2 jar at `libs/enigmaticlegacyplus-26.1.2-1.1.2.jar` (not committed);
`./gradlew runClient -PquickPlayServer=localhost:25565` joins a running dev server directly. Add `-PwithBetterCombat` to
`runClient`/`runServer` to load Better Combat (and its dependencies) and test the Dice weapon attributes.

## License

MIT, the license of the original work. See [LICENSE](LICENSE).
