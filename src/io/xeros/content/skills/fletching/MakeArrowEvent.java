package io.xeros.content.skills.fletching;

import io.xeros.content.achievement.AchievementType;
import io.xeros.content.achievement.Achievements;
import io.xeros.content.skills.Skill;
import io.xeros.content.skills.slayer.SlayerUnlock;
import io.xeros.model.cycleevent.Event;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.util.Optional;

import static io.xeros.content.skills.fletching.Fletching.getFletchEquipmentCount;

public class MakeArrowEvent extends Event<Player> {

    private FletchableArrow a = null;

    public MakeArrowEvent(Player att, FletchableArrow a) {
        super("skilling", att, 2);
        this.a = a;
    }

    @Override
    public void execute() {
        if (attachment == null || attachment.isDisconnected() || attachment.getSession() == null) {
            stop();
            return;
        }
        if (a == null) {
            stop();
            return;
        }
//        if (Misc.random(300) == 0 && attachment.getInterfaceEvent().isExecutable()) {
//            attachment.getInterfaceEvent().execute();
//            super.stop();
//            return;
//        }
        if (!attachment.getItems().playerHasItem(53, 15)) {
            attachment.sendMessage("You need at least 15 headless arrows to do this.");
            attachment.getPA().removeAllWindows();
            stop();
            return;
        }
        if (!attachment.getItems().playerHasItem(a.getId(), 15)) {
            attachment.sendMessage("You need at least 15 arrowheads to do this.");
            attachment.getPA().removeAllWindows();
            stop();
            return;
        }
        if (attachment.getItems().freeSlots() < 1) {
            attachment.sendMessage("You need at least 1 free slot to do this.");
            attachment.getPA().removeAllWindows();
            stop();
            return;
        }
        Achievements.increase(attachment, AchievementType.FLETCH, 1);
        attachment.getItems().deleteItem2(53, 15);
        attachment.getItems().deleteItem2(a.getId(), 15);
        attachment.getItems().addItem(a.getReward(), 15);
        attachment.getPA().addSkillXPMultiplied((int) a.getExperience() * getFletchEquipmentCount(attachment), Skill.FLETCHING.getId(), true);
    }
}
