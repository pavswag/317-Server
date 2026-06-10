# Turmoil Reward Economy Audit

## Scope

This audit is documentation only. It reviews reward safety before adding new grind rewards and uses repo-visible systems plus the existing guides in `docs/TURMOIL_CONTENT_INDEX.md` and `docs/TURMOIL_PROGRESSION_AUDIT.md`.

The amount ranges below are conservative defaults for new content. Repeatable rewards should stay near the low end. One-time achievements, collection completions, seasonal capstones, and difficult endgame completions can use the upper end after checking the matching shop, upgrade, or sink.

## Evidence Notes

- Ordinary shop stock data is external or not present in this checkout. Not found in repo. Searched `rg --files data src docs | rg -i "(shop|shops).*\\.(json|yaml|yml|txt)$"`.
- Ordinary NPC drop table data is external or not present in this checkout. Not found in repo. Searched `rg --files data src docs | rg -i "(drop|drops).*\\.(json|yaml|yml|txt)$"`.
- Boss point tuning data is loaded by `src/io/xeros/content/bosspoints/BossPoints.java` from an external save/config directory, not from a repo file.
- Daily reward YAML data is loaded by `src/io/xeros/content/dailyrewards/DailyRewardContainer.java` from an external save/config directory, not from a repo file.
- Donation reward JSON data is loaded by `src/io/xeros/content/donationrewards/DonationReward.java` from an external save/config directory, not from a repo file.
- Because shop stock and many drop tables are not visible in the repo, every point range should be treated as a safe starting range, not a final economy balance pass.

## Reward And Currency Audits

### Boss Points

- Main files: `src/io/xeros/content/bosspoints/BossPoints.java`, `src/io/xeros/content/bosspoints/JarsToPoints.java`, `src/io/xeros/content/combat/death/NPCDeath.java`, `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/model/entity/player/Player.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`
- How players earn it: Boss deaths call `BossPoints.getPointsOnDeath` and `BossPoints.addPoints`; raids and other manual systems can call `BossPoints.addManualPoints`; boss jars can be converted through `JarsToPoints` for 500 boss points. Boss point values are loaded from external boss point config.
- How players spend it: Shop id 121 is routed through `ShopAssistant`. Shop stock and price data are external or not present in this checkout.
- Economy power, gear power, cosmetics, or convenience: Medium to high. The impact depends on shop stock. Boss points also feed Demon Hunter XP, event-calendar progress, and leaderboards through `BossPoints.addPoints`.
- Risk level: High.
- Safe amount range for starter rewards: 1-3 boss points per repeatable boss or task; 5-10 only for one-time starter milestones.
- Safe amount range for midgame rewards: 3-10 boss points per repeatable completion; 10-25 for one-time milestones.
- Safe amount range for endgame rewards: 10-25 boss points per repeatable boss, raid, or global event contribution; 25-50 for one-time account milestones.
- What not to reward too often: 100+ boss point bundles, jar-equivalent 500 point rewards, daily passive boss points, and AFK rewards that bypass actual boss kills.

### Vote Points

- Main files: `src/io/xeros/content/commands/all/Voted.java`, `src/io/xeros/content/vote_panel/VotePanelManager.java`, `src/io/xeros/content/vote_panel/VotePanelInterface.java`, `src/io/xeros/content/item/lootable/impl/VoteChest.java`, `src/io/xeros/content/polls/PollTab.java`, `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/model/entity/player/Player.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`
- How players earn it: `::voted` grants vote points, vote key points, coins, XP scroll time, bonus damage time, vote panel progress, and global vote progress. Vote chest rolls can grant extra vote points. `PollTab` can grant a small vote point bonus.
- How players spend it: Shop id 77 is routed through `ShopAssistant`; some NPC/dialogue flows also remove vote points. Shop stock and price data are external or not present in this checkout.
- Economy power, gear power, cosmetics, or convenience: Medium. Vote points are retention currency and can also trigger global vote events, WOGW bonuses, and vote bosses.
- Risk level: Medium.
- Safe amount range for starter rewards: 1-2 vote points outside the normal vote claim path.
- Safe amount range for midgame rewards: 2-5 vote points for one-time or weekly milestones.
- Safe amount range for endgame rewards: 5-10 vote points for major weekly milestones; 10-20 only for rare seasonal or top-voter rewards.
- What not to reward too often: Vote points from non-voting content, vote keys on every activity, rewards that let players ignore the actual vote loop, and direct global vote counter manipulation.

