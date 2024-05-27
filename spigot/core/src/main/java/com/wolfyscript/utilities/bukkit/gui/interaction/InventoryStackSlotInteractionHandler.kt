package com.wolfyscript.utilities.bukkit.gui.interaction

import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit
import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl
import com.wolfyscript.utilities.bukkit.gui.ClickInteractionDetailsImpl
import com.wolfyscript.utilities.bukkit.gui.InteractionUtils
import com.wolfyscript.utilities.gui.ViewRuntime
import com.wolfyscript.utilities.gui.components.StackInputSlot
import com.wolfyscript.utilities.gui.interaction.ClickInteractionDetails
import com.wolfyscript.utilities.gui.interaction.ComponentInteractionHandler
import com.wolfyscript.utilities.gui.interaction.InteractionDetails
import com.wolfyscript.utilities.gui.interaction.InteractionResult
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
                // TODO: Handle accept stack & more
                if (details is ClickInteractionDetailsImpl) {
                    val event: InventoryClickEvent = details.clickEvent
                    InteractionUtils.applyItemFromInteractionEvent(
                        event.slot, event, setOf<Int>()
                    ) { itemStack: ItemStack? ->
                        component.onValueChange?.accept(
                            itemStack?.let { ItemStackImpl(runtime.wolfyUtils as WolfyUtilsBukkit, itemStack) }
                        )
                    }
                }
            }
        }
        return InteractionResult.cancel(false)
    }
}