package me.wolfyscript.utilities.api.inventory.cache;

import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.GuiWindow;
import me.wolfyscript.utilities.api.inventory.button.Button;

import java.util.HashMap;

public class PlayerCache {

    private WolfyUtilities api;

    private HashMap<String, HashMap<Integer, String>> cachedButtons = new HashMap<>();

    public PlayerCache(WolfyUtilities api) {
        this.api = api;
    }

    public void setButton(GuiWindow guiWindow, int slot, String id) {
        HashMap<Integer, String> buttons = cachedButtons.getOrDefault(guiWindow.getNamespace(), new HashMap<>());
        buttons.put(slot, id);
        cachedButtons.put(guiWindow.getNamespace(), buttons);
    }

    public Button getButton(GuiWindow guiWindow, int slot) {
        String id = cachedButtons.getOrDefault(guiWindow.getNamespace(), new HashMap<>()).get(slot);
        if (id != null && !id.isEmpty() && id.contains(":")) {
            return api.getInventoryAPI().getButton(id.split(":")[0], id.split(":")[1]);
        }
        return guiWindow.getButton(id);
    }
}
