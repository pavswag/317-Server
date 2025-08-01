package io.xeros.util.logging.player;

import io.xeros.model.entity.player.Player;
import io.xeros.util.logging.PlayerLog;

import java.util.Set;

public class ReceivedPacketLog extends PlayerLog {

    private final int opcode;
    private final String details;

    public ReceivedPacketLog(Player player, int opcode, String details) {
        super(player);
        this.opcode = opcode;
        this.details = details;
    }


    @Override
    public Set<String> getLogFileNames() {
        return Set.of("received_packets");
    }

    @Override
    public String getLoggedMessage() {
        return String.format("Opcode '%s':  '%s'.", opcode, details);
    }
}
