package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.npc.NPCHandler;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.stream.Collectors;

/**
 * Admin command that prints adaptive state information of the boss the player
 * is currently targeting.
 */
public class Debugadaptive extends Command {

    @Override
    public void execute(Player c, String commandName, String input) {
        if (c.npcAttackingIndex <= 0) {
            c.sendMessage("You are not targeting a boss.");
            return;
        }
        NPC npc = NPCHandler.npcs[c.npcAttackingIndex];
        if (npc == null || !npc.isAdaptive()) {
            c.sendMessage("Target is not adaptive.");
            return;
        }
        c.sendMessage("Phase: " + npc.getCurrentPhase() + "/" + npc.getAdaptivePhases().length);
        c.sendMessage("Traits: " + npc.getAdaptiveTraits().stream().map(t -> t.getName()).collect(Collectors.joining(", ")));
        c.sendMessage("Enraged: " + npc.isEnraged());
        c.sendMessage("Next phase trigger: " + npc.getNextPhaseInfo());
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }
}
