# Turmoil Data Config Loader Map

This map documents the data and configuration loaders currently visible in the Turmoil repo. It is meant to help future Codex tasks prefer data files, enums, and existing managers before touching core Java flow.

Rules for future content work:
- Do not rewrite loaders or core managers.
- Prefer repo-local data files under `data/` and `resources/` when the loader already supports them.
- For external runtime files, confirm the deployed data directory before balancing live content.
- Treat player state files, generated files, and save files as runtime state, not source content.
- When a file is listed as "Not found in repo", the loader references data outside the checked-in repo.

## Startup And Reload Overview

- Main startup file: `src/io/xeros/ServerStartup.java`
- Common reload command: `src/io/xeros/content/commands/owner/Reload.java`
- Admin reload helpers:
  - `src/io/xeros/content/commands/admin/Reloadhazards.java`
  - `src/io/xeros/content/commands/admin/Reloaddhxps.java`
  - `src/io/xeros/content/commands/admin/Reloadaoezone.java`
- AOE debug reload helpers:
  - `src/io/xeros/content/instances/aoe/AoeDebug.java`
  - `src/io/xeros/content/instances/aoe/AoeTierDebug.java`

Important startup load order:
- `src/io/xeros/model/definitions/ItemDef.java`
- `src/io/xeros/model/definitions/ShopDef.java`
- `src/io/xeros/model/world/ShopHandler.java`
- `src/io/xeros/model/definitions/NpcStats.java`
- `src/io/xeros/model/definitions/ItemStats.java`
- `src/io/xeros/model/definitions/NpcDef.java`
- `src/io/xeros/model/entity/npc/stats/NpcCombatDefinition.java`
- `src/io/xeros/model/entity/npc/drops/DropManager.java`
- `src/io/xeros/content/collection_log/CollectionLog.java`
- `src/io/xeros/model/entity/npc/NpcSpawnLoader.java`
- `src/io/xeros/model/entity/npc/NpcSpawnLoaderOSRS.java`
- `src/io/xeros/content/dailyrewards/DailyRewardContainer.java`
- `src/io/xeros/content/worldevent/WorldEventContainer.java`
- `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`

Reload support found:
- `dailyrewards`: reloads `src/io/xeros/content/dailyrewards/DailyRewardContainer.java`
- `doors`: reloads `src/io/xeros/model/collisionmap/doors/DoorDefinition.java`
- `drops`: reloads `src/io/xeros/model/entity/npc/drops/DropManager.java`
- `items`: reloads `src/io/xeros/model/definitions/ItemDef.java` and `src/io/xeros/model/definitions/ItemStats.java`
- `objects`: reloads global object runtime data through `src/io/xeros/model/world/objects/GlobalObjects.java`
- `shops`: rebuilds Fire of Exchange prices, `src/io/xeros/model/definitions/ShopDef.java`, and `src/io/xeros/model/world/ShopHandler.java`
- `npcs`: reinitializes `src/io/xeros/model/entity/npc/NPCHandler.java`; normal NPC JSON reload was not found
- `votes`: reloads `src/io/xeros/content/vote_panel/VotePanelManager.java`
- `hazards`: reloads `src/io/xeros/content/instances/hazard/WeeklyHazardManager.java`
- `demonhunter-xp`: reloads `src/io/xeros/content/skills/slayer/DemonHunterXPTable.java`

## Repo-Local Data And Resource Files

Checked-in gameplay data files:
- `data/aoe/aoe_boss_tiers.json`
- `data/aoe/aoe_tier_rewards.json`
- `data/aoe/AoeZoneMapConfig.json`
- `data/aoe_tiers.json`
- `demonhunter-xp.json`
- `resources/adaptive_traits.json`
- `resources/aoe_environmental_hazards.json`
- `resources/aoe_environmental_patterns.json`
- `resources/aoe_mutator_synergies.json`
- `resources/aoe_mutators.json`
- `resources/aoe_weekly_hazards.json`
- `resources/boss_instance_special_attacks.json`
- `resources/instance_rewards.json`

Checked-in non-gameplay or generated files:
- `config.yaml`
- `item_tooltips.json`
- `manifest.txt`
- `resources/logback.xml`
- `resources/META-INF/MANIFEST.MF`
- `sounds_todo.txt`

Build output copies under `build/resources/main/` are not source data files and should not be edited.

## Core Definition Loaders

### NPC Definitions

- Main loader file: `src/io/xeros/model/definitions/NpcDef.java`
- Data file path if found: Not found in repo. External runtime file referenced by loader: `npc/npc_definitions.json`. Searched terms: `npc_definitions.json`, `NpcDef.load`, `NpcDef.builder`.
- Data format: external runtime JSON
- What fields are supported: NPC id key, `name`, `combatLevel`, `size`, `runnable`. Runtime action fields exist in `NpcDef`, but the JSON builder loads the definition fields above.
- How the data is loaded: `NpcDef.load()` uses JSON parsing from the server data directory and builds an `Int2ObjectOpenHashMap<NpcDef>`.
- When the data is loaded: during startup in `src/io/xeros/ServerStartup.java`, after `ItemStats.load()` and before `NpcCombatDefinition.load()`.
- Whether it can be reloaded without server restart: No safe full reload found. `::reload npcs` reinitializes `NPCHandler`, but does not clearly reload `npc/npc_definitions.json`.
- Safe edits: edit names, combat levels, size, and runnable flags in the external runtime JSON after confirming live data.
- Risky edits: changing ids, names used by drops, boss points, slayer tasks, collection logs, or scripts without updating dependent systems.
- Example content using this loader: every NPC id resolved by `NpcDef.forId(int npcId)`.
- Future content types that should use this loader: new NPC definitions after the npc id is already supported by cache/client data.

### NPC Stats

- Main loader file: `src/io/xeros/model/definitions/NpcStats.java`
- Data file path if found: Not found in repo. External runtime file referenced by loader: `npc/npc_stats.json`. Searched terms: `npc_stats.json`, `NpcStats.load`, `scripts.combat`.
- Data format: external runtime JSON
- What fields are supported: `name`, `hitpoints`, `combatLevel`, `slayerLevel`, `attackSpeed`, `attackLevel`, `strengthLevel`, `defenceLevel`, `rangeLevel`, `magicLevel`, `stab`, `slash`, `crush`, `range`, `magic`, `stabDef`, `slashDef`, `crushDef`, `rangeDef`, `magicDef`, `bonusAttack`, `bonusStrength`, `bonusRangeStrength`, `bonusMagicDamage`, `poisonImmune`, `venomImmune`, `dragon`, `demon`, `undead`, `scripts`.
- How the data is loaded: `NpcStats.load()` parses the runtime JSON into the `NpcStats.stats` map. Startup then resolves `scripts.combat` to a `CombatMethod` through `DynamicClassLoader`.
- When the data is loaded: during startup before `NpcDef.load()` and before NPC combat definitions.
- Whether it can be reloaded without server restart: No safe reload found.
- Safe edits: stat tuning for existing NPCs when combat scripts and dependent ids are unchanged.
- Risky edits: script names, immunity flags, very large stats, or changes to demon/undead/dragon flags that affect slayer and gear bonuses.
- Example content using this loader: combat stats used by normal NPC combat calculations and scripted combat methods.
- Future content types that should use this loader: base combat stat tuning for bosses and monsters.

### NPC Combat Definitions

- Main loader file: `src/io/xeros/model/entity/npc/stats/NpcCombatDefinition.java`
- Data file path if found: Not found in repo. External runtime file referenced by loader: `npc/npc_combat_defs.json`. Searched terms: `npc_combat_defs.json`, `NpcCombatDefinition.load`, `NpcCombatDefinition.Builder`.
- Data format: external runtime JSON
- What fields are supported: `id`, `attackSpeed`, `attackStyle`, `aggressive`, `isPoisonous`, `isImmuneToPoison`, `isImmuneToVenom`, `isImmuneToCannons`, `isImmuneToThralls`, `levels`, `attackBonuses`, `defensiveBonuses`.
- How the data is loaded: `NpcCombatDefinition.load()` parses definitions into combat definition maps keyed by NPC id.
- When the data is loaded: during startup before `Server.npcHandler.init()`.
- Whether it can be reloaded without server restart: No safe reload found.
- Safe edits: standard attack speed, aggression, combat levels, and immunities for existing NPC ids.
- Risky edits: invalid enum keys in `levels`, `attackBonuses`, or `defensiveBonuses`; changing attack speed without checking boss scripts.
- Example content using this loader: NPC combat definition lookup during combat.
- Future content types that should use this loader: base NPC combat behavior that does not require custom special attacks.

### NPC Spawns

- Main loader file: `src/io/xeros/model/entity/npc/NpcSpawnLoader.java`
- Data file path if found: Not found in repo. External runtime files referenced by loader: `npc/npc_spawns.json` and files under `npc/spawns/`. Searched terms: `npc_spawns.json`, `npc/spawns`, `NpcSpawnLoader.load`.
- Data format: external runtime JSON
- What fields are supported: `id`, `position`, `walkingType`. `position` uses `src/io/xeros/model/entity/Position.java`.
- How the data is loaded: `NpcSpawnLoader.load()` reads the main spawn JSON and additional files, then creates NPC spawns through the server NPC handler.
- When the data is loaded: during startup after region, collection log, and object loading.
- Whether it can be reloaded without server restart: No safe full reload found.
- Safe edits: adding normal static NPC spawns in external runtime spawn data.
- Risky edits: duplicate boss spawns, invalid heights, wilderness or instance locations, or spawns that should be controlled by a minigame, world event, or activity boss manager.
- Example content using this loader: ordinary world NPC spawns.
- Future content types that should use this loader: static monsters, shops, and utility NPCs that are not instance-controlled.

### OSRS NPC Spawns

