package io.xeros.content.item.lootable.impl;

import com.google.common.collect.Lists;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.bosspoints.BossPoints;
import io.xeros.content.event.eventcalendar.EventChallenge;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.content.leaderboards.LeaderboardType;
import io.xeros.content.leaderboards.LeaderboardUtils;
import io.xeros.content.prestige.PrestigePerks;
import io.xeros.model.Items;
import io.xeros.model.Npcs;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.npc.drops.DropManager;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

import java.util.*;

public class TheatreOfBloodChest implements Lootable {

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    static {
        items.put(LootRarity.COMMON, Arrays.asList(
                new GameItem(995, Misc.random(500000,5000000)),
                new GameItem(Items.DEATH_RUNE, 600),
                new GameItem(Items.BLOOD_RUNE, 600),
                new GameItem(Items.COAL_NOTED, 600),
                new GameItem(Items.GOLD_ORE_NOTED, 360),
                new GameItem(Items.ADAMANTITE_ORE_NOTED, 150),
                new GameItem(Items.RUNITE_ORE_NOTED, 72),
                new GameItem(Items.GRIMY_CADANTINE_NOTED, 60),
                new GameItem(Items.GRIMY_AVANTOE_NOTED, 48),
                new GameItem(Items.GRIMY_TOADFLAX_NOTED, 44),
                new GameItem(Items.GRIMY_KWUARM_NOTED, 43),
                new GameItem(Items.GRIMY_IRIT_LEAF_NOTED, 40),
                new GameItem(Items.GRIMY_RANARR_WEED_NOTED, 36),
                new GameItem(Items.GRIMY_SNAPDRAGON_NOTED, 32),
                new GameItem(Items.GRIMY_LANTADYME_NOTED, 31),
                new GameItem(Items.GRIMY_DWARF_WEED_NOTED, 28),
                new GameItem(Items.GRIMY_TORSTOL_NOTED, 24),
                new GameItem(Items.BATTLESTAFF_NOTED, 18),
                new GameItem(Items.RUNE_BATTLEAXE_NOTED, 4),
                new GameItem(Items.RUNE_PLATEBODY_NOTED, 4),
                new GameItem(Items.RUNE_CHAINBODY_NOTED, 4)
        ));

        items.put(LootRarity.RARE, Arrays.asList(
                new GameItem(Items.SCYTHE_OF_VITUR, 1),

                new GameItem(Items.SANGUINESTI_STAFF, 1),

                new GameItem(Items.GHRAZI_RAPIER, 1),
                new GameItem(25975),
                new GameItem(22978),
                new GameItem(24780),

                new GameItem(2528),
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
                new GameItem(Items.MASORI_BODY),
                new GameItem(Items.MASORI_CHAPS),
                new GameItem(Items.MASORI_MASK),
                new GameItem(Items.ELIDINIS_WARD),
                new GameItem(Items.ELIDINIS_WARD),
                new GameItem(Items.ELIDINIS_WARD),
                new GameItem(Items.JUSTICIAR_FACEGUARD, 1),
                new GameItem(Items.JUSTICIAR_FACEGUARD, 1),
                new GameItem(Items.JUSTICIAR_FACEGUARD, 1),
                new GameItem(Items.JUSTICIAR_FACEGUARD, 1),

                new GameItem(Items.JUSTICIAR_CHESTGUARD, 1),
                new GameItem(Items.JUSTICIAR_CHESTGUARD, 1),
                new GameItem(Items.JUSTICIAR_CHESTGUARD, 1),
                new GameItem(Items.JUSTICIAR_CHESTGUARD, 1),

                new GameItem(Items.JUSTICIAR_LEGGUARDS, 1),
                new GameItem(Items.JUSTICIAR_LEGGUARDS, 1),
                new GameItem(Items.JUSTICIAR_LEGGUARDS, 1),
                new GameItem(Items.JUSTICIAR_LEGGUARDS, 1),

                new GameItem(Items.AVERNIC_DEFENDER_HILT, 1),
                new GameItem(Items.AVERNIC_DEFENDER_HILT, 1),
                new GameItem(Items.AVERNIC_DEFENDER_HILT, 1),
                new GameItem(Items.AVERNIC_DEFENDER_HILT, 1),
                new GameItem(Items.AVERNIC_DEFENDER_HILT, 1),
                new GameItem(Items.AVERNIC_DEFENDER_HILT, 1),
                new GameItem(Items.AVERNIC_DEFENDER_HILT, 1),
                new GameItem(Items.AVERNIC_DEFENDER_HILT, 1),

                new GameItem(25904, 1),  //Vamp slayer helm
                new GameItem(25904, 1),
                new GameItem(25904, 1),
                new GameItem(25904, 1),
                new GameItem(25904, 1),
                new GameItem(25904, 1),
                new GameItem(25904, 1),
                new GameItem(25904, 1)
        ));
    }

