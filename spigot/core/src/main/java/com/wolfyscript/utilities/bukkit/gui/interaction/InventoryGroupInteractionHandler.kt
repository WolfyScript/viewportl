package com.wolfyscript.utilities.bukkit.gui.interaction

import com.wolfyscript.utilities.gui.interaction.InteractionResult
import com.wolfyscript.utilities.gui.ViewRuntime
import com.wolfyscript.utilities.gui.components.ComponentGroup
import com.wolfyscript.utilities.gui.interaction.ComponentInteractionHandler
import com.wolfyscript.utilities.gui.interaction.InteractionDetails

class InventoryGroupInteractionHandler : ComponentInteractionHandler<ComponentGroup> {

    override fun interact(
        runtime: ViewRuntime,
        component: ComponentGroup,
        details: InteractionDetails
    ): InteractionResult {
        return InteractionResult.def()
    }
}