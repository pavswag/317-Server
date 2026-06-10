# Codex Task Templates For Turmoil

Use these templates when asking Codex to work on Turmoil RSPS. Each template is designed to keep future work grounded in existing repo patterns, player-save safety, reward economy caution, and small reversible changes.

General rules for every template:
- Use repo-relative paths only.
- Search before coding.
- Copy the closest working local pattern.
- Do not invent IDs, commands, save keys, item IDs, NPC IDs, object IDs, shop IDs, or button IDs.
- Do not rewrite core systems.
- Do not modify generated files, build output, runtime saves, jars, archives, or unrelated files.
- Explain economy, combat, save, and rollback risk when relevant.

## 1. Learn A System Template

Use this when Codex should map a system before coding.

```text
Analyze the [SYSTEM NAME] system in Turmoil before any code changes.

This is documentation/analysis only. Do not modify code or config files.

Required docs to read first:
- docs/TURMOIL_CONTENT_GUIDE.md
- docs/TURMOIL_CONTENT_INDEX.md
- docs/TURMOIL_SERVER_METHOD_FLOW_MAP.md
- docs/TURMOIL_CORE_SYSTEMS_RISK_MAP.md
- docs/TURMOIL_DATA_CONFIG_LOADER_MAP.md
- docs/TURMOIL_IDS_AND_REGISTRATION_MAP.md

Files to inspect first:
- Start with the files named for [SYSTEM NAME].
- Search `src/io/xeros/content` for matching package, class, enum, method, command, and config names.
- Search `data` for matching JSON, YAML, TXT, and config files.
- Search `docs` for existing maps that mention the system.

Files to avoid:
- src/io/xeros/model/entity/player/Player.java
- src/io/xeros/model/entity/player/save/PlayerSave.java
- src/io/xeros/model/entity/npc/NPCHandler.java
- src/io/xeros/model/entity/npc/NPCProcess.java
- src/io/xeros/content/combat/death/NPCDeath.java
- src/io/xeros/model/entity/npc/drops/DropManager.java
- src/io/xeros/model/shops/ShopAssistant.java

Step-by-step instructions:
1. List the docs and files inspected.
2. Identify the entry points, main managers, data/config files, save data, commands, interactions, rewards, and tests.
3. Map the method flow from player input or server tick to output/reward.
4. Identify safe extension points and fragile areas.
5. Identify similar systems that can be copied.
6. Say "Not found in repo" for anything missing and list searched terms.

Test requirements:
- No tests are required for analysis-only work.
- Verify the response uses repo-relative paths only.
- Verify no fake files or methods are invented.

Required final response format:
- Summary
- Files inspected
- Main flow
- Data/save/config files
- Safe extension points
- Risk areas
- Similar patterns to copy
- Open questions or missing pieces
```

## 2. Small Bug Fix Template

Use this for fixing one bug safely.

```text
Fix this Turmoil bug safely: [BUG DESCRIPTION]

Required docs to read first:
- docs/TURMOIL_SERVER_METHOD_FLOW_MAP.md
- docs/TURMOIL_CORE_SYSTEMS_RISK_MAP.md
- docs/TURMOIL_TESTING_AND_DEBUG_TOOL_MAP.md
- docs/TURMOIL_PLAYER_STATE_SAVE_AND_PROGRESS_DATA_MAP.md if saves or player state are involved
- docs/TURMOIL_COMBAT_AND_REWARD_PIPELINE_DEEP_DIVE.md if combat, drops, NPC death, rewards, Slayer, achievements, or tasks are involved

Files to inspect first:
- Search for the exact method, command, item, NPC, object, interface, save key, or error text.
- Inspect the owning system file first.
- Inspect the closest working similar flow before editing.
- Inspect tests under `src/test` if the system has tests.

Files to avoid:
- src/io/xeros/model/entity/player/Player.java
- src/io/xeros/model/entity/player/save/PlayerSave.java
- src/io/xeros/model/entity/npc/NPCHandler.java
- src/io/xeros/model/entity/npc/NPCProcess.java
- src/io/xeros/content/combat/death/NPCDeath.java
- src/io/xeros/model/entity/npc/drops/DropManager.java
- src/io/xeros/model/shops/ShopAssistant.java

Step-by-step instructions:
1. Reproduce or trace the bug first.
2. Identify the exact flow and owning class.
3. Search for similar fixed systems.
4. Make the smallest root-cause fix.
5. Add a narrow guard only in the owning system if needed.
6. Do not add broad try/catch blocks that hide broken state.
7. Avoid unrelated cleanup and formatting.

Test requirements:
- Run the smallest compile/test command available for the repo.
- Run focused tests if they exist under `src/test`.
- Provide manual in-game test steps.
- If persistence is involved, test save and reload.
- If rewards are involved, verify no duplicate rewards are granted.

Required final response format:
- Summary
- Files changed
- Root cause
- Systems touched
- Risk level
- Tests run
- Manual in-game test steps
- Rollback notes
- Follow-up recommendations
```

