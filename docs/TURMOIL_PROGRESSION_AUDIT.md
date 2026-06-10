# Turmoil Progression Audit

This audit uses `docs/TURMOIL_CONTENT_GUIDE.md` for implementation rules and `docs/TURMOIL_CONTENT_INDEX.md` as the main content inventory. It focuses on how the current server moves a player from starter gear into long-term bossing, upgrades, item sinks, daily loops, voting, events, collection logs, achievements, AOE tiers, and donor progression.

The server backbone already exists. The safest progression work is adding rewards, tasks, achievements, collection targets, upgrade entries, event rotations, and clear milestone loops through existing systems.

## Repo Evidence Notes

- Ordinary drop table data files: Not found in repo. Searched `rg --files data src docs | rg -i "(drop|drops).*\\.(json|yaml|yml|txt)$"`.
- Ordinary shop stock data files: Not found in repo. Searched `rg --files data src docs | rg -i "(shop|shops).*\\.(json|yaml|yml|txt)$"`.
- Starter questline after tutorial: Not found in repo. Searched `rg -n "starter|Start|new player|newplayer|tutorial|starter pack|beginner|Starter" src/io/xeros docs data`.
- Dedicated Wraith milestone tree: Not found in repo. Searched `rg -n "WRAITH_ESSENCE|Wraith Essence|26879|33431|33433|33434|wraith" src/io/xeros data docs`.
- Daily rewards content data: Not found in repo. Searched `rg -n "daily_rewards|DailyRewards|TaskMaster|Battlepass|battlepass|VotePanel|vote panel|weekly" src/io/xeros data docs`. The daily rewards loader exists, but the reward container data is outside this checkout.
- Fusion command files: Not found in repo. Searched `rg --files src/io/xeros/content/commands | rg -i "fusion|fortune|spin"`.
- Fortune skill package and spin-table package: Not found in repo. Searched `rg --files src/io/xeros | rg -i "fortune|foturne|spin"`.
- Sol Heredit-specific file: Not found in repo. Searched `rg --files src/io/xeros | rg -i "heredit"`.

## 1. Starter Progression

- Main files: `src/io/xeros/content/items/Starter.java`, `src/io/xeros/content/tutorial/TutorialDialogue.java`, `src/io/xeros/content/tutorial/ModeSelection.java`, `src/io/xeros/model/entity/player/DialogueHandler.java`, `src/io/xeros/model/entity/player/Player.java`
- Current purpose: Creates the first player loop: choose XP mode, receive starter gear, receive utility supplies, get a starter Slayer task, and learn where core home systems are located.
- Current player grind value: Strong first-session utility. New players receive starter AOE weapons, coins, supplies, skilling tools, runes, a ring of wealth, XP scroll support, and an initial Slayer task.
- Weak spots: The tutorial points at shops, dailies, chests, Slayer, boss points, restricted shops, voting, donor shop, upgrade table, Nomad, and altar, but there is no found guided post-tutorial starter boss ladder. The player can start many systems, but the repo does not show a curated "kill these first five bosses" progression path.
- Expansion ideas: Add starter achievements, a first-hour Task Master chain, first AOE tier objectives, first upgrade objective, first collection log objective, and first vote claim objective. Keep the starter pack stable to avoid save/economy surprises.
- Safe files to copy patterns from: `src/io/xeros/content/items/Starter.java`, `src/io/xeros/content/tutorial/TutorialDialogue.java`, `src/io/xeros/content/taskmaster/Tasks.java`, `src/io/xeros/content/achievement/Achievements.java`, `src/io/xeros/content/dialogue/DialogueBuilder.java`
- Risk level: Medium

## 2. Early Game Bosses

- Main files: `src/io/xeros/content/bosses/obor/OborNPC.java`, `src/io/xeros/content/bosses/bryophyta/Bryophyta.java`, `src/io/xeros/content/bosses/bryophyta/BryophytaNPC.java`, `src/io/xeros/content/minigames/barrows/RewardList.java`, `src/io/xeros/content/minigames/barrows/BrotherEvent.java`, `src/io/xeros/content/taskmaster/Tasks.java`
- Current purpose: Gives newer players combat targets before high-tier raids and late-game bosses. The content index also lists God Wars, Giant Mole, Barrows, Sarachnis, Kraken, KBD, and basic wilderness bosses as reusable early and early-mid combat targets.
- Current player grind value: Useful for initial uniques, collection log progress, boss points, starter Task Master combat tasks, and Slayer-adjacent grind.
- Weak spots: Ordinary boss drop data is not present in the repo, so reward quality cannot be fully audited from this checkout. The early boss path is broad but not clearly sequenced in docs or configs.
- Expansion ideas: Add a "starter boss board" that sends players through Obor, Bryophyta, Barrows, God Wars, and KBD. Add low-risk achievements and Task Master entries rather than new combat code.
- Safe files to copy patterns from: `src/io/xeros/content/bosses/obor/OborNPC.java`, `src/io/xeros/content/bosses/bryophyta/Bryophyta.java`, `src/io/xeros/content/minigames/barrows/RewardList.java`, `src/io/xeros/content/taskmaster/Tasks.java`, `src/io/xeros/content/achievement/Achievements.java`
- Risk level: Low

## 3. Mid Game Bosses

