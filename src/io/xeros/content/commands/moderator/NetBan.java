package io.xeros.content.commands.moderator;

import io.xeros.content.commands.Command;
import io.xeros.content.commands.punishment.PunishmentCommand;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

public class NetBan extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        new PunishmentCommand(commandName, input).parse(player);
    }


    @Override
    public boolean hasPrivilege(Player player) {
        return Right.HELPER.isOrInherits(player);
    }

    public Optional<String> getDescription() {
        return Optional.of("Ban all known addresses of an online player.");
    }

    @Override
    public String getFormat() {
        return PunishmentCommand.getFormat(getCommand());
    }
}
