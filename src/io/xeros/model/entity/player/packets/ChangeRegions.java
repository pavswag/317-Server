package io.xeros.model.entity.player.packets;

import io.xeros.Server;
import io.xeros.model.collisionmap.RegionProvider;
import io.xeros.model.entity.player.PacketType;
import io.xeros.model.entity.player.Player;

/**
 * Change Regions
 */
public class ChangeRegions implements PacketType {

	@Override
	public void processPacket(Player c, int packetType, int packetSize) {
		c.getFarming().regionChanged();
		Server.itemHandler.reloadItems(c);
		Server.getGlobalObjects().updateRegionObjects(c);
		if (c.getPA().viewingOtherBank) {
			c.getPA().resetOtherBank();
		}

/*		if (c.skullTimer > 0) {
			c.isSkulled = true;
			c.headIconPk = 0;
			c.getPA().requestUpdates();
		}*/
	}

}
