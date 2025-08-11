package io.xeros.content.items;

import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

import java.util.Set;

/**
 * Handles charging and consuming charges for Wraith weapons using Wraith Essence.
 */
public class WraithWeaponCharge {

    public static final int WRAITH_ESSENCE = 26879;
    public static final int WRAITH_SCYTHE = 33431;
    public static final int WRAITH_STAFF = 33433;
    public static final int WRAITH_BOW = 33434;

    /** Number of charges added per Wraith Essence consumed. */
    public static final int WRAITH_CHARGE_PER_ESSENCE = 500;
    /** Maximum number of charges a Wraith weapon can store. */
    public static final int MAX_WRAITH_CHARGE = 10_000;

    private static final Set<Integer> WRAITH_WEAPONS = Set.of(WRAITH_SCYTHE, WRAITH_STAFF, WRAITH_BOW);

    /**
     * Handles using Wraith Essence on a Wraith weapon.
     *
     * @return true if the interaction was handled
     */
    public static boolean useItem(Player player, int item1, int item2) {
        if ((item1 == WRAITH_ESSENCE && WRAITH_WEAPONS.contains(item2))
                || (item2 == WRAITH_ESSENCE && WRAITH_WEAPONS.contains(item1))) {

            int weaponId = WRAITH_WEAPONS.contains(item1) ? item1 : item2;
            int essenceAmount = player.getItems().getItemAmount(WRAITH_ESSENCE);
            if (essenceAmount <= 0) {
                return true;
            }

            player.getItems().deleteItem(WRAITH_ESSENCE, essenceAmount);
            addCharge(player, weaponId, essenceAmount * WRAITH_CHARGE_PER_ESSENCE);

            int current = getCharge(player, weaponId);
            String name = weaponName(weaponId);
            player.sendMessage("Your " + name + " now has " + Misc.insertCommas(current)
                    + " charges (max " + MAX_WRAITH_CHARGE + ").");
            return true;
        }
        return false;
    }

    /** Adds {@code amount} charges to the specified weapon. */
    public static void addCharge(Player player, int weaponId, int amount) {
        switch (weaponId) {
            case WRAITH_SCYTHE:
                player.setWraithScytheCharge(Math.min(MAX_WRAITH_CHARGE, player.getWraithScytheCharge() + amount));
                break;
            case WRAITH_STAFF:
                player.setWraithStaffCharge(Math.min(MAX_WRAITH_CHARGE, player.getWraithStaffCharge() + amount));
                break;
            case WRAITH_BOW:
                player.setWraithBowCharge(Math.min(MAX_WRAITH_CHARGE, player.getWraithBowCharge() + amount));
                break;
        }
    }

    /** Consumes a single charge from the supplied weapon. */
    public static void consumeCharge(Player player, int weaponId) {
        switch (weaponId) {
            case WRAITH_SCYTHE:
                player.setWraithScytheCharge(Math.max(0, player.getWraithScytheCharge() - 1));
                break;
            case WRAITH_STAFF:
                player.setWraithStaffCharge(Math.max(0, player.getWraithStaffCharge() - 1));
                break;
            case WRAITH_BOW:
                player.setWraithBowCharge(Math.max(0, player.getWraithBowCharge() - 1));
                break;
        }
    }

    /** Returns remaining charges for a weapon. */
    public static int getCharge(Player player, int weaponId) {
        switch (weaponId) {
            case WRAITH_SCYTHE:
                return player.getWraithScytheCharge();
            case WRAITH_STAFF:
                return player.getWraithStaffCharge();
            case WRAITH_BOW:
                return player.getWraithBowCharge();
            default:
                return 0;
        }
    }

    public static boolean isWraithWeapon(int weaponId) {
        return WRAITH_WEAPONS.contains(weaponId);
    }

    private static String weaponName(int weaponId) {
        switch (weaponId) {
            case WRAITH_SCYTHE:
                return "Wraith Scythe";
            case WRAITH_STAFF:
                return "Wraith Staff";
            case WRAITH_BOW:
                return "Wraith Bow";
            default:
                return "Wraith weapon";
        }
    }
}
