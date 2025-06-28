package io.xeros.content.tutorial;

import io.xeros.Configuration;
import io.xeros.Server;
import io.xeros.content.commands.owner.Pos;
import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.items.Starter;
import io.xeros.model.entity.player.*;
import io.xeros.model.entity.player.lock.CompleteLock;
import io.xeros.model.entity.player.mode.ExpMode;
import io.xeros.model.entity.player.mode.Mode;
import io.xeros.model.entity.player.mode.ModeType;
import io.xeros.model.entity.player.mode.group.ExpModeType;
import io.xeros.model.entity.player.mode.group.GroupIronman;

import java.util.function.Consumer;

public class TutorialDialogue extends DialogueBuilder {

    public static final int TUTORIAL_NPC = 5525;
    private static final String IN_TUTORIAL_KEY = "in_tutorial";
    private static final DialogueOption[] XP_RATES = {
            new DialogueOption("100x Combat / 100x Skilling", p -> chosenXpRate(p, ExpModeType.TwoFiftyTimes)),
            new DialogueOption("25x Combat / 15x Skilling", p -> chosenXpRate(p, ExpModeType.TwentyFiveTimes)),
            new DialogueOption("10x Combat / 10x Skilling", p -> chosenXpRate(p, ExpModeType.TenTimes)),
            new DialogueOption("5x Combat / 5x Skilling (+7% dr)", p -> chosenXpRate(p, ExpModeType.FiveTimes)),
            new DialogueOption("1x Combat / 1x Skilling (+10% dr)", p -> chosenXpRate(p, ExpModeType.OneTimes))
    };

    public static boolean inTutorial(Player player) {
        return player.getAttributes().getBoolean(IN_TUTORIAL_KEY);
    }

    private static void setInTutorial(Player player, boolean inTutorial) {
        player.getAttributes().setBoolean(IN_TUTORIAL_KEY, inTutorial);
        if (inTutorial)
            player.setMovementState(new PlayerMovementStateBuilder().setLocked(true).createPlayerMovementState());
        else
            player.setMovementState(PlayerMovementState.getDefault());
    }

    public static void selectedMode(Player player, ModeType mode) {
        Consumer<Player> chooseExpRate = p -> chooseExperienceRate(player);
        player.getRights().updatePrimary();
        player.lock(new CompleteLock());

        player.start(new DialogueBuilder(player)
                .setNpcId(TUTORIAL_NPC)
                .npc("You've chosen " + mode.getFormattedName() + ", sound right?")
                .option(new DialogueOption("Yes, play " +mode.getFormattedName() + " mode.", chooseExpRate),
                        new DialogueOption("No, pick another game mode.", p -> p.getModeSelection().openInterface()))
        );
    }

    private static void chosenXpRate(Player player, ExpModeType mode) {
        player.start(new DialogueBuilder(player).setNpcId(TUTORIAL_NPC).npc("You've chosen the " + mode.getFormattedName() + " experience rate.", "Sound right?")
                .option(new DialogueOption("Yes, use " + mode.getFormattedName() + " experience rate.", p -> finish(p, mode)),
                        new DialogueOption("No.", TutorialDialogue::chooseExperienceRate)));
    }

    private static void chooseExperienceRate(Player player) {
        player.start(new DialogueBuilder(player).setNpcId(TUTORIAL_NPC).npc("Select which experience type you want to use.").option(XP_RATES));
    }

    public static void finish(Player player, ExpModeType modeType) {
        switch (modeType) {
            case TwoFiftyTimes:
                player.setExpMode(new ExpMode(ExpModeType.TwoFiftyTimes));
                break;
            case TwentyFiveTimes:
                player.setExpMode(new ExpMode(ExpModeType.TwentyFiveTimes));
                break;
            case TenTimes:
                player.setExpMode(new ExpMode(ExpModeType.TenTimes));
                break;
            case FiveTimes:
                player.setExpMode(new ExpMode(ExpModeType.FiveTimes));
                break;
            case OneTimes:
                player.setExpMode(new ExpMode(ExpModeType.OneTimes));
                break;
        }

        player.getPA().requestUpdates();
        setInTutorial(player, false);
        Starter.addStarter(player);
        player.setCompletedTutorial(true);

        if (player.getRights().contains(Right.WILDYMAN) || player.getRights().contains(Right.HARDCORE_WILDYMAN)) {
            player.moveTo(new Position(3135,3629,0));
        }

        player.start(new DialogueBuilder(player).setNpcId(TUTORIAL_NPC).npc("Enjoy your stay on " + Configuration.SERVER_NAME + "!"));
        PlayerHandler.executeGlobalMessage("[@blu@New Player@bla@] " + player.getDisplayNameFormatted() + " @bla@has logged in! Welcome!");
        player.unlock();
    }

    public TutorialDialogue(Player player, boolean repeat, boolean tutorial) {
        super(player);

        setNpcId(TUTORIAL_NPC);
        if (!Server.isTest() && tutorial) {
            npc(new Position(3213, 3218), "Welcome to " + Configuration.SERVER_NAME + "!", "Here is our home area!");
            npc(new Position(3229, 3214), "Here you can find all the shops needed", "when you first start out! You can buy combat gear");
            npc(new Position(3248, 3233), "Here you can find even more shops, and some skilling areas!", "when you first start out! You can buy combat gear");
            npc(new Position(3218, 3222), "Receive your daily login rewards here!");
            npc(new Position(3222, 3204), "Here multiple chests you can get rewards from.");
            npc(new Position(3227, 3223), "Here you can get Slayer tasks and spend Boss points!");
            npc(new Position(3229, 3211), "If you decide to be a restricted game mode", "you can use the shops here!", "Including a UIM Storage chest!");
            npc(new Position(3209, 3211), "This is the banking area", "you can also access the vote shop!", "as well as the donor shop!");
            npc(new Position(3206, 3226), "Here we have the Upgrade Table,", "Nomad(Dissolve items for points),", "Prayer alter, & more!");
        }
        if (!repeat) {
            npc("Be sure to @blu@set an account pin with ::pin@bla@!", "You only have to enter it when you login", "on a different computer.");
            npc("You have the option to play as an <col=" + Right.IRONMAN + "><img=12></img>Iron Man</col>, <col=" + Right.GROUP_IRONMAN + "><img=27></img>GIM</col>",
                    "<col=" + Right.ULTIMATE_IRONMAN + "><img=13></img>Ultimate Iron Man</col>, <col=" + Right.HC_IRONMAN
                            + "><img=9></img>Hardcore Iron Man</col>, or neither.", "Choose from the following interface.");
            exit(p -> p.getModeSelection().openInterface());
        }
    }

    @Override
    public void initialise() {
        setInTutorial(getPlayer(), true);
        super.initialise();
    }

    private void npc(Position teleport, String...text) {
        npc(text).action(player -> player.moveTo(teleport));
    }
}
