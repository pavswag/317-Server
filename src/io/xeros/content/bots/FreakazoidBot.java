package io.xeros.content.bots;

import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;

/**
 * A persistent NPC that dances and broadcasts hype messages to players.
 */
public class FreakazoidBot {

    private static final int NPC_ID = 12345; // Custom dancing robot NPC ID
    private static final Position SPAWN_POS = new Position(3092, 3505); // Home area
    private static final int[] DANCE_EMOTES = {866, 2109, 2110, 2106};

    private static final String[] MESSAGES = {
        "\uD83D\uDD25 Vote daily to help us grow! ::vote \uD83D\uDD25",
        "\uD83D\uDC8E Donating supports server development! ::donate \uD83D\uDC8E",
        "\uD83D\uDCA1 Tip: You can exchange unwanted gear in the Fire of Exchange!",
        "\uD83C\uDFAF Set a goal today: A new drop? A new pet? Let's go!",
        "\uD83D\uDCE3 Join the Discord for giveaways and events! ::discord"
    };

    private static NPC bot;

    public static void spawn() {
        bot = NPCSpawning.spawnNpc(NPC_ID, SPAWN_POS.getX(), SPAWN_POS.getY(), SPAWN_POS.getHeight(), 0, 0);
        if (bot == null) {
            return;
        }
        bot.revokeWalkingPrivilege = true;
        bot.isGodmode = true;
        bot.setAttackable(false);
        bot.setInvisible(false);
        bot.setCustomName("@blu@Freakazoid");
        startBotLoop();
    }

    private static void startBotLoop() {
        CycleEventHandler.getSingleton().addEvent(bot, new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (bot == null || bot.isDead()) {
                    container.stop();
                    return;
                }

                int emote = DANCE_EMOTES[Misc.trueRand(DANCE_EMOTES.length)];
                bot.startAnimation(emote);

                String message = MESSAGES[Misc.trueRand(MESSAGES.length)];
                PlayerHandler.executeGlobalMessage("@or1@[Freakazoid]@bla@: " + message);
            }
        }, 50); // Fires roughly every 30 seconds
    }
}
