package com.wolfyscript.viewportl.paper.gui.inventorygui.rendering

import com.wolfyscript.viewportl.common.gui.GuiHolderImpl
import com.wolfyscript.viewportl.gui.GuiHolder
import com.wolfyscript.viewportl.gui.UIRuntime
import com.wolfyscript.viewportl.gui.View
import com.wolfyscript.viewportl.gui.WindowType
import com.wolfyscript.viewportl.spigotlike.gui.inventoryui.BukkitInventoryGuiHolder
import com.wolfyscript.viewportl.spigotlike.gui.inventoryui.rendering.SpigotLikeInvUIRenderer
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class PaperInvUIRenderer() : SpigotLikeInvUIRenderer() {

    override fun onWindowOpen(runtime: UIRuntime, view: View) {
        val guiHolder: GuiHolder = GuiHolderImpl(view, runtime)
        val holder = BukkitInventoryGuiHolder(guiHolder)

        // Paper has direct Adventure support, so use it for better titles!
        inventory = when (view.type) {
            WindowType.CUSTOM -> createInventory(view.size, holder, view.title)
            WindowType.DISPENSER -> createInventory(InventoryType.DISPENSER, holder, view.title)
            WindowType.DROPPER -> createInventory(InventoryType.DROPPER, holder, view.title)
            WindowType.HOPPER -> createInventory(InventoryType.HOPPER, holder, view.title)
        }
        holder.setActiveInventory(inventory)
    }

    private fun createInventory(type: InventoryType, holder: InventoryHolder, title: Component?): Inventory {
        if (title != null) {
            return Bukkit.createInventory(holder, type, title)
        }
        return Bukkit.createInventory(holder, type)
    }

    private fun createInventory(size: Int, holder: InventoryHolder, title: Component?): Inventory {
        if (title != null) {
            return Bukkit.createInventory(holder, size, title)
        }
        return Bukkit.createInventory(holder, size)
    }

}