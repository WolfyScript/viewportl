package me.wolfyscript.utilities.api.inventory.gui.cache;

import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.util.TreeMap;

public class CustomCache extends TreeMap<String, Object> {

    private final TreeMap<NamespacedKey, Object> windows;

    public CustomCache() {
        windows = new TreeMap<>();
    }

    public TreeMap<String, Object> getWindowCache(GuiWindow<?> guiWindow) {
        if (!hasWindowCache(guiWindow)) {
            setWindowCache(guiWindow, new TreeMap<>());
            setButtons(guiWindow, new TreeMap<>());
        }
        return (TreeMap<String, Object>) windows.get(guiWindow.getNamespacedKey());
    }

    public boolean hasWindowCache(GuiWindow<?> guiWindow) {
        return windows.containsKey(guiWindow.getNamespacedKey());
    }

    private void setWindowCache(GuiWindow<?> guiWindow, TreeMap<String, Object> cache) {
        windows.put(guiWindow.getNamespacedKey(), cache);
    }

    public TreeMap<Integer, String> getButtons(GuiWindow<?> guiWindow) {
        return ((TreeMap<Integer, String>) getWindowCache(guiWindow).getOrDefault("buttons", new TreeMap<Integer, String>()));
    }

    public void setButtons(GuiWindow<?> guiWindow, TreeMap<Integer, String> buttons) {
        getWindowCache(guiWindow).put("buttons", buttons);
    }
}
