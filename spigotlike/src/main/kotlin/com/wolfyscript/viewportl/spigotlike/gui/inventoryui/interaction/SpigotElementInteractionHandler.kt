package com.wolfyscript.viewportl.spigotlike.gui.inventoryui.interaction

import com.wolfyscript.viewportl.gui.interaction.ElementInteractionHandler
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.elements.Element
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent

interface SpigotElementInteractionHandler<C: Element> : ElementInteractionHandler<C> {

    fun onClick(
        runtime: ViewRuntime,
        element: C,
        event: InventoryClickEvent,
        slot: Slot,
        valueHandler: ValueHandler
    ) {

    }

    fun onDrag(
        runtime: ViewRuntime,
        element: C,
        event: InventoryDragEvent,
        slot: Slot,
        valueHandler: ValueHandler
    ) {

    }

}