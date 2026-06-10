# Turmoil Server Method Flow Map

This map documents common server actions from player input to reward or output. It uses the existing content docs as context and source methods as the source of truth:

- `docs/TURMOIL_CONTENT_GUIDE.md`
- `docs/TURMOIL_CONTENT_INDEX.md`
- `docs/TURMOIL_PROGRESSION_AUDIT.md`

Rules for future content:

- Do not rewrite packet, combat, save, or drop cores for one content addition.
- Prefer existing manager methods and content classes.
- Prefer `PlayerSaveEntry` for new save data.
- Prefer `DialogueBuilder` for new dialogue.
- Prefer command subclasses under `src/io/xeros/content/commands/`.
- Treat runtime data loaded by managers as external when the data file is not in repo.

## 1. Player Login Flow

- Main files:
  - `src/io/xeros/net/login/RS2LoginProtocol.java`
  - `src/io/xeros/model/entity/player/PlayerHandler.java`
  - `src/io/xeros/model/entity/player/Player.java`
  - `src/io/xeros/model/entity/player/save/PlayerSave.java`
  - `src/io/xeros/model/entity/player/save/PlayerSaveEntry.java`
- Entry point method:
  - `RS2LoginProtocol.loadPlayer(Player player, String name, LoginReturnCode returnCode, boolean passedCaptcha)`
- Important method calls in order:
  1. `PlayerSave.loadGame(player, player.getLoginName(), player.playerPass, passedCaptcha)`
  2. `player.getCollectionLog().loadForPlayer(player)`
  3. `PlayerHandler.addLoginQueue(Player player)`
  4. `PlayerHandler.processLoginQueue()`
  5. `playerLoggingIn.finishLogin()`
  6. `Player.finishLogin()`
  7. `getTaskMaster().loadAllMoneyMaking(this)`
  8. `getNpcDeathTracker().normalise()`
  9. `loadController()`
  10. `getController().onLogin(this)`
  11. `getAchievements().onLogin()`
  12. `Pass.handleLogin(this)`
  13. tutorial branch if `!completedTutorial`
  14. `getDailyRewards().onLogin()`
  15. `PlayerSave.login(this)`
  16. `correctCoordinates()`
  17. `BossPoints.doRefund(this)`
- What player fields are read or changed:
  - Login identity, password hash, rights, mode, `completedTutorial`, `initialized`, `isActive`, `saveCharacter`.
  - Runtime state such as controller, clan, achievement state, daily rewards, battlepass tier, and boss point refund flag.
- What save entries are involved:
  - Legacy save parsing in `PlayerSave.loadGame`.
  - Modular save entries loaded by `PlayerSave.loadPlayerSaveEntries()` and called by `PlayerSave.login(Player player)`.
  - Collection log JSON is loaded separately by `CollectionLog.loadForPlayer`.
- What reward systems are called:
  - `Pass.handleLogin`
  - `DailyRewards.onLogin`
  - `BossPoints.doRefund`
  - Achievement login cleanup through `AchievementHandler.onLogin`
- Existing examples to copy:
  - `src/io/xeros/content/dailyrewards/DailyRewardsPlayerSaveEntry.java`
  - `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`
  - `src/io/xeros/model/entity/player/save/impl/AutocastPlayerSaveEntry.java`
- Safe extension points:
  - Add a new `PlayerSaveEntry` for new persistent content.
  - Use `PlayerSaveEntry.login(Player player)` for small login-time initialization.
  - Use `Player.finishLogin()` only when adding a broad login-visible system that must run for all players.
- Dangerous areas to avoid:
  - Do not add new save keys directly to `PlayerSave.java` unless maintaining an existing legacy key.
  - Do not bypass `PlayerHandler.processLoginQueue()`.
  - Do not add rewards directly to login unless the reward is cooldown-gated elsewhere.

## 2. New Player Starter Flow

- Main files:
  - `src/io/xeros/model/entity/player/Player.java`
  - `src/io/xeros/content/tutorial/TutorialDialogue.java`
  - `src/io/xeros/content/tutorial/ModeSelection.java`
  - `src/io/xeros/content/items/Starter.java`
  - `src/io/xeros/model/entity/player/save/PlayerSave.java`
- Entry point method:
  - `Player.finishLogin()`
- Important method calls in order:
  1. `Player.finishLogin()` checks `!completedTutorial`.
  2. `start(new DialogueBuilder(this).option(...))` asks whether to skip tutorial.
  3. `new TutorialDialogue(this, false, false)` or `new TutorialDialogue(this, false, true)` starts the tutorial flow.
  4. `TutorialDialogue.selectedMode(Player player, ModeType modeType)` is called from `ModeSelection`.
  5. `TutorialDialogue.finish(Player player, ExpModeType mode)` finishes mode setup.
  6. `Starter.addStarter(Player c)`
  7. `Starter.addStarterItems(Player c)`
  8. `Starter.standardStarter(Player c)` or `Starter.testingStarter(Player player)`
  9. `player.setCompletedTutorial(true)`
- What player fields are read or changed:
  - `completedTutorial`, `receivedStarter`, mode, exp mode, rights, `dropWarning`, starter tracking through server attributes.
  - Starter items are added to inventory or bank tabs.
  - A starter Slayer task can be assigned through `c.getSlayer().createNewTask(Slayer.EASY_TASK_NPC_ID, false)`.
- What save entries are involved:
  - `completed-tutorial` and `received-starter` style fields are legacy `PlayerSave.java` fields.
  - Server-wide starter MAC tracking is stored by `Server.getServerAttributes().write()`.
- What reward systems are called:
  - Starter bank/inventory grants through `ItemAssistant`.
  - Starter Slayer task assignment through `Slayer.createNewTask`.
- Existing examples to copy:
  - `src/io/xeros/content/tutorial/TutorialDialogue.java`
  - `src/io/xeros/content/items/Starter.java`
  - `src/io/xeros/content/tutorial/ModeSelection.java`
- Safe extension points:
  - Add starter progression through `Starter.addStarterItems` when changing starter kits.
  - Add tutorial dialogue through `TutorialDialogue` or `DialogueBuilder`.
  - Add starter tasks through existing Slayer task creation.
- Dangerous areas to avoid:
  - Do not grant starter items from login without `receivedStarter` checks.
  - Do not create a second starter path in packet option handlers.
  - Do not break old `completedTutorial` saves.

## 3. Command Handling Flow

- Main files:
  - `src/io/xeros/model/entity/player/packets/Commands.java`
  - `src/io/xeros/content/commands/Command.java`
  - `src/io/xeros/content/commands/CommandManager.java`
  - `src/io/xeros/content/commands/all/`
  - `src/io/xeros/content/commands/admin/`
  - `src/io/xeros/content/commands/moderator/`
  - `src/io/xeros/content/commands/donator/`
  - `src/io/xeros/content/commands/test/`
- Entry point method:
  - `Commands.processPacket(Player c, int packetType, int packetSize)`
- Important method calls in order:
  1. `Commands.processPacket(...)` reads the command string.
  2. Movement lock, interface event, bank pin, stuck, and multiplayer session guards run.
  3. `Server.getLogging().write(new CommandLog(...))`
  4. Legacy staff/debug switch branches may return early.
  5. Clan chat slash commands are handled before normal commands.
  6. `CommandManager.execute(c, playerCommand)`
  7. `CommandManager.executeCommand(Player player, String playerCommand)`
  8. `Misc.findCommand(playerCommand)` and `Misc.findInput(playerCommand)`
  9. `COMMAND_MAP.get(commandName)`
  10. `CMD.hasPrivilege(player)`
  11. `CMD.execute(player, commandName, commandInput)`
- What player fields are read or changed:
  - Rights, movement lock, bank pin state, interface state, trade/duel session state, clan state.
  - Individual commands mutate their own target fields.
- What save entries are involved:
  - None in the command dispatcher itself.
  - Individual commands may call systems that save later through `PlayerSave`.
- What reward systems are called:
  - Command-dependent. Example: `src/io/xeros/content/commands/all/Voted.java` calls vote rewards and achievement hooks.
- Existing examples to copy:
  - `src/io/xeros/content/commands/all/Bossinstance.java`
  - `src/io/xeros/content/commands/all/Leaveaoe.java`
  - `src/io/xeros/content/commands/all/Voted.java`
  - `src/io/xeros/content/commands/test/DropTest.java`
- Safe extension points:
  - Add a subclass of `Command` in the correct rank package.
  - Override `execute(Player player, String commandName, String input)`.
  - Override `hasPrivilege(Player player)`.
  - Override `getDescription()` and `getFormat()` when useful.
- Dangerous areas to avoid:
  - Do not add normal commands to `Commands.processPacket`.
  - Do not put player commands in admin or owner packages.
  - Do not make test commands available outside debug/test mode.

## 4. Dialogue Handling Flow

- Main files:
  - `src/io/xeros/content/dialogue/DialogueBuilder.java`
  - `src/io/xeros/content/dialogue/DialogueOption.java`
  - `src/io/xeros/content/dialogue/impl/`
  - `src/io/xeros/model/entity/player/Player.java`
  - `src/io/xeros/model/entity/player/packets/Dialogue.java`
  - `src/io/xeros/model/entity/player/DialogueHandler.java`
  - `src/io/xeros/model/entity/player/packets/ClickingButtons.java`
  - `src/io/xeros/model/entity/player/packets/dialogueoptions/`
- Entry point method:
  - `Player.start(DialogueBuilder dialogueBuilder)`
- Important method calls in order:
  1. Content calls `player.start(new DialogueBuilder(player)...)` or starts a `DialogueBuilder` subclass.
  2. `Player.start(DialogueBuilder dialogueBuilder)` stores the builder and calls `dialogueBuilder.initialise()`.
  3. Continue packet calls `Dialogue.processPacket(Player c, int packetType, int packetSize)`.
  4. Modern flow calls `c.getDialogueBuilder().getCurrent().sendNextDialogue()` when continuable.
  5. Legacy flow calls `c.getDH().sendDialogues(c.nextChat, c.talkingNpc)`.
  6. Option buttons enter `ClickingButtons.dialogueOption(Player c, int buttonId)`.
  7. Modern options call `DialogueBuilder` actions such as `handleAction` or `dispatchAction`.
  8. Legacy options route to `TwoOptions`, `ThreeOptions`, `FourOptions`, `FiveOptions`, and `OptionHandler`.
- What player fields are read or changed:
  - `dialogueBuilder`, `lastDialogueNewSystem`, `dialogueAction`, `nextChat`, `talkingNpc`, `clickedNpcIndex`, `npcType`.
- What save entries are involved:
  - None directly.
  - Dialogue choices may call systems that save later.
