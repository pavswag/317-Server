# Turmoil Testing And Debug Tool Map

This map documents the existing testing, debug, reload, owner, admin, and developer tools that can be used to verify Turmoil content safely. It is a documentation-only guide for future Codex tasks.

## Safety Labels

- Read-only: Safe for inspection on local, staging, and usually live servers.
- Local/staging only: Safe for development and staging, but should not be used on a live economy unless the owner explicitly approves it.
- NEVER LIVE ECONOMY: Creates items, points, drops, progression, or destructive state and should not be used on production accounts or a live economy.
- Owner-review reload: Can be useful after data edits, but can affect live players, NPCs, shops, drops, or definitions.

## Command Loading And Discovery

### Command subclass loader
- Main file: src/io/xeros/content/commands/CommandManager.java
- Command name or method name: initializeCommands(), getCommand(String)
- Required rank/permission: Commands use each subclass hasPrivilege(Player); commands under src/io/xeros/content/commands/test/ load only when Server.isDebug() or Server.isTest().
- What it does: Scans command packages with ClassGraph, instantiates Command subclasses, and maps command names to lower-case class names unless a subclass overrides getCommand().
- Systems it affects: All modern command packages under src/io/xeros/content/commands/.
- Safe use cases: Search command availability, confirm whether a command is debug-only, and add new command subclasses through the existing command package pattern.
- Dangerous use cases: Editing CommandManager for ordinary content commands; command registration mistakes can hide or override existing commands.
- Example test procedure: Use src/io/xeros/content/commands/all/Commands.java and src/io/xeros/content/commands/all/Staffcommands.java to inspect command list behavior, then verify the target command subclass has the intended hasPrivilege(Player) gate.

### Base command pattern
- Main file: src/io/xeros/content/commands/Command.java
- Command name or method name: execute(Player, String, String[]), hasPrivilege(Player), getCommand()
- Required rank/permission: Defined per subclass.
- What it does: Defines the modern command contract and default command naming.
- Systems it affects: All subclass command behavior.
- Safe use cases: Copy for new command recipes and rank checks.
- Dangerous use cases: Changing default command naming globally.
- Example test procedure: Find a nearby command in src/io/xeros/content/commands/admin/, copy its hasPrivilege(Player) style, and test with the expected rank.

### Legacy packet command handler
- Main file: src/io/xeros/model/entity/player/packets/Commands.java
- Command name or method name: processPacket(Player, int, int)
- Required rank/permission: Mixed inline checks inside the legacy handler.
- What it does: Receives raw command text from the client, writes command logs, and routes to CommandManager plus remaining legacy commands.
- Systems it affects: Command logging, legacy commands, and modern command dispatch.
- Safe use cases: Trace why a command did not execute, inspect old management-only commands, and verify command logging.
- Dangerous use cases: Adding new ordinary content commands here when a Command subclass can be used instead.
- Example test procedure: Trigger a command locally, confirm the modern subclass is discovered by CommandManager, then inspect the command log source in src/io/xeros/util/logging/player/CommandLog.java if needed.

## Test Commands

These commands live in src/io/xeros/content/commands/test/ and only load on debug or test servers through CommandManager.

### Drop sample bank test
- Main file: src/io/xeros/content/commands/test/DropTest.java
- Command name or method name: ::droptest npc_id amount
- Required rank/permission: Primary right must be Right.GAME_DEVELOPER; test package must be loaded by Server.isDebug() or Server.isTest().
- What it does: Calls Server.getDropManager().getDropSample(player, npcId) repeatedly, clears the player's bank, places sampled drops into the bank, and opens the bank.
- Systems it affects: DropManager, player bank, NPC drop sampling.
- Safe use cases: Local-only sampling of expected NPC drop distributions.
- Dangerous use cases: NEVER LIVE ECONOMY: It deletes the current bank contents before adding sampled drops.
- Example test procedure: On a throwaway local developer account, run ::droptest 1 100, inspect the bank sample, then discard the test account state.

### Item spawner test menu
- Main file: src/io/xeros/content/commands/test/Spawn.java
- Command name or method name: ::spawn
- Required rank/permission: Primary right must be Right.GAME_DEVELOPER; test package must be loaded by Server.isDebug() or Server.isTest().
- What it does: Opens ItemSpawner for the player.
- Systems it affects: ItemSpawner, inventory, item definitions.
- Safe use cases: Local item spawning for UI, combat, upgrade, and reward testing.
- Dangerous use cases: NEVER LIVE ECONOMY: It can create arbitrary items.
- Example test procedure: Use ::spawn on a local server, spawn only the items needed for the target test, then validate the target content without saving or transferring spawned items.

### Complete achievement test
- Main file: src/io/xeros/content/commands/test/Completeachievement.java
- Command name or method name: ::completeachievement all, ::completeachievement achievement_type
- Required rank/permission: Primary right must be Right.GAME_DEVELOPER; test package must be loaded by Server.isDebug() or Server.isTest().
- What it does: Calls Achievements.increase(player, type, 2000000000) for one achievement type or all achievement types.
- Systems it affects: Achievements, achievement points, persistent player achievement progress.
- Safe use cases: Local claim interface and reward validation.
- Dangerous use cases: NEVER LIVE ECONOMY: It completes achievement progression and can unlock rewards.
- Example test procedure: Run ::completeachievement all on a local throwaway player, open the achievement interface, verify claim behavior, and reset the test save afterward.

### Bulk point test
- Main file: src/io/xeros/content/commands/test/Points.java
- Command name or method name: ::points
- Required rank/permission: Primary right must be Right.GAME_DEVELOPER; test package must be loaded by Server.isDebug() or Server.isTest().
- What it does: Sets many point fields to 100000, including bossPoints, Slayer points, donatorPoints, pest control points, votePoints, achievementPoints, vote panel points, tournamentPoints, foundryPoints, pkp, and amDonated.
- Systems it affects: Boss points, Slayer points, vote panel, achievement points, foundry points, donation progression, PKP, shops, player save data.
- Safe use cases: Local shop and interface smoke tests.
- Dangerous use cases: NEVER LIVE ECONOMY: It inflates multiple currencies and donation state at once.
- Example test procedure: Use only on a local disposable account, open each relevant shop/interface, then discard the save.

### Slayer task test
- Main file: src/io/xeros/content/commands/test/Slayertask.java
- Command name or method name: ::slayertask task_name
- Required rank/permission: Primary right must be Right.GAME_DEVELOPER; test package must be loaded by Server.isDebug() or Server.isTest().
- What it does: Finds a SlayerMaster containing the named task, sets the player's current Slayer master, task, and random task amount.
- Systems it affects: Slayer task assignment, Slayer progress, player save data.
- Safe use cases: Local Slayer kill hook testing for a specific NPC family.
- Dangerous use cases: Local/staging only: It mutates player task state and can interfere with real Slayer progression.
- Example test procedure: Search src/io/xeros/content/skills/slayer/ for the task name, run ::slayertask matching_name locally, kill one matching NPC, and verify task decrement behavior.

### General developer test harness
- Main file: src/io/xeros/content/commands/test/Test.java
- Command name or method name: ::test subcommand
- Required rank/permission: Primary right must be Right.GAME_DEVELOPER; test package must be loaded by Server.isDebug() or Server.isTest().
- What it does: Contains many ad hoc developer subcommands for GIM logs, collection checks, leaderboards, lost items, max cape checks, private messages, lock/unlock, combat requirement checks, skill set tests, raid reward tests, boss object tests, vote bonus tests, scroll bonus tests, donation rank upgrade tests, walking, placeholders, save/load, teleport, PK counters, and other one-off diagnostics.
- Systems it affects: Mixed systems including skills, raids, voting, items, saves, combat, teleports, and player attributes.
- Safe use cases: Inspect the exact subcommand before using it on local or staging.
- Dangerous use cases: NEVER LIVE ECONOMY for subcommands that alter skills, rewards, items, donation state, saves, raids, or boss rewards.
- Example test procedure: Open src/io/xeros/content/commands/test/Test.java, inspect the selected subcommand branch, then run only that branch on a disposable local account.

### Donation reward test
- Main file: src/io/xeros/content/commands/test/TestDonationRewards.java
- Command name or method name: ::testdonationrewards amount
- Required rank/permission: Primary right must be Right.GAME_DEVELOPER; test package must be loaded by Server.isDebug() or Server.isTest().
- What it does: Calls player.getDonationRewards().increaseDonationAmount(amount).
- Systems it affects: Donation rewards and persistent donation progression.
- Safe use cases: Local donor reward interface testing.
- Dangerous use cases: NEVER LIVE ECONOMY: It mutates donation reward progress.
- Example test procedure: Run ::testdonationrewards 100 on local, inspect the donation reward flow, then discard the test account save.

### Ground item stress test
- Main file: src/io/xeros/content/commands/test/Grounditems.java
- Command name or method name: ::grounditems amount
- Required rank/permission: Primary right must be Right.GAME_DEVELOPER; test package must be loaded by Server.isDebug() or Server.isTest().
- What it does: Spawns repeated ground items at the player.
- Systems it affects: Ground item handling and item visibility.
- Safe use cases: Local ground item rendering or cleanup tests.
- Dangerous use cases: NEVER LIVE ECONOMY: It creates items on the ground.
- Example test procedure: Use a small amount locally, inspect item rendering and pickup behavior, then restart or clean the test state.

### Self-hit test
- Main file: src/io/xeros/content/commands/test/Hit.java
- Command name or method name: ::hit damage
- Required rank/permission: Primary right must be Right.GAME_DEVELOPER; test package must be loaded by Server.isDebug() or Server.isTest().
- What it does: Applies damage to the player.
- Systems it affects: Player hitpoints and damage display.
- Safe use cases: Local damage display, death safety, and UI testing.
- Dangerous use cases: Local/staging only: It can kill or disrupt the player.
- Example test procedure: Use ::hit 1 locally to verify hit splat behavior, then test larger values only on disposable accounts.

### Game mode test placeholder
- Main file: src/io/xeros/content/commands/test/SetGameMode.java
- Command name or method name: ::setgamemode
- Required rank/permission: Primary right must be Right.GAME_DEVELOPER; test package must be loaded by Server.isDebug() or Server.isTest().
- What it does: The implementation is commented out and currently acts as a no-op or usage message.
- Systems it affects: No active game mode mutation found in the current implementation.
- Safe use cases: Not useful as a current test tool.
- Dangerous use cases: Do not assume it changes game mode.
- Example test procedure: Not found in active behavior. Searched terms: setgamemode, GameMode.forType, player.setGameMode.

## Owner And Admin Spawn Tools

### Owner item spawn
- Main file: src/io/xeros/content/commands/owner/Spawnitem.java
- Command name or method name: ::spawnitem itemId amount, ::spawnitem fromId toId amount
- Required rank/permission: Right.GAME_DEVELOPER and staff position.
- What it does: Adds one item or an item ID range to the player's inventory after checking ItemDef.getDefinitions().
- Systems it affects: Inventory, item definitions, economy.
- Safe use cases: Local item definition and content testing.
- Dangerous use cases: NEVER LIVE ECONOMY: It creates arbitrary items.
- Example test procedure: Spawn only the exact item required for a local test, verify behavior, and discard the save.

### Give item to target through spawner
- Main file: src/io/xeros/content/commands/owner/Giveitem.java
- Command name or method name: ::giveitem player_name
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Opens ItemSpawner with a target player attribute and blocks ironman targets unless the executor is staff manager.
- Systems it affects: ItemSpawner, target player inventory, player attributes, economy.
- Safe use cases: Local multi-account reward delivery tests.
- Dangerous use cases: NEVER LIVE ECONOMY unless the owner is intentionally issuing compensation.
- Example test procedure: Use two local test accounts, run ::giveitem target_name, spawn a low-value item, and confirm target inventory behavior.

### Admin spawn menu
- Main file: src/io/xeros/content/commands/admin/SpawnMenu.java
- Command name or method name: ::spawnmenu
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Opens ItemSpawner interface 43214 and clears the give-item target attribute.
- Systems it affects: ItemSpawner, inventory, economy.
- Safe use cases: Local/admin item testing.
- Dangerous use cases: NEVER LIVE ECONOMY: It can create arbitrary items.
- Example test procedure: Open ::spawnmenu on local, search for a test item, spawn one copy, and verify the target system.

### Item spawner backend
- Main file: src/io/xeros/content/ItemSpawner.java
- Command name or method name: open(Player), spawn(Player, int, int), TARGET_ATTRIBUTE_KEY
- Required rank/permission: Used by command wrappers; spawn logic permits administrators and PVP contexts.
- What it does: Drives the item spawn interface and item creation behavior.
- Systems it affects: Inventory, target player attributes, item definitions, economy.
- Safe use cases: Trace item spawn behavior and target-player handling.
- Dangerous use cases: NEVER LIVE ECONOMY for manual item creation.
- Example test procedure: Trigger src/io/xeros/content/commands/admin/SpawnMenu.java locally, use one known item ID, and inspect inventory state.

