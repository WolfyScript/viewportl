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

package me.wolfyscript.utilities.api.nms.v1_18_R1_P0.inventory;

import me.wolfyscript.utilities.api.inventory.gui.GuiHandler;
import me.wolfyscript.utilities.api.inventory.gui.GuiWindow;
import me.wolfyscript.utilities.api.inventory.gui.cache.CustomCache;
import me.wolfyscript.utilities.api.nms.inventory.GUIInventory;
import org.bukkit.craftbukkit.v1_18_R1.inventory.CraftInventoryCustom;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.InventoryHolder;

public class GUIInventoryCustom<C extends CustomCache> extends CraftInventoryCustom implements GUIInventory<C> {

    private final GuiWindow<C> window;
    private final GuiHandler<C> guiHandler;

    public GUIInventoryCustom(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder owner, InventoryType type) {
        super(owner, type);
        this.guiHandler = guiHandler;
        this.window = window;
    }

    public GUIInventoryCustom(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder owner, InventoryType type, String title) {
        super(owner, type, title);
        this.guiHandler = guiHandler;
        this.window = window;
    }

    public GUIInventoryCustom(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder owner, int size) {
        super(owner, size);
        this.guiHandler = guiHandler;
        this.window = window;
    }

    public GUIInventoryCustom(GuiHandler<C> guiHandler, GuiWindow<C> window, InventoryHolder owner, int size, String title) {
        super(owner, size, title);
        this.guiHandler = guiHandler;
        this.window = window;
    }

    @Override
    public GuiWindow<C> getWindow() {
        return this.window;
    }

    @Override
    public GuiHandler<C> getGuiHandler() {
        return guiHandler;
    }
}
