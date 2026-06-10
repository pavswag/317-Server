# Turmoil Progress Hook Map

This map documents the repo-visible hooks that advance player progression. Future content should plug into these hooks instead of rewriting core systems. All paths below are repo-relative.

## Hook Rules

- Search for an existing matching hook before adding a new one.
- Prefer manager methods such as `Achievements.increase`, `Pass.addExperience`, `BossPoints.addPoints`, `CollectionLog.handleDrop`, `GlobalBossActivityManager.record`, and `AoeTierController.incrementKill`.
- For new persistent player data, prefer a new `PlayerSaveEntry` like `src/io/xeros/model/entity/player/save/PlayerSaveEntry.java` and `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`.
- Avoid expanding `src/io/xeros/model/entity/player/save/PlayerSave.java` unless maintaining an older legacy save key.
- Avoid direct mutation of points, task counters, collection log maps, or pass tiers when a public hook already exists.
- Some content data is external at runtime. This doc names the repo-side loader or manager when the actual data file is not checked into the repo.

## Achievements

- Main files:
  - `src/io/xeros/content/achievement/Achievements.java`
  - `src/io/xeros/content/achievement/AchievementHandler.java`
  - `src/io/xeros/content/achievement/AchievementType.java`
  - `src/io/xeros/content/combat/death/NPCDeath.java`
  - `src/io/xeros/model/entity/player/save/PlayerSave.java`
- What event triggers progress:
  - NPC deaths through `NPCDeath.dropItemsFor` and `AchievementHandler.npcKilled`.
  - Rare drop announcements through `NPCDeath.announce`.
  - Collection log unlocks through `CollectionLog.handleDrop`.
  - Daily reward claims through `DailyRewards.claim`.
  - Vote claims and vote chest unlocks through `Voted.rewards` and `VoteChest.roll`.
  - Fire of Exchange burns through `FireOfExchange.exchangeItemForPoints`.
  - Slayer task completion through `Slayer.killTaskMonster`.
- What method updates progress:
  - `Achievements.increase(Player, AchievementType, int)` is the main hook.
  - `AchievementHandler.npcKilled(NPC)` handles broad NPC kill categories.
  - `AchievementHandler.claim(Achievement)` and `AchievementHandler.claim(AchievementTier)` grant rewards.
  - `Achievements.updateProgress(Player, AchievementType)` refreshes interface progress.
- What player fields/save entries are used:
  - `AchievementHandler` stores progress, completion state, claim state, and achievement points.
  - `PlayerSave.java` reads and writes achievement tier sections and `achievement-points`.
- Existing examples to copy:
  - `src/io/xeros/content/collection_log/CollectionLog.java` calls `Achievements.increase(player, AchievementType.COLLECTOR, 1)`.
  - `src/io/xeros/content/dailyrewards/DailyRewards.java` calls `Achievements.increase(player, AchievementType.DAILY, 1)`.
  - `src/io/xeros/content/commands/all/Voted.java` calls `Achievements.increase(player, AchievementType.VOTER, voteCount)`.
  - `src/io/xeros/content/fireofexchange/FireOfExchange.java` calls `Achievements.increase(c, AchievementType.FOE_POINTS, amount)`.
  - `src/io/xeros/content/skills/slayer/Slayer.java` calls `Achievements.increase(player, AchievementType.SLAY, 1)`.
- Safe ways to add new progress:
  - Add a new value to `AchievementType` only when no existing type fits.
  - Add a matching `Achievements.Achievement` enum entry with tier, target amount, and rewards.
  - Call `Achievements.increase` from the real completion event.
  - Keep achievement rewards conservative because they can stack with battlepass, collection logs, and daily loops.
- Risk level:
  - Medium.
- What to avoid:
  - Do not edit achievement save parsing unless adding a compatible migration.
  - Do not mutate `AchievementHandler` arrays directly from content.
  - Do not create achievement progress from passive or AFK loops without rate limits.

## Task Master

- Main files:
  - `src/io/xeros/content/taskmaster/Tasks.java`
  - `src/io/xeros/content/taskmaster/TaskMaster.java`
  - `src/io/xeros/content/taskmaster/TaskMasterKills.java`
  - `src/io/xeros/content/taskmaster/TaskDifficulty.java`
  - `src/io/xeros/content/taskmaster/TaskType.java`
  - `src/io/xeros/model/entity/npc/NPCProcess.java`
  - `src/io/xeros/model/entity/player/Player.java`
- What event triggers progress:
  - Normal NPC death processing in `NPCProcess` checks active `TaskMasterKills` entries against the killed NPC name.
  - Existing special cases handle Barrows and Dagannoth task descriptions.
  - Task generation runs from player login/startup flow and reset-item flows.
- What method updates progress:
  - `TaskMasterKills.incrementAmountKilled(int)` increments the runtime task counter.
  - `TaskMaster.trackActivity(Player, TaskMasterKills)` checks completion percentage and calls `finishTask`.
  - `TaskMaster.finishTask(Player, TaskMasterKills)` grants rewards and marks the task claimed.
  - `TaskMaster.generateTasks(Player, boolean)` creates new task entries from `Tasks`.
- What player fields/save entries are used:
  - `Player.getTaskMaster().taskMasterKillsList` holds active task entries.
  - `TaskMaster.loadAllMoneyMaking` and `TaskMaster.saveAllMoneyMaking` persist per-player Task Master JSON in runtime storage.
  - `TaskMasterKills` stores description, amount killed, target amount, task type, difficulty, reward items, claimed state, and timer fields.
- Existing examples to copy:
  - `src/io/xeros/model/entity/npc/NPCProcess.java` increments combat Task Master tasks after NPC death.
  - `src/io/xeros/content/taskmaster/Tasks.java` is the data enum for adding simple combat or skilling task definitions.
  - `src/io/xeros/model/entity/player/packets/itemoptions/ItemOptionOne.java` clears and regenerates tasks for reset items.
