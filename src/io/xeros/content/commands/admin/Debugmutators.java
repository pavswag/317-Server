package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.instances.InstanceMutator;
import io.xeros.content.instances.InstanceMutatorManager;
import io.xeros.content.instances.MutatorRarity;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.util.Misc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/** Lists active mutators and synergy modifiers. */
public class Debugmutators extends Command {
    @Override
    public void execute(Player c, String commandName, String input) {
        if (!hasPrivilege(c)) {
            c.sendMessage("This command is for admins only.");
            return;
        }
        String time = new SimpleDateFormat("HH:mm:ss").format(new Date());
        for (InstanceMutator mut : InstanceMutatorManager.getActiveMutators()) {
            MutatorRarity rarity = InstanceMutatorManager.getRarity(mut);
            c.sendMessage(mut.name() + " (" + rarity + ")");
            Misc.println("[" + time + "] " + mut.name() + " (" + rarity + ")");
            List<?> mods = InstanceMutatorManager.getHazardSynergies(mut);
            for (Object obj : mods) {
                c.sendMessage(" - " + obj.toString());
            }
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("debug mutators");
    }
}
