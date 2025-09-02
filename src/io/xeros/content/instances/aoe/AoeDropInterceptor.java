package io.xeros.content.instances.aoe;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import java.util.Optional;

/**
 * Helper to bank drops while inside AOE tier instances.
 */
public class AoeDropInterceptor {

    private static final String ATTR_BANK_OVERRIDE = "aoe_reward_bank";

    public static boolean awardInsideAoe(Player player, GameItem item) {
        if (player == null || item == null) return false;
        int tier = AoeTierController.getActiveTier(player);
        if (tier <= 0) return false;
        Optional<AoeTierRewardsDef> opt = AoeTierRewardsLoader.forTier(tier);
        if (opt.isEmpty()) return false;
        AoeTierRewardsDef def = opt.get();
        boolean bank = player.getAttributes().getBoolean(ATTR_BANK_OVERRIDE, def.isBankAllDrops());
        if (!bank) return false;
        if (def.getBlacklist() != null && def.getBlacklist().contains(item.getId())) return false;
        if (def.getWhitelist() != null && !def.getWhitelist().isEmpty() && !def.getWhitelist().contains(item.getId()))
            return false;
        AoeRewardTracker tracker = AoeTierController.getTracker(player);
        if (tracker != null) tracker.add(item);
        player.getItems().addItemToBankOrDrop(item.getId(), item.getAmount());
        return true;
    }

    public static void setBankOverride(Player player, boolean enable) {
        player.getAttributes().setBoolean(ATTR_BANK_OVERRIDE, enable);
    }
}
