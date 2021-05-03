package me.wolfyscript.utilities.api.inventory.gui.cache;

import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public abstract class CustomCache {

    private final Map<GuiWindow<?>, Map<Integer, String>> cachedButtons;
    private final Map<NamespacedKey, Map<String, Object>> windows;

    protected CustomCache() {
        this.cachedButtons = new HashMap<>();
        windows = new TreeMap<>();
    }

    /**
     * @param guiWindow The {@link GuiWindow} to get the cache data from.
     * @return the cache of the specified {@link GuiWindow}
     */
    @NotNull
    public Map<String, Object> getWindowCache(GuiWindow<?> guiWindow) {
        return windows.computeIfAbsent(guiWindow.getNamespacedKey(), n -> new TreeMap<>());
    }

    public boolean hasWindowCache(GuiWindow<?> guiWindow) {
        return windows.containsKey(guiWindow.getNamespacedKey());
    }

    /**
     * @param window The {@link GuiWindow} to get the cache {@link me.wolfyscript.utilities.api.inventory.gui.button.Button}s from.
     * @return the cached {@link me.wolfyscript.utilities.api.inventory.gui.button.Button}s of the specified {@link GuiWindow}.
     */
    @NotNull
    public Map<Integer, String> getButtons(GuiWindow<?> window) {
        return cachedButtons.computeIfAbsent(window, guiWindow -> new TreeMap<>());
    }

    public void setButton(GuiWindow<?> window, int slot, String buttonID) {
        getButtons(window).put(slot, buttonID);
    }
}
