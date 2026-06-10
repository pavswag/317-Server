# Turmoil RSPS Codex Agent Guide

This file is the permanent Codex instruction file for Turmoil RSPS. It applies to all future Codex tasks in this repo.

## 1. Project Identity

- Turmoil RSPS is a Java Old School RuneScape private server.
- The repo is PI/Xeros-style with newer modular systems under `src/io/xeros/content`.
- The server backbone already exists.
- Most work should add content, rewards, tasks, achievements, configs, docs, and focused bug fixes without rewriting core systems.
- Treat the repo as a live private-server codebase with player saves, economy balance, progression loops, and legacy compatibility risk.

## 2. Required Reading Before Any Code Change

Before making any code change, read the docs relevant to the touched system. For broad tasks, read the main maps first:

- `docs/TURMOIL_CONTENT_GUIDE.md`
- `docs/TURMOIL_CONTENT_INDEX.md`
- `docs/TURMOIL_PROGRESSION_AUDIT.md`
- `docs/TURMOIL_SERVER_METHOD_FLOW_MAP.md`
- `docs/TURMOIL_CONTENT_CREATION_PATTERN_BOOK.md`
- `docs/TURMOIL_CORE_SYSTEMS_RISK_MAP.md`
- `docs/TURMOIL_DATA_CONFIG_LOADER_MAP.md`
- `docs/TURMOIL_IDS_AND_REGISTRATION_MAP.md`
- `docs/TURMOIL_TESTING_AND_DEBUG_TOOL_MAP.md`
- `docs/TURMOIL_COMBAT_AND_REWARD_PIPELINE_DEEP_DIVE.md`
- `docs/TURMOIL_INSTANCE_AND_MINIGAME_LIFECYCLE_MAP.md`
- `docs/TURMOIL_PLAYER_STATE_SAVE_AND_PROGRESS_DATA_MAP.md`

Read these supplemental maps when the task touches their areas:

- `docs/TURMOIL_AOE_SYSTEM_DEEP_DIVE.md`
- `docs/TURMOIL_REWARD_ECONOMY_AUDIT.md`
- `docs/TURMOIL_PROGRESS_HOOK_MAP.md`

## 3. Core Rules

- Search for an existing pattern before coding.
- Copy the closest working local pattern.
- Prefer data, configs, enums, and managers over core rewrites.
- Use repo-relative paths only in explanations.
- Do not invent item IDs, NPC IDs, object IDs, shop IDs, button IDs, save keys, or commands.
- Do not modify generated files, build output files, archives, jars, or runtime save output unless explicitly requested.
- Do not change live economy balance without explaining the risk.
- Do not break existing player saves.
- Keep changes modular and scoped to the requested task.
- Avoid unrelated cleanup and broad formatting churn.
- When unsure whether a system is legacy or modular, inspect the docs and search for a working implementation first.

## 4. Systems To Avoid Rewriting

Avoid rewriting or broadly refactoring these systems unless the user explicitly asks for that level of work:

- `src/io/xeros/model/entity/player/Player.java`
- `src/io/xeros/model/entity/player/save/PlayerSave.java`
- `src/io/xeros/model/entity/npc/NPCHandler.java`
- `src/io/xeros/model/entity/npc/NPCProcess.java`
- `src/io/xeros/content/combat/death/NPCDeath.java`
- `src/io/xeros/model/entity/npc/drops/DropManager.java`
- `src/io/xeros/model/shops/ShopAssistant.java`
- central packet handlers
- combat formula classes
- core instance height allocation
- core login, logout, and save flow

For isolated content, prefer extension hooks, configs, managers, enums, and existing content classes instead of editing these files.

## 5. Preferred Extension Points

Use these extension points whenever they match the requested work:

- `PlayerSaveEntry` for new persistent player data.
- `DialogueBuilder` for new dialogues.
- Command subclasses for commands.
- `NPCAction` and `ObjectAction` where available.
- `InstancedArea` subclasses for new isolated instances.
- `NPCAutoAttackBuilder` for boss mechanics.
- `data/aoe` JSON files for AOE config changes.
- `UpgradeMaterials` for upgrade recipes.
- `FusionMaterials` for fusion recipes.
- `CollectionRewards` for collection reward changes.
- `Tasks.java` for Task Master additions.
- `Achievements.java` and `AchievementType.java` for achievements.

## 6. Coding Workflow

Before editing:

- Explain the relevant docs read.
- List files inspected.
- Identify the closest existing pattern.
- Explain the plan.
- Identify risks, especially save, economy, combat, and instance risks.

During editing:

- Keep changes small.
- Avoid unrelated cleanup.
- Do not reformat unrelated files.
- Do not touch core files unless absolutely necessary.
- Prefer `apply_patch` for manual edits.
- Preserve existing player save keys, enum names, command names, and content IDs unless the task is specifically a migration.

After editing:

- Summarize every changed file.
- Explain test steps.
- Explain economy, save, and combat risk.
- Explain rollback steps.
- State any tests that could not be run.

## 7. Bug Fix Workflow

For bugs:

- Reproduce or trace the bug first.
- Identify the exact flow using `docs/TURMOIL_SERVER_METHOD_FLOW_MAP.md`.
- Search for similar fixed systems.
- Fix the smallest root cause.
- Add guards only where the owning system should own them.
- Do not mask bugs with broad try/catch blocks.
- Do not silently swallow errors that affect player saves, drops, trades, shops, or rewards.

## 8. Content Workflow

For content:

- Decide whether the change is data-only, enum-only, manager-only, or requires Java support.
- Avoid adding ordinary rewards directly to `NPCDeath` or `DropManager` code.
- Avoid new save fields in `PlayerSave.java`; use `PlayerSaveEntry` for new persistent data.
- Prefer existing hooks for achievements, Task Master, collection logs, battlepass, boss points, Slayer, AOE, Wraith, and Fire of Exchange.
- Keep starter rewards modest.
- Keep endgame rewards rare and owner-reviewed.
- Check ID and registration docs before adding any item, NPC, object, button, shop, task, achievement, command, dialogue, or save key.
- Check reward economy docs before adding points, currencies, upgrade materials, rare drops, or item sinks.

## 9. Testing Workflow

- Use `docs/TURMOIL_TESTING_AND_DEBUG_TOOL_MAP.md` before using commands.
- Never use item, point, or drop spawning tools on a live economy.
- Prefer local or staging accounts.
- Verify compile or build when Java changes are made.
- Verify JSON or YAML parsing if data files changed.
- Verify save and load if persistence changed.
- Verify no duplicate rewards are granted.
- Verify no unrelated systems changed.
- For combat changes, test attack start, damage, death, drops, achievements, tasks, collection log, and cleanup.
- For instance changes, test enter, leave, death, logout, cleanup, and reward behavior.

## 10. PR Response Format

Every Codex PR or task response must include:

- Summary
- Files changed
- Systems touched
- Risk level
- Tests run
- Manual in-game test steps
- Rollback notes
- Follow-up recommendations

Keep responses specific to the actual files and systems touched. Use repo-relative paths only.
