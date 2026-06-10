# Turmoil Content Creation Pattern Book

This recipe book is for future Codex tasks adding content to Turmoil. It uses these docs as context:

- `docs/TURMOIL_CONTENT_GUIDE.md`
- `docs/TURMOIL_CONTENT_INDEX.md`
- `docs/TURMOIL_PROGRESSION_AUDIT.md`
- `docs/TURMOIL_SERVER_METHOD_FLOW_MAP.md`

Core rule: copy the closest working local pattern, add content through data, enums, managers, or small content classes where possible, and avoid rewriting packet, combat, drop, save, shop, or dialogue cores for one content update.

## 1. Adding A Simple Command

- Best existing file to copy: `src/io/xeros/content/commands/all/Leaderboards.java`
- Files usually edited:
  - `src/io/xeros/content/commands/all/`
  - `src/io/xeros/content/commands/Command.java`
- Files to avoid:
  - `src/io/xeros/model/entity/player/packets/Commands.java`
  - `src/io/xeros/content/commands/CommandManager.java`
- Required method/hook:
  - Extend `Command`.
  - Implement `execute(Player player, String commandName, String input)`.
  - Implement `hasPrivilege(Player player)`.
  - Optional: override `getDescription()`, `getFormat()`, or `getParameter()`.
- Required player fields or save entry: None unless the command changes persistent progression.
- Reward/economy risk: Low for interface, teleport, or info commands. Medium or High if the command grants items, points, teleports into dangerous content, or bypasses progression.
- Testing checklist:
  - Start the server and run the command by name.
  - Confirm the command is found without editing `CommandManager`.
  - Confirm no rank should be required.
  - Confirm bad or blank input is handled.
- Example search terms future agents should use before coding:
  - `extends Command`
  - `getDescription()`
  - `hasPrivilege(Player player)`
  - `Leaderboards extends Command`

## 2. Adding A Command With Permission Or Rank Checks

- Best existing file to copy: `src/io/xeros/content/commands/admin/Broadcast.java`
- Files usually edited:
  - `src/io/xeros/content/commands/admin/`
  - `src/io/xeros/content/commands/moderator/`
  - `src/io/xeros/content/commands/donator/`
  - `src/io/xeros/content/commands/Command.java`
- Files to avoid:
  - `src/io/xeros/model/entity/player/packets/Commands.java`
  - `src/io/xeros/content/commands/CommandManager.java`
- Required method/hook:
  - Put the command in the correct package for the intended rank.
  - Use `Right.ADMINISTRATOR.isOrInherits(player)`, `Right.HELPER.isOrInherits(player)`, `Right.MODERATOR.isOrInherits(player)`, or a matching local rank check in `hasPrivilege(Player player)`.
  - For player-targeting commands, copy lookup and null handling from `src/io/xeros/content/commands/moderator/Kick.java`.
- Required player fields or save entry: Usually none. Staff commands that change rights, bans, inventory, points, or cooldowns must use the existing system field or manager for that feature.
- Reward/economy risk: Medium for commands that mutate player state. High for item, points, donation, rights, or punishment commands.
- Testing checklist:
  - Test with an account below the required rank.
  - Test with the intended rank.
  - Test blank input and invalid targets.
  - Confirm the command is not visible or usable from an unintended package.
- Example search terms future agents should use before coding:
  - `Right.ADMINISTRATOR.isOrInherits`
  - `Right.HELPER.isOrInherits`
  - `PlayerHandler.getOptionalPlayerByDisplayName`
  - `Broadcast extends Command`

## 3. Adding A Dialogue Using DialogueBuilder

- Best existing file to copy: `src/io/xeros/content/dialogue/impl/BossInstanceDialogue.java`
- Files usually edited:
  - `src/io/xeros/content/dialogue/impl/`
  - `src/io/xeros/content/dialogue/DialogueBuilder.java`
  - A small interaction hook such as `src/io/xeros/model/entity/player/packets/npcoptions/NpcOptionOne.java`
- Files to avoid:
  - `src/io/xeros/model/entity/player/DialogueHandler.java`
  - Legacy `dialogueAction` flows unless modifying an existing legacy dialogue.
- Required method/hook:
  - Start the dialogue with `player.start(new DialogueBuilder(player)...)` or a `DialogueBuilder` subclass.
  - Use `option(...)`, `statement(...)`, `npc(...)`, `player(...)`, `action(...)`, `exit(...)`, and `continueAction(...)` from `DialogueBuilder`.
  - Use `DialogueOption` for option callbacks.
- Required player fields or save entry: None for display-only dialogue. Add or reuse save data only if the dialogue changes progression.
- Reward/economy risk: Low unless the dialogue grants rewards, opens shops, or consumes currency.
- Testing checklist:
  - Click through every dialogue branch.
  - Confirm the dialogue closes on cancel and after final step.
  - Confirm callbacks do not fire twice.
  - Confirm inventory and point checks happen before deleting costs.
- Example search terms future agents should use before coding:
  - `new DialogueBuilder`
  - `DialogueOption`
  - `player.start(new`
  - `BossInstanceDialogue`

## 4. Adding An NPC Interaction

- Best existing file to copy: `src/io/xeros/model/entity/npc/NPCAction.java`
- Files usually edited:
  - `src/io/xeros/model/entity/npc/NPCAction.java`
  - `src/io/xeros/model/entity/player/packets/npcoptions/NpcOptionOne.java`
  - `src/io/xeros/model/entity/player/packets/npcoptions/NpcOptionTwo.java`
  - `src/io/xeros/model/entity/player/packets/npcoptions/NpcOptionThree.java`
- Files to avoid:
  - `src/io/xeros/model/entity/player/packets/ClickNPC.java`
  - `src/io/xeros/model/entity/npc/NPCHandler.java`
- Required method/hook:
  - Prefer `NPCAction.register(int npcId, int option, NPCAction action)` when an init-time registration fits.
  - Otherwise add a narrow case to `NpcOptionOne.handleOption(Player player, int npcType)` or the matching option handler.
  - Use `NPCAction.handle(Player player, NPC npc)` for registered actions.