- What reward systems are called:
  - Dialogue-dependent. Examples include starter completion, Fire of Exchange confirmation, Slayer task selection, and daily reward dialogue.
- Existing examples to copy:
  - `src/io/xeros/content/dialogue/impl/BossInstanceDialogue.java`
  - `src/io/xeros/content/tutorial/TutorialDialogue.java`
  - `src/io/xeros/content/dailyrewards/DailyRewardsDialogue.java`
  - `src/io/xeros/content/skills/slayer/DemonHunterSlayerDialogue.java`
- Safe extension points:
  - Use `DialogueBuilder` and `DialogueOption` for new content.
  - Put reusable trees under `src/io/xeros/content/dialogue/impl/`.
  - Use `player.start(...)` from NPC/object/item handlers.
- Dangerous areas to avoid:
  - Do not add large new trees to `DialogueHandler`.
  - Do not add new `dialogueAction` IDs unless extending an existing legacy flow.
  - Do not duplicate rewards in both modern and legacy option handlers.

## 5. NPC First-Click / Second-Click / Third-Click Handling

- Main files:
  - `src/io/xeros/model/entity/player/packets/ClickNPC.java`
  - `src/io/xeros/model/entity/player/ActionHandler.java`
  - `src/io/xeros/model/entity/player/packets/npcoptions/NpcOptionOne.java`
  - `src/io/xeros/model/entity/player/packets/npcoptions/NpcOptionTwo.java`
  - `src/io/xeros/model/entity/player/packets/npcoptions/NpcOptionThree.java`
  - `src/io/xeros/model/entity/player/packets/npcoptions/NpcOptions.java`
  - `src/io/xeros/model/entity/npc/NPCAction.java`
- Entry point method:
  - `ClickNPC.processPacket(Player c, int packetType, int packetSize)`
- Important method calls in order:
  1. `ClickNPC.processPacket(...)` reads NPC index and option packet.
  2. It sets click fields such as `npcClickIndex` and faces/follows the NPC.
  3. Distance and cycle-event checks complete.
  4. First option calls `c.getActions().firstClickNpc(n)`.
  5. Second option calls `c.getActions().secondClickNpc(NPCHandler.npcs[c.npcClickIndex])`.
  6. Third option calls `c.getActions().thirdClickNpc(NPCHandler.npcs[c.npcClickIndex])`.
  7. `ActionHandler.firstClickNpc(NPC npc)` calls `NpcOptionOne.handleOption(c, npc.getNpcId())`, then `NpcOptions.handle(c, npc, 1)`.
  8. `NpcOptionOne`, `NpcOptionTwo`, and `NpcOptionThree` check `npc.actions[]` and `npcDef.defaultActions[]` before legacy switches.
  9. Shared fallback logic runs in `NpcOptions.handle(Player player, NPC npc, int option)`.
- What player fields are read or changed:
  - `npcClickIndex`, `clickedNpcIndex`, `npcType`, `talkingNpc`, click delays, face/follow state, dialogue fields.
- What save entries are involved:
  - None directly.
- What reward systems are called:
  - NPC-dependent. Examples include shops, daily rewards, BossInstanceDialogue, Fire of Exchange display, Slayer task assignment, and minigame entry.
- Existing examples to copy:
  - `src/io/xeros/model/entity/player/packets/npcoptions/NpcOptionOne.java` for NPC `6599` and `Npcs.INSTANCE_MASTER` opening `BossInstanceDialogue`.
  - `src/io/xeros/model/entity/player/packets/npcoptions/NpcOptionOne.java` for NPC `10529` opening the Fire of Exchange interface.
  - `src/io/xeros/content/dialogue/impl/BossInstanceDialogue.java`.
- Safe extension points:
  - Prefer `NPCAction` or NPC definition actions where available.
  - Use a small NPC option branch only when matching existing legacy style.
  - Start modern dialogue with `player.start(new DialogueBuilder(...))`.
- Dangerous areas to avoid:
  - Do not put combat mechanics in NPC click handlers.
  - Do not mutate unrelated click fields after starting dialogue.
  - Do not add broad logic to `ClickNPC.processPacket`.

## 6. Object Click Handling

- Main files:
  - `src/io/xeros/model/entity/player/packets/ClickObject.java`
  - `src/io/xeros/model/entity/player/ActionHandler.java`
  - `src/io/xeros/model/entity/player/packets/objectoptions/ObjectOptionOne.java`
  - `src/io/xeros/model/entity/player/packets/objectoptions/ObjectOptionTwo.java`
  - `src/io/xeros/model/entity/player/packets/objectoptions/ObjectOptionThree.java`
  - `src/io/xeros/model/entity/player/packets/objectoptions/ObjectOptionFour.java`
  - `src/io/xeros/content/instances/InstancedArea.java`
- Entry point method:
  - `ClickObject.processPacket(Player c, int packetType, int packetSize)`
- Important method calls in order:
  1. `ClickObject.processPacket(...)` guards movement, bank pin, interface state, duels, fping, and teleports.
  2. It reads object id, x, y, and option.
  3. `ClickObject.walkTo(Player player, int option)` schedules a `WalkToTickable`.
  4. `ClickObject.finishObjectClick(Player c, int option, WorldObject worldObject)` runs after pathing.
  5. Instance hooks run first, especially `c.getInstance().handleClickObject(c, worldObject, option)`.
  6. Other content hooks run, including raid containers, quests, and special object systems.
  7. Option one flows to local object switch or `c.getActions().firstClickObject(...)`.
  8. Option two flows to `c.getActions().secondClickObject(...)`.
  9. Option three flows to `c.getActions().thirdClickObject(...)`.
  10. `ActionHandler` routes to `ObjectOptionOne`, `ObjectOptionTwo`, `ObjectOptionThree`, or `ObjectOptionFour`.
- What player fields are read or changed:
  - `objectId`, `objectX`, `objectY`, `objectDistance`, `objectXOffset`, `objectYOffset`, open interfaces, walk target, skilling state.
- What save entries are involved:
  - None directly.
- What reward systems are called:
  - Object-dependent. Examples include upgrade interface open, fusion interface open, chests, farming, raids, and Fire of Exchange rate display.
- Existing examples to copy:
  - `src/io/xeros/model/entity/player/packets/ClickObject.java` for object `30943` opening the upgrade interface.
  - `src/io/xeros/model/entity/player/packets/ClickObject.java` for object `30944` opening fusion.
  - `src/io/xeros/content/instances/InstancedArea.java` for instance object interception.
- Safe extension points:
  - For instanced content, override `InstancedArea.handleClickObject(Player player, WorldObject object, int option)`.
  - For global objects, use the nearest `ObjectOption*` handler or object definition actions.
  - Keep object-specific content in a small manager where possible.
- Dangerous areas to avoid:
  - Do not bypass `walkTo` distance handling for ordinary objects.
  - Do not add isolated minigame object behavior high in `ClickObject.processPacket`.
  - Do not skip instance object hooks.

## 7. Item Click Handling

- Main files:
  - `src/io/xeros/model/entity/player/packets/itemoptions/ItemOptionOne.java`
  - `src/io/xeros/model/entity/player/packets/itemoptions/ItemOptionTwo.java`
  - `src/io/xeros/model/entity/player/packets/itemoptions/ItemOptionThree.java`
  - `src/io/xeros/model/entity/player/packets/itemoptions/ItemOptionFour.java`
  - `src/io/xeros/model/items/ItemAction.java`
  - `src/io/xeros/content/items/UseItem.java`
- Entry point method:
  - `ItemOptionOne.processPacket(Player c, int packetType, int packetSize)`
- Important method calls in order:
  1. Item option packet reads item id, slot, and interface id.
  2. Guards movement lock, interface state, invalid slots, and item mismatch.
  3. `ItemDef.forId(itemId).inventoryActions` is checked.
  4. Registered `ItemAction` runs first when present.
  5. Legacy item id switch handles older content.
  6. Option two, three, and four use parallel packet classes for their option number.
- What player fields are read or changed:
  - Inventory arrays, selected item slot, dialogue fields, teleport fields, item charges, timers, and content-specific fields.
- What save entries are involved:
  - Item-specific. Examples include Wraith charge legacy keys and battlepass membership fields.
- What reward systems are called:
  - Item-dependent. Examples include battlepass membership, boxes, scrolls, lamps, Slayer unlock items, and Wraith Essence dialogue.
- Existing examples to copy:
  - `src/io/xeros/model/items/ItemAction.java`
  - `src/io/xeros/model/entity/player/packets/itemoptions/ItemOptionOne.java`
  - `src/io/xeros/content/items/UseItem.java`
- Safe extension points:
  - Prefer `ItemAction.registerInventory(int itemId, int option, ItemAction action)` for new item actions.
  - Put reusable item behavior in a content manager and call it from the item option handler.
- Dangerous areas to avoid:
  - Do not add broad item category logic to packet classes.
  - Do not bypass inventory slot validation.
  - Do not duplicate item rewards across option handlers.

## 8. Item-On-Item Handling

- Main files:
  - `src/io/xeros/model/entity/player/packets/ItemOnItem.java`
  - `src/io/xeros/content/items/UseItem.java`
  - `src/io/xeros/content/items/ItemCombinations.java`
  - `src/io/xeros/content/wraith/WraithCharges.java`
- Entry point method:
  - `ItemOnItem.processPacket(Player c, int packetType, int packetSize)`
- Important method calls in order:
  1. `ItemOnItem.processPacket(...)` guards movement, fping, interface state, bank pin, and duels.
  2. It reads both item ids and slots.
  3. It validates both items are still in inventory.
  4. `UseItem.ItemonItem(c, itemUsed, useWith, itemUsedSlot, usedWithSlot)`
  5. `ItemCombinations.getCombinations(...)` handles modern combinables first.
  6. Manager hooks run for special systems.
  7. Wraith Essence on a Wraith weapon calls `WraithCharges.addChargesFromEssence(...)`.
  8. Legacy item combinations run after manager hooks.
- What player fields are read or changed:
  - Inventory arrays, current combination state, `dialogueAction`, `nextChat`, Wraith charge fields.
- What save entries are involved:
  - Wraith charge legacy keys in `PlayerSave.java`: `wraith-scythe-charge`, `wraith-staff-charge`, `wraith-bow-charge`.
- What reward systems are called:
  - Combination-dependent. Wraith charging consumes Wraith Essence and updates charge state.
- Existing examples to copy:
  - `src/io/xeros/content/items/ItemCombinations.java`
  - `src/io/xeros/content/wraith/WraithCharges.java`
  - `src/io/xeros/content/items/UseItem.java`
- Safe extension points:
  - Prefer `ItemCombinations` for normal recipe-style item pairs.
  - Add a small manager hook for a new family of item interactions.
