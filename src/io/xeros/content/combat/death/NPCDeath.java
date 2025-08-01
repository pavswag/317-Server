package io.xeros.content.combat.death;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.achievement_diary.impl.FremennikDiaryEntry;
import io.xeros.content.achievement_diary.impl.MorytaniaDiaryEntry;
import io.xeros.content.battlepass.Pass;
import io.xeros.content.bosses.Hunllef;
import io.xeros.content.bosses.Kraken;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.bosses.nightmare.NightmareConstants;
import io.xeros.content.bosses.wildypursuit.FragmentOfSeren;
import io.xeros.content.bosses.wildypursuit.TheUnbearable;
import io.xeros.content.bosspoints.BossPoints;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.event.eventcalendar.EventChallenge;
import io.xeros.content.events.monsterhunt.MonsterHunt;
import io.xeros.content.minigames.warriors_guild.AnimatedArmour;
import io.xeros.content.skills.Skill;
import io.xeros.content.skills.hunter.impling.ItemRarity;
import io.xeros.model.Graphic;
import io.xeros.model.Npcs;
import io.xeros.model.collisionmap.RegionProvider;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.entity.EntityProperties;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.drops.DropManager;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.*;
import io.xeros.model.entity.player.lock.CompleteLock;
import io.xeros.model.items.EquipmentSet;
import io.xeros.model.items.GameItem;
import io.xeros.util.Location3D;
import io.xeros.util.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class NPCDeath {

    public static void dropItems(NPC npc) {
        if (npc == null) return;

        Player c = PlayerHandler.players[npc.killedBy];

        if (c == null) return;

        dropItemsFor(npc, c, npc.getNpcId());
    }

    public static void dropItemsFor(NPC npc, Player player, int npcId) {
        if (npc == null) return;

        if (player.getTargeted() != null && npc.equals(player.getTargeted())) {
            player.setTargeted(null);
            player.getPA().sendEntityTarget(0, npc);
        }
        if(player.hasPetSpawned && Misc.random(1, 100) == 100) {
            List<Position> positions = new ArrayList<>();
            List<NPC> npcs = new ArrayList<>();
            for(int i = 0; i < 5; i++) {
                Position position;
                do position = new Position(Misc.random(player.absX - 3, player.absX + 3), Misc.random(player.absY - 3, player.absY + 3), player.heightLevel);
                while (positions.contains(position) || RegionProvider.getGlobal().isBlocked(position));
                positions.add(position);
            }
            CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
                @Override
                public void execute(CycleEventContainer container) {
                    int ticks = container.getTotalTicks();
                    switch (ticks) {
                        case 1 -> {
                            player.lock(new CompleteLock());
                            player.sendMessage("Your pet triggers a massacre event!");
                            //player.currentPetNpc.startAnimation(11446);
                        }
                        case 2 -> {
                            // player.currentPetNpc.forceChat("Kill them all!");
                            player.sendMessage("Your pet kills them all!");
                            positions.forEach(position -> {
                                NPC npc = new NPC(npcId, position);
                                npc.spawnedBy = player.getIndex();
                                npc.getBehaviour().setRespawn(false);
                                npc.getBehaviour().setAggressive(false);
                                npc.getBehaviour().setRespawnWhenPlayerOwned(false);
                                npc.getHealth().setCurrentHealth(1);
                                npc.getHealth().setMaximumHealth(1);
                                npc.addEntityProperty(EntityProperties.SHADOW);
                                npcs.add(npc);
                               // NPCSpawning.spawn(npc);
                            });
                        }
                        case 3 -> {
                            // player.currentPetNpc.startGraphic(new Graphic(2698));
                        }
                        case 8 -> {
                            npcs.forEach(npc -> {
                                npc.startGraphic(new Graphic(2697));
                                npc.appendDamage(player, 1, Hitmark.HIT);
                            });
                        }
                        case 9 -> {
                            player.sendMessage("Your pet increases your drops!");
                            player.unlock();
                            container.stop();
                        }
                    }
                }
            }, 1);
        }
        player.getAchievements().kill(npc);

        PetHandler.rollOnNpcDeath(player, npc);

        if (npcId >= 1610 && npcId <= 1612) {
            player.getQuestTab().updateInformationTab();
        }


        if (npcId == 2266 || npcId == 2267 || npcId == 2265) {
            player.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.KILL_DAGANNOTH_KINGS);
        }
        if (npcId == 411) {
            player.getDiaryManager().getFremennikDiary().progress(FremennikDiaryEntry.KILL_KURASK);
        }

        if (npcId == 9021 || npcId == 9022 || npcId == 9023 || npcId == 9024) {
            player.hunllefDead = true;
            Hunllef.rewardPlayers(player);
        }
        if (npcId == 1047) {
            player.getDiaryManager().getMorytaniaDiary().progress(MorytaniaDiaryEntry.KILL_CAVE_HORROR);
        }

        if (npcId == 1673) { //barrows npcs
            Achievements.increase(player, AchievementType.DH_KILLS, 1);
        }
        if (npcId == 5744 || npcId == 5762) {
            player.setShayPoints(player.getShayPoints() + 5);
            player.sendMessage("@red@You gain 5 point for killing the Penance! You now have " + player.getShayPoints()
                    + " Assault Points.");
        }

        if (npc.getNpcId() == 3162 || npc.getNpcId() == 3129 || npc.getNpcId() == 2205 || npc.getNpcId() == 2215) {
            player.getEventCalendar().progress(EventChallenge.KILL_X_GODWARS_BOSSES_OF_ANY_TYPE);
        }
        if (npc.getNpcId() == 9293) {
            player.getEventCalendar().progress(EventChallenge.KILL_BASILISK_KNIGHTS_X_TIMES);
        }

        if (npc.getNpcId() == 2042 || npc.getNpcId() == 2043 || npc.getNpcId() == 2044) {
            player.getEventCalendar().progress(EventChallenge.KILL_ZULRAH_X_TIMES);
        }
        if (npc.getNpcId() == 9021) {
            player.getEventCalendar().progress(EventChallenge.KILL_HUNLLEF_X_TIMES);
        }
        if (IntStream.of(DropManager.wildybossesforgiveaway).anyMatch(id -> id == npc.getNpcId()) && player.getPosition().inWild()) {
            player.getEventCalendar().progress(EventChallenge.KILL_X_WILDY_BOSSES);
        }
        if (npcId >= 7931 && npcId <= 7940) {
            player.getEventCalendar().progress(EventChallenge.KILL_X_REVS_IN_WILDY);
        }

        if (npcId == Npcs.CORPOREAL_BEAST) {
            NPCHandler.kill(Npcs.DARK_ENERGY_CORE, npc.heightLevel);
        }
