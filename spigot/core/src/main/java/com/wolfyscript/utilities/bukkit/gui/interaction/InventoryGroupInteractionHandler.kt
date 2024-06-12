package com.wolfyscript.utilities.bukkit.gui.interaction

import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.components.ComponentGroup
import com.wolfyscript.viewportl.gui.interaction.*

class InventoryGroupInteractionHandler : ComponentInteractionHandler<ComponentGroup> {

    override fun onDrag(
        runtime: ViewRuntime,
        component: ComponentGroup,
        details: DragInteractionDetails,
        transaction: DragTransaction
    ) {
        details.invalidate()
    }

    override fun onClick(
        runtime: ViewRuntime,
        component: ComponentGroup,
        details: ClickInteractionDetails,
        transaction: ClickTransaction
    ) {
        details.invalidate()
    }
}