## 3. Content Addition Template

Use this for adding one new content update.

```text
Add one Turmoil content update: [CONTENT DESCRIPTION]

Required docs to read first:
- docs/TURMOIL_CONTENT_GUIDE.md
- docs/TURMOIL_CONTENT_INDEX.md
- docs/TURMOIL_CONTENT_CREATION_PATTERN_BOOK.md
- docs/TURMOIL_PROGRESSION_AUDIT.md
- docs/TURMOIL_REWARD_ECONOMY_AUDIT.md
- docs/TURMOIL_IDS_AND_REGISTRATION_MAP.md
- docs/TURMOIL_TESTING_AND_DEBUG_TOOL_MAP.md

Files to inspect first:
- Search for the closest existing content pattern in `src/io/xeros/content`.
- Search for matching IDs, commands, save keys, and reward hooks.
- Inspect any data/config files that already control the content type.
- Inspect the relevant docs for progression and economy risk.

Files to avoid:
- src/io/xeros/content/combat/death/NPCDeath.java unless adding a narrow existing hook requires it
- src/io/xeros/model/entity/npc/drops/DropManager.java unless updating an existing supported drop pipeline
- src/io/xeros/model/entity/player/save/PlayerSave.java
- src/io/xeros/model/entity/player/Player.java
- src/io/xeros/model/shops/ShopAssistant.java

Step-by-step instructions:
1. Decide whether the update is data-only, enum-only, manager-only, or Java-supported.
2. Identify the closest existing local pattern.
3. Confirm IDs and save keys are not invented.
4. Add the smallest content changes through existing configs, enums, managers, or hooks.
5. Hook achievements, Task Master, collection log, battlepass, boss points, Slayer, AOE, Wraith, or Fire of Exchange only when needed.
6. Keep rewards modest unless the update is explicitly endgame and owner-reviewed.

Test requirements:
- Verify compile/build for Java changes.
- Verify JSON/YAML parsing for data changes.
- Test the content entry point, reward path, repeat behavior, and failure path.
- Verify no duplicate rewards, stuck state, or unsafe save behavior.

Required final response format:
- Summary
- Files changed
- Systems touched
- Content pattern copied
- Risk level
- Economy/save/combat notes
- Tests run
- Manual in-game test steps
- Rollback notes
- Follow-up recommendations
```

## 4. Data-Only Update Template

Use this for JSON, YAML, TXT, or config-only updates.

