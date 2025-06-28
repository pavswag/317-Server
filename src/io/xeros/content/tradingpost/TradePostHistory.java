package io.xeros.content.tradingpost;

import io.xeros.model.items.GameItem;
import lombok.Getter;

/**
 * @author ArkCane
 * @social Discord: ArkCane
 * Website: www.arkcane.net
 * @since 09/04/2024
 */
public class TradePostHistory {

    @Getter
    private final String buyer;
    @Getter
    private final String seller;
    @Getter
    private final GameItem item;
    @Getter
    private final long timestamp;
    @Getter
    private final boolean nomad;
    @Getter
    private final int cost;


    public TradePostHistory(String buyer, String seller, GameItem item, long timestamp, boolean nomad, int cost) {
        this.buyer = buyer;
        this.seller = seller;
        this.item = item;
        this.timestamp = timestamp;
        this.nomad = nomad;
        this.cost = cost;
    }
}