- Required player fields or save entry: None unless the interaction changes unlocks, cooldowns, points, or task progress.
- Reward/economy risk: Low for dialogue, shop opening, teleport, or interface opening. Medium if the NPC exchanges items or points.
- Testing checklist:
  - Test first, second, and third click if the NPC has multiple options.
  - Confirm pets and special handlers still return before your logic if needed.
  - Confirm the NPC exists and the clicked option is the intended one.
  - Confirm the interaction respects trade, duel, and movement guards.
- Example search terms future agents should use before coding:
  - `NPCAction.register`
  - `NpcOptionOne.handleOption`
  - `NpcOptionTwo.handleOption`
  - `clickNpcType`

## 5. Adding An Object Interaction

- Best existing file to copy: `src/io/xeros/model/entity/player/packets/objectoptions/ObjectOptionOne.java`
- Files usually edited:
  - `src/io/xeros/model/entity/player/packets/objectoptions/ObjectOptionOne.java`
  - `src/io/xeros/model/entity/player/packets/objectoptions/ObjectOptionTwo.java`
  - `src/io/xeros/model/entity/player/packets/objectoptions/ObjectOptionThree.java`
  - `src/io/xeros/content/instances/InstancedArea.java`
- Files to avoid:
  - `src/io/xeros/model/entity/player/packets/ClickObject.java` for ordinary object behavior after the option dispatch already reaches option handlers.
  - Broad global object handling for one isolated minigame or boss.
- Required method/hook:
  - Add the smallest object case to `ObjectOptionOne.handleOption(final Player c, int objectType, int obX, int obY)` or the matching option handler.
  - For instance-specific objects, prefer the instance area's object click hook instead of global object switches.
- Required player fields or save entry: None unless the object starts progression, claims a reward, opens a cooldown, or stores state.
- Reward/economy risk: Low for interface or teleport objects. Medium for chests, shops, and item sinks. High for objects that grant rare gear or points.
- Testing checklist:
  - Click the object from every reachable side.
  - Confirm object distance and offsets are correct.
  - Confirm wilderness, duel, trade, and instance restrictions are respected.
  - Confirm repeated clicking cannot duplicate rewards.
- Example search terms future agents should use before coding:
  - `ObjectOptionOne.handleOption`
  - `objectDistance`
  - `GlobalObject object`
  - `handleClickObject`

## 6. Adding An Item Interaction

- Best existing file to copy: `src/io/xeros/model/items/ItemAction.java`
- Files usually edited:
  - `src/io/xeros/model/items/ItemAction.java`
  - `src/io/xeros/model/entity/player/packets/itemoptions/ItemOptionOne.java`
  - `src/io/xeros/model/entity/player/packets/itemoptions/ItemOptionTwo.java`
  - `src/io/xeros/model/entity/player/packets/itemoptions/ItemOptionThree.java`
- Files to avoid:
  - Large unrelated switches in item option packet handlers when `ItemAction.registerInventory` can handle the item.
  - Item definition loading unless the item action must be attached through definitions.
- Required method/hook:
  - Prefer `ItemAction.registerInventory(int itemId, int option, ItemAction action)`.
  - Existing packet flow checks `ItemDef.inventoryActions` before legacy item-id branches.
  - For legacy-only items, add a focused case inside the correct `ItemOption` packet class.
- Required player fields or save entry: None unless the item toggles a persistent feature or consumes saved charges.
- Reward/economy risk: Medium if the item opens boxes, grants membership, teleports, or creates gear. Low for informational items.
- Testing checklist:
  - Test the exact inventory option number.
  - Confirm the item exists in the inventory before deleting it.
  - Confirm stackable amounts are handled.
  - Confirm no action runs during trade, duel, bank pin, or locked movement states if those checks apply.
- Example search terms future agents should use before coding:
  - `ItemAction.registerInventory`
  - `inventoryActions`
  - `ItemOptionOne`
  - `new GameItem(itemId)`

## 7. Adding An Item-On-Item Recipe

- Best existing file to copy: `src/io/xeros/content/items/ItemCombinations.java`
- Files usually edited:
  - `src/io/xeros/content/items/ItemCombinations.java`
  - `src/io/xeros/content/items/item_combinations/`
  - `src/io/xeros/content/items/UseItem.java`
  - `src/io/xeros/model/items/ItemCombination.java`
- Files to avoid:
  - `src/io/xeros/model/entity/player/packets/ItemOnItem.java`
  - Broad `UseItem.ItemonItem(...)` branches for a normal two-item combination when `ItemCombinations` fits.
- Required method/hook:
  - Add an `ItemCombinations` enum entry for standard combine or revert behavior.
  - Implement or copy an `ItemCombination` subclass with `showDialogue(Player player)` and `combine(Player player)`.
  - The packet flow calls `UseItem.ItemonItem(...)`, then `ItemCombinations.getCombinations(...)`.
- Required player fields or save entry: Usually none. Use saved charges only when the recipe changes a persistent charged item.
- Reward/economy risk: Medium because recipes can create gear and item sinks. High for endgame, best-in-slot, or reversible value loops.
- Testing checklist:
  - Test both item orders.
  - Confirm all required items are present before showing or completing the combine.
  - Confirm every consumed item and produced item amount is correct.
  - Confirm the recipe cannot duplicate items through full inventory, bank pin, trade, or duel edge cases.
- Example search terms future agents should use before coding:
  - `ItemCombinations.getCombinations`
  - `extends ItemCombination`
  - `showDialogue(Player player)`
  - `combine(Player player)`

## 8. Adding An Item-On-Object Recipe

- Best existing file to copy: `src/io/xeros/content/tools/ToolAugments.java`
- Files usually edited:
  - `src/io/xeros/content/items/UseItem.java`
  - `src/io/xeros/model/entity/player/packets/ItemOnObject.java`
  - A focused content manager under `src/io/xeros/content/`
- Files to avoid:
  - `src/io/xeros/model/entity/player/packets/ItemOnObject.java` for reward logic after it has already dispatched to `UseItem.ItemonObject(...)`.
  - Object option handlers when the action is specifically item-on-object.
- Required method/hook:
  - Add the object and item handling in `UseItem.ItemonObject(Player c, int objectID, int objectX, int objectY, int itemId)`.
  - Prefer a small manager method such as `ToolAugments.useOnTable(c, itemId)` when the interaction has more than one item or reward.
