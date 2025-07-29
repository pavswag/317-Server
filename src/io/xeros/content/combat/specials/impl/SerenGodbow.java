package io.xeros.content.combat.specials.impl;

import io.xeros.content.bosses.Hunllef;
import io.xeros.content.combat.Damage;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.range.RangeData;
import io.xeros.content.combat.specials.Special;
import io.xeros.content.questing.hftd.DagannothMother;
import io.xeros.content.skills.Skill;
import io.xeros.model.CombatType;
import io.xeros.model.Npcs;
import io.xeros.model.collisionmap.PathChecker;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerAssistant;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.util.Misc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SerenGodbow extends Special {

    public SerenGodbow() {
        super(5.0, 1.5, 1.0, new int[] { 30152,33058,30340,30228 });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.usingBow = true;
        player.startAnimation(9170);
        damage.setAmount(Misc.random(25,50));

        if (target.isNPC()) {
            switch (target.asNPC().getNpcId()) {
                case Npcs.CORPOREAL_BEAST:
                case 5630:
                case 6474:
                case 2948:
                case 7527:
                case 7528:
                case 7585:
                case 7544:
                case 1226:
                case Npcs.DEMONIC_GORILLA_2:
                case Hunllef.RANGED_PROTECT:
                case DagannothMother.MELEE_PHASE:
                case 6374:
                case DagannothMother.EARTH_PHASE:
                case 6378:
                case DagannothMother.FIRE_PHASE:
                case 6376:
                case DagannothMother.WATER_PHASE:
                case 6375:
                case 6373:
                case 8355:
                case 8356:
                case DagannothMother.AIR_PHASE:
                    damage.setAmount(0);
            }
        }


        if (target.isPlayer()) {
            RangeData.fireProjectilePlayer(player, (Player) target, 50, 70, 682, 43, 31, 37, 10);
        } else if (target.isNPC()) {
            RangeData.fireProjectileNpc(player, (NPC) target, 50, 70, 682, 43, 31, 37, 10);
        }
    }


    @Override
    public void hit(Player player, Entity target, Damage damage) {

    }
}