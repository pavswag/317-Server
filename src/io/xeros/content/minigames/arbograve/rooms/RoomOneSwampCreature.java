package io.xeros.content.minigames.arbograve.rooms;

import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.arbograve.ArbograveBoss;
import io.xeros.content.minigames.arbograve.ArbograveConstants;
import io.xeros.content.minigames.arbograve.ArbograveRoom;
import io.xeros.content.minigames.arbograve.bosses.GiantSnails;
import io.xeros.content.minigames.arbograve.bosses.Leech;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;

public class RoomOneSwampCreature extends ArbograveRoom {
    @Override
    public ArbograveBoss spawn(InstancedArea instancedArea) {
        spawnSnails(instancedArea);
        for (int i = 0; i < 8 + (instancedArea.getPlayers().size() * 2); i++) {
            new Leech(new Position(Misc.random(1684, 1700), Misc.random(4264, 4270)),instancedArea);
        }

        return null;
    }

    public void spawnSnails(InstancedArea instancedArea) {
        for (int i = 0; i < ArbograveConstants.giantSnailSpawns.length; i++) {
            new GiantSnails(new Position(ArbograveConstants.giantSnailSpawns[i][0], ArbograveConstants.giantSnailSpawns[i][1]), instancedArea);
        }
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(1685, 4269);
    }

    @Override
    public Boundary getBoundary() {
        return ArbograveConstants.ARBO_1st_ROOM;
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
        return new Position(1685, 4269);
    }

    @Override
    public Position getFightStartPosition() {
        return new Position(1685, 4269);
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }
}
