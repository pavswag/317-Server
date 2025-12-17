package io.xeros.content.instances.aoe;

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
        int reservedHeight
) {
    public int regionId() {
        return ((baseX >> 3) << 8) | (baseY >> 3);
    }
}