- Required player fields or save entry: None unless the object recipe unlocks permanent progression.
- Reward/economy risk: Medium for sinks and crafting. High if the object creates rare gear or consumes upgrade points.
- Testing checklist:
  - Test item-on-object from all sides.
  - Confirm the object exists through `ClickObject.getObject(...)`.
  - Confirm item presence and amount before deletion.
  - Confirm output behavior with a full inventory.
- Example search terms future agents should use before coding:
  - `UseItem.ItemonObject`
  - `ToolAugments.useOnTable`
  - `getFarming().handleItemOnObject`
  - `objectDistance`

## 9. Adding A New Boss Using An Existing Boss Pattern

- Best existing file to copy: `src/io/xeros/content/bosses/obor/OborNPC.java`
- Files usually edited:
  - `src/io/xeros/content/bosses/`
  - `src/io/xeros/content/combat/npc/NPCAutoAttackBuilder.java`
  - `src/io/xeros/content/combat/npc/NPCAutoAttack.java`
  - Runtime NPC spawn and drop data, if the boss needs definitions or tables outside repo-local Java.
- Files to avoid:
  - `src/io/xeros/model/entity/npc/NPCProcess.java`
  - `src/io/xeros/model/entity/npc/NPCHandler.java`
  - `src/io/xeros/content/combat/death/NPCDeath.java` for ordinary boss loot.
- Required method/hook:
  - Use a boss NPC class and `NPCAutoAttackBuilder` for custom attacks.
  - Let normal death and drops flow through `NPCDeath.dropItemsFor(NPC npc, Player player, int npcId)`.
  - Add only narrow hooks for boss-specific state or instance cleanup.
- Required player fields or save entry: None for a simple boss. Use `PlayerSaveEntry` only for new persistent unlocks, streaks, or cooldowns.
- Reward/economy risk: Medium for normal bosses. High for bosses with new rare gear, high point rewards, or global broadcasts.
- Testing checklist:
  - Spawn the boss in a safe test area.
  - Confirm attack style, max hit, animation, projectile, and delay.
  - Kill the boss and verify drops, boss points, kill count, pets, achievements, and collection log behavior.
  - Confirm respawn and instance cleanup.
- Example search terms future agents should use before coding:
  - `NPCAutoAttackBuilder`
  - `setOnAttack`
  - `setOnHit`
  - `src/io/xeros/content/bosses/`

## 10. Adding A Boss Special Attack

- Best existing file to copy: `src/io/xeros/content/bosses/sarachnis/SarachnisNpc.java`
- Files usually edited:
  - `src/io/xeros/content/bosses/`
  - `src/io/xeros/content/combat/npc/NPCAutoAttackBuilder.java`
  - `src/io/xeros/content/combat/npc/NPCAutoAttack.java`
- Files to avoid:
  - `src/io/xeros/model/entity/npc/NPCProcess.java`
  - `src/io/xeros/model/entity/npc/actions/NPCHitPlayer.java`
  - Generic combat damage classes for one boss mechanic.
- Required method/hook:
  - Build a `NPCAutoAttack` with `setCombatType`, `setAnimation`, `setMaxHit`, `setAttackDelay`, `setHitDelay`, `setProjectile`, `setOnAttack`, or `setOnHit`.
  - Use existing player selectors for multi-target behavior when possible.
- Required player fields or save entry: None unless the special applies persistent effects.
- Reward/economy risk: Low for mechanics only. Medium if the special changes drop eligibility or contribution.
- Testing checklist:
  - Fight the boss long enough to see every special.
  - Confirm protection prayers, distance, multi-combat, and instance boundaries behave as expected.
  - Confirm special attack timing does not stall normal attacks.
  - Confirm the boss cannot damage players outside the intended area.
- Example search terms future agents should use before coding:
  - `setOnAttack`
  - `setOnHit`
  - `getDefaultSelectPlayersForAttack`
  - `SarachnisNpc`

## 11. Adding A Boss Death Reward Hook

- Best existing file to copy: `src/io/xeros/content/combat/death/NPCDeath.java`
- Files usually edited:
  - `src/io/xeros/content/combat/death/NPCDeath.java`
  - A boss-specific class under `src/io/xeros/content/bosses/`
  - A reward manager under `src/io/xeros/content/`
- Files to avoid:
  - Hardcoding ordinary drops in `NPCDeath`.
  - Adding unrelated reward logic to `src/io/xeros/model/entity/npc/NPCProcess.java`.
- Required method/hook:
  - Ordinary drops should use `Server.getDropManager().create(...)` through `NPCDeath.dropItemsFor(...)`.
  - Special one-off reward hooks can run from `NPCDeath.dropItemsFor(...)` only when there is no normal drop-table path.
  - For activity bosses, use `GlobalBossActivityManager.onBossDeath(NPC npc, Player killer)`.
- Required player fields or save entry: Use existing fields for points or KC. Use `PlayerSaveEntry` if adding a new persistent reward state.
- Reward/economy risk: High because boss death hooks can duplicate or bypass drop-table economy controls.
- Testing checklist:
  - Kill the boss once and verify one reward path fires.
  - Kill it in an instance and outside an instance if both are possible.
  - Confirm group damage, killer attribution, collection log, and rare broadcasts.
  - Confirm no reward is given when the NPC is despawned or force-killed.
- Example search terms future agents should use before coding:
  - `NPCDeath.dropItemsFor`
  - `Server.getDropManager().create`
  - `GlobalBossActivityManager.onBossDeath`
  - `rewardPlayers`

## 12. Adding Boss Points

- Best existing file to copy: `src/io/xeros/content/bosspoints/BossPoints.java`
- Files usually edited:
  - Runtime boss point config loaded by `BossPoints.init()`.
  - `src/io/xeros/content/bosspoints/BossPoints.java` only when changing shared boss point behavior.
  - Boss or minigame completion code only for manual boss point awards.
- Files to avoid:
  - `src/io/xeros/content/combat/death/NPCDeath.java` for ordinary point values.
  - Directly editing `player.bossPoints` outside `BossPoints.addPoints(...)` unless copying an existing legacy manual pattern.
- Required method/hook:
  - Normal NPC death points use `BossPoints.getPointsOnDeath(NPC npc)` and `BossPoints.addPoints(Player player, int points, boolean message)`.
  - Manual completion rewards use `BossPoints.addManualPoints(Player player, String name)`.
