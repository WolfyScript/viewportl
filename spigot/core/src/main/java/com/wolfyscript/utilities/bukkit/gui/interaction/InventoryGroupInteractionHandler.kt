package com.wolfyscript.utilities.bukkit.gui.interaction

import com.wolfyscript.viewportl.gui.interaction.InteractionResult
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.components.ComponentGroup
import com.wolfyscript.viewportl.gui.interaction.ComponentInteractionHandler
import com.wolfyscript.viewportl.gui.interaction.InteractionDetails

class InventoryGroupInteractionHandler : ComponentInteractionHandler<ComponentGroup> {

    override fun interact(
        runtime: ViewRuntime,
        component: ComponentGroup,
        details: InteractionDetails
    ): InteractionResult {
        return InteractionResult.def()
    }
}