package io.xeros.content.commands.test;

import java.util.Optional;

import io.xeros.content.commands.Command;
import io.xeros.content.vote_panel.VotePanelManager;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.util.Misc;

public class Points extends Command {
    @Override
    public void execute(Player player, String commandName, String input) {
        int points = 100_000;
        player.bossPoints = points;
        player.getSlayer().setPoints(points);
        player.donatorPoints = points;
        player.pcPoints = points;
        player.votePoints = points;
        player.achievementPoints = points;
        VotePanelManager.addVote(player.getLoginName());
        VotePanelManager.getUser(player).setBluePoints(points);
        VotePanelManager.getUser(player).setRedPoints(points);
        player.tournamentPoints = points;
        player.foundryPoints = points;
        player.pkp = points;
        player.amDonated = points;
        player.sendMessage("You have received " + Misc.insertCommas(points) + " of each point type.");
        player.getQuestTab().updateInformationTab();
    }

    @Override
    public boolean hasPrivilege(Player player) {
        return Right.GAME_DEVELOPER.equals(player.getRights().getPrimary());
    }

    public Optional<String> getDescription() {
        return Optional.of("Adds 100k of each point to your account.");
    }
}
