package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

public class animloop extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        String[] args = input.split(" ");

/*        if (Integer.parseInt(args[0]) > 3000) {
            c.sendMessage("Max graphic id is: 3000");
            return;
        }*/

        if (args.length >= 2) {
            final int id = Integer.parseInt(args[0]);
            boolean plus = args[1].equals("+");
            CycleEventHandler.getSingleton().addEvent(c, new CycleEvent() {
                int idLoop = id;
                @Override
                public void execute(CycleEventContainer container) {
                    c.startAnimation(idLoop);
                    c.sendMessage("Playing animation: " + idLoop);
                    if (plus) {
                        idLoop++;
                    } else {
                        idLoop--;
                    }
                }
            }, 6);

        } else {
            c.sendMessage("Incorrect.");
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }
}