package com.client.draw;

import java.util.List;

/**
 * Client side container for hover tooltip data.
 */
public class HoverMenu {
    public final String text;
    public final List<Integer> items;

    public HoverMenu(String text) {
        this(text, null);
    }

    public HoverMenu(String text, List<Integer> items) {
        this.text = text;
        this.items = items;
    }
}

