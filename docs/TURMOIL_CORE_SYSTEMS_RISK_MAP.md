# Turmoil Core Systems Risk Map

This map identifies fragile core systems in Turmoil and explains how future Codex tasks should extend them safely. It uses all current docs in `docs/` as context and follows the same content rule: do not rewrite server backbone systems for ordinary content work.

Risk labels:

- Low risk: isolated content or data changes with narrow blast radius.
- Medium risk: shared content managers where mistakes affect several features.
- High risk: central gameplay systems where mistakes affect many players or progression loops.
- Critical risk: backbone systems where mistakes can break login, saves, combat, economy, or core server processing.

## 1. Player.java

- Risk: Critical risk
- Main files:
  - `src/io/xeros/model/entity/player/Player.java`
  - `src/io/xeros/model/entity/player/PlayerAssistant.java`
  - `src/io/xeros/model/entity/player/PlayerHandler.java`
- Why it is important: `Player` owns core runtime state, content managers, combat state, inventory, skills, rights, saves, interfaces, and login setup.
- What usually depends on it: Commands, combat, shops, minigames, instances, achievements, collection logs, battlepass, Slayer, Task Master, Fire of Exchange, upgrades, Wraith charges, and daily rewards.
- Safe changes: Add accessor methods for a narrowly scoped new content field only when `PlayerSaveEntry` or attributes are not enough. Prefer adding content state to the owning content class.
- Risky changes: Renaming fields, changing login initialization, changing default manager construction, or altering core state flags used by combat and packet handling.
- Changes future agents should avoid: Do not move large content logic into `Player`. Do not add reward grants directly to `finishLogin()` unless the reward is already cooldown-gated elsewhere.
- Safer alternative extension points:
  - `src/io/xeros/model/entity/player/save/PlayerSaveEntry.java`
  - `src/io/xeros/content/dailyrewards/DailyRewardsPlayerSaveEntry.java`
  - `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`
  - Player attributes for runtime-only state.
- Existing pattern files to copy instead:
  - `src/io/xeros/content/instances/aoe/AoeTierController.java`
  - `src/io/xeros/content/wraith/WraithCharges.java`
  - `src/io/xeros/content/dailyrewards/DailyRewards.java`

## 2. PlayerSave.java

- Risk: Critical risk
- Main files:
  - `src/io/xeros/model/entity/player/save/PlayerSave.java`
  - `src/io/xeros/model/entity/player/save/PlayerSaveEntry.java`
  - `src/io/xeros/model/entity/player/save/PlayerSaveOffline.java`
- Why it is important: Loads and writes existing player files, legacy keys, credentials, inventory, bank, stats, achievements, progression fields, and compatibility data.
- What usually depends on it: Login, logout, migration safety, player progression, economy state, mode data, achievements, Wraith charges, battlepass, boss points, and legacy fields.
- Safe changes: Fix an old key, decode an old legacy value, or migrate a specific existing save field with owner review.
- Risky changes: Changing parse order, save key names, default values, achievement parsing, inventory/bank save layout, or password handling.
- Changes future agents should avoid: Do not add new content save keys directly to `PlayerSave.java`. Do not rename save keys without a migration.
- Safer alternative extension points:
  - `src/io/xeros/model/entity/player/save/PlayerSaveEntry.java`
  - `src/io/xeros/model/entity/player/save/impl/`
  - Content-local save entries under `src/io/xeros/content/`
- Existing pattern files to copy instead:
  - `src/io/xeros/content/dailyrewards/DailyRewardsPlayerSaveEntry.java`
  - `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`
  - `src/io/xeros/model/entity/player/save/impl/AutocastPlayerSaveEntry.java`

## 3. PlayerSaveEntry System

- Risk: Medium risk
- Main files:
  - `src/io/xeros/model/entity/player/save/PlayerSaveEntry.java`
  - `src/io/xeros/model/entity/player/save/PlayerSave.java`
  - `src/io/xeros/content/dailyrewards/DailyRewardsPlayerSaveEntry.java`
  - `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`
- Why it is important: Provides the safer modular path for new persistent content without expanding the legacy save switch.
- What usually depends on it: AOE tier progression, daily rewards, private messaging, wild warning, lost property, donation rewards, questing, and content-local persistence.
- Safe changes: Add a new implementation with unique stable keys, defensive decode logic, clear defaults, and a no-op or minimal `login(Player player)` method.
- Risky changes: Overlapping keys with another entry, returning dynamic key lists that omit old data, or throwing exceptions on malformed values.
- Changes future agents should avoid: Do not use it for huge structured data when a dedicated JSON store already exists. Do not use unstable key names.
- Safer alternative extension points:
  - Content-specific JSON stores for systems that already use them.
  - Player attributes for runtime-only data.
- Existing pattern files to copy instead:
  - `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`
  - `src/io/xeros/content/dailyrewards/DailyRewardsPlayerSaveEntry.java`

## 4. NPCHandler

- Risk: Critical risk
- Main files:
  - `src/io/xeros/model/entity/npc/NPCHandler.java`
  - `src/io/xeros/model/entity/npc/NPC.java`
  - `src/io/xeros/model/entity/npc/NPCSpawning.java`
- Why it is important: Owns global NPC arrays, spawning, lifecycle management, and shared NPC processing entry points.
- What usually depends on it: Bosses, Slayer, world events, global bosses, minigames, drops, pets, combat, and NPC interactions.
- Safe changes: Very small bug fixes to shared NPC lifecycle with full testing. Content should normally avoid this file.
- Risky changes: Changing NPC index management, spawn behavior, aggression, despawn rules, or global processing.
- Changes future agents should avoid: Do not add isolated boss mechanics here. Do not add content-specific reward logic here.
- Safer alternative extension points:
  - `src/io/xeros/content/bosses/`
  - `src/io/xeros/model/entity/npc/NPCAction.java`
  - `src/io/xeros/content/activityboss/GlobalBossActivityManager.java`
  - `src/io/xeros/content/worldevent/`
- Existing pattern files to copy instead:
  - `src/io/xeros/content/bosses/obor/OborNPC.java`
  - `src/io/xeros/content/bosses/sarachnis/SarachnisNpc.java`
  - `src/io/xeros/model/entity/npc/NPCAction.java`

## 5. NPCProcess

- Risk: Critical risk
- Main files:
  - `src/io/xeros/model/entity/npc/NPCProcess.java`
  - `src/io/xeros/model/entity/npc/NPCHandler.java`
  - `src/io/xeros/content/combat/death/NPCDeath.java`
