# Turmoil Content Index

This index is a repo-side content inventory for future content tasks. It names the main Java files, the reward/progression role, and the closest pattern to copy. Some live data is loaded from external YAML or JSON by repo loaders; those entries identify the repo-side loader and manager instead of inventing unavailable config paths.

## Source Of Truth Notes

- Ordinary NPC drops are managed by `src/io/xeros/model/entity/npc/drops/DropManager.java` and related drop table classes. The checked-in repo contains the loader and runtime logic; normal drop table data is external.
- Shop stock is loaded by `src/io/xeros/model/definitions/ShopDef.java` into `src/io/xeros/model/world/ShopHandler.java`. The checked-in repo contains special-currency shop behavior in `src/io/xeros/model/shops/ShopAssistant.java`; most shop stock data is external.
- Collection log NPC/category membership is loaded by `src/io/xeros/content/collection_log/CollectionLog.java`. The checked-in repo contains special category handling and rewards, while the NPC list itself is external.
- New persistent content should prefer `src/io/xeros/model/entity/player/save/PlayerSaveEntry.java` when a modular save entry exists, and avoid expanding `src/io/xeros/model/entity/player/save/PlayerSave.java` unless maintaining an older save key.

## Bosses

- Name: Cerberus
  - Main files: `src/io/xeros/content/bosses/Cerberus.java`
  - Rewards: DropManager drops, boss points, Cerberus achievement progress, kill tracker progress.
  - Progression purpose: Slayer-adjacent midgame boss and pet/unique grind.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/Cerberus.java`, `src/io/xeros/content/combat/death/NPCDeath.java`

- Name: Corporeal Beast
  - Main files: `src/io/xeros/content/bosses/CorporealBeast.java`, `src/io/xeros/content/combat/death/NPCDeath.java`
  - Rewards: DropManager drops, boss points, Corp achievement progress.
  - Progression purpose: Group/high-HP boss with rare shield and high-value drops.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/CorporealBeast.java`

- Name: Cryo
  - Main files: `src/io/xeros/content/bosses/Cryo.java`
  - Rewards: DropManager drops and boss point flow if configured for its NPC id.
  - Progression purpose: Custom boss slot for repeatable PVM content.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/Cryo.java`

- Name: DCorp
  - Main files: `src/io/xeros/content/bosses/DCorp.java`
  - Rewards: DropManager drops and boss point flow if configured for its NPC id.
  - Progression purpose: Custom Corp-style boss variant.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/DCorp.java`

- Name: Donator Boss
  - Main files: `src/io/xeros/content/bosses/DonorBoss.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`
  - Rewards: DropManager drops plus daily donor kill allowances saved on the player.
  - Progression purpose: Donator daily boss loop.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/DonorBoss.java`

- Name: Extreme Donator Boss
  - Main files: `src/io/xeros/content/bosses/DonorBoss2.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`
  - Rewards: DropManager drops plus higher-rank donor daily kill allowances.
  - Progression purpose: Higher donator rank PVM loop.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/DonorBoss2.java`

- Name: Supreme Donator Boss
  - Main files: `src/io/xeros/content/bosses/DonorBoss3.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`
  - Rewards: DropManager drops plus highest-rank donor daily kill allowances.
  - Progression purpose: Top donor rank PVM loop.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/DonorBoss3.java`

- Name: Durial321
  - Main files: `src/io/xeros/content/bosses/Durial321.java`
  - Rewards: DropManager drops and boss point flow if configured for its NPC id.
  - Progression purpose: Custom nostalgia/PVP-themed boss encounter.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/Durial321.java`

- Name: Ghost
  - Main files: `src/io/xeros/content/bosses/Ghost.java`
  - Rewards: DropManager drops and boss point flow if configured for its NPC id.
  - Progression purpose: Custom repeatable boss.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/Ghost.java`

- Name: Herbiboar
  - Main files: `src/io/xeros/content/bosses/Herbiboar.java`, `src/io/xeros/content/combat/death/NPCDeath.java`
  - Rewards: Herbiboar damage/reward handling and drop table rewards.
  - Progression purpose: Skilling-boss style hunt content.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/Herbiboar.java`, `src/io/xeros/content/bosses/Tempoross.java`, `src/io/xeros/content/bosses/Zalcano.java`

- Name: Hunllef
  - Main files: `src/io/xeros/content/bosses/Hunllef.java`, `src/io/xeros/content/item/lootable/impl/HunllefChest.java`
  - Rewards: Hunllef chest rewards, collection log progress, achievement progress.
  - Progression purpose: Gauntlet-style solo boss and rare chest grind.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/Hunllef.java`

- Name: Kraken and Jack O Kraken
  - Main files: `src/io/xeros/content/bosses/Kraken.java`, `src/io/xeros/content/bosses/KrakenInstance.java`, `src/io/xeros/content/bosses/JackOKraken.java`
  - Rewards: DropManager drops, Slayer/Kraken achievement progress, instanced Kraken flow.
  - Progression purpose: Slayer boss grind and seasonal/custom Kraken variant.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/KrakenInstance.java`

- Name: Lightbearer
  - Main files: `src/io/xeros/content/bosses/Lightbearer.java`
  - Rewards: DropManager drops and boss point flow if configured for its NPC id.
  - Progression purpose: Custom item-themed boss encounter.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/Lightbearer.java`

- Name: Lizardman Shaman
  - Main files: `src/io/xeros/content/bosses/LizardmanShaman.java`
  - Rewards: DropManager drops and collection/kill tracker progress.
  - Progression purpose: Midgame rare item grind.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/LizardmanShaman.java`

- Name: Scorpia
  - Main files: `src/io/xeros/content/bosses/Scorpia.java`, `src/io/xeros/content/combat/death/NPCDeath.java`
  - Rewards: DropManager drops, wilderness boss achievements, boss points.
  - Progression purpose: Wilderness boss loop.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/Scorpia.java`

- Name: Skotizo
  - Main files: `src/io/xeros/content/bosses/Skotizo.java`
  - Rewards: DropManager drops and boss point flow if configured for its NPC id.
  - Progression purpose: Solo boss and totem/key style progression.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/Skotizo.java`

- Name: Sol
  - Main files: `src/io/xeros/content/bosses/Sol.java`
  - Rewards: DropManager drops and boss point flow if configured for its NPC id.
  - Progression purpose: Custom boss encounter.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/Sol.java`

- Name: Solak
  - Main files: `src/io/xeros/content/bosses/Solak.java`
  - Rewards: DropManager drops and boss point flow if configured for its NPC id.
  - Progression purpose: Custom high-tier boss encounter.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/Solak.java`

- Name: Tekton
  - Main files: `src/io/xeros/content/bosses/Tekton.java`, `src/io/xeros/content/minigames/raids/Raids.java`
  - Rewards: Raid points/damage contribution when used in raids, DropManager drops if spawned standalone.
  - Progression purpose: Raid-style boss mechanic.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/Tekton.java`, `src/io/xeros/content/minigames/raids/Raids.java`

- Name: Tempoross
  - Main files: `src/io/xeros/content/bosses/Tempoross.java`, `src/io/xeros/content/combat/death/NPCDeath.java`
  - Rewards: Tempoross damage/reward tracking and skilling-style loot.
  - Progression purpose: Fishing/skilling boss loop.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/Tempoross.java`, `src/io/xeros/content/bosses/Zalcano.java`

- Name: Vorkath
  - Main files: `src/io/xeros/content/bosses/Vorkath.java`, `src/io/xeros/content/combat/death/NPCDeath.java`
  - Rewards: DropManager drops, Vorkath achievement progress, boss points.
  - Progression purpose: Repeatable high-value solo boss.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/Vorkath.java`

- Name: Zalcano
  - Main files: `src/io/xeros/content/bosses/Zalcano.java`, `src/io/xeros/content/combat/death/NPCDeath.java`
  - Rewards: Zalcano damage/reward tracking and skilling-style loot.
  - Progression purpose: Mining/smithing skilling boss loop.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/Zalcano.java`, `src/io/xeros/content/bosses/Tempoross.java`

- Name: Bryophyta
  - Main files: `src/io/xeros/content/bosses/bryophyta/Bryophyta.java`, `src/io/xeros/content/bosses/bryophyta/BryophytaNPC.java`, `src/io/xeros/content/bosses/bryophyta/Growthling.java`
  - Rewards: DropManager drops and boss point flow if configured for its NPC id.
  - Progression purpose: Keyed/low-mid boss encounter with adds.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/bryophyta/BryophytaNPC.java`

- Name: Duke Sucellus
  - Main files: `src/io/xeros/content/bosses/dukesucellus/DukeSucellus.java`
  - Rewards: DropManager drops and boss point flow if configured for its NPC id.
  - Progression purpose: Modern solo boss slot.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/dukesucellus/DukeSucellus.java`

- Name: Fluffie
  - Main files: `src/io/xeros/content/bosses/fluffie/Fluffie.java`
  - Rewards: DropManager drops and boss point flow if configured for its NPC id.
  - Progression purpose: Custom boss encounter.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/fluffie/Fluffie.java`

- Name: Gobbler
  - Main files: `src/io/xeros/content/bosses/gobbler/Gobbler.java`, `src/io/xeros/content/bosses/gobbler/Spawns.java`
  - Rewards: DropManager drops and boss point flow if configured for its NPC id.
  - Progression purpose: Custom boss with spawn support.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/gobbler/Gobbler.java`

