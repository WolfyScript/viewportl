package com.wolfyscript.viewportl.gui.interaction

import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.components.Button
import com.wolfyscript.viewportl.gui.interaction.*

class InventoryButtonInteractionHandler : ComponentInteractionHandler<Button> {

    private fun playSound(runtime: ViewRuntime, component: Button) {
        component.sound?.let { sound ->
            runtime.viewers.forEach {
                runtime.scaffolding.adventure.player(it).playSound(sound)
            }
        }
    }

    override fun onClick(
        runtime: ViewRuntime,
        component: Button,
        details: ClickInteractionDetails,
        transaction: ClickTransaction
    ) {
        playSound(runtime, component)

        component.onClick?.let { click ->
            with(click) {
                transaction.consume()
            }
        }

        details.invalidate() // Never allow to validate it!
    }

    override fun onDrag(
        runtime: ViewRuntime,
        component: Button,
        details: DragInteractionDetails,
        transaction: DragTransaction
    ) {
        details.invalidate()
    }

}