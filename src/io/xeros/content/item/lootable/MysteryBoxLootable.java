package io.xeros.content.item.lootable;

import io.xeros.Configuration;
import io.xeros.model.definitions.ItemDef;
import io.xeros.model.entity.player.Player;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;

import java.util.List;
import java.util.Random;

public abstract class MysteryBoxLootable implements Lootable {

    public abstract int getItemId();

    /**
     * The player object that will be triggering this event
     */
    private final Player player;

    /**
     * Constructs a new mystery box to handle item receiving for this player and this player alone
     *
     * @param player the player
     */
    public MysteryBoxLootable(Player player) {
        this.player = player;
    }

    /**
     * Can the player open the mystery box
     */
    public boolean canMysteryBox = true;

    /**
     * The prize received
     */
    private int mysteryPrize;

    private int mysteryAmount;

    private int spinNum;

    /**
     * The chance to obtain the item
     */
    private double random;
    private boolean active;
    private final int INTERFACE_ID = 47000;
    private final int ITEM_FRAME = 47101;

    public boolean isActive() {
        return active;
    }

    public void spin() {
        // Server side checks for spin
        if (!canMysteryBox) {
            player.sendMessage("Please finish your current spin.");
            return;
        }
        if (!player.getItems().playerHasItem(getItemId())) {
            player.sendMessage("You require a mystery box to do this.");
            return;
        }

        // Delete box
        player.getItems().deleteItem(getItemId(), 1);
        // Initiate spin
        player.sendMessage(":resetBox");
        for (int i=0; i<66; i++) {
            player.getPA().mysteryBoxItemOnInterface(-1, 1, ITEM_FRAME, i);
        }
        spinNum = 0;
        player.sendMessage(":spin");
        process();
    }

    public void process() {
        player.getPA().closeAllWindows();
        mysteryPrize = -1;
        mysteryAmount = -1;
        canMysteryBox = false;
        active = true;
        setMysteryPrize();

        if (mysteryPrize == -1) {
            return;
        }
        // Send items to interface
        // Move non-prize items client side if you would like to reduce server load
        if (spinNum == 0) {
            for (int i=0; i<66; i++){
                MysteryBoxRarity notPrizeRarity = MysteryBoxRarity.values()[new Random().nextInt(MysteryBoxRarity.values().length)];
                GameItem NotPrize =Misc.getRandomItem(getLoot().get(notPrizeRarity.getLootRarity()));
                final int NOT_PRIZE_ID = NotPrize.getId();
                sendItem(i, 55, mysteryPrize, NOT_PRIZE_ID,1);
            }
        } else {
            for (int i=spinNum*50 + 16; i<spinNum*50 + 66; i++){
                MysteryBoxRarity notPrizeRarity = MysteryBoxRarity.values()[new Random().nextInt(MysteryBoxRarity.values().length)];
                final int NOT_PRIZE_ID = Misc.getRandomItem(getLoot().get(notPrizeRarity.getLootRarity())).getId();
                sendItem(i, (spinNum+1)*50 + 5, mysteryPrize, NOT_PRIZE_ID, mysteryAmount);
            }
        }
        player.getPA().showInterface(INTERFACE_ID);
        spinNum++;
    }

    public void setMysteryPrize() {
        double[] rarityProbabilities = {0.02, 0.12, 0.39, 0.90}; // Very Rare, Rare, Uncommon, Common, scrap = remainder of common - 100

        double random = Math.random();

        List<GameItem> itemList = null;

        if (getItemId() == 19897) {
            if (random < rarityProbabilities[0] && (Configuration.GLOBAL_BOX_COUNT == 0)) {
                itemList = getLoot().get(MysteryBoxRarity.VERY_RARE.getLootRarity());
            } else if (random < rarityProbabilities[1]) {
                itemList = getLoot().get(MysteryBoxRarity.RARE.getLootRarity());
            } else if (random < rarityProbabilities[2]) {
                itemList = getLoot().get(MysteryBoxRarity.UNCOMMON.getLootRarity());
            } else if (random < rarityProbabilities[3]) {
                itemList = getLoot().get(MysteryBoxRarity.COMMON.getLootRarity());
            }

            if (itemList != null) {
                GameItem item = Misc.getRandomItem(itemList);
                mysteryPrize = item.getId();
                mysteryAmount = item.getAmount();
            } else {
                mysteryPrize = 11681;
                mysteryAmount = 1;
            }

            Configuration.GLOBAL_BOX_COUNT--;
            if (Configuration.GLOBAL_BOX_COUNT == 0) {
                Configuration.GLOBAL_BOX_COUNT = 500;
            }
        } else {
            if (random < rarityProbabilities[0]) {
                itemList = getLoot().get(MysteryBoxRarity.VERY_RARE.getLootRarity());
            } else if (random < rarityProbabilities[1]) {
                itemList = getLoot().get(MysteryBoxRarity.RARE.getLootRarity());
            } else if (random < rarityProbabilities[2]) {
                itemList = getLoot().get(MysteryBoxRarity.UNCOMMON.getLootRarity());
            } else if (random < rarityProbabilities[3]) {
                itemList = getLoot().get(MysteryBoxRarity.COMMON.getLootRarity());
            }

            if (itemList != null) {
                GameItem item = Misc.getRandomItem(itemList);
                mysteryPrize = item.getId();
                mysteryAmount = item.getAmount();
            } else {
                mysteryPrize = 11681;
                mysteryAmount = 1;
            }
        }
    }