- Main files: `src/io/xeros/content/bosses/Cerberus.java`, `src/io/xeros/content/bosses/hydra/AlchemicalHydra.java`, `src/io/xeros/content/bosses/zulrah/Zulrah.java`, `src/io/xeros/content/bosses/Vorkath.java`, `src/io/xeros/content/bosses/grotesqueguardians/GrotesqueGuardianNpc.java`, `src/io/xeros/content/bosses/grotesqueguardians/GrotesqueInstance.java`, `src/io/xeros/content/bosses/hespori/Hespori.java`, `src/io/xeros/content/taskmaster/Tasks.java`
- Current purpose: Bridges early account gearing into late and endgame gear loops. These bosses support rare-drop hunting, Slayer tasks, achievements, collection logs, and boss point farming.
- Current player grind value: Strong account progression if drops and collection logs are tuned. Task Master already includes many mid-tier combat tasks such as Kraken, Zulrah, Sarachnis, Hydra, Cerberus, and KQ across multiple difficulty bands.
- Weak spots: The repo does not expose ordinary drop data files, so the audit cannot confirm whether these bosses feed upgrade materials, Wraith Essence, AOE weapons, or currencies consistently.
- Expansion ideas: Tie mid-game bosses into upgrade chains, Fire of Exchange burn values, Task Master weekly rotations, and collection log reward milestones. Add drop-table changes through the existing drop data source when available, not through death handlers.
- Safe files to copy patterns from: `src/io/xeros/content/bosses/Cerberus.java`, `src/io/xeros/content/bosses/hydra/AlchemicalHydra.java`, `src/io/xeros/content/bosses/Vorkath.java`, `src/io/xeros/content/taskmaster/Tasks.java`, `src/io/xeros/content/collection_log/CollectionLog.java`
- Risk level: Medium

## 4. Late Game Bosses

- Main files: `src/io/xeros/content/bosses/nightmare/Nightmare.java`, `src/io/xeros/content/bosses/nightmare/NightmareInstance.java`, `src/io/xeros/content/bosses/nex/NexNPC.java`, `src/io/xeros/content/bosses/toa/ToaInstance.java`, `src/io/xeros/content/minigames/raids/Raids.java`, `src/io/xeros/content/minigames/tob/TobContainer.java`, `src/io/xeros/content/minigames/TOA/TombsOfAmascutContainer.java`
- Current purpose: Provides difficult bosses and raids for rare drops, prestige goals, collection logs, group play, MVP-style competition, and high-tier upgrade ingredients.
- Current player grind value: Strong, especially where raid and boss completions feed tasks, battlepass XP, collection logs, and public rare-drop broadcasts.
- Weak spots: Late game has many systems, but the repo does not show a unified gear-score ladder or milestone board that explains which late boss unlocks which upgrade chain. Rewards may feel scattered unless task, log, shop, and upgrade loops are aligned.
- Expansion ideas: Add late-game milestones for raid completions, Nex and Nightmare KC, rare upgrade paths, boss point thresholds, and collection log completion tiers.
- Safe files to copy patterns from: `src/io/xeros/content/bosses/nightmare/Nightmare.java`, `src/io/xeros/content/bosses/nex/NexNPC.java`, `src/io/xeros/content/minigames/raids/Raids.java`, `src/io/xeros/content/minigames/tob/TobContainer.java`, `src/io/xeros/content/minigames/TOA/TombsOfAmascutContainer.java`
- Risk level: Medium

## 5. Endgame Bosses

- Main files: `src/io/xeros/content/bosses/Solak.java`, `src/io/xeros/content/bosses/dukesucellus/DukeSucellus.java`, `src/io/xeros/content/bosses/leviathan/TheLeviathan.java`, `src/io/xeros/content/bosses/vardorvis/Vardorvis.java`, `src/io/xeros/content/bosses/whisperer/TheWhisperer.java`, `src/io/xeros/content/bosses/Sol.java`, `src/io/xeros/content/upgrade/UpgradeMaterials.java`
- Current purpose: Gives maxed or near-maxed players aspirational targets that can carry rare drops, pets, collection log slots, upgrade ingredients, and economy sinks.
- Current player grind value: High if their unique drops feed upgrades, Fire of Exchange, achievements, and collection logs. The content index shows many modern endgame bosses are already present as content candidates.
- Weak spots: There is no repo-visible endgame reward index mapping each boss to its upgrade purpose. If drops are external, future tasks need a separate source of truth before tuning the endgame economy.
- Expansion ideas: Build a written endgame ladder: each boss should feed one of four loops: best-in-slot upgrades, Wraith item progression, AOE tier progression, or cosmetic prestige. Add achievements and collection rewards before rewriting boss mechanics.
- Safe files to copy patterns from: `src/io/xeros/content/bosses/dukesucellus/DukeSucellus.java`, `src/io/xeros/content/bosses/leviathan/TheLeviathan.java`, `src/io/xeros/content/bosses/vardorvis/Vardorvis.java`, `src/io/xeros/content/bosses/whisperer/TheWhisperer.java`, `src/io/xeros/content/upgrade/UpgradeMaterials.java`
- Risk level: High

## 6. AOE Boss and Minigame Progression

- Main files: `src/io/xeros/content/instances/aoe/AoeTierController.java`, `src/io/xeros/content/instances/aoe/AoeBossTierLoader.java`, `src/io/xeros/content/instances/aoe/AoeTierRewardsLoader.java`, `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`, `src/io/xeros/content/instances/aoe/AoeInstanceService.java`, `src/io/xeros/content/instances/aoe/AoeDropInterceptor.java`, `data/aoe/aoe_boss_tiers.json`, `data/aoe/aoe_tier_rewards.json`
- Current purpose: Provides a tiered AOE instance ladder with saved unlock state, per-tier kill count, per-tier bosses, map definitions, minions, multipliers, and end-of-run rewards.
- Current player grind value: Very strong backbone. Players unlock higher tiers by getting kills, tiers scale from Unicow through God Wars, Dagannoth Kings, Kalphite Queen, King Black Dragon, and Chaos Elemental, and each tier can grant AOE reward tracking, drop multipliers, and Fortune XP.
- Weak spots: `data/aoe/aoe_tier_rewards.json` is shallow. Tier 1 has no bonus reward and tiers 2 through 9 mostly use coin rewards. The AOE ladder needs more unique sinks, upgrade materials, collection targets, and weekly rotations to feel like a full progression track.
- Expansion ideas: Add tier-specific upgrade materials, Wraith Essence, Fortune spin tickets, boss point bundles, cosmetic shards, collection log entries, weekly AOE hazards, and leaderboard-style completion goals.
- Safe files to copy patterns from: `src/io/xeros/content/instances/aoe/AoeTierController.java`, `src/io/xeros/content/instances/aoe/AoeTierRewardsLoader.java`, `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`, `data/aoe/aoe_boss_tiers.json`, `data/aoe/aoe_tier_rewards.json`
- Risk level: Medium

