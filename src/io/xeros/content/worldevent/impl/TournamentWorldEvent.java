package io.xeros.content.worldevent.impl;

import io.xeros.content.commands.Command;
import io.xeros.content.commands.all.Outlast;
import io.xeros.content.tournaments.TourneyManager;
import io.xeros.content.worldevent.WorldEvent;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;
import io.xeros.model.entity.player.broadcasts.Broadcast;

import java.util.List;

public class TournamentWorldEvent implements WorldEvent {

    private final TourneyManager tourney = TourneyManager.getSingleton();

    @Override
    public void init() {
        tourney.openLobby();
    }

    @Override
    public void dispose() {
        tourney.endGame();
    }

    @Override
    public boolean isEventCompleted() {
        return !tourney.isLobbyOpen() && !tourney.isArenaActive();
    }

    @Override
    public String getCurrentStatus() {
        return tourney.getTimeLeft();
    }

    @Override
    public String getEventName() {
        return "Outlast";
    }

    @Override
    public String getStartDescription() {
        return "starts";
    }

    @Override
    public Class<? extends Command> getTeleportCommand() {
        return Outlast.class;
    }

    @Override
    public void announce(List<Player> players) {
        String name = tourney.getTournamentType();
        if (tourney.getTournamentType().equalsIgnoreCase("DYOG")) {
            name = "Dig Your Own Grave";
        }
        new Broadcast("[<img=20>TOURNAMENT<img=20>] " + name + " style will begin soon, type ::outlast!").addTeleport(new Position(3109, 3480, 0)).copyMessageToChatbox().submit();
    }
}
