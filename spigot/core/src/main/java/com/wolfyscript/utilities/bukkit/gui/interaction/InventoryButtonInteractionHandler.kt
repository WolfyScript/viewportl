package com.wolfyscript.utilities.bukkit.gui.interaction

import com.wolfyscript.utilities.gui.InteractionResult
import com.wolfyscript.utilities.gui.ViewRuntime
import com.wolfyscript.utilities.gui.components.Button
import com.wolfyscript.utilities.gui.interaction.ComponentInteractionHandler
import com.wolfyscript.utilities.gui.interaction.InteractionDetails

class InventoryButtonInteractionHandler : ComponentInteractionHandler<Button> {

    override fun interact(runtime: ViewRuntime, component: Button, details: InteractionDetails): InteractionResult {
        component.sound().ifPresent { sound ->
            runtime.viewers.forEach {
                runtime.wolfyUtils.core.platform.audiences.player(it).playSound(sound)
            }
        }
        component.interactCallback().interact(runtime, details)
        return InteractionResult.cancel(true)
    }

}