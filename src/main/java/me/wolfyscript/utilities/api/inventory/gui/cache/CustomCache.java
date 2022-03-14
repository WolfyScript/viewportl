/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.utilities.api.inventory.gui.cache;

import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class CustomCache {

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
