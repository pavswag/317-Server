package io.xeros.content.minigames.arbograve.rooms;

import io.xeros.Server;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.arbograve.ArbograveBoss;
import io.xeros.content.minigames.arbograve.ArbograveConstants;
import io.xeros.content.minigames.arbograve.ArbograveRoom;
import io.xeros.content.minigames.arbograve.bosses.Leech;
import io.xeros.content.minigames.arbograve.bosses.NailBeast;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;

import static io.xeros.content.minigames.arbograve.ArbograveConstants.nailBeastsSpawns;

public class RoomFourNailBeast extends ArbograveRoom {
    @Override
    public ArbograveBoss spawn(InstancedArea instancedArea) {
        for (int[] nailBeastsSpawn : nailBeastsSpawns) {
            new NailBeast(new Position(nailBeastsSpawn[0], nailBeastsSpawn[1]), instancedArea);
        }
        Server.getGlobalObjects().add(new GlobalObject(21299, 1675, 4253, instancedArea.getHeight(), 3, 10, -1, -1).setInstance(instancedArea));
        for (int i = 0; i < 8 + (instancedArea.getPlayers().size() * 2); i++) {
            new Leech(new Position(Misc.random(1676, 1687), Misc.random(4249, 4255)),instancedArea);
        }
        return null;
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(1688, 4253);
    }

    @Override
    public Boundary getBoundary() {
        return ArbograveConstants.ARBO_4th_ROOM;
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
        return new Position(1688, 4253);
    }

    @Override
    public Position getFightStartPosition() {
        return new Position(1688, 4253);
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }
}
