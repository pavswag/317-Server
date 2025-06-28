package io.xeros.content.minigames.arbograve.bosses;

import com.google.common.collect.Lists;
import io.xeros.content.combat.npc.NPCAutoAttack;
import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.content.combat.npc.NPCCombatAttack;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.arbograve.ArbograveBoss;
import io.xeros.model.Animation;
import io.xeros.model.CombatType;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 04/02/2024
 */
public class Scarab extends ArbograveBoss {
    public Scarab(InstancedArea instancedArea) {
        super(1127, new Position(1712, 4264, instancedArea.getHeight()), instancedArea);
        facePosition(1713, 4273);
        setAttacks();
    }

    @Override
    public void process() {
        setAttacks(); postDefend();
        super.process();
    }

    private long delay = 0;

    private void postDefend() {
        if (Misc.random(0,10) == 0 && delay < System.currentTimeMillis()) {
            for (int i = 0; i < 5 + (2 + asNPC().getInstance().getPlayers().size()); i++) {
                new ScarabSwarm(this.getInstance());
            }
            delay = (System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(30));
        }
    }

    private void setAttacks() {
        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(5457))
                        .setCombatType(CombatType.MELEE)
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setMultiAttack(true)
                        .setDistanceRequiredForAttack(3)
                        .setMaxHit(6)
                        .setAccuracyBonus(npcCombatAttack -> 10.0)
                        .setPrayerProtectionPercentage(new Function<NPCCombatAttack, Double>() {
                            @Override
                            public Double apply(NPCCombatAttack npcCombatAttack) {
                                return 0.75;
                            }
                        })
                        .setPoisonDamage(4)
                        .setAttackDelay(4)
                        .createNPCAutoAttack()

        ));
    }
}
