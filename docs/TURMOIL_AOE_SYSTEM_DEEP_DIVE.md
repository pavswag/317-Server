# Turmoil AOE System Deep Dive

This document explains the current AOE tier system before reward changes are made. It uses `docs/TURMOIL_CONTENT_GUIDE.md`, `docs/TURMOIL_CONTENT_INDEX.md`, and `docs/TURMOIL_PROGRESSION_AUDIT.md` as context, then verifies the implementation from the AOE source and data files.

The short version: the AOE tier backbone already exists. Boss tier definitions, map definitions, saved unlocks, kill counts, NPC respawns, drop banking, end-of-run item rewards, and AOE weapon definitions are present. The safest first changes are JSON-only item reward changes in `data/aoe/aoe_tier_rewards.json`. Direct currency grants, real Fortune XP, and richer reward types need Java support.

## Important Findings

- `data/aoe/aoe_tier_rewards.json` currently supports item-id rewards through `bonusRewards`.
- Coins are already implemented as item rewards with item id `995`.
- Wraith Essence can be awarded as a JSON item reward with item id `26879`.
- Direct upgrade/foundry points are not supported by the reward JSON. They are stored on the player as `foundryPoints`.
- `fortuneXpPerKill` exists in `data/aoe/aoe_tier_rewards.json`, but the current hook calls `player.addDemonHunterXP`, not the Fortune skill XP method used by `src/io/xeros/content/fireofexchange/FireOfExchange.java` and `src/io/xeros/content/upgrade/UpgradeInterface.java`.
- The `rewards` object in `data/aoe/aoe_boss_tiers.json` is loaded into `AoeBossTierDef`, but no active consumer was found for `xpMultOnTask`, `xpMultOffTask`, `dropMult`, or `fortuneXp`. Not found in repo. Searched `rg -n "getRewards\\(|xpMultOnTask|xpMultOffTask|dropMult|fortuneXp" src/io/xeros/content/instances/aoe src/io/xeros`.
- `data/aoe/AoeZoneMapConfig.json` includes `npcs`, but active AOE spawns are built from `data/aoe/aoe_boss_tiers.json`. The map `npcs` list is only used as a fallback if a tier has no boss or minions.

## End-To-End Flow

1. Player opens the AOE tier menu through `src/io/xeros/content/commands/all/Bossinstance.java`, NPC option handling in `src/io/xeros/model/entity/player/packets/npcoptions/NpcOptionOne.java`, or starts directly through `src/io/xeros/content/commands/all/Aoe.java`.
2. `src/io/xeros/content/dialogue/impl/BossInstanceDialogue.java` reads tiers from `AoeTierRepo`, displays locked/unlocked status, and calls `AoeTierController.startTier`.
3. `src/io/xeros/content/instances/aoe/AoeTierController.java` checks the tier definition, disabled state, and player unlock.
4. `src/io/xeros/content/instances/aoe/AoeInstanceService.java` resolves the tier map id through `AoeZoneMaps`, reserves an instance height, copies map chunks, moves the player, registers the instance, and calls `AoeNpcSpawner.spawnForInstance`.
5. `src/io/xeros/content/instances/aoe/AoeNpcSpawner.java` builds spawn templates from the boss tier definition, assigns spawn points from the tier grid, spawns NPCs, tracks NPC indexes, and handles respawns.
6. `src/io/xeros/content/combat/death/NPCDeath.java` calls `AoeTierEvents.onNpcDeath`.
7. `src/io/xeros/content/instances/aoe/AoeTierEvents.java` increments tier kill count only when the dead NPC matches the tier boss NPC id.
8. `src/io/xeros/model/entity/npc/drops/DropManager.java` calls `AoeDropInterceptor.awardInsideAoe` during drop creation.
9. `src/io/xeros/content/instances/aoe/AoeDropInterceptor.java` auto-banks eligible drops and records them in `AoeRewardTracker`.
10. `AoeTierController.endTier` adds configured end-of-run item rewards, opens the reward viewer when requested, clears runtime attributes, and tears down the instance.

## File Deep Dive

### data/aoe/aoe_boss_tiers.json

