package io.xeros.content.skills.woodcutting;

import io.xeros.Server;
import io.xeros.content.skills.Skill;
import io.xeros.model.Items;
import io.xeros.model.entity.player.Player;

public class Woodcutting {

	private static final Woodcutting INSTANCE = new Woodcutting();

	public void chop(Player player, int objectId, int x, int y) {
        Tree tree = Tree.forObject(objectId);
        player.facePosition(x, y);
        if (!player.isBot() && player.playerLevel[Player.playerWoodcutting] < tree.getLevelRequired()) {
                player.sendMessage("You do not have the woodcutting level required to cut this tree down.");
                return;
        }
        Hatchet hatchet = Hatchet.getBest(player);
        if (hatchet == null && !player.isBot()) {
                hatchet = Hatchet.getBest(player);
        }
        if (!player.isBot() && hatchet == null) {
                player.sendMessage("You must have an axe and the level required to cut this tree down.");
                return;
        }
        if (!player.isBot() && player.getItems().freeSlots() == 0) {
                player.sendMessage("You must have at least one free inventory space to do this.");
                return;
        }
		if (Server.getGlobalObjects().exists(tree.getStumpId(), x, y)) {
			player.sendMessage("This tree has been cut down to a stump, you must wait for it to grow.");
			return;
		}
		player.getPA().stopSkilling();
		player.sendMessage("You swing your axe at the tree.");
		player.startAnimation(hatchet.getAnimation());
		Server.getEventHandler().submit(new WoodcuttingEvent(player, tree, hatchet, objectId, x, y));
	}

	public static Woodcutting getInstance() {
		return INSTANCE;
	}

}
