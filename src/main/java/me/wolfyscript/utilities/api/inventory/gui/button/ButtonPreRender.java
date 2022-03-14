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
import org.bukkit.inventory.ItemStack;

/**
 * @param <C> The type of the {@link CustomCache}
 */
public interface ButtonPreRender<C extends CustomCache> {

    /**
     * This method is run before the render method and provides the previous inventory, which includes all the items of last render.
     * <br>
     * It can be used for caching or other code that needs to be executed just before render, requires items from last render or needs to prepare data for the next render.
     * <br>
     * For example it can be used for setting items into cache for something like item input see {@link ItemInputButton}
     * <br>
     * .
     *
     * @param cache       The current cache of the GuiHandler
     * @param guiHandler  The current GuiHandler.
     * @param player      The current Player.
     * @param inventory   The original/previous inventory. No changes to this inventory will be applied on render!
     * @param itemStack   The original/previous item stack. No changes to this item stack will be applied on render!
     * @param slot        The slot in which the button is rendered.
     * @param helpEnabled Returns true if help is enabled.
     */
    void prepare(C cache, GuiHandler<C> guiHandler, Player player, GUIInventory<C> inventory, ItemStack itemStack, int slot, boolean helpEnabled);
}
