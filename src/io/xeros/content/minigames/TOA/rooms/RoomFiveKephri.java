package io.xeros.content.minigames.TOA.rooms;

import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.TOA.TombsOfAmascutConstants;
import io.xeros.content.minigames.TOA.TombsOfAmascutRoom;
import io.xeros.content.minigames.TOA.bosses.Kephri;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.world.objects.GlobalObject;

public class RoomFiveKephri extends TombsOfAmascutRoom {

    @Override
    public Kephri spawn(InstancedArea instancedArea) {
        return new Kephri(instancedArea);
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(3544, 5404);
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject worldObject, int option) {
        return false;
    }

    @Override
    public void handleClickBossGate(Player player, WorldObject worldObject) {
        if (worldObject.getY() < 4392) {        // South
            if (player.getY() >= 4380) {
                player.getPA().movePlayer(player.getX(), 4378, player.getHeight());
            } else {
                player.getPA().movePlayer(player.getX(), 4380, player.getHeight());
            }
        } else {                                // North
            if (player.getY() >= 4396) {
                player.getPA().movePlayer(player.getX(), 4394, player.getHeight());
            } else {
                player.getPA().movePlayer(player.getX(), 4396, player.getHeight());
            }
        }
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }

    @Override
    public Boundary getBoundary() {
        return TombsOfAmascutConstants.KEPHRI_BOSS_ROOM_BOUNDARY;
    }

    @Override
    public Position getDeathPosition() {
        return new Position(3550, 5408);
    }

    @Override
    public Position getFightStartPosition() {
        return new Position(3550, 5408);
    }

    @Override
    public GlobalObject getFoodChestPosition() {
        return getFoodChest(new Position(3546, 5400), 3);
    }
}