- Why it is important: Runs shared NPC tick behavior, combat progression, death checks, despawns, and legacy special cases.
- What usually depends on it: Every NPC, all bosses, Slayer kills, global bosses, drops, NPC combat, and minigames.
- Safe changes: Avoid for content. Only shared bug fixes with regression testing should touch it.
- Risky changes: Changing death timing, hit processing, respawn timing, or global NPC behavior.
- Changes future agents should avoid: Do not add one boss special, one drop, one task hook, or one minigame rule here unless copying an existing legacy pattern by explicit request.
- Safer alternative extension points:
  - `src/io/xeros/content/combat/npc/NPCAutoAttackBuilder.java`
  - `src/io/xeros/content/combat/death/NPCDeath.java` for narrow death hooks only.
  - `src/io/xeros/content/instances/aoe/AoeTierEvents.java`
- Existing pattern files to copy instead:
  - `src/io/xeros/content/bosses/bryophyta/BryophytaNPC.java`
  - `src/io/xeros/content/bosses/sarachnis/SarachnisNpc.java`

## 6. NPCDeath

- Risk: High risk
- Main files:
  - `src/io/xeros/content/combat/death/NPCDeath.java`
  - `src/io/xeros/model/entity/npc/drops/DropManager.java`
  - `src/io/xeros/content/bosspoints/BossPoints.java`
  - `src/io/xeros/content/instances/aoe/AoeTierEvents.java`
- Why it is important: Centralizes NPC death rewards, drop creation, boss points, kill achievements, pets, event progress, global boss death, and many legacy special cases.
- What usually depends on it: Drop tables, boss points, collection logs, Slayer, achievements, Task Master, battlepass, world events, AOE tier kills, and minigame rewards.
- Safe changes: Add a narrow shared hook only when a manager cannot handle the behavior elsewhere.
- Risky changes: Changing reward order, duplicate prevention, killer attribution, drop positions, double drops, or pet rolls.
- Changes future agents should avoid: Do not hardcode ordinary NPC drops here. Do not add broad content reward logic here.
- Safer alternative extension points:
  - Runtime drop tables loaded by `DropManager`.
  - `src/io/xeros/content/bosspoints/BossPoints.java`
  - `src/io/xeros/content/instances/aoe/AoeTierEvents.java`
  - Boss-specific reward classes under `src/io/xeros/content/bosses/`
- Existing pattern files to copy instead:
  - `src/io/xeros/content/item/lootable/impl/KonarChest.java`
  - `src/io/xeros/content/minigames/raids/Raids.java`
  - `src/io/xeros/content/activityboss/GlobalBossDropHandler.java`

## 7. DropManager

- Risk: Critical risk
- Main files:
  - `src/io/xeros/model/entity/npc/drops/DropManager.java`
  - `src/io/xeros/model/entity/npc/drops/TableGroup.java`
  - `src/io/xeros/model/entity/npc/drops/Table.java`
  - `src/io/xeros/model/entity/npc/drops/Drop.java`
  - `src/io/xeros/model/entity/npc/drops/TablePolicy.java`
- Why it is important: Loads and rolls NPC drop tables, applies rarity tables, drop-rate modifiers, rare announcements, and collection log hooks.
- What usually depends on it: Every NPC drop, boss rewards, collection log, rare-drop broadcasts, and economy balance.
- Safe changes: Avoid code changes for ordinary content. Use runtime drop data and reload tools.
- Risky changes: Changing table probabilities, access policy, modifier math, rare classification, or collection log updates.
- Changes future agents should avoid: Do not alter `DropManager` for one NPC. Do not bypass `Server.getDropManager().create(...)` for normal NPC drops.
- Safer alternative extension points:
  - Runtime drop YAML loaded by `DropManager`.
  - `src/io/xeros/content/commands/test/DropTest.java` for sampling.
  - Chest classes for non-NPC reward tables.
- Existing pattern files to copy instead:
  - `src/io/xeros/content/item/lootable/impl/LarransChest.java`
  - `src/io/xeros/content/item/lootable/impl/RaidsChestRare.java`
- Not found in repo:
  - Repo-local normal drop table data. Searched terms: `drop table`, `npc_id`, `drops.yaml`, `DropManager.read`.

## 8. ShopAssistant

- Risk: Critical risk
- Main files:
  - `src/io/xeros/model/shops/ShopAssistant.java`
  - `src/io/xeros/model/world/ShopHandler.java`
  - `src/io/xeros/model/definitions/ShopDef.java`
- Why it is important: Handles shop opening, pricing, buying, selling, stock display, currency-specific branches, logging, and mode restrictions.
- What usually depends on it: All shops, Fire of Exchange display shop, Slayer shops, minigame shops, donor shops, PKP shops, and custom currency stores.
- Safe changes: Add a narrow branch only for a real custom shop currency or display behavior that cannot live in data.
- Risky changes: Changing common buy/sell price logic, stock arrays, shop access checks, special currency branches, or logging.
- Changes future agents should avoid: Do not edit `ShopAssistant` for ordinary stock changes. Do not hardcode a new shop there if data or `ShopHandler.addShopAnywhere(...)` fits.
- Safer alternative extension points:
  - Runtime shop definitions loaded by `ShopDef`.
  - `src/io/xeros/model/world/ShopHandler.java`
  - `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java` for dynamic display-shop pattern.
- Existing pattern files to copy instead:
  - `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`
  - `src/io/xeros/content/skills/slayer/DemonMarkRewardHandler.java`
- Not found in repo:
  - Complete repo-local shop stock data. Searched terms: `ShopDef.load`, `shops.yaml`, `ShopItems`, `shop definitions`.

## 9. Command Handling

- Risk: High risk
- Main files:
  - `src/io/xeros/model/entity/player/packets/Commands.java`
  - `src/io/xeros/content/commands/Command.java`
  - `src/io/xeros/content/commands/CommandManager.java`
  - `src/io/xeros/content/commands/all/`
  - `src/io/xeros/content/commands/admin/`
  - `src/io/xeros/content/commands/moderator/`
  - `src/io/xeros/content/commands/donator/`
- Why it is important: Parses player commands, guards locked states, logs usage, checks privileges, and dispatches subclasses.
- What usually depends on it: Player commands, staff commands, reload/debug tools, teleports, vote claims, interfaces, AOE commands, Wraith commands, and global boss admin tools.
- Safe changes: Add a new `Command` subclass in the correct package with `execute(...)` and `hasPrivilege(...)`.
- Risky changes: Editing command dispatch, rank package mapping, logging, or guard order.
- Changes future agents should avoid: Do not add normal commands directly to `Commands.java`. Do not edit `CommandManager` for a single command.
- Safer alternative extension points:
  - `src/io/xeros/content/commands/Command.java`
  - Appropriate package under `src/io/xeros/content/commands/`
- Existing pattern files to copy instead:
  - `src/io/xeros/content/commands/all/Leaderboards.java`
  - `src/io/xeros/content/commands/all/Wraith.java`
  - `src/io/xeros/content/commands/admin/Broadcast.java`