```text
Make this Turmoil data-only update: [DATA UPDATE DESCRIPTION]

Do not modify Java code unless the existing loader does not support the requested fields. If Java support is required, stop and explain what is missing first.

Required docs to read first:
- docs/TURMOIL_DATA_CONFIG_LOADER_MAP.md
- docs/TURMOIL_IDS_AND_REGISTRATION_MAP.md
- docs/TURMOIL_REWARD_ECONOMY_AUDIT.md if rewards, shops, drops, points, or currencies are involved
- docs/TURMOIL_AOE_SYSTEM_DEEP_DIVE.md if AOE data is involved

Files to inspect first:
- The target data file named by the request.
- The loader class named by docs/TURMOIL_DATA_CONFIG_LOADER_MAP.md.
- Existing nearby data entries in the same file.
- ID definitions and registration points from docs/TURMOIL_IDS_AND_REGISTRATION_MAP.md.

Files to avoid:
- src/io/xeros/model/entity/player/save/PlayerSave.java
- src/io/xeros/model/entity/player/Player.java
- src/io/xeros/content/combat/death/NPCDeath.java
- src/io/xeros/model/entity/npc/drops/DropManager.java
- src/io/xeros/model/shops/ShopAssistant.java

Step-by-step instructions:
1. Confirm the existing loader supports the requested fields.
2. Copy the closest valid data entry.
3. Preserve formatting and schema.
4. Do not invent IDs or unsupported fields.
5. If an ID source is external or missing, say so before editing.
6. Make only the requested data changes.

Test requirements:
- Verify the data file parses.
- Run the server startup validation if available.
- If no parser command exists, inspect loader expectations manually and state that.
- Test one happy path and one invalid/locked path in game if possible.

Required final response format:
- Summary
- Files changed
- Loader used
- Schema fields changed
- Risk level
- Tests run
- Manual in-game test steps
- Rollback notes
- Follow-up recommendations
```

## 5. New Boss Template

Use this for creating a boss using an existing pattern.

```text
Add a new Turmoil boss: [BOSS DESCRIPTION]

Required docs to read first:
- docs/TURMOIL_CONTENT_GUIDE.md
- docs/TURMOIL_CONTENT_INDEX.md
- docs/TURMOIL_CONTENT_CREATION_PATTERN_BOOK.md
- docs/TURMOIL_COMBAT_AND_REWARD_PIPELINE_DEEP_DIVE.md
- docs/TURMOIL_INSTANCE_AND_MINIGAME_LIFECYCLE_MAP.md if instanced
- docs/TURMOIL_REWARD_ECONOMY_AUDIT.md
- docs/TURMOIL_IDS_AND_REGISTRATION_MAP.md

Files to inspect first:
- src/io/xeros/content/bosses
- src/io/xeros/content/combat/npc/NPCAutoAttackBuilder.java
- src/io/xeros/content/combat/npc/NPCAutoAttack.java
- src/io/xeros/content/combat/death/NPCDeath.java
- src/io/xeros/model/entity/npc/drops/DropManager.java
- src/io/xeros/content/bosspoints/BossPoints.java
- src/io/xeros/content/collection_log/CollectionLog.java
- src/io/xeros/content/achievement/Achievements.java
- src/io/xeros/content/taskmaster/Tasks.java

Files to avoid:
- src/io/xeros/model/entity/npc/NPCHandler.java
- src/io/xeros/model/entity/npc/NPCProcess.java
- src/io/xeros/content/combat/death/NPCDeath.java except for the smallest existing hook pattern
- src/io/xeros/model/entity/npc/drops/DropManager.java except for supported drop registration behavior
- src/io/xeros/model/entity/player/Player.java
- src/io/xeros/model/entity/player/save/PlayerSave.java

Step-by-step instructions:
1. Pick the closest existing boss pattern from `src/io/xeros/content/bosses`.
2. Confirm the NPC ID exists and is not invented.
3. Decide whether the boss is global, instanced, wilderness, Slayer, AOE, or regular.
4. Implement mechanics through boss class patterns and `NPCAutoAttackBuilder`.
5. Add drops through the existing drop table path, not hardcoded death rewards.
6. Add boss points, achievements, Task Master, collection log, battlepass, or announcements only through existing hooks.
7. Keep mechanics local to the boss class where possible.

Test requirements:
- Verify compile/build.
- Spawn or access the boss locally using safe tools from docs/TURMOIL_TESTING_AND_DEBUG_TOOL_MAP.md.
- Test spawn, attack, special attack, death, drop, collection log, boss points, Slayer/task progress, and cleanup.
- Test logout/death behavior if instanced.

Required final response format:
- Summary
- Files changed
- Boss pattern copied
- Systems touched
- Reward/economy risk
- Combat/instance risk
- Tests run
- Manual in-game test steps
- Rollback notes
- Follow-up recommendations
```

