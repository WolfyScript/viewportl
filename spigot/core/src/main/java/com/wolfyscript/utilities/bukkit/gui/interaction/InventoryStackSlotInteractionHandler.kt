package com.wolfyscript.utilities.bukkit.gui.interaction

import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit
import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl
import com.wolfyscript.utilities.bukkit.gui.ClickInteractionDetailsImpl
import com.wolfyscript.utilities.bukkit.gui.InteractionUtils
import com.wolfyscript.utilities.gui.interaction.InteractionResult
import com.wolfyscript.utilities.gui.ViewRuntime
import com.wolfyscript.utilities.gui.components.StackInputSlot
import com.wolfyscript.utilities.gui.interaction.ComponentInteractionHandler
import com.wolfyscript.utilities.gui.interaction.InteractionDetails
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class InventoryStackSlotInteractionHandler : ComponentInteractionHandler<StackInputSlot> {

    override fun interact(
        runtime: ViewRuntime,
        component: StackInputSlot,
        details: InteractionDetails
    ): InteractionResult {
        return component.onClick?.let {
            val result: InteractionResult = it.interact(runtime, details)
            if (!result.isCancelled) {
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
            result
        } ?: InteractionResult.cancel(false)
    }
}