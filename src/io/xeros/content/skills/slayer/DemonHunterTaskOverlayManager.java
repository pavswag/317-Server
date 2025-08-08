package io.xeros.content.skills.slayer;

import io.xeros.content.skills.Skill;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;

/**
 * Displays Demon Hunter task information to players.
 */
public class DemonHunterTaskOverlayManager {

    private static final int EVENT_ID = 9821;

    public static void send(Player player) {
        player.getDemonHunterTask().ifPresent(task -> {
            int xp = DemonHunterXPTable.getXPFor(task.getBoss().getTier(), player.playerLevel[Skill.DEMON_HUNTER.getId()]);
            player.sendMessage("\uD83D\uDC80 Demon Hunter Task:");
            player.sendMessage("Boss: " + task.getBoss().getNpcName());
            player.sendMessage("Kills Left: " + player.getDemonHunterTaskProgress());
            player.sendMessage("XP per kill: " + xp);
            player.sendMessage("Streak: " + player.getDemonTaskStreak() + " | Marks: " + player.getDemonMarks());
            player.getDemonContract().ifPresent(c -> player.sendMessage("\uD83C\uDFAF Bonus: Defeat " + c.getTarget().getNpcName() + " for 2x XP"));
        });
    }

    public static void schedule(Player player) {
        CycleEventHandler.getSingleton().stopEvents(player, EVENT_ID);
        CycleEventHandler.getSingleton().addEvent(EVENT_ID, player, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (player.getDemonHunterTask().isEmpty()) {
                    container.stop();
                    return;
                }
                send(player);
            }
        }, 50);
    }
}
