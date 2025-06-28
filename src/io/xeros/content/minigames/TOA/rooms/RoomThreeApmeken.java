package io.xeros.content.minigames.TOA.rooms;

import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.TOA.TombsOfAmascutConstants;
import io.xeros.content.minigames.TOA.TombsOfAmascutRoom;
import io.xeros.content.minigames.TOA.bosses.Apmeken;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.world.objects.GlobalObject;

public class RoomThreeApmeken extends TombsOfAmascutRoom {

    @Override
    public Apmeken spawn(InstancedArea instancedArea) {
        return new Apmeken(instancedArea);
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(3807, 5286);
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject worldObject, int option) {
        return false;
    }

    @Override
    public void handleClickBossGate(Player player, WorldObject worldObject) {
        if (player.getY() >= 4256) {
            player.getPA().movePlayer(player.getX(), 4254, player.getHeight());
        } else {
            player.getPA().movePlayer(player.getX(), 4256, player.getHeight());
        }
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }

    @Override
    public Boundary getBoundary() {
        return TombsOfAmascutConstants.APMEKEN1_BOSS_ROOM_BOUNDARY;
    }

    @Override
    public Position getDeathPosition() {
        return new Position(3795, 5280);
    }

    @Override
    public Position getFightStartPosition() {
        return new Position(3807, 5279);
    }

    @Override
    public GlobalObject getFoodChestPosition() {
        return getFoodChest(new Position(3795, 5277, 0), 2);
    }
}