## 6. New Task Master Task Template

Use this for adding daily or weekly Task Master tasks.

```text
Add a new Task Master task: [TASK DESCRIPTION]

Required docs to read first:
- docs/TURMOIL_CONTENT_CREATION_PATTERN_BOOK.md
- docs/TURMOIL_PROGRESS_HOOK_MAP.md
- docs/TURMOIL_PLAYER_STATE_SAVE_AND_PROGRESS_DATA_MAP.md
- docs/TURMOIL_REWARD_ECONOMY_AUDIT.md
- docs/TURMOIL_TESTING_AND_DEBUG_TOOL_MAP.md

Files to inspect first:
- src/io/xeros/content/taskmaster/Tasks.java
- src/io/xeros/content/taskmaster/TaskMaster.java
- src/io/xeros/content/taskmaster/TaskMasterKills.java
- Existing hooks that call Task Master progress for similar activity.

Files to avoid:
- src/io/xeros/model/entity/player/save/PlayerSave.java
- src/io/xeros/model/entity/player/Player.java
- src/io/xeros/content/combat/death/NPCDeath.java unless the existing task hook requires a narrow addition

Step-by-step instructions:
1. Identify the closest existing task type in `Tasks.java`.
2. Confirm whether the task is daily or weekly.
3. Reuse existing `TaskMaster.trackActivity` flow where possible.
4. Add rewards conservatively and explain economy risk.
5. Do not change `TaskMasterKills` JSON fields unless explicitly migrating saves.

Test requirements:
- Verify compile/build.
- Generate or assign tasks locally.
- Progress the new task once.
- Complete the task and claim reward.
- Log out and back in to verify Task Master JSON state persists.

Required final response format:
- Summary
- Files changed
- Task pattern copied
- Systems touched
- Reward risk
- Tests run
- Manual in-game test steps
- Rollback notes
- Follow-up recommendations
```

## 7. New Achievement Template

Use this for adding achievement progress.

```text
Add a new Turmoil achievement: [ACHIEVEMENT DESCRIPTION]

Required docs to read first:
- docs/TURMOIL_PROGRESS_HOOK_MAP.md
- docs/TURMOIL_PLAYER_STATE_SAVE_AND_PROGRESS_DATA_MAP.md
- docs/TURMOIL_COMBAT_AND_REWARD_PIPELINE_DEEP_DIVE.md if combat-related
- docs/TURMOIL_CONTENT_CREATION_PATTERN_BOOK.md
- docs/TURMOIL_IDS_AND_REGISTRATION_MAP.md
- docs/TURMOIL_REWARD_ECONOMY_AUDIT.md

Files to inspect first:
- src/io/xeros/content/achievement/Achievements.java
- src/io/xeros/content/achievement/AchievementHandler.java
- src/io/xeros/content/achievement/AchievementType.java
- Existing files that call achievement progress for a similar action.

Files to avoid:
- src/io/xeros/model/entity/player/save/PlayerSave.java
- src/io/xeros/model/entity/player/Player.java
- src/io/xeros/content/combat/death/NPCDeath.java unless adding a narrow existing progress hook is the correct owner

Step-by-step instructions:
1. Pick the correct achievement tier and type.
2. Copy the nearest existing achievement definition.
3. Add progress at the owning event hook, not in a random downstream reward path.
4. Confirm the achievement save name will not collide or rename an old achievement.
5. Keep rewards aligned with existing achievement rewards.

Test requirements:
- Verify compile/build.
- Trigger the progress event.
- Confirm progress increments once.
- Confirm completion and claim behavior.
- Log out and back in to verify achievement state persists.

Required final response format:
- Summary
- Files changed
- Achievement pattern copied
- Hook used
- Save/economy risk
- Tests run
- Manual in-game test steps
- Rollback notes
- Follow-up recommendations
```

