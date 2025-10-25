package io.xeros.content.instances.aoe;

import io.xeros.content.instances.InstanceHeight;
import io.xeros.model.entity.player.Player;
import io.xeros.util.MapBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Coordinates dynamic map construction and teardown for AOE instances.
 */
public class AoeInstanceService {

    private static final Logger logger = LoggerFactory.getLogger(AoeInstanceService.class);

    public void buildAndEnter(Player player, AoeBossTierDef tier, Consumer<AoeInstance> onReady, Consumer<String> onError) {
        if (player == null || tier == null) {
            if (onError != null) onError.accept("Invalid player or tier");
            return;
        }

        String mapId = tier.resolveMapId();
        Optional<AoeZoneMapDef> mapOpt = AoeZoneMaps.forId(mapId);
        if (mapOpt.isEmpty()) {
            if (onError != null) onError.accept("Missing map config for " + mapId);
            return;
        }
        AoeZoneMapDef map = mapOpt.get();

        int reservedHeight = InstanceHeight.getFreeAndReserve();
        AoeZoneMapDef.Source src = map.getSource();
        AoeZoneMapDef.Target target = map.getTarget();
        int baseX = target.getBaseX();
        int baseY = target.getBaseY();
        int targetZ = reservedHeight + target.getZ();

        logger.info("[AOE-MAP] build tier={} mapId={} src=({},{} w{} h{} z{}) target=({},{} z{}) reservedHeight={}",
                tier.getTier(), mapId,
                src.getFromX(), src.getFromY(), src.getWidth(), src.getHeight(), src.getZ(),
                baseX, baseY, targetZ, reservedHeight);

        try {
            MapBuilder.copy(src.getFromX(), src.getFromY(), src.getWidth(), src.getHeight(),
                    baseX, baseY, targetZ,
                    () -> handleMapBuilt(player, tier, map, reservedHeight, baseX, baseY, targetZ, onReady),
                    error -> handleMapError(reservedHeight, onError, error));
        } catch (Exception e) {
            handleMapError(reservedHeight, onError, e);
        }
    }

    private void handleMapBuilt(Player player, AoeBossTierDef tier, AoeZoneMapDef map, int reservedHeight,
                                int baseX, int baseY, int z, Consumer<AoeInstance> onReady) {
        AoeZoneMapDef.Spawn spawn = map.getSpawn();
        int spawnX = spawn != null ? spawn.getX() : baseX + 4;
        int spawnY = spawn != null ? spawn.getY() : baseY + 4;
        int spawnZ = spawn != null ? z + spawn.getZ() : z;

        Set<Integer> playerPids = Collections.newSetFromMap(new ConcurrentHashMap<>());
        AoeInstance instance = new AoeInstance(UUID.randomUUID(), tier, baseX, baseY, z, player.getIndex(), reservedHeight, playerPids);
        playerPids.add(player.getIndex());
        AoeTierRepo.registerInstance(player, instance);

        player.attacking.reset();
        player.getPA().movePlayer(spawnX, spawnY, spawnZ);
        player.getPA().requestUpdates();

        AoeNpcSpawner.spawnForInstance(instance, map);

        logger.info("[AOE-MAP] player={} entered instance={} tier={} spawn=({},{}.{})", player.getLoginName(), instance.id(),
                tier.getTier(), spawnX, spawnY, spawnZ);

        if (onReady != null) {
            onReady.accept(instance);
        }
    }

    private void handleMapError(int reservedHeight, Consumer<String> onError, Exception error) {
        InstanceHeight.free(reservedHeight);
        logger.error("[AOE-MAP] build failed", error);
        if (onError != null) {
            onError.accept("Map copy failed: " + error.getMessage());
        }
    }

    public void teardown(AoeInstance instance) {
        if (instance == null) {
            return;
        }
        try {
            AoeNpcSpawner.despawnForInstance(instance);
            MapBuilder.destroy(instance.baseX(), instance.baseY(), instance.z());
        } catch (Exception e) {
            logger.error("[AOE-MAP] teardown error id={}", instance.id(), e);
        } finally {
            InstanceHeight.free(instance.reservedHeight());
            AoeTierRepo.clearInstance(instance.id());
            if (instance.playerPids() != null) {
                instance.playerPids().clear();
            }
        }
    }
}

