package io.xeros.content.instances.aoe;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.save.PlayerSaveEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Persists AOE tier progression (unlocked tier and per-tier kill counts)
 * across player sessions.
 */
public class AoeTierProgressSaveEntry implements PlayerSaveEntry {

    private static final String UNLOCKED_KEY = "aoe_unlocked_tier";
    private static String kcKey(int tier) { return "aoe_kc_" + tier; }

    @Override
    public List<String> getKeys(Player player) {
        List<String> keys = new ArrayList<>();
        keys.add(UNLOCKED_KEY);
        for (AoeBossTierDef def : AoeTierRepo.get()) {
            keys.add(kcKey(def.tier));
        }
        return keys;
    }

    @Override
    public boolean decode(Player player, String key, String value) {
        try {
            if (UNLOCKED_KEY.equals(key)) {
                AoeTierController.setUnlockedTier(player, Integer.parseInt(value));
            } else if (key.startsWith("aoe_kc_")) {
                int tier = Integer.parseInt(key.substring(7));
                AoeTierController.setKillCount(player, tier, Integer.parseInt(value));
            }
        } catch (NumberFormatException ignored) {
        }
        return true;
    }

    @Override
    public String encode(Player player, String key) {
        if (UNLOCKED_KEY.equals(key)) {
            return Integer.toString(AoeTierController.getUnlockedTier(player));
        } else if (key.startsWith("aoe_kc_")) {
            int tier = Integer.parseInt(key.substring(7));
            return Integer.toString(AoeTierController.getKillCount(player, tier));
        }
        return "0";
    }

    @Override
    public void login(Player player) {
        // Nothing to do on login
    }
}
