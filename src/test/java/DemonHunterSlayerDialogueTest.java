import io.xeros.content.dialogue.DialogueBuilder;
import io.xeros.content.dialogue.DialogueOption;
import io.xeros.content.dialogue.DialogueAction;
import io.xeros.content.dialogue.types.OptionDialogue;
import io.xeros.content.dialogue.DialogueObject;
import io.xeros.content.skills.slayer.DemonHunterSlayerDialogue;
import io.xeros.content.skills.slayer.DemonHunterTaskOverlayManager;
import io.xeros.content.skills.slayer.DemonSlayerMaster;
import io.xeros.model.entity.player.Player;
import io.xeros.model.entity.player.Right;
import io.xeros.model.entity.player.RightGroup;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class DemonHunterSlayerDialogueTest {

    @Test
    void backToMenuDisplaysAndReturns() throws Exception {
        Player player = mock(Player.class);
        when(player.getRights()).thenReturn(new RightGroup(player, Right.PLAYER));

        ArgumentCaptor<DialogueBuilder> captor = ArgumentCaptor.forClass(DialogueBuilder.class);
        DemonHunterSlayerDialogue.showMainMenu(player);
        verify(player).start(captor.capture());
        DialogueBuilder main = captor.getValue();

        clearInvocations(player);
        main.dispatchAction(DialogueAction.OPTION_1);
        verify(player).start(captor.capture());
        DialogueBuilder explain = captor.getValue();

        OptionDialogue backDialogue = (OptionDialogue) getLastDialogue(explain);
        List<String> titles = getOptionTitles(backDialogue);
        assertEquals("Back to menu", titles.get(0));
        assertEquals("Exit", titles.get(1));
        assertFalse(titles.contains("option2"));

        clearInvocations(player);
        explain.dispatchAction(DialogueAction.OPTION_1);
        verify(player).start(captor.capture());
        DialogueBuilder afterBack = captor.getValue();

        OptionDialogue mainMenu = (OptionDialogue) getLastDialogue(afterBack);
        List<String> mainTitles = getOptionTitles(mainMenu);
        assertTrue(mainTitles.contains("What's Demon Hunter Slayer?"));
    }

    @Test
    void viewTaskAssignsWhenMissing() throws Exception {
        Player player = mock(Player.class);
        when(player.getRights()).thenReturn(new RightGroup(player, Right.PLAYER));
        DemonSlayerMaster.DemonSlayerTask task = mock(DemonSlayerMaster.DemonSlayerTask.class);
        when(player.getDemonHunterTask()).thenReturn(Optional.empty(), Optional.of(task));

        try (MockedConstruction<DemonSlayerMaster> cons = mockConstruction(DemonSlayerMaster.class,
                (mock, context) -> when(mock.assign(any())).thenReturn(task));
             MockedStatic<DemonHunterTaskOverlayManager> overlay = mockStatic(DemonHunterTaskOverlayManager.class)) {

            ArgumentCaptor<DialogueBuilder> captor = ArgumentCaptor.forClass(DialogueBuilder.class);
            DemonHunterSlayerDialogue.showTaskMenu(player);
            verify(player).start(captor.capture());
            DialogueBuilder menu = captor.getValue();

            OptionDialogue options = (OptionDialogue) getLastDialogue(menu);
            DialogueOption[] opts = getOptions(options);
            opts[0].getConsumer().accept(player);

            DemonSlayerMaster constructed = cons.constructed().get(0);
            verify(constructed).assign(player);
            overlay.verify(() -> DemonHunterTaskOverlayManager.schedule(player));
            verify(player, never()).sendMessage("You don't currently have a Demon Hunter task.");
        }
    }

    private static DialogueObject getLastDialogue(DialogueBuilder builder) throws Exception {
        Field rootField = DialogueBuilder.class.getDeclaredField("root");
        rootField.setAccessible(true);
        DialogueObject current = (DialogueObject) rootField.get(builder);
        while (current.getChild() != null) {
            current = current.getChild();
        }
        return current;
    }

    private static List<String> getOptionTitles(OptionDialogue dialogue) throws Exception {
        Field optionsField = OptionDialogue.class.getDeclaredField("options");
        optionsField.setAccessible(true);
        DialogueOption[] options = (DialogueOption[]) optionsField.get(dialogue);
        return Arrays.stream(options).map(DialogueOption::getTitle).collect(Collectors.toList());
    }

    private static DialogueOption[] getOptions(OptionDialogue dialogue) throws Exception {
        Field optionsField = OptionDialogue.class.getDeclaredField("options");
        optionsField.setAccessible(true);
        return (DialogueOption[]) optionsField.get(dialogue);
    }
}
