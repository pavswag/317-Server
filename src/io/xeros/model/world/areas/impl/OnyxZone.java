package io.xeros.model.world.areas.impl;

import io.xeros.Server;
import io.xeros.content.PlayerEmotes;
import io.xeros.content.skills.Skill;
import io.xeros.model.collisionmap.ObjectDef;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.model.world.areas.SkillEvent;
import io.xeros.model.world.areas.Zone;

import java.util.Objects;

import static io.xeros.content.skills.Skill.HUNTER;
import static io.xeros.content.skills.Skill.RUNECRAFTING;

public class OnyxZone extends Zone {

    public OnyxZone() {
        super(Boundary.ONYX_ZONE_NEW);
    }

    @Override
    public boolean handledClickObject(Player player, ObjectDef def) {
        if (Objects.isNull(def))
            return false;
        if (!Boundary.isIn(player, getBoundary()))
            return false;
        var objectName = def.name.toLowerCase();

        for (var skill : Skill.values())  {
            var skillName = skill.name().toLowerCase();
            if (objectName.contains(skillName)) {
                if (!player.isBusy()) {
                    if (objectName.contains("hunting"))
                        skill = HUNTER;
                    if (objectName.contains("runecrafting"))
                        skill = RUNECRAFTING;
                    var skillCapeData = PlayerEmotes.SKILLCAPE_ANIMATION_DATA.values()[skill.getId()];
                    Server.getEventHandler().submit(new SkillEvent(player, skill, skillCapeData));
                    return true;
                }
            }
        }
        return false;
    }
}
