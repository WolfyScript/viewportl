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

package me.wolfyscript.utilities.api.inventory.gui.button;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;

import java.io.IOException;

/**
 * @param <C> The type of the {@link CustomCache}
 */
public interface ButtonAction<C extends CustomCache> {

    /**
     * @param cache      The current cache of the GuiHandler.
     * @param guiHandler The current GuiHandler.
     * @param player     The current Player.
     * @param inventory  The original/previous inventory. No changes to this inventory will be applied on render!
     * @param slot       The slot in which the button is rendered.
     * @param event      The previous event of the click that caused the update. Can be a InventoryClickEvent or InventoryDragEvent
     * @return a boolean indicating whether the interaction should be cancelled. If true the interaction is cancelled.
     * @throws IOException if an error occurs on the execution.
     */
    boolean run(C cache, GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, int slot, InventoryInteractEvent event) throws IOException;

    default boolean execute(C cache, GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, Button<C> button, int slot, InventoryInteractEvent event) {
        try {
            if (this instanceof ButtonActionExtended<C> extendedAction) {
                return extendedAction.run(cache, guiHandler, player, inventory, button, slot, event);
            }
            return run(cache, guiHandler, player, inventory, slot, event);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
