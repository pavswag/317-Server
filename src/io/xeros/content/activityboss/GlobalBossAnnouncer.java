package io.xeros.content.activityboss;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

/**
 * Sends progress announcements for global boss events while allowing
 * players to opt-out of notifications.
 */
public class GlobalBossAnnouncer {

    private GlobalBossAnnouncer() {
    }

    public static void announceProgress(GlobalBossType type, int progress) {
        int threshold = type.getThreshold();
        int percent = progress * 100 / threshold;
        if (percent < 80) {
            return;
        }
        String message = "@dre@Global Boss nearly ready! " + percent + "% progress to trigger!";
        for (Player p : PlayerHandler.getPlayers()) {
            if (p != null) {
                p.sendMessage(message);
            }
        }
    }
}