- Name: God Wars bosses
  - Main files: `src/io/xeros/content/bosses/godwars/Godwars.java`, `src/io/xeros/content/bosses/godwars/GodwarsNPCs.java`, `src/io/xeros/content/bosses/godwars/impl/BandosInstance.java`, `src/io/xeros/content/bosses/godwars/impl/ArmadylInstance.java`, `src/io/xeros/content/bosses/godwars/impl/SaradominInstance.java`, `src/io/xeros/content/bosses/godwars/impl/ZamorakInstance.java`
  - Rewards: DropManager drops, God Wars kill progression, boss points, task/achievement progress.
  - Progression purpose: Classic boss dungeon and instanced God Wars encounters.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/godwars/impl/BandosInstance.java`

- Name: Grotesque Guardians
  - Main files: `src/io/xeros/content/bosses/grotesqueguardians/GrotesqueInstance.java`, `src/io/xeros/content/bosses/grotesqueguardians/GrotesqueGuardianNpc.java`, `src/io/xeros/content/bosses/grotesqueguardians/DawnMelee.java`, `src/io/xeros/content/bosses/grotesqueguardians/DawnRanged.java`, `src/io/xeros/content/bosses/grotesqueguardians/DuskMelee.java`, `src/io/xeros/content/bosses/grotesqueguardians/DuskRanged.java`
  - Rewards: DropManager drops, Grotesque achievement progress, collection log special name.
  - Progression purpose: Multi-phase Slayer boss encounter.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/grotesqueguardians/GrotesqueInstance.java`

- Name: Hespori
  - Main files: `src/io/xeros/content/bosses/hespori/Hespori.java`, `src/io/xeros/content/bosses/hespori/HesporiSpawner.java`, `src/io/xeros/content/bosses/hespori/HesporiBonus.java`, `src/io/xeros/content/item/lootable/impl/HesporiChest.java`, `src/io/xeros/content/item/lootable/impl/HesporiChestItems.java`
  - Rewards: Hespori chest, Hespori keys, seed/bonus systems, collection log, achievement progress.
  - Progression purpose: Server/global farming boss and key chest loop.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/hespori/Hespori.java`, `src/io/xeros/content/worldevent/impl/HesporiWorldEvent.java`

- Name: Alchemical Hydra
  - Main files: `src/io/xeros/content/bosses/hydra/AlchemicalHydra.java`, `src/io/xeros/content/bosses/hydra/HydraStage.java`, `src/io/xeros/content/bosses/hydra/CombatProjectile.java`
  - Rewards: DropManager drops, Hydra achievement progress, boss points.
  - Progression purpose: High-tier Slayer boss with stage mechanics.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/hydra/AlchemicalHydra.java`

- Name: The Leviathan
  - Main files: `src/io/xeros/content/bosses/leviathan/TheLeviathan.java`
  - Rewards: DropManager drops and boss point flow if configured for its NPC id.
  - Progression purpose: Modern solo boss slot.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/leviathan/TheLeviathan.java`

- Name: Mimic
  - Main files: `src/io/xeros/content/bosses/mimic/MimicInstance.java`, `src/io/xeros/content/bosses/mimic/MimicNpc.java`, `src/io/xeros/content/bosses/mimic/MimicCasket.java`, `src/io/xeros/content/bosses/mimic/StrangeCasketDialogue.java`
  - Rewards: Mimic casket rewards, clue-style rare rewards, Mimic achievement progress.
  - Progression purpose: Casket-triggered boss reward gamble.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/mimic/MimicInstance.java`

- Name: Nex
  - Main files: `src/io/xeros/content/bosses/nex/NexNPC.java`, `src/io/xeros/content/bosses/nex/attacks/`, `src/io/xeros/content/globalboss/NEX.java`
  - Rewards: DropManager drops, Nex achievement progress, boss points, legacy global boss support.
  - Progression purpose: High-tier group boss and global boss variant.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/nex/NexNPC.java`

- Name: Nightmare
  - Main files: `src/io/xeros/content/bosses/nightmare/Nightmare.java`, `src/io/xeros/content/bosses/nightmare/party/NightmareParty.java`, `src/io/xeros/content/bosses/nightmare/NightmareInstance.java`, `src/io/xeros/content/bosses/nightmare/NightmareInterface.java`
  - Rewards: Nightmare drops, party rewards, Nightmare achievement progress, collection log.
  - Progression purpose: Group/instance high-tier boss.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/nightmare/NightmareInstance.java`

- Name: Obor
  - Main files: `src/io/xeros/content/bosses/obor/OborInstance.java`, `src/io/xeros/content/bosses/obor/OborNPC.java`
  - Rewards: DropManager drops and key/instance boss rewards.
  - Progression purpose: Starter-to-mid keyed boss.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/obor/OborInstance.java`

- Name: Sarachnis
  - Main files: `src/io/xeros/content/bosses/sarachnis/SarachnisNpc.java`, `src/io/xeros/content/bosses/sarachnis/SarachnisMelee.java`, `src/io/xeros/content/bosses/sarachnis/SarachnisRanged.java`, `src/io/xeros/content/bosses/sarachnis/SarachnisWeb.java`, `src/io/xeros/content/globalboss/Sarachnis.java`
  - Rewards: DropManager drops, Sarachnis achievement/task progress, legacy global boss support.
  - Progression purpose: Mid-tier boss with minions and web mechanics.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/sarachnis/SarachnisNpc.java`

- Name: Solo Tombs of Amascut bosses
  - Main files: `src/io/xeros/content/bosses/toa/ToaInstance.java`, `src/io/xeros/content/bosses/toa/SoloBaba.java`, `src/io/xeros/content/bosses/toa/SoloCrondis.java`, `src/io/xeros/content/bosses/toa/SoloApmeken.java`, `src/io/xeros/content/bosses/toa/SoloAkkha.java`, `src/io/xeros/content/bosses/toa/SoloKephri.java`, `src/io/xeros/content/bosses/toa/SoloTumekensWarden.java`
  - Rewards: TOA-style instance/chest progression when wired through its instance.
  - Progression purpose: Solo raid-boss progression path.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/toa/ToaInstance.java`

- Name: Vardorvis
  - Main files: `src/io/xeros/content/bosses/vardorvis/Vardorvis.java`, `src/io/xeros/content/bosses/vardorvis/VardorvisAxePositions.java`
  - Rewards: DropManager drops and boss point flow if configured for its NPC id.
  - Progression purpose: Modern solo boss with positional mechanics.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/vardorvis/Vardorvis.java`

- Name: The Whisperer
  - Main files: `src/io/xeros/content/bosses/whisperer/TheWhisperer.java`
  - Rewards: DropManager drops and boss point flow if configured for its NPC id.
  - Progression purpose: Modern solo boss slot.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/whisperer/TheWhisperer.java`

- Name: Wildy Pursuit bosses
  - Main files: `src/io/xeros/content/bosses/wildypursuit/FragmentOfSeren.java`, `src/io/xeros/content/bosses/wildypursuit/TheUnbearable.java`, `src/io/xeros/content/item/lootable/impl/SerenChest.java`, `src/io/xeros/content/item/lootable/impl/UnbearableChest.java`, `src/io/xeros/content/events/monsterhunt/MonsterHunt.java`
  - Rewards: Seren/Unbearable chest rewards, MonsterHunt cleanup, wilderness event rewards.
  - Progression purpose: Rotating wilderness pursuit/global event boss loop.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/wildypursuit/FragmentOfSeren.java`

- Name: Wintertodt
  - Main files: `src/io/xeros/content/bosses/wintertodt/Wintertodt.java`, `src/io/xeros/content/bosses/wintertodt/WintertodtActions.java`
  - Rewards: Wintertodt points, store points, skilling boss rewards.
  - Progression purpose: Firemaking/skilling boss loop.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/wintertodt/Wintertodt.java`

- Name: Zulrah
  - Main files: `src/io/xeros/content/bosses/zulrah/Zulrah.java`, `src/io/xeros/content/bosses/zulrah/ZulrahStage.java`, `src/io/xeros/content/bosses/zulrah/ZulrahLocation.java`, `src/io/xeros/content/bosses/zulrah/impl/`
  - Rewards: DropManager drops, Zulrah achievement progress, boss points.
  - Progression purpose: Repeatable phase/rotation solo boss.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/zulrah/Zulrah.java`

- Name: Legacy global bosses
  - Main files: `src/io/xeros/content/globalboss/KBD.java`, `src/io/xeros/content/globalboss/KQ.java`, `src/io/xeros/content/globalboss/NEX.java`, `src/io/xeros/content/globalboss/Sarachnis.java`
  - Rewards: Global boss drops through their legacy handlers and DropManager integration.
  - Progression purpose: Older global boss spawn/reward support.
  - Similar systems that can be copied: Prefer `src/io/xeros/content/activityboss/` for new activity bosses; copy legacy files only when extending old global boss behavior.