## 10. Dialogue Handling

- Risk: High risk
- Main files:
  - `src/io/xeros/content/dialogue/DialogueBuilder.java`
  - `src/io/xeros/content/dialogue/DialogueOption.java`
  - `src/io/xeros/model/entity/player/packets/Dialogue.java`
  - `src/io/xeros/model/entity/player/packets/dialogueoptions/OptionHandler.java`
  - `src/io/xeros/model/entity/player/DialogueHandler.java`
- Why it is important: Drives modern and legacy dialogues, option callbacks, continuation packets, NPC statements, and reward choices.
- What usually depends on it: NPC interactions, tutorial, shops, daily rewards, boss instances, AOE tier selection, quest flows, and legacy `dialogueAction` content.
- Safe changes: Add a `DialogueBuilder` subclass or local `new DialogueBuilder(...)` flow.
- Risky changes: Changing option packet routing, legacy `DialogueHandler`, continuation behavior, or `dialogueAction` semantics.
- Changes future agents should avoid: Do not add new legacy `dialogueAction` flows unless the surrounding content is already legacy-only.
- Safer alternative extension points:
  - `src/io/xeros/content/dialogue/impl/`
  - `src/io/xeros/content/dialogue/DialogueBuilder.java`
  - `src/io/xeros/content/dialogue/DialogueOption.java`
- Existing pattern files to copy instead:
  - `src/io/xeros/content/dialogue/impl/BossInstanceDialogue.java`
  - `src/io/xeros/content/dailyrewards/DailyRewardsDialogue.java`
  - `src/io/xeros/content/tutorial/TutorialDialogue.java`

## 11. Button And Interface Handling

- Risk: Critical risk
- Main files:
  - `src/io/xeros/model/entity/player/packets/ClickingButtons.java`
  - `src/io/xeros/model/entity/player/packets/ClickingButtonsNew.java`
  - `src/io/xeros/model/entity/player/packets/ContainerAction1.java`
  - `src/io/xeros/model/entity/player/packets/ContainerAction2.java`
  - `src/io/xeros/model/entity/player/packets/ContainerAction3.java`
  - `src/io/xeros/model/entity/player/packets/action/InterfaceAction.java`
- Why it is important: Dispatches nearly every interface click, item-container click, shop action, upgrade/fusion action, battlepass action, collection log action, and minigame UI action.
- What usually depends on it: Shops, banks, upgrades, fusion, collection logs, battlepass, Task Master, leaderboards, daily rewards, teleport UI, and many legacy interfaces.
- Safe changes: Add a small early manager dispatch when the target system already exposes a `handleButton` or `handleItemAction` method.
- Risky changes: Reordering dispatch, changing packet ids, changing container slot semantics, or broad switch refactors.
- Changes future agents should avoid: Do not put reward or economy logic directly in packet handlers when a content manager can own it.
- Safer alternative extension points:
  - Content manager `handleButton(...)` methods.
  - Content manager `handleItemAction(...)` methods.
  - Interface-specific classes under `src/io/xeros/content/`
- Existing pattern files to copy instead:
  - `src/io/xeros/content/fusion/FusionSystem.java`
  - `src/io/xeros/content/upgrade/UpgradeInterface.java`
  - `src/io/xeros/content/leaderboards/LeaderboardInterface.java`

## 12. Combat Damage Calculation

- Risk: Critical risk
- Main files:
  - `src/io/xeros/content/combat/core/AttackEntity.java`
  - `src/io/xeros/content/combat/Damage.java`
  - `src/io/xeros/content/combat/Hitmark.java`
  - `src/io/xeros/content/combat/EntityDamageQueue.java`
  - `src/io/xeros/content/combat/formula/CombatFormula.java`
  - `src/io/xeros/content/combat/formula/rework/CombatFormula.java`
  - `src/io/xeros/content/combat/formula/rework/MeleeCombatFormula.java`
  - `src/io/xeros/content/combat/formula/rework/RangeCombatFormula.java`
  - `src/io/xeros/content/combat/formula/rework/MagicCombatFormula.java`
- Why it is important: Calculates hits, damage timing, accuracy, combat type behavior, charge consumption, and many equipment effects.
- What usually depends on it: All PvM, PvP, NPC auto attacks, Wraith charges, AOE weapons, special attacks, Slayer, and boss balance.
- Safe changes: Extremely narrow equipment or charge hooks only when matching an existing local pattern and tested in combat.
- Risky changes: Formula rewrites, queue timing changes, hitmark changes, combat type assumptions, or changing global accuracy and max-hit math.
- Changes future agents should avoid: Do not alter combat formulas to make one boss easier or one item stronger. Do not bypass existing damage queues.
- Safer alternative extension points:
  - Boss-specific `NPCAutoAttackBuilder` mechanics.
  - Item-specific manager methods such as `src/io/xeros/content/wraith/WraithCharges.java`.
  - AOE weapon definitions under `src/io/xeros/content/items/aoeweapons/`
- Existing pattern files to copy instead:
  - `src/io/xeros/content/bosses/sarachnis/SarachnisNpc.java`
  - `src/io/xeros/content/items/aoeweapons/AoeManager.java`
  - `src/io/xeros/content/wraith/WraithCharges.java`

## 13. NPC Auto Attacks

- Risk: Medium risk
- Main files:
  - `src/io/xeros/content/combat/npc/NPCAutoAttack.java`
  - `src/io/xeros/content/combat/npc/NPCAutoAttackBuilder.java`
  - `src/io/xeros/content/combat/npc/NPCAutoAttackDamage.java`
  - `src/io/xeros/content/bosses/`
- Why it is important: Provides the safer boss-specific path for custom NPC attacks without editing combat core.
- What usually depends on it: Custom bosses, special attacks, multi-target bosses, projectiles, and combat-type mechanics.
- Safe changes: Add boss-specific auto attacks using builder setters and local callbacks.
- Risky changes: Changing builder defaults, shared target selectors, or damage application classes.
- Changes future agents should avoid: Do not edit auto-attack core for one boss unless adding a reusable feature.
- Safer alternative extension points:
  - New or existing boss class under `src/io/xeros/content/bosses/`
  - Local `setOnAttack` and `setOnHit` callbacks.
- Existing pattern files to copy instead:
  - `src/io/xeros/content/bosses/obor/OborNPC.java`
  - `src/io/xeros/content/bosses/sarachnis/SarachnisNpc.java`
  - `src/io/xeros/content/bosses/bryophyta/BryophytaNPC.java`

## 14. Item Definitions

- Risk: High risk
- Main files:
  - `src/io/xeros/model/definitions/ItemDef.java`
  - `src/io/xeros/model/definitions/ItemDefinitionLoader.java`
  - `src/io/xeros/model/definitions/editor/ItemDefEditor.java`
  - `src/io/xeros/model/items/ItemAction.java`
