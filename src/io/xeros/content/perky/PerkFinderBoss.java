package io.xeros.content.perky;

import io.xeros.Server;
import io.xeros.annotate.PostInit;
import io.xeros.content.battlepass.Pass;
import io.xeros.content.combat.death.NPCDeath;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueExpression;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCAction;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.objects.ObjectAction;
import io.xeros.util.Location3D;

import java.util.HashMap;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 11/03/2024
 */
public class PerkFinderBoss {

    private static final int Ancient_Guardian = 12337;
    private static int KC_UNTIL_SPAWN = 5000;

    private static HashMap<Player, Integer> player_kc_tracker = new HashMap<>();

    public static void addPerkFinderKills(Player player) {
        if (player_kc_tracker.containsKey(player)) {
            player_kc_tracker.replace(player, player_kc_tracker.get(player), player_kc_tracker.get(player) + 1);
        } else {
            player_kc_tracker.put(player, 1);
        }
        KC_UNTIL_SPAWN -= 1;
        if (KC_UNTIL_SPAWN <= 0) {
            KC_UNTIL_SPAWN = 5000;
            PlayerHandler.executeGlobalMessage("Dis Boss Just Spawned God Damn!");
            new AncientGuardian(Ancient_Guardian, new Position(3370, 9645, 1));
            new AncientGuardian(Ancient_Guardian, new Position(3358, 9645, 1));
            new AncientGuardian(Ancient_Guardian, new Position(3370, 9634, 1));
            new AncientGuardian(Ancient_Guardian, new Position(3358, 9634, 1));
        }
    }

    private static boolean hasKcRequirement(Player player) {
        return player_kc_tracker.getOrDefault(player, 0) >= 100;
    }

    @PostInit
    public static void handleInteractions() {
        NPCAction.register(5981, 1, (player, npc) -> {
            if (hasKcRequirement(player)) {
                player.getPA().spellTeleport(3364, 9639, 1, false);
            } else {
                player.start(new DialogueBuilder(player).npc(5981, "You have not yet earned access this domain!", "You need to kill " + (100 - player_kc_tracker.getOrDefault(player, 0)) + " avatars!"));
            }
        });
    }

    public static void handleNPCDeath(NPC npc) {
        if (npc.getNpcId() != Ancient_Guardian) {
            return;
        }

        player_kc_tracker.forEach((player, i) -> {
            if (i >= 100) {
                int amountOfDrops = 1;
                if (NPCDeath.isDoubleDrops()) {
                    amountOfDrops++;
                }
                Pass.addExperience(player, 5);
                Server.getDropManager().create(player, npc, new Location3D(player.getX(), player.getY(), player.getHeight()), amountOfDrops, 12337);
                player.getNpcDeathTracker().add(npc.getName(), npc.getDefinition().getCombatLevel(), 1);
            }
            player_kc_tracker.remove(player);
        });

    }

}
