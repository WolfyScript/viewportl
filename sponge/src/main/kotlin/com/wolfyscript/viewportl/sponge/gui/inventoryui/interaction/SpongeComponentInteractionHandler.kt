package com.wolfyscript.viewportl.sponge.gui.inventoryui.interaction

import com.wolfyscript.viewportl.common.gui.interaction.ComponentInteractionHandler
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.elements.Element
import org.spongepowered.api.event.item.inventory.container.ClickContainerEvent
import org.spongepowered.api.item.inventory.transaction.SlotTransaction

interface SpongeComponentInteractionHandler<C : Element> : ComponentInteractionHandler<C> {

    fun onSingleSlotClick(runtime: ViewRuntime<*, SpongeUIInteractionHandler>, component: C, event: ClickContainerEvent) {}

    fun onDrag(
        runtime: ViewRuntime<*, SpongeUIInteractionHandler>,
        component: C,
        slotTransaction: SlotTransaction,
        event: ClickContainerEvent.Drag
    ) {
    }

}