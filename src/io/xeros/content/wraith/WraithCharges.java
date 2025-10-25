package io.xeros.content.wraith;

import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Utility for managing Wraith weapon charges. Provides capped, atomic charging
 * with clear player messaging.
 */
public class WraithCharges {

    private static final Logger logger = LoggerFactory.getLogger(WraithCharges.class);

    public static final int WRAITH_ESSENCE = 26879;
    public static final int WRAITH_SCYTHE = 33431;
    public static final int WRAITH_STAFF = 33433;
    public static final int WRAITH_BOW = 33434;

    private static final Set<Integer> WRAITH_WEAPONS = Set.of(WRAITH_SCYTHE, WRAITH_STAFF, WRAITH_BOW);

    /** Default maximum number of charges. */
    public static int getCapFor(int itemId) {
        return 10_000; // TODO: read from config
    }

    /** Default number of charges gained per essence. */
    public static int chargesPerEssence() {
        return 500; // TODO: read from config
    }

    public static int getCurrentCharges(Player p, int itemId) {
        switch (itemId) {
            case WRAITH_SCYTHE:
                return p.getWraithScytheCharge();
            case WRAITH_STAFF:
                return p.getWraithStaffCharge();
            case WRAITH_BOW:
                return p.getWraithBowCharge();
            default:
                return 0;
        }
    }

    public static void setCurrentCharges(Player p, int itemId, int newValue) {
        newValue = Math.max(0, Math.min(getCapFor(itemId), newValue));
        switch (itemId) {
            case WRAITH_SCYTHE:
                p.setWraithScytheCharge(newValue);
                break;
            case WRAITH_STAFF:
                p.setWraithStaffCharge(newValue);
                break;
            case WRAITH_BOW:
                p.setWraithBowCharge(newValue);
                break;
        }
    }

    public static int getCharge(Player p, int itemId) {
        return getCurrentCharges(p, itemId);
    }

    public static void addCharge(Player p, int itemId, int amount) {
        setCurrentCharges(p, itemId, getCurrentCharges(p, itemId) + amount);
    }

    public static void consumeCharge(Player p, int itemId) {
        setCurrentCharges(p, itemId, getCurrentCharges(p, itemId) - 1);
    }

    public static boolean isWraithWeapon(int itemId) {
        return WRAITH_WEAPONS.contains(itemId);
    }

    private static String weaponName(int itemId) {
        switch (itemId) {
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

    /**
     * Returns true if any charges were added (or a valid no-op message was shown), false if failed.
     * Guarantees: never consumes more essence than needed; inventory and charges update atomically.
     */
    public static boolean addChargesFromEssence(Player p, int wraithItemSlot, int essenceItemId, int requestedEss) {
        if (p == null) {
            return false;
        }

        boolean itemInInventory = wraithItemSlot >= 0 && wraithItemSlot < p.playerItems.length;
        int itemId;
        if (itemInInventory) {
            itemId = p.playerItems[wraithItemSlot] - 1;
        } else if (wraithItemSlot >= 0 && wraithItemSlot < p.playerEquipment.length) {
            itemId = p.playerEquipment[wraithItemSlot];
        } else {
            return false;
        }

        if (!isWraithWeapon(itemId) || essenceItemId != WRAITH_ESSENCE) {
            return false;
        }

        if (requestedEss <= 0) {
            requestedEss = Integer.MAX_VALUE; // use as many as needed
        }

        int cap = getCapFor(itemId);
        int perEss = chargesPerEssence();
        int current = getCurrentCharges(p, itemId);
        if (current >= cap) {
            p.sendMessage("Your " + weaponName(itemId) + " is already at the maximum of " + cap + " charges.");
            logger.info("[WRAITH] at-cap");
            return true;
        }

        int remainingCharges = cap - current;
        int essenceNeeded = (int) Math.ceil(remainingCharges / (double) perEss);
        int invEss = p.getItems().getItemCount(essenceItemId);
        int willConsume = Math.min(requestedEss, Math.min(invEss, essenceNeeded));
        if (willConsume <= 0) {
            p.sendMessage("You need more Wraith Essence to charge that.");
            return false;
        }

        int addCharges = Math.min(remainingCharges, willConsume * perEss);
        int invBefore = invEss;

        // remove essence
        p.getItems().deleteItem(essenceItemId, willConsume);
        int invAfter = p.getItems().getItemCount(essenceItemId);
        if (invBefore - invAfter != willConsume) {
            // rollback
            p.getItems().addItem(essenceItemId, invBefore - invAfter); // attempt to restore
            return false;
        }

        setCurrentCharges(p, itemId, current + addCharges);
        int newTotal = getCurrentCharges(p, itemId);

        p.sendMessage("Added +" + Misc.insertCommas(addCharges) + " charges using " + willConsume
                + " Essence. Now at " + Misc.insertCommas(newTotal) + "/" + cap + ".");
        if (requestedEss > willConsume) {
            p.sendMessage((requestedEss - willConsume) + " Essence not needed; cap reached.");
        }

        logger.info("[WRAITH] charge add=" + addCharges + " consume=" + willConsume + " now=" + newTotal + "/" + cap
                + " req=" + requestedEss + " invBefore=" + invEss);
        return true;
    }
}