### NPC spawn
- Main file: src/io/xeros/content/commands/owner/Npc.java
- Command name or method name: ::npc npcId
- Required rank/permission: Primary right must be Right.GAME_DEVELOPER.
- What it does: Spawns an NPC at the player's position with respawn enabled.
- Systems it affects: NPCHandler, NPC definitions, combat, drops, world state.
- Safe use cases: Local boss combat and interaction testing.
- Dangerous use cases: Local/staging only: On live it can create unwanted NPCs, drops, or combat state.
- Example test procedure: Teleport to an isolated local area, run ::npc npcId, test combat or dialogue, then restart or remove the spawned NPC.

### Object spawn
- Main file: src/io/xeros/content/commands/owner/Object.java
- Command name or method name: ::object objectId, ::object objectId type face
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Sends a global object at the player's current tile.
- Systems it affects: Object handling, clipping-visible world state, player interactions.
- Safe use cases: Local object-click and map-object rendering tests.
- Dangerous use cases: Local/staging only: It can create confusing or blocking world state for players.
- Example test procedure: Use an isolated local tile, spawn the object, click it, then reload or restart the test world.

## Teleport, Interface, And Visual Debug Tools

### Admin coordinate teleport
- Main file: src/io/xeros/content/commands/admin/Tele.java
- Command name or method name: ::tele x y, ::tele x y height
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Moves the player to specific coordinates.
- Systems it affects: Movement, instance presence, region loading, teleblock-sensitive testing.
- Safe use cases: Local and staging navigation to content areas.
- Dangerous use cases: Live use can bypass intended progression or enter unsafe state if used inside instances.
- Example test procedure: Use ::coords first, teleport to a known test tile, verify region objects/NPCs load, then return home normally.

### Moderator relative movement
- Main file: src/io/xeros/content/commands/moderator/Move.java
- Command name or method name: ::move up amount, ::move down amount, ::move north amount, ::move east amount, ::move south amount, ::move west amount
- Required rank/permission: Right.MODERATOR.
- What it does: Moves the player by a relative amount and blocks use in clan wars.
- Systems it affects: Movement and position.
- Safe use cases: Staging navigation and position correction.
- Dangerous use cases: Live use can bypass boundaries or instance flow.
- Example test procedure: In a local empty area, run ::move north 1 and verify position changed without entering restricted content.

### Teleport to player
- Main file: src/io/xeros/content/commands/moderator/Xteleto.java
- Command name or method name: ::xteleto player_name
- Required rank/permission: Right.HELPER.
- What it does: Teleports staff to another online player with restrictions for some instance areas when the executor is not admin.
- Systems it affects: Staff movement, instances, player position.
- Safe use cases: Staff support and staging multi-account tests.
- Dangerous use cases: Live use can disrupt instance boundaries or staff moderation if misused.
- Example test procedure: Use two staging accounts, place the target in a normal area, run ::xteleto target_name, and verify staff arrives at the target.

### Teleport player to staff
- Main file: src/io/xeros/content/commands/moderator/Teletome.java
- Command name or method name: ::teletome player_name
- Required rank/permission: Right.MODERATOR.
- What it does: Teleports an online player to the staff member.
- Systems it affects: Player movement and moderation support.
- Safe use cases: Staging support flow tests.
- Dangerous use cases: Live use can remove players from combat, instances, or progression areas.
- Example test procedure: Use only a consenting local or staging target, verify target movement, then return both accounts.

### Position display
- Main file: src/io/xeros/content/commands/owner/Pos.java
- Command name or method name: ::pos
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Prints current coordinates to chat and console.
- Systems it affects: Read-only position debugging.
- Safe use cases: Recording coordinates for object, NPC, or teleport tests.
- Dangerous use cases: Read-only.
- Example test procedure: Stand on the target tile, run ::pos, and copy the coordinate values into the planned content note.

### Coordinate display
- Main file: src/io/xeros/content/commands/admin/Coords.java
- Command name or method name: ::coords
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Prints current coordinates.
- Systems it affects: Read-only position debugging.
- Safe use cases: Confirming location before tests.
- Dangerous use cases: Read-only.
- Example test procedure: Run ::coords before and after a teleport to confirm the player arrived at the expected tile.

### Interface open test
- Main file: src/io/xeros/content/commands/owner/Interface.java
- Command name or method name: ::interface interfaceId
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Opens the specified interface ID.
- Systems it affects: Client interfaces and player UI state.
- Safe use cases: Local UI smoke testing.
- Dangerous use cases: Local/staging only: Opening arbitrary interfaces can confuse live players or break their current UI flow.
- Example test procedure: Run ::interface 43214 locally to inspect the item spawn interface shell, then close it normally.

### Item-on-interface test
- Main file: src/io/xeros/content/commands/owner/Itemoninterface.java
- Command name or method name: ::itemoninterface itemId
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Displays an item on a fixed interface area.
- Systems it affects: Client interface item rendering.
- Safe use cases: Local interface item sprite validation.
- Dangerous use cases: Local/staging only: It changes current interface state.
- Example test procedure: Use a known item ID, run ::itemoninterface itemId, and verify the item sprite renders.

### Varbit/config test
- Main file: src/io/xeros/content/commands/owner/Varbit.java
- Command name or method name: ::varbit-id-state
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Sends a client config or varbit state to the player.
- Systems it affects: Client config display and interface state.
- Safe use cases: Local interface state experiments.
- Dangerous use cases: Local/staging only: Unknown client configs can leave the client in confusing states.
- Example test procedure: Test one known config value at a time, then relog to clear client state if needed.

### Graphics test
- Main file: src/io/xeros/content/commands/owner/Gfx.java
- Command name or method name: ::gfx gfxId
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Plays a graphics effect on the player.
- Systems it affects: Visual effects.
- Safe use cases: Local special attack and spell effect verification.
- Dangerous use cases: Live use can spam visuals.
- Example test procedure: Stand in an isolated local area, run ::gfx gfxId, and confirm the effect is correct.

### Graphics height test
- Main file: src/io/xeros/content/commands/owner/GfxTest.java
- Command name or method name: ::gfxtest gfxId height
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Tests graphics rendering with a height parameter.
- Systems it affects: Visual effects.
- Safe use cases: Local boss attack visual alignment.
- Dangerous use cases: Live use can spam visuals.
- Example test procedure: Use a known boss attack graphic locally and compare height variants.

### Animation test
- Main file: src/io/xeros/content/commands/owner/Anim.java
- Command name or method name: ::anim animationId
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Plays an animation on the player.
- Systems it affects: Player animation state.
- Safe use cases: Local emote, boss, or dialogue animation verification.
- Dangerous use cases: Live use can interrupt normal player animation.
- Example test procedure: Run ::anim animationId locally and verify the animation matches the intended action.

### Sound test
- Main file: src/io/xeros/content/commands/owner/soundtest.java
- Command name or method name: ::soundtest soundId
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Plays a sound effect for the player.
- Systems it affects: Client audio.
- Safe use cases: Local audio verification.
- Dangerous use cases: Live use can annoy players if repeated.
- Example test procedure: Run ::soundtest soundId locally with client audio enabled.

### Camera test
- Main file: src/io/xeros/content/commands/owner/Camera.java
- Command name or method name: ::camera
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Exercises camera behavior for the player.
- Systems it affects: Client camera state.
- Safe use cases: Local cutscene or special encounter camera experiments.
- Dangerous use cases: Live use can disrupt player view.
- Example test procedure: Run locally, verify camera behavior, then relog if the camera remains altered.

### Equipment/stat interface debug
- Main file: src/io/xeros/content/commands/owner/equip.java
- Command name or method name: ::equip
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Opens gear/stat interface and calculates values such as max hit or drop rate.
- Systems it affects: Equipment stats, combat calculations display, interface state.
- Safe use cases: Local gear and stat display checks.
- Dangerous use cases: Read/display oriented, but avoid on live player accounts during active combat.
- Example test procedure: Equip test gear locally, run ::equip, and compare displayed values with expected combat stats.

## Drop, Shop, And Economy Test Tools

### Owner drop table test
- Main file: src/io/xeros/content/commands/owner/Testdroptable.java
- Command name or method name: ::testdroptable npcId-amount
- Required rank/permission: Primary right must be Right.GAME_DEVELOPER.
- What it does: Clears the current bank tab and calls Server.getDropManager().test(player, npcId, amount).
- Systems it affects: DropManager, player bank, NPC drop tables.
- Safe use cases: Local drop table sampling.
- Dangerous use cases: NEVER LIVE ECONOMY: It clears bank tab contents and creates sampled drops.
- Example test procedure: Use a disposable local account, run a small sample first, inspect the bank tab, then run larger samples only if needed.

### Simulate NPC drops
- Main file: src/io/xeros/content/commands/owner/Simulate.java
- Command name or method name: ::simulate-npcid-amount
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Creates a fake NPC, calls Server.getDropManager().create(player, npc, null) repeatedly, adds kill tracker entries, and unregisters the fake NPC.
- Systems it affects: DropManager, ground or inventory drops, kill tracker, collection log hooks that respond to drops.
- Safe use cases: Local reward hook testing.
- Dangerous use cases: NEVER LIVE ECONOMY: It creates real drop rewards and kill tracker state.
- Example test procedure: Run with amount 1 first on local, inspect reward output and logs, then increase sample amount only on disposable state.

### Shop open test
- Main file: src/io/xeros/content/commands/owner/Shop.java
- Command name or method name: ::shop shopId
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Opens a shop by ID.
- Systems it affects: ShopAssistant, ShopHandler, ShopDef, player currency spend paths.
- Safe use cases: Staging shop interface inspection and price verification without buying.
- Dangerous use cases: NEVER LIVE ECONOMY if used to buy items from unfinished, hidden, or test shops.
- Example test procedure: Open the target shop locally, inspect stock and price labels, buy one low-value item only on a disposable account.

### Item ID lookup
- Main file: src/io/xeros/content/commands/owner/Getid.java
- Command name or method name: ::getid search_text
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Searches loaded item definitions for matching names.
- Systems it affects: Read-only item definition lookup.
- Safe use cases: Finding real item IDs before writing docs or content.
- Dangerous use cases: Read-only.
- Example test procedure: Run ::getid wraith locally and use the exact loaded ID in the planned test note.

### NPC ID lookup
- Main file: src/io/xeros/content/commands/owner/Getnpcid.java
- Command name or method name: ::getnpcid search_text
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Searches loaded NPC definitions for matching names.
- Systems it affects: Read-only NPC definition lookup.
- Safe use cases: Finding real NPC IDs before testing spawns or drops.
- Dangerous use cases: Read-only.
- Example test procedure: Run ::getnpcid demon locally, record candidates, then verify the exact NPC in definitions or combat files before spawning.

### Admin item lookup
- Main file: src/io/xeros/content/commands/admin/FindItem.java
- Command name or method name: ::finditem search_text
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Searches item definitions for matching names.
- Systems it affects: Read-only item definition lookup.
- Safe use cases: Admin-side ID lookup on staging.
- Dangerous use cases: Read-only.
- Example test procedure: Run ::finditem key on staging, confirm the item ID, then test the relevant reward flow locally.

### Cox reward roll test
- Main file: src/io/xeros/content/commands/owner/CoxReward.java
- Command name or method name: ::coxreward
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Rolls Chambers of Xeric-style rewards, adds items, opens reward interface, and can consume an item used by the reward flow.
- Systems it affects: Raid rewards, inventory, reward interface, economy.
- Safe use cases: Local raid reward UI checks.
- Dangerous use cases: NEVER LIVE ECONOMY: It awards raid rewards.
- Example test procedure: Use a disposable local account, ensure inventory state is understood, run ::coxreward once, and verify interface/reward handling.

### Theatre chest roll test
- Main file: src/io/xeros/content/commands/owner/Tobchest.java
- Command name or method name: ::tobchest
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Calls TheatreOfBloodChest.getRandomItems(player, 5).
- Systems it affects: Theatre of Blood chest reward logic.
- Safe use cases: Local reward roll inspection.
- Dangerous use cases: Local/staging only: Treat as reward testing and avoid live economy.
- Example test procedure: Run locally and inspect console/chat output or reward behavior from the chest code.

### Raid test placeholders
- Main file: src/io/xeros/content/commands/owner/Raidtest.java
- Command name or method name: ::raidtest
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: The execute method is empty in the current implementation.
- Systems it affects: No active behavior found.
- Safe use cases: Not useful as a current test tool.
- Dangerous use cases: Do not assume it validates raids.
- Example test procedure: Not found in active behavior. Searched terms: raidtest, Raidtest, execute(Player).

### Raid kill placeholder
- Main file: src/io/xeros/content/commands/owner/Raidkill.java
- Command name or method name: ::raidkill
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: The execute method is empty in the current implementation.
- Systems it affects: No active behavior found.
- Safe use cases: Not useful as a current test tool.
- Dangerous use cases: Do not assume it completes raids.
- Example test procedure: Not found in active behavior. Searched terms: raidkill, Raidkill, execute(Player).

