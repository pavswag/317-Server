package io.xeros.model.world.areas;

import io.xeros.model.collisionmap.ObjectDef;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;

import java.util.Objects;

public abstract class Zone {

    private final Boundary ZONE_BOUNDS;

    protected Zone(Boundary zoneBounds) {
        this.ZONE_BOUNDS = zoneBounds;
    }

    public Boundary getBoundary() {
        if (Objects.isNull(ZONE_BOUNDS))
            throw new IllegalStateException("Boundary not set");
        return ZONE_BOUNDS;
    }
    public abstract boolean handledClickObject(Player player, ObjectDef def);
}
