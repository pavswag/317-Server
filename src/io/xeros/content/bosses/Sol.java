package io.xeros.content.bosses;

import io.xeros.Server;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.bosspoints.BossPoints;
import io.xeros.content.combat.death.NPCDeath;
import io.xeros.model.Npcs;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Location3D;
import io.xeros.util.discord.Discord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Sol {

	/**
	 * Checks wether or not a core is alive, else spawns when corp goes below certain hp
	 */
	public static void checkCore(NPC corp) {
		if (corp == null || corp.getAttributes().getBoolean("spawned_core")) {
			return;
		}

		if (corp.getHealth().getCurrentHealth() < 2000) {
			NPCSpawning.spawnNpc(Npcs.SKELETAL_MYSTIC_2, 1829, 3106, 0, 0, 20);
			corp.getAttributes().setBoolean("spawned_core", true);
		}
	}

	public static void healWhenNoPlayers(NPC corp) {
		if (!corp.getHealth().isMaximum()) {
			if (Boundary.getPlayersInBoundary(Boundary.SOL) == 0) {
				corp.getHealth().reset();
				NPC core = NPCHandler.getNpc(Npcs.SKELETAL_MYSTIC_2);
				if (core != null)
					core.getHealth().reset();
			}
		}
	}

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

		for (String s : map.keySet()) {
			if (map.containsKey(s) && map.get(s) > 1) {
				for (Player player : PlayerHandler.getPlayers()) {
					if (player.getUUID().equalsIgnoreCase(s)) {
						//Discord.writeServerSyncMessage("[s] "+player.getDisplayName() + " has tried to take more than 2 account's there!");
					}
				}
			}
		}

		map.values().removeIf(integer -> integer > 1);

		damageCount.forEach((player, integer) -> {
			if (integer > 1 && Boundary.isIn(player, Boundary.SOL) && map.containsKey(player.getUUID())) {
				int amountOfDrops = 1;
				if (NPCDeath.isDoubleDrops()) {
					amountOfDrops++;
				}
				Server.getDropManager().create(player, npc, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, 12821);
			//	Achievements.increase(player, AchievementType.SLAY_CORP, 1);
				int bossPoints = BossPoints.getPointsOnDeath(npc);
				BossPoints.addPoints(player, bossPoints, false);
				player.getNpcDeathTracker().add(npc.getDefinition().getName(), npc.getDefinition().getCombatLevel(), bossPoints);

				PetHandler.rollOnNpcDeath(player, npc);
			}
		});
		damageCount.clear();
	}
}
