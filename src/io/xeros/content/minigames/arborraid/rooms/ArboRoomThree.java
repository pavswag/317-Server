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

public class ArboRoomThree extends ArboRaidRoom {
    @Override
    public void spawn(InstancedArea instancedArea) {
        for (int i = 0; i < 3; i++) {
            NPC npc = new NPC(Npcs.MOSS_GIANT, new Position(1705 + i, 4254, instancedArea.getHeight()));
            instancedArea.add(npc);
        }
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(1711, 4253);
    }

    @Override
    public Boundary getBoundary() {
        return ArboRaidConstants.ROOM_THREE;
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject worldObject, int option) {
        return false;
    }

    @Override
    public Position getDeathPosition() {
        return new Position(1711, 4253);
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }
}