- Name: Drop/config-only tracked bosses
  - Main files: `src/io/xeros/content/combat/death/NPCDeath.java`, `src/io/xeros/model/entity/npc/drops/DropManager.java`, `src/io/xeros/content/bosspoints/BossPoints.java`
  - Rewards: DropManager drops, boss points, achievements, collection log if included by collection config.
  - Progression purpose: Existing bosses tracked primarily by NPC id and external drop data, including Abyssal Sire, King Black Dragon, Kalphite Queen, Giant Mole, Barrelchest, Dagannoth Kings, Callisto, Vet'ion, Venenatis, Chaos Elemental, Chaos Fanatic, Crazy Archaeologist, Araphel, and Shadow Araphel.
  - Similar systems that can be copied: Prefer a new class under `src/io/xeros/content/bosses/` for mechanics; use `src/io/xeros/content/combat/death/NPCDeath.java` only for death-side hooks that cannot live elsewhere.

## Minigames And Gameplay Modes

- Name: Fight Pits
  - Main files: `src/io/xeros/content/minigames/FightPits.java`
  - Rewards: Fight Pits participation/winner rewards where configured.
  - Progression purpose: PVP minigame.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/FightPits.java`

- Name: Sailing
  - Main files: `src/io/xeros/content/minigames/Sailing.java`
  - Rewards: Sailing travel/content rewards where configured.
  - Progression purpose: Utility/minigame travel content.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/Sailing.java`

- Name: Arbograve Swamp
  - Main files: `src/io/xeros/content/minigames/arbograve/ArbograveContainer.java`, `src/io/xeros/content/minigames/arbograve/instance/ArbograveInstance.java`, `src/io/xeros/content/minigames/arbograve/bosses/`, `src/io/xeros/content/item/lootable/impl/ArbograveChest.java`, `src/io/xeros/content/item/lootable/impl/ArbograveChestItems.java`
  - Rewards: Arbograve points, foundry points, Arbo keys, Arbograve chest rares, achievements, collection log.
  - Progression purpose: Raid-like room progression and high-value custom chest loop.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/arbograve/instance/ArbograveInstance.java`, `src/io/xeros/content/minigames/arbograve/ArbograveBoss.java`

- Name: Barrows
  - Main files: `src/io/xeros/content/minigames/barrows/`, `src/io/xeros/content/minigames/barrows/brothers/`, `src/io/xeros/content/minigames/barrows/RewardList.java`
  - Rewards: Barrows set items, common resources, Barrows achievement progress.
  - Progression purpose: Midgame armor grind and chest loop.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/barrows/`, `src/io/xeros/content/minigames/barrows/brothers/Dharok.java`

- Name: Blast Furnace
  - Main files: `src/io/xeros/content/minigames/blastfurnance/`
  - Rewards: Smithing/skilling output and utility progression.
  - Progression purpose: Skilling minigame and production loop.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/blastfurnance/`

- Name: Bounty Hunter
  - Main files: `src/io/xeros/content/minigames/bounty_hunter/`
  - Rewards: PVP/Bounty Hunter rewards and target progression.
  - Progression purpose: PVP target loop.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/bounty_hunter/`

- Name: Coin Flip
  - Main files: `src/io/xeros/content/minigames/coinflip/`
  - Rewards: Player wagering rewards.
  - Progression purpose: Gambling/social economy content.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/coinflip/`

- Name: Bloody Battle
  - Main files: `src/io/xeros/content/minigames/dz/Bloody_Battle.java`, `src/io/xeros/content/minigames/dz/Wave.java`
  - Rewards: Bloody points and wave rewards.
  - Progression purpose: Donator-zone wave combat content.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/dz/Bloody_Battle.java`

- Name: Fight Cave
  - Main files: `src/io/xeros/content/minigames/fight_cave/FightCave.java`, `src/io/xeros/content/minigames/fight_cave/Wave.java`
  - Rewards: Fire cape, Fight Cave achievements, wave completion.
  - Progression purpose: Classic wave-based PVM milestone.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/fight_cave/FightCave.java`

- Name: Inferno
  - Main files: `src/io/xeros/content/minigames/inferno/Inferno.java`, `src/io/xeros/content/minigames/inferno/InfernoWaveData.java`, `src/io/xeros/content/minigames/inferno/Tzkalzuk.java`
  - Rewards: Infernal cape/rewards, Inferno achievement progress.
  - Progression purpose: Endgame wave-based PVM milestone.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/inferno/Inferno.java`

- Name: Pest Control
  - Main files: `src/io/xeros/content/minigames/pest_control/PestControl.java`, `src/io/xeros/content/minigames/pest_control/PestControlRewards.java`
  - Rewards: PC points, coins, void gear, packs, event calendar progress.
  - Progression purpose: Group minigame for void gear and utility points.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/pest_control/PestControl.java`

- Name: PK Arena
  - Main files: `src/io/xeros/content/minigames/pk_arena/Highpkarena.java`, `src/io/xeros/content/minigames/pk_arena/Lowpkarena.java`
  - Rewards: PVP practice/reward flow where configured.
  - Progression purpose: PVP arena content.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/pk_arena/Highpkarena.java`

- Name: Chambers of Xeric
  - Main files: `src/io/xeros/content/minigames/raids/Raids.java`, `src/io/xeros/content/minigames/raids/RaidConstants.java`, `src/io/xeros/content/minigames/raids/RaidMonsters.java`, `src/io/xeros/content/item/lootable/impl/RaidsChestRare.java`
  - Rewards: Raid points, raid chest regulars/uniques, COX achievements, collection log.
  - Progression purpose: Party raid progression and raid gear chase.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/raids/Raids.java`, `src/io/xeros/content/minigames/raids/CoxParty.java`

- Name: Ranging Guild
  - Main files: `src/io/xeros/content/minigames/rangingguild/RangingGuild.java`
  - Rewards: Ranged minigame rewards where configured.
  - Progression purpose: Ranged skilling/combat minigame.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/rangingguild/RangingGuild.java`

- Name: Recipe for Disaster
  - Main files: `src/io/xeros/content/minigames/rfd/DisposeTypes.java`
  - Rewards: RFD/gloves-style progression where configured.
  - Progression purpose: Legacy quest/minigame reward unlock.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/rfd/DisposeTypes.java`

- Name: Tombs of Amascut
  - Main files: `src/io/xeros/content/minigames/TOA/TombsOfAmascutContainer.java`, `src/io/xeros/content/minigames/TOA/instance/TombsOfAmascutInstance.java`, `src/io/xeros/content/minigames/TOA/bosses/`, `src/io/xeros/content/item/lootable/impl/TombsOfAmascutChest.java`
  - Rewards: TOA chest rewards, MVP points, food/supply points, raid completions.
  - Progression purpose: Modern raid progression and chest loop.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/TOA/instance/TombsOfAmascutInstance.java`

- Name: Theatre of Blood
  - Main files: `src/io/xeros/content/minigames/tob/instance/TobInstance.java`, `src/io/xeros/content/minigames/tob/TobBoss.java`, `src/io/xeros/content/minigames/tob/bosses/`, `src/io/xeros/content/item/lootable/impl/TheatreOfBloodChest.java`
  - Rewards: TOB chest rewards, MVP points, food/supply points, TOB achievements, collection log.
  - Progression purpose: Endgame party raid progression.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/tob/instance/TobInstance.java`, `src/io/xeros/content/minigames/tob/TobBoss.java`

- Name: Warriors Guild
  - Main files: `src/io/xeros/content/minigames/warriors_guild/WarriorsGuild.java`, `src/io/xeros/content/minigames/warriors_guild/AnimatedArmour.java`
  - Rewards: Warrior Guild tokens and defender-style progression.
  - Progression purpose: Melee equipment unlock loop.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/warriors_guild/WarriorsGuild.java`

- Name: Wheel of Fortune
  - Main files: `src/io/xeros/content/minigames/wheel/WheelOfFortune.java`, `src/io/xeros/content/minigames/wheel/WheelOfFortuneGame.java`, `src/io/xeros/model/entity/player/Player.java`
  - Rewards: Fortune spin rewards and prize rolls.
  - Progression purpose: Spin-based reward system.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/wheel/WheelOfFortune.java`

- Name: Xeric Waves
  - Main files: `src/io/xeros/content/minigames/xeric/Xeric.java`, `src/io/xeros/content/minigames/xeric/XericLobby.java`, `src/io/xeros/content/minigames/xeric/XericRewards.java`, `src/io/xeros/content/minigames/xeric/XericWave.java`
  - Rewards: Xeric rewards and wave progression.
  - Progression purpose: Wave/lobby PVM minigame.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/xeric/Xeric.java`

- Name: Outlast Tournaments
  - Main files: `src/io/xeros/content/tournaments/TourneyManager.java`, `src/io/xeros/content/tournaments/OutlastController.java`, `src/io/xeros/content/tournaments/TourneySetup.java`, `src/io/xeros/content/tournaments/Maps.java`
  - Rewards: Tournament points, kill rewards, coins, streak rewards, leaderboard/winner tracking.
  - Progression purpose: Scheduled PVP tournament and leaderboard loop.
  - Similar systems that can be copied: `src/io/xeros/content/tournaments/TourneyManager.java`

