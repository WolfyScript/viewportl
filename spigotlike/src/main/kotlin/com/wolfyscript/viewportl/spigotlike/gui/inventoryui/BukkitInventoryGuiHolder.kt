/*
 *     viewportl - multiplatform GUI framework to easily create reactive GUIs
 *     Copyright (C) 2024  WolfyScript
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

package com.wolfyscript.viewportl.spigotlike.gui.inventoryui

import com.wolfyscript.viewportl.gui.GuiHolder
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

internal class BukkitInventoryGuiHolder(val guiHolder: GuiHolder) :
    InventoryHolder {
    private var activeInventory: Inventory? = null

    fun setActiveInventory(activeInventory: Inventory?) {
        this.activeInventory = activeInventory
    }

    override fun getInventory(): Inventory {
        return activeInventory!!
    }
}
