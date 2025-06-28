package io.xeros.model.entity.player.mode;

import io.xeros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.xeros.model.entity.player.Player;

public class HardcoreWildyman extends IronmanMode {
    public HardcoreWildyman(ModeType type) {
        super(type);
    }

    @Override
    public boolean isTradingPermitted(Player player, Player other) {
        return other.getMode().getType().equals(ModeType.WILDYMAN) || other.getMode().getType().equals(ModeType.HARDCORE_WILDYMAN);
    }
    @Override
    public double getDropModifier() {
        return -0.25;
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
        return switch (shopId) {
            case 112, 77, 131, 121, 2, 20, 191, 10, 41, 16, 171, 21, 197, 122, 22, 23, 118, 196, 80, 119, 195, 192, 179,
                 17, 199 -> true;
            default -> false;
        };
    }

    @Override
    public boolean isItemPurchasable(int shopId, int itemId) {
        return switch (shopId) {
            case 112, 179, 17, 197, 21, 41, 23, 122, 131, 77, 118, 196, 191, 121, 2, 20, 195, 10, 171, 22, 16, 80, 119,
                 192, 199 -> true;
            default -> false;
        };
    }
}
