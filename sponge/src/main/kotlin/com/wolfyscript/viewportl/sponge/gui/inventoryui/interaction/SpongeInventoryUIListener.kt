package com.wolfyscript.viewportl.sponge.gui.inventoryui.interaction

import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.into
import com.wolfyscript.viewportl.gui.GuiHolder
import com.wolfyscript.viewportl.sponge.gui.inventoryui.GuiCarrier
import org.spongepowered.api.data.Keys
import org.spongepowered.api.event.Listener
import org.spongepowered.api.event.item.inventory.container.ClickContainerEvent
import org.spongepowered.api.event.item.inventory.container.ClickContainerEvent.Drag
import org.spongepowered.api.event.item.inventory.container.InteractContainerEvent
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.Slot
import org.spongepowered.api.item.inventory.menu.ClickType
import org.spongepowered.api.item.inventory.menu.ClickTypes
import org.spongepowered.api.item.inventory.type.CarriedInventory

class SpongeInventoryUIListener(val viewportl: Viewportl) {

    fun findGuiHolder(inventory: Inventory) : GuiHolder? {
        if (inventory is CarriedInventory<*>) {
            return inventory.carrier().map {
                if (it is GuiCarrier) {
                    return@map it.holder
                }
                null
            }.orElse(null)
        }
        return null
    }

    @Listener
    fun onClickPrimary(event: ClickContainerEvent.Primary) {
        event.slot().ifPresent { slot ->
            slot.get(Keys.SLOT_INDEX).ifPresent { index ->
                findGuiHolder(event.inventory())?.viewManager?.into()?.interactionHandler?.onClick(ClickInteractionDetailsImpl(ClickTypes.CLICK_LEFT.get(), slot, index, -1))
            }
        }
    }

    @Listener
    fun onClickSecondary(event: ClickContainerEvent.Secondary) {

    }

    @Listener
    fun onClickMiddle(event: ClickContainerEvent.Middle) {

    }

    @Listener
    fun onClickDrag(event: Drag) {
        val inventory: Inventory = event.inventory()
        findGuiHolder(inventory)?.let {
            event.cursorTransaction()
            for (transaction in event.transactions()) {
                transaction.slot().get(Keys.SLOT_INDEX).ifPresent { slotIndex: Int ->
                    if (slotIndex == 11) {
                        transaction.invalidate()
                    }
                }
            }
        }
    }

    @Listener
    fun onClose(event: InteractContainerEvent.Close) {
        val inventory = event.inventory()
        findGuiHolder(inventory)?.let {
            it.viewManager.window?.close()
        }
    }

    private fun onClick(event: ClickContainerEvent, clickType: ClickType<*>, slot: Slot, slotIndex: Int) {
        findGuiHolder(event.inventory())?.let { holder ->

        }
    }

}