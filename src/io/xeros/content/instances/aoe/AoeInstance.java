package io.xeros.content.instances.aoe;

import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Immutable record describing a single active AOE instance.
 */
public record AoeInstance(
        UUID id,
        AoeBossTierDef tier,
        int baseX,
        int baseY,
        int z,
        int ownerPid,
        int reservedHeight,
        Set<Integer> playerPids
) {
    public int regionId() {
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