- Required player fields or save entry:
  - Existing `player.bossPoints`.
  - Existing `player.bossPointsRefund` for historical refund safety.
- Reward/economy risk: Medium to High. Boss points feed shops, Demon Hunter XP, event calendar progress, and leaderboards.
- Testing checklist:
  - Kill the NPC and confirm points match the runtime config.
  - Confirm `EventChallenge.GAIN_X_BOSS_POINTS` progresses.
  - Confirm `LeaderboardType.BOSS_POINTS` increments.
  - Confirm manual rewards do not also award death points unless intended.
- Example search terms future agents should use before coding:
  - `BossPoints.addPoints`
  - `BossPoints.addManualPoints`
  - `BossPoints.getPointsOnDeath`
  - `boss_points`
- Not found in repo:
  - Repo-local boss point data file. Searched terms: `boss_points`, `BossPointEntry`, `manual points`.

## 13. Adding An Achievement

- Best existing file to copy: `src/io/xeros/content/achievement/Achievements.java`
- Files usually edited:
  - `src/io/xeros/content/achievement/Achievements.java`
  - `src/io/xeros/content/achievement/AchievementType.java`
  - `src/io/xeros/content/achievement/AchievementHandler.java`
  - The content file that will call the progress hook.
- Files to avoid:
  - Player save parsing for achievement data unless fixing an old save key.
  - Duplicating reward claim logic outside `AchievementHandler`.
- Required method/hook:
  - Add or reuse an `AchievementType`.
  - Add an `Achievements.Achievement` enum entry with tier, id, target amount, points, and rewards.
  - Increment progress with `Achievements.increase(Player player, AchievementType type, int amount)`.
  - Kill achievements can also flow through `AchievementHandler.kill(NPC npc)`.
- Required player fields or save entry:
  - Existing achievement arrays inside `AchievementHandler`.
  - Existing save print/read behavior in `AchievementHandler.print(...)` and `AchievementHandler.readFromSave(...)`.
- Reward/economy risk: Medium. Achievement rewards include boxes, points, crystals, and utility items.
- Testing checklist:
  - Trigger progress once and confirm the interface updates.
  - Claim the achievement and confirm rewards are granted once.
  - Relog and confirm progress persists.
  - Confirm the achievement id does not collide inside the same tier.
- Example search terms future agents should use before coding:
  - `Achievements.increase`
  - `AchievementType`
  - `AchievementTier.STARTER`
  - `AchievementHandler.kill`

## 14. Adding A Task Master Task

- Best existing file to copy: `src/io/xeros/content/taskmaster/Tasks.java`
- Files usually edited:
  - `src/io/xeros/content/taskmaster/Tasks.java`
  - `src/io/xeros/content/taskmaster/TaskMaster.java`
  - `src/io/xeros/content/taskmaster/TaskMasterKills.java`
  - The content hook that increments the task.
- Files to avoid:
  - Player save core.
  - Adding a second daily or weekly task system for the same purpose.
- Required method/hook:
  - Add a `Tasks` enum entry with amount, display name, difficulty, type, daily flag, and wildcard flag.
  - Existing task progress uses `TaskMasterKills.incrementAmountKilled(int amountKilled)`.
  - Existing completion uses `TaskMaster.trackActivity(Player player, TaskMasterKills kills)` and `TaskMaster.finishTask(Player player, TaskMasterKills kills)`.
  - Task generation uses `TaskMaster.generateTasks(Player player, boolean resetScroll)`.
- Required player fields or save entry:
  - Task Master stores active/completed task state through `TaskMaster.loadAllMoneyMaking(Player player)` and `TaskMaster.saveAllMoneyMaking(Player player)`.
- Reward/economy risk: Medium because task rewards include boxes and lamps.
- Testing checklist:
  - Generate tasks and confirm the new task can appear in the intended category.
  - Complete progress and confirm completion rewards once.
  - Relog and confirm current task state persists.
  - Confirm daily or weekly flags do not make the task appear in the wrong pool.
- Example search terms future agents should use before coding:
  - `enum Tasks`
  - `TaskMasterKills.incrementAmountKilled`
  - `TaskMaster.trackActivity`
  - `generateTasks`

## 15. Adding Collection Log Support

- Best existing file to copy: `src/io/xeros/content/collection_log/CollectionLog.java`
- Files usually edited:
  - `src/io/xeros/content/collection_log/CollectionLog.java`
  - `src/io/xeros/content/collection_log/CollectionRewards.java`
  - Drop or chest reward code that calls `handleDrop`.
  - Runtime collection NPC config loaded by `CollectionLog.init()`, if the NPC category list must change.
- Files to avoid:
  - Adding collection tracking directly to unrelated packet handlers.
  - Rewriting drop table rare logic that already calls collection log hooks.
- Required method/hook:
  - Use `player.getCollectionLog().handleDrop(Player player, int npcId, int dropId, int dropAmount)`.
  - Use the overload with `boolean message` when copying an existing special chest pattern.
  - Collection rewards are defined in `CollectionRewards`.
- Required player fields or save entry:
  - Collection log JSON is stored by `CollectionLog`, not by `PlayerSaveEntry`.
  - Player fields include current viewed collection log and selected UI state.
- Reward/economy risk: Medium. Completion rewards can include high-value boxes, upgrade points, or rare items.
- Testing checklist:
  - Get the target drop and confirm it appears in the right log.
  - Complete the log and claim the reward once.
  - Relog and confirm the collection remains.
  - Confirm rare upgrade or AOE categories use existing special category ids where applicable.
- Example search terms future agents should use before coding:
  - `handleDrop(player`
  - `CollectionRewards`
  - `collectionNPCS`
  - `getForNpcID`

## 16. Adding Battlepass Progress

- Best existing file to copy: `src/io/xeros/content/battlepass/Pass.java`
- Files usually edited:
  - `src/io/xeros/content/battlepass/Pass.java`
  - `src/io/xeros/content/battlepass/Rewards.java`
  - `src/io/xeros/content/battlepass/RewardList.java`
  - The activity file that grants battlepass XP.
- Files to avoid:
  - Player save core.
  - Granting battlepass XP in dangerous or restricted areas without checking existing guards.
- Required method/hook:
  - Grant progress with `Pass.addExperience(Player c, int exp)`.
  - Login and season rollover use `Pass.handleLogin(Player player)`.
  - Rewards are granted by `Pass.grantRewards(Player player)`.
