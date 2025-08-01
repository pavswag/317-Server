package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;

public class Toggle extends Command {

	@Override
	public void execute(Player c, String commandName, String input) {
		String[] args = input.split(" ");
		
		switch (args[0]) {
		
		case "":
			c.sendMessage("Usage: ::toggle crystal");
			c.sendMessage("Usage: ::toggle hourly");
			c.sendMessage("Usage: ::toggle crystalinfo");
			break;
		case "crystal":
			if (c.getFracturedCrystalToggle()) {
				c.setFracturedCrystalToggle(false);
				c.sendMessage("You will @red@no longer receive @bla@fractured crystals.");
			} else {
				c.setFracturedCrystalToggle(true);
				c.sendMessage("You will @gre@now receive @bla@fractured crystals.");
			}
			break;
		case "hourly":
			if (c.getHourlyBoxToggle()) {
				c.setHourlyBoxToggle(false);
				c.sendMessage("You will @red@no longer receive @bla@hourly reward boxes.");
			} else {
				c.setHourlyBoxToggle(true);
				c.sendMessage("You will @gre@now receive @bla@hourly reward boxes.");
			}
			break;
		case "crystalinfo":
			if (c.crystalDrop) {
				c.crystalDrop = false;
				c.sendMessage("You have @gre@enabled @bla@crystal drop information.");
			} else {
				c.crystalDrop = true;
				c.sendMessage("You have @red@disabled @bla@crystal drop information.");
			}
			break;	
		}
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return true;
	}


	@Override
	public Optional<String> getDescription() {
		return Optional.of("Toggles various events");
	}
}
