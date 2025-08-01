package io.xeros.content.combat.range;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

import io.xeros.model.entity.player.Player;

public enum Bow {

	SHORTBOW(841, Arrow.IRON), 
	LONGBOW(839, Arrow.IRON), 
	OAK_SHORTBOW(843, Arrow.STEEL), 
	OAK_LONGBOW(845, Arrow.STEEL), 
	WILLOW_SHORTBOW(849, Arrow.MITHRIL), 
	WILLOW_LONGBOW(847, Arrow.MITHRIL), 
	MAPLE_SHORTBOW(853, Arrow.ADAMANT), 
	MAPLE_LONGBOW(851, Arrow.ADAMANT), 
	YEW_SHORTBOW(857, Arrow.RUNE), 
	YEW_LONGBOW(855, Arrow.RUNE), 
	MAGIC_SHORTBOW(861, Arrow.AMETHYST), 
	MAGIC_LONGBOW(859, Arrow.AMETHYST), 
	DARK_BOW(11235, Arrow.DRAGON), 
	THIRD_AGE_BOW(12424, Arrow.DRAGON),
	DARK_BOW_GREEN(12765, Arrow.DRAGON), 
	DARK_BOW_BLUE(12766, Arrow.DRAGON), 
	DARK_BOW_YELLOW(12767, Arrow.DRAGON), 
	DARK_BOW_WHITE(12768, Arrow.DRAGON),
	TWISTED_BOW(20997, Arrow.DRAGON),
	MAGMA_TWISTED_BOW1(30203, Arrow.DRAGON),
	MAGMA_TWISTED_BOW2(30205, Arrow.DRAGON),
	MAGMA_TWISTED_BOW(30152, Arrow.DRAGON),
	CRYSTAL_LONG_BOW(30228, Arrow.DRAGON),
	Venator_Bow(27610, Arrow.DRAGON),
	CANDY_TWISTED_BOW(33160, Arrow.DRAGON),
	MAGIC_SHORTBOW_I(12788, Arrow.AMETHYST);

	private final int bowId;
	private final Arrow maxArrow;

	/**
	 * Construct an enum entry.
	 * 
	 * @param bowId The item id of the bow.
	 * @param maxArrow The best {@link Arrow} the bow can use.
	 */
	Bow(int bowId, Arrow maxArrow) {
		this.bowId = bowId;
		this.maxArrow = maxArrow;
	}

	/**
	 * Unmodifiable Set of the enum.
	 */
	private static final Set<Bow> VALUES = Collections.unmodifiableSet(EnumSet.allOf(Bow.class));

	/**
	 * Test whether a given a given bow can use a given arrow.
	 * 
	 *
	 * @param c@return True in case the arrow can be used in combination with the bow, False if either is not a correct item id for the slot or if it can't be used.
	 */
	public static boolean canUseArrow(Player c) {
		int bowId = c.playerEquipment[Player.playerWeapon];
		int arrowId = c.playerEquipment[Player.playerArrows];
		Optional<Bow> bow = VALUES.stream().filter(b -> b.bowId == bowId).findFirst();
		Optional<Arrow> arrow = Arrow.getArrow(arrowId);
		if (bow.isPresent() && arrow.isPresent()) {
			if (bow.get().maxArrow == arrow.get()) {
				return true;
			}
			try {
				return Arrow.indexOf(arrow.get()) <= Arrow.indexOf(bow.get().maxArrow);
			} catch (IllegalArgumentException e) {
				return false;
			}
		}
		return false;
	}

}