- Purpose: Defines the AOE boss ladder: tier number, display name, unlock requirement, spawn grid, boss, minions, respawn timing, aggression, reward metadata, and map id.
- Important methods/classes: Loaded into `AoeBossTierDef` by `AoeBossTierLoader`.
- What data it loads or saves: It loads tier data only. It does not save player progress.
- How it connects to other AOE files: `AoeBossTierLoader` loads it, `AoeTierRepo` stores it, `BossInstanceDialogue` displays it, `AoeTierController` checks unlocks from it, and `AoeNpcSpawner` uses it to decide what NPCs to spawn.
- Safe extension points: Add a new tier object, adjust `unlockKills`, tune `aoeGrid`, adjust `aggroRange`, adjust `respawnSeconds`, set `boss`, set `minions`, and point `mapId` to an existing map config id.
- Dangerous areas to avoid: Do not assume `rewards.xpMultOnTask`, `rewards.xpMultOffTask`, `rewards.dropMult`, or `rewards.fortuneXp` are active. No active consumer was found. Do not remove `npcId` values unless NPC name resolution is tested. Missing NPC ids can disable a tier.

Current tiers:

- Tier 1: Unicow Pasture, boss `3601`, unlock after 10 kills, map `T1`.
- Tier 2: Bandos Stronghold, boss `2215`, unlock after 25 kills, map `T2`.
- Tier 3: Zamorak Fortress, boss `3129`, unlock after 35 kills, map `T3`.
- Tier 4: Saradomin Encampment, boss `2205`, unlock after 45 kills, map `T4`.
- Tier 5: Armadyl Eyrie, boss `3162`, unlock after 55 kills, map `T5`.
- Tier 6: Dagannoth Lair, boss `2266`, unlock after 65 kills, map `T6`.
- Tier 7: Kalphite Hive, boss `963`, unlock after 75 kills, map `T7`.
- Tier 8: King Black Dragon Lair, boss `239`, unlock after 85 kills, map `T8`.
- Tier 9: Chaos Elemental Plane, boss `2054`, unlock after 95 kills, map `T9`.

### data/aoe/aoe_tier_rewards.json

- Purpose: Defines per-tier reward behavior for AOE runs.
- Important methods/classes: Loaded into `AoeTierRewardsDef` by `AoeTierRewardsLoader`; consumed by `AoeTierController`, `AoeDropInterceptor`, and `AoeTierEvents`.
- What data it loads or saves: It loads reward definitions only. It does not save player progress.
- How it connects to other AOE files: `AoeDropInterceptor` reads `bankAllDrops`, `blacklist`, and `whitelist`; `AoeTierController.endTier` reads `endOfRunRolls`, `bonusRewards`, and `reportTitle`; `AoeTierEvents` reads `fortuneXpPerKill`.
- Safe extension points: Add item rewards to `bonusRewards`, tune `endOfRunRolls`, change `bankAllDrops`, add item ids to `blacklist`, add item ids to `whitelist`, and update `reportTitle`.
- Dangerous areas to avoid: Do not add unsupported fields and expect them to work. Direct player currencies, direct foundry points, direct boss points, and true Fortune XP are not implemented as JSON reward types.

Supported fields:

- `tier`: Integer tier id used by `AoeTierRewardsLoader.forTier`.
- `name`: Human-readable name.
- `endOfRunRolls`: Number of times the `bonusRewards` list is awarded when the player leaves or ends the tier with a report.
- `bonusRewards`: List of item rewards. Each entry supports `itemId`, `min`, and `max`.
- `bankAllDrops`: If true, eligible NPC drops are banked instead of falling to the ground.
- `blacklist`: Item ids excluded from AOE auto-banking.
- `whitelist`: If non-empty, only these item ids are AOE auto-banked.
- `fortuneXpPerKill`: Numeric field read on boss death, but currently grants Demon Hunter XP through `player.addDemonHunterXP`.
- `reportTitle`: Title passed to the loot viewer when `endTier` shows a report.

Current reward state:

- Tier 1 has no end-of-run bonus rewards.
- Tiers 2 through 9 each have one end-of-run roll.
- Tiers 2 through 9 currently award coins with item id `995`.
- Tiers 2 through 9 define `fortuneXpPerKill` from 50 through 120, but this is not currently true Fortune XP.

### data/aoe/AoeZoneMapConfig.json