- Why it is important: Defines item names, values, stackability, noted behavior, equipment behavior, and inventory actions.
- What usually depends on it: Inventory, equipment, shops, drops, collection logs, FOE, upgrades, item-on-item, and item-on-object.
- Safe changes: Add item actions with `ItemAction.registerInventory(...)` when appropriate.
- Risky changes: Changing stackability, noted counterparts, values, equipment stats, or global definition loading.
- Changes future agents should avoid: Do not edit item definition loading for a normal content action. Do not change item values casually because shops and FOE can depend on them.
- Safer alternative extension points:
  - `src/io/xeros/model/items/ItemAction.java`
  - `src/io/xeros/content/items/ItemCombinations.java`
  - `src/io/xeros/content/items/UseItem.java` for targeted item-on-object or item-on-item behavior.
- Existing pattern files to copy instead:
  - `src/io/xeros/content/items/ItemCombinations.java`
  - `src/io/xeros/content/wraith/WraithCharges.java`
  - `src/io/xeros/content/items/aoeweapons/AoeWeapons.java`

## 15. NPC Definitions

- Risk: High risk
- Main files:
  - `src/io/xeros/model/definitions/NpcDef.java`
  - `src/io/xeros/model/entity/npc/NPCAction.java`
  - `src/io/xeros/model/entity/npc/NPCSpawning.java`
- Why it is important: Defines NPC names, stats, actions, and default interaction behavior.
- What usually depends on it: NPC clicks, Slayer matching, boss points by NPC name, drop logs, combat, collection log categories, and world event spawns.
- Safe changes: Register a click action with `NPCAction.register(...)` or add content-specific NPC behavior in a boss class.
- Risky changes: Renaming NPCs, changing stats globally, changing default actions for shared NPC ids, or changing spawn behavior.
- Changes future agents should avoid: Do not change definition loading or shared NPC ids to solve one interaction.
- Safer alternative extension points:
  - `src/io/xeros/model/entity/npc/NPCAction.java`
  - `src/io/xeros/model/entity/player/packets/npcoptions/NpcOptionOne.java`
  - `src/io/xeros/content/bosses/`
- Existing pattern files to copy instead:
  - `src/io/xeros/content/skills/fishing/ArielFishing.java`
  - `src/io/xeros/content/dialogue/impl/BossInstanceDialogue.java`
  - `src/io/xeros/content/bosses/obor/OborNPC.java`

## 16. Object Handling

- Risk: High risk
- Main files:
  - `src/io/xeros/model/entity/player/packets/ClickObject.java`
  - `src/io/xeros/model/entity/player/packets/objectoptions/ObjectOptionOne.java`
  - `src/io/xeros/model/entity/player/packets/objectoptions/ObjectOptionTwo.java`
  - `src/io/xeros/model/entity/player/packets/objectoptions/ObjectOptionThree.java`
  - `src/io/xeros/model/entity/player/packets/objectoptions/ObjectOptionFour.java`
  - `src/io/xeros/model/collisionmap/ObjectDef.java`
- Why it is important: Handles object clicks, movement-to-object behavior, object distance, option dispatch, chests, portals, skilling, shops, and minigame objects.
- What usually depends on it: Teleports, minigames, raids, upgrade/fusion stations, chests, world objects, farming, and skilling.
- Safe changes: Add a narrow object case to the correct option handler or the owning instance class.
- Risky changes: Changing object lookup, walking behavior, object distance defaults, option dispatch, or global object definitions.
- Changes future agents should avoid: Do not add instance-only object behavior globally when `InstancedArea` can handle it.
- Safer alternative extension points:
  - `src/io/xeros/content/instances/InstancedArea.java`
  - `src/io/xeros/model/entity/player/packets/objectoptions/impl/RaidObjects.java`
  - Lootable chest classes under `src/io/xeros/content/item/lootable/impl/`
- Existing pattern files to copy instead:
  - `src/io/xeros/content/item/lootable/impl/KonarChest.java`
  - `src/io/xeros/model/entity/player/packets/objectoptions/impl/RaidObjects.java`
  - `src/io/xeros/content/minigames/arbograve/instance/ArbograveInstance.java`

## 17. Teleport Handling

- Risk: High risk
- Main files:
  - `src/io/xeros/content/teleportv2/inter/TeleportInterface.java`
  - `src/io/xeros/content/teleportation/TeleportTablets.java`
  - `src/io/xeros/content/teleportation/TeleportationDevice.java`
  - `src/io/xeros/model/entity/player/PlayerAssistant.java`
  - `src/io/xeros/content/commands/all/Teleport.java`
- Why it is important: Moves players between content, enforces restrictions, and interacts with wilderness, instances, safe zones, and interface favorites.
- What usually depends on it: Boss teleports, minigame teleports, world events, commands, tablets, portal objects, AOE entry, and starter navigation.
- Safe changes: Add entries to existing teleport enums or open an existing teleport interface from a safe command or dialogue.
- Risky changes: Changing teleport lock rules, wilderness restrictions, animation/timer behavior, or coordinates in shared commands.
- Changes future agents should avoid: Do not bypass teleport restrictions by directly moving players in ordinary content unless copying an existing safe command or instance exit pattern.
- Safer alternative extension points:
  - `src/io/xeros/content/teleportv2/inter/TeleportInterface.java`
  - `src/io/xeros/content/dialogue/impl/BossInstanceDialogue.java`
  - World event teleport commands under `src/io/xeros/content/commands/all/`
- Existing pattern files to copy instead:
  - `src/io/xeros/content/commands/all/Leaveaoe.java`
  - `src/io/xeros/content/worldevent/impl/HesporiWorldEvent.java`
  - `src/io/xeros/content/teleportation/TeleportTablets.java`

## 18. Task Scheduling And Process Loops

- Risk: Critical risk
- Main files:
  - `src/io/xeros/Server.java`
  - `src/io/xeros/ServerState.java`
  - `src/io/xeros/model/cycleevent/CycleEvent.java`
  - `src/io/xeros/model/cycleevent/CycleEventContainer.java`
  - `src/io/xeros/model/cycleevent/CycleEventHandler.java`
  - `src/io/xeros/util/task/TaskManager.java`
- Why it is important: Drives server ticks, scheduled events, cycle events, delayed actions, world events, combat timing, and content loops.
- What usually depends on it: NPC processing, player processing, world events, minigames, boss mechanics, timed rewards, and delayed teleports.
- Safe changes: Add content-owned cycle events with clear stop conditions and owner objects.
- Risky changes: Changing global tick rates, event ownership, cancellation behavior, task execution order, or server startup scheduling.
- Changes future agents should avoid: Do not add long-running or unbounded loops. Do not use global process loops for content that can use an instance or manager.
- Safer alternative extension points:
  - Content-local `CycleEvent` with `container.stop()`.
  - `src/io/xeros/content/worldevent/WorldEventContainer.java`
  - Instance lifecycle hooks.
