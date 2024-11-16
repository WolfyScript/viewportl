package com.wolfyscript.viewportl.spigot.gui.inventoryui.interaction

import com.wolfyscript.viewportl.common.gui.interaction.ComponentInteractionHandler
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.elements.Element
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent

interface SpigotComponentInteractionHandler<C: Element> : ComponentInteractionHandler<C> {

    fun onClick(
        runtime: ViewRuntime<*, SpigotInvUIInteractionHandler>,
        component: C,
        event: InventoryClickEvent,
        slot: Slot,
        valueHandler: ValueHandler
    ) {

    }

    fun onDrag(
        runtime: ViewRuntime<*, SpigotInvUIInteractionHandler>,
        component: C,
        event: InventoryDragEvent,
        slot: Slot,
        valueHandler: ValueHandler
    ) {

    }

}