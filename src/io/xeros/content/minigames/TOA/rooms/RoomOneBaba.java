package io.xeros.content.minigames.TOA.rooms;

import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.TOA.TombsOfAmascutConstants;
import io.xeros.content.minigames.TOA.TombsOfAmascutRoom;
import io.xeros.content.minigames.TOA.bosses.Baba;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.world.objects.GlobalObject;

public class RoomOneBaba extends TombsOfAmascutRoom {

    @Override
    public Baba spawn(InstancedArea instancedArea) {
        return new Baba(instancedArea);
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(3815, 5407);
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject worldObject, int option) {
        return false;
    }

    @Override
    public void handleClickBossGate(Player player, WorldObject worldObject) {
        if (player.getX() >= 3186) {
            player.getPA().movePlayer(3184, player.getY(), player.getHeight());
        } else if (player.getX() <= 3184) {
            player.getPA().movePlayer(3186, player.getY(), player.getHeight());
        }
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }

    @Override
    public Boundary getBoundary() {
        return TombsOfAmascutConstants.BABA_BOSS_ROOM_BOUNDARY;
    }

    @Override
    public Position getDeathPosition() {
        return new Position(3799, 5409);
    }

    @Override
    public Position getFightStartPosition() {
        return new Position(3808, 5408, 0);
    }

    @Override
    public GlobalObject getFoodChestPosition() {
        return getFoodChest(new Position(3175, 4422, 0), 0);
    }
}