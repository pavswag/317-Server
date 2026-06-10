# Turmoil Content Guide

This guide is for future Codex tasks adding content to the Turmoil RSPS server. The codebase is a Java Old School RuneScape private server with classic PI/Xeros-style systems and newer modular content under `src/io/xeros/content/`.

## Server Rules

- Do not rewrite core systems unless explicitly requested.
- Before coding, search for similar working systems and copy the closest local pattern.
- Prefer configs, enums, managers, and small content classes over broad core edits.
- Use `src/io/xeros/model/entity/player/save/PlayerSaveEntry.java` for new persistent player progression.
- Use `src/io/xeros/content/dialogue/DialogueBuilder.java` for new dialogues.
- Add commands as subclasses of `src/io/xeros/content/commands/Command.java` instead of editing `CommandManager`.
- Keep player save compatibility. Never rename existing save keys without a migration.
- Explain changed files and test steps after every content implementation.

## New NPC Bosses

Main patterns:

- `src/io/xeros/content/bosses/`
- `src/io/xeros/content/bosses/obor/OborNPC.java`
- `src/io/xeros/content/bosses/sarachnis/SarachnisNpc.java`
- `src/io/xeros/content/combat/npc/NPCAutoAttackBuilder.java`
- `src/io/xeros/content/combat/npc/NPCAutoAttack.java`
- `src/io/xeros/content/combat/death/NPCDeath.java`
- `src/io/xeros/model/entity/npc/NPC.java`

How to add a boss:

1. Search `src/io/xeros/content/bosses/` for a boss with similar mechanics.
2. For custom attacks, create or reuse an NPC class and set auto-attacks with `NPCAutoAttackBuilder`.
3. Use `setAnimation`, `setCombatType`, `setMaxHit`, `setAttackDelay`, `setHitDelay`, `setProjectile`, `setOnAttack`, and `setOnHit` as needed.
4. For multi-target attacks, use `NPCAutoAttack.getDefaultSelectPlayersForAttack()` or a local selector.
5. Let ordinary boss deaths flow through `NPCDeath.dropItems` and `NPCDeath.dropItemsFor`.
6. Add only boss-specific death handling when the reward flow is not a normal drop table.
7. Hook achievements, boss points, collection log, or world activity only if the content design requires it.

Avoid:

- Do not add isolated boss mechanics directly to `src/io/xeros/model/entity/npc/NPCHandler.java`.
- Do not add broad process logic to `src/io/xeros/model/entity/npc/NPCProcess.java` unless matching an existing legacy global-boss pattern.
- Do not hardcode ordinary loot in boss classes if the drop table system can handle it.

## Drops

Main patterns:

- `src/io/xeros/model/entity/npc/drops/DropManager.java`
- `src/io/xeros/model/entity/npc/drops/TableGroup.java`
- `src/io/xeros/model/entity/npc/drops/Table.java`
- `src/io/xeros/model/entity/npc/drops/Drop.java`
- `src/io/xeros/model/entity/npc/drops/TablePolicy.java`
- `src/io/xeros/content/combat/death/NPCDeath.java`
- `src/io/xeros/content/commands/test/DropTest.java`

How drops work:

- `Server` owns the singleton `DropManager`.
- Startup calls `Server.getDropManager().read()`.
- Drop YAML is loaded from the configured external drop directory used by `DropManager`, not from a repo `data/drops` folder.
- Tables use `TablePolicy`: `CONSTANT`, `COMMON`, `UNCOMMON`, `RARE`, `VERY_RARE`, and `EXTREMELY_RARE`.
- `NPCDeath.dropItemsFor` computes location, double drops, boss points, kill tracking, then calls `Server.getDropManager().create(...)`.
- `TableGroup.access(...)` rolls the tables, applies drop-rate modifiers, updates collection logs for rare drops, and announces rare drops.

How to add drops:

1. Add normal NPC loot in the drop YAML using the same structure as existing drop configs.
2. Use `npc_id` for one NPC or an array when several NPC IDs share one table.
3. Put guaranteed drops under `constant`.
4. Put rollable drops under the appropriate rarity table.
5. Use `::reload drops` after editing drop configs.
6. Use `::droptest` in debug/test mode when available to sample the table.

Avoid:

- Do not hardcode ordinary drops in `NPCDeath`.
- Do not modify `DropManager` for one boss unless the drop engine itself needs a shared feature.
- Do not bypass `Server.getDropManager().create(...)` for normal NPC drops.

## Item Upgrades

Main patterns:

- `src/io/xeros/content/upgrade/UpgradeMaterials.java`
- `src/io/xeros/content/upgrade/UpgradeInterface.java`
- `src/io/xeros/model/entity/player/packets/ClickObject.java`
- `src/io/xeros/model/entity/player/packets/ClickingButtons.java`
- `src/io/xeros/model/entity/player/packets/ContainerAction1.java`
- `src/io/xeros/model/entity/player/packets/ContainerAction3.java`

How upgrades work:

