package io.xeros.content.minigames.blastfurnance.npcs;

import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPCDumbPathFinder;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Position;
import io.xeros.util.task.TaskManager;

public class Thumpy extends BlastFurnaceNpc {
    public Thumpy(int npcId, Position position) {
        super(npcId, position);
        NPCSpawning.spawnNpc(npcId, position.getX(), position.getY(), position.getHeight(), 0, 0);
    }

    public void repair() {
        NPCDumbPathFinder.walkTowards(asNPC(), getX()+1, getY());
        TaskManager.submit(2, () -> startAnimation(898));
        TaskManager.submit(6, () -> NPCDumbPathFinder.walkTowards(asNPC(), getX() -1, getY()));
    }
}
