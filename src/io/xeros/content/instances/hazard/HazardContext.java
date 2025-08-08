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
    private final java.util.List<Position> affectedTiles;

    public HazardContext(EnvironmentalHazardType type, HazardTier tier, Position position) {
        this(type, tier, position, java.util.List.of(position));
    }

    public HazardContext(EnvironmentalHazardType type, HazardTier tier, Position position, java.util.List<Position> affectedTiles) {
        this.type = type;
        this.tier = tier;
        this.position = position;
        this.affectedTiles = affectedTiles;
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

    public java.util.List<Position> getAffectedTiles() {
        return affectedTiles;
    }
}
