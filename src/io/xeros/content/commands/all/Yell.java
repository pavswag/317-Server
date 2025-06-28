package io.xeros.content.commands.all;

import io.xeros.Server;
import io.xeros.content.commands.Command;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.RightGroup;
import io.xeros.punishments.PunishmentType;
import io.xeros.util.Misc;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Tells the player they need to be a donator to use this feature.
 *
 * @author Emiel
 */
public class Yell extends Command {

	private static final Right[] PERMITTED = { Right.DONATOR, Right.SUPER_DONATOR, Right.GREAT_DONATOR, Right.EXTREME_DONATOR, Right.MAJOR_DONATOR, Right.SUPREME_DONATOR, Right.GILDED_DONATOR,
			Right.PLATINUM_DONATOR, Right.APEX_DONATOR, Right.ALMIGHTY_DONATOR, Right.HELPER, Right.MODERATOR, Right.ADMINISTRATOR,
			Right.STAFF_MANAGER, Right.GAME_DEVELOPER, Right.YOUTUBER, Right.HITBOX };

	private static final String[] ILLEGAL_ARGUMENTS = { "<img", "@cr", "<tran", "#url#", "<shad", "<str",
			":tradereq:", ":duelreq:", ":chalreq:" };

	@Override
	public void execute(Player player, String commandName, String input) {
		RightGroup rights = player.getRights();

		Set<Right> prohibited = new HashSet<>(Arrays.asList(PERMITTED));


		long count = rights.getSet().stream().filter(r -> prohibited.stream().anyMatch(r2 -> r.isOrInherits(player))).count();

		if (count == 0 || rights.getPrimary() == Right.PLAYER) {
			player.sendMessage("You do not have the rights to access this command.");
			return;
		}

		long delay = getDelay(player);
		if (System.currentTimeMillis() - player.lastYell < delay) {
			player.sendMessage("You must wait another " + TimeUnit.MILLISECONDS.toSeconds(player.lastYell + delay - System.currentTimeMillis()) + " seconds before you can yell.");
			return;
		}
		if (Server.getPunishments().contains(PunishmentType.MUTE, player.getLoginName())) {
			player.sendMessage("You are muted, you cannot use this system.");
			return;
		}
		if (Server.getPunishments().isNetMuted(player)) {
			player.sendMessage("You are muted, you cannot use this system.");
			return;
		}
		if (System.currentTimeMillis() < player.muteEnd) {
			player.sendMessage("You are muted, you cannot use this system.");
			return;
		}

		for (String argument : ILLEGAL_ARGUMENTS) {
			if (input.contains(argument) && !rights.isOrInherits(Right.STAFF_MANAGER)) {
				player.sendMessage("Your message contains an illegal set of characters, you cannot yell this.");
				return;
			}
		}

		String message = formatMessage(player.getTitles().getCurrentTitle(), player.getDisplayNameFormatted(), player.getRights().getPrimary().getColor(), StringUtils.capitalize(input));

		if (!Misc.isValidChatMessage(message)) {
			player.sendMessage("Invalid message.");
			return;
		}

		if (Misc.isSpam(message)) {
			player.sendMessage("Please don't spam.");
			return;
		}

		player.lastYell = System.currentTimeMillis();
		PlayerHandler.executeGlobalMessage(message, receiving -> !receiving.getFriendsList().getRepository().isIgnore(player));
	}

	@Override
	public boolean hasPrivilege(Player player) {
		return true;
	}

	public static String formatMessage(String title, String username, String color, String message) {
		if (title.equalsIgnoreCase("None") || title.length() == 0) {
			title = "";
		} else if (title.contains("owner") || title.contains("manager") || title.contains("moderator") || title.contains("support")) {
			title = "[<col=" + color + ">" + title + "</col>]";
		}
		return title + "<col=" + color + ">" + username + "</col>: " + StringUtils.capitalize(message.toLowerCase());
	}
	private long getDelay(Player player) {
		RightGroup rights = player.getRights();

		if (rights.isOrInherits(Right.HELPER)) {
			return 0;
		}
		if (player.amDonated >=500) {
			return 0;
		} else if (player.amDonated >=250) {
			return 0;
		} else if (player.amDonated >=100) {
			return 5;
		} else if (player.amDonated >=50) {
			return 15;
		} else if (player.amDonated >=20) {
			return 30;
		} else {
			return 60;
		}
	}


	@Override
	public Optional<String> getDescription() {
		return Optional.of("Sends a global chat message");
	}

	@Override
	public Optional<String> getParameter() {
		return Optional.of("message");
	}

	@Override
	public boolean isHidden() {
		return true;
	}
}
