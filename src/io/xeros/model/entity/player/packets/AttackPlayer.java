package io.xeros.model.entity.player.packets;

import io.xeros.Server;
import io.xeros.content.combat.magic.CombatSpellData;
import io.xeros.content.combat.magic.LunarSpells;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Boundary;
import io.xeros.model.entity.player.PacketType;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;

/**
 * Attack Player
 **/
public class AttackPlayer implements PacketType {

	public static final int ATTACK_PLAYER = 73, MAGE_PLAYER = 249;

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
		player.playerAttackingIndex = 0;
		player.npcAttackingIndex = 0;

		if (player.isForceMovementActive()) {
			return;
		}
		if (player.isNpc) {
			return;
		}
		if (player.insideBoundary(Boundary.THEATRE_LOBBY_ENTRANCE) || (player.insideBoundary(Boundary.XERIC_LOBBY) || (player.insideBoundary(Boundary.TOMBS_OF_AMASCUT_LOBBY_ENTRANCE)))) {
			return;
		}
		if (player.insideBoundary(Boundary.WILDERNESS)) {
			for (int i : new int[]{20370, 20374, 20372, 20368, 27275, 33205, 30154, 30156, 33434,30158, 33058, 33207, 27235,
					27238, 27241, 33141, 33142, 33143, 33202, 33204, 28688, 28254, 28256, 28258, 26269, 26551,
					10559, 10556, 26914, 33160, 33161, 33162, 25739, 25736, 26708, 24664, 24666, 24668,
					25918, 26482, 26484, 25734, 26486, 33184, 25975, 22954, 13372, 33203, 33206, 27428, 27430, 27432, 27434, 27436, 27438,
					33149, 26477, 26475, 26473, 26467, 33189, 33190, 33191, 27253, 33183, 33186, 33187, 33188, 12899, 12900,
					26469, 26471, 13681, 26235, 12892, 12893, 12894, 12895, 12896, 33064, 33059, 33063, 33060, 33062, 33061, 27473, 27475, 27477, 27479, 27481}) {

				//System.out.println(ItemDef.forId(i).getName());
				if (player.getItems().hasItemOnOrInventory(i)) {
					player.sendMessage("[@red@WILD@bla@] @blu@You can not take " + ItemDef.forId(i).getName() + " into the wild!");
					return;
				}
			}
		}
		switch (packetType) {
		case ATTACK_PLAYER:
			if (player.morphed || player.respawnTimer > 0) {
				return;
			}

			//player.stopMovement();
			int playerIndex = player.getInStream().readSignedWordBigEndian();

			String option = player.getPA().getPlayerOptions().getOrDefault(3, "null");
			player.debug(String.format("PlayerOption \"%s\" on player index %d.", option, player.playerAttackingIndex));

			PlayerHandler.getOptionalPlayerByIndex(playerIndex).ifPresentOrElse(player1 -> {
				if (player.getController().onPlayerOption(player, player1, option))
					return;

				player.usingClickCast = false;
				player.playerAttackingIndex = playerIndex;
				player.faceEntity(player1);

				if (player.getPA().viewingOtherBank) {
					player.getPA().resetOtherBank();
				}

				if (player.attacking.attackEntityCheck(player1, true)) {
					player.attackEntity(player1);
				} else {
					player.attacking.reset();
				}
			}, () -> player.attacking.reset());
			break;

		case MAGE_PLAYER:
			if (player.morphed || player.respawnTimer > 0) {
				player.attacking.reset();
				return;
			}

			player.stopMovement();
			player.playerAttackingIndex = player.getInStream().readSignedWordA();
			int castingSpellId = player.getInStream().readSignedWordBigEndian();
			player.usingClickCast = false;

			PlayerHandler.getOptionalPlayerByIndex(player.playerAttackingIndex).ifPresentOrElse(player1 -> {
				player.faceEntity(player1);

				if (player1.isTeleblocked() && castingSpellId == CombatSpellData.TELEBLOCK) {
					player.sendMessage("That player is already affected by this spell.");
					player.attacking.reset();
					return;
				}

				for (int r = 0; r < CombatSpellData.REDUCE_SPELLS.length; r++) {
					if (CombatSpellData.REDUCE_SPELLS[r] == castingSpellId) {
						if ((System.currentTimeMillis()
								- player1.reduceSpellDelay[r]) < CombatSpellData.REDUCE_SPELL_TIME[r]) {
							player.sendMessage("That player is currently immune to this spell.");
							player.attacking.reset();
							return;
						}
					}
				}

				if (castingSpellId > 30_000) {
					LunarSpells.CastingLunarOnPlayer(player, castingSpellId);
				}

				for (int i = 0; i < CombatSpellData.MAGIC_SPELLS.length; i++) {
					if (castingSpellId == CombatSpellData.MAGIC_SPELLS[i][0]) {
						if (player.attacking.attackEntityCheck(player1, true)) {
							player.attackEntity(player1);
							player.setSpellId(i);
							player.usingClickCast = true;
						} else {
							player.attacking.reset();
						}
						return;
					}
				}

				player.attacking.reset();
			}, () -> player.attacking.reset());
			break;

		}

	}

}