- Safe ways to add new progress:
  - Add simple NPC-name tasks through `Tasks` when the existing `NPCProcess` matching can recognize the kill.
  - Reuse `TaskType.COMBAT` and `TaskType.SKILLING` unless a new type is truly needed.
  - For new non-kill activities, add a small explicit hook near the real activity completion and call `trackActivity`.
  - Keep rewards in `TaskMaster.finishTask` style, with clear hourly/daily/weekly separation.
- Risk level:
  - Medium.
- What to avoid:
  - Do not rewrite task persistence.
  - Do not depend on fragile substring matching for complex new task types.
  - Do not mutate `taskMasterKillsList` from unrelated systems without using the same task lifecycle.

## Collection Logs

- Main files:
  - `src/io/xeros/content/collection_log/CollectionLog.java`
  - `src/io/xeros/content/collection_log/CollectionRewards.java`
  - `src/io/xeros/model/entity/npc/drops/TableGroup.java`
  - `src/io/xeros/model/entity/npc/drops/DropManager.java`
  - `src/io/xeros/model/entity/npc/pets/PetHandler.java`
  - `src/io/xeros/model/entity/player/save/PlayerSave.java`
- What event triggers progress:
  - Rare NPC drops from `TableGroup.access`.
  - Pet unlocks from `PetHandler`.
  - Upgrade successes from `UpgradeInterface`.
  - AOE weapon drops and shops that call `CollectionLog.handleDrop`.
  - Clue, raid, Arbograve, Hespori, Vote Chest, and skilling pet reward paths.
- What method updates progress:
  - `CollectionLog.handleDrop(Player, int, int, int)` and `CollectionLog.handleDrop(Player, int, int, int, boolean)` are the main hooks.
  - `CollectionLog.saveToJSON()` persists unlocked collection items.
  - `CollectionRewards.handleButton(Player, int)` grants completion rewards.
- What player fields/save entries are used:
  - `Player.collectionLog`, `Player.viewingCollectionLog`, `Player.collectionLogNPC`, and `Player.collectionLogTab`.
  - `Player.getClaimedLog()` tracks claimed completion rewards.
  - `PlayerSave.java` reads and writes claimed collection log reward ids.
  - Collection entries themselves are saved by `CollectionLog` in runtime collection-log JSON.
- Existing examples to copy:
  - `src/io/xeros/model/entity/npc/drops/TableGroup.java` calls `player.getCollectionLog().handleDrop(player, npcId, item.getId(), item.getAmount())` for rare drops.
  - `src/io/xeros/content/upgrade/UpgradeInterface.java` uses category ids `6`, `7`, `8`, and `9` for rare upgrade materials.
  - `src/io/xeros/model/entity/npc/drops/TableGroup.java` and `src/io/xeros/model/shops/ShopAssistant.java` use category id `10` for AOE weapons.
  - `src/io/xeros/content/item/lootable/impl/RaidsChestRare.java`, `src/io/xeros/content/item/lootable/impl/TheatreOfBloodChest.java`, and `src/io/xeros/content/item/lootable/impl/ArbograveChest.java` call the same collection hook from chest rewards.
- Safe ways to add new progress:
  - Call `CollectionLog.handleDrop` from the real reward event when the player actually receives a unique item.
  - Add completion rewards in `CollectionRewards` only after checking the existing economy audit.
  - Use existing special category ids only when the item belongs to that category.
  - For normal boss drops, let `TableGroup` and `DropManager` call the hook instead of adding duplicate calls.
- Risk level:
  - Medium.
- What to avoid:
  - Do not manipulate `CollectionLog.getCollections()` directly.
  - Do not add duplicate collection calls for the same drop path.
  - Do not assume category membership is repo-local; the NPC/category list is loaded externally by `CollectionLog.init`.

## Battlepass

- Main files:
  - `src/io/xeros/content/battlepass/Pass.java`
  - `src/io/xeros/content/battlepass/Rewards.java`
  - `src/io/xeros/content/battlepass/RewardList.java`
  - `src/io/xeros/model/entity/player/save/PlayerSave.java`
  - `src/io/xeros/model/entity/player/Player.java`
- What event triggers progress:
  - Achievement reward claims.
  - Random qualifying NPC kills.
  - Slayer streak milestones.
  - Collection log reward claims.
  - Some boss, minigame, raid, and event completion paths.
- What method updates progress:
  - `Pass.addExperience(Player, int)` is the main hook.
  - `Pass.levelUp(Player)` advances tiers after enough XP.
  - `Pass.grantRewards(Player)` grants default and member rewards.
  - `Pass.handleLogin(Player)` resets player pass state when a new season starts.
- What player fields/save entries are used:
  - `Player.tier`, `Player.xp`, `Player.member`, and `Player.currentSeason`.
  - `PlayerSave.java` keys: `division-tier`, `division-xp`, `division-member`, and `division-season`.
  - `Rewards` persists generated pass reward lists through runtime data files under the server data directory.
- Existing examples to copy:
  - `src/io/xeros/content/achievement/AchievementHandler.java` grants pass XP on achievement claims.
  - `src/io/xeros/content/collection_log/CollectionRewards.java` grants pass XP on completed collection logs.
  - `src/io/xeros/content/skills/slayer/Slayer.java` grants pass XP at long Slayer streak milestones.
  - `src/io/xeros/model/entity/npc/NPCProcess.java` grants occasional pass XP for high-combat NPC kills.
- Safe ways to add new progress:
  - Use small `Pass.addExperience` amounts from real completion events.
  - Respect the guardrails in `Pass.addExperience`, especially boundaries, active season, and tier cap.
  - Use collection completions, task completions, and weekly events as safer pass XP sources than raw repeatable kill loops.
- Risk level:
  - Medium.
