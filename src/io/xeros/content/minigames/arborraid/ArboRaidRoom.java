package io.xeros.content.minigames.arborraid;

import io.xeros.content.instances.InstancedArea;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

public abstract class ArboRaidRoom {

    public abstract void spawn(InstancedArea instancedArea);

    public abstract Position getPlayerSpawnPosition();

    public abstract Boundary getBoundary();

    public abstract boolean handleClickObject(Player player, WorldObject worldObject, int option);

    public abstract Position getDeathPosition();

    public abstract boolean isRoomComplete(InstancedArea instancedArea);
}