- Main loader file: `src/io/xeros/model/entity/npc/NpcSpawnLoaderOSRS.java`
- Data file path if found: Not found in repo. External runtime directory referenced by loader: `npc/osrsspawns/`. Searched terms: `osrsspawns`, `NpcSpawnLoaderOSRS`, `initOsrsSpawns`.
- Data format: external runtime JSON
- What fields are supported: OSRS spawn entries with NPC id and position fields.
- How the data is loaded: `NpcSpawnLoaderOSRS.initOsrsSpawns()` reads runtime OSRS spawn files and registers spawns.
- When the data is loaded: during startup immediately after `NpcSpawnLoader.load()`.
- Whether it can be reloaded without server restart: No safe reload found.
- Safe edits: importing static OSRS-style NPC spawns when ids are already supported.
- Risky edits: duplicating custom spawns or spawning content into instance maps.
- Example content using this loader: OSRS imported world spawns.
- Future content types that should use this loader: bulk static spawn imports only.

### Item Definitions

- Main loader file: `src/io/xeros/model/definitions/ItemDef.java`
- Data file path if found: Not found in repo. External runtime file referenced by loader: `item/item_definitions.yaml`. Searched terms: `item_definitions.yaml`, `ItemDef.load`, `ItemDef.builder`.
- Data format: external runtime YAML
- What fields are supported: `id`, `name`, `description`, `shopValue`, `noteId`, `noted`, `stackable`, `untradeable`, `checkBeforeDrop`, `undroppable`, `destroyable`, `equipmentModelType`, `requirements`, `parent`.
- How the data is loaded: `ItemDef.load()` parses YAML from the runtime item data directory.
- When the data is loaded: during startup before shops, NPC stats, and item stats.
- Whether it can be reloaded without server restart: Yes, through `::reload items`.
- Safe edits: text, shop value, note links, stackability, and trade/drop flags for existing ids after checking economy impact.
- Risky edits: changing item names used by shops, drops, upgrade materials, collection logs, or scripts.
- Example content using this loader: all item metadata resolved by `ItemDef.forId(int itemId)`.
- Future content types that should use this loader: metadata for custom items after cache/client item support exists.

### Item Stats

- Main loader file: `src/io/xeros/model/definitions/ItemStats.java`
- Data file path if found: Not found in repo. External runtime file referenced by loader: `item/item_stats.json`. Searched terms: `item_stats.json`, `ItemStats.load`, `ItemEquipmentStats`.
- Data format: external runtime JSON
- What fields are supported: `name`, `quest`, `equipable`, `weight`, `equipment`. Equipment fields include `slot`, `astab`, `aslash`, `acrush`, `amagic`, `arange`, `dstab`, `dslash`, `dcrush`, `dmagic`, `drange`, `str`, `rstr`, `mdmg`, `prayer`, `aspeed`.
- How the data is loaded: `ItemStats.load()` parses JSON into item stat maps.
- When the data is loaded: during startup after `NpcStats.load()` and before `NpcDef.load()`.
- Whether it can be reloaded without server restart: Yes, through `::reload items`.
- Safe edits: equipment stats and weight tuning for supported equipment.
- Risky edits: attack speed, equipment slot, magic damage, or large stat increases without combat and economy review.
- Example content using this loader: equipment bonuses used by combat and item equipping.
- Future content types that should use this loader: new equipment stat profiles for already-defined items.

### Item Definition Loader Cache Data

- Main loader file: `src/io/xeros/model/definitions/ItemDefinitionLoader.java`
- Data file path if found: Not found in repo. External runtime directory referenced by loader: `itemdata/`. Searched terms: `ItemDefinitionLoader.init`, `itemdata`.
- Data format: external runtime file
- What fields are supported: cache-backed item definition data managed by `ItemDefinitionLoader`.
- How the data is loaded: `ItemDefinitionLoader.init()` loads item data from the runtime data directory.
- When the data is loaded: during startup after global object loading.
- Whether it can be reloaded without server restart: No command-specific safe reload found beyond general item reloads for `ItemDef` and `ItemStats`.
- Safe edits: owner-reviewed cache support data only.
- Risky edits: cache mismatch, item id shifts, or definition edits that conflict with `ItemDef` and `ItemStats`.
- Example content using this loader: low-level item definition support.
- Future content types that should use this loader: cache-aligned item definition maintenance, not ordinary reward tuning.

## Economy And Reward Loaders

### Shops

- Main loader file: `src/io/xeros/model/definitions/ShopDef.java`
- Data file path if found: Not found in repo. External runtime directory referenced by loader: `shops/`. Searched terms: `ShopDef.load`, `shops`, `NamedShopItem`.
- Data format: external runtime YAML
- What fields are supported: `id`, `name`, `items`. Shop item fields support `id` or `name`, `amount`, and `price`.
- How the data is loaded: `ShopDef.load()` parses every shop YAML file and converts item names through `ItemConstants`. `src/io/xeros/model/world/ShopHandler.java` copies definitions into shop arrays.
- When the data is loaded: during startup before `ShopHandler.load()`.
- Whether it can be reloaded without server restart: Yes, through `::reload shops`.
- Safe edits: shop stock, item amounts, and listed prices for existing shops after currency review.
- Risky edits: adding powerful gear to easy shops, invalid item names, shop id collisions, or stock that bypasses intended boss/upgrade grinds.
- Example content using this loader: normal shop inventories opened by `ShopAssistant`.
- Future content types that should use this loader: item shops, point shops, and utility stores where currency handling already exists in Java.

### Drop Tables

- Main loader file: `src/io/xeros/model/entity/npc/drops/DropManager.java`
- Data file path if found: Not found in repo. External runtime directory referenced by loader: `drops/`. Searched terms: `DropManager.read`, `drops`, `TablePolicy`, `TableGroup`.
- Data format: external runtime YAML
- What fields are supported: `npc_id`, `constant`, `common`, `uncommon`, `rare`, `very_rare`, `extremely_rare`, `accessibility`, `items`, `item`, `name`, `minimum`, `maximum`.
- How the data is loaded: `DropManager.read()` scans the runtime drops directory, reads YAML nodes, builds `NpcDropTable` objects, and groups items by `TablePolicy`.
- When the data is loaded: during startup after NPC, shop, combat, and AOE initialization.
- Whether it can be reloaded without server restart: Yes, through `::reload drops`.
- Safe edits: ordinary boss and monster drop tables through YAML.
- Risky edits: hardcoding normal drops in Java, unsupported table names, missing `minimum` or `maximum`, invalid item names, or excessive rare drop quantities.
- Example content using this loader: NPC kill rewards, collection log rare drop hooks, and rare broadcasts.
- Future content types that should use this loader: new monster and boss drop tables when the reward is a normal NPC drop.

### Boss Points

- Main loader file: `src/io/xeros/content/bosspoints/BossPoints.java`
- Data file path if found: Not found in repo. External runtime file referenced by loader: `npc/boss_points.yaml`. Searched terms: `boss_points.yaml`, `BossPoints`, `BossPointEntry`.
- Data format: external runtime YAML
- What fields are supported: boss point entries with `name`, `points`, and `manual`.
- How the data is loaded: `BossPoints.load()` reads the YAML into a list of `BossPointEntry` values. `BossPoints.add(Player, NPC)` and `BossPoints.add(Player, String)` resolve matching entries.
- When the data is loaded: through the `@Init` method before normal startup loaders finish.
- Whether it can be reloaded without server restart: No dedicated reload found.
- Safe edits: point values for existing boss names and manual content labels.
- Risky edits: names that do not exactly match NPC definitions or manual labels, inflated point rates, or adding points to farmable low-risk NPCs.
- Example content using this loader: NPC death boss point awards and manual raid point awards.
- Future content types that should use this loader: new boss point entries for bosses that should participate in the boss point shop loop.

### Vote Panel Rewards

- Main loader file: `src/io/xeros/content/vote_panel/VotePanelManager.java`
- Data file path if found: Not found in repo. Runtime state file referenced by loader: `vote_panel.json`. Searched terms: `VotePanelManager.init`, `vote_panel.json`, `VotePanelInterface`, `Voted`.
- Data format: external runtime JSON state plus Java hardcoded rewards
- What fields are supported: state fields include vote user records, weekly top voter records, finish time, vote counts, streaks, blue points, red points, and prize slots. Reward ids and shop behavior are hardcoded in Java.
- How the data is loaded: `VotePanelManager.init()` loads or creates runtime state. Vote claims and vote shop actions update player and panel state.
- When the data is loaded: during startup after poll and AOE systems.
- Whether it can be reloaded without server restart: Yes, through `::reload votes`.
- Safe edits: runtime state recovery only with owner approval; reward balancing should be done in Java or shops depending on the reward path.
- Risky edits: manual vote state edits, weekly prize manipulation, or raising reward power without economy review.
- Example content using this loader: weekly vote panel progress and vote reward claiming.
- Future content types that should use this loader: vote participation tracking, not general reward catalog changes.

### Daily Rewards

- Main loader file: `src/io/xeros/content/dailyrewards/DailyRewardContainer.java`
- Data file path if found: Not found in repo. External runtime directory referenced by loader: `daily_rewards/`. Searched terms: `DailyRewardContainer.load`, `daily_rewards`, `DailyRewardMonth`.
- Data format: external runtime YAML
- What fields are supported: `identifier`, `date`, `rewards`. `date` is an int array in year, month, day order. Reward entries support `id` or `name` and `amount`.
- How the data is loaded: `DailyRewardContainer.load()` reads YAML configs, creates `DailyRewardMonth` values, and selects the current active reward month by date.
- When the data is loaded: during startup near the end of server initialization.
- Whether it can be reloaded without server restart: Yes, through `::reload dailyrewards`.
- Safe edits: rotating daily login rewards with conservative item quantities.
- Risky edits: high-value gear, strong boxes, excessive currencies, or invalid date identifiers.
- Example content using this loader: daily login reward calendar.
- Future content types that should use this loader: daily login calendars and seasonal login campaigns.

