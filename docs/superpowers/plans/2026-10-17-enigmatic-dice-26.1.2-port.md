# Enigmatic Dice 26.1.2 Port Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Ship Enigmatic Dice on two branches — a cleaned-up `1.21.1` and a new `26.1.2` port that runs in-game next to Enigmatic Legacy Plus 26.1.2.

**Architecture:** Shared fixes (docs, license, Korean translation, data bugs) land on `1.21.1` first; `26.1.2` branches from there and gets the build-system swap, a compile-error-driven Java migration modelled on the Legacy Plus `26.1.2` branch, a resource-format migration, and runtime checks.

**Tech Stack:** NeoForge (21.1.187 → 26.1.2.122), ModDevGradle (2.0.99 → 2.0.148), Gradle (8.14.3 → 9.2.1), Java (21 → 25), Curios (9.5.1+1.21.1 → 15.0.0+26.1.2).

**Spec:** `docs/superpowers/specs/2026-10-17-enigmatic-dice-26.1.2-port-design.md`

## Global Constraints

- Repo: `/Users/delta/Desktop/Minecraft/Enigmatic-Dice-Unofficial-Port`; branches `1.21.1` and `26.1.2`; commits end with `Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`.
- `mod_id=enigmaticdice`, package `net.jrdemiurge.enigmaticdice` unchanged.
- `mod_authors=JrDemiurge, LukeNightstar`; `mod_license=MIT`.
- `26.1.2` versions: `minecraft_version=26.1.2`, `minecraft_version_range=[26.1.2]`, `neo_version=26.1.2.122`, `curios_version=15.0.0+26.1.2`, `mod_version=26.1.2-1.4.3`; mods.toml ranges: curios `[15.0.0+26.1.2,)`, enigmaticlegacyplus `[26.1.2-1.1.2,)`.
- No gameplay/balance/config-default changes except the spec's bug fixes. Never comment out or no-op a feature to make it compile; if a feature has no 26.1.2 equivalent, stop and report.
- Never commit any jar under `libs/` (already gitignored).
- Legacy Plus references are ID strings only; no compile dependency on Legacy Plus.
- API migration reference, in this order: Legacy Plus fork `/Users/delta/Desktop/Minecraft/Enigmatic-Legacy-Plus` (`git log main..26.1.2`, 32 commits) → decompiled MC/NeoForge sources `/Users/delta/Desktop/Minecraft/Enigmatic-Legacy-Plus/build/moddev/artifacts/minecraft-patched-26.1.2.122-sources.jar` → Curios 15 jar in `~/.gradle/caches/modules-2/files-2.1/top.theillusivec4.curios/curios-neoforge/15.0.0+26.1.2/`.
- Gradle runs: `1.21.1` with `JAVA_HOME=$(/usr/libexec/java_home -v 21)`; `26.1.2` with Java 25 via the toolchain (foojay resolver downloads it; JDK 26/27 are installed locally and may run Gradle itself).

## Review Focus

