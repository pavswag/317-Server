package io.xeros;

import io.xeros.content.QuestTab;
import io.xeros.content.battlepass.Pass;
import io.xeros.content.bosses.DonorBoss;
import io.xeros.content.bosses.DonorBoss2;
import io.xeros.content.bosses.DonorBoss3;
import io.xeros.content.bosses.gobbler.Gobbler;
import io.xeros.content.bosses.wintertodt.Wintertodt;
import io.xeros.content.events.monsterhunt.ShootingStars;
import io.xeros.content.instances.InstanceHeight;
import io.xeros.content.minigames.blastfurnance.BlastFurnace;
import io.xeros.content.minigames.pk_arena.Highpkarena;
import io.xeros.content.minigames.pk_arena.Lowpkarena;
import io.xeros.content.npchandling.ForcedChat;
import io.xeros.content.wilderness.ActiveVolcano;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.net.ChannelHandler;
import io.xeros.net.login.RS2LoginProtocol;
import io.xeros.sql.dailytracker.DailyDataTracker;
import io.xeros.util.Misc;
import io.xeros.util.discord.DiscordIntegration;
import io.xeros.util.task.TaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

public class GameThread extends Thread {

    public static final String THREAD_NAME = "GameThread";
    public static final int PRIORITY = 9;
    private static final Logger logger = LoggerFactory.getLogger(GameThread.class);
    private final List<Consumer<GameThread>> tickables = new ArrayList<>();
    private final Runnable startup;
    private long totalCycleTime = 0;

    public GameThread(Runnable startup) {
        setName(THREAD_NAME);
        setPriority(PRIORITY);
        this.startup = startup;
        setTickables();
    }

    private void setTickables() {
        tickables.add(i -> Server.itemHandler.process());
        tickables.add(i -> Server.npcHandler.process());
        tickables.add(i -> Server.playerHandler.process());
        tickables.add(i -> Server.shopHandler.process());
        tickables.add(i -> Highpkarena.process());
        tickables.add(i -> Lowpkarena.process());
        tickables.add(i -> DiscordIntegration.givePoints());
        tickables.add(i -> ActiveVolcano.Tick());
        tickables.add(i -> ShootingStars.Tick());
        tickables.add(i -> Server.getGlobalObjects().pulse());
        tickables.add(i -> CycleEventHandler.getSingleton().process());
        tickables.add(i -> Server.getEventHandler().process());
        tickables.add(i -> Wintertodt.pulse());
        tickables.add(i -> DonorBoss.tick());
        tickables.add(i -> DonorBoss2.tick());
        tickables.add(i -> DonorBoss3.tick());
        tickables.add(i -> Gobbler.ticker());
        tickables.add(i -> Pass.tick());
        tickables.add(i -> BlastFurnace.process());
        tickables.add(i -> TaskManager.sequence());
        tickables.add(i -> QuestTab.Tick());
        tickables.add(i -> Server.tickCount++);
        tickables.add(i -> ForcedChat.StartForcedChat());
        tickables.add(i -> DailyDataTracker.newDay());
    }

    private void tick() {
        for (Consumer<GameThread> tickable : tickables) {
            try {
                tickable.accept(this);
            } catch (Exception e) {
                logger.error("Error caught in GameThread, should be caught up the chain and handled.", e);
                e.printStackTrace(System.err);
            }
        }

        // Report
        if (Server.getTickCount() % 50 == 0) {
            long totalMemory = Runtime.getRuntime().totalMemory();
            long usedMemory = totalMemory - Runtime.getRuntime().freeMemory();
            var joiner = new StringJoiner(", ");
                joiner.add("runtime=" + Misc.cyclesToTime(Server.getTickCount()));
                joiner.add("connections=" + ChannelHandler.getActiveConnections());
                joiner.add("players=" + PlayerHandler.getPlayers().size());
                joiner.add("uniques=" + PlayerHandler.getUniquePlayerCount());
                joiner.add("npcs=" + (int) NPCHandler.nonNullStream().count());
                joiner.add("reserved-heights=" + InstanceHeight.getReservedCount());
                joiner.add("average-cycle-time=" + (totalCycleTime / Server.getTickCount()) + "ms");
                joiner.add("handshakes-per-tick=" + (RS2LoginProtocol.getHandshakeRequests() / Server.getTickCount()));
                joiner.add("memory=" + Misc.formatMemory(usedMemory) + "/" + Misc.formatMemory(totalMemory));

            logger.info("Status [{}]", joiner);
        }
    }

    @Override
    public void run() {
        startup.run();
        while (!Thread.interrupted()) {
            long time = System.currentTimeMillis();
            try {
                tick();
            } catch (Exception e) {
                logger.error("An error occurred while running the game thread tickables.", e);
                e.printStackTrace(System.err);
            }
            long pastTime = System.currentTimeMillis() - time;
            totalCycleTime += pastTime;
            if (pastTime < 600) {
                try {
                    Thread.sleep(600 - pastTime);
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
            } else {
                logger.error("Game thread took {}ms to process!", Misc.insertCommas(pastTime + ""));
            }
        }
    }
}
