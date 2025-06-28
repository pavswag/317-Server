package io.xeros.content.minigames.TOA.rooms;

import  io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.TOA.TombsOfAmascutConstants;
import io.xeros.content.minigames.TOA.TombsOfAmascutRoom;
import io.xeros.content.minigames.TOA.bosses.TumekensWarden;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;

public class RoomSixTumekensWarden extends TombsOfAmascutRoom {

    private static final Position[] CAGES = {
            new Position(3157, 4325, 0),
            new Position(3159, 4325, 0),
            new Position(3161, 4325, 0),
            new Position(3175, 4325, 0),
            new Position(3177, 4325, 0),
            new Position(3179, 4325, 0),
    };

    @Override
    public TumekensWarden spawn(InstancedArea instancedArea) {
        return new TumekensWarden(instancedArea);
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(3807, 5148, 2);
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject worldObject, int option) {
        return false;
    }

    @Override
    public void handleClickBossGate(Player player, WorldObject worldObject) {

    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }

    @Override
    public Boundary getBoundary() {
        return TombsOfAmascutConstants.TUMEKENS_WARDEN_BOSS_ROOM_BOUNDARY;
    }

    @Override
    public Position getDeathPosition() {
        return CAGES[Misc.trueRand(CAGES.length)];
    }

    @Override
    public Position getFightStartPosition() {
        return null;
    }

    @Override
    public GlobalObject getFoodChestPosition() {
        return null;
    }
}
