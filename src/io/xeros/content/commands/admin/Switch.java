package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.model.SpellBook;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

/**
 * @author Arthur Behesnilian 1:04 PM
 */
public class Switch extends Command {

    @Override
    public void execute(Player player, String commandName, String input) {
        SpellBook nextSpellbook = SpellBook.MODERN;
        switch (player.playerMagicBook) {
            case 0:
                nextSpellbook = SpellBook.ANCIENT;
                break;
            case 1:
                nextSpellbook = SpellBook.LUNAR;
                break;
        }
        player.sendMessage("You switch your spellbook to the " + nextSpellbook.name()+ " spellbook.");
        player.setSpellBook(nextSpellbook);
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }

}
