package com.wolfyscript.viewportl.paper.gui.inventorygui.rendering

import com.wolfyscript.viewportl.common.gui.GuiHolderImpl
import com.wolfyscript.viewportl.gui.GuiHolder
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.WindowType
import com.wolfyscript.viewportl.spigotlike.gui.inventoryui.BukkitInventoryGuiHolder
import com.wolfyscript.viewportl.spigotlike.gui.inventoryui.rendering.SpigotLikeInvUIRenderer
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class PaperInvUIRenderer() : SpigotLikeInvUIRenderer() {

    override fun onWindowOpen(runtime: ViewRuntime, window: Window) {
        val guiHolder: GuiHolder = GuiHolderImpl(window, runtime)
        val holder = BukkitInventoryGuiHolder(guiHolder)

        // Paper has direct Adventure support, so use it for better titles!
        inventory = when (window.type) {
            WindowType.CUSTOM -> createInventory(window.size, holder, window.title)
            WindowType.DISPENSER -> createInventory(InventoryType.DISPENSER, holder, window.title)
            WindowType.DROPPER -> createInventory(InventoryType.DROPPER, holder, window.title)
            WindowType.HOPPER -> createInventory(InventoryType.HOPPER, holder, window.title)
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