package io.xeros.model.entity.player.packets;

import io.xeros.Server;
import io.xeros.model.ContainerAction;
import io.xeros.model.ContainerActionType;
import io.xeros.model.entity.player.PacketType;
import io.xeros.model.entity.player.Player;
import io.xeros.util.logging.player.ReceivedPacketLog;

public class ContainerAction7 implements PacketType {

	@Override
	public void processPacket(Player player, int packetType, int packetSize) {
		if (player.getMovementState().isLocked() || player.getLock().cannotInteract(player))
			return;
		if (player.isFping()) {
			/**
			 * Cannot do action while fping
			 */
			return;
		}
		player.interruptActions();
		int interfaceId = player.getInStream().readUnsignedWord();
		int itemId = player.getInStream().readSignedWordBigEndianA();
		int itemSlot = player.getInStream().readSignedWordBigEndian();
		Server.getLogging().write(new ReceivedPacketLog(player, packetType, "i=" + interfaceId + "/" + itemSlot + "/" + itemId));

		ContainerAction action = new ContainerAction(ContainerActionType.ACTION_7, interfaceId, itemId, itemSlot);

		if (player.debugMessage)
			player.sendMessage("ContainerAction4: interfaceid: "+interfaceId+", removeSlot: "+itemSlot+", removeID: " + itemId);
	}

}
