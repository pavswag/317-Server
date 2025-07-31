package io.xeros.content.items.aoeweapons;

import io.xeros.content.combat.Damage;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.range.RangeData;
import io.xeros.content.skills.Skill;
import io.xeros.model.CombatType;
import io.xeros.model.Graphic;
import io.xeros.model.collisionmap.doors.Location;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Random;

public class AoeManager {

    public static boolean canAOE(Player player) {

        return Boundary.isIn(player, Boundary.AOEInstance) &&
                Arrays.stream(AoeWeapons.values()).anyMatch(i -> i.ID == player.playerEquipment[Player.playerWeapon]);
    }

    public static void castAOE(Player player, Entity victim) {
//        if (!canAOE(player)) {
//            player.sendMessage("You cannot do this here.");
//            player.attacking.reset();
//            return;
//        }

        AoeWeapons aoeData = AOESystem.getSingleton().getAOEData(player.playerEquipment[Player.playerWeapon]);
        if (aoeData == null) {
            return;
        }

        int dmg = Misc.random(aoeData.DMG);
        int range = aoeData.Size;
        int delay = aoeData.Delay;
        int anim = aoeData.anim;
        int gfx = aoeData.gfx;
        String style = aoeData.style;

        player.startAnimation(anim);

        if (player.isPlayer() && victim.isNPC()) {
            victim.startGraphic(new Graphic(gfx, 0, Graphic.GraphicHeight.MIDDLE));
            for (NPC next : NPCHandler.npcs) {
                if (next == null) {
                    continue;
                }
                if (next.getInstance() != player.getInstance() || next.getHeight() != player.getHeight()) {
                    continue;
                }
                if (!player.getPosition().withinDistance(next.getPosition(), range) || next.getHealth().getCurrentHealth() <= 0) {
                    continue;
                }
                next.startGraphic(new Graphic(gfx, 0, Graphic.GraphicHeight.MIDDLE));
                int calc = Misc.random(dmg);
                next.appendDamage(player, calc, (calc > 0 ? Hitmark.HIT : Hitmark.MISS));
                next.attackEntity(player);
            }
            player.attackTimer = delay;
        }
    }
}
