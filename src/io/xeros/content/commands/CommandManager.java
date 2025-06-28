package io.xeros.content.commands;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.content.battlepass.Pass;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.tournaments.TourneyManager;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.mode.ExpMode;
import io.xeros.model.entity.player.mode.group.ExpModeType;
import io.xeros.util.ClassGraphHandler;
import io.xeros.util.Misc;
import io.xeros.util.discord.Discord;
import io.xeros.util.discord.DiscordIntegration;
import net.dv8tion.jda.api.EmbedBuilder;
import org.apache.logging.log4j.util.TriConsumer;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static io.xeros.util.discord.Discord.DISCORD_THUMBNAIL;
import static io.xeros.util.discord.Discord.getJDA;
import static io.xeros.util.discord.DiscordIntegration.generateCode;

public class CommandManager {

    private static final java.util.logging.Logger log = java.util.logging.Logger.getLogger(CommandManager.class.getName());

    public static final Map<String, Command> COMMAND_MAP = new TreeMap<>();

    public static final List<CommandPackage> COMMAND_PACKAGES = Lists.newArrayList(
            new CommandPackage("admin", Right.ADMINISTRATOR),
            new CommandPackage("owner", Right.ADMINISTRATOR),
            new CommandPackage("owner", Right.ADMINISTRATOR),
            new CommandPackage("moderator", Right.MODERATOR),
            new CommandPackage("helper", Right.MODERATOR),
            new CommandPackage("donator", Right.DONATOR),
            new CommandPackage("all", Right.PLAYER)
    );

    private static boolean hasRightsRequirement(Player player, Right rightsRequired) {
        return rightsRequired.isOrInherits(player);
    }

    public static void execute(Player c, String playerCommand) {
        for (CommandPackage commandPackage : COMMAND_PACKAGES) {
            if (commandPackage.getRight().isOrInherits(c)) {
                if (executeCommand(c, playerCommand)) {
                    return;
                }
            }
        }
    }

    public static CommandPackage getPackage(Command command) {
        for (CommandPackage commandPackage : COMMAND_PACKAGES) {
            if (command.getClass().getPackageName().contains(commandPackage.getPackagePath())) {
                return commandPackage;
            }
        }
        return null;
    }

    private static String getPackageName(String packagePath) {
        String[] split = packagePath.split("\\.");
        return split[split.length - 2];
    }

