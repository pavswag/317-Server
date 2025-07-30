package com.client.draw;

import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.client.Client;
import com.client.Sprite;
import com.client.engine.impl.MouseHandler;
import com.client.definitions.ItemBonusDefinition;
import com.client.definitions.ItemDefinition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class HoverManager {

    public static final int BACKGROUND_COLOUR = 0xFFFFFFF;

    public static HashMap<Integer, HoverMenu> menus = new HashMap<>();

    /**
     * Load hover text from a json file dumped by the server.
     */
    public static void init() {
        loadFromFile("item_tooltips.json");
    }

    private static void loadFromFile(String file) {
        try (Reader reader = new FileReader(file)) {
            Gson gson = new Gson();
            Type type = new TypeToken<Map<String, String>>(){}.getType();
            Map<String, String> data = gson.fromJson(reader, type);
            for (Map.Entry<String, String> e : data.entrySet()) {
                menus.put(Integer.parseInt(e.getKey()), new HoverMenu(e.getValue()));
            }
        } catch (Exception e) {
            System.err.println("Unable to load hover menus: " + e.getMessage());
        }
        System.out.println("Turmoil has loaded " + menus.size() + "x menu hovers.");
    }

    public static int drawType() {
        if (MouseHandler.mouseX > 0 && MouseHandler.mouseX < 500 && MouseHandler.mouseY > 0
                && MouseHandler.mouseY < 300) {
            return 1;
        }
        return 0;
    }

    public static boolean shouldDraw(int id) {
        return menus.get(id) != null;
    }

    public static boolean showMenu;

    public static String hintName;

    public static int hintId;

    public static int displayIndex;

    public static long displayDelay;

    public static int[] itemDisplay = new int[4];

    private static int lastDraw;

    public static void reset() {
        showMenu = false;
        hintId = -1;
        hintName = "";
    }

    public static boolean canDraw() {
        if (Client.instance.menuActionRow < 2 && Client.instance.itemSelected == 0
                && Client.instance.spellSelected == 0) {
            return false;
        }
        if (Client.instance.getMenuManager().getMenuEntry(Client.instance.menuActionRow - 1) != null) {
            if (Client.instance.getMenuManager().getMenuEntry(Client.instance.menuActionRow - 1).getOption().contains("Walk")) {
                return false;
            }
        }
        if (Client.instance.toolTip.contains("Walk") || Client.instance.toolTip.contains("World")) {
            return false;
        }
        if (Client.instance.menuOpen) {
            return false;
        }
        if (hintId == -1) {
            return false;
        }
        if (!showMenu) {
            return false;
        }
        return true;
    }

    public static void drawHintMenu() {
        int mouseX = MouseHandler.mouseX;
        int mouseY = MouseHandler.mouseY;

        if (!canDraw()) {
            return;
        }

        if (MouseHandler.mouseY < Client.canvasHeight - 450 && MouseHandler.mouseX < Client.canvasWidth - 200) {
            return;
        }
        mouseX -= 100;
        mouseY -= 50;

        if (Client.controlIsDown) {
            drawStatMenu();
            return;
        }

        if (lastDraw != hintId) {
            lastDraw = hintId;
            itemDisplay = new int[4];
        }

        HoverMenu menu = menus.get(hintId);

        if (menu != null) {
            String text[] = split(menu.text, 20).split("\n");

            int height = (text.length * 12) + (menu.items != null ? 40 : 0);

            int width = (16 + text[0].length() * 5) + (menu.items != null ? 30 : 0);

            Rasterizer2D.drawBoxOutline(mouseX, mouseY + 5, width + 4, 26 + height, 0x696969);
            Rasterizer2D.drawTransparentBox(mouseX + 1, mouseY + 6, width + 2, 24 + height, 0x000000, 150);

            Client.instance.newSmallFont.drawBasicString("@lre@" + hintName, (int) (mouseX + 4), mouseY + 19,
                    BACKGROUND_COLOUR, 1);
            int y = 0;

            for (String string : text) {
                Client.instance.newSmallFont.drawBasicString(string, mouseX + 4, mouseY + 35 + y, BACKGROUND_COLOUR, 1);
                y += 12;
            }

            if (menu.items != null) {
                int spriteX = 10;

                if (System.currentTimeMillis() - displayDelay > 300) {
                    displayDelay = System.currentTimeMillis();
                    displayIndex++;
                    if (displayIndex == menu.items.size()) {
                        displayIndex = 0;
                    }

                    if (menu.items.size() <= 4) {
                        for (int i = 0; i < menu.items.size(); i++) {
                            itemDisplay[i] = menu.items.get(i);
                        }
                    } else {
                        if (displayIndex >= menu.items.size() - 1) {
                            displayIndex = menu.items.size() - 1;
                        }
                        int next = menu.items.get(displayIndex);
                        for (int i = 0; i < itemDisplay.length - 1; i++) {
                            itemDisplay[i] = itemDisplay[i + 1];
                        }
                        itemDisplay[3] = next;
                    }
                }

                for (int id : itemDisplay) {
                    if (id < 1) {
                        continue;
                    }
                    Sprite item = ItemDefinition.getSprite(id, 1, 0);
                    if (item != null) {
                        item.drawSprite(mouseX + spriteX, mouseY + 35 + y);
                        spriteX += 40;
                    }
                }
            }
            return;
        }
    }

    public static void drawStatMenu() {
        if (!canDraw()) {
            return;
        }

        if(ItemBonusDefinition.getItemBonusDefinition(hintId) == null) {
            HoverManager.reset();
            return;
        }

        int mouseX = MouseHandler.mouseX;
        int mouseY = MouseHandler.mouseY;

        mouseX -= 100;
        mouseY -= 50;

        short stabAtk = ItemBonusDefinition.getItemBonuses(hintId)[0];
        int slashAtk = ItemBonusDefinition.getItemBonuses(hintId)[1];
        int crushAtk = ItemBonusDefinition.getItemBonuses(hintId)[2];
        int magicAtk = ItemBonusDefinition.getItemBonuses(hintId)[3];
        int rangedAtk = ItemBonusDefinition.getItemBonuses(hintId)[4];

        int stabDef = ItemBonusDefinition.getItemBonuses(hintId)[5];
        int slashDef = ItemBonusDefinition.getItemBonuses(hintId)[6];
        int crushDef = ItemBonusDefinition.getItemBonuses(hintId)[7];
        int magicDef = ItemBonusDefinition.getItemBonuses(hintId)[8];
        int rangedDef = ItemBonusDefinition.getItemBonuses(hintId)[9];

        int prayerBonus = ItemBonusDefinition.getItemBonuses(hintId)[11];
        int strengthBonus = ItemBonusDefinition.getItemBonuses(hintId)[10];

        Rasterizer2D.drawBoxOutline(mouseX, mouseY + 5, 150, 120, 0x696969);
        Rasterizer2D.drawTransparentBox(mouseX + 1, mouseY + 6, 150, 121, 0x000000, 90);

        Client.instance.newSmallFont.drawBasicString("@lre@" + hintName, mouseX + 4, mouseY + 18, BACKGROUND_COLOUR, 1);

        Client.instance.newSmallFont.drawBasicString("ATK:", mouseX + 62, mouseY + 30, BACKGROUND_COLOUR, 1);
        Client.instance.newSmallFont.drawBasicString("DEF:", mouseX + 112, mouseY + 30, BACKGROUND_COLOUR, 1);

        Client.instance.newSmallFont.drawBasicString("Stab", mouseX + 2, mouseY + 43, BACKGROUND_COLOUR, 1);
        Client.instance.newSmallFont.drawBasicString(Integer.toString(stabAtk), mouseX + 62, mouseY + 43,
                BACKGROUND_COLOUR, 1);
        Client.instance.newSmallFont.drawBasicString(Integer.toString(stabDef), mouseX + 112, mouseY + 43,
                BACKGROUND_COLOUR, 1);

        Client.instance.newSmallFont.drawBasicString("Slash", mouseX + 2, mouseY + 56, 0xFF00FF, 1);
        Client.instance.newSmallFont.drawBasicString(Integer.toString(slashAtk), mouseX + 62, mouseY + 56,
                BACKGROUND_COLOUR, 1);
        Client.instance.newSmallFont.drawBasicString(Integer.toString(slashDef), mouseX + 112, mouseY + 56,
                BACKGROUND_COLOUR, 1);

        Client.instance.newSmallFont.drawBasicString("Crush", mouseX + 2, mouseY + 69, BACKGROUND_COLOUR, 1);
        Client.instance.newSmallFont.drawBasicString(Integer.toString(crushAtk), mouseX + 62, mouseY + 69,
                BACKGROUND_COLOUR, 1);
        Client.instance.newSmallFont.drawBasicString(Integer.toString(crushDef), mouseX + 112, mouseY + 69,
                BACKGROUND_COLOUR, 1);

        Client.instance.newSmallFont.drawBasicString("Magic", mouseX + 2, mouseY + 80, BACKGROUND_COLOUR, 1);
        Client.instance.newSmallFont.drawBasicString(Integer.toString(magicAtk), mouseX + 62, mouseY + 80,
                BACKGROUND_COLOUR, 1);
        Client.instance.newSmallFont.drawBasicString(Integer.toString(magicDef), mouseX + 112, mouseY + 80,
                BACKGROUND_COLOUR, 1);

        Client.instance.newSmallFont.drawBasicString("Ranged", mouseX + 2, mouseY + 95, BACKGROUND_COLOUR, 1);
        Client.instance.newSmallFont.drawBasicString(Integer.toString(rangedAtk), mouseX + 62, mouseY + 95,
                BACKGROUND_COLOUR, 1);
        Client.instance.newSmallFont.drawBasicString(Integer.toString(rangedDef), mouseX + 112, mouseY + 95,
                BACKGROUND_COLOUR, 1);

        Client.instance.newSmallFont.drawBasicString("Strength", mouseX + 2, mouseY + 108, BACKGROUND_COLOUR, 1);
        Client.instance.newSmallFont.drawBasicString("Prayer", mouseX + 2, mouseY + 121, BACKGROUND_COLOUR, 1);

        Client.instance.newSmallFont.drawBasicString(Integer.toString(strengthBonus), mouseX + 112, mouseY + 108,
                BACKGROUND_COLOUR, 1);
        Client.instance.newSmallFont.drawBasicString(Integer.toString(prayerBonus), mouseX + 112, mouseY + 121,
                BACKGROUND_COLOUR, 1);

        Client.instance.newSmallFont.drawBasicString("Stab", mouseX + 2, mouseY + 43, BACKGROUND_COLOUR, 1);
        Client.instance.newSmallFont.drawBasicString("Slash", mouseX + 2, mouseY + 56, BACKGROUND_COLOUR, 1);
        Client.instance.newSmallFont.drawBasicString("Crush", mouseX + 2, mouseY + 69, BACKGROUND_COLOUR, 1);
        Client.instance.newSmallFont.drawBasicString("Magic", mouseX + 2, mouseY + 80, BACKGROUND_COLOUR, 1);
        Client.instance.newSmallFont.drawBasicString("Ranged", mouseX + 2, mouseY + 95, BACKGROUND_COLOUR, 1);
        Client.instance.newSmallFont.drawBasicString("Strength", mouseX + 2, mouseY + 108, BACKGROUND_COLOUR, 1);
        Client.instance.newSmallFont.drawBasicString("Prayer", mouseX + 2, mouseY + 121, BACKGROUND_COLOUR, 1);
    }

    private static String split(String text, int length) {
        String string = "";

        int size = 0;

        for (String s : text.split(" ")) {
            string += s + " ";
            size += s.length();
            if (size > length) {
                string += "\n";
                size = 0;
            }
        }
        return string;
    }

    public static void drawHoverBox(RSFont font, int xPos, int yPos, String text, int colour, int backgroundColour) {
        String[] results = text.split("\n");
        int height = (results.length * 16) + 6;
        int width;
        width = font.getTextWidth(results[0]) + 6;
        for (int i = 1; i < results.length; i++)
            if (width <= font.getTextWidth(results[i]) + 6)
                width = font.getTextWidth(results[i]) + 6;
        Rasterizer2D.drawBox(xPos, yPos, width, height, backgroundColour);
        Rasterizer2D.drawBoxOutline(xPos, yPos, width, height, 0);
        yPos += 14;
        for (int i = 0; i < results.length; i++) {
            font.drawBasicString(results[i], xPos + 3, yPos, colour, 0);
            yPos += 16;
        }
    }
}