### Battlepass Rewards

- Main loader file: `src/io/xeros/content/battlepass/Rewards.java`
- Data file path if found: Not found in repo. External runtime files referenced by loader: `seasonpass/info.txt`, `seasonpass/memberRewards.txt`, `seasonpass/defaultRewards.txt`. Searched terms: `seasonpass`, `Rewards.init`, `memberRewards.txt`, `defaultRewards.txt`.
- Data format: external runtime TXT plus Java enum fallback
- What fields are supported: `info.txt` stores season timing fields. Reward files store `itemId : amount` lines. Generated defaults come from `src/io/xeros/content/battlepass/RewardList.java`.
- How the data is loaded: `Rewards.init()` reads season info and reward files. If files are absent, `Rewards.generateRewards()` creates them from `RewardList`.
- When the data is loaded: early startup, before player save entries are loaded.
- Whether it can be reloaded without server restart: No dedicated battlepass reward reload found.
- Safe edits: low-risk reward file changes between seasons after backing up runtime files.
- Risky edits: editing generated files mid-season, adding high-tier items, or changing `RewardList` without checking battlepass progression.
- Example content using this loader: free and member battlepass reward tracks.
- Future content types that should use this loader: seasonal pass reward rotations.

### Collection Log Config

- Main loader file: `src/io/xeros/content/collection_log/CollectionLog.java`
- Data file path if found: Not found in repo. External runtime file referenced by loader: `collection_npcs.json`. Searched terms: `collection_npcs.json`, `CollectionLog.init`, `CollectionTabType`.
- Data format: external runtime JSON
- What fields are supported: map of `CollectionTabType` to NPC id lists. Supported categories include `BOSSES`, `WILDERNESS`, `RAIDS`, `MINIGAMES`, and `OTHER`.
- How the data is loaded: `CollectionLog.init()` reads NPC category mappings and builds lookup structures used by drop logging.
- When the data is loaded: during startup after item definition support and before region/spawn loading.
- Whether it can be reloaded without server restart: No dedicated reload found.
- Safe edits: adding NPC ids to the correct collection log tab.
- Risky edits: removing active NPC ids, adding non-boss farm NPCs to completion categories, or editing player collection save files.
- Example content using this loader: NPC collection logs filled by rare drop events.
- Future content types that should use this loader: collection log grouping for new bosses and minigames.

### Collection Log Rewards

- Main loader file: `src/io/xeros/content/collection_log/CollectionRewards.java`
- Data file path if found: Not found in repo. Searched terms: `CollectionRewards`, `CollectionReward`, `CollectionRewardItems`.
- Data format: Java hardcoded enum
- What fields are supported: enum entries keyed by NPC id or special collection id, each with `GameItem[] Rewards`.
- How the data is loaded: enum constants are available when the class loads.
- When the data is loaded: class load time when collection rewards are referenced.
- Whether it can be reloaded without server restart: No.
- Safe edits: small Java enum additions for new collection completion rewards after economy review.
- Risky edits: using powerful rewards for easy logs or changing existing special ids.
- Example content using this loader: completion rewards for NPC logs, clue logs, pet logs, and upgrade logs.
- Future content types that should use this loader: collection completion rewards when a new collection log needs a one-time payout.

### Donation Rewards

- Main loader file: `src/io/xeros/content/donationrewards/DonationReward.java`
- Data file path if found: Not found in repo. External runtime file referenced by loader: `donation_rewards.json`. Searched terms: `donation_rewards.json`, `DonationReward.load`, `DonationRewards`.
- Data format: external runtime JSON
- What fields are supported: exactly six reward entries, each with `item` and `price`. The `item` value uses `GameItem` fields such as `id` and `amount`.
- How the data is loaded: `DonationReward.load()` reads the JSON into reward entries and validates the reward count.
- When the data is loaded: early startup before player save entries load.
- Whether it can be reloaded without server restart: No dedicated reload found.
- Safe edits: owner-approved donation reward rotation.
- Risky edits: changing reward count away from six, adding progression-breaking gear, or changing prices without store alignment.
- Example content using this loader: donation reward interface offers.
- Future content types that should use this loader: rotating donation offers only.

### Donator Deals And Offers

- Main loader files:
  - `src/io/xeros/content/deals/TimeOffers.java`
  - `src/io/xeros/content/deals/CosmeticDeals.java`
  - `src/io/xeros/content/deals/BonusItems.java`
  - `src/io/xeros/content/deals/AccountBoosts.java`
  - `src/io/xeros/content/donor/CosmeticManager.java`
- Data file path if found: Not found in repo. External runtime files referenced by loaders: `deals/timed_offers.yaml`, `deals/cosmetic_offers.yaml`, `deals/bonus_items.yaml`, `deals/weekly_time.yaml`, `deals/cosmetic_costs.json`. Searched terms: `timed_offers.yaml`, `cosmetic_offers.yaml`, `bonus_items.yaml`, `weekly_time.yaml`, `cosmetic_costs.json`.
- Data format: external runtime YAML and JSON
- What fields are supported: timed offers use `description`, `totalTime`, `itemIdToBuy`, `itemAmountToBuy`, and `rewards`. Bonus item offers use buy item lists and reward lists. Cosmetic costs map item ids to costs. Weekly time stores weekly donation timing.
- How the data is loaded: loaders marked with `@PostInit` read or create runtime YAML/JSON files.
- When the data is loaded: post-init after main startup loaders.
- Whether it can be reloaded without server restart: Some managers expose reload-style methods, but no universal owner command was confirmed.
- Safe edits: owner-approved cosmetic-only offer changes.
- Risky edits: donation economy, bonus item multipliers, paid progression gear, or manual runtime state changes.
- Example content using this loader: timed donation offers, cosmetic store costs, and bonus purchase rewards.
- Future content types that should use this loader: donation and cosmetic promotions only.

## AOE And Instance Loaders

### AOE Boss Tiers

- Main loader file: `src/io/xeros/content/instances/aoe/AoeBossTierLoader.java`
- Data file path if found: `data/aoe/aoe_boss_tiers.json`
- Data format: JSON
- What fields are supported: `tier`, `mapId`, `zoneName`, `unlockKills`, `aoeGrid`, `aggroRange`, `respawnSeconds`, `boss`, `minions`, `rewards`, `templateRegionId`, `templateX`, `templateY`, `widthChunks`, `heightChunks`, `rotation`, `z`, `spawnOffsetX`, `spawnOffsetY`, `useDynamicChunks`. Nested fields include grid `rows`, `cols`, `spacing`; boss and minion `name`, `npcId`, `count`; reward modifiers `xpMultOnTask`, `xpMultOffTask`, `dropMult`, `fortuneXp`.
- How the data is loaded: `AoeBossTierLoader.loadAllOrWarn()` reads JSON into `AoeBossTierDef` values and validates or disables invalid tier definitions.
- When the data is loaded: static loader initialization and during AOE startup paths.
- Whether it can be reloaded without server restart: Yes through AOE debug/admin reload paths.
- Safe edits: tier names, unlock kills, NPC ids, NPC counts, map template metadata, and conservative reward modifiers after testing tiers.
- Risky edits: invalid map chunks, spawn offsets, high NPC counts, unsupported NPC ids, or changing existing tier ids used by save data.
- Example content using this loader: AOE tier 1 and higher boss instance definitions.
- Future content types that should use this loader: new AOE tiers and AOE boss/minion rotations.

### Legacy AOE Tier Fallback

- Main loader file: `src/io/xeros/content/instances/aoe/AoeBossTierLoader.java`
- Data file path if found: `data/aoe_tiers.json`
- Data format: JSON
- What fields are supported: legacy fallback version of AOE tier definitions.
- How the data is loaded: used as fallback if the primary `data/aoe/aoe_boss_tiers.json` path is unavailable.
- When the data is loaded: same time as AOE boss tier loading.
- Whether it can be reloaded without server restart: Yes through the same AOE reload paths.
- Safe edits: avoid editing this when the primary file exists.
- Risky edits: diverging fallback data from primary data.
- Example content using this loader: fallback tier definitions.
- Future content types that should use this loader: emergency compatibility only.

### AOE Tier Rewards

- Main loader file: `src/io/xeros/content/instances/aoe/AoeTierRewardsLoader.java`
- Data file path if found: `data/aoe/aoe_tier_rewards.json`
- Data format: JSON
- What fields are supported: `tier`, `name`, `endOfRunRolls`, `bonusRewards`, `bankAllDrops`, `blacklist`, `whitelist`, `fortuneXpPerKill`, `reportTitle`. Bonus rewards support `itemId`, `min`, and `max`.
- How the data is loaded: `AoeTierRewardsLoader.load()` reads JSON tier reward configs into `AoeTierRewardsLoader.TierRewardConfig` values. `reload()` is available.
- When the data is loaded: static initialization and AOE reward service usage.
- Whether it can be reloaded without server restart: Yes through AOE debug/admin reload paths.
- Safe edits: item bonus rewards, blacklists, whitelists, end-of-run roll counts, and conservative per-kill XP values.
- Risky edits: assuming unsupported currency reward fields work, excessive roll counts, invalid item ids, or blacklisting core progression drops.
- Example content using this loader: AOE tier end-of-run item rewards.
- Future content types that should use this loader: JSON-only AOE item reward tuning.

### AOE Zone Maps

