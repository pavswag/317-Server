package io.xeros.content.minigames.arbograve;

import io.xeros.content.instances.InstancedArea;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

public abstract class ArbograveRoom {

    public abstract ArbograveBoss spawn(InstancedArea instancedArea);

    public abstract Position getPlayerSpawnPosition();

    public abstract Boundary getBoundary();

    public abstract boolean handleClickObject(Player player, WorldObject worldObject, int option);

    public abstract void handleClickBossGate(Player player, WorldObject worldObject);

    public abstract Position getDeathPosition();

    public abstract Position getFightStartPosition();

    public abstract boolean isRoomComplete(InstancedArea instancedArea);

}
