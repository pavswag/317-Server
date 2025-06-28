package io.xeros.content.wogw;

import com.google.common.collect.Lists;
import io.xeros.Server;
import io.xeros.model.entity.player.Player;
import io.xeros.sql.wogw.GetRecentContributionsSqlQuery;
import io.xeros.sql.wogw.GetTopContributionsSqlQuery;
import io.xeros.util.Misc;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WogwContributeInterface {

    public static final int INTERFACE_ID = 22931;
    public static final int WOGW_BONUS_BUTTON_SELECTION = 1373;
    private static final int CONTRIBUTE_BUTTON = 22940;
    private static final int TOP_CONTRIBUTOR_TEXT = 22935;
    private static final int RECENT_CONTRIBUTORS_CONTAINER = 22937;

    private final Player player;
    @Getter
    private WogwInterfaceButton selectedButton = WogwInterfaceButton.EXPERIENCE_BOOST;

    public WogwContributeInterface(Player player) {
        this.player = player;
    }

    public void open() {
        if (player.hitDatabaseRateLimit(true))
            return;

        player.addQueuedAction(plr -> {
            updateConfig();
            player.getPA().sendString(TOP_CONTRIBUTOR_TEXT, "N/A");

            player.getPA().sendStringContainer(RECENT_CONTRIBUTORS_CONTAINER, Lists.newArrayList("N/A"));


            Arrays.stream(WogwInterfaceButton.values())
                    .forEach(button ->
                            player.getPA().sendString(button.getCoinsTextId(),
                                    String.format("%s%s</col>/%s%s",
                                            Misc.getCoinColour(button.getCurrentCoins()),
                                            Misc.formatCoins(button.getCurrentCoins()),
                                            Misc.getCoinColour(button.getCoinsRequired()),
                                            Misc.formatCoins(button.getCoinsRequired())
                                    )
                            )
                    );

            player.getPA().showInterface(INTERFACE_ID);
        });
    }

    private void updateConfig() {
        player.getPA().sendConfig(WOGW_BONUS_BUTTON_SELECTION, selectedButton.ordinal());
    }

    public boolean clickButton(int buttonId) {
        if (buttonId == CONTRIBUTE_BUTTON) {
            player.getPA().sendEnterAmount("Donate to " + selectedButton.toString(), (p, a) -> Wogw.donate(player, a, -1, -1));
            return true;
        }

        Optional<WogwInterfaceButton> button = Arrays.stream(WogwInterfaceButton.values()).filter(it -> it.getButtonId() == buttonId).findAny();
        if (button.isPresent()) {
            selectedButton = button.get();
            updateConfig();
            return true;
        }
        return false;
    }

}
