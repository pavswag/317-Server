package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.sql.youtube.YouTubeAPIClient;

import java.util.List;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 19/03/2024
 */
public class Ytest extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        // Create an instance of YouTubeAPIClient
        YouTubeAPIClient youTubeAPIClient = new YouTubeAPIClient();

        // Video ID of the YouTube video
        String videoId = "N_TJkBWmu68";

        // Extract usernames from comments for the given video
        List<String> usernames = youTubeAPIClient.extractUsernamesFromComments(videoId);

        // Print the extracted usernames
        System.out.println("Usernames extracted from comments:");

        for (String username : usernames) {
            System.out.println(username);
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}
