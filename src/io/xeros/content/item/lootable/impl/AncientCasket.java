package io.xeros.content.item.lootable.impl;

import io.xeros.content.item.lootable.LootRarity;
import io.xeros.content.item.lootable.MysteryBoxLootable;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AncientCasket extends MysteryBoxLootable {

    private static final Map<LootRarity, List<GameItem>> items = new HashMap<>();

    /**
     * Constructs a new myster box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public AncientCasket(Player player) {
        super(player);
    }

    @Override
    public Map<LootRarity, List<GameItem>> getLoot() {
        return items;
    }

    @Override
    public int getItemId() {
        return 23071;
    }

    static {

        items.put(LootRarity.COMMON,
                Arrays.asList(
                        new GameItem(5554),  //Rogue Set
                        new GameItem(5553),  //Rogue Set
                        new GameItem(5555),  //Rogue Set
                        new GameItem(5556),  //Rogue Set
                        new GameItem(5557),  //Rogue Set

                        new GameItem(9032),  //Scarab's
                        new GameItem(9032),  //Scarab's
                        new GameItem(9030),  //Scarab's
                        new GameItem(9030),  //Scarab's
                        new GameItem(9028),  //Scarab's
                        new GameItem(9028),  //Scarab's

                        new GameItem(9042),  //Seals
                        new GameItem(9042),  //Seals
                        new GameItem(9040),  //Seals
                        new GameItem(9040)   //Seals
                ));

        items.put(LootRarity.UNCOMMON,
                Arrays.asList(
                        new GameItem(995, Misc.random(50_000, 150_000)),  // Coins
                        new GameItem(696, 4),   // 1M Upgrade Points
                        new GameItem(693, 10),  // 500k Upgrade Points
                        new GameItem(692,2),    // 50k Upgrade Points
                        new GameItem(691,1),    // 10k Upgrade Points
                        new GameItem(3694,4),   // Golden wool
                        new GameItem(2951,2),   // Golden needle
                        new GameItem(2948,3),   // Golden pot
                        new GameItem(2950,3),   // Golden feather

                        new GameItem(995, Misc.random(50_000, 150_000)),  // Coins
                        new GameItem(696, 4),   // 1M Upgrade Points
                        new GameItem(693, 10),  // 500k Upgrade Points
                        new GameItem(692,2),    // 50k Upgrade Points
                        new GameItem(691,1),    // 10k Upgrade Points
                        new GameItem(3694,4),   // Golden wool
                        new GameItem(2951,2),   // Golden needle
                        new GameItem(2948,3),   // Golden pot
                        new GameItem(2950,3),   // Golden feather

                        new GameItem(995, Misc.random(50_000, 150_000)),  // Coins
                        new GameItem(696, 4),  // 1M Upgrade Points
                        new GameItem(693, 10), // 500k Upgrade Points
                        new GameItem(692,2),   // 50k Upgrade Points
                        new GameItem(691,1),   // 10k Upgrade Points
                        new GameItem(3694,4),  // Golden wool
                        new GameItem(2951,2),  // Golden needle
                        new GameItem(2948,3),  // Golden pot
                        new GameItem(2950,3),  // Golden feather
                        new GameItem(22883,1), // Iasor seed
                        new GameItem(22885,1), // Kronos seed
                        new GameItem(22875,1)  // Hespori seed
                ));

        items.put(LootRarity.RARE,
                Arrays.asList(
                        new GameItem(3694,4),   // Golden wool
                        new GameItem(2951,2),   // Golden needle
                        new GameItem(2948,3),   // Golden pot
                        new GameItem(2950,3),   // Golden feather
                        new GameItem(23071,3),  // Ancient Casket
                        new GameItem(3694,4),   // Golden wool
                        new GameItem(2951,2),   // Golden needle
                        new GameItem(2948,3),   // Golden pot
                        new GameItem(2950,3),   // Golden feather
                        new GameItem(23071,3),  // Ancient Casket
                        new GameItem(18),  // Magic gold feather
                        new GameItem(3694,4),   // Golden wool
                        new GameItem(2951,2),   // Golden needle
                        new GameItem(2948,3),   // Golden pot
                        new GameItem(2950,3),   // Golden feather
                        new GameItem(23071,3),  // Ancient Casket
                        new GameItem(18),  // Magic gold feather
                        new GameItem(8167,1),   // Nomad chest
                        new GameItem(13346,1),  // Umb
                        new GameItem(27369)   // Ancient key
                ));
    }

}