## 7. Wraith Item Progression

- Main files: `src/io/xeros/content/wraith/WraithCharges.java`, `src/io/xeros/content/commands/all/Wraith.java`, `src/io/xeros/content/commands/all/Wraithcharges.java`, `src/io/xeros/content/upgrade/UpgradeMaterials.java`, `src/io/xeros/content/items/UseItem.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`, `src/io/xeros/model/entity/player/Player.java`
- Current purpose: Wraith weapons consume Wraith Essence as charges. The upgrade table also includes Wraith weapon outputs and Wraith sacrifice entries that turn high-tier items into Wraith Essence.
- Current player grind value: Good item sink and late/endgame weapon maintenance loop. Players must keep Wraith weapons charged and can use sacrifice upgrades to convert powerful items into Wraith Essence.
- Weak spots: Dedicated Wraith milestone tree: Not found in repo. Searched `rg -n "WRAITH_ESSENCE|Wraith Essence|26879|33431|33433|33434|wraith" src/io/xeros data docs`. Charge caps and essence values are hardcoded in `WraithCharges.java`, and Wraith save data currently lives in legacy `PlayerSave.java`.
- Expansion ideas: Add Wraith Essence sources to existing reward systems, add collection log milestones for Wraith weapons, add Wraith charge achievements, and eventually move future Wraith progression data into `PlayerSaveEntry` rather than expanding legacy save parsing.
- Safe files to copy patterns from: `src/io/xeros/content/wraith/WraithCharges.java`, `src/io/xeros/content/upgrade/UpgradeMaterials.java`, `src/io/xeros/model/entity/player/save/PlayerSaveEntry.java`, `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`
- Risk level: Medium

## 8. Upgrade System

- Main files: `src/io/xeros/content/upgrade/UpgradeMaterials.java`, `src/io/xeros/content/upgrade/UpgradeInterface.java`, `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`, `src/io/xeros/content/collection_log/CollectionLog.java`
- Current purpose: Converts base items plus upgrade points into stronger weapons, armor, accessories, and misc gear. The enum carries required item, reward item, cost, success rate, XP, type, and rare flag.
- Current player grind value: Very high. This is one of the main reasons to grind bosses, burn duplicate items, chase currency, and revisit old content.
- Weak spots: The upgrade list is large and powerful, so poor tuning can inflate gear or devalue older bosses. Some progression chains are clear, especially AOE weapons and Wraith items, but the full player-facing ladder is not documented in one place.
- Expansion ideas: Add small, reversible upgrade entries first. Prefer intermediate bridge items, cosmetic upgrades, and sink upgrades over immediate best-in-slot jumps. Tie new upgrade outputs into collection log and Fire of Exchange values.
- Safe files to copy patterns from: `src/io/xeros/content/upgrade/UpgradeMaterials.java`, `src/io/xeros/content/upgrade/UpgradeInterface.java`, `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`, `src/io/xeros/content/collection_log/CollectionRewards.java`
- Risk level: Medium

## 9. Fusion System

- Main files: `src/io/xeros/content/fusion/FusionSystem.java`, `src/io/xeros/content/fusion/FusionMaterials.java`, `src/io/xeros/content/fusion/FusionTypes.java`, `src/io/xeros/content/achievement/AchievementHandler.java`
- Current purpose: Provides a second item-combining path separate from the upgrade interface. The content index identifies Fusion as a high-tier progression and item sink system that can grant Fortune XP.
- Current player grind value: Good for long-tail item hunting because fusion recipes can require multiple rare or upgraded items.
- Weak spots: Fusion creates high economy pressure. New recipes can accidentally invalidate upgrade paths, Fire of Exchange values, or boss drops. Fusion command files were not found in repo. Searched `rg --files src/io/xeros/content/commands | rg -i "fusion|fortune|spin"`.
- Expansion ideas: Add fusion entries only after checking UpgradeMaterials, Fire of Exchange burn values, collection logs, and existing rare-item supply. Use fusion for late-game sidegrades, cosmetics, and prestige variants.
- Safe files to copy patterns from: `src/io/xeros/content/fusion/FusionSystem.java`, `src/io/xeros/content/fusion/FusionMaterials.java`, `src/io/xeros/content/upgrade/UpgradeMaterials.java`, `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`
- Risk level: High

## 10. Fire of Exchange

- Main files: `src/io/xeros/content/fireofexchange/FireOfExchange.java`, `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`, `src/io/xeros/content/upgrade/UpgradeMaterials.java`, `src/io/xeros/model/entity/player/Player.java`
- Current purpose: Burns items for upgrade points and Fortune XP, creating an item sink and a way to turn duplicate drops into account progression.
- Current player grind value: Very high. It keeps old drops useful, supports upgrade-point demand, rewards duplicate rare drops, and adds a reason to farm bosses after completing gear slots.
- Weak spots: Burn price tuning is economy-critical. `FireOfExchangeBurnPrice.java` derives some burn prices from upgrade costs and also contains explicit item handling, so changes can ripple through upgrade pacing.
- Expansion ideas: Add clear burn tiers for new content, make Wraith/AOE/raid duplicates worth sinking, and use public broadcasts only for notable burns. Keep pricing conservative and review shop buy prices when available.
- Safe files to copy patterns from: `src/io/xeros/content/fireofexchange/FireOfExchange.java`, `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`, `src/io/xeros/content/upgrade/UpgradeMaterials.java`
- Risk level: High

