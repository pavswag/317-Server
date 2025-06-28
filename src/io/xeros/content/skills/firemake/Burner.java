package io.xeros.content.skills.firemake;

import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.skills.Skill;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

public class Burner extends Event<Player> {

    private LogData log = null;

    public Burner(Player player, LogData log) {
        super("skilling", player, (Boundary.isIn(player, Boundary.DONATOR_ZONE) || Boundary.isIn(player, Boundary.DONATOR_ZONE_NEW) ? 1 : 3));
        this.log = log;
    }

    @Override
    public void execute() {
        if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
            stop();
            return;
        }

        if (log == null) {
            stop();
            return;
        }

//        if (Misc.random(300) == 0 && attachment.getInterfaceEvent().isExecutable()) {
//            attachment.getInterfaceEvent().execute();
//            stop();
//            return;
//        }

        double osrsExperience = 0;

        if (!attachment.getItems().playerHasItem(log.getlogId())) {
            attachment.sendMessage("You do not have anymore of this log.");
            stop();
            return;
        }

        attachment.getItems().deleteItem(log.getlogId(), 1);
        Achievements.increase(attachment, AchievementType.FIRE, 1);
        osrsExperience = log.getExperience() + log.getExperience() / 10;

        attachment.getPA().addSkillXPMultiplied((int) osrsExperience * 2, Skill.FIREMAKING.getId(), true);

    }
}
