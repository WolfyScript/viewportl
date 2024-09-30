package com.wolfyscript.viewportl.sponge.gui.inventoryui.interaction

import com.wolfyscript.scafall.common.api.into
import com.wolfyscript.scafall.sponge.api.SpongePluginWrapper
import com.wolfyscript.viewportl.common.gui.components.ButtonImpl
import com.wolfyscript.viewportl.common.gui.components.SlotImpl
import com.wolfyscript.viewportl.common.gui.inventoryui.interaction.InvUIInteractionHandler
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.components.NativeComponent
import com.wolfyscript.viewportl.gui.interaction.ClickInteractionDetails
import com.wolfyscript.viewportl.gui.interaction.DragInteractionDetails
import com.wolfyscript.viewportl.gui.model.Node
import org.spongepowered.api.Sponge
import org.spongepowered.api.data.Keys
import org.spongepowered.api.event.item.inventory.container.ClickContainerEvent
import org.spongepowered.api.item.inventory.transaction.SlotTransaction

class SpongeUIInteractionHandler : InvUIInteractionHandler<SpongeUIInteractionHandler>() {

    companion object {

        init {
            registerComponentInteractionHandler(ButtonImpl::class.java, InventoryButtonInteractionHandler())
            registerComponentInteractionHandler(SlotImpl::class.java, InventoryStackSlotInteractionHandler())
        }

    }

    private var listener: SpongeInventoryUIListener? = null

    override fun onWindowOpen(window: Window) {
        super.onWindowOpen(window)

        if (listener == null) {
            listener = SpongeInventoryUIListener(runtime, runtime.viewportl)
            Sponge.eventManager().registerListeners(runtime.viewportl.scafall.corePlugin.into<SpongePluginWrapper>().plugin, listener)
        }
    }

    fun onClick(event: ClickContainerEvent) {
        for (transaction in event.transactions()) { // Can these events actually have multiple transactions?
            if (event !is ClickContainerEvent.Shift && !event.inventory().isViewedSlot(transaction.slot().viewedSlot())) {
                continue // Let's not handle events not affecting top inventory // TODO: Handle shift separately?
            }

            transaction.slot().get(Keys.SLOT_INDEX).ifPresent { slotIndex ->
                val node = getNodeAt(slotIndex)
                if (node != null) {
                    callComponentHandler(node.nativeComponent) {
                        onSingleSlotClick(runtime, node.nativeComponent, event)
                    }
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
                callComponentHandler(node.nativeComponent) {
                    onDrag(runtime, node.nativeComponent, transaction, event)
                }
                return@ifPresent
            }

            // No node/component was found -> cancel interaction!
            transaction.isValid = false
        }
    }

    private fun getNodeAt(slotIndex: Int): Node? {
        return slotNodes[slotIndex]?.let { runtime.model.getNode(it) }
    }

    private fun <C: NativeComponent> callComponentHandler(component: C, fn: SpongeComponentInteractionHandler<C>.() -> Unit) {
        val componentHandler = getComponentInteractionHandler(component.javaClass) as SpongeComponentInteractionHandler<C>
        componentHandler.fn()
    }

}