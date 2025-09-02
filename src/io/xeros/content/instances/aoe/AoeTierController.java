package io.xeros.content.instances.aoe;

import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

import java.util.List;

/**
 * Minimal controller for starting tiers and tracking player progress. It stores
 * unlock and killcount data on the {@link Player}'s attribute map to avoid
 * invasive save changes.
 */
public class AoeTierController {

    private static final String ATTR_UNLOCKED = "aoe_unlocked_tier";
    private static String kcKey(int tier) { return "aoe_kc_" + tier; }

    public static int getUnlockedTier(Player player) {
        return player.getAttributes().getInt(ATTR_UNLOCKED, 1);
    }

    public static int getKillCount(Player player, int tier) {
        return player.getAttributes().getInt(kcKey(tier), 0);
    }

    public static void setUnlockedTier(Player player, int tier) {
        player.getAttributes().setInt(ATTR_UNLOCKED, tier);
    }

    public static void incrementKill(Player player, int tier) {
        int kc = getKillCount(player, tier) + 1;
        player.getAttributes().setInt(kcKey(tier), kc);
        AoeBossTierDef def = AoeBossTierLoader.getTier(tier);
        if (def != null && kc >= def.unlockKills && tier >= getUnlockedTier(player)) {
            setUnlockedTier(player, tier + 1);
            player.sendMessage("\uD83D\uDD13 You have unlocked AOE tier " + (tier + 1) + ".");
        }
    }

    /** Starts the given tier at the player's current location. */
    public static List<NPC> startTier(Player player, int tier) {
        AoeBossTierDef def = AoeBossTierLoader.getTier(tier);
        if (def == null) {
            player.sendMessage("Unknown tier: " + tier);
            return List.of();
        }
        if (tier > getUnlockedTier(player)) {
            player.sendMessage("You have not unlocked this tier yet.");
            return List.of();
        }
        Position centre = new Position(player.absX, player.absY, player.heightLevel);
        return AoeBossSpawner.spawn(player, def, centre);
    }
}
