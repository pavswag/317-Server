package io.xeros.content.combat.effects.special.impl;

import io.xeros.Server;
import io.xeros.content.combat.effects.special.SpecialEffect;
import io.xeros.content.items.ChristmasWeapons;
import io.xeros.model.Direction;
import io.xeros.model.Graphic;
import io.xeros.model.Items;
import io.xeros.model.StillGraphic;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Position;

/**
 * @author Arthur Behesnilian 9:11 PM
 */
public class ScytheOfVitur implements SpecialEffect {

    public static ScytheOfVitur SCYTHE_EFFECT = new ScytheOfVitur();

    public static boolean usingScythe(Player player) {
        return player.getItems().isWearingItem(Items.SCYTHE_OF_VITUR, Player.playerWeapon) ||
                player.getItems().isWearingItem(25736, Player.playerWeapon) ||
                player.getItems().isWearingItem(25739, Player.playerWeapon) ||
                player.getItems().isWearingItem(33162, Player.playerWeapon) ||
                player.getItems().isWearingItem(28997, Player.playerWeapon) ||
                player.getItems().isWearingItem(30340, Player.playerWeapon)||
                player.getItems().isWearingItem(30336, Player.playerWeapon)||
                player.getItems().isWearingItem(30154, Player.playerWeapon) ||
                player.getItems().isWearingItem(30156, Player.playerWeapon) ||
                player.getItems().isWearingItem(33161, Player.playerWeapon) ||
                player.getItems().isWearingItem(33148, Player.playerWeapon) ||
                player.getItems().isWearingItem(33184, Player.playerWeapon) ||
                player.getItems().isWearingItem(33203, Player.playerWeapon) ||
                player.getItems().isWearingItem(28543, Player.playerWeapon);
    }

    @Override
    public boolean activateSpecialEffect(Player player, Object... args) {
        boolean usingScythe = player.getItems().isWearingItem(Items.SCYTHE_OF_VITUR, Player.playerWeapon) ||
                player.getItems().isWearingItem(25736, Player.playerWeapon) ||
                player.getItems().isWearingItem(25739, Player.playerWeapon)||
                player.getItems().isWearingItem(33148, Player.playerWeapon)||
                player.getItems().isWearingItem(30340, Player.playerWeapon)||
                player.getItems().isWearingItem(30336, Player.playerWeapon)||
                player.getItems().isWearingItem(33184, Player.playerWeapon)||
                player.getItems().isWearingItem(28534, Player.playerWeapon)||
                player.getItems().isWearingItem(28997, Player.playerWeapon) ||
                player.getItems().isWearingItem(33203, Player.playerWeapon)||
                player.getItems().isWearingItem(28543, Player.playerWeapon)||
                player.getItems().isWearingItem(30154, Player.playerWeapon)||
                player.getItems().isWearingItem(30156, Player.playerWeapon)||
                player.getItems().isWearingItem(33162, Player.playerWeapon) && player.getChristmasWeapons().getCharges(33162) > 0
                        && (player.getChristmasWeapons().getCharges(33162) - 50) > 0 ||
                player.getItems().isWearingItem(33161, Player.playerWeapon) && player.getChristmasWeapons().getCharges(33161) > 0
                        && (player.getChristmasWeapons().getCharges(33161) - 50) > 0;

        Entity defender = (Entity) args[0];

        if (usingScythe) {
            Position defenderAdjacentPosition = defender.getAdjacentBorderPosition(player.getPosition());
            Position directional = player.getPosition().toDirectional(defenderAdjacentPosition);
            Direction direction = Direction.fromDirectional(directional);
            Position position = player.getPosition().translate(direction);
            int gfx = direction == Direction.NORTH ? 506
                    : direction == Direction.EAST ? 1172
                    : direction == Direction.SOUTH ? 478
                    : 1231;

            if (player.getItems().isWearingItem(33162, Player.playerWeapon)) {
                ChristmasWeapons.removeCharges(player, 33162, 50);
            }

            if (!player.getItems().isWearingItem(33162, Player.playerWeapon) && !player.getItems().isWearingItem(33203)) {
                Server.playerHandler.sendStillGfx(new StillGraphic(gfx, Graphic.GraphicHeight.HIGH, position), player.getInstance());
            }
        }

        return usingScythe;
    }

}
