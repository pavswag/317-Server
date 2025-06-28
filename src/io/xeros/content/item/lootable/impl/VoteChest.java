package io.xeros.content.item.lootable.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.event.eventcalendar.EventChallenge;
import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.Lootable;
import io.xeros.content.prestige.PrestigePerks;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

public class VoteChest implements Lootable {

    public static final int KEY = 22093; //vote key heree
    private static final int ANIMATION = 881;

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    static {
        items.put(LootRarity.COMMON, Arrays.asList(
                new GameItem(692, 5),    //10k nomad
                new GameItem(6679, 25),  //mini mystery box
                new GameItem(22374, 5),  //hespori key
                new GameItem(12873),  //guthns armour
                new GameItem(12875),  //veracs armour
                new GameItem(12877),  //dharoks armour
                new GameItem(12879),  //torags armour
                new GameItem(12881),  //ahrims armour
                new GameItem(12883)   //karils armour
        ));

        items.put(LootRarity.UNCOMMON, Arrays.asList(
                new GameItem(6677, 25),  //mini smb
                new GameItem(696, 2),    //250k nomad
                new GameItem(4185),   //porazdir key
                new GameItem(6792),   //serens key
                new GameItem(11666),  //elite void token
                new GameItem(12004),  //kraken tenticle
                new GameItem(9185),   //rune c'bow
                new GameItem(23933, 3)   //vote crystal
        ));

        items.put(LootRarity.RARE, Arrays.asList(
                new GameItem(6678, 10),  //mini umb
                new GameItem(11832),  //bandos chest
                new GameItem(11834),  //bandos tassets
                new GameItem(11836),  //bandos boots
                new GameItem(11826),  //armadyl helm
                new GameItem(11828),  //armadyl chest
                new GameItem(11830),  //armadyl chainskirt
                new GameItem(6889),   //mages book
                new GameItem(12904),  //toxic staff
                new GameItem(11905),  //trident
                new GameItem(26482),  //whip (or)
                new GameItem(26486),  //rune c'bow (or)
                new GameItem(696, 10)   //10m nomad
        ));
    }

    private static GameItem randomChestRewards(Player c, int chance) {
        int random = Misc.random(chance);
        int rareChance = 90;
        int uncommonChance = 50;
        if (c.getItems().playerHasItem(21046)) {
            rareChance = 89;
            c.getItems().deleteItem(21046, 1);
            c.sendMessage("@red@You sacrifice your @cya@tablet @red@for an increased drop rate." );
            c.getEventCalendar().progress(EventChallenge.USE_X_CHEST_RATE_INCREASE_TABLETS, 1);
        }
        List<GameItem> itemList = random < uncommonChance ? items.get(LootRarity.COMMON) : random >= uncommonChance && random <= rareChance ? items.get(LootRarity.UNCOMMON) : items.get(LootRarity.RARE);
        return Misc.getRandomItem(itemList);
    }

    private static void votePet(Player c) {
        int petchance = Misc.random(1500);
        if (petchance >= 1499) {
            c.getItems().addItem(21262, 1);
            c.getCollectionLog().handleDrop(c, 5, 21262, 1);
            PlayerHandler.executeGlobalMessage("@red@- "+ c.getDisplayName() +"@blu@ has just received the @red@Vote Genie Pet");
            c.sendMessage("@red@@cr10@You pet genie is waiting in your bank, waiting to serve you as his master.");
            c.gfx100(1028);
        }
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    @Override
    public void roll(Player c) {
        if (c.getItems().playerHasItem(KEY)) {
            c.getItems().deleteItem(KEY, 1);
            if (c.playTime > Misc.toCycles(5, TimeUnit.HOURS)) {
                Achievements.increase(c, AchievementType.VOTE_CHEST_UNLOCK, 1);
            }

            if (c.playTime < Misc.toCycles(5, TimeUnit.HOURS)) {
                c.sendMessage("You have not earned towards your vote achievement, this requires 5hours playtime!", TimeUnit.MINUTES.toMillis(10));
            }
            c.startAnimation(ANIMATION);
            GameItem reward = randomChestRewards(c, 100);

            String name = ItemDef.forId(reward.getId()).getName();
            c.getItems().addItem(reward.getId(), (PrestigePerks.hasRelic(c, PrestigePerks.DOUBLE_PC_POINTS) && Misc.isLucky(10) ? reward.getAmount() * 2 : reward.getAmount()));
//            PlayerHandler.executeGlobalMessage("@pur@["+ c.getDisplayName() +"]@blu@ has just opened the vote chest and received a " + name + "!");
            int random = 1 + Misc.random(5);
            c.votePoints+= random;
            c.sendMessage("You have received an extra "+random+" vote points from the chest.");
            votePet(c);
        } else {
            c.sendMessage("@blu@Use @red@::vpanel @blu@to see when you'll get your next key!");
        }
    }
}
