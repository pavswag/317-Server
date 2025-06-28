package io.xeros.content.fireofexchange;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.event.eventcalendar.EventChallenge;
import io.xeros.content.leaderboards.LeaderboardType;
import io.xeros.content.leaderboards.LeaderboardUtils;
import io.xeros.content.prestige.PrestigePerks;
import io.xeros.content.skills.Skill;
import io.xeros.content.upgrade.UpgradeMaterials;
import io.xeros.model.Items;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.model.items.GameItem;
import io.xeros.model.items.ItemAssistant;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;
import io.xeros.util.logging.player.FireOfExchangeLog;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static io.xeros.content.fireofexchange.FireOfExchangeBurnPrice.getBurnPrice;
import static io.xeros.content.upgrade.UpgradeMaterials.VALUES;

public class FireOfExchange {

    public static final int FOE_SHOP_ID = 171;

    public static int TOTAL_POINTS_EXCHANGED;

    public static boolean canBurnWithBranch(Player c) {
        int currentItem = c.currentExchangeItem;
        boolean isFoeRewardItem = getExchangeShopPrice(currentItem) != Integer.MAX_VALUE;

        if (isFoeRewardItem) {
            c.sendMessage("You cannot dissolve this item with your ancient branch.");
            return false;
        }
        return true;
    }


    public static void exchangeItemForPoints(Player c) {
        if (Configuration.DISABLE_FOE) {
            c.sendMessage("Nomad's Dissolver has been temporarily disabled.");
            return;
        }
        c.objectYOffset = 5;
        c.objectXOffset = 5;
        c.objectDistance = 5;
        c.getQuesting().exchangeItemForPoints(c);
        if (c.currentExchangeItem == -1) {
            c.sendMessage("@red@You cannot dissolve this item for Points.");
            return;
        }

        for (int crystal : FireOfExchangeBurnPrice.crystals) {
            if (c.currentExchangeItem == crystal) {
                c.sendMessage("@red@You cannot dissolve this item for Points.");
                return;
            }
        }

        ItemDef def = ItemDef.forId(c.currentExchangeItem);

        long exchangePrice = (def.isNoted() ? (long) getBurnPrice(c, def.getUnNotedIdIfNoted(), true) * c.currentExchangeItemAmount
                : (long) getBurnPrice(c, c.currentExchangeItem, true) * c.currentExchangeItemAmount);

        boolean noted = def.isNoted();

        if (exchangePrice == -1) {
            for (UpgradeMaterials value : VALUES) {
                if (value.getReward().getId() == c.currentExchangeItem) {
                    exchangePrice = (value.getCost() / 5);
                }
            }
        }
        if (exchangePrice == -1) {
            c.sendMessage("@red@You cannot dissolve @blu@" + ItemAssistant.getItemName(c.currentExchangeItem) + " for @red@ Points.");
            return;
        }

        if (!c.getItems().playerHasItem(c.currentExchangeItem)) {
            c.sendMessage("You no longer have this item on you.");
            return;
        }

        if (c.getMode().isIronmanType() && canBurnWithBranch(c)) {
            exchangePrice *= 1.10;
        }
        if (c.EliteCentBoost > 0) {
            exchangePrice *= 1.10;
        }

        if (c.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33092) &&
                Misc.random(0,100) > 97 &&
                c.currentExchangeItem != 691 &&
                c.currentExchangeItem != 692 &&
                c.currentExchangeItem != 693 &&
                c.currentExchangeItem != 696 &&
                c.currentExchangeItem != 2399 &&
                c.currentExchangeItem != 21046 &&
                c.currentExchangeItem != 8866 &&
                c.currentExchangeItem != 8868) {
            exchangePrice *= 2;
        }

        if (PrestigePerks.hasRelic(c, PrestigePerks.NOMAD_PLUS_15) &&
                c.currentExchangeItem != 691 &&
                c.currentExchangeItem != 692 &&
                c.currentExchangeItem != 693 &&
                c.currentExchangeItem != 696 &&
                c.currentExchangeItem != 2399 &&
                c.currentExchangeItem != 21046 &&
                c.currentExchangeItem != 8866 &&
                c.currentExchangeItem != 8868) {
            exchangePrice *= 1.15;
        }

        int itemAmount = c.currentExchangeItemAmount;
            c.getItems().deleteItem2(c.currentExchangeItem, itemAmount);
        c.foundryPoints += exchangePrice;
        TOTAL_POINTS_EXCHANGED += exchangePrice;

