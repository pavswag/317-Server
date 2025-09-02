package io.xeros.content.wraith;

import io.xeros.model.entity.player.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WraithChargesTest {

    @Test
    void addChargeCapsAtMax() {
        Player player = new Player(null);
        WraithCharges.addCharge(player, WraithCharges.WRAITH_SCYTHE, WraithCharges.getCapFor(WraithCharges.WRAITH_SCYTHE) + 5000);
        assertEquals(WraithCharges.getCapFor(WraithCharges.WRAITH_SCYTHE), player.getWraithScytheCharge());
    }

    @Test
    void consumeCharge() {
        Player player = new Player(null);
        player.setWraithStaffCharge(1);
        WraithCharges.consumeCharge(player, WraithCharges.WRAITH_STAFF);
        assertEquals(0, player.getWraithStaffCharge());
    }
}