- Dangerous areas to avoid:
  - Do not put new normal item recipes deep in the legacy switch if `ItemCombinations` can handle them.
  - Do not consume items before all validation passes.
  - Do not bypass Wraith charge caps.

## 9. Item-On-Object Handling

- Main files:
  - `src/io/xeros/model/entity/player/packets/ItemOnObject.java`
  - `src/io/xeros/content/items/UseItem.java`
  - `src/io/xeros/content/fireofexchange/FireOfExchange.java`
  - `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`
- Entry point method:
  - `ItemOnObject.processPacket(Player c, int packetType, int packetSize)`
- Important method calls in order:
  1. `ItemOnObject.processPacket(...)` guards movement, fping, interface state, bank pin, and duels.
  2. It reads object id, object coordinates, item id, and item slot.
  3. It validates the world object and inventory item.
  4. It schedules a `WalkToTickable`.
  5. Farming receives an early `c.getFarming().handleItemOnObject(...)` hook.
  6. `UseItem.ItemonObject(c, objectId, objectX, objectY, itemId)`
  7. Object-specific managers or legacy switch cases run.
- What player fields are read or changed:
  - `objectId`, `objectX`, `objectY`, inventory, farming state, `currentExchangeItem` for FOE flows when selected elsewhere.
- What save entries are involved:
  - None directly.
- What reward systems are called:
  - Object-dependent. Examples include chests, skilling, tool augments, WOGW donation, and Fire of Destruction dialogue.
- Existing examples to copy:
  - `src/io/xeros/content/items/UseItem.java`
  - `src/io/xeros/model/entity/player/packets/ItemOnObject.java`
- Safe extension points:
  - Add object-specific item behavior to `UseItem.ItemonObject` or a manager called from it.
  - Keep instance-specific item/object behavior inside instance code when applicable.
- Dangerous areas to avoid:
  - Do not bypass object existence validation.
  - Do not duplicate FOE burn confirmation here; FOE confirmation uses container/dialogue flow.

## 10. Button / Interface Action Handling

- Main files:
  - `src/io/xeros/model/entity/player/packets/ClickingButtons.java`
  - `src/io/xeros/model/entity/player/packets/ClickingButtonsNew.java`
  - `src/io/xeros/content/teleportv2/inter/TeleportInterface.java`
  - `src/io/xeros/content/upgrade/UpgradeInterface.java`
  - `src/io/xeros/content/collection_log/CollectionLog.java`
  - `src/io/xeros/content/collection_log/CollectionRewards.java`
  - `src/io/xeros/content/vote_panel/VotePanelInterface.java`
- Entry point method:
  - `ClickingButtons.processPacket(Player c, int packetType, int packetSize)`
- Important method calls in order:
  1. `ClickingButtons.processPacket(...)` reads `actionButtonId` and `realButtonId`.
  2. It logs `ClickButtonLog` and `ReceivedPacketLog`.
  3. Dead, fping, and tutorial checks run.
  4. Early interface handlers run, including deals, trade post, YouTube manager, coin flip, prestige perks, quest interface, wheel, achievements, mode selection, ref manager, Slayer interface, and collection rewards.
  5. Movement lock guard runs.
  6. Main content handlers run, including `VotePanelInterface.handleActionButton`, `c.getCollectionLog().handleActionButtons`, `TeleportInterface.handleButton`, `c.getUpgradeInterface().handleButton`, `c.getFusionSystem().handleButton`, and death interface.
  7. Legacy button switch handles older interfaces.
  8. `ClickingButtonsNew.processPacket(Player c, int packetType, int packetSize)` handles newer button ids and calls daily rewards, event calendar, questing, achievements, diary, party, and wild warning.
- What player fields are read or changed:
  - Open interface ids, movement lock, `dialogueAction`, tutorial state, many content-specific fields.
- What save entries are involved:
  - Interface-dependent. Daily rewards use `DailyRewardsPlayerSaveEntry`; achievements and collection rewards use legacy and JSON state.
- What reward systems are called:
  - `Achievements`, `CollectionRewards`, `DailyRewards.claim`, vote panel rewards, upgrade/fusion rewards, battlepass through achievement/collection claims.
- Existing examples to copy:
  - `src/io/xeros/model/entity/player/packets/ClickingButtons.java`
  - `src/io/xeros/model/entity/player/packets/ClickingButtonsNew.java`
  - `src/io/xeros/content/upgrade/UpgradeInterface.java`
  - `src/io/xeros/content/dailyrewards/DailyRewards.java`
- Safe extension points:
  - Add a button handler to the owning content class and call it from the central handler.
  - Return early after a content handler consumes a button.
  - Use `realButtonId` or `actionButtonId` consistently with nearby code.
- Dangerous areas to avoid:
  - Do not add large content logic directly inside `ClickingButtons`.
  - Do not let one interface consume another interface's button ids.
  - Do not skip movement and tutorial guards.

## 11. Shop Opening and Shop Buying Flow

- Main files:
  - `src/io/xeros/model/world/ShopHandler.java`
  - `src/io/xeros/model/shops/ShopAssistant.java`
  - `src/io/xeros/model/entity/player/packets/ContainerAction1.java`
  - `src/io/xeros/model/entity/player/packets/ContainerAction2.java`
  - `src/io/xeros/model/entity/player/packets/ContainerAction3.java`
  - `src/io/xeros/model/entity/player/packets/ContainerAction4.java`
  - `src/io/xeros/model/entity/player/packets/ContainerAction5.java`
  - `src/io/xeros/model/definitions/ShopDef.java`
- Entry point method:
  - `ShopAssistant.openShop(int ShopID)`
- Important method calls in order:
  1. NPC/object/dialogue/command content calls `player.getShops().openShop(shopId)`.
  2. `ShopAssistant.openShop(int ShopID)` validates sessions, mode restrictions, and interface state.
  3. It sets `isShopping`, `myShopId`, scroll state, inventory interface, and shop interface.
  4. It calls `resetShop(ShopID)`.
  5. Buying packets call `ShopAssistant.buyItem(int itemID, int fromSlot, int amount)`.
  6. Selling packets call `ShopAssistant.sellItem(int itemID, int fromSlot, int amount)`.
  7. Currency-specific branches spend points or items according to `myShopId`.
  8. Inventory and shop containers refresh.
- What player fields are read or changed:
  - `isShopping`, `myShopId`, inventory, coins, `votePoints`, `bossPoints`, `foundryPoints`, `pcPoints`, `pkp`, `donatorPoints`, Slayer points.
- What save entries are involved:
  - Most point fields are legacy keys in `PlayerSave.java`.
  - Slayer points are stored through legacy Slayer save keys.
- What reward systems are called:
  - Shop-dependent. Some shop purchases add collection log entries for AOE weapons or spend FOE/foundry points.
- Existing examples to copy:
  - `src/io/xeros/model/shops/ShopAssistant.java`
  - `src/io/xeros/model/world/ShopHandler.java`
  - `src/io/xeros/model/entity/player/packets/npcoptions/NpcOptionOne.java` shop-opening examples.
- Safe extension points:
  - Add shop stock through the existing shop definition flow.
  - Use `player.getShops().openShop(shopId)` from NPC/object/dialogue handlers.
  - Add custom currency branches only when the existing currency branches cannot represent the shop.
- Dangerous areas to avoid:
  - Do not bypass `ShopAssistant.buyItem` or `sellItem`.
  - Do not add high-value rewards to shops without checking currency inflation.
  - Shop stock data is external/runtime in this repo snapshot; do not invent a repo-local shop stock path.

## 12. Teleport Handling Flow

- Main files:
  - `src/io/xeros/content/teleportv2/inter/TeleportInterface.java`
  - `src/io/xeros/model/entity/player/PlayerAssistant.java`
  - `src/io/xeros/model/entity/player/packets/ClickingButtons.java`
  - `src/io/xeros/content/wildwarning/WildWarning.java`
  - `src/io/xeros/content/instances/aoe/AoeTierController.java`
  - `src/io/xeros/content/instances/BossInstanceManager.java`
- Entry point method:
  - `TeleportInterface.handleButton(Player player, int buttonID)`
- Important method calls in order:
  1. Player opens teleport UI through `TeleportInterface.open(Player player)`.
  2. Button click enters `ClickingButtons.processPacket(...)`.
  3. `TeleportInterface.handleButton(player, realButtonId)` handles tabs, previous teleport, favorite, or teleport action.
  4. Specific handlers call `player.getPA().startTeleport(Position data, "modern", false)`.
  5. `PlayerAssistant.startTeleport(Position position, String teleportType, boolean homeTeleport)`
  6. `PlayerAssistant.startTeleport(int x, int y, int height, String teleportType, boolean homeTeleport)`
  7. Wilderness warning can wrap `startTeleportFinal`.
  8. `PlayerAssistant.startTeleportFinal(...)` runs teleport guards.
  9. If active AOE, `AoeTierController.endTier(player, true)` runs.
  10. Teleblock, fping, bank pin, skilling, combat, raids, and session guards run.
  11. Teleport target and timer fields are set.
  12. `PlayerAssistant.movePlayer(int x, int y, int h)` performs instant moves and calls `BossInstanceManager.leave(player)`.
- What player fields are read or changed:
  - `teleX`, `teleY`, `teleHeight`, `teleTimer`, `lastTeleportX`, `lastTeleportY`, `lastTeleportZ`, combat target, skilling state, open interfaces.
- What save entries are involved:
  - None directly.
- What reward systems are called:
  - None directly, but teleports can end AOE report flow and boss instances.
- Existing examples to copy:
  - `src/io/xeros/content/teleportv2/inter/TeleportInterface.java`
  - `src/io/xeros/content/commands/all/Home.java`
  - `src/io/xeros/content/commands/all/Foundry.java`
- Safe extension points:
  - Add teleport destinations through teleport interface data/handlers.
  - Use `player.getPA().startTeleport(...)` for normal player teleports.
  - Use `movePlayerUnconditionally` only for controlled instance entry.
- Dangerous areas to avoid:
  - Do not bypass teleblock or wild warning for public teleports.
  - Do not forget active instance cleanup.
  - Do not use instant move where a teleport should respect combat restrictions.

## 13. NPC Spawn Flow

- Main files:
  - `src/io/xeros/ServerStartup.java`
  - `src/io/xeros/model/entity/npc/NpcSpawnLoader.java`
  - `src/io/xeros/model/entity/npc/NpcSpawnLoaderOSRS.java`
  - `src/io/xeros/model/entity/npc/NPCHandler.java`
  - `src/io/xeros/model/entity/npc/NPCSpawning.java`
  - `src/io/xeros/content/instances/InstancedArea.java`