### Achievement Points

- Main files: `src/io/xeros/content/achievement/Achievements.java`, `src/io/xeros/content/achievement/AchievementHandler.java`, `src/io/xeros/content/achievement/AchievementType.java`, `src/io/xeros/content/achievement/AchievementTier.java`, `src/io/xeros/content/achievement/inter/AchieveV2.java`, `src/io/xeros/content/achievement/inter/TasksInterface.java`, `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`
- How players earn it: `Achievements.increase` completes achievement enum entries and adds the achievement's point value to `player.getAchievements().points`. Current visible achievements grant 0-4 points depending on tier.
- How players spend it: Shop id 78 spends `player.getAchievements().points` through `ShopAssistant`.
- Economy power, gear power, cosmetics, or convenience: Medium to high, depending on achievement shop stock. Achievement item rewards can also be powerful at higher tiers.
- Risk level: Medium.
- Safe amount range for starter rewards: 0-1 achievement point per achievement.
- Safe amount range for midgame rewards: 1-2 achievement points per achievement.
- Safe amount range for endgame rewards: 2-4 achievement points per achievement.
- What not to reward too often: Repeatable achievement points, achievement points outside `Achievements.increase`, and direct changes to the legacy `Player.achievementPoints` field. Future content should use the achievement handler path already saved by `PlayerSave`.

### Upgrade And Foundry Points

- Main files: `src/io/xeros/content/upgrade/UpgradeMaterials.java`, `src/io/xeros/content/upgrade/UpgradeInterface.java`, `src/io/xeros/content/fireofexchange/FireOfExchange.java`, `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`, `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/model/entity/player/Player.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`
- How players earn it: Players burn eligible items through Fire of Exchange, receive upgrade points from some raids/minigames, convert some drop items in `DropManager`, claim certain tasks, or receive item rewards that represent upgrade point value. `FireOfExchange` also awards Fortune XP from burn value.
- How players spend it: `UpgradeInterface` consumes `player.foundryPoints` for `UpgradeMaterials`; Fire of Exchange shops use shop ids 171, 172, and 173; prestige relics also consume large foundry amounts in `PrestigePerks`.
- Economy power, gear power, cosmetics, or convenience: Very high gear power. Upgrade points are a primary path into AOE weapons, Wraith items, prestige relics, and other upgraded gear.
- Risk level: High.
- Safe amount range for starter rewards: 25,000-100,000 foundry points per repeatable reward; 250,000 only for one-time starter milestones.
- Safe amount range for midgame rewards: 250,000-2,000,000 foundry points for repeatable midgame completions; 2,000,000-5,000,000 for one-time milestones.
- Safe amount range for endgame rewards: 5,000,000-25,000,000 foundry points for difficult repeatable content; 25,000,000-50,000,000 only for major one-time milestones.
- What not to reward too often: Direct 100,000,000+ foundry grants, 500,000,000+ Wraith sacrifice equivalents, 1,000,000,000+ upgrade skips, and passive rewards that outpace Fire of Exchange item sinks.

### Fire Of Exchange

- Main files: `src/io/xeros/content/fireofexchange/FireOfExchange.java`, `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`, `src/io/xeros/content/upgrade/UpgradeMaterials.java`, `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/model/entity/npc/drops/DropManager.java`
- How players earn it: This is the item sink that turns burnable items into `player.foundryPoints`. Burn prices are either explicit in `FireOfExchangeBurnPrice` or inferred from upgrade reward costs in `UpgradeMaterials`.
- How players spend it: The earned points are spent as foundry points through upgrades, Fire of Exchange shops, and prestige relic costs.
- Economy power, gear power, cosmetics, or convenience: Very high economy and gear power. This is both an item sink and the main upgrade currency generator.
- Risk level: High.
- Safe amount range for starter rewards: Reward burnable items worth 25,000-100,000 burn value total.
- Safe amount range for midgame rewards: Reward burnable items worth 250,000-1,000,000 burn value total.
- Safe amount range for endgame rewards: Reward burnable items worth 2,500,000-10,000,000 burn value total for repeatable content; higher only for one-time milestones.
- What not to reward too often: High burn-value pets, perk items, rare gear, Wraith Essence bundles, direct foundry points that bypass item deletion, and items whose Fire of Exchange value is derived from expensive upgrade outputs.