- Name: WeaponGames
  - Main files: `src/io/xeros/content/WeaponGames/WGManager.java`, `src/io/xeros/content/WeaponGames/WGController.java`, `src/io/xeros/content/WeaponGames/WGModes.java`, `src/io/xeros/content/WeaponGames/WGArmor.java`
  - Rewards: WeaponGame points, winner rewards, staged weapon upgrades during the match.
  - Progression purpose: Scheduled weapon-progression PVP minigame.
  - Similar systems that can be copied: `src/io/xeros/content/WeaponGames/WGManager.java`

- Name: Casino and social games
  - Main files: `src/io/xeros/content/games/WheelOfFortuneGame.java`, `src/io/xeros/content/games/Roulette.java`, `src/io/xeros/content/games/Poker.java`, `src/io/xeros/content/games/PartyRoom.java`, `src/io/xeros/content/games/blackjack/BJManager.java`
  - Rewards: Player wager/social rewards and party drops.
  - Progression purpose: Economy/social side content rather than character power progression.
  - Similar systems that can be copied: `src/io/xeros/content/games/Roulette.java`, `src/io/xeros/content/games/blackjack/BJManager.java`

## Activity Bosses And Global Events

- Name: Ahrim Activity Boss
  - Main files: `src/io/xeros/content/activityboss/GlobalBossType.java`, `src/io/xeros/content/activityboss/GlobalBossActivityManager.java`
  - Rewards: Ranked contribution rewards through `src/io/xeros/content/activityboss/GlobalBossRewardHandler.java`; placeholder loot table in `src/io/xeros/content/activityboss/GlobalBossLootTable.java`.
  - Progression purpose: Server-wide clue casket activity target.
  - Similar systems that can be copied: `src/io/xeros/content/activityboss/GlobalBossType.java`

- Name: Dharok Activity Boss
  - Main files: `src/io/xeros/content/activityboss/GlobalBossType.java`, `src/io/xeros/content/activityboss/GlobalBossActivityManager.java`
  - Rewards: Ranked contribution rewards and activity boss contribution history.
  - Progression purpose: Server-wide item upgrade activity target.
  - Similar systems that can be copied: `src/io/xeros/content/activityboss/GlobalBossType.java`

- Name: Karil Activity Boss
  - Main files: `src/io/xeros/content/activityboss/GlobalBossType.java`, `src/io/xeros/content/activityboss/GlobalBossActivityManager.java`
  - Rewards: Ranked contribution rewards and activity boss contribution history.
  - Progression purpose: Server-wide vote claim activity target.
  - Similar systems that can be copied: `src/io/xeros/content/activityboss/GlobalBossType.java`

- Name: Guthan Activity Boss
  - Main files: `src/io/xeros/content/activityboss/GlobalBossType.java`, `src/io/xeros/content/activityboss/GlobalBossActivityManager.java`
  - Rewards: Ranked contribution rewards and activity boss contribution history.
  - Progression purpose: Server-wide Fire of Exchange burn target.
  - Similar systems that can be copied: `src/io/xeros/content/activityboss/GlobalBossType.java`, `src/io/xeros/content/fireofexchange/FireOfExchange.java`

- Name: Verac Activity Boss
  - Main files: `src/io/xeros/content/activityboss/GlobalBossType.java`, `src/io/xeros/content/activityboss/GlobalBossActivityManager.java`
  - Rewards: Ranked contribution rewards and activity boss contribution history.
  - Progression purpose: Server-wide 10+ killstreak target.
  - Similar systems that can be copied: `src/io/xeros/content/activityboss/GlobalBossType.java`

- Name: Groot
  - Main files: `src/io/xeros/content/activityboss/Groot.java`, `src/io/xeros/content/commands/all/Groot.java`
  - Rewards: Groot-specific activity boss rewards where configured.
  - Progression purpose: Legacy/custom activity boss command/event.
  - Similar systems that can be copied: Prefer `src/io/xeros/content/activityboss/GlobalBossType.java` for new activity bosses.

- Name: World Event Rotation
  - Main files: `src/io/xeros/content/worldevent/WorldEventContainer.java`, `src/io/xeros/content/worldevent/WorldEvent.java`, `src/io/xeros/content/worldevent/WorldEventState.java`
  - Rewards: Event-specific rewards, broadcasts, and event status.
  - Progression purpose: Scheduled server-wide content rotation.
  - Similar systems that can be copied: `src/io/xeros/content/worldevent/impl/HesporiWorldEvent.java`

- Name: Hespori World Event
  - Main files: `src/io/xeros/content/worldevent/impl/HesporiWorldEvent.java`, `src/io/xeros/content/bosses/hespori/HesporiSpawner.java`, `src/io/xeros/content/bosses/hespori/Hespori.java`
  - Rewards: Hespori world boss rewards and chest/key flow.
  - Progression purpose: Timed server boss event.
  - Similar systems that can be copied: `src/io/xeros/content/worldevent/impl/HesporiWorldEvent.java`

- Name: Wilderness Boss World Event
  - Main files: `src/io/xeros/content/worldevent/impl/WildernessBossWorldEvent.java`, `src/io/xeros/content/events/monsterhunt/MonsterHunt.java`, `src/io/xeros/content/events/monsterhunt/MonsterHuntLocation.java`
  - Rewards: Wilderness event keys/chests and MonsterHunt boss rewards.
  - Progression purpose: Timed wilderness boss event.
  - Similar systems that can be copied: `src/io/xeros/content/worldevent/impl/WildernessBossWorldEvent.java`

- Name: Tournament World Event
  - Main files: `src/io/xeros/content/worldevent/impl/TournamentWorldEvent.java`, `src/io/xeros/content/tournaments/TourneyManager.java`
  - Rewards: Tournament rewards and tournament points.
  - Progression purpose: Timed PVP world event.
  - Similar systems that can be copied: `src/io/xeros/content/worldevent/impl/TournamentWorldEvent.java`

- Name: WeaponGames World Event
  - Main files: `src/io/xeros/content/worldevent/impl/WGWorldEvent.java`, `src/io/xeros/content/WeaponGames/WGManager.java`
  - Rewards: WeaponGame points and winner rewards.
  - Progression purpose: Timed WeaponGames event support.
  - Similar systems that can be copied: `src/io/xeros/content/worldevent/impl/WGWorldEvent.java`

## Upgrade Systems

- Name: Upgrade Interface
  - Main files: `src/io/xeros/content/upgrade/UpgradeInterface.java`, `src/io/xeros/content/upgrade/UpgradeMaterials.java`
  - Rewards: Upgraded items, Fortune XP, rare upgrade collection log entries, activity boss progress for upgrades.
  - Progression purpose: Main item power progression using foundry/upgrade points and Fortune levels.
  - Similar systems that can be copied: `src/io/xeros/content/upgrade/UpgradeMaterials.java`

- Name: Fusion System
  - Main files: `src/io/xeros/content/fusion/FusionSystem.java`, `src/io/xeros/content/fusion/FusionMaterials.java`, `src/io/xeros/content/fusion/FusionTypes.java`
  - Rewards: Fused items, Fortune XP, foundry-material sink.
  - Progression purpose: Material-based upgrade path parallel to the main upgrade interface.
  - Similar systems that can be copied: `src/io/xeros/content/fusion/FusionMaterials.java`

- Name: Fire of Exchange
  - Main files: `src/io/xeros/content/fireofexchange/FireOfExchange.java`, `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`
  - Rewards: Foundry/Upgrade Points, Fortune XP, activity boss FOE burn progress.
  - Progression purpose: Item sink and upgrade-currency generator.
  - Similar systems that can be copied: `src/io/xeros/content/fireofexchange/FireOfExchange.java`

- Name: Item Combinations
  - Main files: `src/io/xeros/content/items/item_combinations/`
  - Rewards: Combined items such as spirit shields, godswords, boots, books, and tool upgrades.
  - Progression purpose: Traditional combine-item upgrades.
  - Similar systems that can be copied: `src/io/xeros/content/items/item_combinations/Godswords.java`, `src/io/xeros/content/items/item_combinations/TentacleWhip.java`

- Name: AOE Weapons
  - Main files: `src/io/xeros/content/items/aoeweapons/AoeWeapons.java`, `src/io/xeros/content/items/aoeweapons/AOESystem.java`, `src/io/xeros/content/items/aoeweapons/AoeManager.java`, `data/aoe/aoe_boss_tiers.json`, `data/aoe/aoe_tier_rewards.json`, `data/aoe/AoeZoneMapConfig.json`
  - Rewards: AOE weapons, AOE instance points, AOE tier rewards.
  - Progression purpose: Custom AOE weapon and instance-tier progression.
  - Similar systems that can be copied: `src/io/xeros/content/items/aoeweapons/AoeWeapons.java`

- Name: Wraith Charges
  - Main files: `src/io/xeros/content/wraith/WraithCharges.java`, `src/io/xeros/content/commands/all/Wraith.java`, `src/io/xeros/content/commands/all/Wraithcharges.java`
  - Rewards: Charged Wraith weapon power/charges.
  - Progression purpose: Charge-based item maintenance and power loop.
  - Similar systems that can be copied: `src/io/xeros/content/wraith/WraithCharges.java`