//        if (npcId == 1028) {
//            NPCHandler.kill(8030, player.heightLevel);
//        }
        if (npcId == 1802) {
            NPCHandler.kill(Npcs.DARK_ENERGY_CORE, npc.heightLevel);
        }
        if (npcId == 12821) {
            NPCHandler.kill(Npcs.SKELETAL_MYSTIC_2, 0);
        }
        if (npcId == 7278) {
            if ((player.getSlayer().getTask().isPresent() && player.getSlayer().getTask().get().getPrimaryName().equals("nechryael"))) {
                player.getPA().addSkillXPMultiplied(100, Skill.SLAYER.getId(), true);
            }
        }

        if (npcId == Npcs.DUSK_9) {
            Achievements.increase(player, AchievementType.GROTESQUES, 1);
        }
        if (npcId == Npcs.ALCHEMICAL_HYDRA_7) {
            Achievements.increase(player, AchievementType.HYDRA, 1);
        }

        if (npcId == 2266 || npcId == 2267 || npcId == 2265) {
            if ((player.getSlayer().getTask().isPresent() && player.getSlayer().getTask().get().getPrimaryName().equals("dagannoth"))) {
                player.getPA().addSkillXPMultiplied(165, Skill.SLAYER.getId(), true);
            }
        }

        if (npcId == 1673 || npcId == 1674 || npcId == 1677 || npcId == 1676 || npcId == 1675
                || npcId == 1672) {//barrows
            Achievements.increase(player, AchievementType.BARROWS_KILLS, 1);
            player.getEventCalendar().progress(EventChallenge.KILL_X_BARROWS_BROTHERS);
            if (EquipmentSet.AHRIM.isWearingBarrows(player) || EquipmentSet.KARIL.isWearingBarrows(player)
                    || EquipmentSet.DHAROK.isWearingBarrows(player)
                    || EquipmentSet.VERAC.isWearingBarrows(player)
                    || EquipmentSet.GUTHAN.isWearingBarrows(player)
                    || EquipmentSet.TORAG.isWearingBarrows(player)) {
                player.getDiaryManager().getMorytaniaDiary().progress(MorytaniaDiaryEntry.BARROWS_CHEST);
            }
        }
        if (npcId == 7144 || npcId == 7145 || npcId == 7146) {
            player.getEventCalendar().progress(EventChallenge.KILL_X_DEMONIC_GORILLAS, 1);
        }

        if (npcId == 1739 || npcId == 1740 || npcId == 1741 || npcId == 1742) {
            player.getEventCalendar().progress(EventChallenge.GAIN_X_PEST_CONTROL_POINTS, 7);
            player.pcPoints += 7;
        }

        if (npcId == 8781 && npc.getHealth().getCurrentHealth() <= 0) {
            player.setDonorBossKC(player.getDonorBossKC() + 1);
//            player.getPA().movePlayer(1944,5359,0);
        }
        if (npcId == 10531 && npc.getHealth().getCurrentHealth() <= 0) {
            player.setDonorBossKCx(player.getDonorBossKCx() + 1);
//            player.getPA().movePlayer(1944,5359,0);
        }
        if (npcId == 10532 && npc.getHealth().getCurrentHealth() <= 0) {
            player.setDonorBossKCy(player.getDonorBossKCy() + 1);
//            player.getPA().movePlayer(1944,5359,0);
        }

        if (npcId == FragmentOfSeren.NPC_ID && npc.getHealth().getCurrentHealth() <= 0) {
            // Player p = PlayerHandler.players[npc.killedBy];
            int randomPkp = Misc.random(15) + 10;
            player.pkp += randomPkp * (player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33074) ? 2 : 1);
            player.getQuestTab().updateInformationTab();
            MonsterHunt.setCurrentLocation(null);
            player.sendMessage("Well done! You killed Seren!");
            player.sendMessage("You received: " + randomPkp + " PK Points for killing the wildy boss.");

        }

        if (npcId == TheUnbearable.NPC_ID && npc.getHealth().getCurrentHealth() <= 0) {
            int randomPkp = Misc.random(15) + 10;
            player.pkp += randomPkp * (player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33074) ? 2 : 1);
            player.getQuestTab().updateInformationTab();
            MonsterHunt.setCurrentLocation(null);
            player.sendMessage("Well done! You killed The Unbearable!");
            player.sendMessage("You received: " + randomPkp + " PK Points for killing the wildy boss.");
        }

        if (npcId == Hespori.NPC_ID && npc.getHealth().getCurrentHealth() <= 0) {
            player.getQuestTab().updateInformationTab();
            player.sendMessage("Well done! You killed Hespori!");
        }
        int dropX = npc.absX;
        int dropY = npc.absY;
        int dropHeight = npc.heightLevel;

        if (!PathFinder.getPathFinder().accessable(player, dropX, dropY)) {
            for (Position border : npc.getBorder()) {
                if (PathFinder.getPathFinder().accessable(player, dropX, dropY)) {
                    dropX = border.getX();
                    dropY = border.getY();
                    break;
                }
            }
        }

        if (npcId == 492 || npcId == 494 || npcId == NightmareConstants.NIGHTMARE_ACTIVE_ID || npcId == Npcs.VORKATH) {
            dropX = player.absX;
            dropY = player.absY;
        }
        if (npcId == 2042 || npcId == 2043 || npcId == 2044
                || npcId == 6720) {
            dropX = 2268;
            dropY = 3069;
            player.getItems().addItem(12938, 1);
            Achievements.increase(player, AchievementType.SLAY_ZULRAH, 1);
            player.getZulrahEvent().stop();
        }
        if (npcId == Kraken.KRAKEN_ID) {
            dropX = 2280;
            dropY = 10031;
        }

        /**
         * Warriors guild
         */
        player.getWarriorsGuild().dropDefender(npc.absX, npc.absY);
        if (AnimatedArmour.isAnimatedArmourNpc(npcId)) {
            if (npc.getX() == 2851 && npc.getY() == 3536) {
                dropX = 2851;
                dropY = 3537;
                AnimatedArmour.dropTokens(player, npcId, npc.absX, npc.absY + 1);
            } else if (npc.getX() == 2857 && npc.getY() == 3536) {
                dropX = 2857;
                dropY = 3537;
                AnimatedArmour.dropTokens(player, npcId, npc.absX, npc.absY + 1);
            } else {
                AnimatedArmour.dropTokens(player, npcId, npc.absX, npc.absY);
            }
        }

        if (npc.getNpcId() == 5862) {
            Achievements.increase(player, AchievementType.SLAY_CERB, 1);
        } else if (npc.getNpcId() == 319) {
            Achievements.increase(player, AchievementType.SLAY_CORP, 1);
        } else if (npc.getNpcId() == 239) {
            Achievements.increase(player, AchievementType.SLAY_KBD, 1);
        } else if (npc.getNpcId() >= 5886 && npc.getNpcId() <= 5891) {
            Achievements.increase(player, AchievementType.SLAY_SIRE, 1);
        } else if (npc.getNpcId() == 494) {
            Achievements.increase(player, AchievementType.SLAY_KRAKEN, 1);
        } else if (npc.getNpcId() == 6503) {
            Achievements.increase(player, AchievementType.SLAY_CALLISTO, 1);
        } else if (npc.getNpcId() == 6615) {
            Achievements.increase(player, AchievementType.SLAY_SCORPIA, 1);
        } else if (npc.getNpcId() == 6610) {
            Achievements.increase(player, AchievementType.SLAY_VENENATIS, 1);
        } else if (npc.getNpcId() == 2054) {
            Achievements.increase(player, AchievementType.SLAY_CHAOSELE, 1);
        } else if (npc.getNpcId() == 6619) {
            Achievements.increase(player, AchievementType.SLAY_CHAOSFANATIC, 1);
        } else if (npc.getNpcId() == 6618) {
            Achievements.increase(player, AchievementType.SLAY_ARCHAEOLOGIST, 1);
        } else if (npc.getNpcId() == 8164) {
            Achievements.increase(player, AchievementType.SLAY_SHADOWARAPHAEL, 1);
        } else if (npc.getNpcId() == 8172) {
            Achievements.increase(player, AchievementType.SLAY_ARAPHAEL, 1);
        }

        Location3D location = new Location3D(dropX, dropY, dropHeight);
        int amountOfDrops = 1;
        if (isDoubleDrops()) {
            amountOfDrops++;
        }

        int bossPoints = BossPoints.getPointsOnDeath(npc);
        BossPoints.addPoints(player, bossPoints, false);

        if (npc.getInstance() instanceof io.xeros.content.instances.BossInstanceManager.BossInstanceArea area) {
            io.xeros.content.instances.BossInstanceManager.BossTier tier = area.getTier();
            int newCount = player.getTierKillCounts().merge(tier, 1, Integer::sum);
            if (newCount >= tier.getRequiredKillCountToUnlockNext()) {
                io.xeros.content.instances.BossInstanceManager.BossTier next = tier.getNextTier();
                if (next != null && player.getUnlockedBossTiers().add(next)) {
                    player.sendMessage("\uD83C\uDF89 You've unlocked " + next.name().replace('_', ' ') + "! Return to the instance portal to challenge new bosses!");
                }
            }
        }

        if (NpcDef.forId(npcId).getCombatLevel() >= 1) {
            player.getNpcDeathTracker().add(NpcDef.forId(npcId).getName(), NpcDef.forId(npcId).getCombatLevel(), bossPoints);
        }

        Server.getDropManager().create(player, npc, location, amountOfDrops, npcId);
    }

    public static void announce(Player player, GameItem item, int npcId) {
        if (player == null || item == null) return;

        if (player.NexUnlocked && item.getId() == 26362 || player.NexUnlocked && item.getId() == 26364 || player.NexUnlocked && item.getId() == 26360 || player.NexUnlocked && item.getId() == 26358) {
            Achievements.increase(player, AchievementType.UNIQUE_DROPS, 1);
            return;
        }

        if (!Boundary.isIn(player, Boundary.STAFF_ZONE) && player.announce) {
            int kc = player.getNpcDeathTracker().getKc(NpcDef.forId(npcId).getName());
            announceKc(player, item, NpcDef.forId(npcId).getName(), kc);        }

        Achievements.increase(player, AchievementType.UNIQUE_DROPS, 1);

        player.kcCounter++;

        if (player.kcCounter >= 200) {
            Pass.addExperience(player, 5);
            player.kcCounter = 0;
        }
    }

    public static void announceKc(Player player, GameItem item, String npcName, int kc) {
        if (player == null || item == null) return;
//        if (player.getDisplayName().equalsIgnoreCase("Mercy")) {
//            return;
//        }
        ItemRarity rarity = item.getRarity();
        if (rarity == null || (rarity != ItemRarity.RARE && rarity != ItemRarity.VERY_RARE)) {
            return;
        }

        String from = npcName != null && !npcName.isEmpty() ? " from " + npcName : "";
        PlayerHandler.executeGlobalMessage("@pur@" + player.getDisplayNameFormatted() +
                " received a drop: " + ItemDef.forId(item.getId()).getName() + " x " + item.getAmount() +
                from + " at <col=E9362B>" + kc + "</col>@pur@ kills.");
    }

    public static boolean isDoubleDrops() {
        return (Configuration.DOUBLE_DROPS_TIMER > 0 || Configuration.DOUBLE_DROPS);
    }
}
