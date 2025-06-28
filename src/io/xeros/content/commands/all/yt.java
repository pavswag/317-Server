package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.ytmanager.YTManager;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.sql.ingamestore.PayPal;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 19/03/2024
 */
public class yt extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        if (player.getRights().isOrInherits(Right.ADMINISTRATOR) || player.getRights().isOrInherits(Right.YOUTUBER)) {
            player.start(new DialogueBuilder(player).option("YouTube Management",
                    new DialogueOption("Open youtube voting page", YTManager::open),
                    new DialogueOption("Post youtube video", p -> {
                        p.getPA().closeAllWindows();
                        player.getPA().sendEnterString("Enter youtube video ID to add", YTManager::postVideo);
                    }),
                    new DialogueOption("Delete video", p -> {
                        p.getPA().closeAllWindows();
                        player.getPA().sendEnterString("Enter youtube video ID to delete", YTManager::deleteVideo);
                    })));
            return;
        }
        YTManager.open(player);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }
}
