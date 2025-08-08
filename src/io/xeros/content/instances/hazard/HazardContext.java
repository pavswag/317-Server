package io.xeros.content.instances.hazard;

import io.xeros.model.entity.player.Position;

/**
 * Context describing a hazard activation instance passed to reactive
 * listeners such as NPCs or players.
 */
public class HazardContext {
    private final EnvironmentalHazardType type;
    private final HazardTier tier;
    private final Position position;

    public HazardContext(EnvironmentalHazardType type, HazardTier tier, Position position) {
        this.type = type;
        this.tier = tier;
        this.position = position;
    }

    public EnvironmentalHazardType getType() {
        return type;
    }

    public HazardTier getTier() {
        return tier;
    }

    public Position getPosition() {
        return position;
    }
}
