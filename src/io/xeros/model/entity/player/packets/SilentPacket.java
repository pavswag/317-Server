package io.xeros.model.entity.player.packets;

import io.xeros.Server;
import io.xeros.model.entity.player.PacketType;
import io.xeros.model.entity.player.Player;
import io.xeros.util.logging.player.ReceivedPacketLog;

/**
 * Slient Packet
 **/
public class SilentPacket implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		Server.getLogging().write(new ReceivedPacketLog(c, packetType, "data type: " + packetType + ", data length: " + packetSize));
	}
}