- Name: God Wars Equipment Data
  - Main files: `src/io/xeros/content/bosses/godwars/GodwarsEquipment.java`, `src/io/xeros/content/bosses/godwars/GodwarsNPCs.java`
  - Rewards: God alignment/equipment handling for God Wars access.
  - Progression purpose: Dungeon eligibility and equipment rules.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/godwars/GodwarsEquipment.java`

## Currencies And Point Systems

- Name: Coins
  - Main files: `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/model/items/ItemAssistant.java`
  - Rewards: Base economy currency.
  - Progression purpose: Standard purchases, drops, and rewards.
  - Similar systems that can be copied: Coin shop flow in `src/io/xeros/model/shops/ShopAssistant.java`

- Name: Platinum Tokens
  - Main files: `src/io/xeros/content/commands/all/Tok.java`, `src/io/xeros/model/shops/ShopAssistant.java`
  - Rewards: High-denomination coin storage and platinum token shop purchases.
  - Progression purpose: Large-value economy convenience.
  - Similar systems that can be copied: `src/io/xeros/content/commands/all/Tok.java`

- Name: PKP Points
  - Main files: `src/io/xeros/model/entity/player/Player.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`, `src/io/xeros/model/shops/ShopAssistant.java`
  - Rewards: PKP shop purchases.
  - Progression purpose: PVP reward currency.
  - Similar systems that can be copied: Special shop handling for shop id 80 in `src/io/xeros/model/shops/ShopAssistant.java`

- Name: Boss Points
  - Main files: `src/io/xeros/content/bosspoints/BossPoints.java`, `src/io/xeros/content/bosspoints/JarsToPoints.java`, `src/io/xeros/content/combat/death/NPCDeath.java`
  - Rewards: Boss point shop purchases and boss kill progression.
  - Progression purpose: Cross-boss grind currency.
  - Similar systems that can be copied: `src/io/xeros/content/bosspoints/BossPoints.java`

- Name: Achievement Points
  - Main files: `src/io/xeros/content/achievement/Achievements.java`, `src/io/xeros/model/shops/ShopAssistant.java`
  - Rewards: Achievement shop purchases.
  - Progression purpose: Account-wide objective reward currency.
  - Similar systems that can be copied: Achievement shop id 78 in `src/io/xeros/model/shops/ShopAssistant.java`

- Name: Raid Points
  - Main files: `src/io/xeros/content/minigames/raids/Raids.java`, `src/io/xeros/model/entity/player/Player.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`
  - Rewards: COX reward chance and raid chest budget.
  - Progression purpose: Raid contribution reward scaling.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/raids/Raids.java`

- Name: Vote Points and Vote Key Points
  - Main files: `src/io/xeros/content/commands/all/Voted.java`, `src/io/xeros/content/vote_panel/VotePanelManager.java`, `src/io/xeros/content/vote_panel/VotePanelInterface.java`, `src/io/xeros/model/shops/ShopAssistant.java`
  - Rewards: Vote shop, vote keys, vote panel streak/top-voter rewards, activity boss vote progress.
  - Progression purpose: Voting retention loop.
  - Similar systems that can be copied: `src/io/xeros/content/commands/all/Voted.java`

- Name: Blood Points and Blood Money
  - Main files: `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/model/entity/player/Player.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`
  - Rewards: Blood money shop purchases and PVP reward shops.
  - Progression purpose: Wilderness/PVP economy.
  - Similar systems that can be copied: Special shop handling for shop ids 116, 117, and 119 in `src/io/xeros/model/shops/ShopAssistant.java`

- Name: Pest Control Points
  - Main files: `src/io/xeros/content/minigames/pest_control/PestControl.java`, `src/io/xeros/content/minigames/pest_control/PestControlRewards.java`
  - Rewards: Void gear, XP, packs, shop purchases.
  - Progression purpose: Minigame gear currency.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/pest_control/PestControlRewards.java`

- Name: Donator Points and Donation Coins
  - Main files: `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/content/donationrewards/DonationRewards.java`, `src/io/xeros/model/entity/player/Player.java`
  - Rewards: Donator shop purchases, donation coin purchases, weekly donation rewards.
  - Progression purpose: Donator economy and reward loop.
  - Similar systems that can be copied: Donator shop ids 9, 112, and 199 in `src/io/xeros/model/shops/ShopAssistant.java`

- Name: Loyalty Points
  - Main files: `src/io/xeros/model/entity/player/Player.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`
  - Rewards: Loyalty-based reward hooks where used by shops/content.
  - Progression purpose: Account retention currency.
  - Similar systems that can be copied: Existing saved point fields in `src/io/xeros/model/entity/player/Player.java`

- Name: Exchange Points and Foundry/Upgrade Points
  - Main files: `src/io/xeros/content/fireofexchange/FireOfExchange.java`, `src/io/xeros/content/upgrade/UpgradeInterface.java`, `src/io/xeros/content/fusion/FusionSystem.java`
  - Rewards: Upgrade/fusion purchases, item sink returns, Fortune XP.
  - Progression purpose: Main item sink and upgrade economy.
  - Similar systems that can be copied: `src/io/xeros/content/fireofexchange/FireOfExchange.java`

- Name: Arbograve Points
  - Main files: `src/io/xeros/content/minigames/arbograve/ArbograveBoss.java`, `src/io/xeros/model/shops/ShopAssistant.java`
  - Rewards: Arbograve shop purchases.
  - Progression purpose: Arbograve completion currency.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/arbograve/ArbograveBoss.java`

- Name: AFK Points
  - Main files: `src/io/xeros/content/afkzone/`, `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/model/entity/player/Player.java`
  - Rewards: AFK shop purchases.
  - Progression purpose: AFK-zone retention currency.
  - Similar systems that can be copied: Shop id 195 handling in `src/io/xeros/model/shops/ShopAssistant.java`

- Name: Bloody Points
  - Main files: `src/io/xeros/content/minigames/dz/Bloody_Battle.java`, `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/model/entity/player/Player.java`
  - Rewards: Bloody points shop purchases.
  - Progression purpose: Bloody Battle/wave reward currency.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/dz/Bloody_Battle.java`

- Name: Seasonal Points
  - Main files: `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/model/entity/player/Player.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`
  - Rewards: Seasonal shop purchases.
  - Progression purpose: Seasonal event currency.
  - Similar systems that can be copied: Shop id 196 handling in `src/io/xeros/model/shops/ShopAssistant.java`

- Name: Discord Points
  - Main files: `src/io/xeros/model/entity/player/Player.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`, `src/io/xeros/model/shops/ShopAssistant.java`
  - Rewards: Discord point shop purchases.
  - Progression purpose: Discord linking/booster reward currency.
  - Similar systems that can be copied: Shop id 118 handling in `src/io/xeros/model/shops/ShopAssistant.java`

- Name: Prestige Points
  - Main files: `src/io/xeros/content/prestige/PrestigeSkills.java`, `src/io/xeros/content/prestige/PrestigePerks.java`, `src/io/xeros/model/shops/ShopAssistant.java`
  - Rewards: Prestige shop purchases and prestige relics.
  - Progression purpose: Post-max skill reset/relic progression.
  - Similar systems that can be copied: `src/io/xeros/content/prestige/PrestigeSkills.java`

- Name: Tournament Points
  - Main files: `src/io/xeros/content/tournaments/TourneyManager.java`, `src/io/xeros/model/shops/ShopAssistant.java`
  - Rewards: Tournament shop purchases and PVP rewards.
  - Progression purpose: PVP tournament currency.
  - Similar systems that can be copied: `src/io/xeros/content/tournaments/TourneyManager.java`

- Name: WeaponGame Points
  - Main files: `src/io/xeros/content/WeaponGames/WGManager.java`, `src/io/xeros/model/shops/ShopAssistant.java`
  - Rewards: WeaponGame shop purchases.
  - Progression purpose: WeaponGames event currency.
  - Similar systems that can be copied: `src/io/xeros/content/WeaponGames/WGManager.java`

- Name: AOE Instance Points
  - Main files: `src/io/xeros/content/items/aoeweapons/`, `src/io/xeros/content/instances/aoe/`, `src/io/xeros/model/shops/ShopAssistant.java`, `data/aoe/aoe_tier_rewards.json`
  - Rewards: AOE shop/tier rewards.
  - Progression purpose: AOE instance and boss tier progression.
  - Similar systems that can be copied: `src/io/xeros/content/items/aoeweapons/AoeManager.java`

- Name: Mage Arena Points
  - Main files: `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/model/entity/player/Player.java`
  - Rewards: Mage arena shop purchases.
  - Progression purpose: Mage Arena reward currency.
  - Similar systems that can be copied: Shop id 40 handling in `src/io/xeros/model/shops/ShopAssistant.java`

- Name: Shayzien Assault Points
  - Main files: `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/model/entity/player/Player.java`
  - Rewards: Assault shop purchases.
  - Progression purpose: Assault/minigame currency.
  - Similar systems that can be copied: Shop id 82 handling in `src/io/xeros/model/shops/ShopAssistant.java`

- Name: Fortune Spins
  - Main files: `src/io/xeros/content/minigames/wheel/WheelOfFortune.java`, `src/io/xeros/model/entity/player/Player.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`
  - Rewards: Wheel spin prize rolls.
  - Progression purpose: Tokenized spin reward loop, distinct from the Fortune skill.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/wheel/WheelOfFortune.java`