- What to avoid:
  - Do not mutate `Player.xp` or `Player.tier` directly.
  - Do not bypass the boundary checks in `Pass.addExperience`.
  - Do not add frequent pass XP to AFK or high-volume loops.

## Daily Rewards

- Main files:
  - `src/io/xeros/content/dailyrewards/DailyRewards.java`
  - `src/io/xeros/content/dailyrewards/DailyRewardsPlayerSaveEntry.java`
  - `src/io/xeros/content/dailyrewards/DailyRewardsRecords.java`
  - `src/io/xeros/content/dailyrewards/DailyRewardContainer.java`
- What event triggers progress:
  - Player presses the daily reward claim button after the 24-hour cooldown.
  - `DailyRewards.onLogin` notifies the player when a claim is ready.
- What method updates progress:
  - `DailyRewards.claim()` grants the current reward, increments streak, records the claim, and calls `Achievements.increase(player, AchievementType.DAILY, 1)`.
  - `DailyRewardsRecords.add(Player, int)` records account/device claim data.
  - `DailyRewardContainer.load()` loads the active reward container from external daily reward data.
- What player fields/save entries are used:
  - `DailyRewardsPlayerSaveEntry` keys: `daily_rewards_claim_date`, `daily_rewards_identifier`, and `daily_rewards_streak`.
  - `DailyRewardsRecords` stores shared claim records in runtime save storage.
- Existing examples to copy:
  - `src/io/xeros/content/dailyrewards/DailyRewards.java` is the pattern for a cooldown-gated daily claim.
  - `src/io/xeros/content/dailyrewards/DailyRewardsPlayerSaveEntry.java` is the pattern for modular save data.
- Safe ways to add new progress:
  - Change daily reward content through the existing daily reward container flow, not by adding hardcoded claim logic.
  - Add extra hooks from `DailyRewards.claim` only for safe meta progression such as achievements.
  - Keep donor doubling in mind when balancing rewards.
- Risk level:
  - Medium.
- What to avoid:
  - Do not bypass `DailyRewardsRecords.canClaim`.
  - Do not grant high-value repeatable items directly from login.
  - Do not add new daily save keys to `PlayerSave.java` when `PlayerSaveEntry` is available.

## Vote Panel

- Main files:
  - `src/io/xeros/content/commands/all/Voted.java`
  - `src/io/xeros/content/vote_panel/VotePanelManager.java`
  - `src/io/xeros/content/vote_panel/VotePanelInterface.java`
  - `src/io/xeros/content/vote_panel/VoteUser.java`
  - `src/io/xeros/content/item/lootable/impl/VoteChest.java`
  - `src/io/xeros/model/entity/player/save/PlayerSave.java`
- What event triggers progress:
  - Player claims votes with the `::voted` command.
  - Vote panel interface buttons spend vote panel points.
  - Vote chest rolls consume vote keys and can add extra vote points.
- What method updates progress:
  - `Voted.claimVotes(Player)` coordinates vote rewards, panel progress, bonus timers, and global vote counters.
  - `Voted.votePanel(Player)` updates panel vote count and day streak.
  - `Voted.rewards(Player, int)` grants vote points, vote key points, XP scroll time, coins, achievements, and activity boss progress.
  - `Voted.incrementGlobalVote(int)` updates global vote counters and vote boss/WOGW triggers.
  - `VotePanelManager.addVote(String)` updates weekly voter counts.
  - `VotePanelManager.saveToJSON()` persists vote panel runtime data.
- What player fields/save entries are used:
  - `Player.votePoints`, `Player.voteKeyPoints`, `Player.lastVote`, and `Player.lastVotePanelPoint`.
  - `PlayerSave.java` keys: `votePoints`, `voteKeyPoints`, `lastVote`, and `lastVotePanelPoint`.
  - `VotePanelManager.wrapper` stores weekly vote panel data in runtime save storage.
- Existing examples to copy:
  - `src/io/xeros/content/commands/all/Voted.java` calls `Achievements.increase(player, AchievementType.VOTER, voteCount)`.
  - `src/io/xeros/content/commands/all/Voted.java` calls `GlobalBossActivityManager.record(ActivityType.VOTE_CLAIM, voteCount)`.
  - `src/io/xeros/content/item/lootable/impl/VoteChest.java` calls `Achievements.increase(c, AchievementType.VOTE_CHEST_UNLOCK, 1)`.
- Safe ways to add new progress:
  - Hook vote-based global events inside the actual vote claim path.
  - Use `GlobalBossActivityManager.record(ActivityType.VOTE_CLAIM, amount)` for vote-driven activity boss progress.
  - Use weekly or streak rewards through `VotePanelManager` and `VotePanelInterface`.
- Risk level:
  - Medium.
- What to avoid:
  - Do not grant vote points from non-vote content unless explicitly balancing a retention event.
  - Do not increment `Voted.globalVotes` or `Voted.totalVotes` outside `Voted.incrementGlobalVote`.
  - Do not write vote panel data outside `VotePanelManager.saveToJSON`.

## Boss Points

- Main files:
  - `src/io/xeros/content/bosspoints/BossPoints.java`
  - `src/io/xeros/content/bosspoints/JarsToPoints.java`
  - `src/io/xeros/content/combat/death/NPCDeath.java`
  - `src/io/xeros/model/entity/player/save/PlayerSave.java`
  - `src/io/xeros/content/combat/stats/NPCDeathTracker.java`
- What event triggers progress:
  - Normal boss deaths through `NPCDeath.dropItemsFor`.
  - Manual raid/chest completions through `BossPoints.addManualPoints`.
  - Some legacy boss classes call `BossPoints.getPointsOnDeath` and `BossPoints.addPoints` directly.
  - Boss jar conversion through `JarsToPoints.open`.
