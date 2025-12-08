package com.wolfyscript.viewportl.spigot.gui.inventorygui.rendering

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.common.gui.GuiHolderImpl
import com.wolfyscript.viewportl.gui.GuiHolder
import com.wolfyscript.viewportl.gui.UIRuntime
import com.wolfyscript.viewportl.gui.View
import com.wolfyscript.viewportl.gui.ViewType
import com.wolfyscript.viewportl.spigotlike.gui.inventoryui.BukkitInventoryGuiHolder
import com.wolfyscript.viewportl.spigotlike.gui.inventoryui.rendering.SpigotLikeInvUIRenderer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder

class SpigotInvUIRenderer : SpigotLikeInvUIRenderer() {

    override fun onViewInit(runtime: UIRuntime, view: View) {
        initiateUIInventory(runtime, view)
    }

    override fun onViewChange(
        runtime: UIRuntime,
        view: View,
    ) {
        initiateUIInventory(runtime, view)
    }

    private fun initiateUIInventory(runtime: UIRuntime, view: View) {
        val guiHolder: GuiHolder = GuiHolderImpl(view, runtime)
        val holder = BukkitInventoryGuiHolder(guiHolder)
        val title: String? = view.properties.title.component?.let { LegacyComponentSerializer.legacySection().serialize(it) }
        inventory = when (view.properties.type.type) {
            ViewType.CUSTOM -> createInventory(view.properties.dimensions.inventorySize, holder, title)
            ViewType.DISPENSER -> createInventory(InventoryType.DISPENSER, holder, title)
            ViewType.DROPPER -> createInventory(InventoryType.DROPPER, holder, title)
            ViewType.HOPPER -> createInventory(InventoryType.HOPPER, holder, title)
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



    private fun createInventory(type: InventoryType, holder: InventoryHolder, title: String?): Inventory {
        if (title != null) {
            return Bukkit.createInventory(holder, type, title)
        }
        return Bukkit.createInventory(holder, type)
    }

    private fun createInventory(size: Int, holder: InventoryHolder, title: String?): Inventory {
        if (title != null) {
            return Bukkit.createInventory(holder, size, title)
        }
        return Bukkit.createInventory(holder, size)
    }

}