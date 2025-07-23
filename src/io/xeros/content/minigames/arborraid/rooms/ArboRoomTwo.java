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

public class ArboRoomTwo extends ArboRaidRoom {
    @Override
    public void spawn(InstancedArea instancedArea) {
        for (int i = 0; i < 4; i++) {
            NPC npc = new NPC(Npcs.HILL_GIANT, new Position(1712, 4270 - i, instancedArea.getHeight()));
            instancedArea.add(npc);
        }
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(1713, 4273);
    }

    @Override
    public Boundary getBoundary() {
        return ArboRaidConstants.ROOM_TWO;
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject worldObject, int option) {
        return false;
    }

    @Override
    public Position getDeathPosition() {
        return new Position(1713, 4273);
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }
}