## 11. Fortune System

- Main files: `src/io/xeros/content/skills/Skill.java`, `src/io/xeros/content/fireofexchange/FireOfExchange.java`, `src/io/xeros/content/upgrade/UpgradeInterface.java`, `src/io/xeros/content/minigames/wheel/WheelOfFortune.java`, `src/io/xeros/content/minigames/wheel/WheelOfFortuneGame.java`, `src/io/xeros/content/games/WheelOfFortuneGame.java`, `src/io/xeros/net/WheelOfFortuneEndListener.java`
- Current purpose: Adds a long-term progression skill and spin/reward layer that can be fed by upgrades, Fire of Exchange, AOE tiers, and reward systems.
- Current player grind value: Strong when Fortune XP is connected to item sinks and upgrades. It gives players a reason to burn items and perform upgrades even after gear progression slows down.
- Weak spots: A dedicated Fortune skill package and spin-table package were not found in repo. Searched `rg --files src/io/xeros | rg -i "fortune|foturne|spin"`. The current visible implementation connects Fortune XP through Fire of Exchange and upgrades, while "fortune" file names mostly appear as Wheel of Fortune minigame files.
- Expansion ideas: Add Fortune XP to AOE tier rewards, Wraith sacrifices, weekly boss tasks, and rare exchange events. Add milestone benefits that are useful but not mandatory for combat balance.
- Safe files to copy patterns from: `src/io/xeros/content/fireofexchange/FireOfExchange.java`, `src/io/xeros/content/upgrade/UpgradeInterface.java`, `src/io/xeros/content/minigames/wheel/WheelOfFortune.java`, `src/io/xeros/content/minigames/wheel/WheelOfFortuneGame.java`
- Risk level: Medium

## 12. Slayer and Demon Hunter Progression

- Main files: `src/io/xeros/content/skills/slayer/Slayer.java`, `src/io/xeros/content/skills/slayer/Task.java`, `src/io/xeros/content/skills/slayer/SlayerMaster.java`, `src/io/xeros/content/skills/slayer/SlayerRewardsInterfaceData.java`, `src/io/xeros/content/skills/slayer/KonarSlayer.java`, `src/io/xeros/content/skills/slayer/DemonSlayerMaster.java`, `src/io/xeros/content/skills/slayer/DemonHunterTaskManager.java`, `src/io/xeros/content/skills/slayer/DemonSlayerMilestoneManager.java`, `src/io/xeros/content/skills/slayer/DemonSlayerLeaderboardManager.java`
- Current purpose: Slayer assigns repeatable combat tasks, rewards Slayer points, unlocks perks/extensions, and supports streak rewards. Demon Hunter layers boss-tier tasks, Demon Hunter XP, Demon Marks, milestones, perks, contracts, and weekly leaderboard messages.
- Current player grind value: Very high. Slayer starts early through the starter kit, scales into boss tasks, and Demon Hunter adds weekly competition and long-term boss progression.
- Weak spots: Slayer and Demon Hunter are mature systems with many save fields and hooks. Adding content without understanding task matching names can break task tracking. Demon Hunter rewards currently appear stronger as messages, XP, marks, and milestones than as a broad reward shop loop.
- Expansion ideas: Add Demon Hunter milestones for AOE tiers and late bosses, weekly Demon Mark shop rotations, collection log tasks, and starter Slayer achievements. Add tasks by extending existing task pools and reward handlers, not by rewriting Slayer core.
- Safe files to copy patterns from: `src/io/xeros/content/skills/slayer/Task.java`, `src/io/xeros/content/skills/slayer/SlayerMaster.java`, `src/io/xeros/content/skills/slayer/DemonSlayerMaster.java`, `src/io/xeros/content/skills/slayer/DemonSlayerMilestoneManager.java`
- Risk level: High

## 13. Prestige System

- Main files: `src/io/xeros/content/prestige/PrestigeSkills.java`, `src/io/xeros/content/prestige/PrestigePerks.java`, `src/io/xeros/model/entity/player/Player.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`
- Current purpose: Gives maxed or progression-heavy accounts another account-growth path through prestige points, skill resets or milestones, and perks.
- Current player grind value: Strong long-term retention if perks are meaningful and balanced. Prestige can make skilling relevant to combat progression and keep maxed players active.
- Weak spots: Prestige touches account state and save data. Poor perk tuning can become mandatory or power creep. The content index shows the system exists, but this audit did not find a current prestige roadmap tied to AOE, boss points, Wraith, or Fortune.
- Expansion ideas: Add prestige cosmetics, small utility perks, title rewards, and non-combat convenience. Avoid combat power jumps until the endgame economy is mapped.
- Safe files to copy patterns from: `src/io/xeros/content/prestige/PrestigeSkills.java`, `src/io/xeros/content/prestige/PrestigePerks.java`, `src/io/xeros/model/entity/player/save/PlayerSaveEntry.java`
- Risk level: High

## 14. Boss Points

- Main files: `src/io/xeros/model/entity/player/Player.java`, `src/io/xeros/model/entity/npc/drops/DropManager.java`, `src/io/xeros/content/combat/death/NPCDeath.java`, `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/content/taskmaster/Tasks.java`
- Current purpose: Boss points reward repeated boss kills and create a shop/currency loop around bossing.
- Current player grind value: Good broad retention loop because any supported boss can advance a spendable currency. It is especially useful for players who are dry on rare drops.
- Weak spots: The audit could not inspect ordinary shop stock data. Boss point reward pacing and shop quality cannot be fully validated from repo files alone.
- Expansion ideas: Add milestone achievements at boss point thresholds, weekly boss point bonus events, boss point sinks for AOE/Wraith/upgrade support items, and collection log bonus tokens.
- Safe files to copy patterns from: `src/io/xeros/model/entity/npc/drops/DropManager.java`, `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/content/achievement/Achievements.java`, `src/io/xeros/content/taskmaster/Tasks.java`
- Risk level: Medium

