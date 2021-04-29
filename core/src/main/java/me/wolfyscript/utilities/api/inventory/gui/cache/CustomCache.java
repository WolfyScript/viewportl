package me.wolfyscript.utilities.api.inventory.gui.cache;

import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.HashMap;
import java.util.TreeMap;

/**
 * Warning! In upcoming update 1.6.4.0 this class will no longer extend the {@link TreeMap}!
 * Please create a custom class that extends this one instead!
 */
public class CustomCache extends TreeMap<String, Object> {

    private final HashMap<GuiWindow<?>, TreeMap<Integer, String>> cachedButtons;
    private final TreeMap<NamespacedKey, Object> windows;

    public CustomCache() {
        this.cachedButtons = new HashMap<>();
        windows = new TreeMap<>();
    }

    public TreeMap<String, Object> getWindowCache(GuiWindow<?> guiWindow) {
        if (!hasWindowCache(guiWindow)) {
            setWindowCache(guiWindow, new TreeMap<>());
        }
        return (TreeMap<String, Object>) windows.get(guiWindow.getNamespacedKey());
    }

    public boolean hasWindowCache(GuiWindow<?> guiWindow) {
        return windows.containsKey(guiWindow.getNamespacedKey());
    }

    private void setWindowCache(GuiWindow<?> guiWindow, TreeMap<String, Object> cache) {
        windows.put(guiWindow.getNamespacedKey(), cache);
    }


    public TreeMap<Integer, String> getButtons(GuiWindow<?> window) {
        return cachedButtons.getOrDefault(window, new TreeMap<>());
    }

    public void setButton(GuiWindow<?> window, int slot, String buttonID) {
        TreeMap<Integer, String> buttons = getButtons(window);
        buttons.put(slot, buttonID);
        cachedButtons.put(window, buttons);
    }
}
