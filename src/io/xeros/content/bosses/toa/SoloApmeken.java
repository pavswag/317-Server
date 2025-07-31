package io.xeros.content.bosses.toa;

import io.xeros.content.minigames.TOA.bosses.Apmeken;
import io.xeros.content.instances.InstancedArea;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.content.item.lootable.impl.TombsOfAmascutChest;
import io.xeros.model.items.GameItem;
import io.xeros.util.Misc;
import java.util.List;

public class SoloApmeken extends Apmeken {
    public SoloApmeken(InstancedArea area) {
        super(area);
    }

    @Override
    public NPC provideRespawnInstance() {
        return new SoloApmeken(getInstance());
    }

    @Override
    public void onDeath() {
        InstancedArea inst = getInstance();
        if (inst != null) {
            Player rareWinner = Misc.random(inst.getPlayers());
            for (Player plr : inst.getPlayers()) {
                List<GameItem> rewards = TombsOfAmascutChest.getRandomItems(plr.equals(rareWinner), 1);
                TombsOfAmascutChest.rewardItems(plr, rewards);
            }
        }
    }
}