## 8. AOE Update Template

Use this for AOE tier, reward, and instance updates.

```text
Make this AOE update: [AOE UPDATE DESCRIPTION]

Required docs to read first:
- docs/TURMOIL_AOE_SYSTEM_DEEP_DIVE.md
- docs/TURMOIL_DATA_CONFIG_LOADER_MAP.md
- docs/TURMOIL_REWARD_ECONOMY_AUDIT.md
- docs/TURMOIL_PLAYER_STATE_SAVE_AND_PROGRESS_DATA_MAP.md
- docs/TURMOIL_INSTANCE_AND_MINIGAME_LIFECYCLE_MAP.md
- docs/TURMOIL_TESTING_AND_DEBUG_TOOL_MAP.md

Files to inspect first:
- data/aoe/aoe_boss_tiers.json
- data/aoe/aoe_tier_rewards.json
- data/aoe/AoeZoneMapConfig.json
- src/io/xeros/content/instances/aoe/AoeTierController.java
- src/io/xeros/content/instances/aoe/AoeTierRewardsLoader.java
- src/io/xeros/content/instances/aoe/AoeBossTierLoader.java
- src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java
- src/io/xeros/content/instances/aoe/AoeInstanceService.java
- src/io/xeros/content/instances/aoe/AoeDropInterceptor.java

Files to avoid:
- src/io/xeros/model/entity/player/save/PlayerSave.java
- src/io/xeros/model/entity/player/Player.java
- src/io/xeros/model/entity/npc/NPCHandler.java
- src/io/xeros/model/entity/npc/NPCProcess.java
- src/io/xeros/content/combat/death/NPCDeath.java

Step-by-step instructions:
1. Decide whether the update is JSON-only or requires Java reward support.
2. Confirm supported JSON fields before editing data.
3. Copy an existing tier or reward entry.
4. Preserve tier IDs and progression key compatibility.
5. Add Java support only if the requested reward type is not supported by the loader.
6. Keep active instance state out of player saves.

Test requirements:
- Verify JSON parses.
- Verify compile/build if Java changed.
- Test AOE tier 1 through tier 3 if tier progression or rewards changed.
- Test entry, kill count, reward grant, unlock, logout, and cleanup.
- Verify `aoe_unlocked_tier`, `aoe_kc_#`, and `aoe-points` behavior if persistence changed.

Required final response format:
- Summary
- Files changed
- JSON-only or Java-supported
- Systems touched
- Reward/save/instance risk
- Tests run
- Manual in-game test steps
- Rollback notes
- Follow-up recommendations
```

## 9. Wraith Update Template

Use this for Wraith charges, essence, achievements, and rewards.

```text
Make this Wraith update: [WRAITH UPDATE DESCRIPTION]

Required docs to read first:
- docs/TURMOIL_PLAYER_STATE_SAVE_AND_PROGRESS_DATA_MAP.md
- docs/TURMOIL_REWARD_ECONOMY_AUDIT.md
- docs/TURMOIL_COMBAT_AND_REWARD_PIPELINE_DEEP_DIVE.md
- docs/TURMOIL_PROGRESS_HOOK_MAP.md
- docs/TURMOIL_CONTENT_CREATION_PATTERN_BOOK.md

Files to inspect first:
- src/io/xeros/content/wraith/WraithCharges.java
- src/io/xeros/model/entity/player/Player.java for existing Wraith charge getters/setters only
- src/io/xeros/model/entity/player/save/PlayerSave.java for existing Wraith charge keys only
- Existing combat or item-use hooks that call Wraith behavior.
- src/test/java/io/xeros/content/wraith/WraithChargesTest.java

