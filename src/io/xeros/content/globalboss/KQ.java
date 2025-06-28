package io.xeros.content.globalboss;

import io.xeros.Server;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.battlepass.Pass;
import io.xeros.content.bosspoints.BossPoints;
import io.xeros.content.combat.death.NPCDeath;
import io.xeros.content.taskmaster.TaskMasterKills;
import io.xeros.content.taskmaster.Tasks;
import io.xeros.model.definitions.NpcDef;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.pets.PetHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Location3D;

import java.util.HashMap;
import java.util.List;

import static io.xeros.content.combat.death.NPCDeath.isDoubleDrops;

public class KQ {

    public static HashMap<Player, Integer> damageCount = new HashMap<>();

    public static void handleDeath(NPC npc) {
        HashMap<String, Integer> map = new HashMap<>();
        damageCount.forEach((p, i) -> {
            if (map.containsKey(p.getUUID())) {
                map.put(p.getUUID(), map.get(p.getUUID()) + 1);
            } else {
                map.put(p.getUUID(), 1);
            }
        });

        map.values().removeIf(integer -> integer > 1);

        damageCount.forEach((player, integer) -> {
            if (integer > 100 && map.containsKey(player.getUUID())) {
                int amountOfDrops = 1;
                if (NPCDeath.isDoubleDrops()) {
                    amountOfDrops++;
                }
                Server.getDropManager().create(player, npc, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, npc.getNpcId());

                int bossPoints = BossPoints.getPointsOnDeath(npc);
                BossPoints.addPoints(player, bossPoints, false);

                if (NpcDef.forId(npc.getNpcId()).getCombatLevel() >= 1) {
                    player.getNpcDeathTracker().add(NpcDef.forId(npc.getNpcId()).getName(), NpcDef.forId(npc.getNpcId()).getCombatLevel(), bossPoints);
                }
                for (TaskMasterKills killz : player.getTaskMaster().taskMasterKillsList) {
                    for (Tasks value : Tasks.values()) {
                        if (killz.getDesc().equalsIgnoreCase(value.desc) && killz.getAmountKilled() != killz.getAmountToKill() && killz.getDesc().contains(npc.getName())) {
                            killz.incrementAmountKilled(1);
                            player.getTaskMaster().trackActivity(player, killz);
                            break;
                        }
                    }
                }
                PetHandler.rollOnNpcDeath(player, npc);
                player.getBossTimers().death(npc);
                Pass.addExperience(player, 1);

                for (TaskMasterKills killz : player.getTaskMaster().taskMasterKillsList) {
                    for (Tasks value : Tasks.values()) {
                        if (killz.getDesc().equalsIgnoreCase(value.desc) && killz.getAmountKilled() != killz.getAmountToKill() && killz.getDesc().contains(npc.getName())) {
                            killz.incrementAmountKilled(1);
                            player.getTaskMaster().trackActivity(player, killz);
                            break;
                        }
                    }
                }
            }
        });
        reset();
    }

    private static void reset() {
        if (!damageCount.isEmpty()) {
            damageCount.clear();
        }
    }
}
