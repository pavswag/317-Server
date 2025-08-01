package io.xeros.content.minigames.arbograve.rooms;

import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.arbograve.ArbograveBoss;
import io.xeros.content.minigames.arbograve.ArbograveConstants;
import io.xeros.content.minigames.arbograve.ArbograveRoom;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

public class StarterRoom extends ArbograveRoom {
    @Override
    public ArbograveBoss spawn(InstancedArea instancedArea) {
        return null;
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(1672, 4270, 0);
    }

    @Override
    public Boundary getBoundary() {
        return ArbograveConstants.ARBO_STARTER_ROOM;
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject worldObject, int option) {
        return false;
    }

    @Override
    public void handleClickBossGate(Player player, WorldObject worldObject) {

    }

    @Override
    public Position getDeathPosition() {
        return new Position(1672, 4270, 0);
    }

    @Override
    public Position getFightStartPosition() {
        return new Position(1672, 4270, 0);
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }
}
