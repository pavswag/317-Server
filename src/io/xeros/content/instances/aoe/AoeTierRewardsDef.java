package io.xeros.content.instances.aoe;

import java.util.List;

/**
 * Definition of extra rewards and behaviour per AOE tier instance.
 */
public class AoeTierRewardsDef {

    public static class BonusReward {
        public int itemId;
        public int min;
        public int max;
    }

    public int tier;
    public String name;
    public int endOfRunRolls;
    public List<BonusReward> bonusRewards;
    public boolean bankAllDrops = true;
    public List<Integer> blacklist;
    public List<Integer> whitelist;
    public int fortuneXpPerKill;
    public String reportTitle;

    public int getTier() { return tier; }
    public String getName() { return name; }
    public int getEndOfRunRolls() { return endOfRunRolls; }
    public List<BonusReward> getBonusRewards() { return bonusRewards; }
    public boolean isBankAllDrops() { return bankAllDrops; }
    public List<Integer> getBlacklist() { return blacklist; }
    public List<Integer> getWhitelist() { return whitelist; }
    public int getFortuneXpPerKill() { return fortuneXpPerKill; }
    public String getReportTitle() { return reportTitle; }
}