Files to avoid:
- src/io/xeros/model/entity/player/save/PlayerSave.java unless maintaining existing Wraith keys
- src/io/xeros/model/entity/player/Player.java unless existing getters/setters are insufficient
- src/io/xeros/content/combat/death/NPCDeath.java
- src/io/xeros/model/entity/npc/drops/DropManager.java

Step-by-step instructions:
1. Identify whether the change affects charges, essence, rewards, combat consumption, achievements, or tasks.
2. Use `WraithCharges` methods for charge math and clamping.
3. Do not bypass cap or essence consumption logic.
4. Add achievements or tasks through their owning progress hooks.
5. If adding a new persistent Wraith field, prefer a new `PlayerSaveEntry` instead of legacy save keys.

Test requirements:
- Run Wraith unit tests if available.
- Verify compile/build.
- Test charging, consuming, cap behavior, insufficient essence, and save/load.
- Test achievement or task progress if added.

Required final response format:
- Summary
- Files changed
- Wraith pattern copied
- Systems touched
- Save/economy/combat risk
- Tests run
- Manual in-game test steps
- Rollback notes
- Follow-up recommendations
```

## 10. Save Data Template

Use this for new persistent player data using `PlayerSaveEntry`.

```text
Add new persistent player data for: [SAVE DATA DESCRIPTION]

Required docs to read first:
- docs/TURMOIL_PLAYER_STATE_SAVE_AND_PROGRESS_DATA_MAP.md
- docs/TURMOIL_CORE_SYSTEMS_RISK_MAP.md
- docs/TURMOIL_IDS_AND_REGISTRATION_MAP.md
- docs/TURMOIL_CONTENT_CREATION_PATTERN_BOOK.md

Files to inspect first:
- src/io/xeros/model/entity/player/save/PlayerSaveEntry.java
- src/io/xeros/model/entity/player/save/PlayerSave.java
- src/io/xeros/model/entity/player/save/impl/AttackStyleSaveEntry.java
- src/io/xeros/content/dailyrewards/DailyRewardsPlayerSaveEntry.java
- src/io/xeros/content/instances/aoe/AoeTierProgressSaveEntry.java
- src/io/xeros/content/donationrewards/DonationRewardsPlayerSaveEntry.java

Files to avoid:
- src/io/xeros/model/entity/player/save/PlayerSave.java unless fixing existing legacy save behavior
- src/io/xeros/model/entity/player/Player.java unless a new runtime field is truly required

Step-by-step instructions:
1. Confirm the state must survive logout.
2. Search for existing save keys to avoid collisions.
3. Create or update a content-owned `PlayerSaveEntry`.
4. Keep keys content-prefixed and optional.
5. Implement default-safe `decode`, `encode`, and `login`.
6. Return `null` from `encode` when a value should not be written.
7. Do not make old accounts require the new key to log in.

Test requirements:
- Verify compile/build.
- Log in on an old account save without the new key.
- Change the state.
- Log out normally and wait for save completion.
- Log back in and verify the state persisted.
- Confirm the character file still ends with `[EOF]`.

Required final response format:
- Summary
- Files changed
- Save keys added or changed
- Runtime fields used
- Migration/default behavior
- Save risk
- Tests run
- Manual in-game test steps
- Rollback notes
- Follow-up recommendations
```

## 11. Economy Review Template

Use this before any update touching rewards, shops, drops, points, upgrades, Fire of Exchange, donation rewards, or currencies.

```text
Review economy risk for this Turmoil update: [ECONOMY UPDATE DESCRIPTION]

This is analysis first. Do not modify code or config files until the risk is understood.

Required docs to read first:
- docs/TURMOIL_REWARD_ECONOMY_AUDIT.md
- docs/TURMOIL_PROGRESSION_AUDIT.md
- docs/TURMOIL_CONTENT_INDEX.md
- docs/TURMOIL_DATA_CONFIG_LOADER_MAP.md
- docs/TURMOIL_IDS_AND_REGISTRATION_MAP.md
- docs/TURMOIL_PLAYER_STATE_SAVE_AND_PROGRESS_DATA_MAP.md