- Main loader file: `src/io/xeros/content/instances/aoe/AoeZoneMaps.java`
- Data file path if found: `data/aoe/AoeZoneMapConfig.json`
- Data format: JSON
- What fields are supported: root `tiers`; each tier supports `id`, `source`, `target`, `spawn`, and `npcs`. Source fields include `fromX`, `fromY`, `width`, `height`, `z`. Target and spawn fields include coordinates and height. NPC fields include `id`, `x`, `y`, `z`, `radius`, and `walk`.
- How the data is loaded: `AoeZoneMaps.reload()` reads the JSON config and stores zone map definitions by tier id.
- When the data is loaded: static initialization and AOE zone map usage.
- Whether it can be reloaded without server restart: Yes through AOE reload support.
- Safe edits: map copy coordinates and NPC layout after local instance tests.
- Risky edits: source/target region mismatch, overlapping generated maps, invalid spawn coordinates, or NPC spawns outside the copied area.
- Example content using this loader: legacy AOE zone map layouts.
- Future content types that should use this loader: AOE map templates and static AOE NPC placement.

### AOE Weapons

- Main loader file: `src/io/xeros/content/items/aoeweapons/AoeWeapons.java`
- Data file path if found: Not found in repo. Searched terms: `AoeWeapons`, `AOESystem`, `AoeManager`, `loadAOEDATA`.
- Data format: Java hardcoded enum
- What fields are supported: AOE weapon enum entries define item id and combat behavior fields used by `src/io/xeros/content/items/aoeweapons/AoeManager.java` and `src/io/xeros/content/items/aoeweapons/AOESystem.java`.
- How the data is loaded: enum constants and `AOESystem.getSingleton().loadAOEDATA()` initialize AOE weapon behavior.
- When the data is loaded: during startup in `src/io/xeros/ServerStartup.java`.
- Whether it can be reloaded without server restart: No data reload found.
- Safe edits: small Java enum additions only after combat testing.
- Risky edits: changing radius, damage, attack delay, animations, or weapon ids without checking combat balance.
- Example content using this loader: existing AOE weapons used by AOE combat.
- Future content types that should use this loader: new AOE weapons only when a Java code change is explicitly requested.

### Instance Rewards

- Main loader file: `src/io/xeros/content/instances/InstanceRewardLoader.java`
- Data file path if found: `resources/instance_rewards.json`
- Data format: JSON
- What fields are supported: map keys matching performance ranks such as `bronze`, `silver`, `gold`, `platinum`, and `diamond`; values are lists of `GameItem` entries with `id` and `amount`.
- How the data is loaded: static class initialization reads the classpath resource into `Map<PerformanceRank, List<GameItem>>`.
- When the data is loaded: when `InstanceRewardLoader` is first referenced by instance reward code.
- Whether it can be reloaded without server restart: No reload found.
- Safe edits: conservative instance reward item lists.
- Risky edits: high-tier gear, excessive currencies, or invalid performance rank keys.
- Example content using this loader: instance performance rewards in `src/io/xeros/content/instances/BossInstanceManager.java`.
- Future content types that should use this loader: ranked instance reward chests and end-of-run payouts.

### Boss Instance Special Attacks

- Main loader file: `src/io/xeros/content/instances/NpcSpecialAttackLoader.java`
- Data file path if found: `resources/boss_instance_special_attacks.json`
- Data format: JSON
- What fields are supported: `name`, `activationChance`, `cooldown`, `animation`, `gfx`, `sound`, `effect`, `messages`.
- How the data is loaded: static loader reads the resource file and creates `NpcSpecialAttack` definitions.
- When the data is loaded: when the special attack loader class is first referenced.
- Whether it can be reloaded without server restart: No reload found.
- Safe edits: tuning activation chance, cooldown, messages, and existing effect references.
- Risky edits: unsupported effect names, broken animations/gfx, or high-frequency unavoidable damage.
- Example content using this loader: boss instance special attack definitions selected by name.
- Future content types that should use this loader: configurable special attacks for instance bosses where Java already knows how to apply the named effect.

### AOE Mutators

- Main loader file: `src/io/xeros/content/instances/InstanceMutatorManager.java`
- Data file paths if found:
  - `resources/aoe_mutators.json`
  - `resources/aoe_mutator_synergies.json`
- Data format: JSON
- What fields are supported: `aoe_mutators.json` maps `InstanceMutator` enum names to rarity metadata. `aoe_mutator_synergies.json` supports synergy entries with `mutators` and `name`.
- How the data is loaded: static initialization reads classpath resources into mutator rarity and synergy maps.
- When the data is loaded: when `InstanceMutatorManager` is first referenced.
- Whether it can be reloaded without server restart: No reload found.
- Safe edits: rarity tuning and synergy naming for existing enum mutators.
- Risky edits: adding new mutator names that are not in the Java enum, or creating synergies with unsupported behavior.
- Example content using this loader: AOE/instance mutator selection.
- Future content types that should use this loader: balancing existing mutator weights and synergy labels.

### Environmental Hazards

- Main loader file: `src/io/xeros/content/instances/hazard/EnvironmentalHazardLoader.java`
- Data file path if found: `resources/aoe_environmental_hazards.json`
- Data format: JSON
- What fields are supported: boss tier keys mapping to hazard configs with `frequency` and `hazards`. Hazard definitions support `type`, `damage`, `drain`, `stun`, `duration`, `triggerCondition`, `cooldownWindow`, and `tier`.
- How the data is loaded: static initialization reads the classpath resource into tier hazard configs.
- When the data is loaded: when the hazard loader is first referenced.
- Whether it can be reloaded without server restart: No reload found for this loader.
- Safe edits: hazard frequency and numeric tuning for existing hazard types.
- Risky edits: unsupported hazard types, excessive damage/drain, or hazards that overlap unavoidable boss mechanics.
- Example content using this loader: AOE environmental hazard configs.
- Future content types that should use this loader: hazard tuning for existing instance tiers.

### Environmental Hazard Patterns

- Main loader file: `src/io/xeros/content/instances/hazard/EnvironmentalHazardPatternLoader.java`
- Data file path if found: `resources/aoe_environmental_patterns.json`
- Data format: JSON
- What fields are supported: boss tier keys with `frequency` and `patterns`. Pattern types include known enum values such as `FLAME_RING`, `VOID_PULSE`, `TOXIC_TORRENT`, `SHOCKWAVE`, and `CHAOS_RIFT`.
- How the data is loaded: static initialization reads classpath JSON and normalizes tier keys before storing pattern configs.
- When the data is loaded: when the pattern loader is first referenced.
- Whether it can be reloaded without server restart: No reload found.
- Safe edits: frequency and existing pattern lists.
- Risky edits: unsupported pattern names or too many simultaneous patterns.
- Example content using this loader: AOE hazard pattern rotations.
- Future content types that should use this loader: visual and positional hazard pattern tuning.

### Weekly Hazards

- Main loader file: `src/io/xeros/content/instances/hazard/WeeklyHazardManager.java`
- Data file path if found: `resources/aoe_weekly_hazards.json`
- Data format: JSON
- What fields are supported: `staticHazards`, `synergyMutators`, and `eliteHazard`.
- How the data is loaded: `WeeklyHazardManager.load()` reads the resource file into weekly hazard state.
- When the data is loaded: static initialization and admin reloads.
- Whether it can be reloaded without server restart: Yes, through `src/io/xeros/content/commands/admin/Reloadhazards.java`.
- Safe edits: weekly hazard rotation values for existing hazard and mutator names.
- Risky edits: unsupported names or elite hazards that make early AOE tiers inaccessible.
- Example content using this loader: weekly AOE hazard modifiers.
- Future content types that should use this loader: weekly instance modifier rotations.

### Adaptive Traits

- Main loader file: `src/io/xeros/model/entity/npc/AdaptiveTraitLoader.java`
- Data file path if found: `resources/adaptive_traits.json`
- Data format: JSON
- What fields are supported: adaptive trait definitions with fields such as `name` and `description`.
- How the data is loaded: `AdaptiveTraitLoader` reads `resources/adaptive_traits.json` into adaptive trait objects.
- When the data is loaded: when adaptive NPC trait code is first referenced.
- Whether it can be reloaded without server restart: No reload found.
- Safe edits: descriptions or metadata for existing adaptive trait names.
- Risky edits: adding trait names without behavior support.
- Example content using this loader: adaptive NPC or instance behavior traits.
- Future content types that should use this loader: metadata for existing adaptive trait behaviors.

## Progression And Task Loaders

### Slayer Tasks

- Main loader file: `src/io/xeros/content/skills/slayer/SlayerMaster.java`
- Data file path if found: Not found in repo. External runtime file referenced by loader: `cfg/slayer_masters.json`. Searched terms: `slayer_masters.json`, `SlayerMaster`, `Task`.
- Data format: external runtime JSON
- What fields are supported: slayer master entries with `id`, `level`, `pointRewards`, and `available`. Task entries support `names`, `level`, `experience`, `minimum`, `maximum`, `locations`, and `teleport`.
- How the data is loaded: static initialization reads the JSON array into `SlayerMaster` values.
- When the data is loaded: when `SlayerMaster` is first referenced by slayer code.
- Whether it can be reloaded without server restart: No reload found.
- Safe edits: adding task entries that match existing NPC names and teleport coordinates.
- Risky edits: malformed `pointRewards`, invalid teleports, names that do not match NPC definitions, or high XP/point values for easy NPCs.
- Example content using this loader: standard Slayer task assignment pools.
- Future content types that should use this loader: new Slayer task targets for existing masters.

### Demon Hunter Tasks

- Main loader file: `src/io/xeros/content/skills/slayer/DemonSlayerMaster.java`
- Data file path if found: Not found in repo. Searched terms: `DemonSlayerMaster`, `DemonSlayerTask`, `BossTier`.
- Data format: Java hardcoded enum
- What fields are supported: Java `Tier` and `BossTier` enums define available Demon Hunter boss tiers, level requirements, and task targets.
- How the data is loaded: enum constants are available when Demon Hunter code loads.
- When the data is loaded: class load time.
- Whether it can be reloaded without server restart: No.
- Safe edits: Java-only task additions with save and balance review.
- Risky edits: changing tier ids or level requirements without checking player progression and saved tasks.
- Example content using this loader: Demon Hunter boss task assignment.
- Future content types that should use this loader: new Demon Hunter boss tasks only when a Java change is intended.