- Required player fields or save entry:
  - Existing fields include `player.tier`, `player.xp`, `player.member`, and `player.currentSeason`.
  - These are legacy save fields handled by existing save code.
- Reward/economy risk: Medium. Battlepass rewards can include strong consumables or boxes across many players.
- Testing checklist:
  - Grant XP from a safe activity and verify tier progress.
  - Test in restricted areas where `Pass.addExperience(...)` should return.
  - Confirm rewards go to inventory or bank as existing code expects.
  - Relog and confirm tier and XP persist.
- Example search terms future agents should use before coding:
  - `Pass.addExperience`
  - `Pass.handleLogin`
  - `Rewards.generateRewards`
  - `RewardList`

## 17. Adding Daily Or Weekly Progression

- Best existing file to copy: `src/io/xeros/content/dailyrewards/DailyRewards.java`
- Files usually edited:
  - `src/io/xeros/content/dailyrewards/DailyRewards.java`
  - `src/io/xeros/content/dailyrewards/DailyRewardContainer.java`
  - `src/io/xeros/content/dailyrewards/DailyRewardsPlayerSaveEntry.java`
  - `src/io/xeros/content/event/eventcalendar/EventCalendar.java`
  - `src/io/xeros/content/event/eventcalendar/EventChallenge.java`
  - `src/io/xeros/content/taskmaster/Tasks.java`
- Files to avoid:
  - `src/io/xeros/model/entity/player/save/PlayerSave.java` for new cooldowns or streaks.
  - Adding new login grants without cooldown and account checks.
- Required method/hook:
  - Daily rewards use `DailyRewards.claim()` and `DailyRewards.onLogin()`.
  - Event calendar progress uses `player.getEventCalendar().progress(EventChallenge eventChallenge, int amount)`.
  - Task Master daily-style tasks use `Tasks` entries with the daily flag and Task Master progress methods.
- Required player fields or save entry:
  - Existing daily rewards save keys are in `DailyRewardsPlayerSaveEntry`.
  - Event calendar and Task Master use their own existing persistence paths.
- Reward/economy risk: Medium. Daily systems create predictable reward faucets.
- Testing checklist:
  - Claim once, then try to claim again before cooldown.
  - Relog and confirm cooldown or progress persists.
  - Confirm account restrictions are respected.
  - Confirm reward amounts are safe for daily repetition.
- Example search terms future agents should use before coding:
  - `DailyRewards.claim`
  - `DailyRewardsPlayerSaveEntry`
  - `EventChallenge`
  - `getEventCalendar().progress`

## 18. Adding A Shop Or Shop Behavior

- Best existing file to copy: `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`
- Files usually edited:
  - `src/io/xeros/model/shops/ShopAssistant.java`
  - `src/io/xeros/model/world/ShopHandler.java`
  - `src/io/xeros/model/definitions/ShopDef.java`
  - The NPC, object, command, or dialogue that opens the shop.
  - Runtime shop definition data, if adding ordinary stock.
- Files to avoid:
  - `ShopAssistant` for ordinary stock-only changes.
  - Hardcoding prices in shop code when `ShopDef` or existing price helpers can handle it.
- Required method/hook:
  - Open a shop with `player.getShops().openShop(int ShopID)`.
  - Runtime definitions are loaded through `ShopDef.load()`.
  - Dynamic display shops can use `ShopHandler.addShopAnywhere(String name, List<ShopItem> items)`.
  - Custom buy/sell behavior belongs in `ShopAssistant` only when the currency or behavior is genuinely special.
- Required player fields or save entry: Depends on currency. Existing fields include `player.pkp`, `player.bossPoints`, `player.foundryPoints`, and minigame-specific points.
- Reward/economy risk: High. Shops directly define item faucets and sinks.
- Testing checklist:
  - Open the shop from every intended entry point.
  - Check price display and actual buy price.
  - Check sell behavior if selling is allowed.
  - Test ironman and restricted mode access.
- Example search terms future agents should use before coding:
  - `openShop(`
  - `ShopHandler.addShopAnywhere`
  - `ShopDef.load`
  - `getBuyFromShopPrice`
- Not found in repo:
  - Complete repo-local shop stock data. Searched terms: `ShopDef.load`, `shops.yaml`, `shop definitions`, `ShopItems`.

## 19. Adding An Upgrade Recipe

- Best existing file to copy: `src/io/xeros/content/upgrade/UpgradeMaterials.java`
- Files usually edited:
  - `src/io/xeros/content/upgrade/UpgradeMaterials.java`
  - `src/io/xeros/content/upgrade/UpgradeInterface.java`
- Files to avoid:
  - `src/io/xeros/model/entity/player/packets/ClickingButtons.java` for normal recipes.
  - `src/io/xeros/model/entity/player/packets/ContainerAction1.java` for normal recipes.
  - `src/io/xeros/model/entity/player/packets/ClickObject.java` unless adding a new upgrade station.
- Required method/hook:
  - Add an `UpgradeMaterials` enum entry under the correct `UpgradeType`.
  - Let `UpgradeInterface` render, validate, roll success, consume costs, give rewards, grant Fortune XP, update achievements, update collection log, and record activity boss progress.
- Required player fields or save entry:
  - Existing `player.foundryPoints`.
  - Existing Fortune skill XP.
  - No new save entry for ordinary recipes.
- Reward/economy risk: High. Upgrade recipes are major item sinks and power progression.
- Testing checklist:
  - Open the upgrade interface and find the recipe.
  - Try without requirements.
  - Try with exact requirements.
  - Confirm success, failure, rare broadcast, collection log, achievement, Fortune XP, and activity-boss progress.
- Example search terms future agents should use before coding:
  - `UpgradeMaterials`
  - `UpgradeType`
  - `handleUpgrade`
  - `GlobalBossActivityManager.record(ActivityType.UPGRADE_ITEM`

## 20. Adding A Fusion Recipe

- Best existing file to copy: `src/io/xeros/content/fusion/FusionMaterials.java`
- Files usually edited:
  - `src/io/xeros/content/fusion/FusionMaterials.java`
  - `src/io/xeros/content/fusion/FusionSystem.java`
  - `src/io/xeros/content/fusion/FusionTypes.java` only if adding a new category.
