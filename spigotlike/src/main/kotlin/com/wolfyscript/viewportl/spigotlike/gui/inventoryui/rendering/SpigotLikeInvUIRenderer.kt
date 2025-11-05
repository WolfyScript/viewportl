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

package com.wolfyscript.viewportl.spigotlike.gui.inventoryui.rendering

import com.wolfyscript.scafall.spigot.api.wrappers.utils.unwrapSpigot
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.viewportl.common.gui.inventoryui.rendering.InvUIRenderer
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.WindowType
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.modifier.InventoryDrawScope
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory

abstract class SpigotLikeInvUIRenderer : InvUIRenderer<SpigotInvUIRenderContext>(SpigotInvUIRenderContext::class.java) {

    protected var inventory: Inventory? = Bukkit.createInventory(null, 27)

    override fun createContext(): SpigotInvUIRenderContext {
        return SpigotInvUIRenderContext(this)
    }

    protected fun getInventoryType(window: Window): InventoryType {
        return when (window.type) {
            WindowType.CUSTOM -> InventoryType.CHEST
            WindowType.HOPPER -> InventoryType.HOPPER
            WindowType.DROPPER -> InventoryType.DROPPER
            WindowType.DISPENSER -> InventoryType.DISPENSER
        }
    }

    override fun clearSlots(slots: Collection<Int>) {
        for (slot in slots) {
            inventory?.clear(slot)
        }
    }

    override fun subDrawScope(offset: Offset, width: Int, height: Int): InventoryDrawScope {
        val nodeOffset = offset
        return object : InventoryDrawScope { // TODO: mutable shared scope instead to reduce allocations
            override val width: Int = width
            override val height: Int = height

            override fun drawStack(
                offset: Offset,
                stack: ItemStackSnapshot,
            ) {
                val finalPos = nodeOffset + offset
                inventory?.setItem(finalPos.x.slot.value + finalPos.y.slot.value * 9, stack.unwrapSpigot())
            }

        }

    }
}