### Wraith Essence

- Main files: `src/io/xeros/content/wraith/WraithCharges.java`, `src/io/xeros/content/commands/all/Wraith.java`, `src/io/xeros/content/commands/all/Wraithcharges.java`, `src/io/xeros/content/items/UseItem.java`, `src/io/xeros/content/combat/core/AttackEntity.java`, `src/io/xeros/content/upgrade/UpgradeMaterials.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`
- How players earn it: `UpgradeMaterials` includes Wraith sacrifice entries that convert high-tier Wraith items into Wraith Essence. `src/io/xeros/content/elonmusk/Islandv2.java` also has a visible Wraith Essence reward. Ordinary drop-table sources are external or not present in this checkout.
- How players spend it: `WraithCharges.addChargesFromEssence` consumes item 26879 to charge Wraith weapons. Some item option flows also consume large Wraith Essence amounts for Wraith gear upgrades.
- Economy power, gear power, cosmetics, or convenience: High gear power and maintenance value. Wraith weapons can become unusable without charges, and Wraith gear upgrades consume large quantities.
- Risk level: High.
- Safe amount range for starter rewards: 1-3 Wraith Essence, only if the activity is meant to introduce the system.
- Safe amount range for midgame rewards: 5-15 Wraith Essence for targeted Wraith or AOE milestones.
- Safe amount range for endgame rewards: 25-75 Wraith Essence for difficult repeatable content; 100-250 only for rare one-time Wraith milestones.
- What not to reward too often: 250 Essence sacrifice-equivalent bundles, 750-1,050 Essence armor-upgrade bundles, passive daily Wraith Essence, and large Essence rewards before players reach Wraith gear.

### AOE Instance Points

- Main files: `src/io/xeros/content/items/aoeweapons/AoeWeapons.java`, `src/io/xeros/content/items/aoeweapons/AoeManager.java`, `src/io/xeros/content/items/aoeweapons/AOESystem.java`, `src/io/xeros/content/instances/aoe/AoeTierController.java`, `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/model/entity/player/Player.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`, `data/aoe/aoe_tier_rewards.json`
- How players earn it: Not found in repo. Searched `rg -n "instanceCurrency\\s*(\\+\\+|\\+=|=|-=)|aoe-points|AOE Instance points|instance points|instanceCurrency" src/io/xeros data docs`. The save field and shop spending path exist, but no active repo-visible earning path was found.
- How players spend it: `ShopAssistant` spends `player.instanceCurrency` for the AOE instance point shop. Shop stock and price data are external or not present in this checkout.
- Economy power, gear power, cosmetics, or convenience: Unknown until shop stock is reviewed. Because AOE weapons and AOE tiers are progression-critical, treat it as high risk.
- Risk level: High.
- Safe amount range for starter rewards: 1-3 AOE points if an earning path is added.
- Safe amount range for midgame rewards: 3-10 AOE points if shop prices support it.
- Safe amount range for endgame rewards: 10-25 AOE points for difficult AOE clears; 25-50 only for one-time tier unlock milestones.
- What not to reward too often: Any AOE points before confirming shop stock and prices, direct grants from non-AOE content, and high passive daily AOE point rewards.

### Fortune XP