- Files to avoid:
  - Packet handlers for normal recipes.
  - Player save core.
- Required method/hook:
  - Add a `FusionMaterials` enum entry with type, level requirement, required items, reward, Platinum Token cost, XP, and rare flag.
  - Existing UI and execution flow uses `FusionSystem.openInterface(FusionTypes type)`, `FusionSystem.handleItemAction(int slot)`, and `FusionSystem.handleFusion()`.
- Required player fields or save entry:
  - Existing `player.foundryPoints` display is shown in the interface.
  - Existing Demon Hunter/Fortune skill checks and XP are used by the system.
  - No new save entry for ordinary recipes.
- Reward/economy risk: High. Fusion consumes several materials and can create rare gear.
- Testing checklist:
  - Open the fusion interface from the existing station or command.
  - Select the recipe and confirm all required item icons and amounts render.
  - Try without items, without cost, and without level.
  - Complete a rare fusion and verify broadcast and XP.
- Example search terms future agents should use before coding:
  - `FusionMaterials`
  - `FusionSystem.handleFusion`
  - `FusionTypes`
  - `openInterface(FusionTypes`

## 21. Adding Fire Of Exchange Burn Value

- Best existing file to copy: `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`
- Files usually edited:
  - `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`
  - `src/io/xeros/content/fireofexchange/FireOfExchange.java`
- Files to avoid:
  - `src/io/xeros/model/shops/ShopAssistant.java` unless changing FOE display shop behavior.
  - Direct item deletion outside `FireOfExchange.exchangeItemForPoints(Player c)`.
- Required method/hook:
  - Add the burn price to the price source used by `FireOfExchangeBurnPrice.getBurnPrice(Player c, int itemId, boolean displayMessage)`.
  - Existing burn execution runs through `FireOfExchange.exchangeItemForPoints(Player c)`.
  - FOE also updates achievements, event calendar, leaderboards, total earned exchange points, activity boss progress, and burn history.
- Required player fields or save entry:
  - Existing `player.foundryPoints`.
  - Existing `player.totalEarnedExchangePoints`.
  - Existing recent dissolve history fields.
- Reward/economy risk: High. FOE is a major item sink and point faucet.
- Testing checklist:
  - Check the displayed burn value.
  - Burn one item and confirm points, Fortune XP, achievements, event calendar, leaderboard, and activity boss progress.
  - Confirm blocked items cannot be burned.
  - Confirm noted and unnoted values match expectations.
- Example search terms future agents should use before coding:
  - `getBurnPrice`
  - `exchangeItemForPoints`
  - `TOTAL_POINTS_EXCHANGED`
  - `ActivityType.FOE_BURN`

## 22. Adding Wraith Charge Behavior

- Best existing file to copy: `src/io/xeros/content/wraith/WraithCharges.java`
- Files usually edited:
  - `src/io/xeros/content/wraith/WraithCharges.java`
  - `src/io/xeros/content/items/UseItem.java`
  - `src/io/xeros/content/commands/all/Wraith.java`
  - `src/io/xeros/content/combat/core/AttackEntity.java`
- Files to avoid:
  - Player save core for existing Wraith charge fields.
  - Combat core for anything that can stay inside `WraithCharges`.
- Required method/hook:
  - Use `WraithCharges.isWraithWeapon(int itemId)` to identify weapons.
  - Add charges with `WraithCharges.addChargesFromEssence(Player p, int wraithItemSlot, int essenceItemId, int requestedEss)`.
  - Consume charges with `WraithCharges.consumeCharge(Player p, int itemId)` from the existing combat hook.
  - Adjust caps through `WraithCharges.getCapFor(int itemId)` and essence conversion through `WraithCharges.chargesPerEssence()`.
- Required player fields or save entry:
  - Existing Wraith charge player fields accessed through getters and setters in `Player`.
  - Existing legacy save keys for those fields.
- Reward/economy risk: Medium to High. Wraith Essence and charged weapon use are endgame item-sink loops.
- Testing checklist:
  - Charge each Wraith weapon from inventory and equipped slot if supported.
  - Confirm cap behavior and partial essence consumption.
  - Attack with a charged weapon and verify charge consumption.
  - Relog and confirm charge count persists.
- Example search terms future agents should use before coding:
  - `WraithCharges`
  - `addChargesFromEssence`
  - `consumeCharge`
  - `isWraithWeapon`

## 23. Adding AOE Tier Rewards

- Best existing file to copy: `data/aoe/aoe_tier_rewards.json`
- Files usually edited:
  - `data/aoe/aoe_tier_rewards.json`
  - `src/io/xeros/content/instances/aoe/AoeTierRewardsDef.java`
  - `src/io/xeros/content/instances/aoe/AoeTierRewardsLoader.java`
  - `src/io/xeros/content/instances/aoe/AoeTierController.java`
  - `src/io/xeros/content/instances/aoe/AoeDropInterceptor.java`
  - `src/io/xeros/content/instances/aoe/AoeTierEvents.java`
- Files to avoid:
  - NPC death core unless adding a shared AOE hook.
  - Drop table core for tier-only reward changes.
- Required method/hook:
  - JSON-only supported fields are `tier`, `name`, `endOfRunRolls`, `bonusRewards`, `bankAllDrops`, `blacklist`, `whitelist`, `fortuneXpPerKill`, and `reportTitle`.
  - End-of-run item rewards use `bonusRewards` objects with `itemId`, `min`, and `max`.
  - Per-kill Fortune/Demon Hunter style XP uses `AoeTierEvents.onNpcDeath(Player player, NPC npc)`.
  - Banked drops use `AoeDropInterceptor.awardInsideAoe(Player player, GameItem item)`.
- Required player fields or save entry:
  - Runtime active tier and tracker attributes from `AoeTierController`.
  - Persistent tier progress in `AoeTierProgressSaveEntry`.
- Reward/economy risk: Medium for coins or small materials. High for rare gear, Wraith Essence, upgrade points, or new currency rewards that require Java support.
- Testing checklist:
  - Reload or restart so `AoeTierRewardsLoader.load()` reads the JSON.
  - Run tiers 1 through 3.
  - Confirm banked drops, blacklist, whitelist, Fortune XP, and report title.
  - Confirm end-of-run rewards do not duplicate when leaving.
