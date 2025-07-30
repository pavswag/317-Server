package io.xeros.content.menu;

/**
 * Simple container for tooltip text displayed when hovering over an item.
 */
public class HoverMenu {
    private final String text;

    public HoverMenu(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