- Name: Item currencies
  - Main files: `src/io/xeros/model/shops/ShopAssistant.java`
  - Rewards: Tokkul, Marks of Grace, Stardust, Molch Pearls, Scrap Paper, Rusty Caskets, Platinum Tokens, Blood Money, and Donation Coins are consumed directly as item currencies by special shops.
  - Progression purpose: Item-token sinks tied to specific content loops.
  - Similar systems that can be copied: `src/io/xeros/model/shops/ShopAssistant.java`

## Shops

- Name: External YAML shops
  - Main files: `src/io/xeros/model/definitions/ShopDef.java`, `src/io/xeros/model/world/ShopHandler.java`, `src/io/xeros/model/shops/Shop.java`, `src/io/xeros/model/shops/ShopItem.java`, `src/io/xeros/model/shops/NamedShopItem.java`
  - Rewards: Configured stock with configured prices.
  - Progression purpose: Primary data-driven shop framework.
  - Similar systems that can be copied: `src/io/xeros/model/definitions/ShopDef.java`

- Name: Special-currency shop router
  - Main files: `src/io/xeros/model/shops/ShopAssistant.java`
  - Rewards: Handles purchases for PKP, boss points, vote points, donator points, achievement points, PC points, slayer points, prestige points, exchange/foundry points, Arbograve points, AOE points, and item currencies.
  - Progression purpose: Central purchase rules for custom currencies.
  - Similar systems that can be copied: Add new shop behavior conservatively near existing special shop id handling in `src/io/xeros/model/shops/ShopAssistant.java`

- Name: Fire of Exchange shop
  - Main files: `src/io/xeros/content/fireofexchange/FireOfExchange.java`, `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`, `src/io/xeros/model/shops/ShopAssistant.java`
  - Rewards: Upgrade/foundry points and burn-price preview shop.
  - Progression purpose: Item sink and upgrade currency generator.
  - Similar systems that can be copied: `src/io/xeros/content/fireofexchange/FireOfExchangeBurnPrice.java`

- Name: Slayer reward shops
  - Main files: `src/io/xeros/content/skills/slayer/SlayerRewardsInterface.java`, `src/io/xeros/content/skills/slayer/SlayerRewardsInterfaceData.java`, `src/io/xeros/model/shops/ShopAssistant.java`
  - Rewards: Slayer unlocks, task extensions, blocked tasks, slayer shop items.
  - Progression purpose: Slayer task economy and reward unlocks.
  - Similar systems that can be copied: `src/io/xeros/content/skills/slayer/SlayerRewardsInterfaceData.java`