- Main files: `src/io/xeros/content/fireofexchange/FireOfExchange.java`, `src/io/xeros/content/upgrade/UpgradeInterface.java`, `src/io/xeros/content/fusion/FusionSystem.java`, `src/io/xeros/content/minigames/wheel/WheelOfFortune.java`, `src/io/xeros/content/taskmaster/TaskMaster.java`, `src/io/xeros/content/instances/aoe/AoeTierEvents.java`, `src/io/xeros/content/skills/Skill.java`, `src/io/xeros/model/entity/npc/drops/DropManager.java`
- How players earn it: Fire of Exchange grants Fortune XP based on burn value, upgrades grant Fortune XP based on upgrade cost, fusion grants configured Fortune XP, Wheel of Fortune can grant XP from burn value, and Task Master has a small visible Fortune XP reward.
- How players spend it: Fortune is a skill, not a spendable currency. Higher Fortune levels improve drop-rate modifier checks in `DropManager`.
- Economy power, gear power, cosmetics, or convenience: Medium to high economy power because Fortune levels influence drop rates.
- Risk level: Medium.
- Safe amount range for starter rewards: 50-250 Fortune XP for repeatable tasks; 250-1,000 for one-time starter milestones.
- Safe amount range for midgame rewards: 500-2,500 Fortune XP for repeatable content; 2,500-10,000 for one-time milestones.
- Safe amount range for endgame rewards: 5,000-25,000 Fortune XP for difficult repeatable content; 25,000-100,000 for major one-time achievements.
- What not to reward too often: Millions of Fortune XP, daily passive Fortune XP, and AOE `fortuneXpPerKill` assumptions before fixing `AoeTierEvents`, because the current AOE hook grants Demon Hunter XP rather than true Fortune XP.

### Slayer Points

- Main files: `src/io/xeros/content/skills/slayer/Slayer.java`, `src/io/xeros/content/skills/slayer/SlayerMaster.java`, `src/io/xeros/content/skills/slayer/SlayerRewardsInterface.java`, `src/io/xeros/content/skills/slayer/SlayerRewardsInterfaceData.java`, `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`
- How players earn it: Slayer task completions grant points based on master, streak milestones, scroll multipliers, and donator rank bonuses. Streak bonuses become very large at high streak counts.
- How players spend it: Slayer points cancel tasks, block tasks, unlock Slayer perks, extend tasks, buy Slayer shop items, purchase XP, and buy ammo/casts.
- Economy power, gear power, cosmetics, or convenience: Medium. It is mostly progression convenience, unlocks, task control, and supplies, but it can affect boss access and Slayer efficiency.
- Risk level: Medium.
- Safe amount range for starter rewards: 1-5 Slayer points for small tasks.
- Safe amount range for midgame rewards: 5-25 Slayer points for weekly or boss Slayer content.
- Safe amount range for endgame rewards: 25-75 Slayer points for difficult Slayer milestones.
- What not to reward too often: 100+ Slayer point bundles, recurring rewards that trivialize unlock costs of 150-400 points, and rewards that bypass the task streak loop.

### Demon Marks

- Main files: `src/io/xeros/content/skills/slayer/DemonMarkRewardHandler.java`, `src/io/xeros/content/skills/slayer/DemonHunterTaskManager.java`, `src/io/xeros/content/skills/slayer/DemonHunterPerks.java`, `src/io/xeros/content/skills/slayer/DemonHunterSlayerDialogue.java`, `src/io/xeros/content/skills/slayer/DemonSlayerMaster.java`, `src/io/xeros/model/entity/player/Player.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`
- How players earn it: Demon Hunter task progress grants Demon Marks on task completion and contract completion. `DemonHunterPerks.MARK_MASTER` adds an extra mark after level 60.
- How players spend it: `DemonMarkRewardHandler.spend` exists, but `DemonMarkRewardHandler.openShop` currently says the Demon Mark reward shop is unavailable.
- Economy power, gear power, cosmetics, or convenience: Low right now because the shop is unavailable. Future risk depends on what the Demon Mark shop sells.
- Risk level: Medium.
- Safe amount range for starter rewards: 0-1 Demon Mark, only from Demon Hunter activities.
- Safe amount range for midgame rewards: 1-2 Demon Marks for Demon Hunter milestones.
- Safe amount range for endgame rewards: 2-5 Demon Marks for difficult Demon Hunter contracts or weekly objectives.
- What not to reward too often: Demon Marks from non-Demon Hunter content, rewards that ignore `MARK_MASTER`, and shop stock before the spend economy is designed.

### Prestige Points