### Boss point direct test tool
- Main file: Not found in repo.
- Command name or method name: Not found in repo.
- Required rank/permission: Not found in repo.
- What it does: No dedicated boss-point-only test command was found.
- Systems it affects: Boss points are indirectly changed by src/io/xeros/content/commands/test/Points.java, src/io/xeros/content/commands/owner/addvp.java, and src/io/xeros/content/commands/owner/Set.java.
- Safe use cases: Use local disposable accounts if using the indirect point tools.
- Dangerous use cases: NEVER LIVE ECONOMY: Indirect point tools mutate many currencies.
- Example test procedure: Searched terms: bossPoints, BossPoints, boss points, set boss points.

## Reload Commands And Runtime Validation

### Owner reload router
- Main file: src/io/xeros/content/commands/owner/Reload.java
- Command name or method name: ::reload reload_type
- Required rank/permission: Primary right must be Right.GAME_DEVELOPER.
- What it does: Routes many reload cases including scan, coinflip, dailyrewards, referralcodes, store, doors, drops, items, wogw, objects, shops, npcs, votes, punishments, and looting.
- Systems it affects: Depends on reload type; can affect NPCs, drops, shops, items, objects, votes, punishments, and configuration flags.
- Safe use cases: Local/staging reload after data-only edits.
- Dangerous use cases: Owner-review reload: Avoid live reload of drops, items, shops, npcs, objects, and scan without owner approval.
- Example test procedure: Edit only a local data file, run the matching ::reload case, verify console errors and in-game behavior, then restart locally to ensure startup still loads cleanly.

### Drop table reload
- Main file: src/io/xeros/content/commands/owner/Reload.java
- Command name or method name: ::reload drops
- Required rank/permission: Primary right must be Right.GAME_DEVELOPER.
- What it does: Calls Server.getDropManager().read().
- Systems it affects: DropManager and loaded NPC drop tables.
- Safe use cases: Local/staging validation after drop YAML edits.
- Dangerous use cases: Owner-review reload on live because drop table changes affect all subsequent NPC rewards.
- Example test procedure: Run ::reload drops locally, confirm no YAML errors, then sample with ::testdroptable on a disposable player.

### Shop reload
- Main file: src/io/xeros/content/commands/owner/Reload.java
- Command name or method name: ::reload shops
- Required rank/permission: Primary right must be Right.GAME_DEVELOPER.
- What it does: Rebuilds Fire of Exchange burn price shop, creates a new ShopHandler, loads ShopDef, and loads shops.
- Systems it affects: ShopHandler, ShopDef, FireOfExchangeBurnPrice, shop economy.
- Safe use cases: Local/staging shop stock and price validation.
- Dangerous use cases: Owner-review reload: Live shop mistakes can create economy exploits.
- Example test procedure: Run ::reload shops locally, open the affected shop with ::shop shopId, inspect prices, and test purchase on a disposable account.

### Item definition reload
- Main file: src/io/xeros/content/commands/owner/Reload.java
- Command name or method name: ::reload items
- Required rank/permission: Primary right must be Right.GAME_DEVELOPER.
- What it does: Reloads ItemDef, ItemStats, custom item handling, donor vault statics, and coin-flip item actions.
- Systems it affects: Item definitions, item stats, donor vault, coin flip item behavior.
- Safe use cases: Local validation after item definition or stat changes.
- Dangerous use cases: Owner-review reload: Item stat mistakes can affect combat, economy, and upgrades.
- Example test procedure: Run ::reload items locally, search the item with ::getid, spawn one test copy locally, and inspect stats or use behavior.

### NPC definition reload
- Main file: src/io/xeros/content/commands/owner/Reload.java
- Command name or method name: ::reload npcs
- Required rank/permission: Primary right must be Right.GAME_DEVELOPER.
- What it does: Replaces Server.npcHandler with a new NPCHandler.
- Systems it affects: NPCHandler, NPC spawns, combat state.
- Safe use cases: Local NPC spawn/combat validation.
- Dangerous use cases: Owner-review reload: Replacing NPCHandler on live can disrupt active NPCs and players in combat.
- Example test procedure: Use local only, run ::reload npcs, spawn a test NPC with ::npc npcId, and verify combat/interactions.

### Vote panel reload
- Main file: src/io/xeros/content/commands/owner/Reload.java
- Command name or method name: ::reload votes
- Required rank/permission: Primary right must be Right.GAME_DEVELOPER.
- What it does: Calls VotePanelManager.init().
- Systems it affects: Vote panel config and vote panel point state.
- Safe use cases: Staging vote panel display validation.
- Dangerous use cases: Owner-review reload if vote panel data is active for live players.
- Example test procedure: Run ::reload votes locally, open ::vpanel, and verify displayed rewards and point values.

### Daily rewards reload
- Main file: src/io/xeros/content/commands/owner/Reload.java
- Command name or method name: ::reload dailyrewards
- Required rank/permission: Primary right must be Right.GAME_DEVELOPER.
- What it does: Calls DailyRewardContainer.load().
- Systems it affects: Daily reward configuration and daily claim interface.
- Safe use cases: Local/staging validation after daily reward YAML edits.
- Dangerous use cases: Owner-review reload if reward values affect live economy.
- Example test procedure: Run ::reload dailyrewards locally, open ::dailyreward at Edgeville, and verify the reward interface loads.

### AOE tier reward reload
- Main file: src/io/xeros/content/instances/aoe/AoeTierDebug.java
- Command name or method name: ::aoetierdebug rewards reload
- Required rank/permission: Administrator check inside command for mutation subcommands.
- What it does: Calls AoeTierRewardsLoader.reload().
- Systems it affects: data/aoe/aoe_tier_rewards.json, AOE reward definitions.
- Safe use cases: Local/staging JSON-only AOE reward validation.
- Dangerous use cases: Owner-review reload on live if reward values affect economy or progression.
- Example test procedure: Edit data/aoe/aoe_tier_rewards.json locally, run ::aoetierdebug rewards reload, then use ::aoetierdebug rewards show tierId.

### AOE boss tier reload
- Main file: src/io/xeros/content/instances/aoe/AoeTierDebug.java
- Command name or method name: ::aoetierdebug tier reload
- Required rank/permission: Administrator check inside command for mutation subcommands.
- What it does: Ends active AOE instances and calls AoeBossTierLoader.loadAllOrWarn("reload").
- Systems it affects: data/aoe/aoe_boss_tiers.json, active AOE instances, AOE tier definitions.
- Safe use cases: Local/staging reload after AOE tier JSON edits.
- Dangerous use cases: Owner-review reload: It ends active AOE instances.
- Example test procedure: Run locally with no active players, then start tier 1 with ::testaoe 1 and verify NPC roster and kill target behavior.

### Public AOE command reload placeholder
- Main file: src/io/xeros/content/commands/all/Aoe.java
- Command name or method name: ::aoe reload
- Required rank/permission: Administrator check for reload subcommand.
- What it does: The AoeBossTierLoader.reload() call is commented out in this command, so it only sends a message in the current implementation.
- Systems it affects: No active reload behavior found in this command.
- Safe use cases: Do not rely on this for actual reloads.
- Dangerous use cases: Assuming it reloaded JSON when it did not.
- Example test procedure: Use src/io/xeros/content/instances/aoe/AoeTierDebug.java for actual AOE reload testing instead.

### Hazard config reload
- Main file: src/io/xeros/content/commands/admin/Reloadhazards.java
- Command name or method name: ::reloadhazards
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Calls WeeklyHazardManager.reload().
- Systems it affects: Weekly hazard configuration and active hazard behavior.
- Safe use cases: Local/staging hazard config validation.
- Dangerous use cases: Owner-review reload on live if active instances depend on current hazards.
- Example test procedure: Run ::reloadhazards locally, enter an AOE or boss instance that uses hazards, and inspect hazard status with ::debughazards zoneId.

### Demon Hunter XP reload
- Main file: src/io/xeros/content/commands/admin/Reloaddhxps.java
- Command name or method name: ::reloaddhxps
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Calls DemonHunterXPTable.reload().
- Systems it affects: Demon Hunter XP scaling.
- Safe use cases: Local/staging validation after Demon Hunter XP table edits.
- Dangerous use cases: Owner-review reload if active player progression depends on the XP table.
- Example test procedure: Run ::reloaddhxps locally, assign a Demon Hunter task, complete one test kill, and verify XP progression.

### Battlepass reload tool
- Main file: Not found in repo.
- Command name or method name: Not found in repo.
- Required rank/permission: Not found in repo.
- What it does: No dedicated Battlepass reload command was found.
- Systems it affects: Battlepass can be opened and modified through other tools, but no reload command was found.
- Safe use cases: Use server restart or existing Pass and Rewards initialization paths for validation.
- Dangerous use cases: Do not assume ::bp reload or similar exists.
- Example test procedure: Searched terms: battlepass, Pass.addExperience, Rewards.init, addbpxp, endBp.

### Collection log reload tool
- Main file: Not found in repo.
- Command name or method name: Not found in repo.
- Required rank/permission: Not found in repo.
- What it does: No dedicated collection log reload command was found.
- Systems it affects: CollectionLog loads at startup through CollectionLog.init().
- Safe use cases: Validate collection log data with server startup or local restart.
- Dangerous use cases: Do not assume collection log config can be safely hot-reloaded.
- Example test procedure: Searched terms: collection_npcs, CollectionLog.init, reload collection, givecollection.

## AOE And Instance Debug Tools

### AOE start test
- Main file: src/io/xeros/content/commands/admin/Testaoe.java
- Command name or method name: ::testaoe tier
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Calls AoeTierController.startTier(player, tier).
- Systems it affects: AOE instance creation, AOE tier definitions, player instance state.
- Safe use cases: Local/staging AOE tier entry testing.
- Dangerous use cases: Local/staging only: Can place players into unfinished or high-tier instances.
- Example test procedure: Run ::testaoe 1 locally, verify instance entry, kill counter, rewards, and exit behavior with ::leaveaoe.

### Public AOE command
- Main file: src/io/xeros/content/commands/all/Aoe.java
- Command name or method name: ::aoe tier status, ::aoe tier start, ::aoe tier set, ::aoe tier simulate, ::aoe reload
- Required rank/permission: Public for status/start; Administrator checks for set, simulate, and reload subcommands.
- What it does: Shows AOE tier status, starts tiers, mutates tier progress for administrators, simulates progression for administrators, and contains a no-op reload message.
- Systems it affects: AOE tier progress, AoeTierController, player save data, AOE rewards depending on subcommand.
- Safe use cases: Public status checks and local tier entry smoke tests.
- Dangerous use cases: NEVER LIVE ECONOMY for simulate/set if rewards or progression are affected.
- Example test procedure: Run ::aoe tier status, then locally run ::aoe tier start 1, complete or exit the instance, and recheck status.

### Leave AOE instance
- Main file: src/io/xeros/content/commands/all/Leaveaoe.java
- Command name or method name: ::leaveaoe
- Required rank/permission: Public.
- What it does: Exits the active AOE instance and teleports the player home.
- Systems it affects: AOE instance cleanup and player position.
- Safe use cases: Player-safe escape and local cleanup after AOE tests.
- Dangerous use cases: Low risk, but should not be used to bypass intended combat outcomes during official testing.
- Example test procedure: Enter with ::testaoe 1, run ::leaveaoe, and verify the player returns home and is no longer in the instance.

### AOE aggro toggle
- Main file: src/io/xeros/content/commands/admin/Aoeaggro.java
- Command name or method name: ::aoeaggro
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Inside an AOE instance, toggles forced aggro through AoeNpcSpawner.toggleForceAggro.
- Systems it affects: AOE NPC targeting behavior.
- Safe use cases: Local/staging spawn aggression tests.
- Dangerous use cases: Live use can change active instance difficulty.
- Example test procedure: Enter an AOE instance locally, run ::aoeaggro, observe NPC targeting, then toggle it back.

### AOE spawn count debug
- Main file: src/io/xeros/content/commands/admin/Aoespawns.java
- Command name or method name: ::aoespawns
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Prints AoeNpcSpawner.debugCounts for the active AOE instance.
- Systems it affects: Read-only AOE NPC spawn diagnostics.
- Safe use cases: Verify expected spawn counts after tier JSON edits.
- Dangerous use cases: Read-only.
- Example test procedure: Start tier 1 locally, run ::aoespawns before and after kills, and confirm counts match expected tier behavior.

### Kill all AOE NPCs
- Main file: src/io/xeros/content/commands/admin/Killallaoe.java
- Command name or method name: ::killallaoe
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Calls AoeNpcSpawner.killAll for the active AOE instance.
- Systems it affects: AOE NPC deaths, kill progression, rewards depending on death flow.
- Safe use cases: Local/staging instance cleanup and wave-completion testing.
- Dangerous use cases: NEVER LIVE ECONOMY if it triggers rewards or progression.
- Example test procedure: Start a local AOE tier, run ::killallaoe, confirm kill counters/reward behavior, and discard test progression if rewards were granted.

