package io.xeros.content.commands.owner;

import io.xeros.content.commands.Command;
import io.xeros.model.Graphic;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.util.Misc;

import java.util.Optional;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 23/03/2024
 */
public class GfxTest extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                for(int i = 0; i < 10; i++) {
                    c.startGraphic(new Graphic(Misc.random(1500)));
                }
            }
        }, 2);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Tests new graphics system");
    }
}
