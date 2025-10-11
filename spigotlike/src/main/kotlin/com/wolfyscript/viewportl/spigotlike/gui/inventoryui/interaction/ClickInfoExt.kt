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
import com.wolfyscript.viewportl.common.gui.interaction.CommonClickInfoImpl
import com.wolfyscript.viewportl.gui.interaction.ClickInfo
import com.wolfyscript.viewportl.gui.interaction.ClickType
import org.bukkit.event.inventory.InventoryClickEvent

fun InventoryClickEvent.convert(): ClickInfo {
    val cursorStack = cursor.wrap()
    val slotStack = currentItem?.wrap()

    return when (click) {
        // dropping items
        org.bukkit.event.inventory.ClickType.DROP -> CommonClickInfoImpl.Drop.Single(rawSlot, cursorStack, slotStack)
        org.bukkit.event.inventory.ClickType.CONTROL_DROP -> CommonClickInfoImpl.Drop.Full(
            rawSlot,
            cursorStack,
            slotStack
        )

        org.bukkit.event.inventory.ClickType.WINDOW_BORDER_LEFT -> CommonClickInfoImpl.Drop.Outside.Primary(
            rawSlot,
            cursorStack,
            slotStack
        )

        org.bukkit.event.inventory.ClickType.WINDOW_BORDER_RIGHT -> CommonClickInfoImpl.Drop.Outside.Secondary(
            rawSlot,
            cursorStack,
            slotStack
        )
        // Key clicks
        org.bukkit.event.inventory.ClickType.NUMBER_KEY -> CommonClickInfoImpl.NumberPress(
            rawSlot,
            hotbarButton,
            cursorStack,
            slotStack
        )
        // most common clicks
        org.bukkit.event.inventory.ClickType.LEFT -> CommonClickInfoImpl.Primary(rawSlot, cursorStack, slotStack)
        org.bukkit.event.inventory.ClickType.RIGHT -> CommonClickInfoImpl.Secondary(rawSlot, cursorStack, slotStack)
        org.bukkit.event.inventory.ClickType.MIDDLE -> CommonClickInfoImpl.Middle(rawSlot, cursorStack, slotStack)
        org.bukkit.event.inventory.ClickType.DOUBLE_CLICK -> CommonClickInfoImpl.Double(rawSlot, cursorStack, slotStack)
        // Shift
        org.bukkit.event.inventory.ClickType.SHIFT_LEFT -> CommonClickInfoImpl.Shift.Primary(
            rawSlot,
            cursorStack,
            slotStack
        )

        org.bukkit.event.inventory.ClickType.SHIFT_RIGHT -> CommonClickInfoImpl.Shift.Secondary(
            rawSlot,
            cursorStack,
            slotStack
        )
        // Creative
        org.bukkit.event.inventory.ClickType.CREATIVE -> CommonClickInfoImpl.Primary(rawSlot, cursorStack, slotStack)
        else -> throw IllegalStateException("Unexpected value: $click")
    }
}

private val spigotClickTypeToViewportl: Map<org.bukkit.event.inventory.ClickType, ClickType> = mapOf(
    org.bukkit.event.inventory.ClickType.DROP to ClickType.DROP,
    org.bukkit.event.inventory.ClickType.CONTROL_DROP to ClickType.CONTROL_DROP,
    org.bukkit.event.inventory.ClickType.LEFT to ClickType.PRIMARY,
    org.bukkit.event.inventory.ClickType.RIGHT to ClickType.SECONDARY,
    org.bukkit.event.inventory.ClickType.SHIFT_LEFT to ClickType.SHIFT_PRIMARY,
    org.bukkit.event.inventory.ClickType.SHIFT_RIGHT to ClickType.SHIFT_SECONDARY,
    org.bukkit.event.inventory.ClickType.MIDDLE to ClickType.MIDDLE,
    org.bukkit.event.inventory.ClickType.CREATIVE to ClickType.CREATIVE,
    org.bukkit.event.inventory.ClickType.NUMBER_KEY to ClickType.NUMBER_KEY,
    org.bukkit.event.inventory.ClickType.DOUBLE_CLICK to ClickType.DOUBLE_CLICK,
    org.bukkit.event.inventory.ClickType.WINDOW_BORDER_LEFT to ClickType.CONTAINER_BORDER_PRIMARY,
    org.bukkit.event.inventory.ClickType.WINDOW_BORDER_RIGHT to ClickType.CONTAINER_BORDER_SECONDARY,
)

fun InventoryClickEvent.wrapClickType(): ClickType {
    return spigotClickTypeToViewportl[click] ?: throw IllegalStateException("Unexpected value: $click")
}

