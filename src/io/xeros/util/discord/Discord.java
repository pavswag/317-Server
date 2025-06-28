package io.xeros.util.discord;


import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.awt.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Discord extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(Discord.class);
    private static final Map<String, TextChannel> channels = new ConcurrentHashMap<>();
    public static JDA jda = null;
    public static String PREFIX = "::";
    public static String OWNER_ROLE = "891929921851645982";
    public static String MANAGER_ROLE = "915240087376240681";
    public static String DEVELOPER_ROLE = "891930130019127316";
    public static String ADMIN_ROLE = "891930584446812200";
    public static String GLOBAL_MOD_ROLE = "892423012551389265";
    public static String SUPPORT_ROLE = "892558604580847627";
    public static final String BOT_TOKEN = "MTA0MDc2MzIwMDUwNjk1NzkwNA.GB6i72.lWCdFD3UxyWW3Oy_eVhMYE0mb0ny8976iW8yME";
    public static final String DISCORD_THUMBNAIL = "https://turmoilrsps.quest/logo.png";
    public static final String SERVER_LOGO = "https://turmoilrsps.quest/logo.png";
    public static final long GUILD_ID = 605271780302651420L;
    public static final long TEST_CHANNEL = 1274582368564281455L;
    public static final long TP_CANCEL_CHANNEL = 1274582368564281455L;
    public static final long TP_LISTING_CHANNEL = 1274582368564281455L;
    public static final long TP_COFFER_CHANNEL = 1256486272403374090L;
    public static final long TP_BUY_CHANNEL = 1274582368564281455L;
    public static final long DONATOR_VAULT_CHANNEL = 1274582368564281455L;
    public static final long TRADE_CHANNEL = 1274582368564281455L;
    public static final long DROP_CHANNEL = 1274582368564281455L;
    public static final long PICKUP_CHANNEL = 1274582368564281455L;
    public static final long DISCORD_SYNC_CHANNEL = 1274582368564281455L;
    public static final long PRIVATE_MESSAGE_CHANNEL = 1274582368564281455L;
    public static final long GAME_MESSAGE_CHANNEL = 1274582368564281455L;
    public static final long ANNOUNCEMENTS_CHANNEL = 1274582368564281455L;

    /**
     * Write to a channel that contains misc. types of information about player activity.
     */
    public static void writeServerSyncMessage(String message, Object... args) {
        sendChannelMessage(TEST_CHANNEL, message, args);//Server-Logs
    }

    public static Guild getGuild() {
        return Discord.jda.getGuildById(GUILD_ID);
    }

    public static User getUser(Player player) {
        return getJDA().getUserById(player.getDiscordUser());
    }


    public static void sendEmbeddedServerLog(long channel, Color color, String title, String description, final String playerName, final String playerIp) {
        Server.getIoExecutorService().submit(() -> {
            try {
                if (getJDA().getTextChannelById(channel) != null) {
                    Objects.requireNonNull(getJDA().getTextChannelById(channel))
                            .sendMessage(buildEmbedMessageOnly(color, title, description, playerName, playerIp)).queue();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        });
    }

    public static void sendEmbeddedDiscordSyncMessage(long channel, Color color, String title, String description) {
        Server.getIoExecutorService().submit(() -> {
            try {
                if (getJDA().getTextChannelById(channel) != null) {
                    Objects.requireNonNull(getJDA().getTextChannelById(channel))
                            .sendMessage(buildEmbed(color, title, description)).queue();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        });
    }

    public static void sendEmbeddedAnnouncement(long channel, Color color, String title) {
        Server.getIoExecutorService().submit(() -> {
            try {
                MessageEmbed embed = buildEmbedAnnouncement(color, title);
                if (embed == null) return;
                if (getJDA().getTextChannelById(channel) != null) {
                    Objects.requireNonNull(getJDA().getTextChannelById(channel)).sendMessage(embed).queue();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        });
    }

    public static MessageEmbed buildEmbedAnnouncement(Color color, final String title) {
        if (Server.isPublic()) {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle(title, null);
            eb.setColor(color);
            eb.addBlankField(false);
            eb.setAuthor("Turmoil Announcement", null, DISCORD_THUMBNAIL);
            eb.setDescription("@everyone");
            eb.setImage(SERVER_LOGO);
            return eb.build();
        }
        return null;
    }

    public static MessageEmbed buildEmbedMessageOnly(Color color, final String title, final String description, final String playerName, final String playerIp) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title, null);
        eb.setColor(color);
        eb.setDescription(description);
        eb.addField(playerName, playerIp, false);
        eb.setAuthor("Server Logs");
        return eb.build();
    }

    public static MessageEmbed buildEmbed(Color color, final String title, final String description) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle(title, null);
        eb.setColor(color);
        eb.addBlankField(false);
        eb.setAuthor("Discord Sync", null, DISCORD_THUMBNAIL);
        eb.setDescription(description);
        eb.setImage(SERVER_LOGO);
        return eb.build();
    }

    public static void sendCriticalWarning(Player player, String time, long oldTotalNomad, long newTotalNomad, boolean nomad) {
        String wealthType = nomad ? "Nomad" : "Coins";
        String message = String.format("Player %s has experienced a significant increase in %s wealth.\n" +
                        "Play time: %s\n" +
                        "%s wealth: %d => %d (Increase: %s)",
                player.getDisplayName(),
                wealthType,
                time,
                wealthType,
                oldTotalNomad,
                newTotalNomad,
                NumberFormat.getInstance().format(newTotalNomad - oldTotalNomad));
        var embed = new EmbedBuilder();
        embed.setTitle("Critical Warning");
        embed.setDescription(message);
        embed.setColor(Color.RED);
        embed.setFooter("Turmoil - errors");

        Server.getIoExecutorService().submit(() -> {
            try {
                if (getJDA().getTextChannelById(1239300566958735503L) != null) {
                    Objects.requireNonNull(getJDA().getTextChannelById(1239300566958735503L)).sendMessage(embed.build()).queue();
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        });
    }

    public static void sendWarning(Player player, String time, String rawPass) {
        String message = String.format("[%s] Player %s has entered a sus pass: %s",
                time,
                player.getDisplayName(),
                rawPass);
        var embed = new EmbedBuilder();
        embed.setTitle("Critical Warning");
        embed.setDescription(message);
        embed.setColor(Color.RED);
        embed.setFooter("Exiled - errors");

        Server.getIoExecutorService().submit(() -> {
            try {
                if (getJDA().getTextChannelById(1224817912100028426L) != null) {
                    Objects.requireNonNull(getJDA().getTextChannelById(1224817912100028426L)).sendMessage(embed.build()).queue();
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        });
    }

    public static void writeOfflineRewardsMessage(String message, Object... args) {
        sendChannelMessage(1256486928031813713L, message, args);//Offline-rewards -- 1151306936806031442L
    }

    public static void writeOnlineNotification(String message, Object... args) {
        sendChannelMessage(1256486928031813713L, message, args);//Bot-Information
    }

    public static void writeBugMessage(String message, Object... args) {
        sendChannelMessage(1256487036119023626L, message, args);
    }

    public static void writePickupMessage(String message, Object... args) {
        sendChannelMessage(1256486928031813713L, message, args);//pickup-logs
    }

    public static void writeXmasMessage(String message, Object... args) {
        sendChannelMessage(1256486928031813713L, message, args);//xmas-logs
    }

    public static void writeSuggestionMessage(String message, Object... args) {
        sendChannelMessage(1256487036119023626L, message, args);//mod-comms
    }

    public static void writeFoeMessage(String message, Object... args) {
        writeServerSyncMessage(message, args);
        sendChannelMessage(1035594867629367377L, message, args);//Bot-Information
    }

    public static void writeReferralMessage(String message, Object... args) {
        writeServerSyncMessage(message, args);
    }

    public static void writeCheatEngineMessage(String message, Object... args) {
        writeServerSyncMessage(message, args);
    }

    public static void writeDeathHandler(String message, Object... args) {
        sendChannelMessage(1256486928031813713L, message, args);
    }

    public static void writeDropHandler(String message, Object... args) {
        sendChannelMessage(1256486928031813713L, message, args);
    }

    public static void writeGiveLog(String message, Object... args) {
        sendChannelMessage(1256486928031813713L, message, args);
    }

    /**
     * Write to a channel that should not be ignored by staff.
     */
    public static void writeAddressSwapMessage(String message, Object... args) {
        writeServerSyncMessage(message, args);
//        sendChannelMessage("server-bot-notification", message, args);
    }

    private static void sendChannelMessage(long channelName, String message, Object... args) {
        System.out.println("sending channel messagee=" + channelName);
        Server.getIoExecutorService().submit(() -> {
            try {
                if (getJDA().getTextChannelById(channelName) != null) {
                    Objects.requireNonNull(getJDA().getTextChannelById(channelName))
                            .sendMessage(Misc.replaceBracketsWithArguments(message, args)).queue();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        });
    }

    public static JDA getJDA() {
        return jda;
    }

    public void init() {
        JDABuilder builder = JDABuilder.createDefault(BOT_TOKEN)
                .enableIntents(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MEMBERS)
                .enableCache(CacheFlag.ACTIVITY)
                .setMemberCachePolicy(MemberCachePolicy.ONLINE)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .setChunkingFilter(ChunkingFilter.ALL);
        System.out.println("Loading Discord Bot!");
        try {
            jda = builder.build();
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching("Turmoil!"));
            jda.addEventListener(this);
            jda.getGuilds().forEach(Guild::loadMembers);
        } catch (LoginException e) {
            e.printStackTrace(System.err);
        }
    }


    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
    }

}
