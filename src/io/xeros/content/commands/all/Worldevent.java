package io.xeros.content.commands.all;

import java.util.Optional;

import io.xeros.content.bosses.hespori.HesporiSpawner;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;

public class Worldevent extends Command {

	@Override
	public void execute(Player player, String commandName, String input) {
		if (player.getMode().equals(Mode.forType(ModeType.HARDCORE_WILDYMAN)) || player.getMode().equals(Mode.forType(ModeType.WILDYMAN))) {
			player.sendMessage("You cannot access this area.");
			return;
		}
		if (HesporiSpawner.isSpawned()) {
			player.getPA().spellTeleport(2460, 3542, 0, false);
			player.setHesporiDamageCounter(0);
		} else {
			player.sendMessage("@red@[World Event] @bla@There is currently no world event going on.");
		}
	}


	@Override
	public boolean hasPrivilege(Player player) {
		return true;
	}

	@Override
	public Optional<String> getDescription() {
		return Optional.of("Teles you to world event.");
	}
}