1. **Existing user config files** (`config/enigmatic_dice.json` copied by older versions) still contain the 4 removed `enigmaticdice_give_*` events and any other dead item IDs → the die must skip them with a WARN log, never hand out air. Test: Task 2 Step 5.
2. **Korean client with missing keys** → no raw `item.enigmaticdice.*` / `tooltip.*` keys visible. Test: Task 3 Step 2 key-coverage check (both branches) + Task 7 Step 4.
3. **Dedicated server** → no client-only class loaded on the server (renderers, `ClientLookController`, `ClientTickHandler`, `KyomuNameplateOverlay` leftovers). Test: Task 7 Step 2 `runServer`.
4. **Old world data** (Soul Eater / Unequal Exchange per-player data, Curios inventories) → after the port, NBT/attachment reads must use defaulting getters so a missing key does not crash. Test: Task 5 Step 4 review checklist + Task 7 Step 4 rejoin.
5. **Optional mods absent** (Alex's Mobs/Caves etc.) → summon events skip; Better Combat absent → `weapon_attributes` ignored without errors. Test: Task 7 Step 2 log grep (runs without those mods).

---

## Branch `1.21.1`

### Task 1: README, LICENSE, authors, drop unused jar dependency

**Files:**
- Create: `README.md`, `LICENSE`
- Modify: `gradle.properties` (`mod_authors`), `build.gradle` (remove `compileOnly files('libs/enigmaticlegacyplus-1.21.1-0.6.0.jar')` and the `flatDir { dirs 'libs' }` repo)

- [ ] **Step 1: Write `LICENSE`** — standard MIT text with two lines: `Copyright (c) 2025 JrDemiurge` and `Copyright (c) 2026 LukeNightstar (unofficial 1.21.1 / 26.1.2 port changes)`.
- [ ] **Step 2: Write `README.md`** in English, modelled on `/Users/delta/Desktop/Minecraft/Enigmatic-Legacy-Plus/README.md`: title `Enigmatic Dice — Unofficial Port (1.21.1 / 26.1.2)`; a quote box stating this is an unofficial port and the original is **[Demis-Enigmatic-Dice](https://github.com/JrDemiurg/Demis-Enigmatic-Dice)** by **[JrDemiurge](https://github.com/JrDemiurg)** under MIT, carried over unchanged; Credits table (original author / unofficial ports by [LukeNightstar](https://github.com/LukeNightstar)); "Branches" (`1.21.1`, `26.1.2`); "Requirements" (NeoForge, Curios, Enigmatic Legacy Plus — link `https://github.com/LukeNightstar/Enigmatic-Legacy-Plus` branch `26.1.2` for 26.1.2); "Modifications" list (filled by Tasks 2–3 and 8); "License" (MIT, see `LICENSE`).
- [ ] **Step 3: Set `mod_authors=JrDemiurge, LukeNightstar`; remove the unused jar dependency.**
- [ ] **Step 4: Build** — `rm -rf libs && JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew build --no-daemon` → `BUILD SUCCESSFUL` (proves the jar was unused).
- [ ] **Step 5: Commit** — `git add README.md LICENSE gradle.properties build.gradle && git commit -m "Add README and MIT license crediting JrDemiurge; drop unused Legacy Plus jar dependency"`

### Task 2: Data and dead-ID bug fixes

**Files:**
- Move: `src/main/resources/data/enigmaticdice/loot_tables/` → `loot_table/`, `recipes/` → `recipe/`
- Modify: the 4 loot table JSONs, `recipe/moon.json`, `item/custom/enigmaticdie/EternalBindingEvent.java:21`, `item/custom/enigmaticdie/RandomEventManager.java` (`loadItemEvents`, `loadResourceEvents`), `assets/enigmaticdice/config/enigmatic_dice.json`, all 5 lang files, `event/LivingHurtLowestHandler.java`
- Delete: `event/KyomuNameplateOverlay.java`; `models/item/{kyomu,phoenix,fire_crystal_greatsword,fire_crystal_spear}.json`; `textures/item/{kyomu_texture,kyomu_animated_texture,phoenix_texture,phoenix_animated_texture}.png` (+ `.mcmeta`), `textures/item/fire_crystal_{greatsword,spear}.png`; `data/enigmaticdice/weapon_attributes/{kyomu,phoenix,fire_crystal_greatsword,fire_crystal_spear}.json`

**Interfaces:**
- Produces: `RandomEventManager` skips any item/resource event whose `item` is not a registered item, logging `EnigmaticDice.LOGGER.warn("Skipping event {}: unknown item {}", name, id)`.

- [ ] **Step 1: Rename the folders with `git mv`; update loot JSONs to 1.21.1 format** — nested table entries use `"value"` instead of `"name"`; `enchant_with_levels` uses `"options": "#minecraft:on_random_loot"` instead of `"treasure": true`; check every other function/condition in the 4 files against the vanilla 1.21.1 format. Recipe result becomes `{"id": "enigmaticdice:moon", "count": 1}`. `DeathLootTable` strings in `WonderlandField.java:55` and `SummonDummyChest.java:61` stay as-is (IDs do not change).
- [ ] **Step 2: Eternal binding** — ID becomes `ResourceLocation.fromNamespaceAndPath("enigmaticlegacyplus", "eternal_binding_curse")`. Keep the config key `enigmaticlegacy_eternal_binding` unchanged (renaming would orphan user configs).
- [ ] **Step 3: Remove unfinished-weapon leftovers** — delete the 4 `enigmaticdice_give_{kyomu,phoenix,fire_crystal_greatsword,fire_crystal_spear}` entries from the default config JSON; delete files listed above; remove every lang key for those 4 items and `tooltip.enigmaticdice.unfinished_item_*` from all 5 lang files; delete the commented-out Kyomu code (whole `KyomuNameplateOverlay.java`, Kyomu block in `LivingHurtLowestHandler.java` — if that leaves the handler empty, delete the class and its registration).
- [ ] **Step 4: Add the unknown-item guard** in `loadItemEvents` and `loadResourceEvents` using `ResourceLocation.tryParse(config.item)` + `BuiltInRegistries.ITEM.containsKey(id)`; first confirm these run after registries are frozen (trace the `RandomEventManager` construction site); if they run earlier, put the same guard at the start of `GiveItemEvent.execute` / `triggerResourceEvent` instead (return `false` / pick another event).
- [ ] **Step 5: Verify**
  - `JAVA_HOME=$(/usr/libexec/java_home -v 21) ./gradlew build --no-daemon` → `BUILD SUCCESSFUL`.
  - `grep -rniE 'kyomu|phoenix|fire_crystal|unfinished' src/` → no output.
  - `find src -type d \( -name loot_tables -o -name recipes \)` → no output.
  - `python3 -c "import json,glob;[json.load(open(f)) for f in glob.glob('src/main/resources/**/*.json',recursive=True)]"` → no error.
  - Review the guard by reading: an `itemEvents` entry with `"item": "enigmaticdice:kyomu"` takes the skip branch.
- [ ] **Step 6: Commit** (two commits): `Fix 1.21 data folder names and loot/recipe formats; fix Eternal Binding enchantment id` and `Remove unfinished weapon leftovers; skip dice events with unknown item ids`. Add both to README "Modifications".

### Task 3: Korean translation

**Files:**
- Create: `src/main/resources/assets/enigmaticdice/lang/ko_kr.json`

- [ ] **Step 1: Translate every key of `en_us.json`** (same key order). Keep `§` codes, `%s`/`%1$s` placeholders and `\n` exactly. Reuse item/effect terms from `/Users/delta/Desktop/Minecraft/Enigmatic-Legacy-Plus/src/main/resources/assets/enigmaticlegacyplus/lang/ko_kr.json` and vanilla Korean names (e.g. 네더라이트, 엔더 진주).
- [ ] **Step 2: Verify coverage and placeholders**

```bash
python3 - <<'EOF'
import json,re
d='src/main/resources/assets/enigmaticdice/lang/'
en=json.load(open(d+'en_us.json')); ko=json.load(open(d+'ko_kr.json'))
assert set(en)==set(ko), (set(en)-set(ko), set(ko)-set(en))
ph=lambda s: sorted(re.findall(r'%(?:\d+\$)?[sd]|§.', s))
bad=[k for k in en if ph(en[k])!=ph(ko[k])]
assert not bad, bad
print('ok', len(ko))
EOF
```
Expected: `ok <N>`.
- [ ] **Step 3: Commit** — `Add Korean translation (ko_kr)`; README "Modifications" gets one line.

---

## Branch `26.1.2`

### Task 4: Branch and build system

**Files:**
- Modify: `build.gradle`, `settings.gradle`, `gradle.properties`, `gradle/wrapper/*`, `gradlew`, `gradlew.bat`, `src/main/templates/META-INF/neoforge.mods.toml`, `src/main/resources/enigmaticdice.mixins.json` (`compatibilityLevel`)

- [ ] **Step 1:** `git checkout -b 26.1.2` from `1.21.1` HEAD.
- [ ] **Step 2: Copy the build skeleton from Legacy Plus 26.1.2** — wrapper (Gradle 9.2.1) and `gradlew*`, ModDevGradle `2.0.148`, `java.toolchain.languageVersion = JavaLanguageVersion.of(25)`, no `parchment` block, `settings.gradle` plugin repos as in Legacy Plus. Keep Dice's own deps (Curios api compileOnly + runtimeOnly), runs, and `generateModMetadata`. Add `localRuntime` entries for the dev client: `files('libs/enigmaticlegacyplus-26.1.2-1.1.2.jar')` and Patchouli `maven.modrinth:patchouli:2CsnFLom` (with the Modrinth maven repo), so `runClient`/`runServer` load Legacy Plus. Set Global Constraints versions in `gradle.properties`/mods.toml; mixin `compatibilityLevel` to what Legacy Plus's mixin json uses.
- [ ] **Step 3: Provide the Legacy Plus jar** — `cp /Users/delta/Desktop/Minecraft/Enigmatic-Legacy-Plus/build/libs/enigmaticlegacyplus-26.1.2-1.1.2.jar libs/` (first confirm it was built from the fork's `26.1.2` HEAD: `git -C ../Enigmatic-Legacy-Plus log -1 --format=%cd` vs jar mtime; rebuild with `./gradlew build` there if older).
- [ ] **Step 4: Verify** — `./gradlew --version` shows Gradle 9.2.1; `./gradlew compileJava` gets past configuration and fails only with Java compile errors (expected at this point).
- [ ] **Step 5: Commit** — `Switch build to Minecraft 26.1.2 / NeoForge 26.1.2.122 / Java 25`.

### Task 5: Java source migration

**Files:** all of `src/main/java/net/jrdemiurge/enigmaticdice/**` (123 files, ~8.3k lines).

**Interfaces:**
- Consumes: Task 4 build. Produces: a tree that compiles on 26.1.2 with no `removal` warnings.

- [ ] **Step 1: Mechanical renames** across the tree (model each on the matching Legacy Plus commit): `ResourceLocation` → `net.minecraft.resources.Identifier`; `EventBusSubscriber(bus=…)` removed (mod-bus events auto-route); `Level#isClientSide` field → `isClientSide()`; `CompoundTag` getters → `getXOr(key, default)` / `getCompoundOrEmpty`; registry `get`/`getHolder` → `getValue`/`get` as in 26.1.2; `Item.Properties` require `setId(ResourceKey)` (use `DeferredRegister.Items#registerItem` as Legacy Plus does). Commit: `Mechanical API renames for 26.1.2`.
- [ ] **Step 2: Areas, one commit each, in this order** — (a) registries, config, commands, network payloads, attributes, effects, sounds, stats; (b) items and Curios items (`ICurioItem` 15.x API, attribute modifiers without `UUID`, tooltips, `use` → `InteractionResult`, tiers → `ToolMaterial`/components; Soul Eater / Unequal Exchange persistence → whichever store Legacy Plus uses for per-player data: `SavedDataType`+Codec or attachments); (c) dice events (teleport/structure/biome lookup, explosions, entity spawning `EntitySpawnReason`, potions, enchantment holders); (d) `DragonclawHook` entity, model/renderer to render-state API, model layers, client handlers, camera/look controller; (e) mixins (`LivingEntityMixin`, `PlayerMixin`, `EntityRendererAccessor`, `LivingEntityRendererMixin`) — re-verify every target method/descriptor against the 26.1.2 sources.
- [ ] **Step 3: Build** — `./gradlew build` → `BUILD SUCCESSFUL`; `./gradlew compileJava --rerun-tasks 2>&1 | grep -c '\[removal\]'` → `0`.
- [ ] **Step 4: Self-review checklist** (read the diff): no feature commented out or stubbed; every NBT read has a default; no client class referenced from common code paths (`@EventBusSubscriber(value = Dist.CLIENT)` or `ClientProxy`-style split as in Legacy Plus commit `6904d76`).

### Task 6: Resource migration

**Files:** `src/main/resources/assets/enigmaticdice/**`, `src/main/resources/data/**`

- [ ] **Step 1: Item model definitions** — create `assets/enigmaticdice/items/<id>.json` for every registered item (`{"model":{"type":"minecraft:model","model":"enigmaticdice:item/<id>"}}`, or the special forms Legacy Plus uses for items with predicates/animation); fix texture refs in `models/item/*.json` to the 26.1.2 form used by Legacy Plus commit `95ef7c0`. Note `four_leaf_clever.json` is the existing (misspelled) model name — keep IDs as registered.
- [ ] **Step 2: Data formats** — recipe ingredients become plain strings (`"S": "enigmaticdice:moon_shard"`); loot tables per 26.1.2; tags under `tags/item/`; Curios data (`data/enigmaticdice/curios/entities/slots.json`, `data/curios/tags/item/*.json`) in the Curios 15 layout that Legacy Plus uses (`data/<ns>/curios/{slots,entities}`); `pack.mcmeta` only if NeoForge 26.1.2 requires it.
- [ ] **Step 3: Verify** — `./gradlew build` → success; Python check: every registered item ID (grep `registerItem("…"` in `ModItems.java`) has `items/<id>.json`, its model file, and an `item.enigmaticdice.<id>` key in `en_us.json` and `ko_kr.json`.
- [ ] **Step 4: Commit** — `Migrate assets and data to 26.1.2 formats`.

### Task 7: Runtime verification

- [ ] **Step 1:** `./gradlew runServer` (accept EULA in `run/eula.txt`), wait for `Done (`; then server console `loot spawn 0 100 0 loot enigmaticdice:entities/mimic_loot_table` and `…/dummy_chest_loot_table` succeed; `stop`.
- [ ] **Step 2:** `grep -iE 'enigmaticdice|mixin.*(fail|error)|Couldn.t parse|Failed to load' run/logs/latest.log | grep -iE 'error|exception|warn'` → nothing attributable to Dice (Legacy Plus/Curios noise noted separately).
- [ ] **Step 3:** `./gradlew runClient`; drive it with Cua Driver (background, per AGENTS.md) — create a creative world, `/language`→ 한국어; open the Dice creative tab: all items have textures and Korean names; `/enigmaticDice <eventType> true` for a gift event, a summon event, a teleport event, an explosion event, `enigmaticlegacy_eternal_binding`; craft `moon` from 4 `moon_shard`; hold Soul Eater / Unequal Exchange / Crucible of Rile and hit a mob; equip Giant's Ring, Ring of Agility, Divine Shield, Gravity Core in Curios slots. Screenshot each.
- [ ] **Step 4:** Leave and rejoin the world; no errors; Soul Eater/Unequal Exchange state persists.
- [ ] **Step 5:** Fix anything found (commit per fix), re-run the failing step.

### Task 8: Publish

- [ ] **Step 1:** README: per-branch status, the 26.1.2 Modifications list, build instructions (`libs/` jar needed only for dev runs).
- [ ] **Step 2:** **Ask the user**, then `gh repo create LukeNightstar/Enigmatic-Dice-Unofficial-Port --public --source . --remote origin`, push both branches, set default branch `26.1.2`.