- Main files: `src/io/xeros/content/prestige/PrestigeSkills.java`, `src/io/xeros/content/prestige/PrestigePerks.java`, `src/io/xeros/content/prestige/PrestigeInter.java`, `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/model/entity/player/Player.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`
- How players earn it: `PrestigeSkills.prestige` resets normal skills, gives donation coins, rare prayer scroll chances, possible Upgrader Cell, and grants `2,000 + currentPrestigeLevel * 100` prestige points.
- How players spend it: Prestige shop id 120 uses prestige points in `ShopAssistant`; `PrestigePerks` consumes prestige points and foundry points for account relics.
- Economy power, gear power, cosmetics, or convenience: Very high. Prestige perks include damage, XP, minigame, key, upgrade, heal, and special utility effects.
- Risk level: High.
- Safe amount range for starter rewards: 0 prestige points. Do not grant prestige points before the prestige loop.
- Safe amount range for midgame rewards: 0 prestige points. Midgame content should point players toward maxing, not bypass it.
- Safe amount range for endgame rewards: Use the existing prestige action as the main source. Outside prestige itself, keep rewards at 100-500 for rare one-time endgame milestones only after checking relic costs.
- What not to reward too often: Prestige points from dailies, vote rewards, AOE tier clears, donor perks, and any reward that skips the max-and-reset loop.

### Daily Rewards

- Main files: `src/io/xeros/content/dailyrewards/DailyRewards.java`, `src/io/xeros/content/dailyrewards/DailyRewardContainer.java`, `src/io/xeros/content/dailyrewards/DailyRewardsRecords.java`, `src/io/xeros/content/dailyrewards/DailyRewardsPlayerSaveEntry.java`, `src/io/xeros/content/achievement/Achievements.java`
- How players earn it: Players can claim one reward every 24 hours, with streak state saved per player and account-level claim protection in `DailyRewardsRecords`. Reward item lists are loaded from external daily reward YAML.
- How players spend it: Daily rewards are direct item grants, not a spendable currency.
- Economy power, gear power, cosmetics, or convenience: High because this is a high-frequency login loop. Donators with `amDonated >= 3000` receive doubled daily item amounts.
- Risk level: High.
- Safe amount range for starter rewards: One small utility item, low-tier box, 1-3 Wraith Essence, 25,000-50,000 foundry-value equivalent, or 50-250 Fortune XP if Java support is added.
- Safe amount range for midgame rewards: 1-3 mid utility items, 3-10 Wraith Essence, 50,000-250,000 foundry-value equivalent, or 250-1,000 Fortune XP.
- Safe amount range for endgame rewards: Monthly capstone only: one rare box/cosmetic, 10-25 Wraith Essence, 250,000-1,000,000 foundry-value equivalent, or 1,000-5,000 Fortune XP.
- What not to reward too often: Best-in-slot gear, donor scrolls, large foundry bundles, large Wraith Essence bundles, high boss point bundles, high vote point bundles, and rewards that become too strong when doubled by high donor rank.

### Battlepass Rewards

- Main files: `src/io/xeros/content/battlepass/Pass.java`, `src/io/xeros/content/battlepass/Rewards.java`, `src/io/xeros/content/battlepass/RewardList.java`, `src/io/xeros/GameThread.java`, `src/io/xeros/ServerStartup.java`
- How players earn it: Players earn Battlepass XP through gameplay hooks spread across boss and minigame content, then claim track rewards generated through `Rewards` and `RewardList`.
- How players spend it: Battlepass rewards are direct item claims, not a spendable currency.
- Economy power, gear power, cosmetics, or convenience: High. `RewardList` contains common item rewards and ultra rewards such as Scythe, Twisted bow, Masori pieces, donor scrolls, bank keys, and high-value boxes.
- Risk level: High.
- Safe amount range for starter rewards: Per level, small supplies, cosmetics, low boxes, or item 696 in amounts 1-5.
- Safe amount range for midgame rewards: Per level, modest boxes, utility items, Wraith Essence 3-10, or item 696 in amounts 5-25.
- Safe amount range for endgame rewards: Seasonal capstones only: rare cosmetics, rare boxes, Wraith Essence 10-50, or item 696 in amounts 25-100. Ultra gear should stay rare and intentional.
- What not to reward too often: Ultra reward list items, donor scrolls, top-tier gear, high-value bank keys, and free-track rewards that devalue boss drops.

### Collection Log Rewards

