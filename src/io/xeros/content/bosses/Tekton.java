package io.xeros.content.bosses;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;

public class Tekton {

	public static void tektonSpecial(Player player) {
		NPC TEKTON = NPCHandler.getNpc(7544);

		if (TEKTON.isDead()) {
			return;
		}

		NPCHandler.npcs[TEKTON.getIndex()].forceChat("RAAAAAAAA!");
		TEKTON.underAttackBy = -1;
		TEKTON.underAttack = false;

		if (Misc.isLucky(5)) {
			DonorBoss3.burnGFX(player, TEKTON);
		}
	}
}
