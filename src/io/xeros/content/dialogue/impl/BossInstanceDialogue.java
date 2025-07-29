package io.xeros.content.dialogue.impl;

import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.instances.BossInstance;
import io.xeros.model.entity.player.Player;
import io.xeros.util.Misc;

public class BossInstanceDialogue extends DialogueBuilder {

    private static final int NPC_ID = io.xeros.model.Npcs.INSTANCE_MASTER;

    public BossInstanceDialogue(Player player) {
        super(player);
        setNpcId(NPC_ID);
        mainMenu();
    }

    private void mainMenu() {
        option(new DialogueOption("OSRS Boss Instances", p -> osrsMenu()),
                new DialogueOption("Custom Boss Instances", p -> customMenu()),
                DialogueOption.nevermind());
    }

    private void osrsMenu() {
        option(optionFor(BossInstance.ZULRAH),
                optionFor(BossInstance.KRAKEN),
                optionFor(BossInstance.CERBERUS),
                optionFor(BossInstance.VORKATH),
                optionFor(BossInstance.HYDRA));
    }

    private void customMenu() {
        option(optionFor(BossInstance.GROTESQUE_GUARDIANS),
                optionFor(BossInstance.OBOR),
                optionFor(BossInstance.BRYOPHYTA),
                optionFor(BossInstance.DUKE_SUCELLUS),
                optionFor(BossInstance.NIGHTMARE));
    }

    private DialogueOption optionFor(BossInstance instance) {
        return new DialogueOption(instance.getName(), p -> handle(instance));
    }

    private void handle(BossInstance instance) {
        if (!getPlayer().getUnlockedInstances().contains(instance)) {
            if (getPlayer().foundryPoints < instance.getCost()) {
                getPlayer().sendMessage("You need " + Misc.formatCoins(instance.getCost()) + " upgrade points to unlock this instance.");
                getPlayer().getPA().closeAllWindows();
                return;
            }
            getPlayer().foundryPoints -= instance.getCost();
            getPlayer().getUnlockedInstances().add(instance);
            getPlayer().sendMessage("You unlock the " + instance.getName() + " instance!");
        }
        instance.start(getPlayer());
        getPlayer().getPA().closeAllWindows();
    }
}
