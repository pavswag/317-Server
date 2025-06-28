package io.xeros.util.discord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import io.xeros.Configuration;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.save.PlayerSave;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.ErrorHandler;
import net.dv8tion.jda.api.requests.ErrorResponse;

import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static io.xeros.util.discord.Discord.*;

public class DiscordIntegration {

    public static Map<String, Long> connectedAccounts = new HashMap<>();
    public static ArrayList<Long> disableMessage = new ArrayList<>();

    public static Map<String, Long> idForCode = new HashMap<>();

    public static String generateCode(int length) {
        String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
        String specialCharacters = "!@#$";
        String numbers = "1234567890";
        String combinedChars = lowerCaseLetters + lowerCaseLetters + specialCharacters + numbers;
        Random random = new Random();
        char[] password = new char[length];

        password[0] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
        password[1] = lowerCaseLetters.charAt(random.nextInt(lowerCaseLetters.length()));
        password[2] = specialCharacters.charAt(random.nextInt(specialCharacters.length()));
        password[3] = numbers.charAt(random.nextInt(numbers.length()));

        for (int i = 4; i < length; i++) {
            password[i] = combinedChars.charAt(random.nextInt(combinedChars.length()));
        }

        return new String(password);
    }


    public static void sendPrivateMessage(User user, TextChannel c, String content) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) {
            return;
        }

        ErrorHandler handler = new ErrorHandler().handle(ErrorResponse.CANNOT_SEND_TO_USER, (error) -> {
            c.sendMessage(user.getAsMention() + " You must enable your private messages first!").queue();
        });

        user.openPrivateChannel().queue((channel) -> {
            channel.sendMessage(content).queue(null, handler);
        });
    }

    public static void sendPMS(String content) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) {
            return;
        }

        Guild guild = Discord.getGuild();
        for (Map.Entry<String, Long> entry : DiscordIntegration.connectedAccounts.entrySet()) {
            Player player = PlayerHandler.getPlayerByLoginName(entry.getKey());
            if (player == null)
                continue;
            Member member = Objects.requireNonNull(guild).getMemberById(entry.getValue());
            if (member == null) {
                continue;
            }
            if (disableMessage.contains(entry.getValue()))
                continue;

            User user = member.getUser();

            ErrorHandler handler = new ErrorHandler().handle(ErrorResponse.CANNOT_SEND_TO_USER, (error) -> {
//                     c.sendMessage(user.getAsMention() + " You must enable your private messages first!").queue();
            });

            user.openPrivateChannel().queue((channel) ->
                    channel.sendMessage("A new update has just released on Turmoil!").queue(null, handler));
        }
    }

    public static void enterTag(Player player) {
        player.getPA().sendEnterString("Enter Your Discord Tag", (p, tag) -> DiscordIntegration.getTag(p, new StringBuilder(tag)));
    }

    public static void getTag(Player player, StringBuilder tag) {
        if (!tag.toString().contains("#")) tag.append("#0000");
        try {
            User user = Discord.jda.getUserByTag(tag.toString());
            if (connectedAccounts.containsValue(Objects.requireNonNull(user).getIdLong())) {
                player.sendErrorMessage("That Discord account is already linked to another player!");
                return;
            }
            final String code = generateCode(5);
            EmbedBuilder eb = new EmbedBuilder();
            eb.setTitle("**Code To Enter: " + code + "**", null);
            eb.setAuthor("Turmoil Discord Link Token");
            eb.setColor(Color.MAGENTA);
            eb.setThumbnail(DISCORD_THUMBNAIL);
            Objects.requireNonNull(Discord.getJDA().getUserByTag(tag.toString()))
                    .openPrivateChannel()
                    .queue((channel) ->
                            channel.sendMessage(eb.build()).queue());
            idForCode.put(code, user.getIdLong());
            player.getPA().sendEnterString("Enter The Generated Code", DiscordIntegration::integrateAccount);
        } catch (IllegalArgumentException e) {
            player.sendErrorMessage("You did not enter your discord tag correctly, You may be missing #!");
        }
    }

    public static void integrateAccount(Player player, String code) {

        if (connectedAccounts == null) {
            loadConnectedAccounts();
        }

        if (player.getDiscordUser() > 0) {
            player.sendMessage("You already have a connected discord account!");
        }

        if (!idForCode.containsKey(code)) {
            player.sendMessage("You have entered an invalid code! Try again.");
            return;
        }

        long user = idForCode.get(code);

        idForCode.remove(code);

        String name = Objects.requireNonNull(Discord.jda.getUserById(user)).getAsTag();

        if (connectedAccounts.containsValue(Objects.requireNonNull(jda.getUserById(user)).getIdLong())) {
            player.sendErrorMessage("That Discord account is already linked to another player!");
            return;
        }

        player.sendMessage("You have connected the discord account '" + name + "'.");
        player.setDiscordUser(user);
        player.setDiscordTag(name);
        connectedAccounts.put(name, user);
        updateDiscordInterface(player);


        Discord.writeServerSyncMessage(player.getDisplayName() + " : " + name + " : " + player.getIpAddress() + " : " + player.getMacAddress() + " : " + player.getUUID());

        if (!player.getDiscordlinked() && player.getDiscordPoints() <= 10) {
            player.amDonated += 10;
            player.updateRank();
            player.sendMessage("@mag@You received $10 to your total donated amount for linking your Discord account!");
            player.setDiscordlinked(true);
            player.increaseDiscordPoints(10);
            final String description =
                    "They received the following **rewards**: \n" +
                            "**$10 in Donation Credits** \n" +
                            "**3010 Bonus Welcoming Discord Points** \n" +
                            " \n" +
                            "** Speak To Discord In-Game To Claim Your Discord Sync Rewards TODAY!**";
            Discord.sendEmbeddedDiscordSyncMessage(DISCORD_SYNC_CHANNEL, Color.MAGENTA, StringUtil.capitalizeJustFirst(player.getLoginName()) + " Has just synced their Discord account!", description);
        }

        PlayerSave.saveGame(player);
    }

    public static void loadConnectedAccounts() {
        File file = new File("./save_files/discord/discordConnectedAccounts.json");

        try (FileReader fileReader = new FileReader(file)) {
            JsonParser fileParser = new JsonParser();
            Gson builder = new GsonBuilder().create();
            JsonObject reader = (JsonObject) fileParser.parse(fileReader);
            if (reader.has("connectedAccounts")) {

                connectedAccounts = builder.fromJson(reader.get("connectedAccounts"),
                        new TypeToken<Map<String, Long>>() {
                        }.getType());
            }

            if (reader.has("disableMessage")) {
                Long[] pricesData = builder.fromJson(reader.get("disableMessage"), Long[].class);
                disableMessage.addAll(Arrays.asList(pricesData));
            }

            System.out.println("Loaded Discord Connected Accounts!");
        } catch (Exception e) {
            System.out.println("Error Loading Discord Connected Accounts!");
        }
    }

    public static void saveConnectedAccounts() {
        File file = new File("./save_files/discord/discordConnectedAccounts.json");
        try (FileWriter writer = new FileWriter(file)) {
            Gson builder = new GsonBuilder().setPrettyPrinting().create();
            JsonObject object = new JsonObject();

            object.add("connectedAccounts", builder.toJsonTree(connectedAccounts));

            object.add("disableMessage", builder.toJsonTree(disableMessage));


            writer.write(builder.toJson(object));
            writer.close();
            System.out.println("Saved Discord Connected Accounts!");
        } catch (Exception e) {
            System.out.println("Error Saving Discord Connected Accounts!");
        }
    }


    public static void updateDiscordPoints(Player player) {
        player.getPA().sendString(37511, "@whi@" + player.getDiscordPoints());
    }

    public static void updateDiscordInterface(Player player) {
        if (player.getDiscordUser() <= 0) {
            player.getPA().sendString(37507, "@red@Inactive");
        } else {
            player.getPA().sendString(37507, "@whi@" + player.getDiscordTag());
        }

        if (disableMessage.contains(player.getDiscordUser())) {
            player.getPA().sendString(37508, "@whi@Active");
        } else {
            player.getPA().sendString(37508, "@red@Inactive");
        }

        if (Discord.jda != null) {
            Guild guild = Discord.getGuild();

            if (guild != null) {
                for (Member booster : guild.getBoosters()) {
                    if (player.getDiscordUser() == booster.getIdLong()) {
                        player.getPA().sendString(37509, "@whi@Boosting!");
                        player.getPA().sendString(37510, "@whi@Receiving 10% Damage Boost!" +
                                "\\n@whi@Receiving 10% Rare rewards from raids!" +
                                "\\n@whi@Receiving 10% Chance double achievement gain!");
                        break;
                    } else {
                        player.getPA().sendString(37509, "@red@Inactive");
                        player.getPA().sendString(37510, "@red@Inactive");
                    }
                }
            }
        }

        player.getPA().sendString(37511, "@whi@" + player.getDiscordPoints());
    }

    public static void buttonClick(Player player) {
        if (player.getDiscordTag() != null && player.getDiscordUser() > 0) {
            if (disableMessage.contains(player.getDiscordUser())) {
                disableMessage.remove(player.getDiscordUser());
            } else {
                disableMessage.add(player.getDiscordUser());
            }
        } else {
            player.sendMessage("You need to link your account first.");
        }
        updateDiscordInterface(player);
    }

    public static void disconnectUser(Player player) {
        for (Map.Entry<String, Long> entry : DiscordIntegration.connectedAccounts.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(player.getLoginName())) {
                DiscordIntegration.connectedAccounts.remove(player.getLoginName());
                player.setDiscordlinked(false);
                player.setDiscordTag("");
                player.setDiscordUser(0);
                player.sendMessage("Your discord account has been removed from your account.");
            }
        }
        Discord.writeServerSyncMessage("[DISCORD] " + player.getDisplayName() + " has disconnected there account.");
        updateDiscordInterface(player);
    }

    public static void sendMessage(String message, long channel) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) {
            return;
        }
        Objects.requireNonNull(Discord.jda.getTextChannelById(channel)).sendMessage(message).queue();
    }

    public static void sendHookMessage(String message, long channel) {
        if (Configuration.DISABLE_DISCORD_MESSAGING) {
            return;
        }
        Objects.requireNonNull(Discord.jda.getTextChannelById(channel)).sendMessage(message).queue();
    }

    public static long delay;

    public static void givePoints() {
        if (delay > System.currentTimeMillis()) {
            return;
        }
        if (Discord.jda != null) {
            Guild guild = Discord.getGuild();

            for (Map.Entry<String, Long> entry : DiscordIntegration.connectedAccounts.entrySet()) {
                Player player = PlayerHandler.getPlayerByDiscordTag(entry.getKey());
                if (player == null)
                    continue;

                Member member = Objects.requireNonNull(guild).getMemberById(entry.getValue());

                if (member == null) continue;

                CheckDonor(player, member.getRoles());

                boolean containsStatus = false;
                boolean boosting = false;

                for (Activity a : member.getActivities()) {
                    String status = a.getName().toLowerCase();
                    if (status.equalsIgnoreCase("Mercy")) {
                        containsStatus = true;
                        break;
                    }
                }

                for (Member booster : guild.getBoosters()) {
                    if (member == booster && player.getDiscordboostlastClaimed() < System.currentTimeMillis()) {
                        player.getItems().addItemUnderAnyCircumstance(13346, 2);
                        player.getItems().addItemUnderAnyCircumstance(696, 100);
                        player.getItems().addItemUnderAnyCircumstance(8167, 1);
                        player.sendMessage("Your discord boost has granted you with 2x UMB, 25m Upgrade Points & a Nomad Chest!");
                        player.setDiscordboostlastClaimed(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7));
                    }

                    if (member == booster) {
                        boosting = true;
                    }
                }

                delay = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15);
                if (containsStatus) {
                    player.increaseDiscordPoints(3000 + (boosting ? 3000 : 0));
                } else {
                    player.increaseDiscordPoints(1000 + (boosting ? 3000 : 0));
                }
            }
        }
    }


    public static void CheckDonor(Player player, List<Role> roles) {
/*        if (player.amDonated >= 20 && player.amDonated < 50) {
            Role rl = (Role) roles.stream().filter(role -> role.getIdLong() == 2L);
        } else if (player.amDonated >= 50 && player.amDonated < 100) {

        } else if (player.amDonated >= 100 && player.amDonated < 250) {

        } else if (player.amDonated >= 250 && player.amDonated < 500) {

        } else if (player.amDonated >= 500 && player.amDonated < 750) {

        } else if (player.amDonated >= 750 && player.amDonated < 1000) {

        } else if (player.amDonated >= 1000 && player.amDonated < 1500) {

        } else if (player.amDonated >= 1500 && player.amDonated < 2000) {

        } else if (player.amDonated >= 2000 && player.amDonated < 3000) {

        } else if (player.amDonated >= 3000) {

        }*/
    }
}
