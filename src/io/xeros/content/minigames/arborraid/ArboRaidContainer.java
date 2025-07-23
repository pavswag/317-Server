package io.xeros.content.minigames.arborraid;

import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import java.util.List;

public class ArboRaidContainer {
    private final Player player;

    public ArboRaidContainer(Player player) {
        this.player = player;
    }

    public boolean handleClickObject(WorldObject object) {
        if (object.getId() != 25155) {
            return false;
        }
        startRaid();
        return true;
    }

    public void startRaid() {
        List<Player> party = List.of(player);
        ArboRaidInstance instance = new ArboRaidInstance(party.size());
        instance.start(party);
    }

    public boolean inRaid() {
        return player.getInstance() instanceof ArboRaidInstance;
    }
}