### Reload active AOE zone
- Main file: src/io/xeros/content/commands/admin/Reloadaoezone.java
- Command name or method name: ::reloadaoezone zoneId
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Finds an active BossInstanceArea by boss tier, reloads it through BossInstanceManager.reloadArea(area), and rerolls weekly mutators.
- Systems it affects: Active boss instance area, AOE zone state, instance mutators.
- Safe use cases: Local/staging active-area reload validation.
- Dangerous use cases: Owner-review reload: It mutates active instance state and mutators.
- Example test procedure: Start the target zone locally, run ::reloadaoezone zoneId, verify mutators and spawn state, then exit the instance.

### AOE tier debug command
- Main file: src/io/xeros/content/instances/aoe/AoeTierDebug.java
- Command name or method name: ::aoetierdebug tier open/status/start/set/simulate/reload, ::aoetierdebug rewards show/clear/bank/reload/simulate
- Required rank/permission: Public command class with administrator checks for mutating subcommands.
- What it does: Opens/statuses/starts/mutates/simulates AOE tier progress, reloads tier config, shows or clears AOE rewards, banks test rewards, reloads rewards, and simulates reward awarding.
- Systems it affects: AOE tier progress, AOE rewards, AOE instances, player save data, inventory/bank depending on subcommand.
- Safe use cases: Local/staging AOE reward and progression validation.
- Dangerous use cases: NEVER LIVE ECONOMY for set, simulate, rewards clear, rewards bank, and rewards simulate.
- Example test procedure: Run ::aoetierdebug rewards reload, ::aoetierdebug rewards show 1, ::aoetierdebug tier start 1, then test one completion path on a disposable account.

## Hazard, Mutator, And Instance Debug Tools

### Hazard status debug
- Main file: src/io/xeros/content/commands/admin/Debughazards.java
- Command name or method name: ::debughazards zoneId
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Finds an active boss instance area and prints hazard scheduler debug state.
- Systems it affects: Read-only hazard diagnostics.
- Safe use cases: Local/staging hazard troubleshooting.
- Dangerous use cases: Read-only.
- Example test procedure: Start the matching instance, run ::debughazards zoneId, and compare scheduler state with expected hazard config.

### Hazard status alias
- Main file: src/io/xeros/content/commands/admin/Hazardstatus.java
- Command name or method name: ::hazardstatus zoneId
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Prints active hazard state for a zone.
- Systems it affects: Read-only hazard diagnostics.
- Safe use cases: Local/staging status checks.
- Dangerous use cases: Read-only.
- Example test procedure: Run after spawning or reloading hazards and confirm state changes are visible.

### Hazard audit log
- Main file: src/io/xeros/content/commands/admin/Hazardaudit.java
- Command name or method name: ::hazardaudit zoneId
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Prints HazardDebugLogger audit logs for an active area.
- Systems it affects: Read-only hazard debug logs.
- Safe use cases: Investigating hazard scheduling decisions.
- Dangerous use cases: Read-only.
- Example test procedure: Trigger one hazard locally, run ::hazardaudit zoneId, and inspect the latest audit entries.

### Hazard log dump
- Main file: src/io/xeros/content/commands/admin/Hazardlogdump.java
- Command name or method name: ::hazardlogdump zoneId
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Dumps hazard logs for a zone into a runtime-generated text file.
- Systems it affects: Hazard debug logging and local filesystem output.
- Safe use cases: Local/staging capture of long hazard logs.
- Dangerous use cases: Avoid on live unless owner asks for diagnostic output.
- Example test procedure: Reproduce a local hazard issue, run ::hazardlogdump zoneId, and inspect the generated hazard log file name printed by the server.

### Hazard vision toggle
- Main file: src/io/xeros/content/commands/admin/Hazardvision.java
- Command name or method name: ::hazardvision
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Toggles the hazardvision player attribute.
- Systems it affects: Player attributes and hazard debug display.
- Safe use cases: Local/staging visual hazard debugging.
- Dangerous use cases: Live use may reveal internal debug state.
- Example test procedure: Toggle before entering a hazard instance, observe debug display, then toggle off.

### Replay hazard
- Main file: src/io/xeros/content/commands/admin/Replayhazard.java
- Command name or method name: ::replayhazard zoneId hazardType
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Replays a hazard type for the active zone scheduler.
- Systems it affects: Active hazards, instance difficulty, player safety.
- Safe use cases: Local/staging hazard pattern validation.
- Dangerous use cases: Live use can damage or disrupt players.
- Example test procedure: Start an empty local instance, run one known hazard type, and verify telegraph, damage, and cleanup.

### Spawn hazard
- Main file: src/io/xeros/content/commands/admin/Spawnhazard.java
- Command name or method name: ::spawnhazard zoneId hazardType tier
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Spawns a hazard pattern in the active zone.
- Systems it affects: Active hazards and instance state.
- Safe use cases: Local hazard geometry and scaling tests.
- Dangerous use cases: Live use can damage or disrupt players.
- Example test procedure: Use an empty local instance, spawn one hazard, verify boundaries and cleanup.

### Mutator debug
- Main file: src/io/xeros/content/commands/admin/Debugmutators.java
- Command name or method name: ::debugmutators
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Lists active instance mutators and synergies.
- Systems it affects: Read-only mutator diagnostics.
- Safe use cases: Local/staging instance mutator verification.
- Dangerous use cases: Read-only.
- Example test procedure: Start an instance, run ::debugmutators, and compare output with expected weekly mutator state.

### Mutator vision toggle
- Main file: src/io/xeros/content/commands/admin/Mutatorvision.java
- Command name or method name: ::mutatorvision
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Toggles the mutatorvision player attribute.
- Systems it affects: Player attributes and mutator debug display.
- Safe use cases: Local/staging mutator visual debugging.
- Dangerous use cases: Live use may expose internal debug state.
- Example test procedure: Toggle in a local instance, inspect mutator visuals, then toggle off.

### NPC reaction debug
- Main file: src/io/xeros/content/commands/admin/Debugnpcreactions.java
- Command name or method name: ::debugnpcreactions zoneId
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Prints HazardDebugLogger NPC reaction logs for an active area.
- Systems it affects: Read-only NPC hazard reaction diagnostics.
- Safe use cases: Local troubleshooting for NPC behavior under hazards.
- Dangerous use cases: Read-only.
- Example test procedure: Spawn an AOE area, trigger a hazard, then run ::debugnpcreactions zoneId.

### Adaptive NPC debug
- Main file: src/io/xeros/content/commands/admin/Debugadaptive.java
- Command name or method name: ::debugadaptive
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Requires a targeted NPC and prints adaptive phase, traits, enrage, and next trigger data.
- Systems it affects: Read-only adaptive NPC diagnostics.
- Safe use cases: Local boss phase and adaptive trait testing.
- Dangerous use cases: Read-only, but requires target selection in active combat context.
- Example test procedure: Spawn the adaptive boss locally, target it, run ::debugadaptive, then verify phase transitions during combat.

### Tinker debug
- Main file: src/io/xeros/content/commands/admin/Debugtinker.java
- Command name or method name: ::debugtinker
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Prints recent player tinker logs.
- Systems it affects: Read-only tinker/upgrade-style diagnostics.
- Safe use cases: Local troubleshooting after tinker or upgrade attempts.
- Dangerous use cases: Read-only.
- Example test procedure: Perform the target tinker action locally, run ::debugtinker, and inspect recent entries.

### Safe mode toggle
- Main file: src/io/xeros/content/commands/admin/Safemode.java
- Command name or method name: ::safemode
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Toggles item loss on death for the player.
- Systems it affects: Death item loss behavior.
- Safe use cases: Local/staging combat and boss testing where item loss would interfere.
- Dangerous use cases: NEVER LIVE ECONOMY: It changes death-risk behavior.
- Example test procedure: Toggle on for a local boss death test, verify the death flow, then toggle off.

## Wraith, Upgrade, Fusion, And Fortune Tools

### Wraith charge command
- Main file: src/io/xeros/content/commands/all/Wraith.java
- Command name or method name: ::wraith charge amount, ::wraith charges
- Required rank/permission: Public.
- What it does: Charges a Wraith weapon by consuming Wraith Essence, or displays Wraith charge state.
- Systems it affects: WraithCharges, Wraith Essence, inventory, weapon charge data.
- Safe use cases: Normal player-facing Wraith charge testing with real items on local/staging.
- Dangerous use cases: Live economy caution: Do not pair with spawned Wraith Essence unless on local.
- Example test procedure: Spawn a Wraith weapon and essence locally, run ::wraith charge 10, then verify charges with ::wraith charges.

### Wraith charge display
- Main file: src/io/xeros/content/commands/all/Wraithcharges.java
- Command name or method name: ::wraithcharges
- Required rank/permission: Public.
- What it does: Displays Wraith weapon charges.
- Systems it affects: Read-only Wraith charge display.
- Safe use cases: Verify charge persistence after relog or combat.
- Dangerous use cases: Read-only.
- Example test procedure: Charge a Wraith weapon locally, run ::wraithcharges, relog, and run it again.

### Wraith charge backend
- Main file: src/io/xeros/content/wraith/WraithCharges.java
- Command name or method name: addChargesFromEssence(Player, int), addCharge(Player, int), consumeCharge(Player)
- Required rank/permission: Called by item/command/combat flows.
- What it does: Adds, displays, and consumes Wraith charges, with Wraith debug logging prefixes in code.
- Systems it affects: Wraith weapons, essence sink, combat charge consumption, player save data.
- Safe use cases: Unit and local integration tests for charge caps and consumption.
- Dangerous use cases: Changing charge economics without checking essence acquisition and item power.
- Example test procedure: Run src/test/java/io/xeros/content/wraith/WraithChargesTest.java through Gradle, then do one local manual charge/consume cycle.

### Upgrade interface test
- Main file: src/io/xeros/content/commands/owner/Upgrade.java
- Command name or method name: ::upgrade
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Opens the upgrade interface for UpgradeMaterials.UpgradeType.WEAPON and sends interface 35000.
- Systems it affects: UpgradeMaterials, upgrade interface, item sinks, foundry/upgrade points depending on recipes.
- Safe use cases: Local upgrade UI and recipe display testing.
- Dangerous use cases: NEVER LIVE ECONOMY if paired with spawned materials or real upgrade attempts on production accounts.
- Example test procedure: Spawn required local test items, run ::upgrade, inspect recipe materials and chance, then attempt only on a disposable account.

### Fusion interface test
- Main file: src/io/xeros/content/commands/owner/Fuse.java
- Command name or method name: ::fuse
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Opens the fusion interface for FusionTypes.WEAPON.
- Systems it affects: Fusion recipes, fusion interface, item sinks.
- Safe use cases: Local fusion UI and recipe testing.
- Dangerous use cases: NEVER LIVE ECONOMY if paired with spawned materials or production items.
- Example test procedure: Spawn test materials locally, run ::fuse, verify recipe display, and attempt fusion only on disposable state.

### Wheel of Fortune interface test
- Main file: src/io/xeros/content/commands/owner/WheelTest.java
- Command name or method name: ::wheeltest
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Opens the Wheel of Fortune interface.
- Systems it affects: Fortune interface and reward presentation.
- Safe use cases: Local interface display and spin flow validation.
- Dangerous use cases: NEVER LIVE ECONOMY if it can grant rewards.
- Example test procedure: Run locally with a disposable account, inspect the interface, and verify no unintended reward is granted before real spin testing.

### Upgrade success simulator
- Main file: Not found in repo.
- Command name or method name: Not found in repo.
- Required rank/permission: Not found in repo.
- What it does: No dedicated command for forcing upgrade or fusion success was found.
- Systems it affects: Use src/io/xeros/content/commands/owner/Upgrade.java and src/io/xeros/content/commands/owner/Fuse.java to open real interfaces instead.
- Safe use cases: Local real-flow testing with disposable items.
- Dangerous use cases: Do not invent forced success paths for testing.
- Example test procedure: Searched terms: Upgrade, Fusion, force upgrade, upgrade test, fuse.

## Vote, Daily, Battlepass, And Donator Tools

### Vote panel open
- Main file: src/io/xeros/content/commands/all/Vpanel.java
- Command name or method name: ::vpanel
- Required rank/permission: Public.
- What it does: Opens the vote panel.
- Systems it affects: VotePanelManager display and vote point spending.
- Safe use cases: UI validation and reward display checks.
- Dangerous use cases: Buying rewards affects economy if used with real vote points.
- Example test procedure: On local, use point tools only on a disposable account, open ::vpanel, and verify display and spend behavior.

### Vote claim
- Main file: src/io/xeros/content/commands/all/Voted.java
- Command name or method name: ::voted
- Required rank/permission: Public.
- What it does: Claims external vote rewards, grants vote keys/lamp/items/points, updates achievements, vote panel state, bonuses, GP, and global boss activity.
- Systems it affects: Vote rewards, achievements, inventory, vote panel, bonus damage, global boss activity, player save data.
- Safe use cases: Staging API verification with owner-approved test accounts.
- Dangerous use cases: NEVER fake on live economy; it grants real rewards and progression.
- Example test procedure: Use a staging account and owner-approved vote test flow, claim once, then verify inventory, vote panel points, achievements, and activity boss contribution.

