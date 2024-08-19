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

package com.wolfyscript.viewportl.spigot.gui.interaction

import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import com.wolfyscript.scafall.spigot.api.wrappers.world.items.ItemStackImpl
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.viewportl.gui.interaction.DragInteractionDetails
import com.wolfyscript.viewportl.gui.interaction.DragType
import org.bukkit.event.inventory.InventoryDragEvent
import java.util.function.Consumer
import java.util.stream.Collectors

class DragInteractionDetailsImpl(private val event: InventoryDragEvent) :
    DragInteractionDetails {

    override var valid: Boolean = true
    override val inventorySlots: Set<Int>
        get() = event.inventorySlots

    override val rawSlots: Set<Int>
        get() = event.rawSlots

    override val type: DragType
        get() = when (event.type) {
            org.bukkit.event.inventory.DragType.EVEN -> DragType.EVEN
            org.bukkit.event.inventory.DragType.SINGLE -> DragType.SINGLE
        }

    private val valueListeners : Multimap<Int, Consumer<ItemStack?>> = Multimaps.newListMultimap(mutableMapOf()) { mutableListOf() }

    override fun onSlotValueUpdate(slot: Int, consumer: Consumer<ItemStack?>) {
        valueListeners.put(slot, consumer)
    }

    override fun callSlotValueUpdate(slot: Int, itemStack: ItemStack?) {
        valueListeners.get(slot).forEach {
            it.accept(itemStack)
        }
    }

    override val addedStacks: Map<Int, ItemStack>
        get() = event.newItems.entries.stream().collect(
            Collectors.toUnmodifiableMap(
                { entry -> entry.key },
                { entry -> ItemStackImpl(entry.value) }
            )
        )
}
