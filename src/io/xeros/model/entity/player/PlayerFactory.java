package io.xeros.model.entity.player;

import io.xeros.util.discord.Discord;

import java.util.Arrays;

public class PlayerFactory {

    public static void createTestPlayers() {
        Arrays.stream(Right.values()).forEach(right -> {
            String name = right.toString();
            if (name.length() > 12)
                name = name.substring(0, 12);
            Player player = Player.createBot(name, right);

           // Discord.writeSuggestionMessage(player.getDisplayName() + " createTestPlayers");
            player.addQueuedAction(Player::forceLogout);
        });
    }
}