### Vote page command
- Main file: src/io/xeros/content/commands/all/Vote.java
- Command name or method name: ::vote
- Required rank/permission: Public.
- What it does: Opens the vote panel and sends the configured external vote page.
- Systems it affects: UI and external voting flow.
- Safe use cases: Confirm the command opens the vote panel and sends the page action.
- Dangerous use cases: Low risk, but avoid unnecessary live spam.
- Example test procedure: Run on staging, verify the panel opens, and do not claim rewards unless testing ::voted.

### Set vote streak
- Main file: src/io/xeros/content/commands/owner/Setvotestreak.java
- Command name or method name: ::setvotestreak-player name-4
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Sets VoteUser.dayStreak for an online player.
- Systems it affects: Vote streak progression and player save data.
- Safe use cases: Local/staging vote streak UI and reward testing.
- Dangerous use cases: NEVER LIVE ECONOMY unless correcting an owner-approved account issue.
- Example test procedure: Use a local target account, set a small streak, open vote UI, and verify streak display/reward behavior.

### Bulk vote and point grant
- Main file: src/io/xeros/content/commands/owner/addvp.java
- Command name or method name: ::addvp
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Sets many currencies and vote panel values to very large numbers.
- Systems it affects: Boss points, vote panel points, achievement points, Slayer points, donation-related fields, and multiple economy currencies.
- Safe use cases: Local UI stress checks on disposable saves.
- Dangerous use cases: NEVER LIVE ECONOMY.
- Example test procedure: Run only locally, inspect the intended shop/panel, then delete or reset the test save.

### Daily reward interface
- Main file: src/io/xeros/content/commands/all/Dailyreward.java
- Command name or method name: ::dailyreward
- Required rank/permission: Public.
- What it does: Opens the daily reward interface from the allowed area.
- Systems it affects: DailyRewards interface and claim flow.
- Safe use cases: Local/staging display and eligibility testing.
- Dangerous use cases: Claiming rewards affects economy and daily progress.
- Example test procedure: Stand at the required hub area, run ::dailyreward, inspect display, and claim only on a disposable local account.

### Daily reward claim backend
- Main file: src/io/xeros/content/dailyrewards/DailyRewards.java
- Command name or method name: claim()
- Required rank/permission: Normal player flow; debug bypass exists only when Server.isDebug() and player has Right.STAFF_MANAGER.
- What it does: Validates timing, grants daily rewards, writes DailyRewardLog and DailyRewardsCompletedLog, and increases daily achievement progress.
- Systems it affects: Daily rewards, achievements, inventory, player save data, logging.
- Safe use cases: Local validation of claim timing and reward display.
- Dangerous use cases: Live economy caution: daily rewards grant real items or currencies.
- Example test procedure: Use a debug staff-manager local account to test the bypass path, then test a normal account path after restarting or adjusting disposable state.

### Daily force/reset test tool
- Main file: Not found in repo.
- Command name or method name: Not found in repo.
- Required rank/permission: Not found in repo.
- What it does: No dedicated command to force reset or force claim daily rewards was found outside the debug bypass in DailyRewards.claim().
- Systems it affects: Use ::dailyreward and DailyRewards.claim() behavior instead.
- Safe use cases: Local timing tests.
- Dangerous use cases: Do not add manual live reset tools casually.
- Example test procedure: Searched terms: dailyreward, DailyRewards, claim, streak, daily_rewards.

### Battlepass open
- Main file: src/io/xeros/content/commands/CommandManager.java
- Command name or method name: ::bp
- Required rank/permission: Inline command registered in CommandManager; no rank gate observed for the open action.
- What it does: Opens the Battlepass interface through Pass.openInterface(player) and related help-tab action.
- Systems it affects: Battlepass UI and reward claim display.
- Safe use cases: UI validation.
- Dangerous use cases: Claiming rewards affects economy/progression.
- Example test procedure: Run ::bp locally, inspect current season reward display, and do not claim rewards unless on a disposable account.

### Add Battlepass XP
- Main file: src/io/xeros/content/commands/owner/addbpxp.java
- Command name or method name: ::addbpxp playerName-amount
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Adds Battlepass experience to a named player through Pass.addExperience.
- Systems it affects: Battlepass progression, player rewards, persistent player state.
- Safe use cases: Local/staging Battlepass level and reward testing.
- Dangerous use cases: NEVER LIVE ECONOMY unless the owner is issuing manual correction.
- Example test procedure: Add a small XP amount to a local test player, open ::bp, verify level progress, and claim only disposable rewards.

### End Battlepass season
- Main file: src/io/xeros/content/commands/owner/endBp.java
- Command name or method name: ::endbp
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Sets Pass.seasonEnded true and sets daysUntilStart to 1.
- Systems it affects: Battlepass season state and player access to the pass.
- Safe use cases: Local season transition testing.
- Dangerous use cases: NEVER LIVE ECONOMY: It changes season state globally.
- Example test procedure: Run only on local, open ::bp before and after, verify ended-season UI, then reset local state.

### Donation amount test
- Main file: src/io/xeros/content/commands/test/TestDonationRewards.java
- Command name or method name: ::testdonationrewards amount
- Required rank/permission: Right.GAME_DEVELOPER on debug/test command package.
- What it does: Increases donation reward amount for the player.
- Systems it affects: Donator progression and reward thresholds.
- Safe use cases: Local donor progression testing.
- Dangerous use cases: NEVER LIVE ECONOMY.
- Example test procedure: Run on local disposable player, inspect donation reward changes, then discard state.

### Manual donation item grant
- Main file: src/io/xeros/content/commands/admin/Givedonation.java
- Command name or method name: ::givedonation player amount
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Gives donation items through the claim/donation flow.
- Systems it affects: Donation rewards, item grants, player economy.
- Safe use cases: Owner-approved staging tests and official manual compensation only.
- Dangerous use cases: NEVER LIVE ECONOMY unless explicitly authorized by owner.
- Example test procedure: On staging, grant a tiny test amount to a disposable player and verify claim behavior.

## Collection Log, Achievement, Task Master, Slayer, And Demon Hunter Tools

### Give collection log entry
- Main file: src/io/xeros/content/commands/admin/Givecollection.java
- Command name or method name: ::givecollection-name-npcid-itemid-amount
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Calls the target player's CollectionLog.handleDrop for an NPC ID, item ID, and amount.
- Systems it affects: Collection log progress, player save data, reward eligibility.
- Safe use cases: Local/staging collection log claim testing.
- Dangerous use cases: NEVER LIVE ECONOMY if it completes collection rewards or progression.
- Example test procedure: Use a local target account, add one known log item, open the collection log, and verify entry count and reward state.

### Fill collection logs
- Main file: src/io/xeros/content/commands/owner/givelogs.java
- Command name or method name: ::givelogs
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Iterates CollectionRewards and fills many collection log drops.
- Systems it affects: Collection logs, collection reward eligibility, player save data.
- Safe use cases: Local-only full reward interface testing.
- Dangerous use cases: NEVER LIVE ECONOMY: It can complete many collection logs.
- Example test procedure: Run on a disposable local player, open collection reward interfaces, verify claim state, and discard the save.

### Complete achievement
- Main file: src/io/xeros/content/commands/test/Completeachievement.java
- Command name or method name: ::completeachievement all, ::completeachievement achievement_type
- Required rank/permission: Right.GAME_DEVELOPER on debug/test command package.
- What it does: Adds massive progress to achievements through Achievements.increase.
- Systems it affects: Achievements, rewards, achievement points, player save data.
- Safe use cases: Local achievement claim testing.
- Dangerous use cases: NEVER LIVE ECONOMY.
- Example test procedure: Complete one achievement type locally, open the achievement interface, verify claim, and discard state.

### Achievement interface test
- Main file: src/io/xeros/content/commands/owner/achieve.java
- Command name or method name: ::achieve
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Exercises achievement interface behavior.
- Systems it affects: Achievement UI.
- Safe use cases: Local interface smoke testing.
- Dangerous use cases: Low risk if it only opens UI, but inspect file before use.
- Example test procedure: Run locally, verify the achievement UI opens, then compare with normal achievement menu access.

### Achievement reset tool
- Main file: Not found in repo.
- Command name or method name: Not found in repo.
- Required rank/permission: Not found in repo.
- What it does: No dedicated achievement reset command was found.
- Systems it affects: Use disposable local saves for achievement completion tests.
- Safe use cases: Reset by discarding local player save, not by live command.
- Dangerous use cases: Do not add a live reset command without owner review.
- Example test procedure: Searched terms: completeachievement, achievement, setComplete, reset achievements.

### Task Master direct test tool
- Main file: Not found in repo.
- Command name or method name: Not found in repo.
- Required rank/permission: Not found in repo.
- What it does: No dedicated Task Master debug command was found.
- Systems it affects: Task Master should be tested through the real triggering events defined in src/io/xeros/content/taskmaster/.
- Safe use cases: Local event-driven task tests.
- Dangerous use cases: Do not fake Task Master progress in live player state unless an owner-approved correction tool exists.
- Example test procedure: Searched terms: TaskMaster, TaskMasterKills, Tasks, taskmaster command, weekly task.

### Slayer task assignment test
- Main file: src/io/xeros/content/commands/test/Slayertask.java
- Command name or method name: ::slayertask task_name
- Required rank/permission: Right.GAME_DEVELOPER on debug/test command package.
- What it does: Sets a specific Slayer task if a matching SlayerMaster task is found.
- Systems it affects: Slayer task state, player save data, Slayer rewards.
- Safe use cases: Local Slayer task kill hook testing.
- Dangerous use cases: Local/staging only; it mutates progression.
- Example test procedure: Assign a known task locally, kill one matching NPC, and verify task amount and reward hooks.

### Slayer task display
- Main file: src/io/xeros/content/commands/all/Task.java
- Command name or method name: ::task
- Required rank/permission: Public.
- What it does: Displays the current Slayer task.
- Systems it affects: Read-only Slayer task display.
- Safe use cases: Confirm local task assignment and decrement behavior.
- Dangerous use cases: Read-only.
- Example test procedure: Run before and after one Slayer kill to confirm remaining amount changed.

### Slayer teleport
- Main file: src/io/xeros/content/commands/all/Slayer.java
- Command name or method name: ::slayer
- Required rank/permission: Public.
- What it does: Teleports the player to Slayer-related areas with wilderness behavior variations.
- Systems it affects: Teleports and Slayer access.
- Safe use cases: Verify player access to Slayer content.
- Dangerous use cases: Live use follows normal player path, but admin testing should avoid bypassing intended progression.
- Example test procedure: Run locally, verify destination and any mode-specific behavior.

### Reset Slayer task legacy command
- Main file: src/io/xeros/model/entity/player/packets/Commands.java
- Command name or method name: resettask player_name
- Required rank/permission: Management-only legacy branch.
- What it does: Removes the target player's current Slayer task.
- Systems it affects: Slayer task state and player save data.
- Safe use cases: Owner-approved correction on staging or live.
- Dangerous use cases: Live use changes player progression; avoid casual testing.
- Example test procedure: Use a staging target with a known task, run the command, and verify ::task shows no current task.

### Demon Hunter task assignment
- Main file: src/io/xeros/content/commands/admin/Dhtask.java
- Command name or method name: ::dhtask, ::dhtask boss_name amount
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: With no args, assigns a random Demon Hunter task; with args, sets a specific DemonSlayerTask and amount.
- Systems it affects: Demon Hunter task state, player progression, save data.
- Safe use cases: Local/staging Demon Hunter task and reward hook testing.
- Dangerous use cases: Local/staging only; it mutates progression.
- Example test procedure: Run ::dhtask boss_name 1 locally, kill the target boss, and verify Demon Hunter progress.

### Demon Marks adjustment
- Main file: src/io/xeros/content/commands/admin/Dhmarks.java
- Command name or method name: ::dhmarks, ::dhmarks amount
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Shows Demon Marks when no amount is provided; positive amounts add marks and negative amounts remove marks.
- Systems it affects: Demon Marks currency, Demon Hunter rewards, player save data.
- Safe use cases: Local shop/perk testing with disposable state.
- Dangerous use cases: NEVER LIVE ECONOMY unless owner-approved correction.
- Example test procedure: Add a small amount locally, open the Demon Hunter reward interface, verify spend behavior, then discard state.

### Demon Hunter contract assignment
- Main file: src/io/xeros/content/commands/admin/Dhcontract.java
- Command name or method name: ::dhcontract, ::dhcontract boss_name amount
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Shows the current Demon Hunter contract or sets a specific boss contract.
- Systems it affects: Demon Hunter contracts and player progression.
- Safe use cases: Local contract flow testing.
- Dangerous use cases: Local/staging only; it mutates progression.
- Example test procedure: Set a one-kill contract locally, complete it, and verify reward and reset behavior.

### Demon Hunter overlay
- Main file: src/io/xeros/content/commands/all/Dhs.java
- Command name or method name: ::dhs
- Required rank/permission: Public.
- What it does: Sends the Demon Hunter task overlay.
- Systems it affects: Demon Hunter UI.
- Safe use cases: Verify current task/contract display.
- Dangerous use cases: Read-only display.
- Example test procedure: Assign a local Demon Hunter task, run ::dhs, and confirm overlay text.