- Example search terms future agents should use before coding:
  - `aoe_tier_rewards`
  - `AoeTierRewardsDef`
  - `bonusRewards`
  - `AoeDropInterceptor`

## 24. Adding AOE Tier Unlock Or Progression

- Best existing file to copy: `data/aoe/aoe_boss_tiers.json`
- Files usually edited:
  - `data/aoe/aoe_boss_tiers.json`
  - `data/aoe/AoeZoneMapConfig.json`
  - `src/io/xeros/content/instances/aoe/AoeBossTierDef.java`
  - `src/io/xeros/content/instances/aoe/AoeBossTierLoader.java`
  - `src/io/xeros/content/instances/aoe/AoeTierController.java`
  - `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`
  - `src/io/xeros/content/dialogue/impl/BossInstanceDialogue.java`
- Files to avoid:
  - Player save core.
  - Instance service internals for simple tier data changes.
- Required method/hook:
  - Define tier data in `data/aoe/aoe_boss_tiers.json`.
  - Start tiers through `AoeTierController.startTier(Player player, int tier)`.
  - Count kills through `AoeTierController.incrementKill(Player player, int tier)`.
  - Unlock tiers with `AoeTierController.setUnlockedTier(Player player, int tier)` only from controlled progression or admin tools.
- Required player fields or save entry:
  - Save keys `aoe_unlocked_tier` and `aoe_kc_` are handled by `AoeTierProgressSaveEntry`.
- Reward/economy risk: Medium. Progression pacing controls access to AOE rewards and bosses.
- Testing checklist:
  - Start from a fresh player and confirm tier 1 is available.
  - Kill enough tier bosses to unlock tier 2.
  - Relog and confirm unlock and kill count persist.
  - Confirm locked tiers cannot be started from the dialogue.
- Example search terms future agents should use before coding:
  - `AoeTierController.startTier`
  - `AoeTierController.incrementKill`
  - `AoeTierProgressSaveEntry`
  - `aoe_boss_tiers`

## 25. Adding A World Event

- Best existing file to copy: `src/io/xeros/content/worldevent/impl/HesporiWorldEvent.java`
- Files usually edited:
  - `src/io/xeros/content/worldevent/WorldEvent.java`
  - `src/io/xeros/content/worldevent/WorldEventContainer.java`
  - `src/io/xeros/content/worldevent/impl/`
  - A teleport command under `src/io/xeros/content/commands/all/`
- Files to avoid:
  - Event scheduling internals for one event.
  - Reward logic in `WorldEventContainer` unless the reward is truly global to all events.
- Required method/hook:
  - Implement `WorldEvent`.
  - Provide `init()`, `dispose()`, `isEventCompleted()`, `getCurrentStatus()`, `getEventName()`, `getStartDescription()`, `getTeleportCommand()`, and `announce(List<Player> players)`.
  - Add the event to `WorldEventContainer.WORLD_EVENT_LIST`.
- Required player fields or save entry:
  - None for basic events.
  - Use a new `PlayerSaveEntry` only for personal contribution, cooldown, or milestone data.
- Reward/economy risk: Medium to High. World events can create burst rewards and daily login pressure.
- Testing checklist:
  - Trigger the event with the existing owner/admin world-event command pattern.
  - Confirm announcement, teleport command, spawn, completion, and disposal.
  - Confirm event status appears in quest tab or status surfaces.
  - Confirm rewards are granted once per eligible participant.
- Example search terms future agents should use before coding:
  - `implements WorldEvent`
  - `WORLD_EVENT_LIST`
  - `getTeleportCommand`
  - `startEvent`

## 26. Adding An Activity Or Global Boss Trigger

- Best existing file to copy: `src/io/xeros/content/activityboss/GlobalBossActivityManager.java`
- Files usually edited:
  - `src/io/xeros/content/activityboss/ActivityType.java`
  - `src/io/xeros/content/activityboss/GlobalBossType.java`
  - `src/io/xeros/content/activityboss/GlobalBossActivityManager.java`
  - `src/io/xeros/content/activityboss/GlobalBossLootTable.java`
  - The content file that records activity.
- Files to avoid:
  - NPC death reward code for activity accumulation.
  - Spawning code outside the global boss manager for normal activity bosses.
- Required method/hook:
  - Add or reuse an `ActivityType`.
  - Add a `GlobalBossType` with NPC id, name, activity type, threshold, spawn position, and combat type.
  - Record progress with `GlobalBossActivityManager.record(ActivityType type, int amount)`.
  - Death rewards use `GlobalBossActivityManager.onBossDeath(NPC npc, Player killer)` and `GlobalBossDropHandler.rewardParticipants(NPC boss)`.
- Required player fields or save entry:
  - Activity totals are static runtime state in `GlobalBossActivityManager`.
  - Contribution is read from NPC damage data through `GlobalBossContributionTracker`.
- Reward/economy risk: High. Global bosses grant multi-player rewards and can be triggered by common actions.
- Testing checklist:
  - Record progress below threshold and confirm progress announcements.
  - Hit threshold and confirm one boss spawns.
  - Confirm cooldown prevents immediate respawn.
  - Kill the boss with multiple contributors and confirm rewards.
- Example search terms future agents should use before coding:
  - `GlobalBossActivityManager.record`
  - `ActivityType`
  - `GlobalBossType`
  - `GlobalBossDropHandler.rewardParticipants`

## 27. Adding Player Save Data With PlayerSaveEntry

- Best existing file to copy: `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`
- Files usually edited:
  - `src/io/xeros/model/entity/player/save/PlayerSaveEntry.java`
  - `src/io/xeros/model/entity/player/save/impl/`
  - A content package save entry such as `src/io/xeros/content/dailyrewards/DailyRewardsPlayerSaveEntry.java`
  - The owning content class or player field accessors.
- Files to avoid:
  - `src/io/xeros/model/entity/player/save/PlayerSave.java` unless modifying an old legacy save key.
  - Renaming existing save keys.
- Required method/hook:
  - Implement `PlayerSaveEntry`.
  - Provide `getKeys(Player player)`, `decode(Player player, String key, String value)`, `encode(Player player, String key)`, and `login(Player player)`.
  - Reflection registration is handled by `PlayerSave.loadPlayerSaveEntries()`.
- Required player fields or save entry:
  - Use unique stable keys.
  - Store runtime values on the owning content class, player attributes, or typed player fields.