- Entry point method:
  - `NpcSpawnLoader.load()`
- Important method calls in order:
  1. `ServerStartup` calls `NpcSpawnLoader.load()`.
  2. `NpcSpawnLoader` reads configured spawn data and calls `NPCHandler.newNPC(...)`.
  3. `NpcSpawnLoaderOSRS.initOsrsSpawns()` reads configured OSRS spawn data and calls `NPCHandler.newNPC(...)`.
  4. `NPCHandler.newNPC(int npcType, int x, int y, int heightLevel, int WalkingType, int maxHit)`
  5. `NPCSpawning.newNPC(...)`
  6. Dynamic content can call `NPCSpawning.spawnNpc(...)`.
  7. Instance content can call `NPCSpawning.spawnNpc(InstancedArea instance, ...)`.
  8. Spawned NPCs are inserted into `NPCHandler.npcs`.
  9. NPC clipping and combat scripts are attached where present.
- What player fields are read or changed:
  - For player-owned spawns, `spawnedBy`, player instance membership, and target fields can be set.
- What save entries are involved:
  - None directly.
- What reward systems are called:
  - None at spawn time.
- Existing examples to copy:
  - `src/io/xeros/content/activityboss/GlobalBossActivityManager.java`
  - `src/io/xeros/content/instances/BossInstanceManager.java`
  - `src/io/xeros/content/instances/aoe/AoeNpcSpawner.java`
- Safe extension points:
  - Use `NPCSpawning.spawnNpc(...)` for dynamic content.
  - Use `NPCSpawning.spawnNpc(InstancedArea instance, ...)` for classic instances.
  - Let static spawns stay in configured spawn data.
- Dangerous areas to avoid:
  - Do not write directly into `NPCHandler.npcs` from content.
  - Do not spawn instance NPCs without adding them to the instance.
  - Static spawn data files are not repo-local in this snapshot. Searched terms: `npc_spawns`, `osrsspawns`, `spawns`.

## 14. NPC Combat Attack Flow

- Main files:
  - `src/io/xeros/model/entity/npc/NPCHandler.java`
  - `src/io/xeros/model/entity/npc/NPC.java`
  - `src/io/xeros/model/entity/npc/NPCProcess.java`
  - `src/io/xeros/model/entity/npc/actions/NPCHitPlayer.java`
  - `src/io/xeros/content/combat/npc/NPCAutoAttackBuilder.java`
  - `src/io/xeros/content/combat/npc/NPCAutoAttack.java`
  - `src/io/xeros/content/combat/npc/NPCCombatAttack.java`
- Entry point method:
  - `NPCHandler.process()`
- Important method calls in order:
  1. `NPCHandler.process()` loops active NPCs.
  2. `NPC.process()` calls `Server.npcHandler.getNpcProcess().process(getIndex())`.
  3. `NPCProcess.process(int i)` checks target, following, attack timers, and death state.
  4. If no auto attacks exist, legacy `npcHandler.attackPlayer(player, npc)` can run.
  5. If auto attacks exist, `npc.selectAutoAttack(player)` runs.
  6. `npc.attack(player, npc.getCurrentAttack())` runs.
  7. `NPC.attack(Player c, NPCAutoAttack npcAutoAttack)` validates distance, multi, height, clipping, and player state.
  8. The attack queues projectile, hit delay, `onAttack`, `onHit`, and damage.
  9. `NPCHitPlayer.applyAutoAttackDamage(NPC npc, Player c, NPCAutoAttack npcAutoAttack)` calculates and applies damage.
- What player fields are read or changed:
  - Prayer state, health, combat target, auto-retaliate, recoil/vengeance, defensive bonuses, damage taken.
- What save entries are involved:
  - None directly.
- What reward systems are called:
  - None during attack, except combat can consume charges or trigger defensive item effects.
- Existing examples to copy:
  - `src/io/xeros/content/bosses/obor/OborNPC.java`
  - `src/io/xeros/content/bosses/sarachnis/SarachnisNpc.java`
  - `src/io/xeros/content/bosses/bryophyta/BryophytaNPC.java`
  - `src/io/xeros/content/combat/npc/NPCAutoAttackBuilder.java`
- Safe extension points:
  - Define boss attacks with `NPCAutoAttackBuilder`.
  - Use `setOnAttack`, `setOnHit`, `setSelectAutoAttack`, and damage modifiers for mechanics.
  - Put boss-specific attack behavior in the boss content class.
- Dangerous areas to avoid:
  - Do not add isolated boss behavior to `NPCProcess`.
  - Do not change `NPCHitPlayer` for one boss.
  - Do not bypass clipping, multi, or attack timer checks.

## 15. NPC Death Flow

- Main files:
  - `src/io/xeros/model/entity/npc/NPCProcess.java`
  - `src/io/xeros/content/combat/death/NPCDeath.java`
  - `src/io/xeros/model/entity/npc/NPC.java`
  - `src/io/xeros/content/instances/aoe/AoeNpcSpawner.java`
  - `src/io/xeros/content/skills/slayer/Slayer.java`
  - `src/io/xeros/content/skills/slayer/DemonHunterTaskManager.java`
- Entry point method:
  - `NPCProcess.process(int i)`
- Important method calls in order:
  1. NPC health reaches zero and death state starts in `NPCProcess.process`.
  2. Death animation and action timer run.
  3. `AoeNpcSpawner.onNpcDeath(npc)` handles AOE spawn bookkeeping.
  4. Legacy special boss death handlers may run for global or custom bosses.
  5. Ordinary deaths call `NPCDeath.dropItems(npc)`.
  6. `npc.onDeath()` runs.
  7. Slayer partner and player Slayer progress calls run.
  8. `DemonHunterTaskManager.handleKill(player, npc)` runs.
  9. Task Master kill matching runs.
  10. Boss kill count, diaries, quest hooks, raids, and respawn cleanup run.
- What player fields are read or changed:
  - Damage contribution, target player, Slayer task state, Demon Hunter task state, Task Master kill counters, NPC death tracker, boss KC, instance state.
- What save entries are involved:
  - Slayer legacy keys in `PlayerSave.java`.
  - Task Master runtime JSON through `TaskMaster.saveAllMoneyMaking`.
  - Demon Hunter save entries: Not found in repo. Searched terms: `demonHunter`, `demon-hunter`, `demonTask`, `demon-task`, `demonMarks`, `demon-marks`, `demonContract`, `DemonSlayer`.
- What reward systems are called:
  - Drops, boss points, achievements, Task Master, Slayer, Demon Hunter, battlepass, collection logs, diaries, quests, global boss activity.
- Existing examples to copy:
  - `src/io/xeros/content/combat/death/NPCDeath.java`
  - `src/io/xeros/model/entity/npc/NPCProcess.java`
  - `src/io/xeros/content/bosses/CorporealBeast.java`
  - `src/io/xeros/content/globalboss/KBD.java`
- Safe extension points:
  - Let normal NPC deaths flow through `NPCDeath.dropItems`.
  - Add unique death behavior in a small boss class or existing special death manager.
  - Hook rewards in `NPCDeath.dropItemsFor` when the reward applies to ordinary NPC deaths.
- Dangerous areas to avoid:
  - Do not add ordinary drops to `NPCProcess`.
  - Do not bypass `NPCDeath.dropItemsFor` for normal bosses.
  - Do not add broad post-death hooks without checking duplicate rewards.

## 16. Drop Reward Flow

- Main files:
  - `src/io/xeros/content/combat/death/NPCDeath.java`
  - `src/io/xeros/model/entity/npc/drops/DropManager.java`
  - `src/io/xeros/model/entity/npc/drops/TableGroup.java`
  - `src/io/xeros/model/entity/npc/drops/Table.java`
  - `src/io/xeros/model/entity/npc/drops/Drop.java`
  - `src/io/xeros/content/instances/aoe/AoeDropInterceptor.java`
  - `src/io/xeros/content/collection_log/CollectionLog.java`
- Entry point method:
  - `NPCDeath.dropItemsFor(NPC npc, Player player, int npcId)`
- Important method calls in order:
  1. `NPCDeath.dropItems(NPC npc)` resolves the killer.
  2. `NPCDeath.dropItemsFor(NPC npc, Player player, int npcId)` runs death reward hooks.
  3. `AoeTierEvents.onNpcDeath(player, npc)` runs for active AOE tiers.
  4. `player.getAchievements().kill(npc)` runs.
  5. `GlobalBossActivityManager.onBossDeath(npc, player)` runs.
  6. Pet, diary, event, point, and instance hooks run.
  7. `BossPoints.getPointsOnDeath(npc)` and `BossPoints.addPoints(player, bossPoints, false)` run.
  8. `player.getNpcDeathTracker().add(...)` records KC.
  9. `Server.getDropManager().create(player, npc, location, amountOfDrops, npcId)` rolls drops.
  10. `TableGroup.access(...)` rolls drop tables.
  11. `DropManager.onDrop(player, item, npcId)` transforms special drops.
  12. `Server.itemHandler.createGroundItem(...)` creates visible loot unless intercepted.
- What player fields are read or changed:
  - Drop rate modifiers, inventory/bank, collection log state, boss points, NPC death tracker, foundry points for upgrade point certificates.
- What save entries are involved:
  - Boss points and NPC death tracker legacy state.
  - Collection log runtime JSON.
  - AOE tier progression through `AoeTierProgressSaveEntry`.
- What reward systems are called:
  - Drop tables, collection log, achievements, boss points, pets, AOE reward tracker, ground item handler.
- Existing examples to copy:
  - `src/io/xeros/model/entity/npc/drops/TableGroup.java`
  - `src/io/xeros/model/entity/npc/drops/DropManager.java`
  - `src/io/xeros/content/commands/test/DropTest.java`
- Safe extension points:
  - Add ordinary NPC drops through drop table data.
  - Use `DropManager.onDrop` only for shared drop transformation behavior.
  - Let `TableGroup.access` handle rare collection log and announcements.
- Dangerous areas to avoid:
  - Do not hardcode ordinary drops in `NPCDeath` or `DropManager`.
  - Do not call `CollectionLog.handleDrop` twice for the same rare drop.
  - Drop table data files are not repo-local in this snapshot. Searched terms: `drop`, `drops`, `npc_id`, `TablePolicy`.

## 17. Boss Point Reward Flow

- Main files:
  - `src/io/xeros/content/bosspoints/BossPoints.java`
  - `src/io/xeros/content/combat/death/NPCDeath.java`
  - `src/io/xeros/content/combat/stats/NPCDeathTracker.java`
  - `src/io/xeros/model/entity/player/save/PlayerSave.java`
  - `src/io/xeros/model/shops/ShopAssistant.java`
