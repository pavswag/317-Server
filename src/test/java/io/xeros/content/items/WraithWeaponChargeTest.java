package io.xeros.content.items;

import io.xeros.model.entity.player.Player;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class WraithWeaponChargeTest {

    @Test
    void addChargeCapsAtMax() {
        Player player = new Player(null);
        WraithWeaponCharge.addCharge(player, WraithWeaponCharge.WRAITH_SCYTHE, WraithWeaponCharge.MAX_WRAITH_CHARGE + 5000);
        assertEquals(WraithWeaponCharge.MAX_WRAITH_CHARGE, player.getWraithScytheCharge());
    }

    @Test
    void consumeCharge() {
        Player player = new Player(null);
        player.setWraithStaffCharge(1);
        WraithWeaponCharge.consumeCharge(player, WraithWeaponCharge.WRAITH_STAFF);
        assertEquals(0, player.getWraithStaffCharge());
    }
}
