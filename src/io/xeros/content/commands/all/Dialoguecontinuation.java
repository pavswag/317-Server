package io.xeros.content.commands.all;

import io.xeros.content.commands.Command;
import io.xeros.content.dialogue.DialogueAction;
import io.xeros.model.entity.player.Player;

public class Dialoguecontinuation extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        if (player.getDialogueBuilder() == null) {
            return;
        }
        switch (input) {
            case "option_one":
                player.getDialogueBuilder().dispatchAction(DialogueAction.OPTION_1);
                break;
            case "option_two":
                player.getDialogueBuilder().dispatchAction(DialogueAction.OPTION_2);
                break;
            case "option_three":
                player.getDialogueBuilder().dispatchAction(DialogueAction.OPTION_3);
                break;
            case "option_four":
                player.getDialogueBuilder().dispatchAction(DialogueAction.OPTION_4);
                break;
            case "option_five":
                player.getDialogueBuilder().dispatchAction(DialogueAction.OPTION_5);
                break;
            case "continue":
                player.getDialogueBuilder().dispatchAction(DialogueAction.CLICK_TO_CONTINUE);
                break;
        }
    }


    @Override
    public boolean hasPrivilege(Player player) {
        return true;
    }
}