- Entry point method:
  - `BossPoints.getPointsOnDeath(NPC npc)`
- Important method calls in order:
  1. `NPCDeath.dropItemsFor(...)` calls `BossPoints.getPointsOnDeath(npc)`.
  2. `BossPoints.getPoints(...)` searches configured boss point entries.
  3. `BossPoints.addPoints(Player player, int points, boolean message)` adds points.
  4. It optionally doubles points from active Hespori seed effects.
  5. `player.bossPoints += points`.
  6. `player.getPA().addSkillXPMultiplied(...)` grants Demon Hunter XP.
  7. Quest tab, event calendar, and leaderboard counters update.
  8. `BossPoints.doRefund(Player player)` may run on login for old refunds.
- What player fields are read or changed:
  - `bossPoints`, `bossPointsRefund`, Demon Hunter skill XP, event calendar progress.
- What save entries are involved:
  - Legacy keys in `PlayerSave.java`: `bossPoints`, `bossPointsRefund`.
- What reward systems are called:
  - Demon Hunter skill XP, event calendar `GAIN_X_BOSS_POINTS`, leaderboard `BOSS_POINTS`.
- Existing examples to copy:
  - `src/io/xeros/content/combat/death/NPCDeath.java`
  - `src/io/xeros/content/bosspoints/BossPoints.java`
  - `src/io/xeros/model/shops/ShopAssistant.java` for boss point shop spending.
- Safe extension points:
  - Add or adjust configured boss point entries outside code when possible.
  - Use `BossPoints.addManualPoints(Player player, String name)` for non-NPC completions that already have manual entries.
- Dangerous areas to avoid:
  - Do not mutate `player.bossPoints` directly from new content when `BossPoints.addPoints` fits.
  - Do not award boss points from high-volume trash NPCs.
  - Boss point runtime config is not repo-local in this snapshot. Searched terms: `boss_points`, `boss-points`, `BossPointEntry`.

## 18. Achievement Progress Flow

- Main files:
  - `src/io/xeros/content/achievement/Achievements.java`
  - `src/io/xeros/content/achievement/AchievementHandler.java`
  - `src/io/xeros/content/achievement/AchievementType.java`
  - `src/io/xeros/content/combat/death/NPCDeath.java`
  - `src/io/xeros/model/entity/player/save/PlayerSave.java`
- Entry point method:
  - `Achievements.increase(Player player, AchievementType type, int amount)`
- Important method calls in order:
  1. Real content event calls `Achievements.increase(...)`.
  2. Bonus amount modifiers can apply.
  3. Matching `Achievements.Achievement` entries are found by type.
  4. `player.getAchievements().amountRemaining` is incremented.
  5. Completion marks `completed`, clamps progress, and adds achievement points.
  6. `Achievements.updateProgress(Player player, AchievementType type)` refreshes UI.
  7. Player claims rewards through `AchievementHandler.clickButton(int buttonId)` or `claimAll`.
  8. Claiming can call `Pass.addExperience`.
- What player fields are read or changed:
  - `AchievementHandler.amountRemaining`, `completed`, `claimed`, `points`.
- What save entries are involved:
  - Legacy achievement sections and `achievement-points` in `PlayerSave.java`.
- What reward systems are called:
  - Achievement items, achievement points, battlepass XP, task interface updates.
- Existing examples to copy:
  - `src/io/xeros/content/dailyrewards/DailyRewards.java` calls `Achievements.increase(player, AchievementType.DAILY, 1)`.
  - `src/io/xeros/content/fireofexchange/FireOfExchange.java` calls `Achievements.increase(c, AchievementType.FOE_POINTS, amount)`.
  - `src/io/xeros/content/collection_log/CollectionLog.java` calls `Achievements.increase(player, AchievementType.COLLECTOR, 1)`.
- Safe extension points:
  - Add new `Achievements.Achievement` enum entries when an existing type fits.
  - Add new `AchievementType` only when necessary.
  - Call `Achievements.increase` at the real completion point.
- Dangerous areas to avoid:
  - Do not mutate achievement arrays directly.
  - Do not add achievement increments in drop preview/test flows.
  - Do not add high-volume passive increments without caps.

## 19. Task Master Progress Flow

- Main files:
  - `src/io/xeros/content/taskmaster/Tasks.java`
  - `src/io/xeros/content/taskmaster/TaskMaster.java`
  - `src/io/xeros/content/taskmaster/TaskMasterKills.java`
  - `src/io/xeros/model/entity/npc/NPCProcess.java`
  - `src/io/xeros/model/entity/player/Player.java`
- Entry point method:
  - `TaskMasterKills.incrementAmountKilled(int amountKilled)`
- Important method calls in order:
  1. `Player.finishLogin()` calls `getTaskMaster().loadAllMoneyMaking(this)`.
  2. NPC death handling in `NPCProcess` loops `target.getTaskMaster().taskMasterKillsList`.
  3. It matches task descriptions against killed NPC names or special cases.
  4. `TaskMasterKills.incrementAmountKilled(1)` increments progress.
  5. `TaskMaster.trackActivity(Player player, TaskMasterKills kills)` checks percent completion.
  6. `TaskMaster.finishTask(Player player, TaskMasterKills kills)` rewards and marks claimed.
  7. Logout calls `getTaskMaster().saveAllMoneyMaking(this)`.
- What player fields are read or changed:
  - `Player.getTaskMaster().taskMasterKillsList`, task amount killed, claimed state, task timer fields.
- What save entries are involved:
  - Task Master uses its own runtime JSON through `TaskMaster.loadAllMoneyMaking` and `TaskMaster.saveAllMoneyMaking`.
- What reward systems are called:
  - Task reward boxes/lamps, Fortune XP, task messages.
- Existing examples to copy:
  - `src/io/xeros/content/taskmaster/Tasks.java`
  - `src/io/xeros/model/entity/npc/NPCProcess.java` kill-matching loop.
  - `src/io/xeros/content/taskmaster/TaskMaster.java`
- Safe extension points:
  - Add simple kill tasks to `Tasks` when matching by NPC name is enough.
  - For non-kill tasks, add one small hook at the real completion event and call the same `TaskMaster` tracking style.
- Dangerous areas to avoid:
  - Do not rewrite Task Master persistence.
  - Do not rely on fragile text matching for complex content.
  - Do not grant rewards without setting claimed/completed state.

## 20. Collection Log Progress Flow

- Main files:
  - `src/io/xeros/content/collection_log/CollectionLog.java`
  - `src/io/xeros/content/collection_log/CollectionRewards.java`
  - `src/io/xeros/model/entity/npc/drops/TableGroup.java`
  - `src/io/xeros/model/entity/npc/drops/DropManager.java`
  - `src/io/xeros/model/entity/player/save/PlayerSave.java`
- Entry point method:
  - `CollectionLog.handleDrop(Player player, int npcId, int dropId, int dropAmount)`
- Important method calls in order:
  1. Rare NPC drops call `player.getCollectionLog().handleDrop(...)` from `TableGroup.access`.
  2. Direct reward systems can call the same method for chest, upgrade, pet, or AOE weapon rewards.
  3. `CollectionLog.handleDrop(...)` normalizes linked group ironman collection logs.
  4. It checks whether the category/NPC is tracked.
  5. It updates `collections`.
  6. It sends unlock messages and calls `Achievements.increase(player, AchievementType.COLLECTOR, 1)`.
  7. It checks completion and calls `saveToJSON()`.
  8. Reward claims run through `CollectionRewards.handleButton(Player player, int ID)`.
- What player fields are read or changed:
  - `collectionLog`, `viewingCollectionLog`, `collectionLogNPC`, `collectionLogTab`, `claimedLog`, `collectionPoints`.
- What save entries are involved:
  - Collection entries are saved as runtime JSON by `CollectionLog`.
  - Claimed rewards are legacy save state in `PlayerSave.java`.
- What reward systems are called:
  - Collection rewards, achievement collector progress, battlepass XP from reward claims.
- Existing examples to copy:
  - `src/io/xeros/model/entity/npc/drops/TableGroup.java`
  - `src/io/xeros/content/upgrade/UpgradeInterface.java`
  - `src/io/xeros/content/collection_log/CollectionRewards.java`
- Safe extension points:
  - For normal boss drops, let `TableGroup` handle rare collection logging.
  - For non-drop unique rewards, call `CollectionLog.handleDrop` once when the item is actually granted.
  - Add completion rewards through `CollectionRewards` after economy review.
- Dangerous areas to avoid:
  - Do not edit collection maps directly.
  - Do not duplicate collection log calls for the same reward path.
  - Collection category data is runtime-loaded; do not invent repo-local category files.

## 21. Battlepass Progress Flow

- Main files:
  - `src/io/xeros/content/battlepass/Pass.java`
  - `src/io/xeros/content/battlepass/Rewards.java`
  - `src/io/xeros/content/battlepass/RewardList.java`
  - `src/io/xeros/model/entity/player/save/PlayerSave.java`
  - `src/io/xeros/model/entity/player/Player.java`
- Entry point method:
  - `Pass.addExperience(Player c, int exp)`
- Important method calls in order:
  1. Login calls `Pass.handleLogin(Player player)`.
  2. Content events call `Pass.addExperience(Player c, int exp)`.
  3. Boundary, season, and cap checks run.
  4. `c.xp` is incremented.
  5. If XP crosses a tier threshold, `Pass.levelUp(Player player)` runs.
  6. `Pass.grantRewards(Player player)` grants free/member tier rewards.
  7. `Rewards.generateRewards()` and related methods manage season reward lists.
- What player fields are read or changed:
  - `tier`, `xp`, `member`, `currentSeason`.
- What save entries are involved:
  - Legacy keys in `PlayerSave.java`: `division-tier`, `division-xp`, `division-member`, `division-season`.
- What reward systems are called:
  - Battlepass default/member rewards from `Rewards` and `RewardList`.
- Existing examples to copy:
  - `src/io/xeros/content/achievement/AchievementHandler.java`
  - `src/io/xeros/content/collection_log/CollectionRewards.java`
  - `src/io/xeros/content/skills/slayer/Slayer.java`
  - `src/io/xeros/model/entity/npc/NPCProcess.java`
- Safe extension points:
  - Call `Pass.addExperience` from meaningful completion events.
  - Keep pass XP small and capped by activity value.
- Dangerous areas to avoid:
  - Do not mutate `Player.xp` or `Player.tier` directly.
  - Do not add pass XP to AFK or extremely high-volume loops.
  - Do not bypass season guards.

## 22. Player Save / Load Flow

