package io.xeros.content.achievement;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.xeros.Server;
import io.xeros.content.achievement.Achievements.Achievement;
import io.xeros.content.achievement.inter.AchieveV2;
import io.xeros.content.achievement.inter.TasksInterface;
import io.xeros.content.battlepass.Pass;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.ModeType;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;
import io.xeros.util.logging.player.ClaimAchievementLog;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Jason MacKeigan (http://www.rune-server.org/members/Jason)
 */
public class AchievementHandler {

    public static final String COLOR = "074091";

    private static final int START_BUTTON_ID = 54801;
    private static final int BUTTON_SEPARATION = 6;

    private static final int MAXIMUM_TIER_ACHIEVEMENTS = 100;
    private static final int MAXIMUM_TIERS = AchievementTier.values().length;

    private final int[][] amountRemaining = new int[MAXIMUM_TIERS][MAXIMUM_TIER_ACHIEVEMENTS];
    private final boolean[][] completed = new boolean[MAXIMUM_TIERS][MAXIMUM_TIER_ACHIEVEMENTS];
    private final boolean[][] claimed = new boolean[MAXIMUM_TIERS][MAXIMUM_TIER_ACHIEVEMENTS];

    private final Player player;
    public int points;
    private boolean firstAchievementLoginJune2021;

    public AchievementHandler(Player player) {
        this.player = player;
    }

    public void onLogin() {
/*        fixKc(AchievementType.COX, player.totalRaidsFinished);
        fixKc(AchievementType.TOB, player.tobCompletions);
        fixKc(AchievementType.GROTESQUES, player.getNpcDeathTracker().getKc("grotesque guardians"));
        fixKc(AchievementType.NIGHTMARE, player.getNpcDeathTracker().getKc("the nightmare"));
        fixKc(AchievementType.HYDRA, player.getNpcDeathTracker().getKc("alchemical hydra"));
        fixKc(AchievementType.HUNLLEF, player.getNpcDeathTracker().getKc("crystalline hunllef"));
        fixKc(AchievementType.MIMIC, player.getNpcDeathTracker().getKc("the mimic"));
        fixKc(AchievementType.SLAY_CERB, player.getNpcDeathTracker().getKc("cerberus"));*/

        if (!player.hasAchieveFix) {
            fixKc(AchievementType.ARBO, player.arboCompletions);
            fixKc(AchievementType.COX, player.totalRaidsFinished);
            fixKc(AchievementType.TOB, player.tobCompletions);
            fixKc(AchievementType.SLAY_KRAKEN, player.getNpcDeathTracker().getKc("kraken"));
            fixKc(AchievementType.SLAY_SIRE, player.getNpcDeathTracker().getKc("abyssal sire"));
            fixKc(AchievementType.SLAY_KBD, player.getNpcDeathTracker().getKc("king black dragon"));
            fixKc(AchievementType.SLAY_CORP, player.getNpcDeathTracker().getKc("corporeal beast"));
            fixKc(AchievementType.SLAY_CERB, player.getNpcDeathTracker().getKc("cerberus"));
            fixKc(AchievementType.HYDRA, player.getNpcDeathTracker().getKc("alchemical hydra"));
            fixKc(AchievementType.SLAY_VORKATH, player.getNpcDeathTracker().getKc("vorkath"));
            fixKc(AchievementType.NIGHTMARE, player.getNpcDeathTracker().getKc("the nightmare"));
            fixKc(AchievementType.SLAY_NEX, player.getNpcDeathTracker().getKc("nex"));

            // Ultimate ironman achievements to autocomplete (but no rewards)
            if (player.getMode().getType() == ModeType.ULTIMATE_IRON_MAN) {
                AchievementType[] autocomplete = { AchievementType.PRESETS, AchievementType.TOURNAMENT };
                for (Achievement achievement : Achievement.values()) {
                    if (Arrays.stream(autocomplete).anyMatch(it -> achievement.getType() == it)) {
                        setComplete(achievement, true);
                        setClaimed(achievement, true);
                        setAmountRemaining(achievement, achievement.getAmount());
                    }
                }
            }

            // This is a fix for someone having a complete achievement marked as incomplete.
            for (Achievement achievement : Achievement.values()) {
                if (!isComplete(achievement)) {
                    int remaining = getAmountRemaining(achievement);
                    int total = achievement.getAmount();
                    if (remaining == total) {
                        setComplete(achievement, true);
                    }
                }
            }
            player.hasAchieveFix = true;
        }

    }

    public void print(BufferedWriter writer, int tier) {
        try {
            for (Achievement achievement : Achievement.ACHIEVEMENTS) {
                if (achievement.getTier().getId() == tier) {
                    if (amountRemaining[tier][achievement.getId()] > 0) {
                        writer.write(achievement.name().toLowerCase() + " = "
                                + amountRemaining[tier][achievement.getId()]
                                + "\t" + completed[tier][achievement.getId()]
                                + "\t" + claimed[tier][achievement.getId()]
                        );
                        writer.newLine();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * If achievement is less than kc, tick achievement by kc.
     */
    private void fixKc(AchievementType type, int kc) {
        for (Achievement achievement : Achievement.values()) {
            if (achievement.getType() == type) {
                int amount = kc - getAmountRemaining(achievement);
                if (amount > 0) {
                    Achievements.increase(player, achievement.getType(), amount);
                }
            }
        }
    }

    public void readFromSave(String name, String[] data, AchievementTier tier) {
        int amount = Integer.parseInt(data[0]);
        boolean complete = Boolean.parseBoolean(data[1]);
        boolean claimed = data.length >= 3 ? Boolean.parseBoolean(data[2]) : complete; // Set to complete because it was auto claimed
        read(name, tier.getId(), amount, complete, claimed);
    }

    private void read(String name, int tier, int amount, boolean complete, boolean claimed) {
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (achievement.getTier().getId() == tier) {
                if (achievement.name().toLowerCase().equals(name)) {
                    this.setComplete(tier, achievement.getId(), complete);
                    this.setAmountRemaining(tier, achievement.getId(), amount);
                    this.setClaimed(tier, achievement.getId(), claimed);
                    break;
                }
            }
        }
    }

    public void kill(NPC npc) {
        String name = npc.getNpcStats().getName();
        if (name == null || name.length() <= 0) {
            return;
        } else {
            name = name.toLowerCase().replaceAll("_", " ");
        }
        Achievements.increase(player, AchievementType.SLAY_ANY_NPCS, 1);
        if ((name.contains("dragon") || name.contains("vorkath")) && !name.contains("baby"))
            Achievements.increase(player, AchievementType.SLAY_DRAGONS, 1);
        List<String> checked = new ArrayList<>();
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (!achievement.getType().name().toLowerCase().contains("kill"))
                continue;
            if (achievement.getType().name().toLowerCase().replaceAll("_", " ").replaceAll("kill ", "").equalsIgnoreCase(name)) {
                if (checked.contains(achievement.getType().name().toLowerCase().replaceAll("_", " ").replaceAll("kill ", "")))
                    continue;
                Achievements.increase(player, achievement.getType(), 1);
                checked.add(achievement.getType().name().toLowerCase().replaceAll("_", " ").replaceAll("kill ", ""));
            }
        }
    }

    public boolean hasCompletedAll() {
        int amount = 0;
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (isComplete(achievement.getTier().getId(), achievement.getId()))
                amount++;
        }
        return amount == Achievements.getMaximumAchievements();
    }

    public boolean hasCompletedHalf() {
        int amount = 0;
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            if (isComplete(achievement.getTier().getId(), achievement.getId()))
                amount++;
        }
        return amount >= (Achievements.getMaximumAchievements()/2);
    }

    public void resetAll() {
        for (Achievement achievement : Achievement.ACHIEVEMENTS) {
            setClaimed(achievement.getTier().getId(), achievement.getId(), false);
            setComplete(achievement.getTier().getId(), achievement.getId(), false);
            setAmountRemaining(achievement, 0);
        }
    }

    public int[] BUTTON_INDEX = {54801,54807,54813,54819,54825,54831,54837,54843,54849,54855,
            54861,54867,54873,54879,54885,54891,54897,54903,54909,54915,
            54921,54927,54933,54939,54945,54951,54957,54963,54969,54975,
            54981,54987,54993,54999,55005,55011,55017,55023,55029,55035,
            55041,55047,55053,55059,55065,55071,55077,55083,55089,55095,
            55101,55107,55113,55119,55125,55131,55137,55143,55149,55155};

    public boolean clickButton(int buttonId) {
        if (!ArrayUtils.contains(BUTTON_INDEX, buttonId)) {
            return false;
        }
        if (player.achievementPage == 0) {
            int index = ArrayUtils.indexOf(BUTTON_INDEX, buttonId);
            List<Achievement> achievements = Arrays.stream(Achievement.values()).filter(a -> a.getTier() == AchievementTier.TIER_1 || a.getTier() == AchievementTier.STARTER).collect(Collectors.toList());
            if (isClaimed(achievements.get(index).getTier().getId(), achievements.get(index).getId())) {
                player.sendMessage(Misc.colorWrap(COLOR, "You've already claimed this achievement!"));
                return true;
            }

            if (!isComplete(achievements.get(index).getTier().getId(), achievements.get(index).getId())) {
                int amountRequired = achievements.get(index).getAmount();
                if (getAmountRemaining(achievements.get(index)) >= amountRequired) {
                    setComplete(achievements.get(index), true);
                } else {
                    player.sendMessage(Misc.colorWrap(COLOR, "You haven't completed this achievement yet!"));
                    return true;
                }
            }

            Pass.addExperience(player,1);
            Achievements.addReward(player, achievements.get(index));
            setClaimed(achievements.get(index).getTier().getId(), achievements.get(index).getId(), true);
            AchieveV2.Update(player, player.achievementPage);
            player.sendMessage("<col=" + COLOR + ">Claimed the " + achievements.get(index).getTier().getName().toLowerCase()
                    + " achievement '" + achievements.get(index).getFormattedName() + "'.</col>");
            Discord.jda.getTextChannelById(1227065946469044244L).sendMessage(player.getDisplayName() + " has just completed " + achievements.get(index).getFormattedName()).queue();
            Server.getLogging().write(new ClaimAchievementLog(player, achievements.get(index)));
            return true;
        } else if (player.achievementPage == 1) {
            int index = ArrayUtils.indexOf(BUTTON_INDEX, buttonId);
            List<Achievement> achievements = Arrays.stream(Achievement.values()).filter(a -> a.getTier() == AchievementTier.TIER_2).collect(Collectors.toList());
            if (isClaimed(achievements.get(index).getTier().getId(), achievements.get(index).getId())) {
                player.sendMessage(Misc.colorWrap(COLOR, "You've already claimed this achievement!"));
                return true;
            }
            if (!isComplete(achievements.get(index).getTier().getId(), achievements.get(index).getId())) {
                int amountRequired = achievements.get(index).getAmount();

                if (getAmountRemaining(achievements.get(index)) >= amountRequired) {
                    setComplete(achievements.get(index), true);
                } else {
                    player.sendMessage(Misc.colorWrap(COLOR, "You haven't completed this achievement yet!"));
                    return true;
                }
            }

            Pass.addExperience(player,2);
            Achievements.addReward(player, achievements.get(index));
            setClaimed(achievements.get(index).getTier().getId(), achievements.get(index).getId(), true);
            AchieveV2.Update(player, player.achievementPage);
            player.sendMessage("<col=" + COLOR + ">Claimed the " + achievements.get(index).getTier().getName().toLowerCase()
                    + " achievement '" + achievements.get(index).getFormattedName() + "'.</col>");
            Discord.jda.getTextChannelById(1227065946469044244L).sendMessage(player.getDisplayName() + " has just completed " + achievements.get(index).getFormattedName()).queue();
            Server.getLogging().write(new ClaimAchievementLog(player, achievements.get(index)));
            return true;
        } else if (player.achievementPage == 2) {
            int index = ArrayUtils.indexOf(BUTTON_INDEX, buttonId);
            List<Achievement> achievements = Arrays.stream(Achievement.values()).filter(a -> a.getTier() == AchievementTier.TIER_3).collect(Collectors.toList());
            if (isClaimed(achievements.get(index).getTier().getId(), achievements.get(index).getId())) {
                player.sendMessage(Misc.colorWrap(COLOR, "You've already claimed this achievement!"));
                return true;
            }
            if (!isComplete(achievements.get(index).getTier().getId(), achievements.get(index).getId())) {
                int amountRequired = achievements.get(index).getAmount();

                if (getAmountRemaining(achievements.get(index)) >= amountRequired) {
                    setComplete(achievements.get(index), true);
                } else {
                    player.sendMessage(Misc.colorWrap(COLOR, "You haven't completed this achievement yet!"));
                    return true;
                }
            }

            Pass.addExperience(player,3);
            Achievements.addReward(player, achievements.get(index));
            setClaimed(achievements.get(index).getTier().getId(), achievements.get(index).getId(), true);
            AchieveV2.Update(player, player.achievementPage);
            player.sendMessage("<col=" + COLOR + ">Claimed the " + achievements.get(index).getTier().getName().toLowerCase()
                    + " achievement '" + achievements.get(index).getFormattedName() + "'.</col>");
            Discord.jda.getTextChannelById(1227065946469044244L).sendMessage(player.getDisplayName() + " has just completed " + achievements.get(index).getFormattedName()).queue();
            Server.getLogging().write(new ClaimAchievementLog(player, achievements.get(index)));
            return true;
        } else if (player.achievementPage == 3) {
            int index = ArrayUtils.indexOf(BUTTON_INDEX, buttonId);
            List<Achievement> achievements = Arrays.stream(Achievement.values()).filter(a -> a.getTier() == AchievementTier.TIER_4).collect(Collectors.toList());
            if (isClaimed(achievements.get(index).getTier().getId(), achievements.get(index).getId())) {
                player.sendMessage(Misc.colorWrap(COLOR, "You've already claimed this achievement!"));
                return true;
            }
            if (!isComplete(achievements.get(index).getTier().getId(), achievements.get(index).getId())) {
                int amountRequired = achievements.get(index).getAmount();
                if (getAmountRemaining(achievements.get(index)) >= amountRequired) {
                    setComplete(achievements.get(index), true);
                } else {
                    player.sendMessage(Misc.colorWrap(COLOR, "You haven't completed this achievement yet!"));
                    return true;
                }
            }

            Pass.addExperience(player,4);
            Achievements.addReward(player, achievements.get(index));
            setClaimed(achievements.get(index).getTier().getId(), achievements.get(index).getId(), true);
            AchieveV2.Update(player, player.achievementPage);
            player.sendMessage("<col=" + COLOR + ">Claimed the " + achievements.get(index).getTier().getName().toLowerCase()
                    + " achievement '" + achievements.get(index).getFormattedName() + "'.</col>");
            Discord.jda.getTextChannelById(1227065946469044244L).sendMessage(player.getDisplayName() + " has just completed " + achievements.get(index).getFormattedName()).queue();
            Server.getLogging().write(new ClaimAchievementLog(player, achievements.get(index)));
            return true;
        }
        return false;
    }

    public boolean claimAll(int buttonID, Player c) {
        if (buttonID == 54765) {
            List<Achievement> achievements = Arrays.stream(Achievement.values()).filter(a -> !isClaimed(a.getTier().getId(), a.getId()) && isComplete(a.getTier().getId(), a.getId())).collect(Collectors.toList());
            for (Achievement achievement : achievements) {
                Achievements.addReward(c, achievement);
                setClaimed(achievement.getTier().getId(), achievement.getId(), true);
                c.sendMessage("<col=" + COLOR + ">Claimed the " + achievement.getTier().getName().toLowerCase()
                        + " achievement '" + achievement.getFormattedName() + "'.</col>");
                Discord.jda.getTextChannelById(1227065946469044244L).sendMessage(player.getDisplayName() + " has just completed " + achievement.getFormattedName()).queue();
                Server.getLogging().write(new ClaimAchievementLog(c, achievement));
            }
            AchieveV2.Update(player, player.achievementPage);
            return true;
        }
        return false;
    }

    public boolean isComplete(Achievement achievement) {
        return isComplete(achievement.getTier().getId(), achievement.getId());
    }

    public boolean isComplete(int tier, int index) {
        return completed[tier][index];
    }

    public boolean setComplete(Achievement achievement, boolean state) {
        return setComplete(achievement.getTier().getId(), achievement.getId(), state);
    }

    public boolean setComplete(int tier, int index, boolean state) {
        return this.completed[tier][index] = state;
    }

    public int getAmountRemaining(Achievement achievement) {
        return getAmountRemaining(achievement.getTier().getId(), achievement.getId());
    }

    public int getAmountRemaining(int tier, int index) {
        return amountRemaining[tier][index];
    }

    public void setAmountRemaining(Achievement achievement, int amountRemaining) {
        setAmountRemaining(achievement.getTier().getId(), achievement.getId(), amountRemaining);
    }

    public void setAmountRemaining(int tier, int index, int amountRemaining) {
        this.amountRemaining[tier][index] = amountRemaining;
    }

    public boolean isClaimed(int tier, int index) {
        return claimed[tier][index];
    }

    public boolean setClaimed(Achievement achievement, boolean state) {
        return setClaimed(achievement.getTier().getId(), achievement.getId(), state);
    }

    public boolean setClaimed(int tier, int index, boolean state) {
        return this.claimed[tier][index] = state;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public boolean isFirstAchievementLoginJune2021() {
        return firstAchievementLoginJune2021;
    }

    public void setFirstAchievementLoginJune2021(boolean firstAchievementLoginJune2021) {
        this.firstAchievementLoginJune2021 = firstAchievementLoginJune2021;
    }
}
