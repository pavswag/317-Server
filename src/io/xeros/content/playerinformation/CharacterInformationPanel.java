package io.xeros.content.playerinformation;

import io.xeros.content.skills.Skill;
import io.xeros.model.entity.npc.drops.DropManager;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.util.concurrent.TimeUnit;

public class CharacterInformationPanel {

    public static void Open(Player player) {
        Update(player);
        player.getPA().showInterface(55160);
    }


    public static String formatString(double decimal) {
        double percentage = decimal * 100;
        return String.format("%.2f%%", percentage);
    }

    public static void Update(Player player) {
        int start = 55164;
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.ATTACK));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.STRENGTH));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.DEFENCE));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.RANGED));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.PRAYER));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.MAGIC));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.RUNECRAFTING));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.HUNTER));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.HITPOINTS));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.AGILITY));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.HERBLORE));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.THIEVING));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.CRAFTING));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.FLETCHING));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.SLAYER));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.DEMON_HUNTER));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.MINING));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.SMITHING));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.FISHING));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.COOKING));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.FIREMAKING));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.WOODCUTTING));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.FARMING));
        player.getPA().sendString(start++, "@whi@" + player.getLevel(Skill.FORTUNE));
        player.getPA().sendString(start++, "Total Level: " + player.totalLevel);
        player.getPA().sendString(start++, "Donator: " + player.getDisplayNameFormatted());
        player.getPA().sendString(start++, "D. Credits: " + player.donatorPoints);
        player.getPA().sendString(start++, "Store: " + player.getStoreDonated() + "/ Ingame: " + player.amDonated);

        int simp = 55202;
        player.getPA().sendString(simp++, "@cr1@@or1@ Player Information");
        simp++;

        long miliseconds = (long) player.playTime * 600;
        long days = TimeUnit.MILLISECONDS.toDays(miliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(miliseconds - TimeUnit.DAYS.toDays(days));
        String time = days + " days, " + hours + " hrs";
        player.getPA().sendString(simp++, "@or1@- TotalTime: @gre@" + time);
        simp++;
        double dr = DropManager.getDropRateModifier(player);

        player.getPA().sendString(simp++, "@or1@- DR: @gre@" + formatString(dr));

        simp++;
        player.getPA().sendString(simp++, "@or1@- XP: @gre@" + player.getExpMode().getType().getFormattedName());
        simp++;
        player.getPA().sendString(simp++, "@or1@- Mode: @gre@" + player.getMode().getType().getFormattedName());
        simp++;
        if (player.killcount > 0) {
            if (player.deathcount > 0) {
                player.getPA().sendString(simp++, "@or1@- KDR: @gre@" + (player.killcount / player.deathcount) + " / (" + player.killcount + "/" + player.deathcount + ")");
            } else {
                player.getPA().sendString(simp++, "@or1@- KDR: @gre@" + (player.killcount) + " / (" + player.killcount + ")");
            }
            simp++;
        }
        player.getPA().sendString(simp++, "@or1@- Vote Points: @gre@" + player.votePoints);
        simp++;
        player.getPA().sendString(simp++, "@or1@- PK Points: @gre@" + player.pkp);
        simp++;
        player.getPA().sendString(simp++, "@or1@- AFK Points: @gre@" + Misc.formatCoins(player.getAfkPoints()));
        simp++;
        player.getPA().sendString(simp++, "@or1@- Arbo Points: @gre@" + Misc.formatCoins(player.arboPoints));
        simp++;
        player.getPA().sendString(simp++, "@or1@- Boss Points: @gre@" + player.bossPoints);
        simp++;
        player.getPA().sendString(simp++, "@or1@- Slayer Points: @gre@" + player.getSlayer().getPoints());
        simp++;
        player.getPA().sendString(simp++, "@or1@- PC Points: @gre@" + player.pcPoints);
        simp++;
        player.getPA().sendString(simp++, "@or1@- Upgrade Points: @gre@" + Misc.formatCoins(player.foundryPoints));
        simp++;
        player.getPA().sendString(simp++, "@or1@- Bloody Points: @gre@" + player.getBloody_points());
        simp++;
        player.getPA().sendString(simp++, "@or1@- Seasonal Points: @gre@" + player.getSeasonalPoints());
        simp++;
        player.getPA().sendString(simp++, "@or1@- Discord Points: @gre@" + Misc.formatCoins(player.getDiscordPoints()));
        simp++;
        player.getPA().sendString(simp++, "@or1@- AOE Points: @gre@" + Misc.formatCoins(player.instanceCurrency));
        simp++;
        player.getPA().sendString(simp++, "@or1@- Fortune Spins: @gre@" + Misc.formatCoins(player.getFortuneSpins()));
    }
}
