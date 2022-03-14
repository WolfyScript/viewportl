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

package me.wolfyscript.utilities.api.nms.inventory;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

/**
 * This interface extends the bukkit inventory interface and is fully compatible with bukkit.
 * <br>
 * It is also transferable over Bukkit's API like Inventory Events and player inventories.
 * <br>
 * This makes it possible to easily check which inventory updates are called from and which GuiHandlers are involved.
 *
 * @param <C> The type of {@link CustomCache}
 */
public interface GUIInventory<C extends CustomCache> extends Inventory {

    /**
     * @return The {@link GuiWindow} of this inventory.
     */
    GuiWindow<C> getWindow();

    /**
     * @return The {@link GuiHandler} that this inventory belongs to.
     */
    GuiHandler<C> getGuiHandler();

    default void onClick(InventoryClickEvent event) {
        getGuiHandler().getInvAPI().onClick(getGuiHandler(), this, event);
    }

    default void onDrag(InventoryDragEvent event) {
        getGuiHandler().getInvAPI().onDrag(getGuiHandler(), this, event);
    }

    default void onClose(InventoryCloseEvent event) {
        getGuiHandler().onClose(this, event);
    }
}
