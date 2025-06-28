package io.xeros.content.bosses.nex.attacks;

import io.xeros.content.combat.Hitmark;
import io.xeros.model.entity.EntityReference;
import io.xeros.model.entity.player.ClientGameTimer;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class IceBarrage {
    public IceBarrage(Player player, List<Player> players) {
        IceBarrage(player, players);
    }

    void IceBarrage(Player player, List<Player> players) {
        for (Player possibleTargets:
                players) {
            if(player.getPosition().getAbsDistance(possibleTargets.getPosition()) <= 3) {

                int dam;
                if(possibleTargets.protectingMagic())
                    dam = Misc.random(15);
                else
                    dam = Misc.random(33);
                possibleTargets.appendDamage(dam, (dam > 0 ? Hitmark.HIT : Hitmark.MISS));
                if (dam > 0) {
                    possibleTargets.gfx0(369);
                } else {
                    possibleTargets.gfx0(85);
                }
                if (possibleTargets.isFreezable()  && possibleTargets.freezeDelay <= 0 && possibleTargets.freezeTimer <= 0 && dam > 0) {
                    int delay = Misc.random(15,30);
                    possibleTargets.frozenBy = EntityReference.getReference(player);
                    possibleTargets.freezeDelay = delay;
                    possibleTargets.freezeTimer = delay;
                    possibleTargets.resetWalkingQueue();
                    possibleTargets.sendMessage("You have been frozen.");
                    possibleTargets.getPA().sendGameTimer(ClientGameTimer.FREEZE, TimeUnit.MILLISECONDS, 600 * delay);
                }
            }

        }
    }
}
