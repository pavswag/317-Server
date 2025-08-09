package io.xeros.content.instances;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BossTierDamageTest {

    @Test
    public void damageScalingIncreasesWithTier() {
        assertTrue(BossInstanceManager.BossTier.TIER1.getDamageMultiplier() <
                BossInstanceManager.BossTier.TIER2.getDamageMultiplier());
        assertTrue(BossInstanceManager.BossTier.TIER5.getDamageMultiplier() <
                BossInstanceManager.BossTier.TIER10.getDamageMultiplier());
    }
}
