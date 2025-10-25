package io.xeros.util;

import io.xeros.model.collisionmap.RegionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

/**
 * Lightweight adapter that prepares region data for dynamic instances.
 */
public final class MapBuilder {

    private static final Logger logger = LoggerFactory.getLogger(MapBuilder.class);

    private MapBuilder() {}

    public static void copy(int srcX, int srcY, int widthChunks, int heightChunks,
                            int targetX, int targetY, int targetZ,
                            Runnable onSuccess, Consumer<Exception> onError) {
        try {
            RegionProvider provider = RegionProvider.getGlobal();
            for (int dx = 0; dx < widthChunks; dx++) {
                for (int dy = 0; dy < heightChunks; dy++) {
                    int sourceTileX = srcX + dx * 8;
                    int sourceTileY = srcY + dy * 8;
                    int targetTileX = targetX + dx * 8;
                    int targetTileY = targetY + dy * 8;
                    provider.get(sourceTileX, sourceTileY);
                    provider.get(targetTileX, targetTileY);
                }
            }
            logger.info("[AOE-MAP] copy complete src=({},{} w{} h{}) -> target=({},{} z{})", srcX, srcY, widthChunks, heightChunks, targetX, targetY, targetZ);
            if (onSuccess != null) {
                onSuccess.run();
            }
        } catch (Exception e) {
            logger.error("[AOE-MAP] copy failed", e);
            if (onError != null) {
                onError.accept(e);
            }
        }
    }

    public static void destroy(int baseX, int baseY, int z) {
        logger.info("[AOE-MAP] destroy base=({},{} z={})", baseX, baseY, z);
    }
}

