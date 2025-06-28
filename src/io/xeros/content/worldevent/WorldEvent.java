package io.xeros.content.worldevent;

import java.util.List;

import io.xeros.content.commands.Command;
import io.xeros.content.worldevent.impl.TournamentWorldEvent;
import io.xeros.content.worldevent.impl.WGWorldEvent;
import io.xeros.model.entity.player.Player;

public interface WorldEvent {

    default boolean isOutlast() {
        return getClass().equals(TournamentWorldEvent.class);
    }

    default boolean isWG() {
        return getClass().equals(WGWorldEvent.class);
    }

    void init();

    void dispose();

    boolean isEventCompleted();

    String getCurrentStatus();

    String getEventName();

    /**
     * Start description would be something like starts/spawns/begins in x minutes.
     */
    String getStartDescription();

    Class<? extends Command> getTeleportCommand();

    void announce(List<Player> players);
}
