import io.xeros.content.skills.slayer.DemonHunterPerks;
import io.xeros.content.skills.slayer.DemonSlayerMaster;
import io.xeros.content.skills.Skill;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.npc.NPC;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DemonHunterPerksTest {

    @Test
    void unlocksBasedOnLevel() {
        Player player = mock(Player.class);
        when(player.getLevel(Skill.DEMON_HUNTER)).thenReturn(40);

        assertTrue(DemonHunterPerks.has(player, DemonHunterPerks.Perk.DROP_RATE));
        assertTrue(DemonHunterPerks.has(player, DemonHunterPerks.Perk.FAST_TRACK));
        assertFalse(DemonHunterPerks.has(player, DemonHunterPerks.Perk.MARK_MASTER));
    }

    @Test
    void damageBonusMatchesLevelAndTask() {
        Player player = mock(Player.class);
        NPC npc = mock(NPC.class);

        DemonSlayerMaster.BossTier boss = DemonSlayerMaster.BossTier.GENERAL_GRAARDOR;
        DemonSlayerMaster.DemonSlayerTask task = new DemonSlayerMaster.DemonSlayerTask(boss, 5);

        when(player.getDemonHunterTask()).thenReturn(Optional.of(task));
        when(player.getLevel(Skill.DEMON_HUNTER)).thenReturn(50);
        when(npc.getNpcId()).thenReturn(boss.getNpcId());

        assertEquals(0.50, DemonHunterPerks.getDamageBonus(player, npc), 0.0001);

        when(npc.getNpcId()).thenReturn(999);
        assertEquals(0.0, DemonHunterPerks.getDamageBonus(player, npc), 0.0001);
    }
}
