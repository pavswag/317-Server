package io.xeros.content.instances;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import io.xeros.content.instances.TierRewardManager;

public class BossTierDamageTest {

    @Test
    public void damageScalingIncreasesWithTier() {
        assertTrue(BossInstanceManager.BossTier.TIER1.getDamageMultiplier() <
                BossInstanceManager.BossTier.TIER2.getDamageMultiplier());
        assertTrue(BossInstanceManager.BossTier.TIER5.getDamageMultiplier() <
                BossInstanceManager.BossTier.TIER10.getDamageMultiplier());
    }

    @Test
    public void aoeModifiersDifferByTier() {
        assertTrue(BossInstanceManager.BossTier.TIER1.getAoeDamageMultiplier() >
                BossInstanceManager.BossTier.TIER7.getAoeDamageMultiplier());
        assertTrue(BossInstanceManager.BossTier.TIER7.getAoeCooldownMultiplier() > 1.0);
    }

    @Test
    public void npcDensityDecreasesWithTier() {
        assertTrue(BossInstanceManager.BossTier.TIER1.getDesiredNpcDensity() <
                BossInstanceManager.BossTier.TIER7.getDesiredNpcDensity());
        assertTrue(BossInstanceManager.BossTier.TIER7.getDesiredNpcDensity() <
                BossInstanceManager.BossTier.TIER10.getDesiredNpcDensity());
    }

    @Test
    public void tierRostersContainVariety() {
        assertTrue(BossInstanceManager.BossTier.TIER2.getMobs().length > 1);
        assertTrue(BossInstanceManager.BossTier.TIER8.getMobs().length > 1);
    }

    @Test
    public void killstreakRewardsScale() {
        int t1 = TierRewardManager.calculateKillstreakReward(BossInstanceManager.BossTier.TIER1, 10);
        int t10 = TierRewardManager.calculateKillstreakReward(BossInstanceManager.BossTier.TIER10, 10);
        assertTrue(t10 > t1);
    }

    @Test
    public void killstreakRewardValuesReducedAgain() {
        assertEquals(6_250, TierRewardManager.calculateKillstreakReward(BossInstanceManager.BossTier.TIER1, 10));
        assertEquals(2_500, TierRewardManager.calculateAoeKillstreakReward(BossInstanceManager.BossTier.TIER1, 5));
    }
}
