package io.xeros.content.battlepass;

import io.xeros.Server;
import io.xeros.content.bosses.nightmare.NightmareConstants;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.minigames.arbograve.ArbograveConstants;
import io.xeros.content.seasons.Halloween;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.discord.Discord;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;


@Getter
public class Pass {

    @Getter
    public static int SEASON = 0;

    @Getter
    @Setter
    public static boolean seasonEnded = true;

    @Getter
    @Setter
    public static Long daysUntilStart = 0L;
    @Getter
    @Setter
    public static Long daysUntilEnd = 0L;

    public static void tick() {
        if (System.currentTimeMillis() > daysUntilEnd && !seasonEnded) {
            //Start new season 30 days later
            seasonEnded = true;
            daysUntilStart = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(1);
        }
        if (System.currentTimeMillis() > daysUntilStart && seasonEnded) {
            Rewards.generateRewards();
            daysUntilEnd = System.currentTimeMillis() + TimeUnit.HOURS.toMillis(720);
            seasonEnded = false;
            SEASON++;
            Rewards.saveDivision();
            for (Player player : PlayerHandler.getPlayers()) {
                if (player != null) {
                    handleLogin(player);
                }
            }
        }
    }

    public static void openInterface(Player player) {
        if (seasonEnded) {
            player.start(new DialogueBuilder(player).statement("The season is over, check back in " + TimeUnit.MILLISECONDS.toDays(daysUntilStart-System.currentTimeMillis()) + " days!"));
            return;
        }

        int xpNeeded = 30 + ((player.getTier() / 2) * 2);

        player.getPA().sendString(59968, "XP: " + player.getXp() + "/" + xpNeeded);
        player.getPA().sendString(59969, "" + (player.getTier() > 100 ? "Complete" : player.getTier()-1));
        player.getPA().sendString(59970, "Division " + SEASON);
        player.getPA().sendString(59971, "Division Ends: " + (TimeUnit.MILLISECONDS.toDays(daysUntilEnd-System.currentTimeMillis())) + " days");


        double number = (double) player.getXp() / (double) xpNeeded * 100;
        if (number >= 50)
            number = 50;

        player.getPA().sendProgressBar(59973, (int) number);

        int index = 0;
        int interfaceId = 59995;
        for (int i = 0; i < 50; i++) {
            if (Rewards.defaultRewards.size() > index) {
                player.getPA().itemOnInterface(Rewards.defaultRewards.get(index).getId(), Rewards.defaultRewards.get(index).getAmount(),
                        interfaceId++,0);
            } else {
                player.getPA().itemOnInterface(-1, 1,interfaceId++,0);
            }

            if (Rewards.memberRewards.size() > index) {
                player.getPA().itemOnInterface(Rewards.memberRewards.get(index).getId(), Rewards.memberRewards.get(index).getAmount(),
                        interfaceId++,0);
            } else {
                player.getPA().itemOnInterface(-1, 1,interfaceId++,0);
            }


            player.getPA().sendConfig(1614 + index, player.tier > i + 1 ? 1 : 0);
            if (player.isMember())
                player.getPA().sendConfig(1814 + index, player.tier > i + 1 ? 1 : 0);
            else
                player.getPA().sendConfig(1814 + index, 0);

            interfaceId += 6;
            index++;
        }

        player.getPA().showInterface(59961);

    }