### Demon Hunter XP Table

- Main loader file: `src/io/xeros/content/skills/slayer/DemonHunterXPTable.java`
- Data file path if found: `demonhunter-xp.json`
- Data format: JSON
- What fields are supported: entries with `tier` and `xpMultiplier`.
- How the data is loaded: static load reads the JSON file into XP multiplier records.
- When the data is loaded: class load time, and on explicit reload.
- Whether it can be reloaded without server restart: Yes, through `src/io/xeros/content/commands/admin/Reloaddhxps.java`.
- Safe edits: XP multiplier tuning for existing Demon Hunter tiers.
- Risky edits: missing tier entries, invalid tier names, or huge multipliers.
- Example content using this loader: Demon Hunter XP scaling.
- Future content types that should use this loader: Demon Hunter XP balancing, not task catalog changes.

### Task Master Catalog

- Main loader file: `src/io/xeros/content/taskmaster/Tasks.java`
- Data file path if found: Not found in repo. Searched terms: `Tasks.java`, `TaskMaster`, `TaskMasterKills`.
- Data format: Java hardcoded enum
- What fields are supported: enum entries with `max`, `desc`, `daily`, `wildy`, `difficultyType`, and `taskType`.
- How the data is loaded: enum constants are available at class load.
- When the data is loaded: class load time.
- Whether it can be reloaded without server restart: No.
- Safe edits: Java enum additions that follow existing task types.
- Risky edits: changing enum names used by player task state, inflating max counts, or editing player task JSON.
- Example content using this loader: daily and weekly Task Master assignments.
- Future content types that should use this loader: new Task Master tasks only with Java changes.

### Task Master Player State

- Main loader file: `src/io/xeros/content/taskmaster/TaskMaster.java`
- Data file path if found: Not found in repo. Runtime player files referenced by loader: `taskmaster/<player>.json`. Searched terms: `taskmaster`, `TaskMaster.load`, `TaskMasterKills`.
- Data format: external runtime JSON state
- What fields are supported: task state includes `items`, `amountToKill`, `claimedReward`, `amountKilled`, `taskDifficulty`, `taskType`, `weekly`, `localDateTime`, and `desc`.
- How the data is loaded: `TaskMaster.load()` reads per-player runtime task state.
- When the data is loaded: when player task master state is needed.
- Whether it can be reloaded without server restart: player state loads through normal player/task flows.
- Safe edits: none for content balancing.
- Risky edits: manual player state manipulation.
- Example content using this loader: assigned Task Master progress.
- Future content types that should use this loader: none; use `src/io/xeros/content/taskmaster/Tasks.java` for catalog changes.

### Achievement Catalog

- Main loader file: `src/io/xeros/content/achievement/Achievements.java`
- Data file path if found: Not found in repo. Searched terms: `Achievements.java`, `AchievementHandler`, `AchievementType`.
- Data format: Java hardcoded enum
- What fields are supported: achievement enum metadata and progress requirements defined in Java.
- How the data is loaded: enum constants and achievement handlers are available at class load.
- When the data is loaded: class load time.
- Whether it can be reloaded without server restart: No.
- Safe edits: Java enum additions that copy existing achievement type patterns.
- Risky edits: changing enum order or identifiers used by player save data.
- Example content using this loader: achievement progress and rewards.
- Future content types that should use this loader: new achievements when Java code changes are approved.

### Prestige System

- Main loader file: Not found in repo. Searched terms: `Prestige`, `prestige points`, `prestigePoints`, `prestige`.
- Data file path if found: Not found in repo.
- Data format: not found
- What fields are supported: Not found in repo.
- How the data is loaded: Not found in repo.
- When the data is loaded: Not found in repo.
- Whether it can be reloaded without server restart: Not found in repo.
- Safe edits: Not found in repo.
- Risky edits: do not invent a prestige data loader without first locating the live implementation.
- Example content using this loader: Not found in repo.
- Future content types that should use this loader: Not found in repo.

## Upgrade, Fusion, Sink, And Weapon Systems

### Upgrade Materials

- Main loader file: `src/io/xeros/content/upgrade/UpgradeMaterials.java`
- Data file path if found: Not found in repo. Searched terms: `UpgradeMaterials`, `UpgradeType`, `UpgradeInterface`.
- Data format: Java hardcoded enum
- What fields are supported: each enum entry defines `UpgradeType`, `levelRequired`, `required`, `reward`, `cost`, `successRate`, `xp`, and `rare`.
- How the data is loaded: enum constants are available when upgrade code is referenced.
- When the data is loaded: class load time.
- Whether it can be reloaded without server restart: No.
- Safe edits: adding upgrade recipes by copying existing enum entries with conservative rates and costs.
- Risky edits: modifying existing recipe ids, lowering costs dramatically, or creating upgrade loops that print value.
- Example content using this loader: weapon, armour, accessory, and misc upgrades.
- Future content types that should use this loader: item upgrade recipes and upgrade collection log entries.

### Fusion Materials

- Main loader file: `src/io/xeros/content/fusion/FusionMaterials.java`
- Data file path if found: Not found in repo. Searched terms: `FusionMaterials`, `FusionTypes`, `Fusion`.
- Data format: Java hardcoded enum
- What fields are supported: each enum entry defines `FusionTypes`, `levelRequired`, required `GameItem[]`, reward `GameItem`, `cost`, `xp`, and `rare`.
- How the data is loaded: enum constants are available at class load.
- When the data is loaded: class load time.
- Whether it can be reloaded without server restart: No.
- Safe edits: adding fusion recipes only when item sink math has been reviewed.
- Risky edits: recipes that bypass upgrades, consume the wrong item ids, or create profitable loops.
- Example content using this loader: fusion item recipes.
- Future content types that should use this loader: fusion recipes that combine multiple items into a new reward.

### Fire Of Exchange Burn Prices

- Main loader file: `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`
- Data file path if found: Not found in repo. Searched terms: `FireOfExchangeBurnPrice`, `getBurnPrice`, `createBurnPriceShop`.
- Data format: Java hardcoded switch plus upgrade-material fallback
- What fields are supported: hardcoded item id to burn price cases; fallback checks matching `UpgradeMaterials` rewards and uses part of the upgrade cost.
- How the data is loaded: Java constants and switch logic are available at class load. `init()` and `createBurnPriceShop()` build display data.
- When the data is loaded: startup calls `FireOfExchangeBurnPrice.init()`.
- Whether it can be reloaded without server restart: shop display can be rebuilt through `::reload shops`; burn price logic itself requires Java changes.
- Safe edits: adding specific burn prices in Java only after economy review.
- Risky edits: broad fallback changes, overvaluing easy drops, or changing Wraith Essence value without Wraith review.
- Example content using this loader: Fire of Exchange item sink values.
- Future content types that should use this loader: new item sink values for upgrade outputs and boss drops.

### Fire Of Exchange Burn Flow

- Main loader file: `src/io/xeros/content/fireofexchange/FireOfExchange.java`
- Data file path if found: Not found in repo. Searched terms: `FireOfExchange`, `burn`, `FireOfExchangeBurnPrice`.
- Data format: Java hardcoded behavior
- What fields are supported: burn actions, points, and shop behavior are controlled by Java.
- How the data is loaded: not a data loader; uses `FireOfExchangeBurnPrice`.
- When the data is loaded: class load and startup initialization paths.
- Whether it can be reloaded without server restart: No for behavior.
- Safe edits: none for data-only work.
- Risky edits: changing burn flow, point awarding, or item deletion logic.
- Example content using this loader: item burn sink.
- Future content types that should use this loader: no data-only content; use burn price entries when Java changes are allowed.

### Wraith Charges

- Main loader file: `src/io/xeros/content/wraith/WraithCharges.java`
- Data file path if found: Not found in repo. Searched terms: `WraithCharges`, `WRAITH_ESSENCE`, `chargesPerEssence`.
- Data format: Java hardcoded constants
- What fields are supported: Wraith Essence item id, Wraith weapon ids, charge cap, and charges-per-essence logic.
- How the data is loaded: constants and helper methods are available at class load.
- When the data is loaded: class load time.
- Whether it can be reloaded without server restart: No.
- Safe edits: Java-only additions with save compatibility review.
- Risky edits: changing charge caps, essence value, or weapon ids without checking player save entries and combat.
- Example content using this loader: Wraith weapon charge behavior.
- Future content types that should use this loader: Wraith weapon support only when Java changes are explicitly requested.

## Teleport And Navigation Loaders

### Teleport Interface

- Main loader file: `src/io/xeros/content/teleportv2/inter/TeleportInterface.java`
- Data file path if found: Not found in repo. Searched terms: `TeleportInterface`, `Teleport`, `teleportCords`.
- Data format: Java hardcoded enums
- What fields are supported: enum entries for `MONSTERS`, `BOSSES`, `MINIGAMES`, `DUNGEONS`, `SKILLING`, and `PK`, each with `name`, `npcID`, and `teleportCords`.
- How the data is loaded: enum constants are available at class load and displayed by interface methods.
- When the data is loaded: class load time.
- Whether it can be reloaded without server restart: No.
- Safe edits: adding new teleports by copying an existing enum entry and testing the interface.
- Risky edits: changing existing button order, invalid coordinates, wilderness teleports, or instance-only destinations.
- Example content using this loader: player teleport interface entries.
- Future content types that should use this loader: new teleport destinations after content exists and coordinates are stable.

### Teleport Tablets And Devices

- Main loader files:
  - `src/io/xeros/content/teleportation/TeleportTablets.java`
  - `src/io/xeros/content/teleportation/TeleportationDevice.java`