    public static List<Command> getCommands(Player player, String... skips) {
        return COMMAND_MAP.entrySet().stream().filter(entry -> {
            for (CommandPackage commandPackage : COMMAND_PACKAGES) {
                if (getPackageName(entry.getKey().toLowerCase()).contains(commandPackage.getPackagePath())) {
                    if (Arrays.stream(skips).anyMatch(skip -> commandPackage.getPackagePath().toLowerCase().contains(skip))) {
                        continue;
                    }
                    if (hasRightsRequirement(player, commandPackage.getRight())) {
                        return true;
                    }
                }
            }
            return false;
        }).map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public static boolean executeCommand(Player player, String playerCommand) {
        if (playerCommand == null) {
            return true;
        }
        String commandName = Misc.findCommand(playerCommand);
        String commandInput = Misc.findInput(playerCommand);

        if (player.isInTradingPost()) {
            player.setSidebarInterface(3, 3213);
            player.inTradingPost = false;
            player.clickDelay = System.currentTimeMillis();
        }

        boolean outlast = TourneyManager.getSingleton().isInArenaBounds(player) || TourneyManager.getSingleton().isInLobbyBounds(player);

        if (outlast && player.getRights().isNot(Right.ADMINISTRATOR)) {
            player.sendMessage("You cannot use commands when in the tournament arena");
            return true;
        }

        if (Right.ADMINISTRATOR.isOrInherits(player)) {
            dev("test", (p, s1, s2) -> {
                final String code = generateCode(5);
                EmbedBuilder eb = new EmbedBuilder();
                eb.setTitle("**Code To Enter: " + code + "**", null);
                eb.setAuthor("Turmoil Discord Link Token");
                eb.setColor(Color.MAGENTA);
                eb.setThumbnail(DISCORD_THUMBNAIL);
                Objects.requireNonNull(Discord.getUser(player))
                        .openPrivateChannel()
                        .queue((channel) ->
                                channel.sendMessage(eb.build()).queue());
            });
        }

        if (Right.STAFF_MANAGER.isManagement()) {
            dev("recmd", (p, c, s) -> {
                COMMAND_MAP.clear();
                CommandManager.initializeCommands();
            });
        }

        if (player.getRights().isOrInherits(Right.ADMINISTRATOR) && commandName.equalsIgnoreCase("changexp")) {
            String[] args = commandInput.split("-");
            try {
                String playerName = args[0];

                ExpMode expMode = null;
                if (args[1].equalsIgnoreCase("1")) {
                    expMode = new ExpMode(ExpModeType.OneTimes);
                } else if (args[1].equalsIgnoreCase("5")) {
                    expMode = new ExpMode(ExpModeType.FiveTimes);
                } else if (args[1].equalsIgnoreCase("10")) {
                    expMode = new ExpMode(ExpModeType.TenTimes);
                } else if (args[1].equalsIgnoreCase("25")) {
                    expMode = new ExpMode(ExpModeType.TwentyFiveTimes);
                } else if (args[1].equalsIgnoreCase("100")) {
                    expMode = new ExpMode(ExpModeType.TwoFiftyTimes);
                }

                Optional<Player> optionalPlayer = PlayerHandler.getOptionalPlayerByDisplayName(playerName);

                if (optionalPlayer.isPresent()) {
                    Player c2 = optionalPlayer.get();

                    c2.setExpMode(expMode);
                    if (expMode != null) {
                        c2.sendErrorMessage("Your expMode has been changed to " + expMode.getType().getFormattedName());
                        player.sendMessage("You have changed " + c2.getDisplayName() + "'s expMode to " + expMode.getType().getFormattedName());
                    }
                }
            } catch (Exception e) {
                player.sendErrorMessage("Format invalid ::changexp-name-(1,5,10,25)");
            }
            return true;
        }

        if (commandName.equalsIgnoreCase("titles")) {
            player.getTitles().display();
            return true;
        }

        if (commandName.equalsIgnoreCase("donocred")) {
            player.start(new DialogueBuilder(player).option("Would you like to convert your donor credits to points?", new DialogueOption("Yes", p -> {
                int amt = p.getItems().getInventoryCount(33251);
                if (amt <= 0) {
                    p.getPA().closeAllWindows();
                    p.sendErrorMessage("You don't have any donor credits!");
                    return;
                }
                p.getPA().closeAllWindows();
                p.getPA().sendEnterAmount("How many would you like to convert?", (plr, amount) -> {
                    int total_am = p.getItems().getInventoryCount(33251);

                    if (amount > total_am) {
                        amount = total_am;
                    }

                    plr.donatorPoints += amount;
                    plr.getItems().deleteItem2(33251, amount);
                });

            }), new DialogueOption("No thank you.", p -> p.getPA().closeAllWindows())));
            return true;
        }

        if (commandName.equalsIgnoreCase("bp")) {
            Pass.openInterface(player);
            player.getQuesting().handleHelpTabActionButton(669);
            return true;
        }

        if (commandName.equalsIgnoreCase("perk")) {
            player.getPA().startTeleport(3363, 9640, 0, "modern", false);
            return true;
        }

        try {
            final Command CMD = COMMAND_MAP.get(commandName);
            if (CMD != null && CMD.hasPrivilege(player)) {
                CMD.execute(player, commandName, commandInput);
                return true;
            }
            return false;
        } catch (Exception e) {
            player.sendMessage("Error while executing the following command: " + playerCommand);
            e.printStackTrace(System.err);
            return true;
        }
    }

    private final static String[] packages = {
            "owner",
            "admin",
            "test",
            "punishment",
            "moderator",
            "helper"
    };

    public static void initializeCommands() {
        if (Server.isDebug() || Server.isTest()) { // Important that this doesn't get removed
            COMMAND_PACKAGES.add(new CommandPackage("test", Right.PLAYER));
        }
        List<Command> tList = new ArrayList<>();
        ClassGraphHandler<Command> entry = new ClassGraphHandler<>();
        entry.submit(Command.class, tList, ClassGraphHandler.LoadType.SUBCLASS_ENTRY).shutdown();
        for (var c : tList) {
            COMMAND_MAP.putIfAbsent(c.getCommand(), c);
            log.fine(String.format("Added command [path=%s] [command=%s]", c.getCommand(), c));
        }
        log.info("Loaded " + COMMAND_MAP.size() + " commands.");
    }


    public static void dev(String cmd, TriConsumer<Player, String, String> tc) {
        COMMAND_MAP.put(cmd, new Command() {
            @Override
            public void execute(Player c, String commandName, String input) {
                tc.accept(c, commandName, input);
            }

            @Override
            public boolean hasPrivilege(Player player) {
                return Right.GAME_DEVELOPER.isOrInherits(player);
            }
        });
    }
}