    public void sendItem(int i, int prizeSlot, int PRIZE_ID, int NOT_PRIZE_ID, int amount) {
        if (i == prizeSlot) {
            player.getPA().mysteryBoxItemOnInterface(PRIZE_ID, amount, ITEM_FRAME, i);
        }
        else {
            player.getPA().mysteryBoxItemOnInterface(NOT_PRIZE_ID, amount, ITEM_FRAME, i);
        }
    }

    public void openInterface() {

        player.boxCurrentlyUsing = getItemId();
        for (int i = 0; i < 66; i++){
            player.getPA().mysteryBoxItemOnInterface(-1, 1, ITEM_FRAME, i);
        }
        spinNum = 0;
        player.getPA().sendString(ItemDef.forId(getItemId()).getName(), 47002);
        player.getPA().showInterface(INTERFACE_ID);
    }

    public void canMysteryBox() {
        canMysteryBox = true;

    }

    public void quickOpen() {

        if (player.getUltraInterface().isActive() ||
                player.getSuperBoxInterface().isActive() ||
                player.getNormalBoxInterface().isActive() ||
                player.getFoeInterface().isActive() ||
                player.getChristmasInterface().isActive() ||
                player.getF2pDivisionBox().isActive() ||
                player.getP2pDivisionBox().isActive() ||
                player.getAncientCasket().isActive()||
                player.getCoxBox().isActive()||
                player.getArboBox().isActive()||
                player.getTobBox().isActive()||
                player.getDonoBox().isActive()||
                player.getCosmeticBox().isActive()||
                player.getMiniArboBox().isActive()||
                player.getMiniCoxBox().isActive()||
                player.getMiniDonoBox().isActive()||
                player.getMiniNormalMysteryBox().isActive()||
                player.getMiniSmb().isActive()||
                player.getMiniTobBox().isActive()||
                player.getMiniUltraBox().isActive()
        ) {
            player.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");

            return;
        }
        if (!(player.getSuperMysteryBox().canMysteryBox) || !(player.getNormalMysteryBox().canMysteryBox) ||
                !(player.getUltraMysteryBox().canMysteryBox) || !(player.getFoeMysteryBox().canMysteryBox) ||
                !(player.getYoutubeMysteryBox().canMysteryBox)||
                !(player.getChristmasBox().canMysteryBox)||
                !(player.getF2pDivisionBox().canMysteryBox)||
                !(player.getP2pDivisionBox().canMysteryBox)||
                !(player.getAncientCasket().canMysteryBox)||
                !(player.getCoxBox().canMysteryBox)||
                !(player.getArboBox().canMysteryBox)||
                !(player.getTobBox().canMysteryBox)||
                !(player.getDonoBox().canMysteryBox)||
                !(player.getCosmeticBox().canMysteryBox)||
                !(player.getMiniArboBox().canMysteryBox)||
                !(player.getMiniCoxBox().canMysteryBox)||
                !(player.getMiniDonoBox().canMysteryBox)||
                !(player.getMiniNormalMysteryBox().canMysteryBox)||
                !(player.getMiniSmb().canMysteryBox)||
                !(player.getMiniTobBox().canMysteryBox)||
                !(player.getMiniUltraBox().canMysteryBox)) {
            player.getPA().showInterface(47000);
            player.sendMessage("@red@[WARNING] @blu@Please do not interrupt or you @red@WILL@blu@ lose items! @red@NO REFUNDS");
            return;
        }

        int amount = player.getItems().getInventoryCount(getItemId());

        if (amount > 1000) {
            amount = 1000;
        }

        for (int i = 0; i < amount; i++) {
            if (player.getItems().playerHasItem(getItemId(), 1)) {
                player.getItems().deleteItem(getItemId(), 1);
                setMysteryPrize();
                roll(player);
            } else {
                player.sendMessage("@blu@You have used your last mystery box.");
                break;
            }
        }
    }