- Main files:
  - `src/io/xeros/model/entity/player/save/PlayerSave.java`
  - `src/io/xeros/model/entity/player/save/PlayerSaveEntry.java`
  - `src/io/xeros/model/entity/player/PlayerSaveExecutor.java`
  - `src/io/xeros/model/entity/player/save/impl/`
  - `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`
  - `src/io/xeros/content/dailyrewards/DailyRewardsPlayerSaveEntry.java`
- Entry point method:
  - `PlayerSave.loadGame(Player p, String playerName, String playerPass, boolean passedCaptcha)`
- Important method calls in order:
  1. Startup calls `PlayerSave.loadPlayerSaveEntries()`.
  2. Login calls `PlayerSave.loadGame(...)`.
  3. `loadGame` opens the character save text file.
  4. Legacy token parsing fills existing fields.
  5. For modular keys, `PlayerSaveEntry.decode(Player player, String key, String value)` is called.
  6. Login completion calls `PlayerSave.login(Player player)`.
  7. Each entry receives `PlayerSaveEntry.login(Player player)`.
  8. Save requests call `PlayerSave.saveGame(Player p)`.
  9. `PlayerSaveExecutor` performs queued save.
  10. `PlayerSave.saveGameInstant(Player p)` writes legacy fields and modular entry keys.
- What player fields are read or changed:
  - Nearly all persistent player fields, including inventory, equipment, points, tutorial state, Slayer, achievements, battlepass, Wraith charges.
- What save entries are involved:
  - Any class implementing `PlayerSaveEntry`.
  - Existing examples include daily rewards and AOE tier progress.
- What reward systems are called:
  - Save/load itself should not grant rewards, except login hooks that restore or notify systems.
- Existing examples to copy:
  - `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`
  - `src/io/xeros/content/dailyrewards/DailyRewardsPlayerSaveEntry.java`
  - `src/io/xeros/model/entity/player/save/impl/AttackStyleSaveEntry.java`
- Safe extension points:
  - Add a new `PlayerSaveEntry` for new small persistent values.
  - Keep key names stable.
  - Decode bad/missing values safely.
- Dangerous areas to avoid:
  - Do not rename existing save keys.
  - Do not expand `PlayerSave.java` for new content unless modifying an old legacy field.
  - Do not write large structured data into character text saves.

## 23. World Event Flow

- Main files:
  - `src/io/xeros/ServerStartup.java`
  - `src/io/xeros/content/worldevent/WorldEvent.java`
  - `src/io/xeros/content/worldevent/WorldEventContainer.java`
  - `src/io/xeros/content/worldevent/WorldEventState.java`
  - `src/io/xeros/content/worldevent/WorldEventInformation.java`
  - `src/io/xeros/content/worldevent/impl/HesporiWorldEvent.java`
  - `src/io/xeros/content/worldevent/impl/WildernessBossWorldEvent.java`
  - `src/io/xeros/content/worldevent/impl/TournamentWorldEvent.java`
- Entry point method:
  - `WorldEventContainer.initialise()`
- Important method calls in order:
  1. `ServerStartup` calls `WorldEventContainer.getInstance().initialise()`.
  2. `WorldEventState.load()` restores event index and ticks.
  3. `WorldEventContainer.scheduleNext()` registers a cycle event.
  4. The cycle decrements ticks until ready.
  5. `WorldEventContainer.next()` cancels current event and selects the next event.
  6. `worldEvent.init()` starts the event.
  7. `worldEvent.announce(PlayerHandler.getPlayers())` broadcasts.
  8. Quest tab information updates.
  9. `WorldEventContainer.cancelCurrent()` calls `dispose()` on active event when needed.
  10. Staff commands can call `WorldEventContainer.startEvent(WorldEvent event)` or `setTriggerImmediateEvent(true)`.
- What player fields are read or changed:
  - Usually none directly in the container.
  - Event implementations can move players, open lobbies, spawn bosses, or grant rewards.
- What save entries are involved:
  - `WorldEventState` persists event state through runtime storage, not player save.
- What reward systems are called:
  - Event-dependent. Hespori, wilderness boss, and tournament rewards live in their own systems.
- Existing examples to copy:
  - `src/io/xeros/content/worldevent/impl/HesporiWorldEvent.java`
  - `src/io/xeros/content/worldevent/impl/WildernessBossWorldEvent.java`
  - `src/io/xeros/content/worldevent/impl/TournamentWorldEvent.java`
- Safe extension points:
  - Implement `WorldEvent`.
  - Add it to `WorldEventContainer.WORLD_EVENT_LIST`.
  - Keep start, announce, completion check, and dispose logic self-contained.
- Dangerous areas to avoid:
  - Do not grant event rewards from `WorldEventContainer.next()`.
  - Do not leave spawned NPCs or lobbies alive in `dispose()`.
  - Do not bypass event completion checks.

## 24. Activity / Global Boss Flow

- Main files:
  - `src/io/xeros/content/activityboss/GlobalBossActivityManager.java`
  - `src/io/xeros/content/activityboss/ActivityType.java`
  - `src/io/xeros/content/activityboss/GlobalBossType.java`
  - `src/io/xeros/content/activityboss/GlobalBossSpawnZoneManager.java`
  - `src/io/xeros/content/activityboss/GlobalBossContributionTracker.java`
  - `src/io/xeros/content/activityboss/GlobalBossDropHandler.java`
  - `src/io/xeros/content/activityboss/GlobalBossLootTable.java`
  - `src/io/xeros/content/combat/death/NPCDeath.java`
- Entry point method:
  - `GlobalBossActivityManager.record(ActivityType type, int amount)`
- Important method calls in order:
  1. A content event calls `GlobalBossActivityManager.record(type, amount)`.
  2. `GlobalBossType.forActivity(type)` resolves the boss.
  3. Active and cooldown checks run.
  4. Progress total is incremented and announced by `GlobalBossAnnouncer.announceProgress`.
  5. At threshold, `GlobalBossActivityManager.spawn(GlobalBossType data)` runs.
  6. `GlobalBossSpawnZoneManager.getAvailableSpawnLocation(data)` chooses a spawn.
  7. `NPCSpawning.spawnNpc(...)` creates the boss.
  8. NPC death enters `NPCDeath.dropItemsFor(...)`.
  9. `GlobalBossActivityManager.onBossDeath(npc, player)` detects active global boss NPC ids.
  10. `GlobalBossDropHandler.rewardParticipants(NPC boss)` rewards contributors.
  11. Active boss state is removed and cooldown set.
- What player fields are read or changed:
  - Damage contribution from `npc.getDamageTaken()`.
  - Rewarded players receive inventory items.
  - Some optional contribution history can be added by `Player.addBossContribution`.
- What save entries are involved:
  - No player save entry was found for activity boss totals in this repo snapshot.
- What reward systems are called:
  - Global boss loot table, broadcast, activity contribution, regular NPC death hooks.
- Existing examples to copy:
  - `src/io/xeros/content/fireofexchange/FireOfExchange.java` records `ActivityType.FOE_BURN`.
  - `src/io/xeros/content/upgrade/UpgradeInterface.java` records `ActivityType.UPGRADE_ITEM`.
  - `src/io/xeros/content/commands/all/Voted.java` records `ActivityType.VOTE_CLAIM`.
  - `src/io/xeros/content/trails/TreasureTrails.java` records `ActivityType.CLUE_CASKET`.
- Safe extension points:
  - Add a new `ActivityType` and `GlobalBossType` mapping when a server-wide activity should spawn a boss.
  - Call `GlobalBossActivityManager.record` only at real activity completion.
  - Add rewards through `GlobalBossLootTable`.
- Dangerous areas to avoid:
  - Do not spawn global bosses manually from many content places.
  - Do not reward participants without checking damage contribution.
  - Do not make thresholds too low for high-volume activities.

## 25. Instance Creation and Cleanup Flow

- Main files:
  - `src/io/xeros/content/instances/InstancedArea.java`
  - `src/io/xeros/content/instances/InstanceHeight.java`
  - `src/io/xeros/content/instances/InstanceConfiguration.java`
  - `src/io/xeros/content/instances/impl/LegacySoloPlayerInstance.java`
  - `src/io/xeros/content/instances/BossInstanceManager.java`
  - `src/io/xeros/model/entity/player/Player.java`
  - `src/io/xeros/model/entity/player/PlayerAssistant.java`
- Entry point method:
  - `new InstancedArea(InstanceConfiguration configuration, Boundary... boundaries)`
- Important method calls in order:
  1. Instance constructor calls `InstanceHeight.getFreeAndReserve()` when using auto height.
  2. Content calls `area.add(Player player)` and moves the player to instance height.
  3. Content spawns NPCs through `NPCSpawning.spawnNpc(...)`.
  4. `InstancedArea.add(NPC npc)` sets `npc.setInstance(this)`.
  5. Object clicks can be intercepted by `InstancedArea.handleClickObject(...)`.
  6. Player leaving calls `InstancedArea.remove(Player player)`.
  7. If close-on-empty is enabled, `InstancedArea.dispose()` runs.
  8. `dispose()` calls `onDispose()`.
  9. `InstanceHeight.free(height)` frees reserved height when applicable.
  10. NPCs unregister and players are removed from the instance.
  11. `Player.destruct()` and `PlayerAssistant.movePlayer(...)` also call instance cleanup paths.
- What player fields are read or changed:
  - Player instance pointer, height level, position, ground item visibility, performance trackers for boss instances.
- What save entries are involved:
  - Boss instance unlock/progress fields are legacy or player maps in existing systems.
- What reward systems are called:
  - Instance-specific. `BossInstanceManager.leave(Player player)` can call instance reward loaders and leaderboards.
- Existing examples to copy:
  - `src/io/xeros/content/instances/BossInstanceManager.java`
  - `src/io/xeros/content/instances/impl/LegacySoloPlayerInstance.java`
  - `src/io/xeros/content/instances/InstancedArea.java`
- Safe extension points:
  - Subclass `InstancedArea` or reuse `LegacySoloPlayerInstance`.
  - Put cleanup in `onDispose()`.
  - Use `InstanceHeight.getFreeAndReserve()` and let `dispose()` free it.
- Dangerous areas to avoid:
  - Do not leak reserved heights.
  - Do not move players out without removing them from the instance.
  - Do not manually remove NPCs without unregistering or letting instance disposal handle them.

## 26. AOE Instance Flow