- What method updates progress:
  - `BossPoints.getPointsOnDeath(NPC)` resolves configured points for normal NPC deaths.
  - `BossPoints.getManualPoints(String)` resolves configured manual rewards.
  - `BossPoints.addPoints(Player, int, boolean)` grants points, Demon Hunter XP, event-calendar progress, and leaderboard count.
  - `BossPoints.addManualPoints(Player, String)` is the safe manual completion hook.
- What player fields/save entries are used:
  - `Player.bossPoints` and `Player.bossPointsRefund`.
  - `PlayerSave.java` keys: `bossPoints` and `bossPointsRefund`.
  - Boss point values are loaded from external boss point config by `BossPoints.init`.
- Existing examples to copy:
  - `src/io/xeros/content/combat/death/NPCDeath.java` handles ordinary boss death points.
  - `src/io/xeros/content/item/lootable/impl/TheatreOfBloodChest.java` calls `BossPoints.addManualPoints(player, "theatre of blood")`.
  - `src/io/xeros/content/minigames/raids/Raids.java` calls `BossPoints.addManualPoints(player, "chambers of xeric")`.
- Safe ways to add new progress:
  - Add boss point values to the external boss point data for ordinary boss deaths.
  - For chest or raid completions, use `BossPoints.addManualPoints` with a configured manual name.
  - Keep point amounts modest and check the boss point shop before adding new sources.
- Risk level:
  - High.
- What to avoid:
  - Do not directly increment `Player.bossPoints` except in legacy jar conversion style.
  - Do not grant boss points for AFK, skilling, or non-boss actions.
  - Do not hardcode new boss point names in `NPCDeath`.

## Slayer Task Progress

- Main files:
  - `src/io/xeros/content/skills/slayer/Slayer.java`
  - `src/io/xeros/content/skills/slayer/SlayerMaster.java`
  - `src/io/xeros/content/skills/slayer/Task.java`
  - `src/io/xeros/content/skills/slayer/SlayerRewardsInterfaceData.java`
  - `src/io/xeros/model/entity/npc/NPCProcess.java`
  - `src/io/xeros/model/entity/player/save/PlayerSave.java`
- What event triggers progress:
  - NPC death flow calls `target.getSlayer().killTaskMonster(npc)` in `NPCProcess`.
  - Slayer partner kills can also call `killTaskMonster` for the partner.
- What method updates progress:
  - `Slayer.killTaskMonster(NPC)` checks whether the killed NPC matches the current task.
  - `Slayer.reduceTaskAmount(Player)` decrements the task amount.
  - Task completion inside `killTaskMonster` grants Slayer points, streak rewards, diary progress, pass XP at high streaks, and `Achievements.increase(player, AchievementType.SLAY, 1)`.
- What player fields/save entries are used:
  - `Slayer.task`, `Slayer.taskAmount`, `Slayer.points`, `Slayer.consecutiveTasks`, unlocks, extensions, and blocked tasks.
  - `PlayerSave.java` keys include `slayer-tasks-completed`, `slayerPoints`, `slayerTaskAmount`, `consecutive-tasks`, `slayer-unlocks`, and `extended-slayer-tasks`.
- Existing examples to copy:
  - `src/io/xeros/model/entity/npc/NPCProcess.java` is the standard kill hook.
  - `src/io/xeros/content/skills/slayer/Slayer.java` has the completion reward, streak, donor bonus, and achievement patterns.
  - `src/io/xeros/content/skills/slayer/LarrensKey.java` shows special Wilderness Slayer reward progress.
- Safe ways to add new progress:
  - Add new Slayer monsters through the existing Slayer task data and matching logic.
  - Hook extra rewards at task completion, not every kill, unless the reward is intentionally low-value.
  - Use existing streak milestones for retention rewards.
- Risk level:
  - Medium.
- What to avoid:
  - Do not add one-off Slayer task progress to unrelated NPC death code.
  - Do not reset task or streak fields outside the Slayer manager.
  - Do not add high-value rewards to every task kill.

## Demon Hunter Progress

- Main files:
  - `src/io/xeros/content/skills/slayer/DemonSlayerMaster.java`
  - `src/io/xeros/content/skills/slayer/DemonHunterTaskManager.java`
  - `src/io/xeros/content/skills/slayer/DemonHunterPerks.java`
  - `src/io/xeros/content/skills/slayer/DemonSlayerMilestoneManager.java`
  - `src/io/xeros/content/skills/slayer/DemonMarkRewardHandler.java`
  - `src/io/xeros/content/skills/slayer/DemonHunterTaskOverlayManager.java`
  - `src/io/xeros/model/entity/npc/NPCProcess.java`
  - `src/io/xeros/model/entity/player/Player.java`
- What event triggers progress:
  - NPC death flow calls `DemonHunterTaskManager.handleKill(target, npc)` in `NPCProcess`.
  - Partner Slayer kills can call `DemonHunterTaskManager.handleKill` for the partner.
  - `DemonSlayerMaster.assign(Player)` assigns a new Demon Hunter task from the boss tier pool.
- What method updates progress:
  - `DemonHunterTaskManager.handleKill(Player, NPC)` matches the NPC against `DemonSlayerMaster.BossTier`.
  - On-task kills decrement `Player.demonHunterTaskProgress`, grant Demon Hunter XP, grant Slayer XP, grant Demon Marks, update leaderboard state, and refresh overlay.
  - Completed tasks increment `Player.demonTaskStreak`, call `DemonSlayerMilestoneManager.check`, call `DemonMarkRewardHandler.reward`, then clear the task.
  - `Player.addDemonHunterXP(int)` adds custom Demon Hunter XP and also calls skill XP for `Skill.DEMON_HUNTER`.
