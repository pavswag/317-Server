package io.xeros.model.entity.player.packets.objectoptions;

import io.xeros.Server;
import io.xeros.content.dialogue.impl.OutlastLeaderboard;
import io.xeros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.xeros.model.collisionmap.ObjectDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.world.objects.GlobalObject;
import io.xeros.objects.ObjectAction;

public class ObjectOptionFour {
	
	public static void handleOption(final Player c, int objectType, int obX, int obY) {
		if (Server.getMultiplayerSessionListener().inAnySession(c)) {
			return;
		}
		c.clickObjectType = 0;

		GlobalObject object = new GlobalObject(objectType, obX, obY, c.heightLevel);

		if (c.getRights().isOrInherits(Right.STAFF_MANAGER) && c.debugMessage)
			c.sendMessage("Clicked Object Option 4:  "+objectType+"");

		ObjectDef def = ObjectDef.getObjectDef(objectType);
		ObjectAction action = null;
		ObjectAction[] actions = def.defaultActions;
		if(actions != null)
			action = actions[3];
		if(action == null && (actions = def.defaultActions) != null)
			action = actions[3];
		if(action != null) {
			action.handle(c, object);
			return;
		}

		if (OutlastLeaderboard.handleInteraction(c, objectType, 4))
			return;

		switch (objectType) {
			case 27980:
				FireOfExchangeBurnPrice.openExchangeRateShop(c);
				break;
		case 31858:
		case 29150:
			c.setSidebarInterface(6, 938);
			c.playerMagicBook = 0;
			c.sendMessage("You feel a drain on your memory.");
			break;
		case 8356://streehosidius
			c.getPA().movePlayer(1679, 3541, 0);
			break;
		}
	}

}
