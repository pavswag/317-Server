package io.xeros.content.commands.admin;

import io.xeros.content.commands.Command;
import io.xeros.content.skills.Skill;
import io.xeros.content.tools.ToolAugments;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;

import java.util.Optional;

public class Givecrystal extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        String[] parts = input.split(" ");
        if (parts.length < 2) {
            player.sendMessage("Usage: ::givecrystal <skill> <amount>");
            return;
        }
        try {
            Skill skill = Skill.valueOf(parts[0].toUpperCase());
            int amount = Integer.parseInt(parts[1]);
            int crystalId = ToolAugments.SKILLING_CRYSTALS.getOrDefault(skill, -1);
            if (crystalId <= 0) {
                player.sendMessage("No crystal for that skill.");
                return;
            }
            player.getItems().addItem(crystalId, amount);
            player.sendMessage("Gave " + amount + " crystals for " + skill.name().toLowerCase() + ".");
        } catch (Exception e) {
            player.sendMessage("Invalid skill or amount.");
        }
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.ADMINISTRATOR.isOrInherits(player);
    }

    @Override
    public Optional<String> getDescription() {
        return Optional.of("Give skilling crystals");
    }
}
