package io.xeros.model.entity.player.mode;

import io.xeros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.xeros.model.entity.player.Player;

public class Wildyman extends IronmanMode {
    public Wildyman(ModeType type) {
        super(type);
    }

    @Override
    public boolean isTradingPermitted(Player player, Player other) {
        return other.getMode().getType().equals(ModeType.WILDYMAN) ||
                other.getMode().getType().equals(ModeType.HARDCORE_WILDYMAN);
    }
    @Override
    public double getDropModifier() {
        return -0.10;
    }


    @Override
    public boolean isItemScavengingPermitted() {
        return true;
    }

    @Override
    public boolean isShopAccessible(int shopId) {
        if (shopId == FireOfExchangeBurnPrice.SHOP_ID) {
            return true;
        }
        switch (shopId) {
            case 112:
            case 10:
            case 122:
            case 23:
            case 118:
            case 77:
            case 121:
            case 191:
            case 20:
            case 195:
            case 41:
            case 171:
            case 197:
            case 16:
            case 22:
            case 2:
            case 131:
            case 80:
            case 196:
            case 21:
            case 119:
            case 192:
            case 179:
            case 17:
            case 199:
                return true;

        }
        return false;
    }

    @Override
    public boolean isItemPurchasable(int shopId, int itemId) {
        switch (shopId) {
            case 112:
            case 41:
            case 10:
            case 197:
            case 118:
            case 23:
            case 21:
            case 196:
            case 191:
            case 77:
            case 131:
            case 16:
            case 121:
            case 122:
            case 2:
            case 20:
            case 171:
            case 195:
            case 22:
            case 80:
            case 119:
            case 192:
            case 179:
            case 17:
            case 199:
                return true;
        }
        return false;
    }
}
