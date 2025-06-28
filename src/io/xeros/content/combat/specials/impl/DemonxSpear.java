package io.xeros.content.combat.specials.impl;

import io.xeros.Server;
import io.xeros.content.combat.Damage;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.specials.Special;
import io.xeros.content.minigames.arbograve.instance.ArbograveInstance;
import io.xeros.model.Graphic;
import io.xeros.model.cycleevent.impl.NightmareStaffEvent;
import io.xeros.model.entity.Entity;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;

public class DemonxSpear extends Special {
    public DemonxSpear() {
        super(5.5, 1.5, 2, new int[] { 25979, 27287, 33204 });
    }

    @Override
    public void activate(Player player, Entity target, Damage damage) {
        player.startAnimation(8532);
        player.gfx0(1760);
        target.startGraphic(new Graphic(1759));

        damage.setAmount(100);

        if (player.getArboContainer().inArbo() && player.getInstance() != null && player.getItems().isWearingItem(33204)) {
            for (NPC npc : player.getInstance().getNpcs()) {
                if (npc.getNpcId() == 3233) {
                    npc.startGraphic(new Graphic(1759));
                    npc.appendDamage(player, npc.getHealth().getMaximumHealth(), Hitmark.HIT);
                }
            }
        }
    }

    @Override
    public void hit(Player player, Entity target, Damage damage) {

    }
}