    public static void addExperience(Player c, int exp) {
        if (c.getPosition().inWild()
                || Server.getMultiplayerSessionListener().inAnySession(c)
                || Boundary.isIn(c, Boundary.DUEL_ARENA)
                || Boundary.isIn(c, Boundary.FIGHT_CAVE)
                || c.getPosition().inClanWarsSafe()
                || Boundary.isIn(c, Boundary.INFERNO)
                || c.getInstance() != null
                || Boundary.isIn(c, NightmareConstants.BOUNDARY)
                || Boundary.isIn(c, Boundary.OUTLAST_AREA)
                || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_AREA)
                || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST_LOBBY)
                || Boundary.isIn(c, Boundary.FOREST_OUTLAST)
                || Boundary.isIn(c, Boundary.SNOW_OUTLAST)
                || Boundary.isIn(c, Boundary.BOUNTY_HUNTER_OUTLAST)
                || Boundary.isIn(c, Boundary.ROCK_OUTLAST)
                || Boundary.isIn(c, Boundary.FALLY_OUTLAST)
                || Boundary.isIn(c, Boundary.LUMBRIDGE_OUTLAST)
                || Boundary.isIn(c, new Boundary(3117, 3640, 3137, 3644))
                || Boundary.isIn(c, new Boundary(3114, 3611, 3122, 3639))
                || Boundary.isIn(c, new Boundary(3122, 3633, 3124, 3639))
                || Boundary.isIn(c, new Boundary(3122, 3605, 3149, 3617))
                || Boundary.isIn(c, new Boundary(3122, 3617, 3125, 3621))
                || Boundary.isIn(c, new Boundary(3144, 3618, 3156, 3626))
                || Boundary.isIn(c, new Boundary(3155, 3633, 3165, 3646))
                || Boundary.isIn(c, new Boundary(3157, 3626, 3165, 3632))
                || Boundary.isIn(c, Boundary.SWAMP_OUTLAST)
                || Boundary.isIn(c, Boundary.WG_Boundary)
                || Boundary.isIn(c, Boundary.PEST_CONTROL_AREA)
                || Boundary.isIn(c, Boundary.RAIDS)
                || Boundary.isIn(c, Boundary.OLM)
                || Boundary.isIn(c, Boundary.RAID_MAIN)
                || Boundary.isIn(c, Boundary.XERIC)
                || Boundary.isIn(c, ArbograveConstants.ALL_BOUNDARIES)) {
            return;
        }
        if (seasonEnded) {
            return;
        }
        if (c.tier > 50)
            return;

        if (Halloween.DoubleDivision) {
            exp *= 2;
        }

        c.xp += exp;

        int xpNeeded = 30 + ((c.tier / 2) * 2);

        while (c.xp >= xpNeeded) {
            c.xp -= xpNeeded;
            levelUp(c);
        }
    }

    public static void levelUp(Player player) {
        if (seasonEnded) {
            return;
        }
        if (player.tier <= 0)
            player.tier = 1;

        if (player.tier < 51) {
            grantRewards(player);
            player.tier += 1;

            if (player.tier == 101) {
                player.sendMessage("<col=660000>[Season pass]<col=100666> You have completed the Season pass!");
                if (player.getIpAddress() != null && player.getMacAddress() != null && player.getUUID() != null)
                    Discord.writeServerSyncMessage("[Division]: " + player.getDisplayName() + ", P2W: " + player.member + ", Gamemode: " + player.getMode().getType().name().toLowerCase());
                return;
            }

        }

        if (player.tier == 51) {
            return;
        }

        player.sendMessage("<col=660000>[Division]<col=100666> You are now Tier " + (player.tier - 1));


    }

    public static void grantRewards(Player player) {
        player.getItems().addItemUnderAnyCircumstance(Rewards.defaultRewards.get(player.tier - 1).getId(), Rewards.defaultRewards.get(player.tier - 1).getAmount());
        if (player.isMember())
            player.getItems().addItemUnderAnyCircumstance(Rewards.memberRewards.get(player.tier - 1).getId(), Rewards.memberRewards.get(player.tier - 1).getAmount());
        player.sendMessage("Your rewards have been sent to your bank");
    }


    public static void grantMembership(Player player) {
        if (player.isMember()) {
            player.sendMessage("<col=660000>[Division]<col=100666> You are already a Division pass gold member.");
            return;
        }

        player.sendMessage("<col=660000>[Division]<col=100666> You are now a Division pass gold member.");
        player.setMember(true);

        if (player.tier > 1) {
            for (int i = 0; i < player.getTier() - 1; i++) {
                player.getItems().addItemUnderAnyCircumstance(Rewards.memberRewards.get(i).getId(), Rewards.memberRewards.get(i).getAmount());
            }

            player.sendMessage("Your gold pass rewards have been sent to your bank");
        }
    }

    public static void handleLogin(Player player) {
        player.getSeasonPassPlaytime().reset();
        if (SEASON != player.currentSeason) {
            player.sendMessage("<col=660000>[Division]<col=100666> A new Division has begun!");
            player.tier = 0;
            player.xp = 0;
            player.member = false;
            player.currentSeason = SEASON;
        }
        if (player.tier <= 0)
            player.tier = 1;
    }


}
