package io.xeros.content.instances.aoe;

import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimal controller for starting tiers and tracking player progress. It stores
 * unlock and killcount data on the {@link Player}'s attribute map to avoid
 * invasive save changes.
 */
public class AoeTierController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AoeTierController.class);
    private static final AoeInstanceService INSTANCE_SERVICE = new AoeInstanceService();

    private static final String ATTR_UNLOCKED = "aoe_unlocked_tier";
    private static final String ATTR_ACTIVE = "aoe_active_tier";
    private static final String ATTR_TRACKER = "aoe_reward_tracker";
    private static final String ATTR_INSTANCE = "aoe_instance";
    private static String kcKey(int tier) { return "aoe_kc_" + tier; }

    public static int getUnlockedTier(Player player) {
        return player.getAttributes().getInt(ATTR_UNLOCKED, 1);
    }

    public static int getKillCount(Player player, int tier) {
        return player.getAttributes().getInt(kcKey(tier), 0);
    }

    /** Convenience alias used by dialogue helpers. */
    public static int getKillcount(Player player, int tier) {
        return getKillCount(player, tier);
    }

    /** Returns true if the player has unlocked the given tier number. */
    public static boolean isUnlocked(Player player, int tier) {
        return getUnlockedTier(player) >= tier;
    }

    public static void setUnlockedTier(Player player, int tier) {
        player.getAttributes().setInt(ATTR_UNLOCKED, tier);
    }

    /** Sets the kill count for the given tier. */
    public static void setKillCount(Player player, int tier, int count) {
        player.getAttributes().setInt(kcKey(tier), count);
    }

    public static void incrementKill(Player player, int tier) {
        int kc = getKillCount(player, tier) + 1;
        player.getAttributes().setInt(kcKey(tier), kc);
        AoeBossTierDef def = AoeTierRepo.byTier(tier);
        if (def != null && kc >= def.unlockKills && tier >= getUnlockedTier(player)) {
            setUnlockedTier(player, tier + 1);
            player.sendMessage("\uD83D\uDD13 You have unlocked AOE tier " + (tier + 1) + ".");
        }
    }

    /** Starts the given tier at the player's current location. */
    public static void startTier(Player player, int tier) {
        endTier(player, false);
        AoeBossTierDef def = AoeTierRepo.byTier(tier);
        if (def == null) {
            player.sendMessage("Unknown tier: " + tier);
            return;
        }
        if (def.disabled) {
            player.sendMessage("Tier disabled: " + def.getDisabledReason());
            logger.info("[AOE] Start refused: disabled: {}", def.getDisabledReason());
            return;
        }
        if (tier > getUnlockedTier(player)) {
            player.sendMessage("You have not unlocked this tier yet.");
            return;
        }
        player.sendMessage("Preparing AOE tier " + tier + "...");
        INSTANCE_SERVICE.buildAndEnter(player, def, instance -> {
            player.getAttributes().setInt(ATTR_ACTIVE, tier);
            player.getAttributes().set(ATTR_TRACKER, new AoeRewardTracker());
            player.getAttributes().set(ATTR_INSTANCE, instance.getId());
            player.sendMessage("AOE Tier " + tier + " — Type ::leaveaoe to exit.");
        }, error -> {
            player.sendMessage("AOE build failed: " + error);
            logger.warn("[AOE] build failed player={} tier={} reason={}", player.getLoginName(), tier, error);
        });
    }

    public static int getActiveTier(Player player) {
        return player.getAttributes().getInt(ATTR_ACTIVE, 0);
    }

    public static AoeRewardTracker getTracker(Player player) {
        Object obj = player.getAttributes().get(ATTR_TRACKER);
        return obj instanceof AoeRewardTracker ? (AoeRewardTracker) obj : null;
    }

    public static void endTier(Player player, boolean showReport) {
        int tier = getActiveTier(player);
        if (tier <= 0) return;
        AoeRewardTracker tracker = getTracker(player);
        List<GameItem> loot = tracker != null ? tracker.snapshot() : new ArrayList<>();
        List<GameItem> finalLoot = new ArrayList<>(loot);
        AoeTierRewardsLoader.forTier(tier).ifPresent(def -> {
            for (int i = 0; i < def.getEndOfRunRolls(); i++) {
                if (def.getBonusRewards() != null) {
                    for (AoeTierRewardsDef.BonusReward br : def.getBonusRewards()) {
                        int amt = Misc.random(br.min, br.max);
                        GameItem gi = new GameItem(br.itemId, amt);
                        finalLoot.add(gi);
                        player.getItems().addItemToBankOrDrop(gi.getId(), gi.getAmount());
                    }
                }
            }
            if (showReport && !finalLoot.isEmpty()) {
                AoeTreasureTrailsAdapter.openLootViewer(player, def.getReportTitle(), finalLoot);
            }
        });
        if (tracker != null) tracker.clear();
        player.getAttributes().remove(ATTR_TRACKER);
        player.getAttributes().removeInt(ATTR_ACTIVE);
        AoeTierRepo.instanceForPlayer(player).ifPresent(instance -> {
            AoeTierRepo.dissociatePlayer(player);
            INSTANCE_SERVICE.teardown(instance);
        });
        player.getAttributes().remove(ATTR_INSTANCE);
    }
}
