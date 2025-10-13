package com.wolfyscript.viewportl.sponge.gui.inventoryui.interaction

import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.gui.GuiHolder
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.sponge.gui.inventoryui.GuiCarrier
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.filter.type.Include
import org.spongepowered.api.event.item.inventory.container.ClickContainerEvent
import org.spongepowered.api.event.item.inventory.container.InteractContainerEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.type.CarriedInventory

class SpongeInventoryUIListener(
    private val runtime: ViewRuntime<*, *>,
    val viewportl: Viewportl
) {

    private fun withGuiHolder(inventory: Inventory, fn: GuiHolder.() -> Unit) {
        if (inventory is CarriedInventory<*>) {
            return inventory.carrier().ifPresent {
                if (it is GuiCarrier) {
                    it.holder.fn()
                }
            }
        }
    }

    /**
     *
     */
    @Listener
    @Include(
        ClickContainerEvent.Primary::class,
        ClickContainerEvent.Secondary::class,
        ClickContainerEvent.Middle::class,
        ClickContainerEvent.Shift::class,
        ClickContainerEvent.Double::class,
        ClickContainerEvent.Drop.Single::class,
        ClickContainerEvent.Drop.Full::class
    )
    fun onClickSingleSlot(event: ClickContainerEvent) {
        withGuiHolder(event.inventory()) {
            if (event.slot().map { !event.inventory().isViewedSlot(it.viewedSlot()) }.orElse(true)) {
                return@withGuiHolder // Let's not handle events not affecting top inventory
            }
            runtime.interactionHandler.onClick(event)
        }
    }

    @Listener
    fun onDoubleCollect(event: ClickContainerEvent.Double) {
        withGuiHolder(event.inventory()) {
            for (transaction in event.transactions()) {
                if (!event.inventory().isViewedSlot(transaction.slot().viewedSlot())) {
                    continue // Let's not handle events not affecting top inventory
                }

                runtime.interactionHandler.onClick(event)
            }
        }
    }

    @Listener
    fun onClickDrag(event: ClickContainerEvent.Drag) {
        val inventory: Inventory = event.inventory()
        withGuiHolder(inventory) {
            for (transaction in event.transactions()) {
                if (!event.inventory().isViewedSlot(transaction.slot().viewedSlot())) {
                    continue // Let's not handle events not affecting top inventory
                }
                runtime.interactionHandler.onDrag(event, transaction)
            }
        }
    }

    @Listener
    fun onClose(event: InteractContainerEvent.Close) {
        val inventory = event.container()
        withGuiHolder(inventory) {
            viewManager.window?.close()
        }
    }

}