- What player fields/save entries are used:
  - `Player.demonHunterTask`, `Player.demonHunterTaskProgress`, `Player.demonHunterXP`, `Player.demonTaskStreak`, `Player.demonHunterTierUnlocked`, `Player.demonHunterMilestones`, `Player.demonMarks`, and `Player.demonContract`.
  - Normal skill XP for `Skill.DEMON_HUNTER` is saved with the standard skill save data.
  - Not found in repo: save keys or a `PlayerSaveEntry` for `demonHunterTask`, `demonHunterTaskProgress`, `demonHunterXP`, `demonTaskStreak`, `demonHunterTierUnlocked`, `demonHunterMilestones`, `demonMarks`, or `demonContract`. Searched `DemonHunter|demonHunter|demon-hunter|demon_task|demonMarks|demon-marks|demonTask|Demon.*SaveEntry` in `src/io/xeros`.
- Existing examples to copy:
  - `src/io/xeros/content/skills/slayer/DemonHunterTaskManager.java` is the kill progress pattern.
  - `src/io/xeros/content/skills/slayer/DemonSlayerMaster.java` is the task assignment and boss tier pattern.
  - `src/io/xeros/content/skills/slayer/DemonSlayerMilestoneManager.java` is the milestone messaging pattern.
- Safe ways to add new progress:
  - Add new eligible Demon Hunter bosses through `DemonSlayerMaster.BossTier`.
  - Reuse `Player.addDemonHunterXP` for Demon Hunter XP.
  - If adding persistent marks, tasks, or milestones, add a dedicated `PlayerSaveEntry` first.
- Risk level:
  - High.
- What to avoid:
  - Do not build new Demon Hunter retention loops until missing persistence is addressed.
  - Do not directly mutate Demon Marks for economy rewards without save support.
  - Do not use the AOE `fortuneXpPerKill` field as proof of Fortune XP support; current code routes it to Demon Hunter XP.

## AOE Tier Progress

- Main files:
  - `data/aoe/aoe_boss_tiers.json`
  - `data/aoe/aoe_tier_rewards.json`
  - `data/aoe/AoeZoneMapConfig.json`
  - `src/io/xeros/content/instances/aoe/AoeTierController.java`
  - `src/io/xeros/content/instances/aoe/AoeTierEvents.java`
  - `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`
  - `src/io/xeros/content/instances/aoe/AoeTierRewardsLoader.java`
  - `src/io/xeros/content/instances/aoe/AoeBossTierLoader.java`
  - `src/io/xeros/content/instances/aoe/AoeDropInterceptor.java`
  - `src/io/xeros/content/instances/aoe/AoeInstanceService.java`
  - `src/io/xeros/content/items/aoeweapons/AoeWeapons.java`
  - `src/io/xeros/content/items/aoeweapons/AoeManager.java`
  - `src/io/xeros/content/combat/death/NPCDeath.java`
- What event triggers progress:
  - Entering an AOE tier calls `AoeTierController.startTier`.
  - NPC death flow calls `AoeTierEvents.onNpcDeath(player, npc)` at the start of `NPCDeath.dropItemsFor`.
  - AOE drop banking is intercepted by `AoeDropInterceptor.awardInsideAoe`.
  - Leaving or ending the instance calls `AoeTierController.endTier`.
- What method updates progress:
  - `AoeTierController.incrementKill(Player, int)` increments per-tier kill count.
  - `AoeTierController.setUnlockedTier(Player, int)` unlocks the next tier when kill requirements are met.
  - `AoeTierProgressSaveEntry.decode` and `encode` persist unlocked tier and per-tier kill counts.
  - `AoeDropInterceptor.awardInsideAoe` banks eligible drops and records them in `AoeRewardTracker`.
- What player fields/save entries are used:
  - AOE progress is stored in player attributes with keys `aoe_unlocked_tier`, `aoe_active_tier`, `aoe_reward_tracker`, `aoe_instance`, and `aoe_kc_` plus tier number.
  - `AoeTierProgressSaveEntry` persists `aoe_unlocked_tier` and `aoe_kc_` plus tier number.
- Existing examples to copy:
  - `src/io/xeros/content/instances/aoe/AoeTierController.java` is the tier start, unlock, and end pattern.
  - `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java` is the modular save pattern.
  - `src/io/xeros/content/instances/aoe/AoeDropInterceptor.java` is the drop interception pattern.
  - `src/io/xeros/content/combat/death/NPCDeath.java` is the existing kill hook location.
- Safe ways to add new progress:
  - Use JSON-only changes in `data/aoe/aoe_boss_tiers.json` and `data/aoe/aoe_tier_rewards.json` when the reward type already exists.
  - Add end-of-run item bonus rewards through `bonusRewards`.
  - Add bank filtering through `blacklist`, `whitelist`, and `bankAllDrops`.
  - For new currency, foundry points, Wraith Essence, or real Fortune XP rewards, add Java support in the AOE reward handler first.
- Risk level:
  - Medium.
- What to avoid:
  - Do not rewrite the instance service or map builder path for reward changes.
  - Do not manually edit player attributes from unrelated systems.
  - Do not assume `fortuneXpPerKill` grants Fortune XP; `AoeTierEvents` currently calls `player.addDemonHunterXP`.

## Wraith Charges

- Main files:
  - `src/io/xeros/content/wraith/WraithCharges.java`
  - `src/io/xeros/content/items/UseItem.java`
  - `src/io/xeros/content/combat/core/AttackEntity.java`
  - `src/io/xeros/content/commands/all/Wraith.java`
  - `src/io/xeros/content/commands/all/Wraithcharges.java`
  - `src/io/xeros/model/entity/player/Player.java`
  - `src/io/xeros/model/entity/player/save/PlayerSave.java`
- What event triggers progress:
  - Using Wraith Essence on a Wraith weapon calls `WraithCharges.addChargesFromEssence`.
  - Attacking with a charged Wraith weapon calls `WraithCharges.consumeCharge`.
  - Combat blocks Wraith weapon use when the matching charge count is zero.
