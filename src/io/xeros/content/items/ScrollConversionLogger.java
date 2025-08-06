package io.xeros.content.items;

import io.xeros.model.entity.player.Player;

import java.util.logging.Logger;

/**
 * Logs scroll conversion events.
 */
public class ScrollConversionLogger {

    private static final Logger logger = Logger.getLogger(ScrollConversionLogger.class.getName());

    public static void log(Player player, String message) {
        logger.info("[" + player.getDisplayName() + "] " + message);
    }
}
