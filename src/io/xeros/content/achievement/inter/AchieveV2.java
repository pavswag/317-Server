package io.xeros.content.achievement.inter;

import io.xeros.Server;
import io.xeros.content.achievement.AchievementTier;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.bosses.nightmare.NightmareConstants;
import io.xeros.content.minigames.arbograve.ArbograveConstants;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

public class AchieveV2 {

    public static void Open(Player c) {
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
        Update(c, c.achievementPage);
        c.getPA().sendChangeSprite(54786, (byte) 1);
        c.getPA().showInterface(54760);
    }

    public static void reset(Player player) {
        int name = 54798;
        int progress = 54799;
        int rewards = 54800;

        for (int i = 0; i < 60; i++) {
            player.getPA().sendString(name, "");
            player.getPA().sendString(progress, "");
            for (int ii = 0; ii < 15; ii++) {
                player.getPA().itemOnInterface(-1,1,rewards, ii);
            }
            name+=6;
            progress+=6;
            rewards+=6;
        }
        player.getPA().resetScrollBar(54795);
    }

    public static void Update(Player player, int page) {
        reset(player);
        int name = 54798;
        int progress = 54799;
        int rewards = 54800;
        int complete = 54764;
        int totalA = 54763;
        int slot = 0;
        int total = 1;
        int amount = 0;
        int counter = 0;
        for (Achievements.Achievement value : Achievements.Achievement.values()) {
            if (value.getTier() == AchievementTier.STARTER && page == 0 || value.getTier() == AchievementTier.TIER_1 && page == 0) {
                int maxProgress = value.getAmount();
                int currentProgress = player.getAchievements().getAmountRemaining(value.getTier().getId(), value.getId());

                player.getPA().sendString(name, value.getDescription());
                if (player.getAchievements().isComplete(value.getTier().getId(), value.getId()) && player.getAchievements().isClaimed(value.getTier().getId(), value.getId())) {
                    player.getPA().sendString(progress, "Current Progress: @gre@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                } else if (player.getAchievements().isComplete(value.getTier().getId(), value.getId()) && !player.getAchievements().isClaimed(value.getTier().getId(), value.getId())) {
                    player.getPA().sendString(progress, "Current Progress: @cya@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                } else {
                    player.getPA().sendString(progress, "Current Progress: @red@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                }
                for (GameItem reward : value.getRewards()) {
                    player.getPA().itemOnInterface(reward.getId(),reward.getAmount(), rewards, slot++);
                }
                if (player.getAchievements().isComplete(value.getTier().getId(), value.getId())) {
                    amount++;
                }

                player.getPA().sendString(complete, "Achievements completed: @whi@" + amount + "/" + total);
                name+=6;
                progress+=6;
                rewards+=6;
                slot = 0;
                total++;
            } else if (value.getTier() == AchievementTier.TIER_2 && page == 1) {
                int maxProgress = value.getAmount();
                int currentProgress = player.getAchievements().getAmountRemaining(value.getTier().getId(), value.getId());

                player.getPA().sendString(name, value.getDescription());
                if (player.getAchievements().isComplete(value.getTier().getId(), value.getId()) && player.getAchievements().isClaimed(value.getTier().getId(), value.getId())) {
                    player.getPA().sendString(progress, "Current Progress: @gre@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                } else if (player.getAchievements().isComplete(value.getTier().getId(), value.getId()) && !player.getAchievements().isClaimed(value.getTier().getId(), value.getId())) {
                    player.getPA().sendString(progress, "Current Progress: @cya@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                } else {
                    player.getPA().sendString(progress, "Current Progress: @red@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                }
                for (GameItem reward : value.getRewards()) {
                    player.getPA().itemOnInterface(reward.getId(),reward.getAmount(), rewards, slot++);
                }
                if (player.getAchievements().isComplete(value.getTier().getId(), value.getId())) {
                    amount++;
                }

                player.getPA().sendString(complete, "Achievements completed: @whi@" + amount + "/" + total);
                name+=6;
                progress+=6;
                rewards+=6;
                slot = 0;
                total++;
            } else if (value.getTier() == AchievementTier.TIER_3 && page == 2) {
                int maxProgress = value.getAmount();
                int currentProgress = player.getAchievements().getAmountRemaining(value.getTier().getId(), value.getId());

                player.getPA().sendString(name, value.getDescription());
                if (player.getAchievements().isComplete(value.getTier().getId(), value.getId()) && player.getAchievements().isClaimed(value.getTier().getId(), value.getId())) {
                    player.getPA().sendString(progress, "Current Progress: @gre@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                } else if (player.getAchievements().isComplete(value.getTier().getId(), value.getId()) && !player.getAchievements().isClaimed(value.getTier().getId(), value.getId())) {
                    player.getPA().sendString(progress, "Current Progress: @cya@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                } else {
                    player.getPA().sendString(progress, "Current Progress: @red@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                }
                for (GameItem reward : value.getRewards()) {
                    player.getPA().itemOnInterface(reward.getId(),reward.getAmount(), rewards, slot++);
                }
                if (player.getAchievements().isComplete(value.getTier().getId(), value.getId())) {
                    amount++;
                }

                player.getPA().sendString(complete, "Achievements completed: @whi@" + amount + "/" + total);
                name+=6;
                progress+=6;
                rewards+=6;
                slot = 0;
                total++;
            } else if (value.getTier() == AchievementTier.TIER_4 && page == 3) {
                int maxProgress = value.getAmount();
                int currentProgress = player.getAchievements().getAmountRemaining(value.getTier().getId(), value.getId());

                player.getPA().sendString(name, value.getDescription());
                if (player.getAchievements().isComplete(value.getTier().getId(), value.getId()) && player.getAchievements().isClaimed(value.getTier().getId(), value.getId())) {
                    player.getPA().sendString(progress, "Current Progress: @gre@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                } else if (player.getAchievements().isComplete(value.getTier().getId(), value.getId()) && !player.getAchievements().isClaimed(value.getTier().getId(), value.getId())) {
                    player.getPA().sendString(progress, "Current Progress: @cya@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                } else {
                    player.getPA().sendString(progress, "Current Progress: @red@" + Misc.formatCoins(currentProgress) + "/" + Misc.formatCoins(maxProgress));
                }

                for (GameItem reward : value.getRewards()) {
                    player.getPA().itemOnInterface(reward.getId(),reward.getAmount(), rewards, slot++);
                }
                if (player.getAchievements().isComplete(value.getTier().getId(), value.getId())) {
                    amount++;
                }

                player.getPA().sendString(complete, "Achievements completed: @whi@" + amount + "/" + total);
                name+=6;
                progress+=6;
                rewards+=6;
                slot = 0;
                total++;
            }
            if (player.getAchievements().isComplete(value.getTier().getId(), value.getId())) {
                counter++;
            }

            player.getPA().sendString(totalA, "Achievement Diary @whi@(" + counter+ "/" + Achievements.Achievement.values().length + ")");
        }

        player.getPA().showInterface(54760);
    }

    public static boolean ButtonHandler(Player player, int buttonId) {
        if (buttonId == 54786) {
            Update(player, 0);
            player.achievementPage = 0;
            return true;
        }

        if (buttonId == 54787) {
            Update(player, 1);
            player.achievementPage = 1;
            return true;
        }

        if (buttonId == 54788) {
            Update(player, 2);
            player.achievementPage = 2;
            return true;
        }

        if (buttonId == 54789) {
            Update(player, 3);
            player.achievementPage = 3;
            return true;
        }

        return false;
    }

}
