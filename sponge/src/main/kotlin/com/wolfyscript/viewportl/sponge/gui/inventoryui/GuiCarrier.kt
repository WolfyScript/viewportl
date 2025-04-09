package com.wolfyscript.viewportl.sponge.gui.inventoryui

import com.wolfyscript.viewportl.gui.GuiHolder
import org.spongepowered.api.item.inventory.Carrier
import org.spongepowered.api.item.inventory.Inventory
import org.spongepowered.api.item.inventory.type.CarriedInventory

class GuiCarrier(val holder: GuiHolder) : Carrier {

    var inventory : Inventory? = null

    override fun inventory(): CarriedInventory<out Carrier> {
        return inventory?.let { it as CarriedInventory<GuiCarrier> } ?: throw IllegalStateException("Could not determine inventory! Menu is not initialized!")
    }


}