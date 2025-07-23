package io.xeros.content.minigames.arborraid.rooms;

import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.arborraid.ArboRaidRoom;
import io.xeros.content.minigames.arborraid.ArboRaidConstants;
import io.xeros.model.Npcs;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

public class ArboRoomFive extends ArboRaidRoom {
    @Override
    public void spawn(InstancedArea instancedArea) {
        NPC boss = new NPC(Npcs.GREEN_DRAGON, new Position(1682, 4243, instancedArea.getHeight()));
        instancedArea.add(boss);
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(1681, 4243);
    }

    @Override
    public Boundary getBoundary() {
        return ArboRaidConstants.ROOM_FIVE;
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject worldObject, int option) {
        return false;
    }

    @Override
    public Position getDeathPosition() {
        return new Position(1681, 4243);
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }
}