- Upgrade recipes are enum entries in `UpgradeMaterials`.
- Each entry defines type, Fortune level, required item, reward item, upgrade-point cost, success rate, XP, and rare status.
- `UpgradeInterface` displays recipes, validates requirements, removes costs, rolls success, grants rewards, grants Fortune XP, increments achievements, records activity-boss progress, and updates collection logs for rare upgrades.
- Rare upgrades can broadcast and enter collection-log categories for weapons, armour, accessories, and misc.

How to add upgrade content:

1. Add a new enum entry in `UpgradeMaterials` under the closest `UpgradeType`.
2. Use the existing constructor fields consistently: type, level, required item, reward item, cost, success rate, XP, rare.
3. Use `rare = true` only when the item should broadcast and appear in collection logs.
4. Let `UpgradeInterface` render and process the recipe automatically.
5. Only touch packet handlers if adding a completely separate upgrade mechanic.

Avoid:

- Do not duplicate upgrade-roll logic outside `UpgradeInterface`.
- Do not add item-specific branches for normal recipes.
- Do not store upgrade progression directly in `PlayerSave.java`.

## Dialogues

Main patterns:

- `src/io/xeros/content/dialogue/DialogueBuilder.java`
- `src/io/xeros/content/dialogue/DialogueOption.java`
- `src/io/xeros/content/dialogue/types/`
- `src/io/xeros/content/dialogue/impl/`
- `src/io/xeros/content/dialogue/impl/BossInstanceDialogue.java`
- `src/io/xeros/model/entity/player/DialogueHandler.java`
- `src/io/xeros/model/entity/player/packets/dialogueoptions/`

How dialogues work:

- Newer content uses `DialogueBuilder` chains.
- `DialogueBuilder` supports player lines, NPC lines, statements, item statements, options, confirmation options, and make-item dialogues.
- Legacy dialogue uses numeric IDs in `DialogueHandler.sendDialogues`, `nextChat`, and `dialogueAction`.
- Option button handling for old dialogues lives under `src/io/xeros/model/entity/player/packets/dialogueoptions/`.

How to add dialogue:

1. Prefer a new class under `src/io/xeros/content/dialogue/impl/` or inline `new DialogueBuilder(player)` for small one-off interactions.
2. Use `player.start(new DialogueBuilder(player)...)` or subclass `DialogueBuilder` for reusable dialogue.
3. Use `DialogueOption` callbacks for choices.
4. Use `setNpcId` when several NPC lines use the same speaker.
5. Use legacy `DialogueHandler` only when extending an existing legacy dialogue flow.

Avoid:

- Do not add new `dialogueAction` IDs unless the target system is already legacy.
- Do not put large new dialogue trees into `DialogueHandler`.
- Do not use non-ASCII symbols in dialogue text unless the surrounding file already uses them and the client supports them.

## Commands

Main patterns:

- `src/io/xeros/content/commands/Command.java`
- `src/io/xeros/content/commands/CommandManager.java`
- `src/io/xeros/content/commands/all/`
- `src/io/xeros/content/commands/donator/`
- `src/io/xeros/content/commands/helper/`
- `src/io/xeros/content/commands/moderator/`
- `src/io/xeros/content/commands/admin/`
- `src/io/xeros/content/commands/owner/`
- `src/io/xeros/content/commands/test/`

How commands work:

- Commands extend `Command`.
- The default command string is the lowercased class name.
- `CommandManager.initializeCommands()` reflection-loads command subclasses with `ClassGraphHandler`.
- `CommandManager.executeCommand(...)` parses command name and input, checks privileges, then calls `execute`.
- Test commands are only loaded in debug/test mode.

How to add a command:

1. Create a small command class in the correct rank package.
2. Implement `execute(Player player, String commandName, String input)`.
3. Implement `hasPrivilege(Player player)` using existing rights checks.
4. Override `getDescription()` if it should appear clearly in `::commands`.
5. Override `getFormat()` when the command has required syntax.

Avoid:

- Do not add normal commands as inline branches in `CommandManager`.
- Do not place player commands in owner/admin packages.
- Do not expose debug-only behavior outside `src/io/xeros/content/commands/test/`.

## Player Progression and Saved Data

Main patterns:

- `src/io/xeros/model/entity/player/save/PlayerSave.java`
- `src/io/xeros/model/entity/player/save/PlayerSaveEntry.java`
- `src/io/xeros/model/entity/player/save/PlayerSaveExecutor.java`
- `src/io/xeros/model/entity/player/save/impl/`
- `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`
- `src/io/xeros/content/wildwarning/WildWarning.java`
- `src/io/xeros/content/collection_log/CollectionLog.java`

How persistence works:

- Player save files are text files under the configured public character-save directory.
- `PlayerSave` is a large legacy parser and writer.
- `PlayerSave.loadPlayerSaveEntries()` reflection-loads `PlayerSaveEntry` implementations.
- `PlayerSaveEntry` implementations declare keys, decode key/value pairs, encode values, and can run login hooks.
- Collection logs are saved separately as JSON by `CollectionLog`.

How to add progression:

1. For new progression, create a small `PlayerSaveEntry`.
2. Store simple values on the `Player` object or in the player attribute map.
3. Use stable key names that will not conflict with existing save keys.
4. Provide safe defaults when a key is missing.
5. Keep decode tolerant of bad or old values.
6. Call existing progression systems such as achievements, battle pass, boss points, and collection log only where relevant.

