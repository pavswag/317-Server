package io.xeros.content.item.lootable;

import io.xeros.content.item.lootable.impl.*;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Shows {@link Lootable} tables.
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
public class LootableInterface {

    private static final int INTERFACE_ID = 44_942;

    // Common table
    private static final int COMMON_SCROLLABLE_INTERFACE_ID = 45143;
    private static final int COMMON_INVENTORY_INTERFACE_ID = 45144;

    // Rare table
    private static final int RARE_SCROLLABLE_INTERFACE_ID = 45183;
    private static final int RARE_INVENTORY_INTERFACE_ID = 45184;

    private static final int VIEW_TABLE_BUTTON_START_ID = 175211;

    private static final int CURRENT_VIEW_CONFIG_ID = 1354;

    private static final int[] BUTTONS = {175211, 175214, 175217, 175220, 175223, 175226, 175229,
            175232, 175235, 175238, 175241, 175244, 175247,
            175250, 175253, 176000, 176003, 176006, 176009,
            176012, 176015, 176018, 176021, 176024, 176027,
            176030, 176033, 176036, 176039, 176042, 176045,
            176048, 176051};

    private enum LootableView {
        MINI_MYSTERY_BOX(new MiniNormalMysteryBox(null)),
        MINI_SUPER_MYSTERY_BOX(new MiniSmb(null)),
        MINI_ULTRA_MYSTERY_BOX(new MiniUltraBox(null)),
        MINI_COX_BOX(new MiniCoxBox(null)),
        MINI_TOB_BOX(new MiniTobBox(null)),
        MINI_ARBO_BOX(new MiniArboBox(null)),
        MINI_DONO_BOX(new MiniDonoBox(null)),
        MYSTERY_BOX(new NormalMysteryBox(null)),
        SUPER_MYSTERY_BOX(new SuperMysteryBox(null)),
        ULTRA_MYSTERY_BOX(new UltraMysteryBox(null)),
        COX_BOX(new CoxBox(null)),
        TOB_BOX(new TobBox(null)),
        ARBO_BOX(new ArboBox(null)),
        FOE_MYSTERY_CHEST(new FoeMysteryBox(null)),
        DIVISION_F2P(new f2pDivisionBox(null)),
        DIVISION_P2P(new p2pDivisionBox(null)),
        DONO_BOX(new DonoBox(null)),
        VOTE_MYSTERY_BOX(new VoteMysteryBox()),
        Cosmetic_Box(new CosmeticBox(null)),
        PVM_CASKET(new PvmCasket()),
        SLAYER_MYSTERY_CHEST(new SlayerMysteryBox(null)),
        RAIDS(new RaidsChestRare()),
        TOB(new TheatreOfBloodChest()),
        ARBGRAVE_CHEST(new ArbograveChest()),
        VOTE_KEY(new VoteChest()),
        CRYSTAL_CHEST(new CrystalChest()),
        BRIMSTONE_KEY(new KonarChest()),
        HESPORI_KEY(new HesporiChest()),
        UNBEARABLE_KEY(new UnbearableChest()),
        LARRANS_CHEST(new LarransChest()),
        SERENS_KEY(new SerenChest()),
        WILDYMAN_CHEST(new AOEChest()),
        HUNNLEFS_KEY(new HunllefChest()),
        ;

        private List<GameItem> common;
        private List<GameItem> rare;

        LootableView(Lootable lootable) {
            this.common = new ArrayList<>();
            this.rare = new ArrayList<>();

            List<GameItem> addingCommon = lootable.getLoot().get(LootRarity.COMMON);
            List<GameItem> addingUncommon = lootable.getLoot().get(LootRarity.UNCOMMON);
            List<GameItem> addingRare = lootable.getLoot().get(LootRarity.RARE);
            List<GameItem> addingVery_rare = lootable.getLoot().get(LootRarity.VERY_RARE);

            if (addingCommon != null)
                common.addAll(lootable.getLoot().get(LootRarity.COMMON));
            if (addingUncommon != null)
                common.addAll(lootable.getLoot().get(LootRarity.UNCOMMON));
            if (addingVery_rare != null)
                rare.addAll(lootable.getLoot().get(LootRarity.VERY_RARE));
            if (addingRare != null)
                rare.addAll(lootable.getLoot().get(LootRarity.RARE));

            common = common.stream().filter(Misc.distinctByKey(GameItem::getId)).collect(Collectors.toList());
            rare = rare.stream().filter(Misc.distinctByKey(GameItem::getId)).collect(Collectors.toList());

            common = common.stream().filter(gameItem -> gameItem.getId() != 11681).collect(Collectors.toList());
            rare = rare.stream().filter(gameItem -> gameItem.getId() != 11681).collect(Collectors.toList());

            common = Collections.unmodifiableList(common);
            rare = Collections.unmodifiableList(rare);
        }

        public int getButtonId() {
            return VIEW_TABLE_BUTTON_START_ID + (ordinal() * 5);
        }
    }

    public static void openInterface(Player player) {
        open(player, LootableView.MINI_MYSTERY_BOX);
    }

    private static void open(Player player, LootableView view) {
        player.getPA().sendConfig(CURRENT_VIEW_CONFIG_ID, view.ordinal());
        player.getPA().resetScrollBar(COMMON_SCROLLABLE_INTERFACE_ID);
        player.getPA().resetScrollBar(RARE_SCROLLABLE_INTERFACE_ID);
        player.getItems().sendItemContainer(COMMON_INVENTORY_INTERFACE_ID, view.common);
        player.getItems().sendItemContainer(RARE_INVENTORY_INTERFACE_ID, view.rare);
        player.getPA().showInterface(INTERFACE_ID);
    }

    public static boolean button(Player player, int buttonId) {
        for (int index = 0; index < BUTTONS.length; index++) {
            if (buttonId == BUTTONS[index]) {
                open(player, LootableView.values()[index]);
                return true;
            }
        }

        return false;
    }
}
