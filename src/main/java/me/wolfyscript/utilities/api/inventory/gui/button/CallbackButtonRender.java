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
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

/**
 * @param <C> The type of the {@link CustomCache}
 */
public interface CallbackButtonRender<C extends CustomCache> extends ButtonRender<C> {

    /**
     * Run when the button is rendered into the GUI.
     * The returned ItemStack will be set into the slot of the button.
     * Using the values HashMap you can replace specific Strings in the item names (e.g. replace placeholder from language file) with custom values.
     *
     * @param values       The HashMap, which contains the Strings, that will be replaced with its value.
     * @param cache        The current cache of the GuiHandler
     * @param guiHandler   The current GuiHandler.
     * @param player       The current Player.
     * @param guiInventory The GUIInventory in which this render was called from.
     * @param itemStack    The current itemsStack of the button.
     * @param slot         The slot in which the button is rendered.
     * @return The itemStack that should be set into the GUI.
     */
    ItemStack render(List<? extends TagResolver> values, C cache, GuiHandler<C> guiHandler, Player player, GUIInventory<C> guiInventory, ItemStack itemStack, int slot);

    default ItemStack render(HashMap<String, Object> values, C cache, GuiHandler<C> guiHandler, Player player, GUIInventory<C> guiInventory, ItemStack itemStack, int slot, boolean helpEnabled) {
        return render(List.of(), cache, guiHandler, player, guiInventory, itemStack, slot);
    }
}