- What method updates progress:
  - `WraithCharges.addChargesFromEssence(Player, int, int, int)` validates the item, consumes essence, and adds capped charges.
  - `WraithCharges.setCurrentCharges(Player, int, int)` clamps and writes charge totals.
  - `WraithCharges.consumeCharge(Player, int)` subtracts one charge per attack.
  - `WraithCharges.getCurrentCharges(Player, int)` reads the current charge count.
- What player fields/save entries are used:
  - `Player.wraithScytheCharge`, `Player.wraithStaffCharge`, and `Player.wraithBowCharge`.
  - `PlayerSave.java` keys: `wraith-scythe-charge`, `wraith-staff-charge`, and `wraith-bow-charge`.
- Existing examples to copy:
  - `src/io/xeros/content/items/UseItem.java` shows item-on-item charging with Wraith Essence.
  - `src/io/xeros/content/combat/core/AttackEntity.java` shows charge consumption and zero-charge checks.
  - `src/io/xeros/content/commands/all/Wraithcharges.java` shows read-only charge display.
- Safe ways to add new progress:
  - Use `WraithCharges.addCharge` or `WraithCharges.addChargesFromEssence` for charge rewards.
  - Reward Wraith Essence as an item sink input rather than adding raw charges too often.
  - If adding a new Wraith weapon, update the weapon id set, charge getters/setters, save keys, and attack checks together.
- Risk level:
  - Medium.
- What to avoid:
  - Do not bypass charge caps.
  - Do not delete essence outside `WraithCharges.addChargesFromEssence`.
  - Do not add a new Wraith weapon without save support for its charges.

## Fire Of Exchange

- Main files:
  - `src/io/xeros/content/fireofexchange/FireOfExchange.java`
  - `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`
  - `src/io/xeros/content/bosspoints/JarsToPoints.java`
  - `src/io/xeros/content/upgrade/UpgradeMaterials.java`
  - `src/io/xeros/content/tools/ToolAugments.java`
  - `src/io/xeros/model/entity/player/Player.java`
  - `src/io/xeros/model/entity/player/save/PlayerSave.java`
- What event triggers progress:
  - Player dissolves an eligible item through Nomad's dissolver.
  - Upgrade-material rewards and burn-price mappings decide exchange value.
  - Tool augment unlock chances can run from the burn event.
- What method updates progress:
  - `FireOfExchange.exchangeItemForPoints(Player)` validates the item, deletes it, adds `foundryPoints`, grants Fortune XP, updates achievements, updates leaderboard count, records activity boss progress, and logs the burn.
  - `FireOfExchangeBurnPrice.getBurnPrice(Player, int, boolean)` resolves burn value.
  - `FireOfExchange.getExchangeShopPrice(int)` protects FOE shop rewards from dissolve loops.
- What player fields/save entries are used:
  - `Player.foundryPoints`, `Player.totalEarnedExchangePoints`, recently dissolved item lists, and normal Fortune skill XP fields.
  - `PlayerSave.java` saves `foundryPoints`.
  - Normal skill save data persists Fortune XP and level.
- Existing examples to copy:
  - `src/io/xeros/content/fireofexchange/FireOfExchange.java` is the safe item sink hook.
  - `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java` is the burn valuation pattern.
  - `src/io/xeros/content/upgrade/UpgradeInterface.java` spends foundry points and grants Fortune XP on upgrade success.
- Safe ways to add new progress:
  - Add item sink value through `FireOfExchangeBurnPrice` only after checking the item source and shop price.
  - Hook retention progress from the burn event after item deletion succeeds.
  - Use `GlobalBossActivityManager.record(ActivityType.FOE_BURN, amount)` for FOE-driven global boss progress.
- Risk level:
  - High.
- What to avoid:
  - Do not add burn values for easily farmed items without a sink review.
  - Do not grant foundry points outside controlled reward paths.
  - Do not remove the protection that blocks dissolving FOE shop reward items.

## Fortune XP

- Main files:
  - `src/io/xeros/content/fireofexchange/FireOfExchange.java`
  - `src/io/xeros/content/upgrade/UpgradeInterface.java`
  - `src/io/xeros/content/fusion/FusionSystem.java`
  - `src/io/xeros/content/minigames/wheel/WheelOfFortune.java`
  - `src/io/xeros/content/taskmaster/TaskMaster.java`
  - `src/io/xeros/content/skills/Skill.java`
  - `src/io/xeros/model/entity/npc/drops/DropManager.java`
  - `src/io/xeros/model/entity/player/save/PlayerSave.java`
- What event triggers progress:
  - Fire of Exchange burns grant Fortune XP based on burn price.
  - Upgrade successes grant Fortune XP based on upgrade cost.
  - Fusion successes grant configured Fortune XP.
  - Wheel of Fortune can grant Fortune XP based on reward burn price.
  - Task Master daily combat completion grants a small Fortune XP reward.
- What method updates progress:
  - `PlayerAssistant.addSkillXPMultiplied(amount, Skill.FORTUNE.getId(), true)` is the common XP hook.
  - `PlayerAssistant.addSkillXP(amount, Skill.FORTUNE.getId(), true)` is used by fusion.
  - `DropManager` reads Fortune level to apply drop rate modifiers.
- What player fields/save entries are used:
  - Standard `Player.playerXP` and `Player.playerLevel` skill arrays for `Skill.FORTUNE`.
  - Standard player skill save data in `PlayerSave.java`.
- Existing examples to copy:
  - `src/io/xeros/content/fireofexchange/FireOfExchange.java` grants Fortune XP from burn value.
  - `src/io/xeros/content/upgrade/UpgradeInterface.java` grants Fortune XP from upgrade cost.
  - `src/io/xeros/content/minigames/wheel/WheelOfFortune.java` grants Fortune XP from reward burn value.
- Safe ways to add new progress:
  - Grant Fortune XP from item sinks, upgrade actions, and limited task completions.
  - Use small XP amounts for repeatable early content because Fortune affects drop modifiers.
  - Prefer real Fortune XP hooks over custom fields.
