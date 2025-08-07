package io.xeros.content.bots;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.xeros.Configuration;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCSpawning;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.util.Misc;

import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Logger;

/**
 * Manages Freakazoid hype bots that dance, talk and broadcast messages.
 */
public class FreakazoidBot {

    public static final int NPC_ID = 12345;
    private static final Logger LOGGER = Logger.getLogger(FreakazoidBot.class.getName());
    private static final Map<Integer, Bot> BOTS = new HashMap<>();
    private static final String CONFIG_FILE = "freakazoid-bots.json";

    public static void init() {
        List<BotDefinition> defs = loadDefinitions();
        for (BotDefinition def : defs) {
            spawn(def);
        }
        if (!BOTS.isEmpty()) {
            startGlobalLoop();
        }
    }

    private static List<BotDefinition> loadDefinitions() {
        try {
            if (Files.exists(Paths.get(CONFIG_FILE))) {
                try (Reader reader = new FileReader(CONFIG_FILE)) {
                    Type type = new TypeToken<List<BotDefinition>>(){}.getType();
                    List<BotDefinition> list = new Gson().fromJson(reader, type);
                    if (list != null) {
                        return list;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Failed loading Freakazoid definitions: " + e.getMessage());
        }
        BotDefinition def = new BotDefinition();
        def.npcId = NPC_ID;
        def.name = "freakazoid";
        def.displayName = "@blu@Freakazoid";
        def.position = new Position(3092, 3505, 0);
        def.emotes = new int[]{866, 2109, 2110, 2106};
        def.messages = Arrays.asList(
                "\uD83D\uDD25 Vote daily to help us grow! ::vote \uD83D\uDD25",
                "\uD83D\uDC8E Donating supports server development! ::donate \uD83D\uDC8E",
                "\uD83D\uDCA1 Tip: You can exchange unwanted gear in the Fire of Exchange!",
                "\uD83C\uDFAF Set a goal today: A new drop? A new pet? Let's go!",
                "\uD83D\uDCE3 Join the Discord for giveaways and events! ::discord"
        );
        def.quotes = Arrays.asList(
                "Keep grinding, the drop will come!",
                "Every day is a chance to improve!",
                "You're doing great, adventurer!"
        );
        def.emoteDelay = 50;
        def.emoteVariance = 10;
        def.messageDelay = 50;
        return Collections.singletonList(def);
    }

    private static void spawn(BotDefinition def) {
        NPC npc = NPCSpawning.spawnNpc(def.npcId, def.position.getX(), def.position.getY(), def.position.getHeight(), 0, 0);
        if (npc == null) {
            return;
        }
        npc.revokeWalkingPrivilege = true;
        npc.isGodmode = true;
        npc.setAttackable(false);
        npc.setInvisible(false);
        npc.setCustomName(def.displayName);
        Bot bot = new Bot(npc, def);
        BOTS.put(npc.getIndex(), bot);
    }

    private static void startGlobalLoop() {
        CycleEventHandler.getSingleton().addEvent(null, new CycleEvent() {
            int tick = 0;
            @Override
            public void execute(CycleEventContainer container) {
                for (Player p : PlayerHandler.players) {
                    if (p != null) {
                        p.setFreakazoidAura(false);
                    }
                }
                for (Bot bot : BOTS.values()) {
                    bot.process(tick);
                }
                tick++;
            }
        }, 1);
    }

    public static void talk(Player player, NPC npc) {
        Bot bot = BOTS.get(npc.getIndex());
        if (bot == null) {
            return;
        }
        String quote = bot.def.quotes.get(Misc.trueRand(bot.def.quotes.size()));
        new io.xeros.content.dialogue.DialogueBuilder(player)
                .setNpcId(npc.getNpcId())
                .npc(quote)
                .send();
        LOGGER.info(player.getLoginName() + " interacted with Freakazoid bot " + bot.def.name);
    }

    public static Collection<Bot> getBots() {
        return BOTS.values();
    }

    public static class Bot {
        private final NPC npc;
        private final BotDefinition def;
        private int nextMessageTick;
        private int nextEmoteTick;

        public Bot(NPC npc, BotDefinition def) {
            this.npc = npc;
            this.def = def;
            this.nextEmoteTick = def.emoteDelay + Misc.trueRand(def.emoteVariance + 1);
            this.nextMessageTick = def.messageDelay;
        }

        private void process(int tick) {
            if (npc.isDead()) {
                return;
            }
            if (tick >= nextEmoteTick) {
                int emote = def.emotes[Misc.trueRand(def.emotes.length)];
                npc.startAnimation(emote);
                nextEmoteTick = tick + def.emoteDelay + Misc.trueRand(def.emoteVariance + 1);
            }
            if (tick >= nextMessageTick) {
                String message = def.messages.get(Misc.trueRand(def.messages.size()));
                PlayerHandler.executeGlobalMessage("@or1@[Freakazoid]@bla@: " + message);
                LOGGER.info("Freakazoid bot " + def.name + " broadcasted: " + message);
                nextMessageTick = tick + def.messageDelay;
            }
            if (Configuration.FREAKAZOID_AURA_ENABLED) {
                for (Player p : PlayerHandler.players) {
                    if (p != null && npc.distanceToPoint(p.getX(), p.getY()) <= 3) {
                        p.setFreakazoidAura(true);
                    }
                }
            }
        }

        public String getStatus() {
            return def.displayName + " at (" + npc.getX() + "," + npc.getY() + "," + npc.getHeight() + ")";
        }
    }

    private static class BotDefinition {
        int npcId;
        String name;
        String displayName;
        Position position;
        int[] emotes;
        List<String> messages = new ArrayList<>();
        List<String> quotes = new ArrayList<>();
        int emoteDelay;
        int emoteVariance;
        int messageDelay;
    }
}
