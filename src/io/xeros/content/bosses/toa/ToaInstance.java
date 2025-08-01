package io.xeros.content.bosses.toa;

import io.xeros.content.instances.InstanceConfiguration;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.instances.InstanceConfigurationBuilder;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

import java.util.function.Function;

public class ToaInstance extends InstancedArea {

    private final Function<InstancedArea, NPC> bossSupplier;
    private final Position spawnPosition;

    private static final InstanceConfiguration CONFIG = new InstanceConfigurationBuilder()
            .setCloseOnPlayersEmpty(true)
            .setRespawnNpcs(true)
            .createInstanceConfiguration();

    public ToaInstance(Boundary boundary, Function<InstancedArea, NPC> bossSupplier, Position spawnPosition) {
        super(CONFIG, boundary);
        this.bossSupplier = bossSupplier;
        this.spawnPosition = spawnPosition;
    }

    public void enter(Player player) {
        add(player);
        player.moveTo(resolve(spawnPosition));
        NPC npc = bossSupplier.apply(this);
        NPCSpawning.spawnNpc(this, npc.getNpcId(), spawnPosition.getX(), spawnPosition.getY(), getHeight(), 0, 50);
           }

    @Override
    public void onDispose() { }
}
