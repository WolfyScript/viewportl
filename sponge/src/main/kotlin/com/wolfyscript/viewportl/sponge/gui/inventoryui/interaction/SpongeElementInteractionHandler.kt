package com.wolfyscript.viewportl.sponge.gui.inventoryui.interaction

import com.wolfyscript.viewportl.gui.interaction.ElementInteractionHandler
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.elements.Element
import org.spongepowered.api.event.item.inventory.container.ClickContainerEvent
import org.spongepowered.api.item.inventory.transaction.SlotTransaction

interface SpongeElementInteractionHandler<C : Element> : ElementInteractionHandler<C> {

    fun onSingleSlotClick(runtime: ViewRuntime, component: C, event: ClickContainerEvent) {}

    fun onDrag(
        runtime: ViewRuntime,
        component: C,
        slotTransaction: SlotTransaction,
        event: ClickContainerEvent.Drag
    ) {
    }

}