package com.wolfyscript.viewportl.sponge.gui.inventoryui.interaction

import com.wolfyscript.viewportl.common.gui.inventoryui.interaction.InvUIInteractionHandler
import com.wolfyscript.viewportl.gui.View
import com.wolfyscript.viewportl.gui.interaction.InteractionContext
import com.wolfyscript.viewportl.gui.compose.Node
import com.wolfyscript.viewportl.sponge.SpongeViewportl
import com.wolfyscript.viewportl.viewportl
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.Keys
import org.spongepowered.api.event.item.inventory.container.ClickContainerEvent
import org.spongepowered.api.item.inventory.transaction.SlotTransaction
import java.lang.invoke.MethodHandles

class SpongeUIInteractionHandler : InvUIInteractionHandler<InteractionContext>() {

    private var listener: SpongeInventoryUIListener? = null

    override fun onViewOpened(view: View) {
        super.onViewOpened(view)

        if (listener == null) {
            listener = SpongeInventoryUIListener(runtime, runtime.viewportl)
            Sponge.eventManager().registerListeners((runtime.viewportl.scafall.viewportl as SpongeViewportl).plugin, listener, MethodHandles.lookup())
        }
    }

    override fun dispose() {
        super.dispose()
        Sponge.eventManager().unregisterListeners(listener)
    }

    fun onClick(event: ClickContainerEvent) {
        // TODO: Handle double click collection to cursor (may have to iterate over the transactions then)
        event.slot().ifPresent {
            it.get(Keys.SLOT_INDEX).ifPresent { slotIndex ->
                val node = getNodeAt(slotIndex)
                if (node != null) {
                    return@ifPresent
                }

                // No node/component was found -> cancel interaction!
                event.cursorTransaction().isValid = false
                event.transactions().forEach { it.isValid = false }
            }
        }
    }

    fun onDrag(event: ClickContainerEvent.Drag, transaction: SlotTransaction) {
        transaction.slot().get(Keys.SLOT_INDEX).ifPresent {
            val node = getNodeAt(it)
            if (node != null) {
                return@ifPresent
            }

            // No node/component was found -> cancel interaction!
            transaction.isValid = false
        }
    }

    private fun getNodeAt(slotIndex: Int): Node? {
        return slotNodes[slotIndex]
    }

}