- Risk level:
  - Medium.
- What to avoid:
  - Do not add large Fortune XP rewards to high-volume NPC kills.
  - Do not confuse AOE `fortuneXpPerKill` with Fortune XP until `AoeTierEvents` is corrected.
  - Do not bypass normal skill XP methods.

## Global Activity Boss Contribution

- Main files:
  - `src/io/xeros/content/activityboss/ActivityType.java`
  - `src/io/xeros/content/activityboss/GlobalBossActivityManager.java`
  - `src/io/xeros/content/activityboss/GlobalBossType.java`
  - `src/io/xeros/content/activityboss/GlobalBossContributionTracker.java`
  - `src/io/xeros/content/activityboss/GlobalBossDropHandler.java`
  - `src/io/xeros/content/activityboss/GlobalBossLootTable.java`
  - `src/io/xeros/content/activityboss/GlobalBossRewardHandler.java`
  - `src/io/xeros/content/activityboss/GlobalBossSpawnZoneManager.java`
  - `src/io/xeros/model/entity/player/Player.java`
- What event triggers progress:
  - Server-wide activities call `GlobalBossActivityManager.record`.
  - Configured thresholds spawn activity bosses through `GlobalBossActivityManager.spawn`.
  - Boss death calls `GlobalBossActivityManager.onBossDeath`.
  - Damage contribution comes from each NPC's damage-taken map.
- What method updates progress:
  - `GlobalBossActivityManager.record(ActivityType, int)` increments the server-wide total for that activity and spawns a boss when the threshold is met.
  - `GlobalBossActivityManager.onBossDeath(NPC, Player)` rewards participants and clears active state.
  - `GlobalBossContributionTracker.getContributors(NPC)` and `getTopContributors(NPC)` calculate damage contribution.
  - `GlobalBossDropHandler.rewardParticipants(NPC)` rolls loot for contributors.
  - `GlobalBossRewardHandler.handleDeath(GlobalBossType, NPC, Player)` ranks contributors and records recent contribution messages.
- What player fields/save entries are used:
  - Activity totals, active boss state, and cooldowns are static runtime maps in `GlobalBossActivityManager`.
  - `Player.bossContributions` stores the latest contribution strings in memory.
  - Not found in repo: save keys for `bossContributions` or activity boss totals/cooldowns. Searched `addBossContribution|getBossContributions|bossContribution|BossContribution|contribution` in `src/io/xeros/model/entity/player` and `src/io/xeros/content/activityboss`.
- Existing examples to copy:
  - `src/io/xeros/content/upgrade/UpgradeInterface.java` calls `GlobalBossActivityManager.record(ActivityType.UPGRADE_ITEM, 1)`.
  - `src/io/xeros/content/trails/TreasureTrails.java` calls `GlobalBossActivityManager.record(ActivityType.CLUE_CASKET, 1)`.
  - `src/io/xeros/content/fireofexchange/FireOfExchange.java` calls `GlobalBossActivityManager.record(ActivityType.FOE_BURN, amount)`.
  - `src/io/xeros/content/commands/all/Voted.java` calls `GlobalBossActivityManager.record(ActivityType.VOTE_CLAIM, voteCount)`.
  - `src/io/xeros/content/combat/pvp/Killstreak.java` calls `GlobalBossActivityManager.record(ActivityType.KILLSTREAK_10, 1)`.
- Safe ways to add new progress:
  - Add a new `ActivityType` and matching `GlobalBossType` only when the activity is server-wide and repeatable.
  - Call `GlobalBossActivityManager.record` from the real completion point, after rewards or item deletion have succeeded.
  - Keep thresholds high for high-volume activities.
  - Keep loot additions in `GlobalBossLootTable` conservative.
- Risk level:
  - Medium.
- What to avoid:
  - Do not spawn bosses directly from activity content when `GlobalBossActivityManager.record` can handle it.
  - Do not store long-term player contribution claims in `Player.bossContributions` without save support.
  - Do not use activity bosses for private account-only progression.

## World Event Participation

- Main files:
  - `src/io/xeros/content/worldevent/WorldEvent.java`
  - `src/io/xeros/content/worldevent/WorldEventContainer.java`
  - `src/io/xeros/content/worldevent/WorldEventState.java`
  - `src/io/xeros/content/worldevent/WorldEventInformation.java`
  - `src/io/xeros/content/worldevent/impl/HesporiWorldEvent.java`
  - `src/io/xeros/content/worldevent/impl/WildernessBossWorldEvent.java`
  - `src/io/xeros/content/worldevent/impl/TournamentWorldEvent.java`
  - `src/io/xeros/content/worldevent/impl/WGWorldEvent.java`
  - `src/io/xeros/content/event/eventcalendar/EventCalendar.java`
  - `src/io/xeros/content/event/eventcalendar/EventChallenge.java`
  - `src/io/xeros/content/event/eventcalendar/EventCalendarDay.java`
  - `src/io/xeros/content/event/eventcalendar/EventChallengeKey.java`
- What event triggers progress:
  - `WorldEventContainer` rotates scheduled world events.
  - Hespori world event spawns through `HesporiWorldEvent.init`.
  - Wilderness boss event spawns through `WildernessBossWorldEvent.init`.
  - Tournament world event starts through `TournamentWorldEvent.init`.
  - Old event-calendar hooks are called from many gameplay events, such as boss kills, Slayer completions, Outlast participation, and Fire of Exchange burns.
- What method updates progress:
  - `WorldEventContainer.next()` starts the next scheduled world event and announces it.
  - `WorldEvent.dispose()` and `WorldEvent.isEventCompleted()` control event cleanup.
  - `EventCalendar.progress(EventChallenge, int)` is the intended calendar progress hook.
  - Current caveat: `EventCalendar.progress` immediately returns `null`, so old event-calendar progress calls are present but inactive.
