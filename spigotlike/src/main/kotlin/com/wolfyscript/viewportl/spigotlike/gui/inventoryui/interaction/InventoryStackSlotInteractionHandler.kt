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

package com.wolfyscript.viewportl.spigotlike.gui.inventoryui.interaction

import com.wolfyscript.scafall.spigot.api.wrappers.utils.wrap
import com.wolfyscript.scafall.wrappers.wrap
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.elements.StackInputSlot
import net.minecraft.world.item.ItemStack
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent

class InventoryStackSlotInteractionHandler : SpigotElementInteractionHandler<StackInputSlot> {

    override fun onDrag(
        runtime: ViewRuntime,
        element: StackInputSlot,
        event: InventoryDragEvent,
        slot: Slot,
        valueHandler: ValueHandler
    ) {
        valueHandler.listeners.put(slot.index) { spigotStack ->
            val wrapped = spigotStack?.wrap() ?: ItemStack.EMPTY.wrap()
            element.onValueChange?.let { it(wrapped) }
            element.value = wrapped
        }
    }

    override fun onClick(
        runtime: ViewRuntime,
        element: StackInputSlot,
        event: InventoryClickEvent,
        slot: Slot,
        valueHandler: ValueHandler
    ) {
        valueHandler.listeners.put(event.rawSlot) { spigotStack ->
            val wrapped = spigotStack?.wrap() ?: ItemStack.EMPTY.wrap()
            element.onValueChange?.let { it(wrapped) }
            element.value = wrapped
        }
    }

}