- Purpose: Defines dynamic map copy source chunks, target base location, player spawn tile, and map NPC metadata for tier maps `T1` through `T9`.
- Important methods/classes: Loaded into `AoeZoneMapDef` by `AoeZoneMaps`.
- What data it loads or saves: It loads map construction data only. It does not save player progress.
- How it connects to other AOE files: `AoeInstanceService` looks up `mapId` through `AoeZoneMaps.forId`, uses `source`, `target`, and `spawn`, then passes the map to `AoeNpcSpawner`.
- Safe extension points: Add a new `tiers` entry for a new map id, adjust source chunk coordinates, target base coordinates, or spawn tile for a known working map.
- Dangerous areas to avoid: Do not treat the `npcs` list as the main spawn table. `AoeNpcSpawner` builds active spawns from `aoe_boss_tiers.json` boss and minions. The map `npcs` list is fallback metadata only in the current implementation.

Supported map fields:

- `id`: Map id such as `T1`.
- `source.fromX`, `source.fromY`, `source.width`, `source.height`, `source.z`: Chunk-copy source.
- `target.baseX`, `target.baseY`, `target.z`: Dynamic map target base.
- `spawn.x`, `spawn.y`, `spawn.z`: Player spawn tile after the map is copied.
- `npcs`: Static NPC metadata with `id`, `x`, `y`, `z`, `radius`, and optional `walk`.

### src/io/xeros/content/instances/aoe/AoeTierController.java

- Purpose: Main runtime controller for starting tiers, tracking active tier state, incrementing kills, unlocking tiers, ending tiers, and granting end-of-run item rewards.
- Important methods/classes: `getUnlockedTier`, `getKillCount`, `isUnlocked`, `setUnlockedTier`, `setKillCount`, `incrementKill`, `startTier`, `getActiveTier`, `getTracker`, `endTier`.
- What data it loads or saves: It stores runtime values in player attributes: `aoe_unlocked_tier`, `aoe_active_tier`, `aoe_reward_tracker`, `aoe_instance`, and `aoe_kc_<tier>`. Persistent save/load is delegated to `AoeTierProgressSaveEntry`.
- How it connects to other AOE files: Reads tier definitions from `AoeTierRepo`, starts maps through `AoeInstanceService`, uses `AoeRewardTracker`, reads rewards through `AoeTierRewardsLoader`, and tears down instances through `AoeInstanceService`.
- Safe extension points: Add additional end-of-run reward handling inside `endTier` if JSON schema is expanded. Add extra player messages or achievement hooks after successful unlocks.
- Dangerous areas to avoid: Do not bypass `startTier` or `endTier`. They set and clear the attributes that other AOE systems depend on. Do not grant unsupported currencies from JSON here without adding schema fields and tests.

### src/io/xeros/content/instances/aoe/AoeTierRewardsLoader.java

- Purpose: Loads `data/aoe/aoe_tier_rewards.json` into memory and provides lookup by tier.
- Important methods/classes: `load`, `reload`, `forTier`.
- What data it loads or saves: Loads a `List<AoeTierRewardsDef>`. It does not save data.
- How it connects to other AOE files: `AoeTierController`, `AoeDropInterceptor`, and `AoeTierEvents` call `forTier`.
- Safe extension points: Add validation for reward definitions, add logging for bad item ids, or support new fields after expanding `AoeTierRewardsDef`.
- Dangerous areas to avoid: Do not silently change the JSON path. Existing code expects `data/aoe/aoe_tier_rewards.json`.

### src/io/xeros/content/instances/aoe/AoeBossTierLoader.java

- Purpose: Loads boss tier definitions from disk, resolves missing NPC ids by name, creates a placeholder if the file is missing, and populates `AoeTierRepo`.
- Important methods/classes: `defaultFile`, `load`, `loadAllOrWarn`, `resolveNpcIds`, `resolveNpcIdByName`, `createPlaceholder`.
- What data it loads or saves: Loads `data/aoe/aoe_boss_tiers.json`, with fallback to `data/aoe_tiers.json`. It can write a placeholder file if neither exists.
- How it connects to other AOE files: Populates `AoeTierRepo`, which is then used by `BossInstanceDialogue`, `AoeTierController`, `AoeTierProgressSaveEntry`, and `AoeNpcSpawner`.
- Safe extension points: Add validation for duplicate tiers, missing map ids, or invalid unlock values.
- Dangerous areas to avoid: Be careful with placeholder creation. It writes to disk if tier files are missing. Do not depend on placeholder behavior for production content.

