package io.xeros.content.combat;

/**
 * An enumeration of different hitmarks. Each type of hitmark has an identification value that seperates it from the rest.
 * 
 * @author Jason MacKeigan
 * @date Jan 26, 2015, 2:41:46 AM
 */
public enum Hitmark {
	MISS(0),//Tinted +8
	HIT(1),//Tinted +8
	NIGHTMARE_SHIELD(2),//Tinted +8
	POISON(3),//Tinted +8
	DAWNBRINGER(4),//Tinted +8
	VENOM(5),//Tinted +8
	ZULCANO_SHIELD(6),//Tinted +8
	TOTEM_CHARGE(7),//Tinted +8

	HEAL_PURPLE(16),
	;

	/**
	 * The id of the hitmark
	 */
	private final int id;

	/**
	 * Creates a new hitmark with an id
	 * 
	 * @param id the id
	 */
	Hitmark(int id) {
		this.id = id;
	}

	/**
	 * The identification value for this hitmark
	 * 
	 * @return the value
	 */
	public int getId() {
		return id;
	}

	/**
	 * Determines if this hitmark is blue, a miss.
	 * 
	 * @return true if the hitmark signifies a miss
	 */
	public boolean isMiss() {
		return equals(MISS);
	}

}