## 15. Vote Rewards

- Main files: `src/io/xeros/content/commands/all/Voted.java`, `src/io/xeros/content/vote_panel/VotePanelManager.java`, `src/io/xeros/content/vote_panel/VotePanelInterface.java`, `src/io/xeros/content/vote_panel/VoteUser.java`, `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/Configuration.java`
- Current purpose: Rewards voting through points, vote keys, vote panel streaks, top-voter rewards, bonus XP, and drop boost.
- Current player grind value: Very high daily login value. The vote panel has reward IDs, streak UI, top-three weekly support, XP boost, and drop-rate boost.
- Weak spots: Shop stock data is not in this repo, so the value of the vote shop cannot be validated here. If rewards are too weak, voting becomes a chore; if too strong, it bypasses boss progression.
- Expansion ideas: Add vote streak milestones that feed AOE attempts, Fortune spins, Wraith Essence, or cosmetic boxes. Add weekly vote goals that trigger global activity bosses or Discord announcements.
- Safe files to copy patterns from: `src/io/xeros/content/vote_panel/VotePanelManager.java`, `src/io/xeros/content/vote_panel/VotePanelInterface.java`, `src/io/xeros/content/commands/all/Voted.java`, `src/io/xeros/model/shops/ShopAssistant.java`
- Risk level: Medium

## 16. Daily Rewards

- Main files: `src/io/xeros/content/dailyrewards/DailyRewards.java`, `src/io/xeros/content/dailyrewards/DailyRewardContainer.java`, `src/io/xeros/content/dailyrewards/DailyRewardsPlayerSaveEntry.java`, `src/io/xeros/content/dailyrewards/DailyRewardsRecords.java`, `src/io/xeros/content/dailyrewards/DailyRewardsDialogue.java`
- Current purpose: Provides a 24-hour claim loop with streak progress and account-level claim records.
- Current player grind value: Good login anchor when the reward track is relevant. It can support monthly retention and event reward cycles.
- Weak spots: Daily rewards content data is not present in the checkout. Not found in repo. Searched `rg -n "daily_rewards|DailyRewards|TaskMaster|Battlepass|battlepass|VotePanel|vote panel|weekly" src/io/xeros data docs`. The loader exists and references external reward data, so tuning cannot be audited here.
- Expansion ideas: Add monthly tracks with AOE tier tickets, Fortune XP, Wraith Essence, vote keys, upgrade points, and event cosmetics. Keep daily rewards account-limited and avoid best-in-slot direct rewards.
- Safe files to copy patterns from: `src/io/xeros/content/dailyrewards/DailyRewards.java`, `src/io/xeros/content/dailyrewards/DailyRewardsPlayerSaveEntry.java`, `src/io/xeros/content/dailyrewards/DailyRewardContainer.java`
- Risk level: Medium

## 17. Task Master Daily and Weekly Systems

- Main files: `src/io/xeros/content/taskmaster/TaskMaster.java`, `src/io/xeros/content/taskmaster/Tasks.java`, `src/io/xeros/content/taskmaster/TaskMasterKills.java`, `src/io/xeros/content/taskmaster/TaskDifficulty.java`, `src/io/xeros/content/taskmaster/TaskType.java`, `src/io/xeros/content/commands/all/TaskManager.java`
- Current purpose: Assigns short-term and weekly tasks across combat and skilling, tracks progress, and pays rewards. The enum already covers many bosses across easy, medium, hard, elite, and weekly variants.
- Current player grind value: Very high. It creates daily and weekly reasons to log in, kill specific bosses, skill, and complete raids/minigames.
- Weak spots: The current reward pattern uses broad rewards and fixed assignment logic. It does not appear to have AOE-tier-specific tasks, Wraith tasks, Fire of Exchange tasks, collection tasks, or vote tasks yet.
- Expansion ideas: Add tasks for AOE tier clears, Wraith charging, item burning, upgrades, vote streaks, Demon Hunter tasks, and collection log claims. Reuse task types and only add new hooks where matching cannot be represented by existing tracking.
- Safe files to copy patterns from: `src/io/xeros/content/taskmaster/Tasks.java`, `src/io/xeros/content/taskmaster/TaskMaster.java`, `src/io/xeros/content/taskmaster/TaskMasterKills.java`
- Risk level: Medium

## 18. Battlepass

- Main files: `src/io/xeros/content/battlepass/Pass.java`, `src/io/xeros/content/battlepass/Rewards.java`, `src/io/xeros/content/battlepass/RewardList.java`, `src/io/xeros/GameThread.java`, `src/io/xeros/ServerStartup.java`
- Current purpose: Adds seasonal progression and reward claiming on top of normal gameplay. Many boss and minigame files import `Pass`, which indicates battlepass XP hooks are spread across content.
- Current player grind value: High when reward tiers refresh and tasks naturally overlap with bossing, raids, AOE, voting, and dailies.
- Weak spots: Battlepass can become invisible if rewards are static or if XP sources are only passive. It also has broad hooks, so changes need testing across boss and minigame completions.
- Expansion ideas: Add seasonal AOE tier objectives, Wraith charge objectives, Fire of Exchange burn objectives, vote milestones, and global boss participation tasks.
- Safe files to copy patterns from: `src/io/xeros/content/battlepass/Pass.java`, `src/io/xeros/content/battlepass/Rewards.java`, `src/io/xeros/content/battlepass/RewardList.java`
- Risk level: Medium

## 19. Collection Logs