### src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java

- Purpose: Persists AOE progression through the modular `PlayerSaveEntry` system.
- Important methods/classes: `getKeys`, `decode`, `encode`, `login`.
- What data it loads or saves: Saves `aoe_unlocked_tier` and `aoe_kc_<tier>` for every loaded tier.
- How it connects to other AOE files: Calls `AoeBossTierLoader.loadAllOrWarn` if the tier repo is empty, enumerates tiers from `AoeTierRepo`, and reads/writes values through `AoeTierController`.
- Safe extension points: Add new AOE persistent keys by creating another `PlayerSaveEntry` or carefully extending this one.
- Dangerous areas to avoid: Do not move AOE progression into legacy `src/io/xeros/model/entity/player/save/PlayerSave.java`. The current pattern is safer and matches `docs/TURMOIL_CONTENT_GUIDE.md`.

### src/io/xeros/content/instances/aoe/AoeInstanceService.java

- Purpose: Builds dynamic AOE maps, moves the player into the instance, registers the instance, spawns NPCs, and tears the instance down.
- Important methods/classes: `buildAndEnter`, `handleMapBuilt`, `handleMapError`, `teardown`.
- What data it loads or saves: Reads map definitions through `AoeZoneMaps`; reserves and frees instance heights through `InstanceHeight`. It does not save player progression.
- How it connects to other AOE files: Called by `AoeTierController.startTier` and `AoeTierController.endTier`; reads `AoeZoneMapDef`; creates `AoeInstance`; registers in `AoeTierRepo`; spawns and despawns through `AoeNpcSpawner`.
- Safe extension points: Add better error messages, validation for missing map config, and test-only logging.
- Dangerous areas to avoid: Do not bypass `InstanceHeight.free` or `AoeTierRepo.clearInstance`. Leaked heights or stale instances can break future runs.

### src/io/xeros/content/instances/aoe/AoeDropInterceptor.java

- Purpose: Intercepts NPC drops while a player is inside an active AOE tier and banks eligible drops.
- Important methods/classes: `awardInsideAoe`, `setBankOverride`.
- What data it loads or saves: Reads active tier from `AoeTierController`, reads reward config from `AoeTierRewardsLoader`, adds items to `AoeRewardTracker`, and banks items through `player.getItems().addItemToBankOrDrop`.
- How it connects to other AOE files: Called from `src/io/xeros/model/entity/npc/drops/DropManager.java`; uses `AoeTierRewardsDef` filtering fields and `AoeRewardTracker`.
- Safe extension points: Add more filter behavior or tracking metadata if needed.
- Dangerous areas to avoid: Do not hardcode ordinary AOE drops here. Use normal drop tables for NPC drops and `data/aoe/aoe_tier_rewards.json` for AOE bonus item rewards.

### src/io/xeros/content/items/aoeweapons/AoeWeapons.java

- Purpose: Defines every AOE weapon id and its AOE behavior numbers.
- Important methods/classes: `AoeWeapons` enum entries.
- What data it loads or saves: No loading or saving. The enum stores item id, AOE radius, max damage, attack delay, animation id, graphic id, and style.
- How it connects to other AOE files: Loaded into `AOESystem`; read by `AoeManager`; referenced from combat code in `AttackEntity` and `HitDispatcher`.
- Safe extension points: Add new AOE weapon enum entries when the item exists and combat tuning is reviewed.
- Dangerous areas to avoid: Do not set extreme `Size`, `DMG`, or low `Delay` without testing NPC density and server performance. Wraith weapons are also listed here, so charge behavior must remain consistent with `src/io/xeros/content/wraith/WraithCharges.java`.

### src/io/xeros/content/items/aoeweapons/AoeManager.java

- Purpose: Applies AOE weapon splash damage during combat.
- Important methods/classes: `canAOE`, `castAOE`.
- What data it loads or saves: Reads equipped weapon, AOE weapon data, nearby NPCs, and optional legacy boss instance tier multipliers. It does not save data.
- How it connects to other AOE files: Gets weapon data from `AOESystem`; uses `AoeWeapons`; combat code calls `castAOE`.
- Safe extension points: Add restrictions, better zone checks, or combat balancing after verifying how AOE weapons should behave inside and outside AOE instances.
- Dangerous areas to avoid: `canAOE` checks `BossInstanceManager`, while the newer AOE tier instance system uses `AoeTierRepo`. Also, the restrictive `canAOE` check inside `castAOE` is currently commented out. Future agents should not assume AOE weapons are limited to AOE tier instances without testing combat behavior.