- Existing pattern files to copy instead:
  - `src/io/xeros/content/worldevent/WorldEventContainer.java`
  - `src/io/xeros/content/minigames/raids/Raids.java`
  - `src/io/xeros/content/instances/aoe/AoeInstanceService.java`

## 19. Minigame Instances

- Risk: High risk
- Main files:
  - `src/io/xeros/content/instances/InstancedArea.java`
  - `src/io/xeros/content/instances/InstanceConfiguration.java`
  - `src/io/xeros/content/instances/InstanceConfigurationBuilder.java`
  - `src/io/xeros/content/instances/InstanceHeight.java`
  - `src/io/xeros/content/instances/BossInstanceManager.java`
  - `src/io/xeros/content/minigames/arbograve/instance/ArbograveInstance.java`
  - `src/io/xeros/content/minigames/tob/instance/TobInstance.java`
- Why it is important: Isolates players, NPCs, objects, rewards, cleanup, height allocation, and minigame state.
- What usually depends on it: Boss instances, raids, AOE, donor Slayer instances, TOB, TOA, Arbograve, Nightmare, and legacy solo instances.
- Safe changes: Add a new instance by copying an existing instance class and using established cleanup hooks.
- Risky changes: Changing height allocation, shared cleanup, player removal, reward eligibility, or object click routing.
- Changes future agents should avoid: Do not put instance-specific state in global object/NPC handlers.
- Safer alternative extension points:
  - `src/io/xeros/content/instances/impl/LegacySoloPlayerInstance.java`
  - Existing minigame instance packages.
  - Lootable reward chests.
- Existing pattern files to copy instead:
  - `src/io/xeros/content/minigames/arbograve/instance/ArbograveInstance.java`
  - `src/io/xeros/content/bosses/obor/OborInstance.java`
  - `src/io/xeros/content/bosses/nightmare/NightmareInstance.java`

## 20. AOE Instances

- Risk: High risk
- Main files:
  - `src/io/xeros/content/instances/aoe/AoeTierController.java`
  - `src/io/xeros/content/instances/aoe/AoeInstanceService.java`
  - `src/io/xeros/content/instances/aoe/AoeBossTierLoader.java`
  - `src/io/xeros/content/instances/aoe/AoeTierRewardsLoader.java`
  - `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`
  - `src/io/xeros/content/instances/aoe/AoeDropInterceptor.java`
  - `data/aoe/aoe_boss_tiers.json`
  - `data/aoe/aoe_tier_rewards.json`
  - `data/aoe/AoeZoneMapConfig.json`
- Why it is important: Controls AOE tier entry, map building, kill counts, unlocks, reward banking, per-kill XP, end-of-run rewards, and persistence.
- What usually depends on it: AOE progression, AOE weapons, instance cleanup, rewards, save data, dialogue tier selection, and future AOE retention loops.
- Safe changes: JSON-only tier reward and tier definition changes when supported by loaders.
- Risky changes: Instance building, teardown, active-tier attributes, saved key names, and death hooks.
- Changes future agents should avoid: Do not rewrite AOE instances to add one reward. Do not bypass `AoeTierController`.
- Safer alternative extension points:
  - `data/aoe/aoe_tier_rewards.json`
  - `data/aoe/aoe_boss_tiers.json`
  - `src/io/xeros/content/instances/aoe/AoeTierEvents.java`
  - `src/io/xeros/content/instances/aoe/AoeDropInterceptor.java`
- Existing pattern files to copy instead:
  - `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`
  - `src/io/xeros/content/instances/aoe/AoeTierController.java`
  - `src/io/xeros/content/dialogue/impl/BossInstanceDialogue.java`

## 21. World Events

- Risk: Medium risk
- Main files:
  - `src/io/xeros/content/worldevent/WorldEvent.java`
  - `src/io/xeros/content/worldevent/WorldEventContainer.java`
  - `src/io/xeros/content/worldevent/WorldEventState.java`
  - `src/io/xeros/content/worldevent/impl/HesporiWorldEvent.java`
  - `src/io/xeros/content/worldevent/impl/WildernessBossWorldEvent.java`
  - `src/io/xeros/content/worldevent/impl/TournamentWorldEvent.java`
- Why it is important: Rotates scheduled events, announces status, controls current event state, and gives players periodic reasons to log in.
- What usually depends on it: World boss spawns, event teleports, quest tab status, broadcasts, Discord notifications, and event cleanup.
- Safe changes: Add a new `WorldEvent` implementation and register it in `WORLD_EVENT_LIST`.
- Risky changes: Changing event rotation timing, state persistence, disposal behavior, or global reward grants.
- Changes future agents should avoid: Do not put event-specific rewards directly in `WorldEventContainer` unless all events share them.
- Safer alternative extension points:
  - `src/io/xeros/content/worldevent/impl/`
  - Event-specific boss or reward manager.
- Existing pattern files to copy instead:
  - `src/io/xeros/content/worldevent/impl/HesporiWorldEvent.java`
  - `src/io/xeros/content/worldevent/impl/WildernessBossWorldEvent.java`

## 22. Activity And Global Bosses

- Risk: High risk
- Main files:
  - `src/io/xeros/content/activityboss/GlobalBossActivityManager.java`
  - `src/io/xeros/content/activityboss/ActivityType.java`
  - `src/io/xeros/content/activityboss/GlobalBossType.java`
  - `src/io/xeros/content/activityboss/GlobalBossDropHandler.java`
  - `src/io/xeros/content/activityboss/GlobalBossContributionTracker.java`
  - `src/io/xeros/content/activityboss/GlobalBossLootTable.java`
- Why it is important: Turns server-wide player activity into global boss spawns and participant rewards.
- What usually depends on it: Upgrades, FOE burns, vote claims, clue caskets, killstreaks, broadcasts, and multi-player rewards.
- Safe changes: Add a new `ActivityType` and `GlobalBossType` with conservative thresholds, then record progress from an existing content hook.
- Risky changes: Lowering thresholds too far, changing cooldown behavior, changing contribution logic, or inflating global boss loot.
- Changes future agents should avoid: Do not spawn activity bosses from random content without using `GlobalBossActivityManager.record(...)`.
- Safer alternative extension points:
  - `src/io/xeros/content/activityboss/ActivityType.java`
  - `src/io/xeros/content/activityboss/GlobalBossType.java`
  - `src/io/xeros/content/activityboss/GlobalBossLootTable.java`
