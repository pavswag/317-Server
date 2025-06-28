package io.xeros.content.bosses;

import io.xeros.Server;
import io.xeros.content.combat.Hitmark;
import io.xeros.content.combat.npc.NPCCombatAttack;
import io.xeros.model.StillGraphic;
import io.xeros.model.cycleevent.CycleEvent;
import io.xeros.model.cycleevent.CycleEventContainer;
import io.xeros.model.cycleevent.CycleEventHandler;
import io.xeros.model.entity.npc.NPC;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.PlayerHandler;
import io.xeros.model.entity.player.Position;
import io.xeros.model.entity.player.Right;
import io.xeros.util.Misc;

import java.time.LocalDate;
import java.util.Objects;

public class DonorBoss3 {

    public static void tick() {
        for (Player player : PlayerHandler.getPlayers()) {
            if (player.getDonorBossKCy() <= getDonorKC(player) && !Objects.equals(player.getDonorBossDatey(), LocalDate.now())) {
                player.setDonorBossKCy(0);
                player.setDonorBossDatey(LocalDate.now());
                if (player.amDonated >= 1000) {
                    player.sendMessage("You can now kill the Supreme Donator+ donor boss!");
                }
            }
        }
    }

    public static int getDonorKC(Player player) {
        if (player.getRights().isOrInherits(Right.ALMIGHTY_DONATOR)) {
            return 8;
        } else if (player.getRights().isOrInherits(Right.APEX_DONATOR)) {
            return 4;
        } else if (player.getRights().isOrInherits(Right.PLATINUM_DONATOR)) {
            return 3;
        } else if (player.getRights().isOrInherits(Right.GILDED_DONATOR)) {
            return 2;
        } else if (player.getRights().isOrInherits(Right.SUPREME_DONATOR)) {
            return 1;
        }
        return 0;
    }

    public static void burnGFX(Player c, NPC npc) {
        NPCCombatAttack npcCombatAttack = new NPCCombatAttack(npc, c);
        Position position = npcCombatAttack.getVictim().getPosition();
        int delay = 0;
        // Cycle event to handle pool damage
        CycleEventHandler.getSingleton().addEvent(new Object(), new CycleEvent() {
            @Override
            public void execute(CycleEventContainer container) {
                if (npc.isDead() || !npc.isRegistered()) {
                    container.stop();
                    return;
                }
                if (container.getTotalExecutions() % 2 == 0) {
                    Server.playerHandler.sendStillGfx(new StillGraphic(1246, 0, position), position);
                }

                if (container.getTotalExecutions() == 72) {
                    container.stop();
                } else if (container.getTotalExecutions() >= 2) {
                    PlayerHandler.getPlayers().stream().filter(plr -> plr.getPosition().equals(position)).forEach(plr ->
                            plr.appendDamage(6 + Misc.random(10), Hitmark.DAWNBRINGER));
                }
            }
        }, 3);
    }
}