- Main files: `src/io/xeros/content/collection_log/CollectionLog.java`, `src/io/xeros/content/collection_log/CollectionRewards.java`, `src/io/xeros/model/entity/npc/drops/DropManager.java`, `src/io/xeros/content/items/aoeweapons/AoeWeapons.java`, `src/io/xeros/content/upgrade/UpgradeMaterials.java`
- How players earn it: Drops are recorded in collection log categories. When a log has all required items, `CollectionRewards.handleButton` grants the configured rewards.
- How players spend it: Collection log rewards are direct item grants, not a spendable currency.
- Economy power, gear power, cosmetics, or convenience: Medium to high. Collection rewards are one-time but can include large bundles and rare items.
- Risk level: High.
- Safe amount range for starter rewards: One-time completion: item 696 in amounts 5-20, small boxes, 1-5 Wraith Essence, or cosmetics.
- Safe amount range for midgame rewards: One-time completion: item 696 in amounts 20-100, moderate boxes, 5-25 Wraith Essence, or low-risk utility.
- Safe amount range for endgame rewards: One-time completion: item 696 in amounts 100-300, high boxes, 25-100 Wraith Essence, or cosmetics. Existing 1,000+ item 696 rewards should be treated as special category rewards, not a copy-paste default.
- What not to reward too often: Repeatable collection claims, direct best-in-slot gear, huge box stacks, large foundry-equivalent bundles, and rewards that make rare drops more valuable from the log than from gameplay.

### Donator Rewards

- Main files: `src/io/xeros/content/donationrewards/DonationRewards.java`, `src/io/xeros/content/donationrewards/DonationReward.java`, `src/io/xeros/content/donationrewards/DonationRewardsPlayerSaveEntry.java`, `src/io/xeros/content/donor/DonorVault.java`, `src/io/xeros/content/item/lootable/impl/DonoVault.java`, `src/io/xeros/content/deals/AccountBoosts.java`, `src/io/xeros/content/bosses/DonorBoss.java`, `src/io/xeros/content/bosses/DonorBoss2.java`, `src/io/xeros/content/bosses/DonorBoss3.java`, `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/model/entity/player/Right.java`
- How players earn it: Donation scrolls and donation claims add donor points or donation progress; weekly donation rewards are loaded from external donation reward JSON; donor ranks unlock donor bosses, donor zones, donor vault access, and convenience features.
- How players spend it: Donator point shops are routed through `ShopAssistant`; donor currency items can be spent as item currencies; donor systems also unlock access or utility by rank.
- Economy power, gear power, cosmetics, or convenience: Very high because donor systems can touch paid progression, rare items, bosses, zones, vault rewards, and convenience.
- Risk level: High.
- Safe amount range for starter rewards: 0 donor points from normal gameplay. Use cosmetics or convenience only.
- Safe amount range for midgame rewards: 0 donor points from normal gameplay. Donor rewards should come from donation flows, not regular content.
- Safe amount range for endgame rewards: 0 donor points from normal gameplay. Donor-exclusive gameplay can reward modest account utility or cosmetics, but should not bypass core boss or upgrade grinds.
- What not to reward too often: Donator points, donation coins, donor scrolls, donor vault entries, best-in-slot gear, paid-track items in normal content, and anything that makes non-donor progression feel obsolete.

## A. Safe Low-Risk Rewards For Early-Game Content

1. Coins in small amounts, supplies, starter consumables, and low-tier boxes.
2. 1-3 boss points only from actual starter boss kills or starter boss tasks.
3. 1-2 vote points only from voting-related milestones.
4. 25,000-100,000 foundry points or equivalent low burn-value items for one-time starter milestones.
5. 50-250 Fortune XP for explicit Fortune tutorials or Fire of Exchange starter goals.
6. 1-3 Wraith Essence only as a preview reward, not as a normal starter loop.
7. Achievement points at 0-1 per starter achievement.
8. Cosmetics, titles, and collection-log nudges that do not replace boss drops.

## B. Safe Low-Risk Rewards For AOE Tier 1 Through Tier 3

Current JSON-only support in `data/aoe/aoe_tier_rewards.json` is item-based through `bonusRewards`. Direct currency, true Fortune XP, boss points, foundry points, and AOE point rewards require Java support in `src/io/xeros/content/instances/aoe/AoeTierRewardsDef.java`, `src/io/xeros/content/instances/aoe/AoeTierController.java`, and `src/io/xeros/content/instances/aoe/AoeTierEvents.java`.