- Existing pattern files to copy instead:
  - `src/io/xeros/content/fireofexchange/FireOfExchange.java`
  - `src/io/xeros/content/upgrade/UpgradeInterface.java`
  - `src/io/xeros/content/vote_panel/VotePanelManager.java`

## 23. Slayer

- Risk: High risk
- Main files:
  - `src/io/xeros/content/skills/slayer/Slayer.java`
  - `src/io/xeros/content/skills/slayer/Task.java`
  - `src/io/xeros/content/skills/slayer/SlayerMaster.java`
  - `src/io/xeros/content/skills/slayer/SlayerUnlock.java`
  - `src/io/xeros/content/skills/slayer/DemonHunterSlayerDialogue.java`
  - `src/io/xeros/content/skills/slayer/DemonHunterTaskManager.java`
  - `src/io/xeros/content/skills/slayer/DemonSlayerContract.java`
- Why it is important: Controls Slayer tasks, task progress, rewards, unlocks, Demon Hunter progression, and task-based PvM loops.
- What usually depends on it: NPC kills, Slayer points, Demon Marks, Demon Hunter tasks, achievements, Task Master overlap, and shops.
- Safe changes: Add task entries or rewards by copying existing task and reward patterns.
- Risky changes: Changing task assignment, kill matching, Slayer point payout, or task completion behavior.
- Changes future agents should avoid: Do not change Slayer core to add one boss unless the boss needs a new task category.
- Safer alternative extension points:
  - `src/io/xeros/content/skills/slayer/Task.java`
  - `src/io/xeros/content/skills/slayer/SlayerRewardsInterface.java`
  - `src/io/xeros/content/skills/slayer/DemonMarkRewardHandler.java`
- Existing pattern files to copy instead:
  - `src/io/xeros/content/skills/slayer/DemonHunterSlayerDialogue.java`
  - `src/io/xeros/content/skills/slayer/DemonSlayerContract.java`
  - `src/io/xeros/content/skills/slayer/SlayerRewardsInterface.java`

## 24. Achievements

- Risk: Medium risk
- Main files:
  - `src/io/xeros/content/achievement/Achievements.java`
  - `src/io/xeros/content/achievement/AchievementHandler.java`
  - `src/io/xeros/content/achievement/AchievementType.java`
  - `src/io/xeros/content/achievement/AchievementTier.java`
- Why it is important: Tracks long-term player milestones and grants achievement rewards.
- What usually depends on it: Boss kills, skilling, voting, FOE, upgrades, collection logs, daily rewards, Slayer, and raids.
- Safe changes: Add a new `AchievementType`, add an enum entry, and increment with `Achievements.increase(...)`.
- Risky changes: Changing achievement ids, save parsing, claim logic, reward payout, or login fix-up behavior.
- Changes future agents should avoid: Do not reuse ids inside the same tier. Do not grant high-value rewards without economy review.
- Safer alternative extension points:
  - `src/io/xeros/content/achievement/AchievementType.java`
  - Existing content hooks that already call `Achievements.increase(...)`.
- Existing pattern files to copy instead:
  - `src/io/xeros/content/dailyrewards/DailyRewards.java`
  - `src/io/xeros/content/fireofexchange/FireOfExchange.java`
  - `src/io/xeros/content/collection_log/CollectionLog.java`

## 25. Task Master

- Risk: Medium risk
- Main files:
  - `src/io/xeros/content/taskmaster/Tasks.java`
  - `src/io/xeros/content/taskmaster/TaskMaster.java`
  - `src/io/xeros/content/taskmaster/TaskMasterKills.java`
  - `src/io/xeros/content/taskmaster/TaskDifficulty.java`
  - `src/io/xeros/content/taskmaster/TaskType.java`
- Why it is important: Drives daily, weekly, hourly, combat, and skilling task loops with repeatable rewards.
- What usually depends on it: Player retention, boss grind loops, skilling loops, boxes, lamps, and task completion counts.
- Safe changes: Add a `Tasks` enum entry or hook progress from existing activity code.
- Risky changes: Changing generation pools, save/load format, reset timing, or reward amounts.
- Changes future agents should avoid: Do not create a parallel task system for content that fits Task Master.
- Safer alternative extension points:
  - `src/io/xeros/content/taskmaster/Tasks.java`
  - Activity hooks that call `TaskMasterKills.incrementAmountKilled(...)`.
- Existing pattern files to copy instead:
  - `src/io/xeros/content/taskmaster/Tasks.java`
  - `src/io/xeros/content/taskmaster/TaskMaster.java`

## 26. Collection Logs

- Risk: Medium risk
- Main files:
  - `src/io/xeros/content/collection_log/CollectionLog.java`
  - `src/io/xeros/content/collection_log/CollectionRewards.java`
  - `src/io/xeros/model/entity/npc/drops/TableGroup.java`
- Why it is important: Tracks rare drops and completion rewards, feeding long-term boss and item collection goals.
- What usually depends on it: Drop tables, chests, pets, rare upgrades, AOE weapons, achievements, collection rewards, and group ironman logs.
- Safe changes: Add a collection reward or call `handleDrop(...)` from a special reward source.
- Risky changes: Changing save directory behavior, tab mapping, item matching, reward claim logic, or rare-drop integration.
- Changes future agents should avoid: Do not manually edit player collection log files. Do not bypass `handleDrop(...)`.
- Safer alternative extension points:
  - `src/io/xeros/content/collection_log/CollectionRewards.java`
  - Drop table rare rewards.
  - Chest reward classes under `src/io/xeros/content/item/lootable/impl/`
- Existing pattern files to copy instead:
  - `src/io/xeros/content/item/lootable/impl/RaidsChestRare.java`
  - `src/io/xeros/content/minigames/raids/Raids.java`
  - `src/io/xeros/content/upgrade/UpgradeInterface.java`

## 27. Battlepass

- Risk: Medium risk
- Main files:
  - `src/io/xeros/content/battlepass/Pass.java`
  - `src/io/xeros/content/battlepass/Rewards.java`
  - `src/io/xeros/content/battlepass/RewardList.java`
- Why it is important: Provides seasonal progression, tier XP, member rewards, and recurring retention rewards.
- What usually depends on it: Login, playtime, reward generation, season reset, reward claims, and player save fields.
- Safe changes: Add conservative rewards or grant XP through `Pass.addExperience(...)` from safe activities.
- Risky changes: Changing season rollover, tier thresholds, reward grant behavior, or restricted-area checks.
- Changes future agents should avoid: Do not grant battlepass XP from every combat tick or unrestricted loop.
- Safer alternative extension points:
  - `src/io/xeros/content/battlepass/RewardList.java`
  - Existing activity completion hooks.
- Existing pattern files to copy instead:
  - `src/io/xeros/content/battlepass/Pass.java`
  - `src/io/xeros/content/battlepass/Rewards.java`