- Main files: `src/io/xeros/content/collection_log/CollectionLog.java`, `src/io/xeros/content/collection_log/CollectionRewards.java`, `src/io/xeros/model/entity/npc/drops/DropManager.java`, `src/io/xeros/content/upgrade/UpgradeInterface.java`
- Current purpose: Tracks boss, wilderness, raid, minigame, other, pets, upgrades, AOE weapons, and special collection categories listed in the content index.
- Current player grind value: Very high for completionists. Collection logs make rare drops valuable even when a player does not need the item for gear.
- Weak spots: The log has many categories, but the audit did not find a clear progression reward ladder for partial completions, category completions, AOE logs, Wraith logs, or seasonal logs.
- Expansion ideas: Add collection milestones that award cosmetics, titles, Fortune XP, boss points, and small AOE or Wraith utility. Avoid adding overpowered combat rewards for log completion.
- Safe files to copy patterns from: `src/io/xeros/content/collection_log/CollectionLog.java`, `src/io/xeros/content/collection_log/CollectionRewards.java`, `src/io/xeros/model/entity/npc/drops/DropManager.java`
- Risk level: Medium

## 20. Achievements

- Main files: `src/io/xeros/content/achievement/Achievements.java`, `src/io/xeros/content/achievement/AchievementHandler.java`, `src/io/xeros/content/achievement/AchievementType.java`, `src/io/xeros/content/achievement/AchievementTier.java`, `src/io/xeros/content/achievement/inter/AchieveV2.java`, `src/io/xeros/content/achievement/inter/TasksInterface.java`, `src/io/xeros/content/achievement_diary/AchievementDiaryManager.java`
- Current purpose: Provides account milestones, achievement points, task interfaces, and achievement diary progression.
- Current player grind value: Strong if achievements are visible and tied to meaningful loops. Achievement points are another reason to do content outside best-in-slot gear.
- Weak spots: Achievements can lag behind new systems. AOE tiers, Wraith charges, Fortune, Fire of Exchange, Task Master, Battlepass, and global events need explicit achievement coverage to become retention loops.
- Expansion ideas: Add achievement tiers for first upgrade, 10 upgrades, first burn, 100m burned, first AOE tier unlock, tier 9 unlock, Wraith charge milestones, vote streaks, and collection log completions.
- Safe files to copy patterns from: `src/io/xeros/content/achievement/Achievements.java`, `src/io/xeros/content/achievement/AchievementHandler.java`, `src/io/xeros/content/achievement/inter/AchieveV2.java`, `src/io/xeros/content/achievement_diary/AchievementDiaryManager.java`
- Risk level: Medium

## 21. Shops and Currencies

- Main files: `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/model/definitions/ShopDef.java`, `src/io/xeros/model/shops/Shop.java`, `src/io/xeros/model/shops/ShopItem.java`, `src/io/xeros/content/skills/slayer/SlayerRewardsInterface.java`, `src/io/xeros/content/fireofexchange/FireOfExchange.java`, `src/io/xeros/content/vote_panel/VotePanelManager.java`, `src/io/xeros/content/donationrewards/DonationRewards.java`, `src/io/xeros/content/donor/DonorVault.java`
- Current purpose: Supports coins, platinum tokens, PKP, boss points, achievement points, raid points, vote points, blood currency, Pest Control points, donator currency, loyalty, exchange/foundry points, seasonal points, prestige points, AOE points, Mage Arena points, Shayzien points, Fortune spins, and item currencies listed in the content index.
- Current player grind value: Very high because currencies turn dry streaks and daily activity into deterministic progress.
- Weak spots: Ordinary shop stock data files are not in the repo. Not found in repo. Searched `rg --files data src docs | rg -i "(shop|shops).*\\.(json|yaml|yml|txt)$"`. Currency balance cannot be fully audited without stock and price data.
- Expansion ideas: Add transparent currency roles: boss points for dry protection, vote points for utility, upgrade points for gear progression, Wraith Essence for maintenance, Fortune for account growth, and seasonal points for cosmetics.
- Safe files to copy patterns from: `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/model/shops/Shop.java`, `src/io/xeros/model/definitions/ShopDef.java`, `src/io/xeros/content/skills/slayer/SlayerRewardsInterface.java`, `src/io/xeros/content/fireofexchange/FireOfExchange.java`
- Risk level: High

## 22. Global Bosses and Activity Bosses

- Main files: `src/io/xeros/content/activityboss/GlobalBossActivityManager.java`, `src/io/xeros/content/activityboss/GlobalBossType.java`, `src/io/xeros/content/activityboss/GlobalBossLootTable.java`, `src/io/xeros/content/activityboss/GlobalBossRewardHandler.java`, `src/io/xeros/content/activityboss/GlobalBossContributionTracker.java`, `src/io/xeros/content/activityboss/GlobalBossAnnouncer.java`, `src/io/xeros/content/activityboss/Groot.java`, `src/io/xeros/content/globalboss/KBD.java`, `src/io/xeros/content/globalboss/KQ.java`, `src/io/xeros/content/globalboss/NEX.java`
- Current purpose: Creates server-wide participation goals with announcements, contribution tracking, reward handling, and globally recognizable bosses.
- Current player grind value: High community retention value. Global bosses create activity spikes, Discord-friendly moments, and reasons to log in on a schedule.
- Weak spots: Activity bosses need strong contribution rewards and predictable spawn triggers. If loot is too generic or contribution is unclear, players will ignore them after novelty fades.
- Expansion ideas: Trigger bosses from vote milestones, Fire of Exchange burns, AOE tier clears, Demon Hunter weekly goals, and Discord events. Add contribution tiers, rare cosmetics, and weekly leaderboards.
- Safe files to copy patterns from: `src/io/xeros/content/activityboss/GlobalBossActivityManager.java`, `src/io/xeros/content/activityboss/GlobalBossRewardHandler.java`, `src/io/xeros/content/activityboss/GlobalBossLootTable.java`, `src/io/xeros/content/activityboss/GlobalBossContributionTracker.java`
- Risk level: Medium

## 23. World Events

