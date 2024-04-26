package com.wolfyscript.utilities.bukkit.gui

import com.wolfyscript.utilities.gui.GuiHolder
import com.wolfyscript.utilities.gui.ViewRuntimeImpl
import com.wolfyscript.utilities.gui.Window
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

internal class BukkitInventoryGuiHolder(private val runtime: ViewRuntimeImpl, private val guiHolder: GuiHolder) :
    InventoryHolder {
    private var activeInventory: Inventory? = null

    private fun currentWindow(): Window? {
        return guiHolder.currentWindow
    }

    fun guiHolder(): GuiHolder {
        return guiHolder
    }

    fun onClick(event: InventoryClickEvent) {
        if (currentWindow() == null || event.clickedInventory == null) return
        if (event.clickedInventory!!.holder == this) {
            val result = runtime.interactionHandler.onInteract(ClickInteractionDetailsImpl(event))
            if (result.isCancelled) {
                event.isCancelled = true
            }
        } else if (event.action != InventoryAction.COLLECT_TO_CURSOR) {
            event.isCancelled = false
            // TODO: Handle bottom inventory clicks
        }
        runtime.wolfyUtils.core.platform.scheduler.syncTask(runtime.wolfyUtils) {
            runtime.reactiveSource.runEffects()
            runtime.currentMenu?.apply {

            }
        }
    }

    fun onDrag(event: InventoryDragEvent) {
        if (event.rawSlots.stream().anyMatch { rawSlot: Int? ->
                event.view.getInventory(rawSlot!!) != activeInventory
            }) {
            event.isCancelled = true
            return
        }
        if (currentWindow() == null) return
        if (event.inventory.holder == this) {
            val interactionDetails = DragInteractionDetailsImpl(event)
            val result = runtime.interactionHandler.onInteract(interactionDetails)
            if (result.isCancelled) {
                event.isCancelled = true
            }
            runtime.wolfyUtils.core.platform.scheduler.syncTask(runtime.wolfyUtils) {
                runtime.reactiveSource.runEffects()
            }
        }
    }

    fun onClose(event: InventoryCloseEvent) {
        // TODO: Close Window
        if (currentWindow() == null) return
        if (event.inventory.holder == this) {
            guiHolder.viewManager.currentMenu?.apply { close() }
        }
    }

    fun setActiveInventory(activeInventory: Inventory?) {
        this.activeInventory = activeInventory
    }

    override fun getInventory(): Inventory {
        return activeInventory!!
    }
}
