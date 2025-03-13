package com.wolfyscript.viewportl.sponge.gui.inventoryui.interaction

import com.wolfyscript.scafall.common.api.into
import com.wolfyscript.scafall.sponge.api.SpongePluginWrapper
import com.wolfyscript.viewportl.common.gui.elements.ButtonImpl
import com.wolfyscript.viewportl.common.gui.elements.SlotImpl
import com.wolfyscript.viewportl.common.gui.inventoryui.interaction.InvUIInteractionHandler
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.elements.Element
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
        // TODO: Handle double click collection to cursor (may have to iterate over the transactions then)
        event.slot().ifPresent {
            it.get(Keys.SLOT_INDEX).ifPresent { slotIndex ->
                val node = getNodeAt(slotIndex)
                if (node != null) {
                    callComponentHandler(node.element) {
                        onSingleSlotClick(runtime, node.element, event)
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
                callComponentHandler(node.element) {
                    onDrag(runtime, node.element, transaction, event)
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

    private fun <C: Element> callComponentHandler(component: C, fn: SpongeComponentInteractionHandler<C>.() -> Unit) {
        val componentHandler = getComponentInteractionHandler(component.javaClass) as SpongeComponentInteractionHandler<C>
        componentHandler.fn()
    }

}