### src/io/xeros/content/items/aoeweapons/AOESystem.java

- Purpose: Singleton lookup table for AOE weapon enum data.
- Important methods/classes: `getSingleton`, `loadAOEDATA`, `getAOEData`.
- What data it loads or saves: Loads all `AoeWeapons.values()` into memory. It does not save data.
- How it connects to other AOE files: Called at startup from `src/io/xeros/ServerStartup.java`; used by `AoeManager`, `AttackEntity`, and `HitDispatcher`.
- Safe extension points: Add duplicate-load protection if needed before calling `loadAOEDATA` multiple times.
- Dangerous areas to avoid: Do not forget startup loading. If `loadAOEDATA` is not called, `getAOEData` will not find enum entries.

## Question Answers

### 1. How does a player enter an AOE instance?

- Via `src/io/xeros/content/commands/all/Bossinstance.java`, which opens `BossInstanceDialogue`.
- Via NPC option handling in `src/io/xeros/model/entity/player/packets/npcoptions/NpcOptionOne.java` for NPC `6599` and `Npcs.INSTANCE_MASTER`, which opens `BossInstanceDialogue`.
- Via `src/io/xeros/content/commands/all/Aoe.java` using `::aoe tier start <tier>`.
- Admins can also use `src/io/xeros/content/commands/admin/Testaoe.java`.

The shared entry point is `AoeTierController.startTier`.

### 2. How are AOE tiers defined?

Tiers are defined in `data/aoe/aoe_boss_tiers.json` and loaded into `AoeBossTierDef`. Each tier can define `tier`, `zoneName`, `unlockKills`, `aoeGrid`, `aggroRange`, `respawnSeconds`, `boss`, `minions`, `rewards`, and `mapId`.

### 3. How are tier unlocks saved?

`AoeTierProgressSaveEntry` saves `aoe_unlocked_tier` through `PlayerSaveEntry`. It reads and writes the value through `AoeTierController.getUnlockedTier` and `AoeTierController.setUnlockedTier`.

### 4. How are tier kills counted?

`NPCDeath.dropItemsFor` calls `AoeTierEvents.onNpcDeath`. If the player has an active AOE tier and the killed NPC id matches the tier boss NPC id, `AoeTierController.incrementKill` increments `aoe_kc_<tier>`. Minions do not count toward tier unlocks in the current hook.

### 5. How are rewards loaded?

`AoeTierRewardsLoader` loads `data/aoe/aoe_tier_rewards.json` at class load time. `AoeTierRewardsLoader.forTier` returns the reward definition for the active tier. `AoeTierController.endTier`, `AoeDropInterceptor`, and `AoeTierEvents` consume that definition.

### 6. What JSON fields are supported in aoe_tier_rewards.json?

Supported fields are `tier`, `name`, `endOfRunRolls`, `bonusRewards`, `bankAllDrops`, `blacklist`, `whitelist`, `fortuneXpPerKill`, and `reportTitle`. Each `bonusRewards` entry supports `itemId`, `min`, and `max`.

### 7. What reward types are currently supported?

- Item rewards through `bonusRewards`.
- Auto-banked normal NPC drops through `bankAllDrops`.
- Drop filtering through `blacklist` and `whitelist`.
- Loot report title through `reportTitle`.
- A numeric per-boss-kill XP field named `fortuneXpPerKill`, but it currently grants Demon Hunter XP, not Fortune XP.

### 8. Can the JSON support item rewards?

Yes. `bonusRewards` supports item id and random amount range. `AoeTierController.endTier` creates `GameItem` rewards and banks them.

### 9. Can the JSON support currency rewards?

Partially. It supports item-backed currencies because they are item ids. Coins work with item id `995`. Item currencies can work the same way if the item exists.

Direct player-field currencies are not supported. Direct currency reward schema: Not found in repo. Searched `rg -n "foundryPoints|FOUNDRY|foundry|upgrade points|Upgrade Points|WRAITH_ESSENCE|26879|addItemToBankOrDrop|addItem\\(" src/io/xeros/content/instances/aoe src/io/xeros/content/items/aoeweapons src/io/xeros/content/fireofexchange src/io/xeros/content/upgrade src/io/xeros/content/wraith`.

