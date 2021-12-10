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

package me.wolfyscript.utilities.api.nms.v1_18_R1_P0.inventory.util;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import me.wolfyscript.utilities.api.nms.v1_18_R1_P0.inventory.GUIInventoryCustom;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

public class GUICustomInventoryConverter implements GUIInventoryCreator.InventoryConverter {

    public GUICustomInventoryConverter() {
    }

    @Override
    public <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder holder, InventoryType type) {
        return new GUIInventoryCustom<>(guiHandler, window, holder, type);
    }

    @Override
    public <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder holder, InventoryType type, String title) {
        return new GUIInventoryCustom<>(guiHandler, window, holder, type, title);
    }

    public <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder owner, int size) {
        return new GUIInventoryCustom<>(guiHandler, window, owner, size);
    }

    public <C extends CustomCache> GUIInventory<C> createInventory(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder owner, int size, String title) {
        return new GUIInventoryCustom<>(guiHandler, window, owner, size, title);
    }
}
