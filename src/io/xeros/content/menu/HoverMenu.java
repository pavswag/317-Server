package io.xeros.content.menu;

import java.util.List;

/**
 * Simple container for tooltip text displayed when hovering over an item.
 */
public class HoverMenu {
        private final String text;
        private final List<Integer> items;

        public HoverMenu(String text) {
            this(text, null);
        }

        public HoverMenu(String text, List<Integer> items) {
            this.text = text;
            this.items = items;
        }

        public String getText() {
            return text;
        }

        public List<Integer> getItems() {
            return items;
        }
    }
