package io.xeros.content.minigames.arbograve;

import io.xeros.Server;
import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.battlepass.Pass;
import io.xeros.content.bosses.hespori.Hespori;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.instances.InstancedArea;
import io.xeros.content.minigames.arbograve.bosses.Leech;
import io.xeros.content.minigames.arbograve.instance.ArbograveInstance;
import io.xeros.content.minigames.tob.TobConstants;
import io.xeros.content.prestige.PrestigePerks;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.items.GameItem;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.util.Misc;

import java.util.stream.Collectors;

public class ArbograveBoss extends NPC {

    public ArbograveBoss(int npcId, Position position, InstancedArea instancedArea) {
        super(npcId, position);
        instancedArea.add(this);
        getBehaviour().setRespawn(false);
        getBehaviour().setAggressive(true);
        getCombatDefinition().setAggressive(true);
    }

    public void onDeath() {
        Entity killer = calculateKiller();
        if (getInstance() != null) {
            if (this.asNPC() != null && this.asNPC().getNpcId() == 6477) {
                for (NPC npc : getInstance().getNpcs()) {
                    npc.appendDamage(npc.getHealth().getMaximumHealth(), Hitmark.HIT);
                }
                for (Player player : getInstance().getPlayers()) {
                    player.sendMessage("You have completed Arbograve!");
                    player.getBossTimers().death("Arbograve Swamp");
                    player.getItems().addItemUnderAnyCircumstance(27285, 1);
                    player.foundryPoints += Misc.random(250_000);
                    player.sendMessage("You now have:"+player.foundryPoints+"upgrading points.");
                    Pass.addExperience(player, 2);
                    player.arboCompletions++;
                    int points = Misc.random(500,2000);
                    int rng = Misc.trueRand(1);
                    if (rng == 1) {
                        points /= 2;
                    }

                    player.arboPoints += points;
                    player.sendMessage("You now have a total of " + player.arboPoints + " Arbograve Swamp Points!");
                    int keys = player.getArboContainer().lives;

                    if (keys > 0) {
                        if (PrestigePerks.hasRelic(player, PrestigePerks.TRIPLE_HESPORI_KEYS) && Misc.isLucky(10)) {
                            keys *= 3;
                        }

                        if (Hespori.KRONOS_TIMER > 0 && Misc.random(100) >= 95) {
                            keys *= 2;
                        }

                        player.getItems().addItemUnderAnyCircumstance(2400, keys);
                    } else {
                        player.sendMessage("You had no key's remaining so you get fuck all.");
                    }
                    player.moveTo(new Position(2834,3264,0));
                    Achievements.increase(player, AchievementType.ARBO, 1);
                }
            }
        }

        if (asNPC().getNpcId() != 3233 && asNPC().getNpcId() != 1782) {
            new Leech(new Position(asNPC().getX(), asNPC().getY()),asNPC().getInstance());

            for (Player player : asNPC().getInstance().getPlayers()) {
                Server.itemHandler.createGroundItem(player, new GameItem(6691,Misc.random(2,4)), new Position(asNPC().getX(), asNPC().getY(), player.getHeight()), 0);
            }
        }
    }
}