- Data file path if found: Not found in repo. Searched terms: `TeleportTablets`, `TeleportationDevice`, `TabType`.
- Data format: Java hardcoded enums and arrays
- What fields are supported: tablet item ids, coordinates, device entries, and prices defined in Java.
- How the data is loaded: enum constants and arrays load with the class.
- When the data is loaded: class load time.
- Whether it can be reloaded without server restart: No.
- Safe edits: Java-only destination additions after coordinate testing.
- Risky edits: invalid map coordinates, bypassing progression locks, or changing item ids.
- Example content using this loader: teleport tablets and teleportation device options.
- Future content types that should use this loader: special item teleports when Java changes are allowed.

## World Event And Activity Boss Loaders

### World Events

- Main loader file: `src/io/xeros/content/worldevent/WorldEventContainer.java`
- Data file path if found: Not found in repo. Runtime state file referenced by `src/io/xeros/content/worldevent/WorldEventState.java`: `world_event_state.json`. Searched terms: `WorldEventContainer`, `WorldEventState`, `world_event_state.json`.
- Data format: Java hardcoded event list plus external runtime JSON state
- What fields are supported: Java event list contains event classes. Runtime state stores fields such as `worldEventIndex` and `ticksUntilNextEvent`.
- How the data is loaded: `WorldEventContainer.initialise()` creates the hardcoded event rotation and loads state through `WorldEventState`.
- When the data is loaded: near the end of startup.
- Whether it can be reloaded without server restart: No general world event catalog reload found.
- Safe edits: none for data-only event catalog work.
- Risky edits: changing runtime state manually or changing rotation logic without testing event timers.
- Example content using this loader: Tournament, Hespori, and Wilderness Boss world events.
- Future content types that should use this loader: new world events require Java event classes, not data-only edits.

### Activity And Global Boss Types

- Main loader files:
  - `src/io/xeros/content/activityboss/ActivityType.java`
  - `src/io/xeros/content/activityboss/GlobalBossType.java`
  - `src/io/xeros/content/activityboss/GlobalBossLootTable.java`
  - `src/io/xeros/content/activityboss/GlobalBossSpawnZoneManager.java`
- Data file path if found: Not found in repo. Searched terms: `ActivityType`, `GlobalBossType`, `GlobalBossLootTable`, `GlobalBossSpawnZoneManager`.
- Data format: Java hardcoded enums and lists
- What fields are supported: activity types, global boss NPC ids, names, thresholds, spawn positions, combat type, spawn zones, and common loot table items.
- How the data is loaded: enum constants and Java lists are available when global boss managers load.
- When the data is loaded: class load time and manager initialization.
- Whether it can be reloaded without server restart: No data reload found.
- Safe edits: Java-only additions with owner review.
- Risky edits: thresholds, spawn positions, loot tables, or activity counters without checking contribution and economy balance.
- Example content using this loader: Barrows-style activity bosses triggered by upgrades, clue caskets, votes, Fire of Exchange burns, and killstreaks.
- Future content types that should use this loader: new activity boss triggers only when Java changes are intended.

## Minigame, Raid, And Chest Loaders

### Raid Chest Rewards

- Main loader files:
  - `src/io/xeros/content/item/lootable/impl/RaidsChestItems.java`
  - `src/io/xeros/content/item/lootable/impl/RaidsChestRare.java`
  - `src/io/xeros/content/item/lootable/impl/RaidsChestCommon.java`
  - `src/io/xeros/content/item/lootable/impl/RaidsChestPlus.java`
  - `src/io/xeros/content/item/lootable/impl/TheatreOfBloodChest.java`
- Data file path if found: Not found in repo. Searched terms: `RaidsChestItems`, `TheatreOfBloodChest`, `LootRarity`, `RaidsChest`.
- Data format: Java hardcoded reward lists
- What fields are supported: common and rare reward `GameItem` lists, rare chance logic, and chest-specific roll behavior.
- How the data is loaded: static Java maps and lists are available at class load.
- When the data is loaded: class load time.
- Whether it can be reloaded without server restart: No.
- Safe edits: Java-only reward additions after raid economy review.
- Risky edits: high-value rare rates, duplicate rare generation, or changing donor modifiers without testing.
- Example content using this loader: Chambers-style and Theatre of Blood chest rewards.
- Future content types that should use this loader: raid chest reward tables when Java changes are allowed.

### Other Lootable Chests

- Main loader files: `src/io/xeros/content/item/lootable/impl/`
- Data file path if found: Not found in repo. Searched terms: `ChestItems`, `Lootable`, `AOEChest`, `VoteChest`, `HesporiChest`.
- Data format: Java hardcoded reward lists
- What fields are supported: chest-specific item tables and roll behavior.
- How the data is loaded: static Java lists and classes.
- When the data is loaded: class load time.
- Whether it can be reloaded without server restart: No.
- Safe edits: Java-only reward tuning with economy review.
- Risky edits: adding rare endgame gear to frequently opened chests.
- Example content using this loader: mystery boxes, raid boxes, vote chests, and boss chests.
- Future content types that should use this loader: item container reward tables when Java changes are allowed.

### Pest Control Rewards

- Main loader file: `src/io/xeros/content/minigames/pest_control/PestControlRewards.java`
- Data file path if found: Not found in repo. Searched terms: `PestControlRewards`, `RewardButton`, `PEST_CONTROL`.
- Data format: Java hardcoded enum and shop behavior
- What fields are supported: reward buttons, point costs, item ids, and reward actions.
- How the data is loaded: enum constants and methods load with the class.
- When the data is loaded: class load time.
- Whether it can be reloaded without server restart: No.
- Safe edits: Java-only reward additions after minigame point economy review.
- Risky edits: low point costs for powerful items or changing button ids.
- Example content using this loader: Pest Control point rewards.
- Future content types that should use this loader: minigame reward shop entries that cannot be represented by normal shop YAML.

## Object, Door, And Map Config Loaders

### Door Definitions

- Main loader file: `src/io/xeros/model/collisionmap/doors/DoorDefinition.java`
- Data file path if found: Not found in repo. External runtime file referenced by loader: `obj/door_definitions.json`. Searched terms: `door_definitions.json`, `DoorDefinition.load`.
- Data format: external runtime JSON
- What fields are supported: `id`, `x`, `y`, `h`, and `face`.
- How the data is loaded: `DoorDefinition.load()` reads door definitions into collision/object handling.
- When the data is loaded: during startup before Godwars equipment and NPC config.
- Whether it can be reloaded without server restart: Yes, through `::reload doors`.
- Safe edits: adding simple door definitions.
- Risky edits: changing doors tied to quests, minigames, or collision-sensitive areas.
- Example content using this loader: interactable world doors.
- Future content types that should use this loader: static door data.

### Global Objects

- Main loader file: `src/io/xeros/model/world/objects/GlobalObjects.java`
- Data file path if found: Not found in repo. External runtime file referenced by loader: `obj/global_objects.cfg`. Searched terms: `global_objects.cfg`, `GlobalObjects`, `reloadObjectFile`.
- Data format: external runtime TXT or CFG
- What fields are supported: object id, x, y, height, face, and type.
- How the data is loaded: global object manager reads the runtime object config and registers objects into the world.
- When the data is loaded: during startup after object definitions.
- Whether it can be reloaded without server restart: Yes, through `::reload objects`.
- Safe edits: static decorative or functional object placement.
- Risky edits: object ids that override doors, banks, minigame entrances, or collision-critical tiles.
- Example content using this loader: static global object spawns.
- Future content types that should use this loader: world object placement, not object behavior.

### Legacy Doors

- Main loader files:
  - `src/io/xeros/objects/Doors.java`
  - `src/io/xeros/objects/DoubleDoors.java`
- Data file path if found: Not found in repo. External runtime files referenced by loaders: `obj/doors.txt`, `obj/doubledoors.txt`. Searched terms: `doors.txt`, `doubledoors.txt`, `Doors.getSingleton().load`, `DoubleDoors.getSingleton().load`.
- Data format: external runtime TXT
- What fields are supported: legacy door coordinate and facing data.
- How the data is loaded: singleton loaders parse runtime text files.
- When the data is loaded: during startup after region and global object setup.
- Whether it can be reloaded without server restart: No owner command found.
- Safe edits: legacy door fixes only after testing.
- Risky edits: changing doors used by pathing-heavy areas.
- Example content using this loader: legacy single and double door handling.
- Future content types that should use this loader: old door compatibility fixes.

## Godwars, Tracking, And Misc Content Data

### Godwars Equipment

- Main loader file: `src/io/xeros/content/bosses/godwars/GodwarsEquipment.java`
- Data file path if found: Not found in repo. External runtime file referenced by loader: `item/god_equipment.json`. Searched terms: `god_equipment.json`, `GodwarsEquipment.load`.
- Data format: external runtime JSON
- What fields are supported: equipment item id and god affiliation.
- How the data is loaded: `GodwarsEquipment.load()` reads runtime JSON into god equipment mappings.
- When the data is loaded: during startup before Godwars NPC data.
- Whether it can be reloaded without server restart: No dedicated reload found.
- Safe edits: adding god affiliation for existing items.
- Risky edits: removing affiliations that affect dungeon aggression.
- Example content using this loader: Godwars protection equipment.
- Future content types that should use this loader: Godwars-affiliated gear.

### Godwars NPCs

- Main loader file: `src/io/xeros/content/bosses/godwars/GodwarsNPCs.java`
- Data file path if found: Not found in repo. External runtime file referenced by loader: `npc/god_npcs.json`. Searched terms: `god_npcs.json`, `GodwarsNPCs.load`.
- Data format: external runtime JSON
- What fields are supported: NPC id and god affiliation.
- How the data is loaded: `GodwarsNPCs.load()` reads runtime JSON into god NPC mappings.
- When the data is loaded: during startup after Godwars equipment.
- Whether it can be reloaded without server restart: No dedicated reload found.
- Safe edits: adding god affiliation for existing NPCs.
- Risky edits: changing boss/minion affiliations without testing dungeon behavior.
- Example content using this loader: Godwars NPC aggression and faction logic.
- Future content types that should use this loader: Godwars-style NPC affiliation.