## World Event And Activity Boss Tools

### Trigger next world event
- Main file: src/io/xeros/content/commands/owner/Triggerworldevent.java
- Command name or method name: ::triggerworldevent
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Sets the next world event to trigger immediately.
- Systems it affects: World event scheduler and global player activity.
- Safe use cases: Local/staging world event timing tests.
- Dangerous use cases: Live use can start server-wide events unexpectedly.
- Example test procedure: Run on staging during a quiet test window, monitor announcements and event spawn behavior, then end/reset through normal event flow.

### Start Hespori world event
- Main file: src/io/xeros/content/commands/admin/Starthespori.java
- Command name or method name: ::starthespori
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Starts a HesporiWorldEvent.
- Systems it affects: World events, boss spawns, player rewards.
- Safe use cases: Local/staging Hespori event validation.
- Dangerous use cases: Live use starts a reward-bearing event.
- Example test procedure: Start locally, use ::worldevent to travel after spawn, kill the event boss, and verify rewards only on disposable state.

### Start WG world event
- Main file: src/io/xeros/content/commands/admin/StartWG.java
- Command name or method name: ::startwg
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Starts a WGWorldEvent.
- Systems it affects: World event scheduling, global event state, rewards.
- Safe use cases: Local/staging event start testing.
- Dangerous use cases: Live use starts a server-wide event.
- Example test procedure: Run on staging, verify announcements and event entry path, then complete or reset in a controlled test.

### Start tournament world event
- Main file: src/io/xeros/content/commands/admin/Starttourney.java
- Command name or method name: ::starttourney type
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Sets tournament type and starts TournamentWorldEvent.
- Systems it affects: Tournament event, player activity, rewards.
- Safe use cases: Local/staging tournament flow validation.
- Dangerous use cases: Live use starts a server-wide competitive event.
- Example test procedure: Run with a known type on staging, verify registration and start flow with test players.

### World event teleport
- Main file: src/io/xeros/content/commands/all/Worldevent.java
- Command name or method name: ::worldevent
- Required rank/permission: Public.
- What it does: Teleports the player to active Hespori when spawned.
- Systems it affects: Player movement and world event participation.
- Safe use cases: Verify event access once an event is active.
- Dangerous use cases: Normal player flow; avoid using admin event starts on live for testing.
- Example test procedure: Start Hespori locally, wait for spawn state, run ::worldevent, and verify teleport destination.

### World event information
- Main file: src/io/xeros/content/commands/all/events.java
- Command name or method name: ::events
- Required rank/permission: Public.
- What it does: Opens WorldEventInformation.
- Systems it affects: World event UI.
- Safe use cases: Confirm event display text and active event state.
- Dangerous use cases: Read-only.
- Example test procedure: Run before and after starting an event and compare displayed state.

### Wilderness event status
- Main file: src/io/xeros/content/commands/all/Event.java
- Command name or method name: ::event
- Required rank/permission: Public.
- What it does: Prints MonsterHunt current location and name.
- Systems it affects: Read-only MonsterHunt event display.
- Safe use cases: Verify wilderness event status.
- Dangerous use cases: Read-only.
- Example test procedure: Start or wait for MonsterHunt, run ::event, and confirm the location string is correct.

### Wilderness event teleport
- Main file: src/io/xeros/content/commands/all/Wildyevent.java
- Command name or method name: ::wildyevent
- Required rank/permission: Public.
- What it does: Teleports to MonsterHunt when active.
- Systems it affects: Player movement and event participation.
- Safe use cases: Verify access to active wilderness event.
- Dangerous use cases: Normal player flow, but can place player into PvP danger depending event rules.
- Example test procedure: Use only on local/staging or a consenting live account during a real event.

### Global boss debug
- Main file: src/io/xeros/content/commands/owner/Bossdebug.java
- Command name or method name: ::bossdebug
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Prints totals and thresholds for GlobalBossActivityManager activity types.
- Systems it affects: Read-only activity boss progression diagnostics.
- Safe use cases: Check whether activity contributions are accumulating correctly.
- Dangerous use cases: Read-only.
- Example test procedure: Perform one contribution action locally, run ::bossdebug, and confirm the matching activity total changed.

### Force activity boss
- Main file: src/io/xeros/content/commands/owner/Forceboss.java
- Command name or method name: ::forceboss GlobalBossType
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Forces a global boss spawn through GlobalBossActivityManager.forceSpawn.
- Systems it affects: Activity/global boss state, broadcasts, boss combat, rewards.
- Safe use cases: Local/staging boss spawn and contribution reward testing.
- Dangerous use cases: Live use starts reward-bearing global boss content unexpectedly.
- Example test procedure: Use staging with owner approval, force one boss, verify spawn, contribution tracking, death rewards, and cooldown.

### Set activity boss cooldown
- Main file: src/io/xeros/content/commands/owner/Setbosscooldown.java
- Command name or method name: ::setbosscooldown bossName seconds
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Sets global boss cooldown through GlobalBossActivityManager.setCooldown.
- Systems it affects: Global boss spawn timing and activity loop.
- Safe use cases: Local/staging cooldown behavior testing.
- Dangerous use cases: Live use can disrupt event cadence.
- Example test procedure: Set a short cooldown locally, contribute to the matching activity, and verify respawn timing.

### Global boss status
- Main file: src/io/xeros/content/commands/all/Bosses.java
- Command name or method name: ::bosses
- Required rank/permission: Public.
- What it does: Shows active global boss status, progress, cooldown, and player contribution entries.
- Systems it affects: Read-only global boss status UI.
- Safe use cases: Verify contribution display after activity hooks.
- Dangerous use cases: Read-only.
- Example test procedure: Run before and after one contribution event and confirm personal contribution display.

### Groot global boss spawn
- Main file: src/io/xeros/content/commands/admin/gboss.java
- Command name or method name: ::gboss
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Calls Groot.spawnGroot().
- Systems it affects: Global boss spawn, NPC combat, drops, announcements.
- Safe use cases: Local/staging Groot event testing.
- Dangerous use cases: Live use starts reward-bearing boss content.
- Example test procedure: Spawn locally, fight or kill with test tools, and verify drop/contribution flow.

### Donor boss spawn
- Main file: src/io/xeros/content/commands/admin/dboss.java
- Command name or method name: ::dboss
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Manually spawns the donor boss, broadcasts globally, and tracks DONOR_BOSS.
- Systems it affects: Donor boss event, NPC combat, drops, pass progress, announcements.
- Safe use cases: Staging donor boss event validation.
- Dangerous use cases: Live use starts reward-bearing donor content; owner approval required.
- Example test procedure: Spawn on staging, verify broadcast, contribution, drops, and pass progress with test players.

### Fluffie boss spawn
- Main file: src/io/xeros/content/commands/admin/fboss.java
- Command name or method name: ::fboss
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Calls Fluffie.handleSpawn() and contains older donor boss logic.
- Systems it affects: Fluffie/global boss spawn, drops, announcements.
- Safe use cases: Local/staging boss spawn validation.
- Dangerous use cases: Live use starts reward-bearing boss content.
- Example test procedure: Spawn locally, verify location, broadcast, and reward handling on death.

### Vote boss spawn
- Main file: src/io/xeros/content/commands/moderator/vboss.java
- Command name or method name: ::vboss
- Required rank/permission: Right.HELPER.
- What it does: Manually spawns the vote boss, broadcasts globally, tracks VOTE_BOSS, and reward flow can include drops, pass progress, and pet roll behavior.
- Systems it affects: Vote boss event, NPC combat, rewards, battlepass, announcements.
- Safe use cases: Official staff event or staging test only.
- Dangerous use cases: Live use is reward-bearing and should be owner-approved.
- Example test procedure: On staging, spawn once, verify broadcast, kill flow, drops, collection log, and Battlepass progress if applicable.

## Owner Economy And Player State Tools

### Save all player backups
- Main file: src/io/xeros/content/commands/owner/Saveall.java
- Command name or method name: ::saveall
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Calls PlayerSaveBackup.backup(LocalDateTime.now()).
- Systems it affects: Player save backups.
- Safe use cases: Back up saves before risky staging or live maintenance.
- Dangerous use cases: Low risk, but backups can be expensive depending player count and filesystem state.
- Example test procedure: Run before owner-approved live reloads or update timer operations.

### Server update timer
- Main file: src/io/xeros/content/commands/owner/Update.java
- Command name or method name: ::update seconds
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Starts update timer, saves WOGW, cancels active duels, and sends update timers to players.
- Systems it affects: Server update flow, duels, player sessions, global state.
- Safe use cases: Owner-approved live maintenance only.
- Dangerous use cases: Do not use as a test command on live; it disrupts all players.
- Example test procedure: Test locally with two clients if needed, verify timer display, then restart local server.

### Debug message toggle
- Main file: src/io/xeros/content/commands/owner/Debug.java
- Command name or method name: ::debug
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Toggles player.debugMessage.
- Systems it affects: Player debug chat messages.
- Safe use cases: Local/staging trace messages for content hooks.
- Dangerous use cases: Live use may spam staff chat/message box.
- Example test procedure: Toggle before testing a target hook, perform one action, read debug messages, and toggle off.

### Attack stat debug toggle
- Main file: src/io/xeros/content/commands/owner/AttackStats.java
- Command name or method name: ::attackstats
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Toggles combat attack stat debug messages.
- Systems it affects: Combat calculation diagnostics.
- Safe use cases: Local NPC combat accuracy/max-hit testing.
- Dangerous use cases: Live use may spam messages and expose combat internals.
- Example test procedure: Toggle locally, attack a target NPC, compare debug values, then toggle off.

### Defence stat debug toggle
- Main file: src/io/xeros/content/commands/owner/DefenceStats.java
- Command name or method name: ::defencestats
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Toggles combat defence stat debug messages.
- Systems it affects: Combat calculation diagnostics.
- Safe use cases: Local NPC/player defence roll testing.
- Dangerous use cases: Live use may spam messages and expose combat internals.
- Example test procedure: Toggle locally, take attacks from a target NPC, compare debug values, then toggle off.

### Max skills
- Main file: src/io/xeros/content/commands/owner/Max.java
- Command name or method name: ::max
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Sets skills 0 through 23 to high levels.
- Systems it affects: Player skills, combat level, progression unlocks, save data.
- Safe use cases: Local gear, boss, and skilling gate tests.
- Dangerous use cases: NEVER LIVE ECONOMY: It changes core progression.
- Example test procedure: Run on a local disposable account before testing high-level content.

### Set level
- Main file: src/io/xeros/content/commands/owner/Setlevel.java
- Command name or method name: ::setlevel skill level
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Sets one or more player skill levels depending command arguments.
- Systems it affects: Player skills, progression unlocks, save data.
- Safe use cases: Local skill gate testing.
- Dangerous use cases: NEVER LIVE ECONOMY unless owner-approved correction.
- Example test procedure: Set the minimum required level locally, attempt the target content, and verify access gating.

### Add XP
- Main file: src/io/xeros/content/commands/owner/Addxp.java
- Command name or method name: ::addxp skill amount
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Adds XP to a player skill.
- Systems it affects: Skills, progression unlocks, save data.
- Safe use cases: Local progression threshold testing.
- Dangerous use cases: NEVER LIVE ECONOMY unless owner-approved correction.
- Example test procedure: Add enough XP locally to cross one threshold and verify unlock messaging or achievement hooks.

### Reset stats
- Main file: src/io/xeros/content/commands/owner/ResetStats.java
- Command name or method name: ::resetstats
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Resets player stats.
- Systems it affects: Player skills and save data.
- Safe use cases: Local disposable account reset tests.
- Dangerous use cases: NEVER LIVE ECONOMY unless correcting an owner-approved issue.
- Example test procedure: Use only on a local throwaway account after skill testing.

### Set points
- Main file: src/io/xeros/content/commands/owner/Set.java
- Command name or method name: ::set slayer amount, ::set dp amount, ::set pkp amount, ::set players amount
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Mutates selected point or server modifier fields.
- Systems it affects: Slayer points, donation points, PKP, player modifier state depending branch.
- Safe use cases: Local shop and progression tests.
- Dangerous use cases: NEVER LIVE ECONOMY.
- Example test procedure: Use a disposable local account, set only the needed currency, open the target shop, and discard state.

### Cash grant
- Main file: src/io/xeros/content/commands/owner/Cash.java
- Command name or method name: ::cash
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Gives the player a very large amount of coins.
- Systems it affects: Inventory and economy.
- Safe use cases: Local shop purchase testing.
- Dangerous use cases: NEVER LIVE ECONOMY.
- Example test procedure: Run locally only, buy one shop item, verify price deduction, then discard state.

### Give all online players item
- Main file: src/io/xeros/content/commands/owner/giveall.java
- Command name or method name: ::giveall itemid amount
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Gives an item to online players and sends a global message.
- Systems it affects: Inventory, economy, announcements.
- Safe use cases: Local multi-client broadcast/reward testing.
- Dangerous use cases: NEVER LIVE ECONOMY unless owner-approved event reward.
- Example test procedure: Use two local test clients, give a harmless item, confirm both receive it, then discard state.

