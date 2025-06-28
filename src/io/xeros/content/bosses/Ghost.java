package io.xeros.content.bosses;

import io.xeros.Server;
import io.xeros.content.bosspoints.BossPoints;
import io.xeros.content.combat.death.NPCDeath;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Location3D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Ghost {

	/**
	 * Checks wether or not a core is alive, else spawns when corp goes below certain hp
	 */

	public static List<Player> targets = new ArrayList<>();
	public static HashMap<Player, Integer> damageCount = new HashMap<>();

	public static void handleRewards(NPC npc) {
		HashMap<String, Integer> map = new HashMap<>();
		damageCount.forEach((p, i) -> {
			if (map.containsKey(p.getUUID())) {
				map.put(p.getUUID(), map.get(p.getUUID()) + 1);
			} else {
				map.put(p.getUUID(), 1);
			}
			p.getPA().sendConfig(999, 0);
		});

		map.values().removeIf(integer -> integer > 1);

		damageCount.forEach((player, integer) -> {
			if (integer > 1) {
				int amountOfDrops = 1;
				if (NPCDeath.isDoubleDrops()) {
					amountOfDrops++;
				}
				Server.getDropManager().create(player, npc, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, 1429);
				int bossPoints = BossPoints.getPointsOnDeath(npc);
				BossPoints.addPoints(player, bossPoints, false);
				player.getNpcDeathTracker().add(npc.getDefinition().getName(), npc.getDefinition().getCombatLevel(), bossPoints);
			}
		});
		damageCount.clear();
	}
}