### Tracked Monsters

- Main loader file: `src/io/xeros/content/combat/stats/TrackedMonster.java`
- Data file path if found: Not found in repo. External runtime file referenced by loader: `tracked_monsters.json`. Searched terms: `tracked_monsters.json`, `TrackedMonster.init`.
- Data format: external runtime JSON
- What fields are supported: `name` and `trackKillTime`.
- How the data is loaded: `TrackedMonster.init()` reads tracked monster definitions into memory.
- When the data is loaded: early startup before item and NPC definitions.
- Whether it can be reloaded without server restart: No reload found.
- Safe edits: adding boss names for kill time tracking.
- Risky edits: names that do not match NPC definitions.
- Example content using this loader: boss kill time tracking.
- Future content types that should use this loader: tracked boss records.

### Animation Lengths

- Main loader file: `src/io/xeros/model/definitions/AnimationLength.java`
- Data file path if found: Not found in repo. External runtime file referenced by loader: `animation_lengths.cfg`. Searched terms: `animation_lengths.cfg`, `AnimationLength.startup`.
- Data format: external runtime CFG
- What fields are supported: `animationId:frameLength`.
- How the data is loaded: `AnimationLength.startup()` reads animation frame lengths into a lookup.
- When the data is loaded: during startup after treasure trail rewards.
- Whether it can be reloaded without server restart: No reload found.
- Safe edits: animation timing corrections.
- Risky edits: changing combat animation timing without testing.
- Example content using this loader: animation length lookups.
- Future content types that should use this loader: animation metadata fixes only.

### Pet Perks

- Main loader file: `src/io/xeros/content/pet/PetUtility.java`
- Data file path if found: Not found in repo. External runtime file referenced by loader: `etc/cfg/pet_perks.json`. Searched terms: `pet_perks.json`, `PetUtility.init`.
- Data format: external runtime JSON
- What fields are supported: pet perk definitions consumed by `PetUtility`.
- How the data is loaded: `PetUtility.init()` loads pet perk data.
- When the data is loaded: during startup after Godwars data.
- Whether it can be reloaded without server restart: No reload found.
- Safe edits: pet perk metadata after checking the exact runtime schema.
- Risky edits: perk ids or power effects.
- Example content using this loader: pet perks.
- Future content types that should use this loader: pet perk balancing only after schema confirmation.

### Did You Know Messages

- Main loader file: `src/io/xeros/model/cycleevent/impl/DidYouKnowEvent.java`
- Data file path if found: Not found in repo. External runtime file referenced by loader: `cfg/did_you_know.json`. Searched terms: `did_you_know.json`, `DidYouKnowEvent`.
- Data format: external runtime JSON
- What fields are supported: list of message strings.
- How the data is loaded: event constructor reads the JSON message list.
- When the data is loaded: when the Did You Know cycle event starts.
- Whether it can be reloaded without server restart: No reload found.
- Safe edits: informational message text.
- Risky edits: malformed JSON or misleading live-event messages.
- Example content using this loader: rotating server tips.
- Future content types that should use this loader: player tips and announcements.

## Poll, Referral, Tournament, And Preset Loaders

### Polls

- Main loader file: `src/io/xeros/content/polls/PollTab.java`
- Data file path if found: Not found in repo. External runtime files referenced by loader: `poll/poll.json` and `poll/polls_backup.json`. Searched terms: `PollTab.init`, `poll.json`, `polls_backup.json`.
- Data format: external runtime JSON
- What fields are supported: active poll data, backup poll data, answers, voters, timing, and rights restrictions handled by `Poll` classes.
- How the data is loaded: `PollTab.init()` and `PollTab.reloadPoll()` read poll JSON.
- When the data is loaded: during startup after AOE initialization.
- Whether it can be reloaded without server restart: Yes, through poll reload commands.
- Safe edits: poll text and answer options after confirming the live schema.
- Risky edits: vote counts, voters, active poll timing, or malformed options.
- Example content using this loader: in-game poll tab questions.
- Future content types that should use this loader: player polls.

### Referral Codes

- Main loader files:
  - `src/io/xeros/content/referral/ReferralCode.java`
  - `src/io/xeros/sql/refsystem/RefManager.java`
- Data file path if found: Not found in repo. External runtime files referenced by loaders: `referral_codes.yaml`, `refs/referral_codes.yaml`, `refs/player_claims.yaml`. Searched terms: `referral_codes.yaml`, `RefManager`, `ReferralCode.load`.
- Data format: external runtime YAML
- What fields are supported: referral `code` and reward item lists. Claim state stores per-player claim records.
- How the data is loaded: startup and post-init loaders read YAML into referral reward maps.
- When the data is loaded: startup and post-init.
- Whether it can be reloaded without server restart: Yes for `ReferralCode` through `::reload referralcodes`; `RefManager` reload support was not confirmed.
- Safe edits: referral code reward rotations with owner approval.
- Risky edits: player claim state, duplicate codes, or high-value referral rewards.
- Example content using this loader: referral code rewards.
- Future content types that should use this loader: referral promotions.

### Tournaments

- Main loader file: `src/io/xeros/content/tournaments/TourneyManager.java`
- Data file path if found: Not found in repo. External runtime file referenced by loader: `tournament/default_tourney.json`. Searched terms: `default_tourney.json`, `TourneyManager`, `initialiseSingleton`.
- Data format: external runtime JSON
- What fields are supported: tournament setup fields handled by `TourneyManager` and tournament model classes.
- How the data is loaded: tournament manager initializes singleton state and loads default tournament config.
- When the data is loaded: during startup.
- Whether it can be reloaded without server restart: no simple data reload confirmed.
- Safe edits: owner-reviewed tournament setup changes.
- Risky edits: active tournament runtime state or reward balancing without event testing.
- Example content using this loader: default tournament configuration.
- Future content types that should use this loader: tournament setups.

### Presets And Equipment Setups

- Main loader files:
  - `src/io/xeros/content/preset/PresetManager.java`
  - `src/io/xeros/model/EquipmentSetup.java`
- Data file path if found: Not found in repo. External runtime files referenced by loaders: `default_presets.json` and `equipment_setups/`. Searched terms: `default_presets.json`, `equipment_setups`, `PresetManager`.
- Data format: external runtime JSON
- What fields are supported: preset and equipment setup fields handled by the preset classes.
- How the data is loaded: startup initializes preset manager; equipment setup files are read from runtime setup directories.
- When the data is loaded: during startup or player setup usage.
- Whether it can be reloaded without server restart: no general reload confirmed.
- Safe edits: owner-reviewed default presets.
- Risky edits: changing player-specific setup files or adding unavailable items.
- Example content using this loader: default gear presets and saved setups.
- Future content types that should use this loader: default combat/skilling presets.

## Server And Runtime Config Loaders

### Server Configuration

- Main loader file: `src/io/xeros/Server.java`
- Data file path if found: `config.yaml`
- Data format: YAML
- What fields are supported: fields defined by `src/io/xeros/ServerConfiguration.java`.
- How the data is loaded: `JsonUtil.fromYaml()` parses `config.yaml` into `ServerConfiguration`.
- When the data is loaded: server boot before normal startup initialization.
- Whether it can be reloaded without server restart: No safe live reload found.
- Safe edits: environment configuration only with owner approval.
- Risky edits: ports, data directories, save directories, world settings, or production toggles.
- Example content using this loader: server runtime configuration.
- Future content types that should use this loader: none; this is not gameplay content.

### Logback Configuration

- Main loader file: logging framework runtime
- Data file path if found: `resources/logback.xml`
- Data format: XML
- What fields are supported: logging appenders, loggers, and formatting.
- How the data is loaded: logging framework reads classpath configuration.
- When the data is loaded: process startup.
- Whether it can be reloaded without server restart: not confirmed.
- Safe edits: logging level changes with owner approval.
- Risky edits: disabling important logs or writing logs to invalid paths.
- Example content using this loader: server logging setup.
- Future content types that should use this loader: none.

## Other Data-Like Runtime Files

These files are referenced by loaders but are state, generated output, or support data rather than content balance sources.

- `src/io/xeros/content/minigames/coinflip/CoinFlipJson.java`
  - Data file path if found: Not found in repo. Runtime file referenced by loader: `coinflip.json`.
  - Data format: external runtime JSON state.
  - Safe edits: none for content updates.

- `src/io/xeros/ServerAttributes.java`
  - Data file path if found: Not found in repo. Runtime file referenced by loader: `server_attributes.json`.
  - Data format: external runtime JSON state.
  - Safe edits: none for content updates.

- `src/io/xeros/content/wogw/Wogw.java`
  - Data file path if found: Not found in repo. Runtime file referenced by loader: `wogw.txt`.
  - Data format: external runtime TXT state.
  - Safe edits: none for content updates.

- `src/io/xeros/content/bosses/hespori/Hespori.java`
  - Data file path if found: Not found in repo. Runtime file referenced by loader: `hespori_seed_bonuses.txt`.
  - Data format: external runtime TXT state.
  - Safe edits: none for content updates.

- `src/io/xeros/content/leaderboards/LeaderboardUtils.java`
  - Data file path if found: Not found in repo. External runtime file referenced by loader: `leaderboard_rewards.yaml`.
  - Data format: external runtime YAML.
  - Safe edits: owner-reviewed leaderboard rewards only.

- `src/io/xeros/util/DataStorage.java`
  - Data file path if found: Not found in repo. Runtime file referenced by loader: `data.json`.
  - Data format: external runtime JSON state.
  - Safe edits: none for content updates.