    @Override
    public void roll(Player player) {

        if (mysteryPrize == -1) {
            canMysteryBox = true;
            player.getNormalMysteryBox().canMysteryBox();
            player.getUltraMysteryBox().canMysteryBox();
            player.getSuperMysteryBox().canMysteryBox();
            player.getFoeMysteryBox().canMysteryBox();
            player.getYoutubeMysteryBox().canMysteryBox();
            player.getChristmasBox().canMysteryBox();
            player.getF2pDivisionBox().canMysteryBox();
            player.getP2pDivisionBox().canMysteryBox();
            player.getAncientCasket().canMysteryBox();
            player.getArboBox().canMysteryBox();
            player.getCoxBox().canMysteryBox();
            player.getTobBox().canMysteryBox();
            player.getDonoBox().canMysteryBox();
            player.getCosmeticBox().canMysteryBox();
            player.getMiniArboBox().canMysteryBox();
            player.getMiniCoxBox().canMysteryBox();
            player.getMiniDonoBox().canMysteryBox();
            player.getMiniNormalMysteryBox().canMysteryBox();
            player.getMiniSmb().canMysteryBox();
            player.getMiniTobBox().canMysteryBox();
            player.getMiniUltraBox().canMysteryBox();
            return;
        }
        int amt = (player.getPerkSytem().gameItems.stream().anyMatch(item -> item.getId() == 33113) && Misc.random(0, 100) < 10 ? mysteryAmount * 2 : mysteryAmount);

        if (player.hasFollower && player.petSummonId == 33067 && Misc.random(0, 100) >= 90 || player.getItems().playerHasItem(33067) && Misc.random(0, 100) >= 90) {
            amt *= 2;
        }

        if (player.EliteCentBoost > 0 && Misc.random(0, 100) >= 90) {
            amt *= 2;
        }

        player.getItems().addItemUnderAnyCircumstance(mysteryPrize, amt);
/*        if (getLoot().get(MysteryBoxRarity.RARE.getLootRarity()).contains(new GameItem(mysteryPrize))) { //Mystery box item to not Announce
            if (mysteryPrize != 11681 && mysteryPrize != 6679 && mysteryPrize != 6677 && mysteryPrize != 6678 && mysteryPrize != 12585
                    && mysteryPrize != 19895 && mysteryPrize != 6680 && mysteryPrize != 19887)  {
                String name = ItemDef.forId(mysteryPrize).getName();
                String itemName = ItemDef.forId(getItemId()).getName();
                PlayerHandler.executeGlobalMessage("[<col=CC0000>" + itemName + "</col>] <col=255>"
                        + player.getDisplayName()
                        + "</col> hit the jackpot and got a <col=CC0000>"+name+"</col>!");
            }
        }

        if (mysteryPrize == 20659) {
            String name = ItemDef.forId(mysteryPrize).getName();
            String itemName = ItemDef.forId(getItemId()).getName();
            PlayerHandler.executeGlobalMessage("[<col=CC0000>" + itemName + "</col>] <col=255>"
                    + player.getDisplayName()
                    + "</col> hit the jackpot and got a <col=CC0000>"+name+"</col>!");
        }*/
        active = false;
        player.inDonatorBox = true;

        canMysteryBox = true;

        mysteryPrize = -1;
        player.getNormalMysteryBox().canMysteryBox();
        player.getUltraMysteryBox().canMysteryBox();
        player.getSuperMysteryBox().canMysteryBox();
        player.getFoeMysteryBox().canMysteryBox();
        player.getYoutubeMysteryBox().canMysteryBox();
        player.getChristmasBox().canMysteryBox();
        player.getF2pDivisionBox().canMysteryBox();
        player.getP2pDivisionBox().canMysteryBox();
        player.getAncientCasket().canMysteryBox();
        player.getArboBox().canMysteryBox();
        player.getCoxBox().canMysteryBox();
        player.getTobBox().canMysteryBox();
        player.getDonoBox().canMysteryBox();
        player.getCosmeticBox().canMysteryBox();
        player.getMiniArboBox().canMysteryBox();
        player.getMiniCoxBox().canMysteryBox();
        player.getMiniDonoBox().canMysteryBox();
        player.getMiniNormalMysteryBox().canMysteryBox();
        player.getMiniSmb().canMysteryBox();
        player.getMiniTobBox().canMysteryBox();
        player.getMiniUltraBox().canMysteryBox();
    }
}