Avoid:

- Do not expand `PlayerSave.java` for new content unless modifying an existing saved field.
- Do not rename existing save keys.
- Do not store large structured content in the character text file if a separate JSON system is more appropriate.

## Minigames and Instances

Main patterns:

- `src/io/xeros/content/minigames/`
- `src/io/xeros/content/instances/InstancedArea.java`
- `src/io/xeros/content/instances/InstanceConfiguration.java`
- `src/io/xeros/content/instances/InstanceHeight.java`
- `src/io/xeros/content/instances/BossInstanceManager.java`
- `src/io/xeros/content/instances/aoe/`
- `src/io/xeros/content/minigames/arbograve/`
- `src/io/xeros/content/minigames/arbograve/instance/ArbograveInstance.java`
- `src/io/xeros/content/minigames/pest_control/PestControl.java`
- `src/io/xeros/content/minigames/barrows/`
- `src/io/xeros/content/minigames/raids/`
- `src/io/xeros/content/minigames/tob/`

How minigames work:

- There is no single minigame framework.
- Static minigames like Pest Control use global state, lobby lists, game lists, and cycle events.
- Newer encounters use `InstancedArea` for players, NPCs, height allocation, disposal, object handling, and death handling.
- AOE tiers are more data-driven and use controllers, loaders, instance services, and `PlayerSaveEntry`.

How to add minigame content:

1. For a roomed encounter or private boss area, prefer `InstancedArea`.
2. Define boundaries and an `InstanceConfiguration`.
3. Add players and NPCs through instance methods so entity instance pointers are correct.
4. Override `handleClickObject` for instance-specific object interactions.
5. Override `handleDeath` for custom death behavior.
6. Use `onDispose` for cleanup that is not automatically handled.
7. For global lobby games, copy the Pest Control style only when a shared global game is intended.

Avoid:

- Do not manually manage instance height if `InstanceHeight` can reserve it.
- Do not leave players or NPCs attached to disposed instances.
- Do not mix global static minigame state into private instance content.

## Global Events and Activity Bosses

Main patterns:

- `src/io/xeros/content/worldevent/`
- `src/io/xeros/content/worldevent/WorldEvent.java`
- `src/io/xeros/content/worldevent/WorldEventContainer.java`
- `src/io/xeros/content/worldevent/WorldEventState.java`
- `src/io/xeros/content/worldevent/impl/`
- `src/io/xeros/content/activityboss/`
- `src/io/xeros/content/activityboss/ActivityType.java`
- `src/io/xeros/content/activityboss/GlobalBossType.java`
- `src/io/xeros/content/activityboss/GlobalBossActivityManager.java`
- `src/io/xeros/content/activityboss/GlobalBossDropHandler.java`
- `src/io/xeros/content/activityboss/GlobalBossContributionTracker.java`
- `src/io/xeros/content/globalboss/`

How global events work:

- `WorldEventContainer` cycles through `WorldEvent` implementations.
- `WorldEventState` persists the current world-event index and ticks until the next event.
- A `WorldEvent` can initialize content, dispose unfinished content, report status, provide a teleport command, and announce to players.

How activity bosses work:

- `GlobalBossActivityManager.record(...)` tracks activity totals by `ActivityType`.
- `GlobalBossType` maps an activity type to an NPC, threshold, spawn position, and combat type.
- When the threshold is reached, the manager spawns the boss and broadcasts it.
- On death, `GlobalBossDropHandler` rewards contributors using damage tracked on the NPC.

How to add global content:

1. For scheduled rotating events, implement `WorldEvent` under `src/io/xeros/content/worldevent/impl/`.
2. Add the event to `WorldEventContainer.WORLD_EVENT_LIST`.
3. For activity-triggered bosses, add or reuse an `ActivityType`.
4. Add a `GlobalBossType` entry with NPC ID, name, activity, threshold, spawn position, and combat type.
5. Record activity from the content action, such as upgrades, vote claims, clue caskets, or kill streaks.
6. Use contributor-based rewards for shared global bosses.

Avoid:

- Do not use the legacy `src/io/xeros/content/globalboss/` damage maps for new activity bosses unless matching old behavior.
- Do not spawn global bosses without cooldown or active-boss checks.
- Do not make scheduled world events depend on player-specific state unless the event handles logout/dispose safely.

## Final Checklist for Future Content Tasks

Before implementation:

- Search the repo for a similar working system.
- Pick the closest pattern file and follow its structure.
- Prefer adding content through configs, enums, managers, or small content classes.
- Decide whether the content needs achievements, collection logs, boss points, battle pass XP, or save data.
- Use `PlayerSaveEntry` for new persistent player progression.
- Use `DialogueBuilder` for new dialogues.
- Use command subclasses for new commands.
- Use drop configs for normal NPC loot.
- Keep changes modular and avoid core rewrites.

After implementation:

- Explain every changed file.
- Include manual test steps.
- Mention whether drops need `::reload drops`.
- Mention whether new progression was tested across logout/login.
- Mention any config files outside the repo that must be updated.