- Main files: `src/io/xeros/content/worldevent/WorldEvent.java`, `src/io/xeros/content/worldevent/WorldEventContainer.java`, `src/io/xeros/content/worldevent/WorldEventInformation.java`, `src/io/xeros/content/worldevent/WorldEventState.java`, `src/io/xeros/content/worldevent/impl/HesporiWorldEvent.java`, `src/io/xeros/content/worldevent/impl/WildernessBossWorldEvent.java`, `src/io/xeros/content/worldevent/impl/TournamentWorldEvent.java`, `src/io/xeros/content/worldevent/impl/WGWorldEvent.java`
- Current purpose: Provides rotating server events such as Hespori, wilderness boss, tournament, and WeaponGames world events.
- Current player grind value: Good if event windows are visible, rewards are time-sensitive, and they feed collection logs, achievements, seasonal currencies, or battlepass XP.
- Weak spots: World events can be fragile because they involve global state, timing, and shared player participation. The audit found event classes, but not a full reward calendar inside the docs.
- Expansion ideas: Add a 4-week event rotation with clear reward themes: AOE week, Wraith week, Slayer week, and Vote/Discord week. Keep event implementation inside existing world event classes.
- Safe files to copy patterns from: `src/io/xeros/content/worldevent/WorldEvent.java`, `src/io/xeros/content/worldevent/WorldEventContainer.java`, `src/io/xeros/content/worldevent/impl/HesporiWorldEvent.java`, `src/io/xeros/content/worldevent/impl/WildernessBossWorldEvent.java`
- Risk level: High

## 24. Donator Progression

- Main files: `src/io/xeros/content/bosses/DonorBoss.java`, `src/io/xeros/content/bosses/DonorBoss2.java`, `src/io/xeros/content/bosses/DonorBoss3.java`, `src/io/xeros/content/donor/DonorVault.java`, `src/io/xeros/content/donor/DonoSlayerInstances.java`, `src/io/xeros/content/donationrewards/DonationRewards.java`, `src/io/xeros/content/deals/AccountBoosts.java`, `src/io/xeros/content/commands/donator/Donatorzone.java`, `src/io/xeros/content/commands/all/Yell.java`, `src/io/xeros/model/entity/player/Right.java`
- Current purpose: Adds donor zones, donor bosses, donation rewards, donor vaults, special commands, donor Slayer instances, weekly donation rewards, and rank-based quality-of-life.
- Current player grind value: Good for monetization and convenience if balanced with non-donor progression. Donor bonuses can encourage active supporters to keep playing.
- Weak spots: Donator systems are high-trust. Overpowered donor rewards can undermine bossing, voting, collection logs, and item sinks. Donation reward data and shop stock are not fully visible in this audit.
- Expansion ideas: Favor cosmetics, convenience, extra instances, reduced friction, and donor-only side objectives over direct best-in-slot gear. Add donor participation rewards that still require gameplay.
- Safe files to copy patterns from: `src/io/xeros/content/donor/DonorVault.java`, `src/io/xeros/content/donor/DonoSlayerInstances.java`, `src/io/xeros/content/donationrewards/DonationRewards.java`, `src/io/xeros/content/deals/AccountBoosts.java`
- Risk level: High

## 25. Long-Term Retention Loops

- Main files: `src/io/xeros/content/taskmaster/Tasks.java`, `src/io/xeros/content/battlepass/Pass.java`, `src/io/xeros/content/dailyrewards/DailyRewards.java`, `src/io/xeros/content/vote_panel/VotePanelManager.java`, `src/io/xeros/content/collection_log/CollectionLog.java`, `src/io/xeros/content/achievement/Achievements.java`, `src/io/xeros/content/instances/aoe/AoeTierController.java`, `src/io/xeros/content/fireofexchange/FireOfExchange.java`, `src/io/xeros/content/worldevent/WorldEventContainer.java`, `src/io/xeros/content/activityboss/GlobalBossActivityManager.java`
- Current purpose: Keeps players logging in daily, grinding bosses, upgrading gear, voting, completing logs, chasing rare drops, burning duplicate items, progressing through AOE tiers, joining events, and competing with others.
- Current player grind value: The backbone is strong. The server has daily rewards, vote streaks, Task Master, Battlepass, achievements, collection logs, upgrades, Fire of Exchange, AOE tiers, Demon Hunter leaderboards, global bosses, and world events.
- Weak spots: The systems are present but not always visibly connected. The biggest retention issue is likely content density and reward alignment, not missing infrastructure.
- Expansion ideas: Build cross-system loops: AOE clears feed Fortune and Wraith Essence, bosses feed upgrade chains and collection logs, voting triggers global bosses, dailies push players into current events, and Battlepass ties weekly content together.
- Safe files to copy patterns from: `docs/TURMOIL_CONTENT_GUIDE.md`, `docs/TURMOIL_CONTENT_INDEX.md`, `src/io/xeros/content/taskmaster/Tasks.java`, `src/io/xeros/content/achievement/Achievements.java`, `data/aoe/aoe_tier_rewards.json`
- Risk level: Medium

## A. Top 10 Progression Gaps

1. AOE tier rewards are too shallow for a major progression track. `data/aoe/aoe_tier_rewards.json` mostly contains coins and tier 1 has no bonus reward.
2. Starter progression lacks a repo-visible guided boss ladder after tutorial and starter kit.
3. Wraith progression is mostly charge maintenance and sacrifice upgrades; a dedicated Wraith milestone tree was not found.
4. Drop table and shop stock data are not present in the repo, making economy tuning hard for future agents.
5. Task Master does not appear to include AOE, Wraith, Fire of Exchange, vote, or collection-log-specific tasks.
6. Collection logs exist but need stronger partial-completion and category-completion rewards.
7. Achievements likely lag behind newer systems such as AOE tiers, Wraith charges, Fire of Exchange, Fortune, and activity bosses.
8. Global/activity boss loops need clearer spawn triggers tied to voting, Discord, AOE, world events, and contribution goals.
9. Daily rewards and battlepass are present but need stronger seasonal reward themes connected to current progression systems.
10. Endgame bosses are numerous, but the repo does not contain a single visible endgame reward ladder tying bosses to upgrades, Wraith, AOE, Fortune, and collection goals.