- Main files:
  - `data/aoe/aoe_boss_tiers.json`
  - `data/aoe/aoe_tier_rewards.json`
  - `data/aoe/AoeZoneMapConfig.json`
  - `src/io/xeros/content/dialogue/impl/BossInstanceDialogue.java`
  - `src/io/xeros/content/commands/all/Bossinstance.java`
  - `src/io/xeros/content/commands/all/Aoe.java`
  - `src/io/xeros/content/commands/all/Leaveaoe.java`
  - `src/io/xeros/content/instances/aoe/AoeTierController.java`
  - `src/io/xeros/content/instances/aoe/AoeInstanceService.java`
  - `src/io/xeros/content/instances/aoe/AoeNpcSpawner.java`
  - `src/io/xeros/content/instances/aoe/AoeTierEvents.java`
  - `src/io/xeros/content/instances/aoe/AoeDropInterceptor.java`
  - `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`
- Entry point method:
  - `AoeTierController.startTier(Player player, int tier)`
- Important method calls in order:
  1. Player opens tier selection through `Bossinstance.execute(...)` or NPC options that start `BossInstanceDialogue`.
  2. `BossInstanceDialogue.handleSelect(Player player, int index, AoeBossTierDef t)` validates lock/disabled state.
  3. It calls `AoeTierController.startTier(player, t.getTier())`.
  4. `startTier` calls `endTier(player, false)` to clear any old run.
  5. `AoeTierRepo.byTier(tier)` loads tier definition.
  6. Unlock and disabled checks run.
  7. `AoeInstanceService.buildAndEnter(...)` builds the map.
  8. `InstanceHeight.getFreeAndReserve()` reserves height.
  9. `MapBuilder.copy(...)` copies map region.
  10. `AoeTierRepo.registerInstance(player, instance)` stores instance.
  11. `player.getPA().movePlayer(...)` enters the map.
  12. `AoeNpcSpawner.spawnForInstance(...)` spawns tier NPCs.
  13. Active tier, reward tracker, and instance id attributes are stored.
  14. NPC deaths call `AoeTierEvents.onNpcDeath(player, npc)` from `NPCDeath.dropItemsFor`.
  15. `AoeTierController.incrementKill(player, tier)` updates kills and unlocks next tier.
  16. Drops call `AoeDropInterceptor.awardInsideAoe(player, item)`.
  17. Exiting calls `AoeTierController.endTier(player, true)`.
  18. `AoeInstanceService.teardown(instance, reason)` despawns NPCs, destroys map, frees height, and clears repo state.
- What player fields are read or changed:
  - Player attributes: `aoe_unlocked_tier`, `aoe_active_tier`, `aoe_reward_tracker`, `aoe_instance`, and per-tier `aoe_kc_` keys.
  - Inventory/bank receives intercepted drops and end-of-run rewards.
- What save entries are involved:
  - `AoeTierProgressSaveEntry` persists `aoe_unlocked_tier` and `aoe_kc_` values.
- What reward systems are called:
  - AOE kill rewards from `AoeTierRewardsLoader`, Fortune/Demon Hunter XP from `AoeTierEvents`, drop banking from `AoeDropInterceptor`, end-of-run rewards from `AoeTierController.endTier`.
- Existing examples to copy:
  - `src/io/xeros/content/dialogue/impl/BossInstanceDialogue.java`
  - `src/io/xeros/content/instances/aoe/AoeTierController.java`
  - `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`
  - `src/io/xeros/content/commands/all/Leaveaoe.java`
- Safe extension points:
  - JSON-only tier definitions in `data/aoe/aoe_boss_tiers.json`.
  - JSON-only tier rewards in `data/aoe/aoe_tier_rewards.json` when using supported fields.
  - Java support should be added through AOE loaders/controllers, not packet handlers.
- Dangerous areas to avoid:
  - Do not bypass `AoeInstanceService.teardown`.
  - Do not bypass `AoeTierRepo.clearInstance`.
  - Do not assume AOE weapons are restricted to AOE instances; combat AOE weapon logic is separate.

## 27. Upgrade Item Flow

- Main files:
  - `src/io/xeros/content/upgrade/UpgradeMaterials.java`
  - `src/io/xeros/content/upgrade/UpgradeInterface.java`
  - `src/io/xeros/model/entity/player/packets/ClickObject.java`
  - `src/io/xeros/model/entity/player/packets/ClickingButtons.java`
  - `src/io/xeros/model/entity/player/packets/ContainerAction1.java`
  - `src/io/xeros/model/entity/player/packets/ContainerAction3.java`
  - `src/io/xeros/content/collection_log/CollectionLog.java`
- Entry point method:
  - `UpgradeInterface.handleUpgrade(boolean all)`
- Important method calls in order:
  1. Object click on upgrade table opens `player.getUpgradeInterface().openInterface(UpgradeMaterials.UpgradeType.WEAPON)`.
  2. `UpgradeInterface.openInterface(UpgradeMaterials.UpgradeType type)` loads recipes through `UpgradeMaterials.getForType(type)`.
  3. Container click calls `UpgradeInterface.handleItemAction(int slot)`.
  4. `UpgradeInterface.showUpgrade(UpgradeMaterials upgrade)` displays selected recipe.
  5. Upgrade button calls `ClickingButtons.processPacket(...)`.
  6. `c.getUpgradeInterface().handleButton(realButtonId)` routes button `35020` to `handleUpgrade(false)`.
  7. `handleUpgrade` validates click delay, selected recipe, Fortune level, required item, and `player.foundryPoints`.
  8. It deletes required item and subtracts cost.
  9. Timer rolls success by success rate and boosts.
  10. On success, it adds reward item, calls `Achievements.increase(player, AchievementType.UPGRADE, 1)`, and calls `GlobalBossActivityManager.record(ActivityType.UPGRADE_ITEM, 1)`.
  11. Rare upgrades broadcast and call `CollectionLog.handleDrop` category ids `6`, `7`, `8`, or `9`.
  12. Fortune XP is granted based on cost.
- What player fields are read or changed:
  - `foundryPoints`, inventory, `clickDelay`, Fortune skill level/XP, donator amount, perk items, collection log state.
- What save entries are involved:
  - `foundryPoints` and skill XP are legacy player save fields.
  - Collection log runtime JSON for rare upgrades.
- What reward systems are called:
  - Achievement upgrade progress, global activity boss progress, collection log, Fortune XP, global broadcasts.
- Existing examples to copy:
  - `src/io/xeros/content/upgrade/UpgradeMaterials.java`
  - `src/io/xeros/content/upgrade/UpgradeInterface.java`
  - `src/io/xeros/content/fireofexchange/FireOfExchange.java` for foundry point source.
- Safe extension points:
  - Add normal recipes as enum entries in `UpgradeMaterials`.
  - Set `rare = true` only for upgrades that should broadcast and enter collection log.
  - Reuse existing categories and costs.
- Dangerous areas to avoid:
  - Do not duplicate upgrade roll logic.
  - Do not add item-specific branches for ordinary recipes.
  - Do not bypass `foundryPoints` validation.

## 28. Fire of Exchange Burn Flow

- Main files:
  - `src/io/xeros/content/fireofexchange/FireOfExchange.java`
  - `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`
  - `src/io/xeros/model/entity/player/DialogueHandler.java`
  - `src/io/xeros/model/entity/player/packets/ContainerAction1.java`
  - `src/io/xeros/model/entity/player/packets/ClickingButtons.java`
  - `src/io/xeros/model/entity/player/packets/dialogueoptions/TwoOptions.java`
  - `src/io/xeros/model/entity/player/packets/dialogueoptions/ThreeOptions.java`
  - `src/io/xeros/model/shops/ShopAssistant.java`
- Entry point method:
  - `FireOfExchange.exchangeItemForPoints(Player c)`
- Important method calls in order:
  1. Fire of Exchange interface is opened from NPC/object/examine/shop helpers.
  2. Container click in `ContainerAction1.processPacket(...)` validates burnable item and sets `c.currentExchangeItem`.
  3. It opens dialogue `130135`.
  4. `DialogueHandler.sendDialogues(130135, ...)` builds confirm options based on item amount.
  5. `TwoOptions` or `ThreeOptions` sets `currentExchangeItemAmount`.
  6. `FireOfExchange.exchangeItemForPoints(c)` runs.
  7. It checks `Configuration.DISABLE_FOE`.
  8. It calls `c.getQuesting().exchangeItemForPoints(c)`.
  9. It validates `currentExchangeItem`, crystals, item ownership, burn price, ironman branch rules, perks, and boosts.
  10. It deletes the item amount.
  11. It adds `c.foundryPoints += exchangePrice`.
  12. It grants Fortune XP from burn value.
  13. It progresses event calendar and leaderboard when eligible.
  14. It calls `Achievements.increase(c, AchievementType.FOE_POINTS, amount)` for normal burns.
  15. It updates recent dissolved history and `totalEarnedExchangePoints`.
  16. It calls `GlobalBossActivityManager.record(ActivityType.FOE_BURN, amount)`.
  17. It writes `FireOfExchangeLog` and checks tool augment unlocks.
- What player fields are read or changed:
  - `currentExchangeItem`, `currentExchangeItemAmount`, `foundryPoints`, `totalEarnedExchangePoints`, `recentlyDissolvedItems`, `recentlyDissolvedPrices`, `burnHistory`, Fortune XP.
- What save entries are involved:
  - `foundryPoints`, `totalEarnedExchangePoints`, and skill XP are legacy save fields.
- What reward systems are called:
  - Upgrade/foundry points, Fortune XP, achievements, event calendar, leaderboard, activity boss progress, tool augments.
- Existing examples to copy:
  - `src/io/xeros/content/fireofexchange/FireOfExchange.java`
  - `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`
  - `src/io/xeros/model/entity/player/packets/dialogueoptions/ThreeOptions.java` dialogue action `130135`.
- Safe extension points:
  - Add burn values in `FireOfExchangeBurnPrice.getBurnPrice`.
  - Add FOE shop prices in `FireOfExchange.getExchangeShopPrice`.
  - Keep burn confirmation in the existing dialogue flow.
- Dangerous areas to avoid:
  - Do not add casual high burn values for powerful gear.
  - Do not bypass item ownership validation.
  - Do not let FOE shop items burn for more than they cost.

## 29. Wraith Charge Flow

- Main files:
  - `src/io/xeros/content/wraith/WraithCharges.java`
  - `src/io/xeros/content/items/UseItem.java`
  - `src/io/xeros/content/commands/all/Wraith.java`
  - `src/io/xeros/content/commands/all/Wraithcharges.java`
  - `src/io/xeros/content/combat/core/AttackEntity.java`
  - `src/io/xeros/model/entity/player/save/PlayerSave.java`
- Entry point method:
  - `WraithCharges.addChargesFromEssence(Player p, int wraithItemSlot, int essenceItemId, int requestedEss)`