    public static ArrayList<GameItem> getRareDrops() {
        ArrayList<GameItem> drops = new ArrayList<>();
        List<GameItem> found = items.get(LootRarity.RARE);
        for(GameItem f : found) {
            boolean foundItem = false;
            for(GameItem drop : drops)
                if (drop.getId() == f.getId()) {
                    foundItem = true;
                    break;
                }
            if (!foundItem) drops.add(f);
        }
        return drops;
    }

    public static ArrayList<GameItem> getAllDrops() {
        ArrayList<GameItem> drops = new ArrayList<>();
        items.forEach((lootRarity, gameItems) -> {
            gameItems.forEach(g -> {
                if (!drops.contains(g)) drops.add(g);
            });
        });
        return drops;
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    public static List<GameItem> getRandomItems(Player player, int size) {
        List<GameItem> rewards = Lists.newArrayList();
        int rareChance = 100;
        if (Hespori.activeKronosSeed) rareChance = 60;
        else if (player.amDonated >= 100 && player.amDonated < 250) rareChance = 90;
        else if (player.amDonated >= 250 && player.amDonated < 500) rareChance = 85;
        else if (player.amDonated >= 500 && player.amDonated < 750) rareChance = 80;
        else if (player.amDonated >= 750 && player.amDonated < 1000) rareChance = 75;
        else if (player.amDonated >= 1000 && player.amDonated < 1500) rareChance = 70;
        else if (player.amDonated >= 1500 && player.amDonated < 2000) rareChance = 65;
        else if (player.amDonated >= 2000 && player.amDonated < 3000) rareChance = 60;
        else if (player.amDonated >= 3000) rareChance = 45;
        PetHandler.roll(player, PetHandler.Pets.TUMEKENS_GUARDIAN);
        if (player.getMode().isOsrs()) rareChance -= 5;
        if (player.getMode().is5x()) rareChance -= 3;

        if (player.hasFollower && (player.petSummonId == 25350 || player.petSummonId == 30122)) rareChance -= 20;

//        if (Discord.jda != null) {
//            Guild guild = Discord.getGuild();
//
//            if (guild != null) for (Member booster : guild.getBoosters())
//                if (player.getDiscordUser() == booster.getUser().getIdLong()) {
//                    rareChance -= 10;
//                    break;
//                }
//        }

        rareChance = (int) (rareChance - (DropManager.getDropRateModifier(player) < 10 ? DropManager.getDropRateModifier(player) : DropManager.getDropRateModifier(player) / 10));
        if (rareChance <= 0) rareChance = 30;
        int chance = Misc.random(1, rareChance);
        if (chance <= 10) rewards.add(Misc.getRandomItem(items.get(LootRarity.RARE)));
        else for (int count = 0; count < 3; count++) rewards.add(Misc.getRandomItem(items.get(LootRarity.COMMON)));
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

        PetHandler.roll(player, PetHandler.Pets.LIL_ZIK);
        player.getEventCalendar().progress(EventChallenge.COMPLETE_TOB);
        LeaderboardUtils.addCount(LeaderboardType.TOB, player, 1);
        Achievements.increase(player, AchievementType.TOB, 1);
        if (Hespori.activeKronosSeed) player.sendMessage("@red@The @gre@Kronos seed@red@ doubled your chances!");
        player.getItems().addItem(995, 500_000 + Misc.random(1_000_000));
        player.foundryPoints += Misc.random(250_000);
        player.sendMessage("You now have:"+player.foundryPoints+"upgrading points.");
        List<GameItem> rareItemList = items.get(LootRarity.RARE);
        for (GameItem reward : rewards)
            if (rareItemList.stream().anyMatch(rareItem -> reward.getId() == rareItem.getId())) {
                PlayerHandler.executeGlobalMessage("@pur@" + player.getDisplayNameFormatted() + " received a drop: " + ItemDef.forId(reward.getId()).getName() + " x " + reward.getAmount() + " from Theatre of Blood.");
                player.getCollectionLog().handleDrop(player, Npcs.THE_MAIDEN_OF_SUGADINTI, rewards.get(0).getId(), 1);
            }

        for (GameItem item : rewards) {
            int finalAmount = (PrestigePerks.hasRelic(player, PrestigePerks.DOUBLE_PC_POINTS) && (Misc.random(0, 100) == 2) ?
                    item.getAmount()* 2 : item.getAmount());
            if (player.daily2xRaidLoot > 0) finalAmount *= 2;
            player.getItems().addItemUnderAnyCircumstance(item.getId(), finalAmount);
//            player.getInventory().addAnywhere(new ImmutableItem(item.getId(), item.getAmount()));
        }

        player.getTobContainer().displayRewardInterface(rewards);
    }

    /**
     * To be removed but kept for now.
     */
    @Override
    public void roll(Player player) {}
}
