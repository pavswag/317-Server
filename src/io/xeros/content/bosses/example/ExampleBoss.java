package io.xeros.content.bosses.example;

import io.xeros.content.instances.InstanceConfiguration;
import io.xeros.content.instances.InstanceConfigurationBuilder;
import io.xeros.content.instances.InstancedArea;
import io.xeros.model.Npcs;
import io.xeros.model.Items;
import io.xeros.model.collisionmap.WorldObject;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

public class ExampleBoss extends InstancedArea {

    public static final int TOKEN = Items.MAGIC_WHISTLE;

    private static final InstanceConfiguration CONFIGURATION = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(true)
            .setRespawnNpcs(false)
            .createInstanceConfiguration();

    public ExampleBoss() {
        super(CONFIGURATION, Boundary.EXAMPLE_BOSS_ROOM);
    }

    public void enter(Player player) {
        try {
            player.getItems().deleteItem(TOKEN, 1);
            add(player);
            player.moveTo(new Position(3005, 9210, getHeight()));
            NPC npc = new NPC(Npcs.OBOR, new Position(3005, 9220, getHeight()));
            add(npc);
            npc.attackEntity(player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean handleClickObject(Player player, WorldObject object, int option) {
        if (object.getId() == 5000) {
            player.moveTo(new Position(3005, 9205, 0));
            dispose();
            return true;
        }
        return false;
    }

    @Override
    public void onDispose() {
        // Nothing to clean up
    }
}
