package com.wolfyscript.utilities.bukkit.gui.interaction

import com.wolfyscript.viewportl.gui.interaction.InteractionResult
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.components.Button
import com.wolfyscript.viewportl.gui.interaction.ClickInteractionDetails
import com.wolfyscript.viewportl.gui.interaction.ComponentInteractionHandler
import com.wolfyscript.viewportl.gui.interaction.InteractionDetails

class InventoryButtonInteractionHandler : ComponentInteractionHandler<Button> {

    override fun interact(runtime: ViewRuntime, component: Button, details: InteractionDetails): InteractionResult {
        component.sound?.let { sound ->
            runtime.viewers.forEach {
                runtime.wolfyUtils.core.platform.audiences.player(it).playSound(sound)
            }
        }
        if (details is ClickInteractionDetails) {
            component.onClick?.let { click ->
                with(click) {
                    details.consume()
                }
            }
        }
        return InteractionResult.cancel(true)
    }

}