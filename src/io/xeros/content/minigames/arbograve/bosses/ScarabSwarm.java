package io.xeros.content.minigames.arbograve.bosses;

import com.google.common.collect.Lists;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.npc.NPCAutoAttack;
import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.content.combat.npc.NPCCombatAttack;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.arbograve.ArbograveBoss;
import io.xeros.model.Animation;
import io.xeros.model.CombatType;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;

import java.util.function.Function;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 08/02/2024
 */
public class ScarabSwarm extends ArbograveBoss {
    public ScarabSwarm(InstancedArea instancedArea) {
        super(1782, new Position(Misc.random(1709, 1717), Misc.random(4264, 4273), instancedArea.getHeight()), instancedArea);

        getBehaviour().setAggressive(true);
        getCombatDefinition().setAggressive(true);
    }

    @Override
    public void process() {
        setAttacks();
        super.process();
    }

    private void setAttacks() {
        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(1946))
                        .setCombatType(CombatType.RANGE)
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setMultiAttack(true)
                        .setDistanceRequiredForAttack(1)
                        .setPoisonDamage(6)
                        .setMaxHit(0)
                        .setAccuracyBonus(npcCombatAttack -> 10.0)
                        .setOnAttack(npcCombatAttack -> {

                            asNPC().getInstance().getNpcs().forEach(npc -> {
                                if (npc.getNpcId() == 1127) {
                                    npc.appendHeal(3, Hitmark.HEAL_PURPLE);
                                }
                            });

                        })
                        .setHitDelay(4)
                        .setAttackDelay(4)
                        .createNPCAutoAttack()

        ));
    }
}
