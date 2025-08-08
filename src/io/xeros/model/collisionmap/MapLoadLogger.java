package io.xeros.model.collisionmap;

import java.util.logging.Logger;

/**
 * Utility for auditing map and region loading.
 */
public final class MapLoadLogger {

    private static final Logger logger = Logger.getLogger(MapLoadLogger.class.getName());

    private MapLoadLogger() {
    }

    public static void log(String source, int regionId, String mapFile, boolean alreadyLoaded, boolean success, Throwable t) {
        String message = String.format("[MapLoad] source=%s regionId=%d file=%s alreadyLoaded=%s success=%s", source, regionId, mapFile, alreadyLoaded, success);
        if (t != null) {
           // logger.warning(message + " error=" + t.getMessage());
        } else {
           // logger.info(message);
        }
    }
}