Files to inspect first:
- src/io/xeros/model/shops/ShopAssistant.java
- src/io/xeros/content/fireofexchange/FireOfExchange.java
- src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java
- src/io/xeros/content/upgrade/UpgradeMaterials.java
- src/io/xeros/content/fusion/FusionMaterials.java
- src/io/xeros/content/wraith/WraithCharges.java
- src/io/xeros/content/bosspoints/BossPoints.java
- src/io/xeros/content/vote_panel/VotePanelManager.java
- src/io/xeros/content/achievement/Achievements.java
- src/io/xeros/content/collection_log/CollectionRewards.java
- src/io/xeros/content/battlepass/RewardList.java
- src/io/xeros/content/dailyrewards/DailyRewards.java

Files to avoid:
- src/io/xeros/model/shops/ShopAssistant.java unless fixing shop behavior
- src/io/xeros/model/entity/npc/drops/DropManager.java unless updating supported drop behavior
- src/io/xeros/content/combat/death/NPCDeath.java
- src/io/xeros/model/entity/player/save/PlayerSave.java

Step-by-step instructions:
1. Identify every reward, currency, sink, source, and progression loop touched.
2. Classify the update as economy power, gear power, cosmetic, convenience, or progression-only.
3. Compare amounts to existing starter, midgame, and endgame reward ranges.
4. Identify abuse paths, alts, repeat rate, daily limits, and item sinks.
5. Recommend safer reward amounts or gating if needed.
6. Only implement changes after explaining the risk.

Test requirements:
- Verify reward quantity and repeat behavior.
- Verify shop spend or sink behavior if involved.
- Verify no duplicate reward paths.
- Verify save/load for point or currency changes.
- Verify local or staging testing only for spawn or point tools.

Required final response format:
- Summary
- Economy systems inspected
- Reward sources
- Reward sinks
- Risk level
- Recommended safe amounts
- Abuse concerns
- Tests run or proposed
- Rollback notes
- Follow-up recommendations
```

## 12. PR Review Template

Use this for asking Codex to review its own work before merging.

```text
Review the current Turmoil changes before merging.

Do not modify files unless explicitly asked after the review.

Required docs to read first:
- AGENTS.md
- docs/TURMOIL_CORE_SYSTEMS_RISK_MAP.md
- docs/TURMOIL_TESTING_AND_DEBUG_TOOL_MAP.md
- docs/TURMOIL_PLAYER_STATE_SAVE_AND_PROGRESS_DATA_MAP.md if save/player state changed
- docs/TURMOIL_REWARD_ECONOMY_AUDIT.md if rewards/economy changed
- docs/TURMOIL_COMBAT_AND_REWARD_PIPELINE_DEEP_DIVE.md if combat/rewards changed
- docs/TURMOIL_INSTANCE_AND_MINIGAME_LIFECYCLE_MAP.md if instances/minigames changed

Files to inspect first:
- Run `git status --short`.
- Inspect every changed file.
- Inspect diffs against the base branch.
- Inspect tests or data validators relevant to changed files.
- Inspect the closest existing pattern for every new content change.

Files to avoid:
- Do not edit any file during review unless the user asks for fixes.
- Do not revert unrelated dirty worktree changes.

Step-by-step instructions:
1. List changed files.
2. Group changes by system.
3. Check for save compatibility risk.
4. Check for economy and duplicate reward risk.
5. Check for combat, instance, cleanup, and logout risk.
6. Check for ID conflicts and invented IDs.
7. Check whether tests were run and whether manual in-game tests are still needed.
8. Lead with findings ordered by severity.

Test requirements:
- No new tests are required for review-only work.
- Report the tests already run.
- Recommend the smallest missing test set before merge.

Required final response format:
- Findings
- Open questions or assumptions
- Changed files reviewed
- Risk level
- Tests already run
- Missing tests
- Manual in-game test checklist
- Merge recommendation
```