- Important method calls in order:
  1. Player uses Wraith Essence on a Wraith weapon in `UseItem.ItemonItem(...)`, or uses `::wraith charge <amount>`.
  2. Code resolves inventory/equipment slot and weapon id.
  3. `WraithCharges.isWraithWeapon(int itemId)` validates the target.
  4. `WraithCharges.addChargesFromEssence(...)` validates essence id and requested amount.
  5. It reads current charges through `getCurrentCharges(Player p, int itemId)`.
  6. It clamps against `getCapFor(int itemId)`.
  7. It computes essence needed using `chargesPerEssence()`.
  8. It deletes essence from inventory.
  9. It calls `setCurrentCharges(Player p, int itemId, int newValue)`.
  10. Combat consumes charges through `WraithCharges.consumeCharge(Player p, int itemId)` in `AttackEntity`.
  11. Combat also checks Wraith weapon charge availability in `AttackEntity`.
- What player fields are read or changed:
  - `wraithScytheCharge`, `wraithStaffCharge`, `wraithBowCharge`, inventory Wraith Essence, equipped weapon.
- What save entries are involved:
  - Legacy keys in `PlayerSave.java`: `wraith-scythe-charge`, `wraith-staff-charge`, `wraith-bow-charge`.
- What reward systems are called:
  - No direct reward system; Wraith Essence acts as an item sink and weapon charge fuel.
- Existing examples to copy:
  - `src/io/xeros/content/wraith/WraithCharges.java`
  - `src/io/xeros/content/commands/all/Wraith.java`
  - `src/io/xeros/content/items/UseItem.java`
- Safe extension points:
  - Add Wraith weapon ids to `WraithCharges` if extending the family.
  - Keep caps and charge-per-essence centralized.
  - Use `addChargesFromEssence` instead of direct charge mutation.
- Dangerous areas to avoid:
  - Do not consume charges outside centralized Wraith logic.
  - Do not add uncapped charging.
  - Do not remove essence before final validation.

## 30. Slayer Task Progress Flow

- Main files:
  - `src/io/xeros/content/skills/slayer/Slayer.java`
  - `src/io/xeros/content/skills/slayer/SlayerMaster.java`
  - `src/io/xeros/content/skills/slayer/DemonHunterTaskManager.java`
  - `src/io/xeros/content/skills/slayer/DemonSlayerMaster.java`
  - `src/io/xeros/model/entity/npc/NPCProcess.java`
  - `src/io/xeros/model/entity/player/save/PlayerSave.java`
- Entry point method:
  - `Slayer.killTaskMonster(NPC npc)`
- Important method calls in order:
  1. Player receives a Slayer task through `Slayer.createNewTask(int masterId, boolean override)`.
  2. Task assignment sets `task`, `taskAmount`, `master`, `lastTask`, and partner task state if needed.
  3. NPC death in `NPCProcess` calls `player.getSlayer().killTaskMonster(npc)`.
  4. `Slayer.killTaskMonster(NPC npc)` checks active task and task NPC match.
  5. `Slayer.reduceTaskAmount(Player player)` decrements task amount with bracelet/perk effects.
  6. Slayer XP is granted by `player.getPA().addSkillXPMultiplied(...)`.
  7. Konar and wilderness task reward rolls can run.
  8. Superior spawn chance runs if Bigger and Badder is unlocked.
  9. When `taskAmount == 0`, event calendar progress, Slayer points, streaks, milestone bonuses, pass XP, achievements, and unlocks run.
  10. `Achievements.increase(player, AchievementType.SLAY, 1)` runs on task completion.
  11. `NPCProcess` also calls `DemonHunterTaskManager.handleKill(player, npc)`.
  12. Demon Hunter matching decrements `demonHunterTaskProgress`, grants Demon Hunter XP, Demon Marks, contract rewards, milestones, and clears completed tasks.
- What player fields are read or changed:
  - Slayer: task, task amount, master, consecutive tasks, Slayer points, extensions, unlocks, removed tasks, `slayerTasksCompleted`, superior flags.
  - Demon Hunter: `demonHunterTask`, `demonHunterTaskProgress`, `demonHunterXP`, `demonTaskStreak`, `demonHunterTierUnlocked`, `demonHunterMilestones`, `demonMarks`, `demonContract`.
- What save entries are involved:
  - Slayer legacy keys in `PlayerSave.java`: `slayer-task`, `slayer-task-amount`, `slayerPoints`, `consecutive-tasks`, `removed-slayer-tasks`, `extended-slayer-tasks`, `slayer-tasks-completed`, `superior-slayer`.
  - Demon Hunter save entries: Not found in repo. Searched terms: `demonHunter`, `demon-hunter`, `demonTask`, `demon-task`, `demonMarks`, `demon-marks`, `demonContract`, `DemonSlayer`.
- What reward systems are called:
  - Slayer XP, Slayer points, brimstone/Larran rewards, event calendar, achievements, battlepass XP, Demon Hunter XP, Demon Marks, milestones.
- Existing examples to copy:
  - `src/io/xeros/content/skills/slayer/Slayer.java`
  - `src/io/xeros/content/skills/slayer/DemonHunterTaskManager.java`
  - `src/io/xeros/content/skills/slayer/DemonHunterSlayerDialogue.java`
- Safe extension points:
  - Add normal Slayer tasks through Slayer task/master data.
  - Add task completion hooks inside the real completion branch, not on every kill.
  - Use Demon Hunter manager methods for demon-specific progression.
- Dangerous areas to avoid:
  - Do not manually decrement `taskAmount` outside `Slayer.killTaskMonster` for NPC kills.
  - Do not grant Slayer points directly when `Slayer` completion logic should do it.
  - Do not add Demon Hunter rewards without confirming persistence expectations.

## A. Safest Flows For Adding New Content

- Commands through `src/io/xeros/content/commands/Command.java` subclasses.
- Dialogues through `src/io/xeros/content/dialogue/DialogueBuilder.java`.
- Upgrade recipes through `src/io/xeros/content/upgrade/UpgradeMaterials.java`.
- AOE tier definitions and supported rewards through `data/aoe/`.
- Achievements through `Achievements.increase(...)` at real completion events.
- Collection log unique rewards through `CollectionLog.handleDrop(...)` once per reward.
- Activity boss progress through `GlobalBossActivityManager.record(...)`.
- New persistent small fields through `PlayerSaveEntry`.

## B. Riskiest Flows To Avoid Editing

- `src/io/xeros/model/entity/player/save/PlayerSave.java` for new content keys.
- `src/io/xeros/model/entity/npc/NPCProcess.java` for isolated boss mechanics.
- `src/io/xeros/model/entity/npc/actions/NPCHitPlayer.java` for one boss or item.
- `src/io/xeros/model/entity/player/packets/ClickingButtons.java` for large feature logic.
- `src/io/xeros/model/entity/player/packets/Commands.java` for normal commands.
- `src/io/xeros/model/entity/npc/drops/DropManager.java` for ordinary boss drops.
- `src/io/xeros/model/shops/ShopAssistant.java` for ordinary shop stock changes.

## C. Best Places To Hook New Rewards

- NPC drops: `src/io/xeros/model/entity/npc/drops/DropManager.java` through drop table data.
- NPC death meta rewards: `src/io/xeros/content/combat/death/NPCDeath.java`.
- Boss points: `src/io/xeros/content/bosspoints/BossPoints.java`.
- Upgrade rewards: `src/io/xeros/content/upgrade/UpgradeInterface.java`.
- AOE rewards: `src/io/xeros/content/instances/aoe/AoeTierRewardsLoader.java` and `src/io/xeros/content/instances/aoe/AoeTierController.java`.
- Global activity rewards: `src/io/xeros/content/activityboss/GlobalBossLootTable.java`.
- Daily rewards: `src/io/xeros/content/dailyrewards/DailyRewards.java` and its runtime reward container.

## D. Best Places To Hook New Achievements

- Call `Achievements.increase(Player, AchievementType, int)` from:
  - `src/io/xeros/content/combat/death/NPCDeath.java` for kill/drop-driven progress.
  - `src/io/xeros/content/upgrade/UpgradeInterface.java` for upgrade success.
  - `src/io/xeros/content/fireofexchange/FireOfExchange.java` for burn milestones.
  - `src/io/xeros/content/dailyrewards/DailyRewards.java` for daily claims.
  - `src/io/xeros/content/skills/slayer/Slayer.java` for Slayer completion.
  - A new content manager at the real completion method for new minigames.

## E. Best Places To Hook New Tasks

- Task Master kill tasks: `src/io/xeros/content/taskmaster/Tasks.java`.
- Task Master completion logic: `src/io/xeros/content/taskmaster/TaskMaster.java`.
- NPC kill matching: `src/io/xeros/model/entity/npc/NPCProcess.java`, only with caution.
- Starter tasks: `src/io/xeros/content/items/Starter.java` through existing Slayer task assignment.
- Slayer tasks: `src/io/xeros/content/skills/slayer/Slayer.java` and `src/io/xeros/content/skills/slayer/SlayerMaster.java`.

## F. Best Places To Hook New Save Data

- New simple player values: create a `PlayerSaveEntry` like `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`.
- Daily cooldown/streak data: copy `src/io/xeros/content/dailyrewards/DailyRewardsPlayerSaveEntry.java`.
- Existing legacy fields: only then touch `src/io/xeros/model/entity/player/save/PlayerSave.java`.
- Large structured per-player data: use a separate runtime JSON manager like `src/io/xeros/content/collection_log/CollectionLog.java` or `src/io/xeros/content/taskmaster/TaskMaster.java`.

## G. Best Places To Hook New Commands

- Player commands: `src/io/xeros/content/commands/all/`.
- Donator commands: `src/io/xeros/content/commands/donator/`.
- Staff commands: `src/io/xeros/content/commands/helper/`, `src/io/xeros/content/commands/moderator/`, or `src/io/xeros/content/commands/admin/`.
- Debug/test commands: `src/io/xeros/content/commands/test/`.
- Command pattern to copy: `src/io/xeros/content/commands/all/Bossinstance.java`, `src/io/xeros/content/commands/all/Leaveaoe.java`, and `src/io/xeros/content/commands/all/Wraith.java`.

## H. Best Places To Hook New Dialogues

- New reusable dialogue classes: `src/io/xeros/content/dialogue/impl/`.
- Dialogue base: `src/io/xeros/content/dialogue/DialogueBuilder.java`.
- Option callbacks: `src/io/xeros/content/dialogue/DialogueOption.java`.
- NPC/object/item entry to dialogue: start it from the relevant handler with `player.start(...)`.
- Patterns to copy: `src/io/xeros/content/dialogue/impl/BossInstanceDialogue.java`, `src/io/xeros/content/tutorial/TutorialDialogue.java`, and `src/io/xeros/content/dailyrewards/DailyRewardsDialogue.java`.