## B. Top 10 Safest Content Updates

1. Expand `data/aoe/aoe_tier_rewards.json` with modest tier-specific rewards such as upgrade points, Wraith Essence, Fortune XP support, and cosmetic shards.
2. Add AOE tier unlock and tier clear achievements in `src/io/xeros/content/achievement/Achievements.java`.
3. Add starter boss tasks to `src/io/xeros/content/taskmaster/Tasks.java` using existing boss names already tracked by Task Master.
4. Add Wraith charge and Wraith Essence achievements using `src/io/xeros/content/wraith/WraithCharges.java` as the source pattern.
5. Add Fire of Exchange burn milestones through existing achievement patterns.
6. Add Task Master weekly tasks for existing raids and bosses already present in `src/io/xeros/content/taskmaster/Tasks.java`.
7. Add collection reward milestones in `src/io/xeros/content/collection_log/CollectionRewards.java` without changing drop mechanics.
8. Add vote streak milestone rewards through `src/io/xeros/content/vote_panel/VotePanelManager.java` and `src/io/xeros/content/vote_panel/VotePanelInterface.java`.
9. Add global boss reward tuning inside `src/io/xeros/content/activityboss/GlobalBossLootTable.java` and `src/io/xeros/content/activityboss/GlobalBossRewardHandler.java`.
10. Add documentation for an endgame boss ladder in `docs/` before changing boss drops or combat behavior.

## C. Top 5 High-Impact Updates

1. AOE reward overhaul: strengthens the most obvious custom progression backbone and gives players a clear grind from tier 1 to tier 9.
2. Starter-to-midgame roadmap: turns the first session into a guided path through starter Slayer, Obor, Bryophyta, Barrows, God Wars, upgrades, and AOE tier 1.
3. Cross-system weekly rotation: Battlepass, Task Master, vote panel, and world events should all point at the same weekly theme.
4. Wraith progression expansion: Wraith Essence sources, Wraith charge milestones, Wraith collection logs, and Wraith-specific tasks would create a long-term item sink.
5. Global boss trigger loop: voting, Discord events, AOE milestones, and Fire of Exchange burns can trigger activity bosses that pull the server together.

## D. Systems To Avoid Touching For Now

- Avoid rewriting `src/io/xeros/model/entity/npc/drops/DropManager.java`; add ordinary drops through the established drop table source when available.
- Avoid expanding `src/io/xeros/model/entity/player/save/PlayerSave.java` unless modifying old save keys. Use `src/io/xeros/model/entity/player/save/PlayerSaveEntry.java` for new persistent data.
- Avoid adding isolated boss mechanics to `src/io/xeros/model/entity/npc/NPCHandler.java` or `src/io/xeros/model/entity/npc/NPCProcess.java`.
- Avoid changing ordinary shop economics until the external shop stock data is available.
- Avoid major edits to `src/io/xeros/content/skills/slayer/Slayer.java` unless a Slayer-specific task requires core behavior changes.
- Avoid broad changes to `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java` without reviewing upgrade costs and shop buy prices.
- Avoid donor reward power increases before non-donor progression gaps are filled.
- Avoid rewriting battlepass hooks; add content through `src/io/xeros/content/battlepass/Rewards.java`, `src/io/xeros/content/battlepass/RewardList.java`, and existing completion hooks.

## E. Best First Coding Task

Add a small AOE retention pack:

- Add tier 1 through tier 3 bonus rewards in `data/aoe/aoe_tier_rewards.json`.
- Keep rewards modest: upgrade points, Wraith Essence, Fortune XP support, and low-tier boxes rather than best-in-slot items.
- Add matching achievements in `src/io/xeros/content/achievement/Achievements.java` only if the current achievement handler already has a matching completion hook.
- Why this first: AOE tiers already have saved progress through `src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java`, config loading through `src/io/xeros/content/instances/aoe/AoeTierRewardsLoader.java`, and a clear player grind.

## F. Best Second Coding Task

Add starter and early-game Task Master objectives:

- Add daily or weekly tasks for Obor, Bryophyta, Barrows, God Wars, KBD, and AOE tier 1 using `src/io/xeros/content/taskmaster/Tasks.java`.
- Use only bosses/minigames that already have tracking hooks.
- Pair the tasks with small rewards and achievement progress.
- Why second: It improves early retention without needing new boss mechanics, new save formats, or drop system rewrites.

## G. Suggested 4-Week Update Roadmap

Week 1:
- Publish the starter-to-midgame roadmap in docs.
- Add modest AOE tier 1 through tier 3 reward improvements.
- Add first-session achievements for starter kit, first Slayer task, first boss kill, first upgrade, and first vote.

Week 2:
- Add Task Master tasks for starter bosses, AOE tier 1, Barrows, and God Wars.
- Add daily or weekly Battlepass objectives that overlap with those tasks.
- Add collection log reward milestones for easy boss categories.

Week 3:
- Expand Wraith progression with Wraith Essence sources, Wraith charge milestones, and Wraith collection goals.
- Add Fire of Exchange burn achievements and Fortune XP milestones.
- Tune AOE tier 4 through tier 6 rewards around mid-game upgrades.

Week 4:
- Create a weekly world event theme that ties vote goals, global boss spawns, activity boss rewards, Discord announcements, AOE tiers, and Battlepass objectives together.
- Add endgame boss milestone achievements for Nex, Nightmare, raids, Solak, Duke, Leviathan, Vardorvis, Whisperer, and Sol Heredit.
- Review donor rewards for convenience and participation without bypassing the new non-donor progression loops.
