package io.xeros.content.item.lootable.impl;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.MysteryBoxLootable;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class p2pDivisionBox  extends MysteryBoxLootable {
    /**
     * A map containing a List of {@link GameItem}'s that contain items relevant to their LootRarity.
     */
    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    static {

        items.put(LootRarity.COMMON,
                Arrays.asList(
                        new GameItem(6679, 150),
                        new GameItem(6677, 75),
                        new GameItem(6678, 20),
                        new GameItem(12585 ,2),
                        new GameItem(19895,2),
                        new GameItem(6680,2)
                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(21295),  // Infernal cape
                        new GameItem(12817),  // Elysian shield
                        new GameItem(24664),  // Twisted ancestral hat
                        new GameItem(24666),  // Twisted ancestral robe top
                        new GameItem(24668),  // Twisted ancestral robe bottoms
                        new GameItem(20370),  // Bandos godsword or
                        new GameItem(20374),  // Zamarok godsword or
                        new GameItem(20372),  // Saradomin godsword or
                        new GameItem(20368),  // Armadyl godsword or
                        new GameItem(25063),  // Infernal pick or
                        new GameItem(25066),  // Infernal axe or
                        new GameItem(25059)   // Infernal harpoon or
                ));

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(8167,1),   // Nomad chest
                        new GameItem(13346,4),  // Umb
                        new GameItem(6831,2),   // F2p division box

                        new GameItem(10559),  // Healer icon

                        new GameItem(24511),  // harmonised orb
                        new GameItem(24514),  // Volatile orb
                        new GameItem(24517),  // Eldritch orb

                        new GameItem(22883,3),  // Iasor seed
                        new GameItem(22885,3),  // Kronos seed
                        new GameItem(22875,3)   // Hespori seed
                ));

        items.put(LootRarity.VERY_RARE,
                Arrays.asList(
                        new GameItem(26382),  // Torva helm
                        new GameItem(26384),  // Torva body
                        new GameItem(26386),  // Torva legs
                        new GameItem(25736),  // Holy scythe

                        new GameItem(27226),  // Masori mask
                        new GameItem(27229),  // Masori body
                        new GameItem(27232),  // Masori chaps
                        new GameItem(20997),  // Twisted bow

                        new GameItem(26374),  // Zaryte c'bow

                        new GameItem(25731),  // Holy sang staff
                        new GameItem(24422),  // Nightmare staff

                        new GameItem(20787),  // Row i4

                        new GameItem(26545),  // Hope(Random Perk)

                        new GameItem(30014)   // K'klik pet





                ));
    }

    /**
     * Constructs a new myster box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public p2pDivisionBox(Player player) {
        super(player);
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    @Override
    public int getItemId() {
        return 6829;
    }
}
