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
import me.wolfyscript.utilities.api.inventory.gui.button.buttons.ItemInputButton;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

/**
 * This interface is identical to the {@link ButtonAction}, however the behavior is different as it is similar to {@link ButtonPreRender}.
 * It is called 1 tick after the execution, right before the ButtonPreRender and only if the button was clicked!
 * <br>
 * It can be used for caching or other code that needs to be executed just before render, but only after execution happened, to prepare data for the next render.
 * <br>
 * For example it can be used for setting items into cache for something like item input see {@link ItemInputButton}
 *
 * @param <C> The type of the {@link CustomCache}
 */
public interface ButtonPostAction<C extends CustomCache> {

    /**
     * @param cache      The current cache of the GuiHandler.
     * @param guiHandler The current GuiHandler.
     * @param player     The current Player.
     * @param inventory  The original/previous inventory. No changes to this inventory will be applied on render!
     * @param slot       The slot in which the button is rendered.
     * @param event      The previous event of the click that caused the update. Can be a InventoryClickEvent or InventoryDragEvent
     * @throws IOException if an error occurs on the execution.
     */
    void run(C cache, GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, ItemStack itemStack, int slot, InventoryInteractEvent event) throws IOException;

}
