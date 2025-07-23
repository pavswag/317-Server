package io.xeros.content.minigames.arborraid;

import io.xeros.content.instances.InstanceConfiguration;
import io.xeros.content.instances.InstancedArea;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

import java.util.List;

import static io.xeros.content.minigames.arborraid.ArboRaidConstants.*;

public class ArboRaidInstance extends InstancedArea {

    private final int size;
    private int roomIndex = 0;

    public ArboRaidInstance(int size) {
        super(InstanceConfiguration.CLOSE_ON_EMPTY, new Boundary(1664, 4224, 1727, 4287));
        this.size = size;
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject object, int option) {
        return false;
    }

    public void start(List<Player> players) {
        if (players.isEmpty()) return;
        roomIndex = 0;
        ArboRaidRoom room = ROOM_LIST.get(roomIndex);
        players.forEach(plr -> {
            add(plr);
            plr.moveTo(resolve(room.getPlayerSpawnPosition()));
        });
        room.spawn(this);
    }

    public void moveNextRoom(Player player) {
        if (roomIndex + 1 >= ROOM_LIST.size()) {
            player.moveTo(FINISHED_POSITION);
            return;
        }
        roomIndex++;
        ArboRaidRoom room = ROOM_LIST.get(roomIndex);
        player.moveTo(resolve(room.getPlayerSpawnPosition()));
        room.spawn(this);
    }

    @Override
    public void onDispose() {
    }
}
