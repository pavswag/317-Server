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

public class TerrorDog extends ArbograveBoss {
    public TerrorDog(Position position, InstancedArea instancedArea) {
        super(6474, new Position(position.getX(), position.getY(), instancedArea.getHeight()), instancedArea);

        asNPC().getCombatDefinition().setAggressive(true);
        asNPC().getBehaviour().setAggressive(true);

        facePosition(1713, 4273);
        setAttacks();
    }

    @Override
    public void process() {
        setAttacks();
        super.process();
    }

    private void setAttacks() {
        setNpcAutoAttacks(Lists.newArrayList(
                new NPCAutoAttackBuilder()
                        .setAnimation(new Animation(5624))
                        .setCombatType(CombatType.MELEE)
                        .setDistanceRequiredForAttack(3)
                        .setSelectPlayersForMultiAttack(NPCAutoAttack.getDefaultSelectPlayersForAttack())
                        .setMultiAttack(true)
                        .setMaxHit(5)
                        .setPrayerProtectionPercentage(new Function<NPCCombatAttack, Double>() {
                            @Override
                            public Double apply(NPCCombatAttack npcCombatAttack) {
                                return 0.75;
                            }
                        })
                        .setAccuracyBonus(npcCombatAttack -> 10.0)
                        .setAttackDelay(4)
                        .createNPCAutoAttack()

        ));
    }
}
