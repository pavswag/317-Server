package io.xeros.content.item.lootable.impl;

import com.google.common.collect.Lists;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.bosspoints.BossPoints;
import io.xeros.content.event.eventcalendar.EventChallenge;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.model.Items;
import io.xeros.model.Npcs;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ImmutableItem;
import io.xeros.util.Misc;

import java.util.*;

public class TombsOfAmascutChest implements Lootable {

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    static {
        items.put(LootRarity.COMMON, Arrays.asList(
                new GameItem(Items.DEATH_RUNE, 6000),
                new GameItem(Items.BLOOD_RUNE, 6000),
                new GameItem(Items.ANGLERFISH_NOTED, 100),
                new GameItem(Items.OVERLOAD_4, 2),
                new GameItem(Items.GOLD_ORE_NOTED, 860),
                new GameItem(Items.ADAMANTITE_ORE_NOTED, 250),
                new GameItem(Items.RUNITE_ORE_NOTED, 172),
                new GameItem(Items.GRIMY_CADANTINE_NOTED, 160),
                new GameItem(Items.GRIMY_AVANTOE_NOTED, 148),
                new GameItem(Items.ANGLERFISH_NOTED, 100),
                new GameItem(Items.GRIMY_TOADFLAX_NOTED, 144),
                new GameItem(Items.GRIMY_KWUARM_NOTED, 143),
                new GameItem(Items.GRIMY_IRIT_LEAF_NOTED, 140),
                new GameItem(Items.GRIMY_RANARR_WEED_NOTED, 136),
                new GameItem(Items.GRIMY_SNAPDRAGON_NOTED, 132),
                new GameItem(Items.OVERLOAD_4, 2),
                new GameItem(Items.GRIMY_LANTADYME_NOTED, 131),
                new GameItem(Items.GRIMY_DWARF_WEED_NOTED, 128),
                new GameItem(Items.ANGLERFISH_NOTED, 100),
                new GameItem(Items.GRIMY_TORSTOL_NOTED, 124),
                new GameItem(Items.BATTLESTAFF_NOTED, 118),
                new GameItem(Items.RUNE_BATTLEAXE_NOTED, 14),
                new GameItem(Items.RUNE_PLATEBODY_NOTED, 14),
                new GameItem(Items.ANGLERFISH_NOTED, 100),
                new GameItem(Items.OVERLOAD_4, 2),
                new GameItem(Items.RUNE_CHAINBODY_NOTED, 14),
                new GameItem(Items.RUNE_DRAGON_MASK, 1),
                new GameItem(2528),
                new GameItem(23300),
                new GameItem(995, 75_000_000)
        ));

        items.put(LootRarity.RARE, Arrays.asList(
                new GameItem(25975),
                new GameItem(22978),
                new GameItem(24780),

                new GameItem(2528),
                new GameItem(28693),
                new GameItem(2528),
                new GameItem(2528),
                new GameItem(23754),
                new GameItem(26374),

                new GameItem(12785),
                new GameItem(25916),

                new GameItem(13346),
                new GameItem(2697),
                new GameItem(Items.OSMUMTENS_FANG),
                new GameItem(Items.OSMUMTENS_FANG),
                new GameItem(Items.OSMUMTENS_FANG),
                new GameItem(27610),

                new GameItem(27277),
                new GameItem(28951),
                new GameItem(27352),
                new GameItem(27385),
                new GameItem(27384),
                new GameItem(27382),
                new GameItem(27383),
                new GameItem(28693),
                new GameItem(Items.MASORI_BODY),
                new GameItem(Items.MASORI_CHAPS),
                new GameItem(Items.MASORI_MASK),
                new GameItem(Items.ELIDINIS_WARD),
                new GameItem(Items.ELIDINIS_WARD),
                new GameItem(Items.ELIDINIS_WARD)

        ));
    }

    public static ArrayList<GameItem> getRareDrops() {
        ArrayList<GameItem> drops = new ArrayList<>();
        List<GameItem> found = items.get(LootRarity.RARE);
        for(GameItem f : found) {
            boolean foundItem = false;
            for(GameItem drop : drops) {
                if (drop.getId() == f.getId()) {
                    foundItem = true;
                    break;
                }
            }
            if (!foundItem) {
                drops.add(f);
            }
        }
        return drops;
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    public static List<GameItem> getRandomItems(boolean rollRares, int size) {
        List<GameItem> rewards = Lists.newArrayList();
        int rareChance = 8;
        if (Hespori.activeKronosSeed) {
            rareChance = 4;
        }

        if (rollRares && Misc.trueRand(rareChance) == 0) {
            rewards.add(Misc.getRandomItem(items.get(LootRarity.RARE)));
        } else {
            for (int count = 0; count < 3; count++) {
                rewards.add(Misc.getRandomItem(items.get(LootRarity.COMMON)));
            }
        }
        return rewards;
    }

    public static boolean containsRare(List<GameItem> itemList) {
        return items.get(LootRarity.RARE).stream().anyMatch(rareItem -> itemList.stream().anyMatch(itemReward -> rareItem.getId() == itemReward.getId()));
    }

    /**
     * Reward items that are generated when the treasure room is initialised.
     */
    public static void rewardItems(Player player, List<GameItem> rewards) {
        BossPoints.addManualPoints(player, "theatre of blood");
//
        PetHandler.roll(player, PetHandler.Pets.TUMEKENS_GUARDIAN);
       // player.getEventCalendar().progress(EventChallenge.COMPLETE_TOB);
//        LeaderboardUtils.addCount(LeaderboardType.TOB, player, 1);
       // Achievements.increase(player, AchievementType.TOB, 1);
        if (Hespori.activeKronosSeed == true) {
            player.sendMessage("@red@The @gre@Kronos seed@red@ doubles your chances!" );
        }
        player.getItems().addItem(995, 500_000 + Misc.random(1_000_000));
        List<GameItem> rareItemList = items.get(LootRarity.RARE);
        for (GameItem reward : rewards) {
            if (rareItemList.stream().anyMatch(rareItem -> reward.getId() == rareItem.getId())) {
                if (!player.getDisplayName().equalsIgnoreCase("yourecooked") && !player.getDisplayName().equalsIgnoreCase("wilks")) {
                    PlayerHandler.executeGlobalMessage("@pur@" + player.getDisplayNameFormatted() + " received a drop: "
                            + ItemDef.forId(reward.getId()).getName() + " x " + reward.getAmount() + " from Tombs of Amascut.");
                }
                player.getCollectionLog().handleDrop(player, Npcs.ELIDINIS_WARDEN, rewards.get(0).getId(), 1);
            }
        }

        for (GameItem item : rewards) {
            player.getInventory().addAnywhere(new ImmutableItem(item.getId(), item.getAmount()));
        }

        player.getTobContainer().displayRewardInterface(rewards);
    }

    /**
     * To be removed but kept for now.
     */
    @Override
    public void roll(Player player) {}
}