### Offline reward grant
- Main file: src/io/xeros/content/commands/owner/Givereward.java
- Command name or method name: ::givereward-player-itemid-amount
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Adds an offline reward through ItemCollection and sends a Discord-style message.
- Systems it affects: Offline rewards, item claims, economy, announcements.
- Safe use cases: Staging offline reward claim validation.
- Dangerous use cases: NEVER LIVE ECONOMY unless explicitly used for owner-approved compensation.
- Example test procedure: Grant one low-value item to a staging player, log in as that player, claim it, and verify the item arrives once.

### View stored rewards
- Main file: src/io/xeros/content/commands/owner/storedrewards.java
- Command name or method name: ::storedrewards
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Shows stored offline rewards.
- Systems it affects: Read-only offline reward inspection.
- Safe use cases: Validate pending reward state before or after reward tests.
- Dangerous use cases: Read-only.
- Example test procedure: Run before and after ::givereward-player-itemid-amount on staging.

### View player rewards
- Main file: src/io/xeros/content/commands/owner/Viewrewards.java
- Command name or method name: ::viewrewards-playername
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Views a player's offline rewards.
- Systems it affects: Read-only offline reward inspection.
- Safe use cases: Verify a specific pending reward before claim.
- Dangerous use cases: Read-only.
- Example test procedure: Grant a staging reward, run ::viewrewards-playername, and confirm item ID and amount.

### Clear player rewards
- Main file: src/io/xeros/content/commands/owner/Clearrewards.java
- Command name or method name: ::clearrewards-playername
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Clears a player's offline rewards.
- Systems it affects: Offline rewards and player compensation state.
- Safe use cases: Local/staging cleanup after tests.
- Dangerous use cases: NEVER LIVE ECONOMY unless owner-approved correction.
- Example test procedure: On staging only, create a test reward, verify it, clear it, and verify it no longer appears.

### Bonus toggle
- Main file: src/io/xeros/content/commands/admin/Bonus.java
- Command name or method name: ::bonus xp, ::bonus vote, ::bonus pc, ::bonus pkp, ::bonus drops, ::bonus pursuit
- Required rank/permission: Right.ADMINISTRATOR.
- What it does: Toggles selected global bonus configuration flags.
- Systems it affects: XP, voting, pest control, PKP, drops, and pursuit-style bonuses.
- Safe use cases: Local/staging bonus behavior tests.
- Dangerous use cases: NEVER LIVE ECONOMY unless owner-approved event activation.
- Example test procedure: Toggle one bonus locally, complete one matching action, confirm the multiplier, then toggle it back.

### Clipping overlay
- Main file: src/io/xeros/content/commands/owner/Clipping.java
- Command name or method name: ::clipping
- Required rank/permission: Right.STAFF_MANAGER.
- What it does: Displays nearby clipping flags using temporary ground item markers.
- Systems it affects: Clipping diagnostics and temporary visual markers.
- Safe use cases: Local/staging pathing and object placement testing.
- Dangerous use cases: Live use can clutter a player's local view.
- Example test procedure: Stand near a suspected clipping issue, run ::clipping, and compare markers with expected walkable tiles.

### Get clipping flag
- Main file: src/io/xeros/content/commands/owner/Getclip.java
- Command name or method name: ::getclip x y z
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Prints clipping data for a coordinate.
- Systems it affects: Read-only clipping diagnostics.
- Safe use cases: Verify pathing flags before object or NPC placement.
- Dangerous use cases: Read-only.
- Example test procedure: Use ::pos to get a nearby coordinate, then run ::getclip x y z.

### Modify current tile clipping
- Main file: src/io/xeros/content/commands/owner/ClipTile.java
- Command name or method name: ::cliptile flag
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Adds a clipping flag to the current tile.
- Systems it affects: Region clipping and movement.
- Safe use cases: Local-only pathing experiments.
- Dangerous use cases: Owner-review only: Live clipping edits can break movement and content access.
- Example test procedure: Use a local isolated tile, apply one flag, verify movement, then restart or reset local map state.

## Logs And Runtime Debugging

### Console and error logging
- Main file: src/io/xeros/Server.java
- Command name or method name: enableExceptionLogging()
- Required rank/permission: Server startup behavior.
- What it does: Creates runtime console and error log outputs under logs/error_logs/ and logs/console_logs/.
- Systems it affects: Server diagnostics and exception visibility.
- Safe use cases: Inspect startup failures, command exceptions, JSON/YAML loader errors, and runtime stack traces.
- Dangerous use cases: Logs can include sensitive operational details; do not publish raw live logs.
- Example test procedure: Start the server locally, reproduce the issue, then inspect the latest runtime console/error log.

### Game logging scheduler
- Main file: src/io/xeros/util/logging/GameLogging.java
- Command name or method name: write(Log), batchWrite(Log...), schedule()
- Required rank/permission: Called by server systems.
- What it does: Writes player and global logs to the configured game log directory.
- Systems it affects: Audit logging for items, shops, commands, achievements, daily rewards, and other player actions.
- Safe use cases: Trace whether a reward or command action was logged.
- Dangerous use cases: Logs can include sensitive player data; avoid sharing raw live logs.
- Example test procedure: Perform one test action locally, then inspect the matching log source class and generated runtime log.

### Command logs
- Main file: src/io/xeros/util/logging/player/CommandLog.java
- Command name or method name: fileName()
- Required rank/permission: Written by command packet flow.
- What it does: Defines command log output as commands.
- Systems it affects: Command auditing.
- Safe use cases: Verify a command was received and who used it.
- Dangerous use cases: Logs may contain sensitive command arguments.
- Example test procedure: Run a local command, then inspect the command log output for that player.

### Button click logs
- Main file: src/io/xeros/util/logging/player/ClickButtonLog.java
- Command name or method name: fileName()
- Required rank/permission: Written by button packet handlers.
- What it does: Defines button-click log output as buttons_clicked.
- Systems it affects: Interface and button debugging.
- Safe use cases: Identify button IDs during UI testing.
- Dangerous use cases: Packet logging can be noisy and may be disabled by configuration.
- Example test procedure: Click the target interface button locally and inspect button-click logs if enabled.

### Object click logs
- Main file: src/io/xeros/util/logging/player/ClickObjectLog.java
- Command name or method name: fileName()
- Required rank/permission: Written by object click handlers.
- What it does: Defines object-click log output as objects_clicked.
- Systems it affects: Object interaction debugging.
- Safe use cases: Identify object IDs and click options during local tests.
- Dangerous use cases: Logs can be noisy on active servers.
- Example test procedure: Click the target object locally and verify the object ID in the log.

### Packet logs
- Main file: src/io/xeros/util/logging/player/ReceivedPacketLog.java
- Command name or method name: fileName()
- Required rank/permission: Controlled by packet logging configuration.
- What it does: Defines received packet logging output as received_packets.
- Systems it affects: Low-level packet debugging.
- Safe use cases: Deep local debugging when interface or click flow is unclear.
- Dangerous use cases: Very noisy; src/io/xeros/Configuration.java sets DISABLE_PACKET_LOG true in the inspected repo.
- Example test procedure: Confirm packet logging is enabled in local config before expecting packet logs.

### Shop buy logs
- Main file: src/io/xeros/util/logging/player/ShopBuyLog.java
- Command name or method name: fileName(), linkedFileName()
- Required rank/permission: Written by shop buy flow.
- What it does: Logs items received from shop buys and links to the general items received log.
- Systems it affects: ShopAssistant, item economy, audit logs.
- Safe use cases: Verify shop purchase tests.
- Dangerous use cases: Live logs include economy-sensitive player data.
- Example test procedure: Buy one local test item, inspect shop buy log output, and compare item ID and amount.

### Shop sell logs
- Main file: src/io/xeros/util/logging/player/ShopSellLog.java
- Command name or method name: fileName(), linkedFileName()
- Required rank/permission: Written by shop sell flow.
- What it does: Logs shop sell actions and links to item-related logs.
- Systems it affects: ShopAssistant and item economy.
- Safe use cases: Verify shop sell value and item removal.
- Dangerous use cases: Live logs include economy-sensitive player data.
- Example test procedure: Sell one disposable local item to a test shop and inspect the log output.

### Fire of Exchange logs
- Main file: src/io/xeros/util/logging/player/FireOfExchangeLog.java
- Command name or method name: fileName()
- Required rank/permission: Written by Fire of Exchange burn flow.
- What it does: Logs Fire of Exchange burns.
- Systems it affects: FireOfExchange, item sinks, exchange economy.
- Safe use cases: Verify item burn value and output.
- Dangerous use cases: Live logs include economy-sensitive sink behavior.
- Example test procedure: Burn one disposable local item, inspect the Fire of Exchange log, and confirm item removal/reward.

### Daily reward logs
- Main file: src/io/xeros/util/logging/player/DailyRewardLog.java
- Command name or method name: fileName()
- Required rank/permission: Written by daily reward claim flow.
- What it does: Logs daily reward claims.
- Systems it affects: DailyRewards and reward auditing.
- Safe use cases: Verify local daily reward claim output.
- Dangerous use cases: Live logs include player reward data.
- Example test procedure: Claim a local daily reward and inspect the daily reward log output.

### Vote logs
- Main file: src/io/xeros/util/logging/player/VotedLog.java
- Command name or method name: fileName()
- Required rank/permission: Written by vote claim flow.
- What it does: Logs vote claims.
- Systems it affects: Vote rewards and vote auditing.
- Safe use cases: Verify staging vote claim behavior.
- Dangerous use cases: Live logs include player voting reward data.
- Example test procedure: Claim one staging vote reward with owner approval and inspect the vote log.

### Achievement claim logs
- Main file: src/io/xeros/util/logging/player/ClaimAchievementLog.java
- Command name or method name: fileName()
- Required rank/permission: Written by achievement claim flow.
- What it does: Logs achievement reward claims.
- Systems it affects: Achievements, achievement rewards, audit logs.
- Safe use cases: Verify local achievement reward claims.
- Dangerous use cases: Live logs include player progression data.
- Example test procedure: Complete one local achievement, claim it, and inspect the achievement claim log.

### Event log dump
- Main file: src/io/xeros/content/commands/owner/EventLog.java
- Command name or method name: ::eventlog
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Writes active cycle event details to a runtime event log text file.
- Systems it affects: Cycle event diagnostics and local filesystem output.
- Safe use cases: Local/staging event-loop debugging.
- Dangerous use cases: Can produce diagnostic output from live server internals.
- Example test procedure: Reproduce a stuck event locally, run ::eventlog, and inspect the generated event log output.

### NPC log dump
- Main file: src/io/xeros/content/commands/owner/Npclog.java
- Command name or method name: ::npclog
- Required rank/permission: Right.GAME_DEVELOPER.
- What it does: Writes active NPC details to a runtime NPC log text file.
- Systems it affects: NPC diagnostics and local filesystem output.
- Safe use cases: Local/staging NPC leak or spawn debugging.
- Dangerous use cases: Can expose live world state if shared.
- Example test procedure: Spawn or reproduce the NPC issue locally, run ::npclog, and inspect the generated NPC log output.

## Startup And Data Loader Validation

### Server startup loader
- Main file: src/io/xeros/ServerStartup.java
- Command name or method name: load()
- Required rank/permission: Server startup behavior.
- What it does: Loads rewards, player save entries, item definitions, shops, NPC stats/definitions/combat definitions, NPC handler, events, vote panel, drops, treasure trails, objects, collection log, commands, daily rewards, world events, Fire of Exchange prices, logging, offline rewards, and post-init tasks.
- Systems it affects: Most server data and content registries.
- Safe use cases: Best full validation path after data or content edits.
- Dangerous use cases: Startup failure can stop the server if required loaders throw.
- Example test procedure: Start a local server after edits, watch console output for loader errors, then test the specific changed content.

### Serverless data loader
- Main file: src/io/xeros/Server.java
- Command name or method name: startServerless(), loadData()
- Required rank/permission: Developer/runtime method, not an in-game command.
- What it does: Provides a serverless data-loading path without starting the normal network service.
- Systems it affects: Data loading and startup validation.
- Safe use cases: Future test harnesses can use this to validate loaders without full server networking.
- Dangerous use cases: Not an existing command; do not assume it replaces full integration tests.
- Example test procedure: If a test harness exists, call startServerless() once, assert data loads, and then inspect loader errors.

### Drop YAML loader
- Main file: src/io/xeros/model/entity/npc/drops/DropManager.java
- Command name or method name: read(), test(Player, int, int), getDropSample(Player, int)
- Required rank/permission: Loaded at startup and through ::reload drops; test methods are called by developer commands.
- What it does: Loads NPC drop YAML, validates entries, and provides drop sampling helpers.
- Systems it affects: NPC drops, collection log/drop hooks, boss rewards, economy.
- Safe use cases: Local YAML validation and drop sampling.
- Dangerous use cases: Drop table mistakes directly affect item supply.
- Example test procedure: Start locally, run ::reload drops, then use ::testdroptable npcId-amount on a disposable account.