### 10. Can the JSON support Fortune XP?

Not correctly right now. `fortuneXpPerKill` exists, but `AoeTierEvents` calls `player.addDemonHunterXP`. True AOE Fortune XP support was not found in repo. Searched `rg -n "addSkillXPMultiplied\\(|addDemonHunterXP\\(|FORTUNE|getFortuneXpPerKill|fortuneXpPerKill|fortuneXp" src/io/xeros/content/instances/aoe src/io/xeros/content/fireofexchange src/io/xeros/content/upgrade src/io/xeros/model/entity/player/Player.java`.

### 11. Can the JSON support Wraith Essence?

Yes, as an item reward. Wraith Essence is item id `26879` in `src/io/xeros/content/wraith/WraithCharges.java`. Add it to `bonusRewards` as `itemId: 26879`.

### 12. Can the JSON support upgrade/foundry points?

Not as direct points. Upgrade/foundry points use `player.foundryPoints`, not an item id. JSON can award items that later burn into upgrade points, but it cannot directly increment `foundryPoints` without Java support.

### 13. If not supported, what code would need to change?

- Add fields to `AoeTierRewardsDef`, such as `foundryPointsMin`, `foundryPointsMax`, `bossPointsMin`, `bossPointsMax`, or `fortuneXp`.
- Add grant logic in `AoeTierController.endTier` for end-of-run currencies.
- Add grant logic in `AoeTierEvents.onNpcDeath` for per-kill XP or points.
- For true Fortune XP, use the pattern from `FireOfExchange` and `UpgradeInterface`: `player.getPA().addSkillXPMultiplied(amount, Skill.FORTUNE.getId(), true)`.
- Update the loot report if non-item rewards should be visible, because `AoeRewardTracker` currently tracks only item ids and amounts.

### 14. Where are AOE weapons defined?

AOE weapons are defined in `src/io/xeros/content/items/aoeweapons/AoeWeapons.java`. They are loaded into memory by `src/io/xeros/content/items/aoeweapons/AOESystem.java`.

### 15. How do AOE weapons interact with combat?

`AttackEntity` checks `AOESystem.getSingleton().getAOEData` for the equipped weapon and calls `AoeManager.castAOE`. `AoeManager.castAOE` applies animation, graphics, splash radius, random damage, and attack delay. `HitDispatcher` also recognizes AOE-style weapons in multi-hit logic.

Important risk: `AoeManager.canAOE` checks legacy `BossInstanceManager`, but `AoeTierController` instances are tracked in `AoeTierRepo`. The hard gate in `AoeManager.castAOE` is commented out, so AOE weapon behavior must be tested before assuming it only works in AOE tier instances.

### 16. Where should future AOE rewards be added safely?

- JSON-only item rewards: `data/aoe/aoe_tier_rewards.json`.
- New tiers or tier unlock pacing: `data/aoe/aoe_boss_tiers.json`.
- Map definitions for new map ids: `data/aoe/AoeZoneMapConfig.json`.
- New direct currency or true Fortune XP support: `src/io/xeros/content/instances/aoe/AoeTierRewardsDef.java`, `src/io/xeros/content/instances/aoe/AoeTierController.java`, and `src/io/xeros/content/instances/aoe/AoeTierEvents.java`.

### 17. What should future agents avoid rewriting?

- Do not rewrite `AoeTierController`, `AoeInstanceService`, `AoeNpcSpawner`, or `AoeDropInterceptor` for simple reward changes.
- Do not hardcode ordinary AOE drops in `DropManager` or `NPCDeath`.
- Do not expand legacy `PlayerSave.java` for new AOE data.
- Do not assume the `rewards` object in `aoe_boss_tiers.json` is active without adding code that consumes it.
- Do not create a second AOE command path without checking command registration. `src/io/xeros/content/instances/aoe/AoeDebug.java` also returns command name `aoe`, while `src/io/xeros/content/commands/all/Aoe.java` defaults to command name `aoe`; `CommandManager` uses `putIfAbsent`.

## A. Safe AOE Reward Changes That Are JSON-Only

