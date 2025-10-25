package io.xeros.content.instances.aoe;

import io.xeros.content.instances.InstanceHeight;
import io.xeros.model.collisionmap.RegionProvider;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilities for allocating heights and preparing dynamic map chunks for
 * AOE tier instances.
 */
public final class AoeInstanceService {

    private static final Logger logger = LoggerFactory.getLogger(AoeInstanceService.class);

    private AoeInstanceService() {}

    /** Allocate a free height level for an AOE tier instance. */
    public static int allocateHeight(String key) {
        int height = InstanceHeight.getFreeAndReserve();
        logger.info("[AOE-MAP] allocate key={} height={}", key, height);
        return height;
    }

    /** Build dynamic region data for the tier at the given height. */
    public static boolean buildDynamicRegion(AoeBossTierDef def, int height) {
        if (def == null) {
            return false;
        }
        int baseX;
        int baseY;
        if (def.templateRegionId > 0) {
            baseX = (def.templateRegionId >> 8) * 64;
            baseY = (def.templateRegionId & 255) * 64;
        } else {
            baseX = def.templateX;
            baseY = def.templateY;
        }
        for (int dx = 0; dx < def.widthChunks; dx++) {
            for (int dy = 0; dy < def.heightChunks; dy++) {
                int x = baseX + dx * 8;
                int y = baseY + dy * 8;
                RegionProvider.getGlobal().get(x, y);
            }
        }
        logger.info("[AOE-MAP] build tier={} height={} templateRegion={} chunks={}x{} rot={}",
                def.tier, height, def.templateRegionId, def.widthChunks, def.heightChunks, def.rotation);
        return true;
    }

    /** Compute spawn coordinates in the new instance. */
    public static int[] computeSpawn(AoeBossTierDef def, int height) {
        int baseX;
        int baseY;
        if (def.templateRegionId > 0) {
            baseX = (def.templateRegionId >> 8) * 64;
            baseY = (def.templateRegionId & 255) * 64;
        } else {
            baseX = def.templateX;
            baseY = def.templateY;
        }
        int absX = baseX + def.spawnOffsetX;
        int absY = baseY + def.spawnOffsetY;
        return new int[] {absX, absY, height};
    }

    /** Teleport the player into the instance and request a map region update. */
    public static void teleportIntoInstance(Player p, int absX, int absY, int height) {
        logger.info("[AOE-MAP] teleport x={} y={} h={} region={}", absX, absY, height,
                ((absX >> 6) << 8) + (absY >> 6));
        p.getPA().requestUpdates();
        p.moveTo(new Position(absX, absY, height));
    }
}