### Shop YAML loader
- Main file: src/io/xeros/model/definitions/ShopDef.java
- Command name or method name: load()
- Required rank/permission: Loaded at startup and through ::reload shops.
- What it does: Loads shop definitions, validates duplicate shop IDs, and checks price bounds.
- Systems it affects: Shops, currencies, item economy.
- Safe use cases: Local shop data validation.
- Dangerous use cases: Shop price mistakes can create live exploits.
- Example test procedure: Start locally, run ::reload shops, open the target shop with ::shop shopId, and test one purchase on disposable state.

### Daily rewards YAML loader
- Main file: src/io/xeros/content/dailyrewards/DailyRewardContainer.java
- Command name or method name: load()
- Required rank/permission: Loaded at startup and through ::reload dailyrewards.
- What it does: Loads daily reward entries.
- Systems it affects: DailyRewards, achievement progress, economy.
- Safe use cases: Local daily reward config validation.
- Dangerous use cases: Reward mistakes affect daily login economy.
- Example test procedure: Run ::reload dailyrewards locally, open ::dailyreward, and verify the reward list.

### AOE boss tier JSON loader
- Main file: src/io/xeros/content/instances/aoe/AoeBossTierLoader.java
- Command name or method name: loadAllOrWarn(String)
- Required rank/permission: Startup behavior and AoeTierDebug admin reload.
- What it does: Loads AOE boss tier data from data/aoe/aoe_boss_tiers.json and has legacy support for data/aoe_tiers.json.
- Systems it affects: AOE tier definitions, NPC rosters, unlocks, kill goals.
- Safe use cases: Local JSON validation after AOE tier edits.
- Dangerous use cases: Bad tier data can block AOE progression or spawn invalid NPCs.
- Example test procedure: Run ::aoetierdebug tier reload locally, then start tiers 1 through 3 and verify NPCs and goals.

### AOE reward JSON loader
- Main file: src/io/xeros/content/instances/aoe/AoeTierRewardsLoader.java
- Command name or method name: reload()
- Required rank/permission: Startup behavior and AoeTierDebug admin reload.
- What it does: Loads AOE tier rewards from data/aoe/aoe_tier_rewards.json.
- Systems it affects: AOE completion or drop reward behavior depending current reward hooks.
- Safe use cases: JSON-only AOE reward testing.
- Dangerous use cases: Reward amount mistakes affect economy and progression.
- Example test procedure: Run ::aoetierdebug rewards reload locally, show rewards for each edited tier, then complete tier 1 on a disposable account.

### AOE zone map JSON loader
- Main file: src/io/xeros/content/instances/aoe/AoeZoneMaps.java
- Command name or method name: reload()
- Required rank/permission: Startup or admin-driven reload depending caller.
- What it does: Loads AOE zone map config from data/aoe/AoeZoneMapConfig.json.
- Systems it affects: AOE zone placement, instance maps, NPC spawn locations.
- Safe use cases: Local zone map validation.
- Dangerous use cases: Bad map data can trap players or break instance spawning.
- Example test procedure: Reload locally, start the affected AOE tier, verify entrance, spawns, hazards, and exit.

### JSON/YAML validation command
- Main file: Not found in repo.
- Command name or method name: Not found in repo.
- Required rank/permission: Not found in repo.
- What it does: No single command was found that validates every JSON/YAML data file without loading the server.
- Systems it affects: Use server startup plus specific reload commands for validation.
- Safe use cases: Full local startup and targeted reloads.
- Dangerous use cases: Do not trust data edits without startup validation.
- Example test procedure: Searched terms: validate json, YAML, Gson, ObjectMapper, reload, load().

## Unit Tests And Test Classes

### Gradle test runner
- Main file: build.gradle
- Command name or method name: test, useJUnitPlatform()
- Required rank/permission: Local developer shell.
- What it does: Configures JUnit 5 and Mockito for unit tests.
- Systems it affects: Local automated test validation.
- Safe use cases: Run before and after Java changes.
- Dangerous use cases: Tests may not cover all content systems; passing tests do not replace local server smoke tests.
- Example test procedure: Run gradlew.bat test from the repo root and inspect failures before merging.

### Wraith charge tests
- Main file: src/test/java/io/xeros/content/wraith/WraithChargesTest.java
- Command name or method name: JUnit test class
- Required rank/permission: Local developer shell.
- What it does: Tests Wraith charge cap and charge consumption behavior.
- Systems it affects: WraithCharges.
- Safe use cases: Validate Wraith charge changes.
- Dangerous use cases: Does not test full in-game item flow or economy.
- Example test procedure: Run gradlew.bat test after editing Wraith charge logic.

### Boss tier damage tests
- Main file: src/test/java/io/xeros/content/instances/BossTierDamageTest.java
- Command name or method name: JUnit test class
- Required rank/permission: Local developer shell.
- What it does: Tests BossInstanceManager tier scaling, rosters, and reward calculations.
- Systems it affects: Boss instances and tiered boss damage/reward logic.
- Safe use cases: Validate instance scaling changes.
- Dangerous use cases: Does not replace manual AOE entry, spawn, and reward testing.
- Example test procedure: Run gradlew.bat test, then manually start the edited tier in-game.

### Demon Hunter dialogue tests
- Main file: src/test/java/DemonHunterSlayerDialogueTest.java
- Command name or method name: JUnit test class
- Required rank/permission: Local developer shell.
- What it does: Tests Demon Hunter dialogue menu/back behavior and task assignment with Mockito.
- Systems it affects: Demon Hunter dialogue and task assignment.
- Safe use cases: Validate Demon Hunter dialogue edits.
- Dangerous use cases: Does not validate live NPC click wiring unless manually tested.
- Example test procedure: Run gradlew.bat test, then click the Demon Hunter master locally.

### Demon Hunter perk tests
- Main file: src/test/java/DemonHunterPerksTest.java
- Command name or method name: JUnit test class
- Required rank/permission: Local developer shell.
- What it does: Tests Demon Hunter perk unlocks and damage bonus behavior.
- Systems it affects: Demon Hunter perks and damage bonuses.
- Safe use cases: Validate Demon Hunter progression changes.
- Dangerous use cases: Does not cover all boss or reward edge cases.
- Example test procedure: Run gradlew.bat test after perk edits, then test one in-game kill locally.

### Sample test placeholder
- Main file: src/test/java/HelloTest.java
- Command name or method name: JUnit test class
- Required rank/permission: Local developer shell.
- What it does: Contains commented sample tests or no active meaningful coverage.
- Systems it affects: None found.
- Safe use cases: Not useful for content validation.
- Dangerous use cases: Do not treat it as meaningful coverage.
- Example test procedure: Not found in active behavior. Searched terms: HelloTest, @Test, assert.

### Dedicated drop/shop/AOE JSON parser unit tests
- Main file: Not found in repo.
- Command name or method name: Not found in repo.
- Required rank/permission: Not found in repo.
- What it does: No dedicated unit tests were found for all drop, shop, or AOE JSON/YAML parser paths beyond current startup/reload validation and the existing instance tests.
- Systems it affects: Use startup, reload commands, and manual smoke tests.
- Safe use cases: Add future focused tests only when a coding task explicitly asks for tests.
- Dangerous use cases: Do not assume data parser edits are covered by existing tests.
- Example test procedure: Searched terms: DropManagerTest, ShopDefTest, AoeTierRewardsLoaderTest, AoeBossTierLoaderTest, YAML, JSON.

## A. Best Tools For Testing AOE Updates

- Use src/io/xeros/content/instances/aoe/AoeTierDebug.java first for ::aoetierdebug rewards reload, ::aoetierdebug rewards show, ::aoetierdebug tier reload, and controlled tier starts.
- Use src/io/xeros/content/commands/admin/Testaoe.java for quick local tier entry with ::testaoe tier.
- Use src/io/xeros/content/commands/admin/Aoespawns.java to confirm spawn counts.
- Use src/io/xeros/content/commands/admin/Aoeaggro.java to test forced aggro behavior.
- Use src/io/xeros/content/commands/admin/Killallaoe.java only on disposable local or staging state because it can trigger progress/rewards.
- Use src/io/xeros/content/commands/all/Leaveaoe.java to cleanly exit after tests.
- Run src/test/java/io/xeros/content/instances/BossTierDamageTest.java through gradlew.bat test when instance scaling code is touched.

## B. Best Tools For Testing Boss Rewards

- Use src/io/xeros/content/commands/owner/Testdroptable.java locally for drop table sampling, but remember it modifies the bank.
- Use src/io/xeros/content/commands/test/DropTest.java locally on debug/test servers for sampled drops, but remember it clears the full bank.
- Use src/io/xeros/content/commands/owner/Simulate.java locally to exercise DropManager.create and kill tracker hooks, but treat it as NEVER LIVE ECONOMY.
- Use src/io/xeros/content/commands/owner/Bossdebug.java to inspect activity boss contribution totals.
- Use src/io/xeros/content/commands/all/Bosses.java to verify public global boss status and contribution display.
- Use src/io/xeros/content/commands/owner/Forceboss.java only on local/staging or owner-approved event testing.

## C. Best Tools For Testing Achievements

- Use src/io/xeros/content/commands/test/Completeachievement.java on debug/test local servers for completion and claim flow.
- Use src/io/xeros/content/commands/owner/achieve.java for achievement UI smoke tests.
- Use src/io/xeros/util/logging/player/ClaimAchievementLog.java to confirm achievement claims were logged.
- For realistic hook tests, trigger the real event that calls src/io/xeros/content/achievement/Achievements.java instead of forcing completion.

## D. Best Tools For Testing Task Master

- No dedicated Task Master test command was found.
- Use real gameplay triggers defined around src/io/xeros/content/taskmaster/Tasks.java, src/io/xeros/content/taskmaster/TaskMaster.java, and src/io/xeros/content/taskmaster/TaskMasterKills.java.
- Use local item/NPC spawn and teleport tools only to set up the real event.
- Verify progress through the normal Task Master interface or player state after one real trigger.
- Searched terms: TaskMaster, TaskMasterKills, Tasks, taskmaster command, weekly task.

## E. Best Tools For Testing Shops And Economy

- Use src/io/xeros/content/commands/owner/Shop.java to open a shop by ID.
- Use src/io/xeros/content/commands/owner/Reload.java with ::reload shops only on local/staging after shop data changes.
- Use src/io/xeros/content/commands/owner/Cash.java, src/io/xeros/content/commands/test/Points.java, or src/io/xeros/content/commands/owner/Set.java only on disposable local accounts.
- Use src/io/xeros/util/logging/player/ShopBuyLog.java and src/io/xeros/util/logging/player/ShopSellLog.java to verify transactions.
- Use src/io/xeros/model/shops/ShopAssistant.java and src/io/xeros/model/definitions/ShopDef.java as the source paths to inspect when shop behavior is unclear.
- NEVER LIVE ECONOMY: Do not test unfinished shops with spawned currencies or bulk point tools on production accounts.

## F. Best Local Test Checklist Before Merging Codex PRs

- Search for an existing command or debug tool before adding any new test hook.
- Confirm any command used is loaded in the intended environment through src/io/xeros/content/commands/CommandManager.java.
- Run gradlew.bat test when Java code changed or the touched system has existing tests.
- Start the server locally after data edits and watch src/io/xeros/ServerStartup.java loader output.
- Use the narrowest reload command possible on local or staging, then restart once to confirm startup still works.
- Use disposable local player saves for spawned items, points, drops, achievements, Battlepass XP, donation state, and collection log completion.
- Verify reward behavior through normal player-facing commands or interfaces after using any setup tool.
- Inspect relevant runtime logs through the logging classes in src/io/xeros/util/logging/player/.
- Document exact test commands used and whether they mutate economy/progression state.

## G. Best Live-Server Safety Checklist Before Pushing Updates

- Do not use NEVER LIVE ECONOMY tools on production accounts.
- Back up saves with src/io/xeros/content/commands/owner/Saveall.java before owner-approved live maintenance.
- Avoid live ::reload drops, ::reload shops, ::reload items, ::reload npcs, ::aoetierdebug tier reload, and ::reloadaoezone unless the owner approves the maintenance window.
- Prefer full restart validation over risky hot reloads for definition, shop, NPC, and drop changes.
- Do not force world events, activity bosses, vote bosses, donor bosses, or reward bosses on live unless it is an announced owner-approved event.
- Do not grant points, donation amounts, Battlepass XP, collection log progress, achievements, or offline rewards except as owner-approved compensation.
- Test all reward amounts locally first against docs/TURMOIL_REWARD_ECONOMY_AUDIT.md before adding them to live content.
- Keep debug toggles such as ::debug, ::attackstats, ::defencestats, hazardvision, and mutatorvision off for normal live play.
- After deployment, verify public read-only commands first: ::commands, ::staffcommands, ::bosses, ::vpanel, ::bp, ::task, ::dhs, and relevant content status commands.