- Name: Pest Control reward shop
  - Main files: `src/io/xeros/content/minigames/pest_control/PestControlRewards.java`, `src/io/xeros/model/shops/ShopAssistant.java`
  - Rewards: Void gear, XP, packs.
  - Progression purpose: PC points sink.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/pest_control/PestControlRewards.java`

- Name: Perdu Lost Property Shop
  - Main files: `src/io/xeros/content/itemskeptondeath/perdu/PerduLostPropertyShop.java`
  - Rewards: Lost/reclaim item purchases.
  - Progression purpose: Item reclaim safety system.
  - Similar systems that can be copied: `src/io/xeros/content/itemskeptondeath/perdu/PerduLostPropertyShop.java`

- Name: Donator shops
  - Main files: `src/io/xeros/model/shops/ShopAssistant.java`, `src/io/xeros/content/commands/donator/Donatorzone.java`, `src/io/xeros/content/commands/donator/Dz.java`
  - Rewards: Donator point purchases and donor-zone access.
  - Progression purpose: Donator economy and rank utility.
  - Similar systems that can be copied: Donator shop id handling in `src/io/xeros/model/shops/ShopAssistant.java`

- Name: Donation rewards interface
  - Main files: `src/io/xeros/content/donationrewards/DonationRewards.java`, `src/io/xeros/content/donationrewards/DonationReward.java`, `src/io/xeros/content/donationrewards/DonationRewardsPlayerSaveEntry.java`
  - Rewards: Weekly donation threshold rewards.
  - Progression purpose: Donation milestone reward loop.
  - Similar systems that can be copied: `src/io/xeros/content/donationrewards/DonationRewardsPlayerSaveEntry.java`

- Name: Donor Vault
  - Main files: `src/io/xeros/content/donor/DonorVault.java`, `src/io/xeros/content/item/lootable/impl/DonoVault.java`
  - Rewards: Donor vault commons/uncommons/rares, mini boxes, donation scrolls, pets, rare gear.
  - Progression purpose: Donor-token loot room/chest.
  - Similar systems that can be copied: `src/io/xeros/content/donor/DonorVault.java`

- Name: Vote panel/shop
  - Main files: `src/io/xeros/content/vote_panel/VotePanelManager.java`, `src/io/xeros/content/vote_panel/VotePanelInterface.java`, `src/io/xeros/content/commands/all/Voted.java`, `src/io/xeros/model/shops/ShopAssistant.java`
  - Rewards: Vote points, vote keys, weekly top voter rewards, bonus XP timer.
  - Progression purpose: Vote retention and weekly leaderboard loop.
  - Similar systems that can be copied: `src/io/xeros/content/vote_panel/VotePanelManager.java`

- Name: Other code-visible special shops
  - Main files: `src/io/xeros/model/shops/ShopAssistant.java`
  - Rewards: Mage Arena, WeaponGame, Shayzien Assault, Arbograve, AOE instance, Blood Money, Bloody Points, AFK Points, Seasonal Points, Discord Points, Prestige Points, Achievement Points, Boss Points, Vote Points, PKP, Tokkul, Marks of Grace, Stardust, Molch Pearls, Scrap Paper, Rusty Casket exchange, Platinum Tokens, and free supply shops.
  - Progression purpose: Content-specific point sinks and item-token sinks.
  - Similar systems that can be copied: Existing shop id branches in `src/io/xeros/model/shops/ShopAssistant.java`

## Collection Log Categories

- Name: Bosses tab
  - Main files: `src/io/xeros/content/collection_log/CollectionLog.java`, `src/io/xeros/content/collection_log/CollectionRewards.java`
  - Rewards: Collection completion tracking and collection rewards.
  - Progression purpose: Boss drop completion log.
  - Similar systems that can be copied: `src/io/xeros/content/collection_log/CollectionLog.java`

- Name: Wilderness tab
  - Main files: `src/io/xeros/content/collection_log/CollectionLog.java`
  - Rewards: Wilderness drop completion tracking.
  - Progression purpose: Wilderness boss/event collection goals.
  - Similar systems that can be copied: `src/io/xeros/content/collection_log/CollectionLog.java`

- Name: Raids tab
  - Main files: `src/io/xeros/content/collection_log/CollectionLog.java`, `src/io/xeros/content/item/lootable/impl/RaidsChestRare.java`, `src/io/xeros/content/item/lootable/impl/TheatreOfBloodChest.java`, `src/io/xeros/content/item/lootable/impl/ArbograveChestItems.java`
  - Rewards: Raid rare completion tracking.
  - Progression purpose: Raid unique completion goals.
  - Similar systems that can be copied: Special raid drop handling in `src/io/xeros/content/collection_log/CollectionLog.java`

- Name: Minigames tab
  - Main files: `src/io/xeros/content/collection_log/CollectionLog.java`
  - Rewards: Minigame collection tracking.
  - Progression purpose: Minigame reward completion goals.
  - Similar systems that can be copied: `src/io/xeros/content/collection_log/CollectionLog.java`

- Name: Other tab
  - Main files: `src/io/xeros/content/collection_log/CollectionLog.java`, `src/io/xeros/content/trails/TreasureTrailsRewards.java`
  - Rewards: Clue casket categories and miscellaneous collection goals.
  - Progression purpose: Non-boss completion log.
  - Similar systems that can be copied: Clue handling in `src/io/xeros/content/collection_log/CollectionLog.java`

- Name: Pets
  - Main files: `src/io/xeros/content/collection_log/CollectionLog.java`, `src/io/xeros/model/entity/npc/pets/PetHandler.java`
  - Rewards: Pet completion tracking.
  - Progression purpose: Account-wide pet collection goal.
  - Similar systems that can be copied: PETS_ID handling in `src/io/xeros/content/collection_log/CollectionLog.java`

- Name: Weapon Upgrades
  - Main files: `src/io/xeros/content/collection_log/CollectionLog.java`, `src/io/xeros/content/upgrade/UpgradeMaterials.java`
  - Rewards: Rare weapon upgrade completion tracking.
  - Progression purpose: Upgrade log completion.
  - Similar systems that can be copied: Upgrade category id 6 handling in `src/io/xeros/content/collection_log/CollectionLog.java`

- Name: Armor Upgrades
  - Main files: `src/io/xeros/content/collection_log/CollectionLog.java`, `src/io/xeros/content/upgrade/UpgradeMaterials.java`
  - Rewards: Rare armor upgrade completion tracking.
  - Progression purpose: Upgrade log completion.
  - Similar systems that can be copied: Upgrade category id 7 handling in `src/io/xeros/content/collection_log/CollectionLog.java`

- Name: Accessory Upgrades
  - Main files: `src/io/xeros/content/collection_log/CollectionLog.java`, `src/io/xeros/content/upgrade/UpgradeMaterials.java`
  - Rewards: Rare accessory upgrade completion tracking.
  - Progression purpose: Upgrade log completion.
  - Similar systems that can be copied: Upgrade category id 8 handling in `src/io/xeros/content/collection_log/CollectionLog.java`

- Name: Misc Upgrades
  - Main files: `src/io/xeros/content/collection_log/CollectionLog.java`, `src/io/xeros/content/upgrade/UpgradeMaterials.java`
  - Rewards: Rare misc upgrade completion tracking.
  - Progression purpose: Upgrade log completion.
  - Similar systems that can be copied: Upgrade category id 9 handling in `src/io/xeros/content/collection_log/CollectionLog.java`

- Name: Aoe Weapons
  - Main files: `src/io/xeros/content/collection_log/CollectionLog.java`, `src/io/xeros/content/items/aoeweapons/AoeWeapons.java`
  - Rewards: AOE weapon completion tracking.
  - Progression purpose: AOE gear completion goal.
  - Similar systems that can be copied: AOE category id 10 handling in `src/io/xeros/content/collection_log/CollectionLog.java`

- Name: Special collection display names
  - Main files: `src/io/xeros/content/collection_log/CollectionLog.java`
  - Rewards: Custom completion display for Theatre of Blood, Arbograve Swamp, Grotesque Guardians, Perkfinder Minigame, and Hespori.
  - Progression purpose: Converts NPC ids or fake ids into player-facing collection categories.
  - Similar systems that can be copied: Special name branches in `src/io/xeros/content/collection_log/CollectionLog.java`

## Achievement Systems

- Name: Core achievements
  - Main files: `src/io/xeros/content/achievement/Achievements.java`, `src/io/xeros/content/achievement/AchievementType.java`, `src/io/xeros/content/achievement/AchievementTier.java`, `src/io/xeros/content/achievement/AchievementHandler.java`
  - Rewards: Item rewards, achievement points, starter/tiered achievement progress.
  - Progression purpose: Account objectives across voting, quests, PVM, skilling, minigames, upgrades, raids, and daily content.
  - Similar systems that can be copied: Add new entries to `src/io/xeros/content/achievement/Achievements.java` and new counters to `src/io/xeros/content/achievement/AchievementType.java`

- Name: Achievement interface V2
  - Main files: `src/io/xeros/content/achievement/inter/AchieveV2.java`, `src/io/xeros/content/achievement/inter/TasksInterface.java`, `src/io/xeros/content/achievement/inter/TaskEntry.java`, `src/io/xeros/content/achievement/inter/TaskDifficulty.java`
  - Rewards: Achievement/task interface display and claim UX.
  - Progression purpose: Player-facing achievement navigation.
  - Similar systems that can be copied: `src/io/xeros/content/achievement/inter/TasksInterface.java`

- Name: Achievement diaries
  - Main files: `src/io/xeros/content/achievement_diary/AchievementDiary.java`, `src/io/xeros/content/achievement_diary/AchievementDiaryManager.java`, `src/io/xeros/content/achievement_diary/DifficultyAchievementDiary.java`, `src/io/xeros/content/achievement_diary/impl/`
  - Rewards: Area diary completion rewards and diary perks.
  - Progression purpose: Region/objective-based progression.
  - Similar systems that can be copied: `src/io/xeros/content/achievement_diary/impl/WildernessAchievementDiary.java`

- Name: Event calendar challenges
  - Main files: `src/io/xeros/content/event/eventcalendar/EventCalendar.java`, `src/io/xeros/content/event/eventcalendar/EventChallenge.java`, `src/io/xeros/content/event/eventcalendar/EventCalendarDay.java`, `src/io/xeros/content/event/eventcalendar/EventChallengeMonthlyReward.java`
  - Rewards: Daily challenge drawing entries and monthly reward support.
  - Progression purpose: Calendar-based seasonal/daily objective system.
  - Similar systems that can be copied: `src/io/xeros/content/event/eventcalendar/EventChallenge.java`

## Daily Task And Recurring Progression Systems

- Name: Daily Rewards
  - Main files: `src/io/xeros/content/dailyrewards/DailyRewards.java`, `src/io/xeros/content/dailyrewards/DailyRewardContainer.java`, `src/io/xeros/content/dailyrewards/DailyRewardsPlayerSaveEntry.java`, `src/io/xeros/content/dailyrewards/DailyRewardsRecords.java`
  - Rewards: Daily reward items, streak progress, DAILY achievement progress.
  - Progression purpose: Daily login/claim retention.
  - Similar systems that can be copied: `src/io/xeros/content/dailyrewards/DailyRewardsPlayerSaveEntry.java`

- Name: Task Master
  - Main files: `src/io/xeros/content/taskmaster/TaskMaster.java`, `src/io/xeros/content/taskmaster/Tasks.java`, `src/io/xeros/content/taskmaster/TaskMasterKills.java`, `src/io/xeros/content/taskmaster/TaskDifficulty.java`, `src/io/xeros/content/taskmaster/TaskType.java`
  - Rewards: Daily/weekly task rewards through `TaskMasterKills` reward items.
  - Progression purpose: Combat and skilling tasks, including daily and weekly objectives.
  - Similar systems that can be copied: `src/io/xeros/content/taskmaster/Tasks.java`

- Name: Vote Panel Weekly Tasks
  - Main files: `src/io/xeros/content/vote_panel/VotePanelManager.java`, `src/io/xeros/content/vote_panel/VotePanelInterface.java`, `src/io/xeros/content/vote_panel/VoteUser.java`
  - Rewards: Top voter rewards, vote streak rewards, bonus XP.
  - Progression purpose: Weekly voting competition.
  - Similar systems that can be copied: `src/io/xeros/content/vote_panel/VotePanelManager.java`

- Name: Battlepass
  - Main files: `src/io/xeros/content/battlepass/Pass.java`, `src/io/xeros/content/battlepass/Rewards.java`, `src/io/xeros/content/battlepass/RewardList.java`
  - Rewards: Default and member season pass rewards across 50 tiers.
  - Progression purpose: Seasonal tier progression.
  - Similar systems that can be copied: `src/io/xeros/content/battlepass/Pass.java`

- Name: Event Calendar
  - Main files: `src/io/xeros/content/event/eventcalendar/EventCalendar.java`, `src/io/xeros/content/event/eventcalendar/EventCalendarDay.java`, `src/io/xeros/content/event/eventcalendar/EventChallenge.java`
  - Rewards: Daily challenge entries and monthly reward drawing support.
  - Progression purpose: Time-gated recurring challenges.
  - Similar systems that can be copied: `src/io/xeros/content/event/eventcalendar/EventCalendarDay.java`

## Prestige System

- Name: Skill Prestige
  - Main files: `src/io/xeros/content/prestige/PrestigeSkills.java`, `src/io/xeros/content/prestige/PrestigeInter.java`, `src/io/xeros/model/entity/player/Player.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`
  - Rewards: Prestige points and donation coins on prestige.
  - Progression purpose: Post-99/post-max skill reset progression.
  - Similar systems that can be copied: `src/io/xeros/content/prestige/PrestigeSkills.java`

- Name: Prestige Perks
  - Main files: `src/io/xeros/content/prestige/PrestigePerks.java`
  - Rewards: Damage bonuses, XP bonuses, double PC points, Bloody minigame boost, cannon extender, Hespori key chance, prayer restore, upgrade/FoE perks, healing, Nomad bonus, Rage, and one-time mystery box rewards.
  - Progression purpose: Long-term account relic tree.
  - Similar systems that can be copied: `src/io/xeros/content/prestige/PrestigePerks.java`

## Slayer Systems

- Name: Standard Slayer
  - Main files: `src/io/xeros/content/skills/slayer/Slayer.java`, `src/io/xeros/content/skills/slayer/SlayerMaster.java`, `src/io/xeros/content/skills/slayer/Task.java`, `src/io/xeros/content/skills/slayer/SlayerRewardsInterface.java`
  - Rewards: Slayer XP, Slayer points, task streak points, rewards/unlocks/extensions, achievements.
  - Progression purpose: Core task-based PVM progression.
  - Similar systems that can be copied: `src/io/xeros/content/skills/slayer/Task.java`, `src/io/xeros/content/skills/slayer/SlayerRewardsInterfaceData.java`

- Name: Konar Slayer
  - Main files: `src/io/xeros/content/skills/slayer/KonarSlayer.java`, `src/io/xeros/content/skills/slayer/KonarDialogue.java`, `src/io/xeros/content/item/lootable/impl/KonarChest.java`
  - Rewards: Location-based slayer tasks and Konar chest/key rewards.
  - Progression purpose: Slayer task variety and location restrictions.
  - Similar systems that can be copied: `src/io/xeros/content/skills/slayer/KonarSlayer.java`

- Name: Duo Slayer
  - Main files: `src/io/xeros/content/skills/slayer/DuoMode.java`
  - Rewards: Shared task progression/coordination.
  - Progression purpose: Cooperative Slayer flow.
  - Similar systems that can be copied: `src/io/xeros/content/skills/slayer/DuoMode.java`

- Name: Slayer Unlocks and Extensions
  - Main files: `src/io/xeros/content/skills/slayer/SlayerUnlock.java`, `src/io/xeros/content/skills/slayer/TaskExtension.java`, `src/io/xeros/content/skills/slayer/TaskExtender.java`, `src/io/xeros/content/skills/slayer/SlayerRewardsInterfaceData.java`
  - Rewards: Unlockable perks, extended tasks, blocked/cancelled task control.
  - Progression purpose: Slayer point sink and task customization.
  - Similar systems that can be copied: `src/io/xeros/content/skills/slayer/SlayerRewardsInterfaceData.java`

- Name: Larran's Key
  - Main files: `src/io/xeros/content/skills/slayer/LarrensKey.java`, `src/io/xeros/content/item/lootable/impl/LarransChest.java`
  - Rewards: Larran chest rewards and wilderness slayer key loop.
  - Progression purpose: Wilderness Slayer reward chase.
  - Similar systems that can be copied: `src/io/xeros/content/skills/slayer/LarrensKey.java`

- Name: Advanced Slayer
  - Main files: `src/io/xeros/content/advancedslayer/ADVSlayer.java`, `src/io/xeros/content/advancedslayer/Gear.java`, `src/io/xeros/content/advancedslayer/Difficulty.java`
  - Rewards: Advanced task points and tiered gear/task challenge rewards.
  - Progression purpose: Gear-restricted challenge Slayer tasks.
  - Similar systems that can be copied: `src/io/xeros/content/advancedslayer/Gear.java`

- Name: Demon Hunter Slayer
  - Main files: `src/io/xeros/content/skills/slayer/DemonSlayerMaster.java`, `src/io/xeros/content/skills/slayer/DemonHunterTaskManager.java`, `src/io/xeros/content/skills/slayer/DemonHunterPerks.java`, `src/io/xeros/content/skills/slayer/DemonMarkRewardHandler.java`, `src/io/xeros/content/skills/slayer/DemonSlayerContract.java`, `src/io/xeros/content/skills/slayer/DemonSlayerMilestoneManager.java`
  - Rewards: Demon Hunter XP, Demon Marks, task streak milestones, Demon Hunter perks, reward shop.
  - Progression purpose: Boss-tier Slayer progression tied to Demon Hunter skill.
  - Similar systems that can be copied: `src/io/xeros/content/skills/slayer/DemonSlayerMaster.java`, `src/io/xeros/content/skills/slayer/DemonHunterTaskManager.java`

## Fortune System

- Name: Fortune skill
  - Main files: `src/io/xeros/content/skills/Skill.java`, `src/io/xeros/content/fireofexchange/FireOfExchange.java`, `src/io/xeros/content/upgrade/UpgradeInterface.java`, `src/io/xeros/content/fusion/FusionSystem.java`
  - Rewards: Fortune XP from Fire of Exchange, upgrades, and fusion; Fortune level gates upgrades/fusions.
  - Progression purpose: Non-combat progression tied to item sinking and upgrade success paths.
  - Similar systems that can be copied: `src/io/xeros/content/upgrade/UpgradeInterface.java`, `src/io/xeros/content/fusion/FusionSystem.java`

- Name: Fortune Spins
  - Main files: `src/io/xeros/content/minigames/wheel/WheelOfFortune.java`, `src/io/xeros/model/entity/player/Player.java`, `src/io/xeros/model/entity/player/save/PlayerSave.java`
  - Rewards: Wheel prize rolls.
  - Progression purpose: Separate spin-token reward loop that should not be confused with the Fortune skill.
  - Similar systems that can be copied: `src/io/xeros/content/minigames/wheel/WheelOfFortune.java`

## Donator Features

- Name: Donator ranks and zone teleports
  - Main files: `src/io/xeros/content/commands/donator/Donatorzone.java`, `src/io/xeros/content/commands/donator/Dz.java`, `src/io/xeros/model/entity/player/Right.java`
  - Rewards: Donator zone access by rank.
  - Progression purpose: Donator utility and area access.
  - Similar systems that can be copied: `src/io/xeros/content/commands/donator/Dz.java`

- Name: Donator commands
  - Main files: `src/io/xeros/content/commands/donator/Donatortitle.java`, `src/io/xeros/content/commands/donator/HideDonor.java`, `src/io/xeros/content/commands/donator/Killtitle.java`, `src/io/xeros/content/commands/donator/Minime.java`
  - Rewards: Donator title, cosmetic/rank visibility, minime utility.
  - Progression purpose: Donator identity and convenience features.
  - Similar systems that can be copied: `src/io/xeros/content/commands/donator/Donatortitle.java`

- Name: Donator bosses
  - Main files: `src/io/xeros/content/bosses/DonorBoss.java`, `src/io/xeros/content/bosses/DonorBoss2.java`, `src/io/xeros/content/bosses/DonorBoss3.java`
  - Rewards: Rank-gated daily donor boss drops.
  - Progression purpose: Donator PVM daily loop.
  - Similar systems that can be copied: `src/io/xeros/content/bosses/DonorBoss.java`

- Name: Donor Vault
  - Main files: `src/io/xeros/content/donor/DonorVault.java`, `src/io/xeros/content/item/lootable/impl/DonoVault.java`
  - Rewards: Donor vault loot, rare donor rewards, boxes, scrolls, pets.
  - Progression purpose: Donor-token loot room.
  - Similar systems that can be copied: `src/io/xeros/content/donor/DonorVault.java`

- Name: Donator Slayer Instances
  - Main files: `src/io/xeros/content/donor/DonoSlayerInstances.java`
  - Rewards: Private donor Slayer task instances.
  - Progression purpose: Donator convenience for Slayer tasks.
  - Similar systems that can be copied: `src/io/xeros/content/donor/DonoSlayerInstances.java`, `src/io/xeros/content/instances/impl/LegacySoloPlayerInstance.java`

- Name: Donation Rewards
  - Main files: `src/io/xeros/content/donationrewards/DonationRewards.java`, `src/io/xeros/content/donationrewards/DonationReward.java`, `src/io/xeros/content/donationrewards/DonationRewardsPlayerSaveEntry.java`
  - Rewards: Weekly donation milestone items.
  - Progression purpose: Donation milestone tracking and claim UI.
  - Similar systems that can be copied: `src/io/xeros/content/donationrewards/DonationRewardsPlayerSaveEntry.java`

- Name: Donor Pet Management
  - Main files: `src/io/xeros/content/donorpet/PetManagement.java`
  - Rewards: Donor coin pet storage/return.
  - Progression purpose: Donator pet utility.
  - Similar systems that can be copied: `src/io/xeros/content/donorpet/PetManagement.java`

- Name: Donor cosmetics
  - Main files: `src/io/xeros/content/donor/CosmeticManager.java`
  - Rewards: Cosmetic donor features.
  - Progression purpose: Donator cosmetic identity.
  - Similar systems that can be copied: `src/io/xeros/content/donor/CosmeticManager.java`

## Best Copy Patterns

- New boss with mechanics: copy `src/io/xeros/content/bosses/hydra/AlchemicalHydra.java`, `src/io/xeros/content/bosses/zulrah/Zulrah.java`, or an instance file under `src/io/xeros/content/bosses/grotesqueguardians/`.
- New simple boss with standard drops: add mechanics under `src/io/xeros/content/bosses/`, then use `src/io/xeros/model/entity/npc/drops/DropManager.java` data flow and keep death hooks minimal.
- New raid/minigame instance: copy `src/io/xeros/content/minigames/tob/instance/TobInstance.java`, `src/io/xeros/content/minigames/TOA/instance/TombsOfAmascutInstance.java`, or `src/io/xeros/content/minigames/arbograve/instance/ArbograveInstance.java`.
- New upgrade recipe: add enum data to `src/io/xeros/content/upgrade/UpgradeMaterials.java` or `src/io/xeros/content/fusion/FusionMaterials.java`.
- New shop stock: prefer the ShopDef YAML route loaded by `src/io/xeros/model/definitions/ShopDef.java`. Only edit `src/io/xeros/model/shops/ShopAssistant.java` for new currency behavior.
- New achievement: add the counter in `src/io/xeros/content/achievement/AchievementType.java`, the achievement in `src/io/xeros/content/achievement/Achievements.java`, and increment from the closest existing content hook.
- New daily/weekly objective: copy `src/io/xeros/content/taskmaster/Tasks.java` or `src/io/xeros/content/event/eventcalendar/EventChallenge.java`.
- New persistent data: use `src/io/xeros/model/entity/player/save/PlayerSaveEntry.java` when possible and avoid adding legacy keys to `src/io/xeros/model/entity/player/save/PlayerSave.java`.
