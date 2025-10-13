package com.wolfyscript.viewportl.paper.gui.inventorygui

import com.wolfyscript.viewportl.gui.GuiHolder
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class PaperInventoryGuiHolder(val guiHolder: GuiHolder) : InventoryHolder {

    private var activeInventory: Inventory? = null

    fun setActiveInventory(activeInventory: Inventory?) {
        this.activeInventory = activeInventory
    }

    override fun getInventory(): Inventory {
        return activeInventory!!
    }
}