- What player fields/save entries are used:
  - `WorldEventState` saves the current world event index and ticks until the next event in runtime storage.
  - `Player.eventCalendar` holds local event calendar progress.
  - `PlayerSave.java` reads and writes `EventCalendar.SAVE_KEY` progress entries.
- Existing examples to copy:
  - `src/io/xeros/content/worldevent/impl/HesporiWorldEvent.java` is the world boss event pattern.
  - `src/io/xeros/content/worldevent/impl/WildernessBossWorldEvent.java` is the wilderness event spawn pattern.
  - `src/io/xeros/content/worldevent/impl/TournamentWorldEvent.java` is the tournament event pattern.
  - `src/io/xeros/content/tournaments/TourneyManager.java` calls event-calendar progress for Outlast participation and wins.
  - `src/io/xeros/content/bosses/wildypursuit/TheUnbearable.java` and `src/io/xeros/content/bosses/wildypursuit/FragmentOfSeren.java` call achievement, leaderboard, and event-calendar hooks for Wildy event keys.
- Safe ways to add new progress:
  - Add new scheduled events by implementing `WorldEvent` and adding them to `WorldEventContainer.WORLD_EVENT_LIST`.
  - Use existing event implementations as small, isolated patterns.
  - For retention tracking, prefer active systems such as achievements, collection logs, Task Master, battlepass, or activity boss progress until `EventCalendar.progress` is intentionally re-enabled.
- Risk level:
  - High.
- What to avoid:
  - Do not assume `EventCalendar.progress` currently records live progress.
  - Do not rewrite the world event scheduler for a single event.
  - Do not add high-value random global rewards to `WorldEventContainer.next` without an economy review.

## A. Safest Systems To Hook New Content Into

1. Achievements through `Achievements.increase` for clear one-time or milestone progress.
2. Collection logs through `CollectionLog.handleDrop` when a real item is awarded.
3. Battlepass through small `Pass.addExperience` rewards from completion events.
4. AOE tiers through `data/aoe/aoe_boss_tiers.json`, `data/aoe/aoe_tier_rewards.json`, and `AoeTierController` patterns.
5. Task Master through `Tasks` for simple NPC-name kill tasks.
6. Wraith charges through `WraithCharges` when rewarding essence or capped charges.

## B. Systems That Require Save Data Caution

1. Demon Hunter task, marks, milestones, and contract data need a repo-visible save entry before being expanded.
2. Activity boss contribution history is memory-only and should not be treated as long-term progression.
3. World event calendar progress has save support, but the progress hook is currently inactive.
4. Wraith charges use legacy `PlayerSave.java` keys, so new Wraith weapons need careful save-key planning.
5. Daily rewards already use `PlayerSaveEntry`; new daily progression should copy that pattern.

## C. Systems That Require Economy Caution

1. Boss points, because shop stock is external and point value depends on configured shop rewards.
2. Fire of Exchange and foundry points, because they are the main item sink and upgrade currency.
3. Vote points and vote panel points, because they are tied to daily retention, global boosts, and vote bosses.
4. Battlepass rewards, because `RewardList` includes high-value items.
5. Collection log rewards, because they grant one-time bundles and battlepass XP.
6. Fortune XP, because Fortune levels affect drop-rate modifiers in `DropManager`.

## D. Best Hook Path For AOE Tier Rewards

1. For JSON-only item rewards, update `data/aoe/aoe_tier_rewards.json` using existing `bonusRewards`, `endOfRunRolls`, `bankAllDrops`, `blacklist`, and `whitelist` fields.
2. For per-kill tier progress, keep using `AoeTierEvents.onNpcDeath` and `AoeTierController.incrementKill`.
3. For new currency or XP reward types, add support in `src/io/xeros/content/instances/aoe/AoeTierRewardsDef.java` and consume it from `src/io/xeros/content/instances/aoe/AoeTierController.java` or `src/io/xeros/content/instances/aoe/AoeTierEvents.java`.
4. If adding real Fortune XP, correct the current `fortuneXpPerKill` handling so it calls the Fortune skill XP method instead of `Player.addDemonHunterXP`.

## E. Best Hook Path For Starter Tasks

1. Use achievements for simple starter milestones such as first vote, first daily claim, first collection unlock, first boss kill, or first upgrade.
2. Use Task Master only for task-style objectives that fit `Tasks` and the current `TaskType` model.
3. Use battlepass XP sparingly on completed starter milestones through `Pass.addExperience`.
4. Avoid adding starter progress directly to `NPCProcess` unless the task requires a kill hook and cannot be represented by `Tasks`.

## F. Best Hook Path For Wraith Milestones

1. Reward Wraith Essence items from bosses, AOE tiers, collection logs, or global events.
2. Let players convert essence into charges through `WraithCharges.addChargesFromEssence`.
3. If account milestones need direct charge rewards, use `WraithCharges.addCharge` so caps are enforced.
4. Add achievements or collection log entries for obtaining Wraith weapons before adding more charge mechanics.
5. Add save support before introducing any new Wraith weapon charge pool.

## G. Best Hook Path For Vote And Global Boss Events

1. Keep vote progress inside `Voted.claimVotes`, `Voted.votePanel`, `Voted.rewards`, and `Voted.incrementGlobalVote`.
2. For vote-driven activity bosses, use `GlobalBossActivityManager.record(ActivityType.VOTE_CLAIM, voteCount)`.
3. For new server-wide activity bosses, add an `ActivityType`, add a matching `GlobalBossType`, then call `GlobalBossActivityManager.record` from the real completion event.
4. Reward participants through `GlobalBossDropHandler` and `GlobalBossLootTable`, not from the activity trigger itself.
5. Keep scheduled world events under `WorldEvent` implementations and `WorldEventContainer`, separate from activity-boss trigger progress.