        if (c.playerXP[Skill.FORTUNE.getId()] < 0) {
           c.playerLevel[Skill.FORTUNE.getId()] = 99;
           c.playerXP[Skill.FORTUNE.getId()] = c.getPA().getXPForLevel(99) + 1;
           c.getPA().refreshSkill(Skill.FORTUNE.getId());
           c.getPA().setSkillLevel(Skill.FORTUNE.getId(), c.playerLevel[Skill.FORTUNE.getId()], c.playerXP[Skill.FORTUNE.getId()]);
           c.getPA().levelUp(Skill.FORTUNE.getId());
        }

        int multiplier = 15;
        switch (c.currentExchangeItem) {
            case 33237 -> multiplier = 1;
            case 691 -> multiplier = 2;
            case 692 -> multiplier = 3;
            case 693 -> multiplier = 4;
            case 696 -> multiplier = 50;
        }

        if (c.playerXP[Skill.FORTUNE.getId()] < 200_000_000) {
            c.getPA().addSkillXPMultiplied(multiplier * itemAmount, Skill.FORTUNE.getId(), true);
        }

        c.sendMessage("Nomad takes your @blu@" + ItemAssistant.getItemName(c.currentExchangeItem) + "@bla@ and gives back @blu@" + Misc.formatCoins(exchangePrice) + " Upgrade Points!");
        if (canBurnWithBranch(c)) {
            c.getEventCalendar().progress(EventChallenge.GAIN_X_EXCHANGE_POINTS, (int) exchangePrice);
            LeaderboardUtils.addCount(LeaderboardType.MOST_DISSOLVED, c, (int) exchangePrice);
        }
        if (c.currentExchangeItem != 691 &&
                c.currentExchangeItem != 692 &&
                c.currentExchangeItem != 693 &&
                c.currentExchangeItem != 696 &&
                c.currentExchangeItem != 2399 &&
                c.currentExchangeItem != 21046 &&
                c.currentExchangeItem != 8866 &&
                c.currentExchangeItem != 8868) {
            Achievements.increase(c, AchievementType.FOE_POINTS, (int) exchangePrice);
            c.totalEarnedExchangePoints += exchangePrice;
/*            if (exchangePrice > 20000) {
                PlayerHandler.executeGlobalMessage("@bla@[@red@FOUNDRY@bla@] @blu@"+c.getDisplayNameFormatted()+" melted a " + ItemAssistant.getItemName(c.currentExchangeItem) + " x" + c.currentExchangeItemAmount +
                        "@blu@ for @red@" + Misc.formatCoins(exchangePrice) + " points.");

            }*/
        }
        c.getRecentlyDissolvedItems().add(c.currentExchangeItem);
        c.getRecentlyDissolvedPrices().add(exchangePrice);
        Server.getLogging().write(new FireOfExchangeLog(c, new GameItem(c.currentExchangeItem, c.currentExchangeItemAmount)));
    }

    /**
     * Buying from shop price.
     */
    public static int getExchangeShopPrice(int id) {
        switch (id) {
            case 30010: //postie pete
                return 250_000;
            case 30011: //imp
                return 300_000;
            case 30012: //toucan
                return 300_000;
            case 30013: //penguin king
                return 350_000;
            case 30014: //k'klik
                return 15_000_000;
            case 30015: //melee pet
                return 750_000;
            case 30016: //range pet
                return 750_000;
            case 30017: //mage pet
                return 750_000;
            case 30018: //healer
                return 800_000;
            case 30019: //holy
                return 800_000;
            case 23939: //Seren
                return 10_000_000;
            case 30020: //corrupt beast
                return 50_000_000;
            case 30021: //roc
                return 50_000_000;
            case 8167: //nomad chest
                return 150_000_000;
            case 12783: //row i scroll
                return 1_000_000;
            case 21259: //name change scroll
                return 350_000;
            case 691: //10k cert
                return 11_000;
            case 692: //25k cert
                return 27_500;
            case 693: //50k cert
                return 55_000;
            case 696: //250k cert
                return 275_000;
            case 21046: //chest rate increase
                return 85_000;
            case 8866: //uim key
                return 25_000;
            case 8868: //perm uim key
                return 1_000_000;
            case 7629: //double slayer points
                return 125_000;
            case 24460: //double clues
                return 250_000;
            case 7968: //pet rate increase
                return 400_000;
            case Items.OVERLOAD_4: //pet rate increase
                return 20_000;
            case 33138:
                return 1_000;
            case 33139:
                return 1_000;
            case 33140:
                return 5_000;
            case 20718: //Burnt Pages
                return 5_000;
        }
        return Integer.MAX_VALUE;
    }
}
