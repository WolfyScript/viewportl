package com.wolfyscript.viewportl.paper.gui.inventorygui.rendering

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.common.gui.GuiHolderImpl
import com.wolfyscript.viewportl.gui.GuiHolder
import com.wolfyscript.viewportl.gui.UIRuntime
import com.wolfyscript.viewportl.gui.View
import com.wolfyscript.viewportl.gui.ViewType
import com.wolfyscript.viewportl.spigotlike.gui.inventoryui.BukkitInventoryGuiHolder
import com.wolfyscript.viewportl.spigotlike.gui.inventoryui.rendering.SpigotLikeInvUIRenderer
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class PaperInvUIRenderer() : SpigotLikeInvUIRenderer() {

    override fun onViewInit(runtime: UIRuntime, view: View) {
        initInventory(view, runtime)
    }

    override fun onViewChange(
        runtime: UIRuntime,
        view: View,
    ) {
        initInventory(view, runtime)
    }

    private fun initInventory(view: View, runtime: UIRuntime) {
        val guiHolder: GuiHolder = GuiHolderImpl(view, runtime)
        val holder = BukkitInventoryGuiHolder(guiHolder)

        // Paper has direct Adventure support, so use it for better titles!
        inventory = when (view.properties.type.type) {
            ViewType.CUSTOM -> createInventory(view.properties.dimensions.inventorySize, holder, view.properties.title.component)
            ViewType.DISPENSER -> createInventory(InventoryType.DISPENSER, holder, view.properties.title.component)
            ViewType.DROPPER -> createInventory(InventoryType.DROPPER, holder, view.properties.title.component)
            ViewType.HOPPER -> createInventory(InventoryType.HOPPER, holder, view.properties.title.component)
        }
        holder.setActiveInventory(inventory)
        if (inventory != null) {
            ScafallProvider.get().scheduler.syncTask(ScafallProvider.get().modInfo) {
                runtime.viewers.forEach {
                    Bukkit.getPlayer(it)?.openInventory(inventory!!)
                }
            }
        }
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