## 28. Fire Of Exchange

- Risk: High risk
- Main files:
  - `src/io/xeros/content/fireofexchange/FireOfExchange.java`
  - `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`
  - `src/io/xeros/model/shops/ShopAssistant.java`
- Why it is important: Converts items into upgrade/foundry points, gives Fortune XP, updates achievements, event calendar, leaderboards, activity boss progress, and burn history.
- What usually depends on it: Item sinks, upgrade economy, FOE pets/shop, Wraith progression, Fortune progression, global boss triggers, and long-term economy health.
- Safe changes: Add or tune burn values carefully in the burn price source.
- Risky changes: Changing burn execution, item blocking, noted item handling, point multipliers, or the shop price display path.
- Changes future agents should avoid: Do not grant foundry points outside established systems without economy review. Do not remove item blocks casually.
- Safer alternative extension points:
  - `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`
  - `src/io/xeros/content/upgrade/UpgradeMaterials.java`
  - Reward audits in `docs/TURMOIL_REWARD_ECONOMY_AUDIT.md`
- Existing pattern files to copy instead:
  - `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`
  - `src/io/xeros/content/commands/all/Burnhistory.java`

## 29. Upgrade System

- Risk: High risk
- Main files:
  - `src/io/xeros/content/upgrade/UpgradeMaterials.java`
  - `src/io/xeros/content/upgrade/UpgradeInterface.java`
  - `src/io/xeros/model/entity/player/packets/ClickingButtons.java`
  - `src/io/xeros/model/entity/player/packets/ContainerAction1.java`
  - `src/io/xeros/model/entity/player/packets/ContainerAction3.java`
- Why it is important: Drives major item sinks, gear progression, Fortune XP, rare broadcasts, collection logs, achievements, and activity boss progress.
- What usually depends on it: Foundry points, Fire of Exchange, collection log rare categories, endgame gear, and progression pacing.
- Safe changes: Add new `UpgradeMaterials` recipes with conservative costs, rates, and rewards.
- Risky changes: Changing roll logic, cost consumption, failure behavior, rare classification, or interface packet handling.
- Changes future agents should avoid: Do not duplicate upgrade logic elsewhere for normal recipes.
- Safer alternative extension points:
  - `src/io/xeros/content/upgrade/UpgradeMaterials.java`
  - `src/io/xeros/content/fusion/FusionMaterials.java` for separate fusion content.
- Existing pattern files to copy instead:
  - `src/io/xeros/content/upgrade/UpgradeMaterials.java`
  - `src/io/xeros/content/fusion/FusionMaterials.java`

## 30. Wraith Charges

- Risk: Medium risk
- Main files:
  - `src/io/xeros/content/wraith/WraithCharges.java`
  - `src/io/xeros/content/items/UseItem.java`
  - `src/io/xeros/content/commands/all/Wraith.java`
  - `src/io/xeros/content/commands/all/Wraithcharges.java`
  - `src/io/xeros/content/combat/core/AttackEntity.java`
- Why it is important: Controls Wraith Essence charging, Wraith weapon caps, charge consumption, command access, and persistent weapon charge loops.
- What usually depends on it: Wraith weapons, endgame item sinks, combat charge consumption, player save fields, and Wraith milestone content.
- Safe changes: Add Wraith weapon ids, tune caps or essence conversion with owner review, or add milestone hooks around existing methods.
- Risky changes: Changing atomic essence consumption, cap logic, combat consumption, or save field semantics.
- Changes future agents should avoid: Do not consume charges directly in scattered combat code; use `WraithCharges.consumeCharge(...)`.
- Safer alternative extension points:
  - `src/io/xeros/content/wraith/WraithCharges.java`
  - `src/io/xeros/content/commands/all/Wraith.java`
  - `src/io/xeros/content/items/UseItem.java` only for item-on-item entry.
- Existing pattern files to copy instead:
  - `src/io/xeros/content/wraith/WraithCharges.java`
  - `src/io/xeros/content/commands/all/Wraith.java`

## 31. Donator Systems

- Risk: High risk
- Main files:
  - `src/io/xeros/content/donationrewards/DonationRewards.java`
  - `src/io/xeros/content/donationrewards/DonationRewardsPlayerSaveEntry.java`
  - `src/io/xeros/content/donor/DonorVault.java`
  - `src/io/xeros/content/donor/DonoSlayerInstances.java`
  - `src/io/xeros/content/donor/CosmeticManager.java`
  - `src/io/xeros/content/bosses/DonorBoss.java`
  - `src/io/xeros/content/bosses/DonorBoss2.java`
  - `src/io/xeros/content/bosses/DonorBoss3.java`
  - `src/io/xeros/sql/donation/query/ClaimDonationsQuery.java`
- Why it is important: Handles paid reward claims, donor perks, donor bosses, donor vault behavior, and account value.
- What usually depends on it: Donation claims, donor rank benefits, donor-only bosses, donor shops, cosmetics, and save entries.
- Safe changes: Add donor content by copying existing donor content classes and keeping reward values conservative.
- Risky changes: Changing donation claim queries, donor entitlement checks, rank checks, or reward duplication prevention.
- Changes future agents should avoid: Do not grant donor rewards from ordinary commands. Do not bypass donation claim flow.
- Safer alternative extension points:
  - `src/io/xeros/content/donor/`
  - `src/io/xeros/content/bosses/DonorBoss.java`
  - `src/io/xeros/content/donationrewards/DonationRewardsPlayerSaveEntry.java`
- Existing pattern files to copy instead:
  - `src/io/xeros/content/donor/DonorVault.java`
  - `src/io/xeros/content/donor/DonoSlayerInstances.java`
  - `src/io/xeros/content/commands/donator/HideDonor.java`

## 32. Economy And Currency Systems

- Risk: Critical risk
- Main files:
  - `src/io/xeros/content/bosspoints/BossPoints.java`
  - `src/io/xeros/content/vote_panel/VotePanelManager.java`
  - `src/io/xeros/content/fireofexchange/FireOfExchange.java`
  - `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`
  - `src/io/xeros/content/PlatinumTokens.java`
  - `src/io/xeros/content/wogw/Wogw.java`
  - `src/io/xeros/content/prestige/PrestigeSkills.java`
  - `src/io/xeros/content/prestige/PrestigePerks.java`
  - `src/io/xeros/model/shops/ShopAssistant.java`
  - `src/io/xeros/model/items/ItemAssistant.java`
