package io.xeros.content.minigames.arborraid.rooms;

import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.arborraid.ArboRaidRoom;
import io.xeros.content.minigames.arborraid.ArboRaidConstants;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

public class ArboStarterRoom extends ArboRaidRoom {
    @Override
    public void spawn(InstancedArea instancedArea) {
        // No NPCs
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(1672, 4270, 0);
    }

    @Override
    public Boundary getBoundary() {
        return ArboRaidConstants.ROOM_STARTER;
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject worldObject, int option) {
        return false;
    }

    @Override
    public Position getDeathPosition() {
        return new Position(1672, 4270, 0);
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return true;
    }
}
