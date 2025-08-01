package io.xeros.content.bosses;

import com.google.common.collect.Lists;
import io.xeros.content.bosses.nex.attacks.IceBarrage;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.npc.NPCAutoAttack;
import io.xeros.content.combat.npc.NPCAutoAttackBuilder;
import io.xeros.content.commands.owner.Objectanim;
import io.xeros.content.skills.Skill;
import io.xeros.model.Animation;
import io.xeros.model.CombatType;
import io.xeros.model.ProjectileBaseBuilder;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

public class Tempoross {

    public static int temporossID = 10575;
    public static int stanceID = 8895;
    public static int attackID = 8903;
    public static int deathID = 8906;
    private static NPC tempoross;
    private static NPC SpiritPoolNorth;
    private static NPC SpiritPoolSouth;

    public static boolean active;

    public static void dealDamage(Player player) {
        CycleEventHandler.getSingleton().addEvent(player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (player.getItems().getInventoryCount(25564) >= 1) {
                    if (Misc.random(2) == 1) {
                        player.getItems().deleteItem(25564, 1);
                        int dmg = Misc.random(0, 15);
                        tempoross.appendDamage(dmg, (dmg > 0 ? Hitmark.HIT : Hitmark.MISS));
                        player.getPA().addSkillXP(50, Skill.COOKING.getId(), true);
                    } else {
                        player.getItems().deleteItem(25564, 1);
                        player.sendMessage("You fail to cook a harpoon fish, and it vanishes!");
                        player.getPA().addSkillXP(10, Skill.COOKING.getId(), true);
                    }
                } else if (player.getItems().getInventoryCount(25564) <= 0) {
                    container.stop();
                    player.sendMessage("You have run out of harpoon fish!");
                }
            }
        }, 3);
    }

    public static void init() {
        active = true;
        SpiritPoolNorth = NPCSpawning.spawnNpc(10571, 3046, 2971,0, 0, 0);
        SpiritPoolSouth = NPCSpawning.spawnNpc(10571, 3046, 2981,0, 0, 0);
        SpiritPoolNorth.getBehaviour().setRespawn(false);
        SpiritPoolSouth.getBehaviour().setRespawn(false);
        SpiritPoolNorth.getBehaviour().setAggressive(false);
        SpiritPoolSouth.getBehaviour().setAggressive(false);

        tempoross = NPCSpawning.spawnNpc(temporossID, 3043, 2973, 0, 0, 0);
        tempoross.getHealth().setMaximumHealth(5000);
        tempoross.getHealth().reset();
        tempoross.getCombatDefinition().setAggressive(true);
        tempoross.getBehaviour().setAggressive(true);
        tempoross.getBehaviour().setRespawn(false);
        tempoross.setNpcAutoAttacks(Lists.newArrayList(new NPCAutoAttackBuilder()
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(2)
                        .setHitDelay(4)
                        .setMultiAttack(false)
                        .setAnimation(new Animation(8903))
                        .setAttackDelay(6)
                        .setOnHit(attack -> {

                        })
                        .createNPCAutoAttack(),

                new NPCAutoAttackBuilder()
                        .setSelectAutoAttack(attack -> Misc.trueRand(1) == 0)
                        .setCombatType(CombatType.MAGE)
                        .setDistanceRequiredForAttack(1)
                        .setHitDelay(2)
                        .setAnimation(new Animation(8903))
                        .setAttackDelay(6)
                        .setOnHit(attack -> {

                        })
                        .createNPCAutoAttack()

        ));
    }

    public static void handleDeath() {

    }

    public static void handleFishing(Player player) {

    }


}
