package io.xeros.content.instances.aoe;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Immutable descriptor for a single active AOE instance.
 */
public final class AoeInstance {

    private final UUID id;
    private final AoeBossTierDef tier;
    private final int baseX;
    private final int baseY;
    private final int z;
    private final int ownerPid;
    private final int reservedHeight;
    private final Set<Integer> playerPids;

    public AoeInstance(UUID id, AoeBossTierDef tier, int baseX, int baseY, int z,
                       int ownerPid, int reservedHeight, Set<Integer> playerPids) {
        this.id = id;
        this.tier = tier;
        this.baseX = baseX;
        this.baseY = baseY;
        this.z = z;
        this.ownerPid = ownerPid;
        this.reservedHeight = reservedHeight;
        this.playerPids = playerPids;
    }

    public UUID getId() {
        return id;
    }

    public AoeBossTierDef getTier() {
        return tier;
    }

    public int getBaseX() {
        return baseX;
    }

    public int getBaseY() {
        return baseY;
    }

    public int getZ() {
        return z;
    }

    public int getOwnerPid() {
        return ownerPid;
    }

    public int getReservedHeight() {
        return reservedHeight;
    }

    public Set<Integer> getPlayerPids() {
        return playerPids;
    }

    public int getRegionId() {
        return ((baseX >> 3) << 8) | (baseY >> 3);
    }

    public List<Player> playersPresent() {
        if (playerPids == null || playerPids.isEmpty()) {
            return Collections.emptyList();
        }
        List<Player> players = new ArrayList<>(playerPids.size());
        for (Integer pid : playerPids) {
            if (pid == null || pid < 0 || pid >= PlayerHandler.players.length) {
                continue;
            }
            Player player = PlayerHandler.players[pid];
            if (player == null || player.isDisconnected()) {
                continue;
            }
            if (player.heightLevel != z) {
                continue;
            }
            if (Math.abs(player.absX - baseX) >= 64 || Math.abs(player.absY - baseY) >= 64) {
                continue;
            }
            players.add(player);
        }
        return players;
    }
}