- Reward/economy risk: Low for counters and unlocks. Medium or High for saved currencies, claimed rewards, or cooldown bypass prevention.
- Testing checklist:
  - Create a fresh save and confirm missing keys default safely.
  - Change the value, logout, relog, and confirm it persists.
  - Confirm malformed values do not crash loading.
  - Confirm key names do not overlap existing save entries.
- Example search terms future agents should use before coding:
  - `implements PlayerSaveEntry`
  - `loadPlayerSaveEntries`
  - `getKeys(Player player)`
  - `AoeTierProgressSaveEntry`

## 28. Adding A Minigame Reward Chest

- Best existing file to copy: `src/io/xeros/content/item/lootable/impl/KonarChest.java`
- Files usually edited:
  - `src/io/xeros/content/item/lootable/Lootable.java`
  - `src/io/xeros/content/item/lootable/LootRarity.java`
  - `src/io/xeros/content/item/lootable/impl/`
  - `src/io/xeros/model/entity/player/packets/objectoptions/ObjectOptionOne.java`
  - The minigame completion file, if rewards are claimed after completion.
- Files to avoid:
  - Drop table core for chest-only rewards.
  - Object packet core for logic that belongs in the chest class.
- Required method/hook:
  - Implement `Lootable`.
  - Provide `getLoot()` and `roll(Player player)`.
  - Use existing static reward helpers like `RaidsChestCommon.randomChestRewards()` or `RaidsChestRare.randomChestRewards()` when copying raid-style reward logic.
  - Hook the chest from an object option or minigame completion flow.
- Required player fields or save entry:
  - Key item, completion count, or reward eligibility fields depend on the minigame.
  - Use `PlayerSaveEntry` if adding a new persistent claim state.
- Reward/economy risk: High. Chests are repeated reward faucets and often include rares.
- Testing checklist:
  - Try opening without the key or completion state.
  - Open with exactly one key or completion state and confirm it is consumed.
  - Confirm common and rare rewards roll and broadcast as intended.
  - Confirm collection log hooks fire for rare rewards if required.
- Example search terms future agents should use before coding:
  - `implements Lootable`
  - `randomChestRewards`
  - `new KonarChest().roll`
  - `RaidsChestRare`

## 29. Adding A Leaderboard-Style System

- Best existing file to copy: `src/io/xeros/content/leaderboards/LeaderboardUtils.java`
- Files usually edited:
  - `src/io/xeros/content/leaderboards/LeaderboardType.java`
  - `src/io/xeros/content/leaderboards/LeaderboardUtils.java`
  - `src/io/xeros/content/leaderboards/LeaderboardInterface.java`
  - The content file that records counts.
- Files to avoid:
  - Database SQL classes unless adding a new storage shape.
  - Leaderboard UI constants unless the existing list capacity or layout is changing.
- Required method/hook:
  - Add a `LeaderboardType`.
  - Record progress with `LeaderboardUtils.addCount(LeaderboardType type, Player player, int amount)`.
  - Open the UI through `LeaderboardInterface.openInterface(Player player)`.
  - Button handling uses `LeaderboardInterface.handleButtons(Player player, int button)`.
- Required player fields or save entry:
  - Counts are stored through the database manager, not player save.
  - UI remembers the last viewed leaderboard in player attributes.
- Reward/economy risk: Medium if rewards are attached. Low if tracking only.
- Testing checklist:
  - Trigger the activity and confirm the database add call runs without errors.
  - Refresh leaderboard data and confirm the type appears.
  - Confirm daily or weekly reward definitions exist if rewards are expected.
  - Open the interface and select the new leaderboard.
- Example search terms future agents should use before coding:
  - `LeaderboardUtils.addCount`
  - `LeaderboardType`
  - `LeaderboardInterface.openInterface`
  - `MOST_DISSOLVED`

## 30. Adding Discord Or Global Announcements If Supported

- Best existing file to copy: `src/io/xeros/content/worldevent/impl/HesporiWorldEvent.java`
- Files usually edited:
  - `src/io/xeros/model/entity/player/broadcasts/Broadcast.java`
  - `src/io/xeros/model/entity/player/PlayerHandler.java`
  - `src/io/xeros/util/discord/Discord.java`
  - The content file that triggers the announcement.
- Files to avoid:
  - Hardcoding new Discord channel ids in content classes.
  - Calling raw JDA APIs directly when an existing `Discord.write...Message` helper fits.
  - Broadcasting common rewards too often.
- Required method/hook:
  - Global in-game message: `PlayerHandler.executeGlobalMessage(String message)`.
  - Rich teleport broadcast: `new Broadcast(String message).addTeleport(Position position).copyMessageToChatbox().submit()`.
  - Discord helper examples include `Discord.writeBugMessage(...)`, `Discord.writeSuggestionMessage(...)`, and `Discord.writeServerSyncMessage(...)`.
- Required player fields or save entry: None unless announcements are tied to cooldowns or milestones.
- Reward/economy risk: Low for announcements only. Medium if announcements imply teleports into reward content. High if spammy rare-drop messages reduce perceived value.
- Testing checklist:
  - Trigger the announcement once.
  - Confirm message formatting in chat.
  - Confirm teleport broadcasts point to the correct location.
  - Confirm Discord calls are guarded by existing helper behavior and do not crash local debug runs.
- Example search terms future agents should use before coding:
  - `executeGlobalMessage`
  - `new Broadcast`
  - `copyMessageToChatbox`
  - `Discord.writeBugMessage`

## Cross-System Checklist Before Coding

- Search for an existing matching system before editing.
- Pick the closest existing file and copy its shape.
- Prefer data, enum, manager, or small content-class changes.
- Use `PlayerSaveEntry` for new persistent data.
- Use `DialogueBuilder` for new dialogue.
- Use command subclasses instead of command dispatcher edits.
- Hook achievements, collection logs, battlepass, Task Master, boss points, and save data only when the content design needs them.
- Keep reward amounts conservative and check `docs/TURMOIL_REWARD_ECONOMY_AUDIT.md` before adding currency or gear.
- Avoid rewriting `NPCProcess`, `NPCHandler`, `DropManager`, `ShopAssistant`, `PlayerSave`, or packet cores unless the requested task is specifically about those systems.
- After implementation, explain changed files and test steps.