- `item_tooltips.json`
  - Main loader file: `src/io/xeros/content/menu/ItemTooltips.java`
  - Data format: generated JSON helper.
  - Safe edits: do not treat as authoritative content data.

## Requested Systems With No Data Loader Found

### Currency Catalogs

- Main loader file: Not found in repo. Searched terms: `Currency`, `points`, `ShopAssistant`, `value`, `coins`, `blood_money`.
- Data file path if found: Not found in repo.
- Data format: Java hardcoded and player fields
- What fields are supported: currency ids, point fields, and shop spend behavior are spread across Java classes such as `src/io/xeros/model/shops/ShopAssistant.java`.
- How the data is loaded: Java behavior and player save data.
- When the data is loaded: class load and player save load.
- Whether it can be reloaded without server restart: No general currency loader found.
- Safe edits: use existing shop definitions for stock, but treat currency behavior as Java-owned.
- Risky edits: changing currency semantics, point fields, or shop buy logic.
- Example content using this loader: coins, vote points, boss points, achievement points, and custom shop currencies.
- Future content types that should use this loader: none; use existing managers and shops.

### Fortune System Rewards

- Main loader file: Not found in repo. Searched terms: `Fortune`, `fortuneXp`, `Fortune XP`, `fortune`.
- Data file path if found: Not found in repo.
- Data format: Java hardcoded or not found
- What fields are supported: AOE JSON has `fortuneXp` and `fortuneXpPerKill` fields, but a general Fortune reward config loader was not found.
- How the data is loaded: Not found in repo.
- When the data is loaded: Not found in repo.
- Whether it can be reloaded without server restart: Not found in repo.
- Safe edits: do not add data-only Fortune rewards until the target Java hook is confirmed.
- Risky edits: assuming AOE reward JSON supports arbitrary Fortune XP economy rewards.
- Example content using this loader: Not found in repo.
- Future content types that should use this loader: Not found in repo.

### Donator Feature Catalog

- Main loader file: Not found in repo. Searched terms: `Donator`, `donator`, `Donor`, `rights`, `rank`.
- Data file path if found: Not found in repo.
- Data format: mixed Java hardcoded behavior plus donor deal loaders
- What fields are supported: donor ranks and perks appear spread across Java systems. Donation offers and cosmetics use external deal files.
- How the data is loaded: mixed Java and external deal loaders.
- When the data is loaded: startup and post-init.
- Whether it can be reloaded without server restart: no universal donor feature reload found.
- Safe edits: use donation/deal loaders for offers; avoid rank feature rewrites.
- Risky edits: changing donor rights or account privilege behavior.
- Example content using this loader: donor deals, cosmetic costs, and account boosts.
- Future content types that should use this loader: donor offers, not core donor permissions.

## A. Best Data-Driven Systems For Safe Updates

1. `data/aoe/aoe_tier_rewards.json`
   - Best for JSON-only AOE item reward tuning.

2. `data/aoe/aoe_boss_tiers.json`
   - Best for AOE tier definitions, unlock kills, boss ids, minion ids, and map metadata.

3. `data/aoe/AoeZoneMapConfig.json`
   - Best for AOE map placement after instance testing.

4. External runtime `drops/`
   - Best for normal NPC and boss drop tables through `src/io/xeros/model/entity/npc/drops/DropManager.java`.

5. External runtime `shops/`
   - Best for shop stock changes through `src/io/xeros/model/definitions/ShopDef.java`.

6. External runtime `daily_rewards/`
   - Best for rotating daily login rewards through `src/io/xeros/content/dailyrewards/DailyRewardContainer.java`.

7. External runtime `collection_npcs.json`
   - Best for adding bosses to collection log categories through `src/io/xeros/content/collection_log/CollectionLog.java`.

8. `resources/instance_rewards.json`
   - Best for ranked instance reward item lists.

9. `demonhunter-xp.json`
   - Best for Demon Hunter XP multiplier tuning.

10. `resources/aoe_weekly_hazards.json`
   - Best for weekly AOE hazard rotations.

## B. Systems That Require Java Changes

- `src/io/xeros/content/upgrade/UpgradeMaterials.java`
- `src/io/xeros/content/fusion/FusionMaterials.java`
- `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`
- `src/io/xeros/content/wraith/WraithCharges.java`
- `src/io/xeros/content/teleportv2/inter/TeleportInterface.java`
- `src/io/xeros/content/activityboss/GlobalBossType.java`
- `src/io/xeros/content/activityboss/GlobalBossLootTable.java`
- `src/io/xeros/content/worldevent/WorldEventContainer.java`
- `src/io/xeros/content/taskmaster/Tasks.java`
- `src/io/xeros/content/achievement/Achievements.java`
- `src/io/xeros/content/battlepass/RewardList.java`
- `src/io/xeros/content/item/lootable/impl/RaidsChestItems.java`
- `src/io/xeros/content/item/lootable/impl/TheatreOfBloodChest.java`
- `src/io/xeros/content/minigames/pest_control/PestControlRewards.java`
- `src/io/xeros/content/items/aoeweapons/AoeWeapons.java`

## C. Config Files Future Codex Tasks Can Safely Edit

These are the safest checked-in data files when the requested change is specifically about that system:

- `data/aoe/aoe_tier_rewards.json`
- `data/aoe/aoe_boss_tiers.json`
- `data/aoe/AoeZoneMapConfig.json`
- `demonhunter-xp.json`
- `resources/instance_rewards.json`
- `resources/boss_instance_special_attacks.json`
- `resources/aoe_weekly_hazards.json`
- `resources/aoe_mutators.json`
- `resources/aoe_mutator_synergies.json`
- `resources/aoe_environmental_hazards.json`
- `resources/aoe_environmental_patterns.json`

Safe means the loader exists and the file is source-controlled. It does not mean every reward value is economy-safe.

## D. Config Files That Require Owner Review

- `config.yaml`
- `resources/logback.xml`
- `resources/instance_rewards.json` when adding high-value items
- `resources/boss_instance_special_attacks.json` when changing damage effects
- `resources/aoe_environmental_hazards.json` when changing damage, stun, or drain
- `resources/aoe_environmental_patterns.json` when increasing hazard density
- `data/aoe/aoe_boss_tiers.json` when changing map templates or unlock progression
- `data/aoe/AoeZoneMapConfig.json` when changing copied map coordinates
- `demonhunter-xp.json` when increasing XP multipliers materially
- external runtime `shops/`
- external runtime `drops/`
- external runtime `daily_rewards/`
- external runtime `donation_rewards.json`
- external runtime `deals/`
- external runtime `seasonpass/`
- external runtime `leaderboard_rewards.yaml`

## E. Missing External Data Needed For Economy Balancing

The following important balance files are referenced by loaders but are not checked into the repo:

- `item/item_definitions.yaml`
- `item/item_stats.json`
- `item/god_equipment.json`
- `npc/npc_definitions.json`
- `npc/npc_stats.json`
- `npc/npc_combat_defs.json`
- `npc/npc_spawns.json`
- `npc/spawns/`
- `npc/osrsspawns/`
- `npc/god_npcs.json`
- `npc/boss_points.yaml`
- `shops/`
- `drops/`
- `collection_npcs.json`
- `daily_rewards/`
- `donation_rewards.json`
- `cfg/slayer_masters.json`
- `seasonpass/info.txt`
- `seasonpass/memberRewards.txt`
- `seasonpass/defaultRewards.txt`
- `deals/timed_offers.yaml`
- `deals/cosmetic_offers.yaml`
- `deals/bonus_items.yaml`
- `deals/weekly_time.yaml`
- `deals/cosmetic_costs.json`
- `vote_panel.json`
- `poll/poll.json`
- `poll/polls_backup.json`
- `referral_codes.yaml`
- `refs/referral_codes.yaml`
- `leaderboard_rewards.yaml`

These files are needed to fully audit live economy rates, shop stock, drop rates, donation offers, vote rewards, Slayer assignments, and collection categories.

## F. Recommended Data-First Update Strategy For Turmoil

1. Search for an existing loader or enum before editing flow classes.

2. Prefer source-controlled JSON for AOE work:
   - `data/aoe/aoe_tier_rewards.json`
   - `data/aoe/aoe_boss_tiers.json`
   - `data/aoe/AoeZoneMapConfig.json`

3. Prefer external runtime YAML for ordinary economy content:
   - `drops/`
   - `shops/`
   - `daily_rewards/`
   - `npc/boss_points.yaml`

4. Use Java enums only where the repo already uses Java enums:
   - upgrades in `src/io/xeros/content/upgrade/UpgradeMaterials.java`
   - fusions in `src/io/xeros/content/fusion/FusionMaterials.java`
   - Task Master tasks in `src/io/xeros/content/taskmaster/Tasks.java`
   - achievements in `src/io/xeros/content/achievement/Achievements.java`
   - teleports in `src/io/xeros/content/teleportv2/inter/TeleportInterface.java`

5. Treat runtime state files as off-limits for content changes:
   - player saves
   - `taskmaster/<player>.json`
   - `world_event_state.json`
   - `vote_panel.json`
   - `server_attributes.json`

6. For reward changes, classify the reward before editing:
   - Low risk: small coins, skilling supplies, low-value consumables, cosmetic-only items.
   - Medium risk: vote points, boss points, moderate boxes, Demon Hunter XP, AOE item bonuses.
   - High risk: best-in-slot gear, Wraith Essence, upgrade/foundry value, Fire of Exchange value, donation rewards, raid rares.

7. After any data edit, test the exact reload path if one exists:
   - `::reload drops`
   - `::reload shops`
   - `::reload dailyrewards`
   - `::reload items`
   - AOE debug reloads for AOE tier and reward files
   - `Reloaddhxps` for `demonhunter-xp.json`

8. If no reload exists, plan for a server restart and document the startup loader that will consume the data.
