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

package com.wolfyscript.viewportl.gui.interaction

import com.google.common.collect.Multimap
import com.google.common.collect.Multimaps
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.viewportl.gui.interaction.ClickInteractionDetails
import com.wolfyscript.viewportl.gui.interaction.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import java.util.function.Consumer

class ClickInteractionDetailsImpl internal constructor(val clickEvent: InventoryClickEvent) : ClickInteractionDetails {

    override var valid: Boolean = true

    override val clickType = when (clickEvent.click) {
        org.bukkit.event.inventory.ClickType.DROP -> ClickType.DROP
        org.bukkit.event.inventory.ClickType.CONTROL_DROP -> ClickType.CONTROL_DROP
        org.bukkit.event.inventory.ClickType.LEFT -> ClickType.PRIMARY
        org.bukkit.event.inventory.ClickType.RIGHT -> ClickType.SECONDARY
        org.bukkit.event.inventory.ClickType.SHIFT_LEFT -> ClickType.SHIFT_PRIMARY
        org.bukkit.event.inventory.ClickType.SHIFT_RIGHT -> ClickType.SHIFT_SECONDARY
        org.bukkit.event.inventory.ClickType.MIDDLE -> ClickType.MIDDLE
        org.bukkit.event.inventory.ClickType.CREATIVE -> ClickType.CREATIVE
        org.bukkit.event.inventory.ClickType.NUMBER_KEY -> ClickType.NUMBER_KEY
        org.bukkit.event.inventory.ClickType.DOUBLE_CLICK -> ClickType.DOUBLE_CLICK
        org.bukkit.event.inventory.ClickType.WINDOW_BORDER_LEFT -> ClickType.CONTAINER_BORDER_PRIMARY
        org.bukkit.event.inventory.ClickType.WINDOW_BORDER_RIGHT -> ClickType.CONTAINER_BORDER_SECONDARY
        else -> throw IllegalStateException("Unexpected value: " + clickEvent.click)
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

    override val isShift: Boolean = clickEvent.isShiftClick

    override val isSecondary: Boolean = clickEvent.isRightClick

    override val isPrimary: Boolean = clickEvent.isLeftClick

    override val slot: Int = clickEvent.slot

    override val rawSlot: Int = clickEvent.rawSlot

    override val hotbarButton: Int = clickEvent.hotbarButton

}