- Why it is important: Controls currency creation, spending, conversion, point stores, item sinks, player power, and long-term inflation.
- What usually depends on it: Shops, boss points, vote rewards, upgrade points, Wraith Essence, Slayer points, prestige perks, WOGW, daily rewards, battlepass, collection rewards, and donor rewards.
- Safe changes: Small reward additions that follow `docs/TURMOIL_REWARD_ECONOMY_AUDIT.md` and reuse existing reward managers.
- Risky changes: Increasing point faucets, adding high-value items to repeatable content, changing shop prices, changing burn values, or adding new currency fields without save review.
- Changes future agents should avoid: Do not casually reward foundry points, Wraith Essence, rare boxes, donor items, or best-in-slot gear. Do not bypass shops, FOE, or point managers.
- Safer alternative extension points:
  - `src/io/xeros/content/bosspoints/BossPoints.java`
  - `src/io/xeros/content/achievement/Achievements.java`
  - `src/io/xeros/content/taskmaster/Tasks.java`
  - `src/io/xeros/content/collection_log/CollectionRewards.java`
  - `data/aoe/aoe_tier_rewards.json`
- Existing pattern files to copy instead:
  - `src/io/xeros/content/dailyrewards/DailyRewardContainer.java`
  - `src/io/xeros/content/item/lootable/impl/KonarChest.java`
  - `src/io/xeros/content/battlepass/RewardList.java`

## A. Critical Files Future Codex Tasks Should Almost Never Rewrite

- `src/io/xeros/model/entity/player/Player.java`
- `src/io/xeros/model/entity/player/save/PlayerSave.java`
- `src/io/xeros/model/entity/npc/NPCHandler.java`
- `src/io/xeros/model/entity/npc/NPCProcess.java`
- `src/io/xeros/content/combat/death/NPCDeath.java`
- `src/io/xeros/model/entity/npc/drops/DropManager.java`
- `src/io/xeros/model/entity/npc/drops/TableGroup.java`
- `src/io/xeros/model/shops/ShopAssistant.java`
- `src/io/xeros/model/entity/player/packets/ClickingButtons.java`
- `src/io/xeros/model/entity/player/packets/Commands.java`
- `src/io/xeros/content/combat/core/AttackEntity.java`
- `src/io/xeros/content/combat/formula/CombatFormula.java`
- `src/io/xeros/Server.java`
- `src/io/xeros/model/cycleevent/CycleEventHandler.java`

## B. Safe Files, Enums, And Configs For Adding Content

- `src/io/xeros/content/commands/all/`
- `src/io/xeros/content/commands/admin/`
- `src/io/xeros/content/dialogue/impl/`
- `src/io/xeros/content/bosses/`
- `src/io/xeros/content/upgrade/UpgradeMaterials.java`
- `src/io/xeros/content/fusion/FusionMaterials.java`
- `src/io/xeros/content/taskmaster/Tasks.java`
- `src/io/xeros/content/achievement/AchievementType.java`
- `src/io/xeros/content/achievement/Achievements.java`
- `src/io/xeros/content/collection_log/CollectionRewards.java`
- `src/io/xeros/content/battlepass/RewardList.java`
- `src/io/xeros/content/activityboss/ActivityType.java`
- `src/io/xeros/content/activityboss/GlobalBossType.java`
- `data/aoe/aoe_boss_tiers.json`
- `data/aoe/aoe_tier_rewards.json`
- `data/aoe/AoeZoneMapConfig.json`

## C. Best Data-Driven Systems

- AOE tiers and rewards through `data/aoe/aoe_boss_tiers.json` and `data/aoe/aoe_tier_rewards.json`.
- Upgrade recipes through `src/io/xeros/content/upgrade/UpgradeMaterials.java`.
- Fusion recipes through `src/io/xeros/content/fusion/FusionMaterials.java`.
- Task Master tasks through `src/io/xeros/content/taskmaster/Tasks.java`.
- Battlepass reward lists through `src/io/xeros/content/battlepass/RewardList.java`.
- Collection rewards through `src/io/xeros/content/collection_log/CollectionRewards.java`.
- Activity boss triggers through `src/io/xeros/content/activityboss/ActivityType.java` and `src/io/xeros/content/activityboss/GlobalBossType.java`.
- Runtime drop tables and runtime shop definitions are data-driven, but the complete data files are not repo-local.

## D. Best Managers And Enums To Extend

- `src/io/xeros/content/commands/Command.java`
- `src/io/xeros/content/dialogue/DialogueBuilder.java`
- `src/io/xeros/model/entity/npc/NPCAction.java`
- `src/io/xeros/model/items/ItemAction.java`
- `src/io/xeros/content/items/ItemCombinations.java`
- `src/io/xeros/content/bosspoints/BossPoints.java`
- `src/io/xeros/content/achievement/Achievements.java`
- `src/io/xeros/content/taskmaster/Tasks.java`
- `src/io/xeros/content/collection_log/CollectionRewards.java`
- `src/io/xeros/content/battlepass/Pass.java`
- `src/io/xeros/content/activityboss/GlobalBossActivityManager.java`
- `src/io/xeros/content/worldevent/WorldEvent.java`
- `src/io/xeros/model/entity/player/save/PlayerSaveEntry.java`

## E. Systems That Need Manual Owner Review Before Merging

- Player save changes, especially `src/io/xeros/model/entity/player/save/PlayerSave.java`.
- Combat formula or damage changes under `src/io/xeros/content/combat/`.
- Drop engine changes under `src/io/xeros/model/entity/npc/drops/`.
- Shop engine changes under `src/io/xeros/model/shops/ShopAssistant.java`.
- Economy reward changes involving foundry points, Wraith Essence, donor rewards, rare boxes, vote points, or best-in-slot gear.
- Donator claim and entitlement changes under `src/io/xeros/content/donationrewards/` and `src/io/xeros/sql/donation/`.
- NPC global process changes under `src/io/xeros/model/entity/npc/NPCProcess.java`.
- Server tick, task, or cycle event changes under `src/io/xeros/Server.java` and `src/io/xeros/model/cycleevent/`.
- Definition loading changes under `src/io/xeros/model/definitions/`.

## F. Pre-Merge Checklist For All Future Codex PRs

- Confirm the task did not rewrite a critical core file when a manager, enum, data file, or content class would work.
- Search for an existing matching system and copy the closest pattern.
- Verify all new persistent data uses `PlayerSaveEntry` or an existing content store.
- Verify reward amounts against `docs/TURMOIL_REWARD_ECONOMY_AUDIT.md`.
- Verify drops use the drop system or chest classes instead of hardcoded death rewards.
- Verify commands are subclasses of `Command`.
- Verify dialogues use `DialogueBuilder`.
- Verify achievements, Task Master, battlepass, collection logs, boss points, and activity bosses are hooked only when needed.
- Test login, logout, relog persistence, and one failure path for any progression feature.
- Test reward duplication prevention for any chest, boss, minigame, shop, or item sink.
- Explain changed files and test steps in the PR summary.