- Tier 1 safe JSON-only rewards: add `endOfRunRolls` 1 with coins in the 25,000-75,000 range, or a very small item reward already used in starter achievements.
- Tier 2 safe JSON-only rewards: keep coins near the existing 50,000-150,000 range, optionally add a rare small Wraith Essence reward of 1-2.
- Tier 3 safe JSON-only rewards: keep coins near the existing 100,000-200,000 range, optionally add a rare small Wraith Essence reward of 2-3 or a low-value utility item.
- Avoid for tiers 1-3: direct AOE weapons, direct high-tier upgrade materials, large item 696 bundles, large boxes, donor items, and direct foundry currency unless Java support and shop balance are reviewed.

## C. Rewards That Should Be Rare

1. Wraith Essence bundles above 25.
2. Foundry point rewards above 5,000,000.
3. Boss point rewards above 25.
4. Vote key rewards and top-voter prize items.
5. Battlepass ultra rewards from `src/io/xeros/content/battlepass/RewardList.java`.
6. Collection log rewards above item 696 amount 300.
7. Fortune XP rewards above 25,000.
8. Prestige points outside the actual prestige action.
9. Donor vault and donor reward items.
10. High burn-value items from `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`.

## D. Rewards That Should Never Be Added Casually

1. Best-in-slot or near-best-in-slot weapons and armor as common drops or daily rewards.
2. Direct donor points, donation coins, donor scrolls, or paid reward-track items in ordinary gameplay.
3. Direct 100,000,000+ foundry point grants.
4. Direct 500+ boss point grants outside jar conversion or carefully balanced one-time milestones.
5. Direct 750+ Wraith Essence grants that skip Wraith armor upgrade sinks.
6. Direct prestige point rewards before a player participates in the prestige loop.
7. Repeated achievement point rewards.
8. AOE instance point rewards before AOE shop stock and prices are reviewed.
9. Battlepass ultra rewards on common or repeatable free-track slots.
10. Fire of Exchange burn-price changes without checking item sinks and upgrade costs.

## E. Economy Systems Future Codex Tasks Should Avoid Touching

1. Avoid rewriting `src/io/xeros/model/shops/ShopAssistant.java`; add or tune shop behavior only after checking existing special-shop branches.
2. Avoid expanding legacy `src/io/xeros/model/entity/player/save/PlayerSave.java` unless modifying old save keys. New persistent data should use `PlayerSaveEntry` patterns when possible.
3. Avoid changing `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java` casually; burn values define the item sink economy.
4. Avoid changing `src/io/xeros/content/upgrade/UpgradeMaterials.java` costs casually; those costs anchor foundry point demand.
5. Avoid changing Wraith charge caps and essence values in `src/io/xeros/content/wraith/WraithCharges.java` without testing Wraith weapon upkeep.
6. Avoid changing prestige relic costs in `src/io/xeros/content/prestige/PrestigePerks.java` without a separate prestige balance pass.
7. Avoid changing donor reward flows in `src/io/xeros/content/donationrewards/` or donor vault rewards without owner review.
8. Avoid hardcoding ordinary drops in `src/io/xeros/content/combat/death/NPCDeath.java` or `src/io/xeros/model/entity/npc/drops/DropManager.java`.
9. Avoid adding unsupported fields to `data/aoe/aoe_tier_rewards.json` and assuming they work.
10. Avoid copying the largest existing collection log or battlepass rewards into new low-tier content.

## F. Recommended Reward Philosophy For Turmoil

1. Keep the backbone: bosses drop items, Fire of Exchange sinks duplicates, upgrades consume foundry points, achievements and collection logs create milestones, and dailies/votes/events bring players back.
2. Reward daily login with useful nudges, not best-in-slot progression.
3. Reward boss grinding with deterministic progress in small point amounts, but keep rare drops valuable.
4. Reward AOE tiers with modest item rewards first, then add Java-supported currencies only after defining exact sinks.
5. Reward Wraith progression carefully because Essence is both a maintenance resource and an upgrade material.
6. Reward Fortune XP through item sinks, upgrades, and explicit milestones, not large passive grants.
7. Keep donor rewards mostly cosmetic, convenient, or access-based so the core server economy still matters.
8. Use one-time milestones for larger rewards and repeatable content for small steady progress.
9. When shop stock or drop tables are external, document the uncertainty before changing reward rates.
10. Future Codex tasks should start by copying the closest existing pattern, making the smallest data or enum change possible, and listing test steps for earning, spending, saving, and logging out.
