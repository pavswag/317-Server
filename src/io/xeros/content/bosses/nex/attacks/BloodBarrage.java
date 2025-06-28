package io.xeros.content.bosses.nex.attacks;

import io.xeros.content.combat.Hitmark;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.util.List;

public class BloodBarrage {


    public BloodBarrage(NPC npc, Player player, List<Player> players) {
        BloodBarrage(npc, player, players);
    }

    void BloodBarrage(NPC npc, Player player, List<Player> players) {

        for (Player possibleTargets: players) {
            if(player.getPosition().getAbsDistance(possibleTargets.getPosition()) <= 3) {
                player.getPA().createPlayersProjectile(npc.getX(), npc.getY(), player.getX(), player.getY(), 16, 10, 374, 43, 43, -1, 65, 3);
                possibleTargets.gfx100(377);
                int dam;
                if(possibleTargets.protectingMagic())
                    dam = Misc.random(40);
                else
                    dam = Misc.random(60);
                if (npc.getHealth().getCurrentHealth() >= 99) {
                    npc.appendHeal(dam, Hitmark.HEAL_PURPLE);
                }
                if (possibleTargets.usingInfAgro) {
                    possibleTargets.attackEntity(npc);
                }
                possibleTargets.appendDamage(dam, (dam > 0 ? Hitmark.HIT : Hitmark.MISS));
            }

        }
    }
}