- Add Wraith Essence as an end-of-run item reward in `data/aoe/aoe_tier_rewards.json` using item id `26879`.
- Add modest coins by changing item id `995` ranges.
- Add item-backed boxes, keys, materials, or shards if their item ids are known and already exist.
- Increase or reduce `endOfRunRolls`.
- Use `blacklist` to prevent specific NPC drops from being auto-banked.
- Use `whitelist` to only auto-bank specific drop ids.
- Update `reportTitle`.

## B. AOE Reward Changes That Require Java Support

- Direct upgrade/foundry point grants.
- Direct boss point grants.
- Direct vote point grants.
- True Fortune XP grants.
- Weighted reward tables.
- Rare broadcast messages for AOE reward rolls.
- Per-tier guaranteed milestone rewards after a kill count threshold.
- Non-item reward display in the loot viewer.
- Using `aoe_boss_tiers.json.rewards.dropMult` or XP multiplier fields.

## C. Best First AOE Patch

Add JSON-only tier 1 through tier 3 item rewards in `data/aoe/aoe_tier_rewards.json`.

Recommended shape:

- Tier 1: Set `endOfRunRolls` to 1 and add a low-value item reward.
- Tier 2: Keep coins and add a small Wraith Essence roll with item id `26879`.
- Tier 3: Keep coins and add a slightly larger Wraith Essence or upgrade-material item roll.

Why this first: it uses existing JSON support, requires no Java changes, and directly improves the weakest early AOE progression loop identified in `docs/TURMOIL_PROGRESSION_AUDIT.md`.

## D. Best Second AOE Patch

Fix and extend Java reward support.

Recommended shape:

- Correct `fortuneXpPerKill` in `AoeTierEvents` so it grants `Skill.FORTUNE` XP instead of Demon Hunter XP, or rename the field if Demon Hunter XP is intended.
- Add explicit optional foundry point fields to `AoeTierRewardsDef`.
- Grant direct foundry points from `AoeTierController.endTier`.
- Add non-item reward messaging so players understand what they received.

Why second: it unlocks real currency and account-progression rewards after the JSON-only reward pass proves the tier flow is stable.

## E. Test Checklist For AOE Tiers 1 Through 3

Before testing:

- Confirm `AOESystem.getSingleton().loadAOEDATA()` is still called from `src/io/xeros/ServerStartup.java`.
- Confirm `data/aoe/aoe_boss_tiers.json`, `data/aoe/aoe_tier_rewards.json`, and `data/aoe/AoeZoneMapConfig.json` are valid JSON.
- Confirm `AoeBossTierLoader.loadAllOrWarn` loads at least tiers 1 through 3.
- Confirm `AoeTierRewardsLoader.reload` loads reward definitions for tiers 1 through 3.

Tier 1:

- Start tier 1 through `::aoe tier start 1` or `::bossinstance`.
- Confirm the player moves to the configured AOE map.
- Confirm Unicow NPCs spawn.
- Kill the tier boss NPC and confirm `aoe_kc_1` increments.
- Leave through `::leaveaoe` and confirm end-of-run report behavior.
- Confirm normal drops are banked when `bankAllDrops` is true.

Tier 2:

- Unlock or admin-set tier 2.
- Start tier 2.
- Confirm General Graardor and minions spawn from `aoe_boss_tiers.json`.
- Kill the boss NPC and confirm `aoe_kc_2` increments.
- Leave through `::leaveaoe`.
- Confirm the configured coin reward from `bonusRewards` is banked.

Tier 3:

- Unlock or admin-set tier 3.
- Start tier 3.
- Confirm K'ril Tsutsaroth and minions spawn from `aoe_boss_tiers.json`.
- Kill the boss NPC and confirm `aoe_kc_3` increments.
- Leave through `::leaveaoe`.
- Confirm the configured reward range is applied once per `endOfRunRolls`.

Regression checks:

- Teleporting while inside an active AOE tier should call `AoeTierController.endTier` from `src/io/xeros/model/entity/player/PlayerAssistant.java`.
- Logging out should call `AoeTierController.endTier` from `src/io/xeros/model/entity/player/Player.java`.
- NPC deaths should still reach `AoeTierEvents.onNpcDeath` from `src/io/xeros/content/combat/death/NPCDeath.java`.
- Drops should still reach `AoeDropInterceptor.awardInsideAoe` from `src/io/xeros/model/entity/npc/drops/DropManager.java`.
- Instance teardown should despawn NPCs and free the reserved height through `AoeInstanceService.teardown`.
