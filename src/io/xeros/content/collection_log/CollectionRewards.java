package io.xeros.content.collection_log;

import io.xeros.Server;
import io.xeros.content.battlepass.Pass;
import io.xeros.content.item.lootable.impl.ArbograveChestItems;
import io.xeros.content.item.lootable.impl.HesporiChestItems;
import io.xeros.content.item.lootable.impl.RaidsChestRare;
import io.xeros.content.item.lootable.impl.TheatreOfBloodChest;
import io.xeros.content.items.aoeweapons.AoeWeapons;
import io.xeros.content.trails.TreasureTrailsRewardItem;
import io.xeros.content.trails.TreasureTrailsRewards;
import io.xeros.content.upgrade.UpgradeMaterials;
import io.xeros.model.Npcs;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.discord.Discord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum CollectionRewards {

    BARREL_CHEST(6342, new GameItem[]{new GameItem(696,8),
            new GameItem(6679,50), new GameItem(6677, 5)}),

    DAGGANOTH_SUPREME(2265, new GameItem[]{new GameItem(696,20),
            new GameItem(6679,50), new GameItem(6677, 5)}),

    DAGANNOTH_PRIME(2266, new GameItem[]{new GameItem(696,20),
            new GameItem(6679,50), new GameItem(6677, 5)}),

    DAGANNOTH_REX(2267, new GameItem[]{new GameItem(696,20),
            new GameItem(6679,5), new GameItem(6677, 5)}),

    KING_BLACK_DRAGON(239, new GameItem[]{new GameItem(696,10), new GameItem(6677,10),
            new GameItem(6678, 5)}),

    KALPHITE_QUEEN(965, new GameItem[]{new GameItem(696,10), new GameItem(6677,10),
            new GameItem(6678, 5)}),

    BANODS(2215, new GameItem[]{new GameItem(696,8),
            new GameItem(6677,25), new GameItem(6678,25),
            new GameItem(20370,1)}),

    KRIL(3129, new GameItem[]{new GameItem(696,80),
            new GameItem(6677,50), new GameItem(6678,25),
            new GameItem(20374,1)}),

    KREE(3162, new GameItem[]{new GameItem(696,80),
            new GameItem(6677,50), new GameItem(6678,25),
            new GameItem(20368,1)}),

    COMMANDER(2205, new GameItem[]{new GameItem(696,80),
            new GameItem(6677,50), new GameItem(6678,25),
            new GameItem(20372,1)}),

    CORP(319, new GameItem[]{new GameItem(696,200),
            new GameItem(6677,100), new GameItem(6678,150)}),

    KRAKEN(494, new GameItem[]{new GameItem(696,20),
            new GameItem(6679,25), new GameItem(6677,5)}),

    CERB(5862, new GameItem[]{new GameItem(696,60),
            new GameItem(6677,50), new GameItem(6678,10)}),

    SIRE(5890, new GameItem[]{new GameItem(696,20),
            new GameItem(6677,10), new GameItem(6678, 5)}),

    DEMONIC(7145, new GameItem[]{new GameItem(696,20), new GameItem(6679, 25),
            new GameItem(6677,50), new GameItem(6678, 25)}),

    SHAMAN(6766, new GameItem[]{new GameItem(696,20), new GameItem(6679, 25),
            new GameItem(6677,50), new GameItem(6678, 25)}),

    VORKATH(8028, new GameItem[]{new GameItem(696,10),
            new GameItem(6677,50), new GameItem(6678, 25)}),

    ZULRAH(2042, new GameItem[]{new GameItem(696,60),
            new GameItem(6677,20), new GameItem(6678,10),
            new GameItem(12922,1)}),

    HYDRA(8621, new GameItem[]{new GameItem(696,20),
            new GameItem(6677,50), new GameItem(6678,25)}),

    NIGHTMARE(9425, new GameItem[]{new GameItem(696,30),
            new GameItem(6677,50), new GameItem(6678,50)}),

    SARACHNIS(8713, new GameItem[]{new GameItem(696,20),
            new GameItem(6679,50), new GameItem(6677,25),
            new GameItem(6678,15)}),

    GUARDIANS(7888, new GameItem[]{new GameItem(696,40),
            new GameItem(6679,50), new GameItem(6677,25),
            new GameItem(6678,12)}),

    BRYOPHYA(8195, new GameItem[]{new GameItem(696,20),
            new GameItem(6677,10)}),

    OBOR(7416, new GameItem[]{new GameItem(696,20),
            new GameItem(6677,10)}),

    NEX(11278, new GameItem[]{new GameItem(696,40),
            new GameItem(6679,100), new GameItem(6677,50),
            new GameItem(6678,250)}),

    MALEDICTUS(5126, new GameItem[]{new GameItem(696,100),
            new GameItem(6678,250), new GameItem(22093,50),
            new GameItem(21262,1), new GameItem(26515,1)}),

    GALVEK(8096, new GameItem[]{new GameItem(696,800), new GameItem(2396,1),
            new GameItem(2400,10), new GameItem(6678,250)}),

    VETRION(6611, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,50000)}),

    CALLISTO(6503, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,50000)}),

    SCORPIA(6615, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,50000)}),

    VENENATIS(6610, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,50000)}),

    ELEMENTAL(2054, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,50000)}),

    FANATIC(6619, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,50000)}),

    ARCHAEOLOGIST(6618, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,50000)}),

    OLM(7554, new GameItem[]{new GameItem(696,200), new GameItem(22885,15),
            new GameItem(2403,1), new GameItem(6678,50), new GameItem(12585,25)}),

    TOB(8360, new GameItem[]{new GameItem(696,200), new GameItem(22885,15),
            new GameItem(2403,1), new GameItem(6678,50), new GameItem(19895,25)}),

    DHAROK(1673, new GameItem[]{new GameItem(696,20),
            new GameItem(6677,50), new GameItem(6678,25)}),

    AHRIM(1672, new GameItem[]{new GameItem(696,20),
            new GameItem(6677,50), new GameItem(6678,25)}),

    GUTHAN(1674, new GameItem[]{new GameItem(696,20),
            new GameItem(6677,50), new GameItem(6678,25)}),

    KARIL(1675, new GameItem[]{new GameItem(696,20),
            new GameItem(6677,50), new GameItem(6678,25)}),

    TORAG(1676, new GameItem[]{new GameItem(696,20),
            new GameItem(6677,50), new GameItem(6678,25)}),

    VERAC(1677, new GameItem[]{new GameItem(696,20),
            new GameItem(6677,50), new GameItem(6678,25)}),

    EASY(1, new GameItem[]{new GameItem(6769,1), new GameItem(696,10),
            new GameItem(6677,25), new GameItem(6678,10), new GameItem(2801,50)}),

    MEDIUM(2, new GameItem[]{new GameItem(6769,1), new GameItem(696,15),
            new GameItem(6677,50), new GameItem(6678,20), new GameItem(2722,50)}),

    HARD(3, new GameItem[]{new GameItem(6769,1), new GameItem(696,20),
            new GameItem(6677,75), new GameItem(6678,40), new GameItem(19835,50)}),

    MASTER(4, new GameItem[]{new GameItem(6769,1), new GameItem(696,25),
            new GameItem(6677,10), new GameItem(6678,80), new GameItem(19941,1)}),

    PETS(5, new GameItem[]{new GameItem(2396,1), new GameItem(696,300),
            new GameItem(6678,250), new GameItem(30023,1)}),

    WEAPONS(6, new GameItem[]{new GameItem(696,1000),
            new GameItem(6677,500), new GameItem(6678,250)}),

    ARMOR(7, new GameItem[]{new GameItem(2396,1),
            new GameItem(696,1000), new GameItem(6677,500),
            new GameItem(6678,250)}),

    ACCESSORY(8, new GameItem[]{new GameItem(696,200),
            new GameItem(6677,100), new GameItem(6678,50)}),

    MISC(9, new GameItem[]{new GameItem(696,200),
            new GameItem(6677,100), new GameItem(6678,50)}),

    ARAPHEL_RED(8172, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,75000)}),

    LIGHT(1025, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,75000)}),
    GHOST(1429, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,75000)}),
    RANGER(1656, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,75000)}),
    ARAPHEL(8164, new GameItem[]{new GameItem(6677,50),
            new GameItem(6678,25), new GameItem(696,40),
            new GameItem(2996,2500), new GameItem(13307,75000)}),

    QUEEN(8781, new GameItem[]{new GameItem(2396,1),
            new GameItem(696,1000), new GameItem(2400,50),
            new GameItem(8167,10), new GameItem(6678,500)}),

    CREATOR(10531, new GameItem[]{new GameItem(786,1),
            new GameItem(696,1400), new GameItem(27285,10),
            new GameItem(8167,15), new GameItem(6678,1000)}),

    DESTRUCTOR(10532, new GameItem[]{new GameItem(761,1),
            new GameItem(696,2000), new GameItem(6805,100),
            new GameItem(6678,1500), new GameItem(33067,1)}),

    ARBOGRAVE(1101, new GameItem[]{new GameItem(696,500),
            new GameItem(27285,10), new GameItem(2400,50),
            new GameItem(6678,250), new GameItem(6680,50)}),

    PERKFINDER(1230, new GameItem[]{new GameItem(696,1000),
            new GameItem(33112,1), new GameItem(6677,1000),
            new GameItem(6678,500), new GameItem(8232,1)}),

    HESPORI(8583, new GameItem[]{new GameItem(696,100),
            new GameItem(6678,100), new GameItem(22883,2),
            new GameItem(22885,2), new GameItem(22875,2)})

    ;

    public int NpcID;
    public GameItem[] Rewards;

    CollectionRewards(int NpcID, GameItem[] Rewards) {
        this.NpcID = NpcID;
        this.Rewards = Rewards;
    }

    public static ArrayList<GameItem> getForNpcID(int npcID) {
        ArrayList<GameItem> collectionRewards = new ArrayList<>();
        for (CollectionRewards value : CollectionRewards.values()) {
            if (value.NpcID == npcID) {
                collectionRewards.addAll(Arrays.asList(value.Rewards));
            }
        }
        return collectionRewards;
    }

    public static boolean handleButton(Player player, int ID) {
        if (ID == 23236) {
            if (player.getCollectionLog().getCollections().containsKey(player.getCollectionLogNPC()+ "")) {
                ArrayList<GameItem> itemsObtained = player.getCollectionLog().getCollections().get(player.getCollectionLogNPC()+ "");
                if (itemsObtained != null) {
                    List<GameItem> drops = Server.getDropManager().getNPCdrops(player.getCollectionLogNPC());
                    if (player.getCollectionLogNPC() == 7554) {
                        drops = RaidsChestRare.getRareDrops();
                    } else if (player.getCollectionLogNPC() >= 1 && player.getCollectionLogNPC() <= 4) {
                        drops.clear();
                        drops = TreasureTrailsRewardItem.toGameItems(TreasureTrailsRewards.getRewardsForType(player.getCollectionLogNPC()));
                    } else if (player.getCollectionLogNPC() == 5) {
                        drops.clear();
                        drops = PetHandler.getPetIds(true);
                    } else if (player.getCollectionLogNPC() == 6) {
                        drops.clear();
                        for (UpgradeMaterials value : UpgradeMaterials.values()) {
                            if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.WEAPON)) {
                                drops.add(value.getReward());
                            }
                        }
                    } else if (player.getCollectionLogNPC() == 7) {
                        drops.clear();
                        for (UpgradeMaterials value : UpgradeMaterials.values()) {
                            if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.ARMOUR)) {
                                drops.add(value.getReward());
                            }
                        }
                    } else if (player.getCollectionLogNPC() == 8) {
                        drops.clear();
                        for (UpgradeMaterials value : UpgradeMaterials.values()) {
                            if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.ACCESSORY)) {
                                drops.add(value.getReward());
                            }
                        }
                    } else if (player.getCollectionLogNPC() == 9) {
                        drops.clear();
                        for (UpgradeMaterials value : UpgradeMaterials.values()) {
                            if (value.isRare() && value.getType().equals(UpgradeMaterials.UpgradeType.MISC)) {
                                drops.add(value.getReward());
                            }
                        }
                    } else if (player.getCollectionLogNPC() == 10) {
                        drops.clear();
                        for (AoeWeapons value : AoeWeapons.values()) {
                            drops.add(new GameItem(value.ID));
                        }
                    } else if (player.getCollectionLogNPC() == Npcs.THE_MAIDEN_OF_SUGADINTI) {
                        drops = TheatreOfBloodChest.getRareDrops();
                    } else if (player.getCollectionLogNPC() == 1101) {
                        drops = ArbograveChestItems.getRareDrops();
                    } else if (player.getCollectionLogNPC() == 8583) {
                        drops = HesporiChestItems.getRareDrops();
                    }
                    if (drops != null &&
                            drops.size() == itemsObtained.size()
                            && !player.getClaimedLog().contains(player.getCollectionLogNPC())) {
                        player.getClaimedLog().add(player.getCollectionLogNPC());

                        for (GameItem gameItem : CollectionRewards.getForNpcID(player.getCollectionLogNPC())) {
                            player.getItems().addItemUnderAnyCircumstance(gameItem.getId(), gameItem.getAmount());
                        }
                        player.sendMessage("@gre@Your rewards have now been claimed!");

//                        for (CollectionRewards value : CollectionRewards.values()) {
//                            if (value.NpcID == player.getCollectionLogNPC()) {
//                                Discord.jda.getTextChannelById(1227064467628490843L).sendMessage(player.getDisplayName() + " has just completed " + value.name().toLowerCase()).queue();
//                                break;
//                            }
//                        }

                        int[] bossIds = {6342,2265,2266,2267,239,965,2215,3129,3162,2205,319,494,5862,5890,7145,6766,8028,2042,8621,9425,8713,7888,8195,7416,11278, 5126, 8096};
                        int[] wildyIds = {6611, 6503, 6615, 6610, 2054, 6619, 6618, 8172, 8164};
                        int[] raidIds = {7554, 8360};
                        int[] other = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
                        if (Arrays.stream(bossIds).anyMatch(i -> i == player.getCollectionLogNPC())) {
                            Pass.addExperience(player,3);
                        } else if (Arrays.stream(wildyIds).anyMatch(i -> i == player.getCollectionLogNPC())) {
                            Pass.addExperience(player,4);
                        } else if (Arrays.stream(raidIds).anyMatch(i -> i == player.getCollectionLogNPC())) {
                            Pass.addExperience(player,5);
                        } else if (Arrays.stream(other).anyMatch(i -> i == player.getCollectionLogNPC())) {
                            Pass.addExperience(player,5);
                        }

                    } else if (drops != null && drops.size() == itemsObtained.size()
                            && player.getClaimedLog().contains(player.getCollectionLogNPC())) {
                        player.sendMessage("@red@You've already claimed the reward from this log!");
                    } else if (drops != null &&
                            drops.size() != itemsObtained.size()) {
                        player.sendMessage("@red@You have not completed the log yet!");
                    }
                }
            }
            return true;
        }


        return false;
    }

}
