package com.wolfyscript.utilities.bukkit.gui.interaction

import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit
import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl
import com.wolfyscript.utilities.bukkit.gui.ClickInteractionDetailsImpl
import com.wolfyscript.utilities.bukkit.gui.InteractionUtils
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.components.StackInputSlot
import com.wolfyscript.viewportl.gui.interaction.ClickInteractionDetails
import com.wolfyscript.viewportl.gui.interaction.ComponentInteractionHandler
import com.wolfyscript.viewportl.gui.interaction.InteractionDetails
import com.wolfyscript.viewportl.gui.interaction.InteractionResult
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class InventoryStackSlotInteractionHandler : ComponentInteractionHandler<StackInputSlot> {

    override fun interact(
        runtime: ViewRuntime,
        component: StackInputSlot,
        details: InteractionDetails
    ): InteractionResult {
        if (details is ClickInteractionDetails) {
            component.onClick?.let {
                with(it) {
                    details.consume()
                }
            }
            // TODO: Handle accept stack & more
            if (details is ClickInteractionDetailsImpl) {
                val event: InventoryClickEvent = details.clickEvent
                InteractionUtils.applyItemFromInteractionEvent(
                    event.slot, event, setOf<Int>()
                ) {
                    component.onValueChange?.accept(it?.let { ItemStackImpl(runtime.wolfyUtils as WolfyUtilsBukkit, it) })
                }
            }
        }
        return InteractionResult.cancel(false)
    }
}