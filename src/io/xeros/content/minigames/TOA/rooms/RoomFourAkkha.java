package io.xeros.content.minigames.TOA.rooms;

import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.TOA.TombsOfAmascutConstants;
import io.xeros.content.minigames.TOA.TombsOfAmascutRoom;
import io.xeros.content.minigames.TOA.bosses.Akkha;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.world.objects.GlobalObject;

public class RoomFourAkkha extends TombsOfAmascutRoom {

    @Override
    public Akkha spawn(InstancedArea instancedArea) {
        return new Akkha(instancedArea);
    }

    @Override
    public Position getPlayerSpawnPosition() {
        return new Position(3680, 5401,1);
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject worldObject, int option) {
        return false;
    }

    @Override
    public void handleClickBossGate(Player player, WorldObject worldObject) {
        if (player.getY() >= 4308) {
            player.getPA().movePlayer(player.getX(), 4306, player.getHeight());
        } else {
            player.getPA().movePlayer(player.getX(), 4308, player.getHeight());
        }
    }

    @Override
    public boolean isRoomComplete(InstancedArea instancedArea) {
        return instancedArea.getNpcs().isEmpty();
    }

    @Override
    public Boundary getBoundary() {
        return TombsOfAmascutConstants.AKKHA_BOSS_ROOM_BOUNDARY;
    }

    @Override
    public Position getDeathPosition() {
        return new Position(3695, 5407,1 );
    }

    @Override
    public Position getFightStartPosition() {
        return new Position(3680, 5408, 1);
    }

    @Override
    public GlobalObject getFoodChestPosition() {
        return getFoodChest(new Position(3694, 5405, 1), 0);
    }
}
