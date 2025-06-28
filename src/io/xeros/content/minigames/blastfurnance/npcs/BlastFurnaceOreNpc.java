package io.xeros.content.minigames.blastfurnance.npcs;

import io.xeros.content.minigames.blastfurnance.BlastFurnace;
import io.xeros.content.minigames.blastfurnance.BlastFurnaceOre;
import io.xeros.content.minigames.blastfurnance.dispenser.BarDispenser;
import io.xeros.model.Npcs;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.util.task.Task;
import io.xeros.util.task.TaskManager;
import lombok.Getter;

public class BlastFurnaceOreNpc extends NPC {

    private static final Position ORE_STARTING_POSITION = new Position(1942, 4966, 0);
    private static final Position ORE_END_POSITION = new Position(1942, 4963, 0);

    private Task task;
    @Getter
    protected BlastFurnaceOre ore;
    public BlastFurnaceOreNpc(int npcId, Position position) {
        super(npcId, position);
    }

    public void start() {
        if (task != null) {
            return;
        }

        TaskManager.submit(1, () -> TaskManager.submit(task = new Task(1, this, false) {
            @Override
            protected void execute() {
                if (asNPC() == null) {
                    start();
                    return;
                }
                moveTowards(ORE_END_POSITION.getX(), ORE_END_POSITION.getY());
                if (getPosition().equals(ORE_END_POSITION)) {
                    onArrival();
                    BlastFurnaceOreNpc.this.stop();
                }
            }
        }));
    }

    public void stop() {
        if (task != null) {
            task.stop();
            task = null;
        }
    }

    public void onArrival() {
        startAnimation(2434);
        TaskManager.submit(1, () -> {
            PlayerHandler.getPlayers().forEach(plr -> {
                if (this.asNPC().spawnedBy == plr.getIndex()) {
                    plr.getBlastFurnace().sendToBelt(ore);
                    BlastFurnace.removeNpc(plr, ore);
                    BarDispenser.melt(plr);
                }
            });
        });
    }

    protected void setup(Player player, BlastFurnaceOre ore) {
        spawnedBy = player.getIndex();
        this.ore = ore;
    }

    public static BlastFurnaceOreNpc create(BlastFurnaceOre ore, Player player) {
        var npc = new BlastFurnaceOreNpc(getNpcId(ore), ORE_STARTING_POSITION);
        npc.setup(player, ore);
        NPCSpawning.spawnNpc(player, npc.getNpcId(), ORE_STARTING_POSITION.getX(), ORE_STARTING_POSITION.getY(), 0, 0,0,false,false);
        return npc;
    }

    public static int getNpcId(BlastFurnaceOre ore) {
        switch (ore) {
            case TIN:
                return Npcs.TIN_ORE;
            case COPPER:
                return Npcs.COPPER_ORE;
            case IRON:
                return Npcs.IRON_ORE;
            case SILVER:
                return Npcs.SILVER_ORE;
            case COAL:
                return Npcs.COAL;
            case GOLD:
                return Npcs.GOLD_ORE;
            case MITHRIL:
                return Npcs.MITHRIL_ORE;
            case ADAMANTITE:
                return Npcs.ADAMANTITE_ORE;
            case RUNITE:
                return Npcs.RUNITE_ORE;
        }
        return -1;
    }
}
