package com.wolfyscript.viewportl.spigot.gui.inventorygui.rendering

import com.wolfyscript.viewportl.common.gui.GuiHolderImpl
import com.wolfyscript.viewportl.gui.GuiHolder
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.spigotlike.gui.inventoryui.BukkitInventoryGuiHolder
import com.wolfyscript.viewportl.spigotlike.gui.inventoryui.rendering.SpigotLikeInvUIRenderer
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.Bukkit

class SpigotInvUIRenderer : SpigotLikeInvUIRenderer() {

    override fun onWindowOpen(runtime: ViewRuntime, window: Window) {
        val guiHolder: GuiHolder = GuiHolderImpl(window, runtime)
        val holder = BukkitInventoryGuiHolder(guiHolder)
        val title: String? = window.title?.let { LegacyComponentSerializer.legacySection().serialize(it) }

        val inventoryType = getInventoryType(window)
        inventory = if (inventoryType != null) {
            title?.let {
                Bukkit.createInventory(holder, inventoryType, title)
            } ?: Bukkit.createInventory(holder, inventoryType)
        } else {
            title?.let {
                Bukkit.createInventory(
                    holder,
                    window.size ?: throw IllegalStateException("Invalid window type/size definition."),
                    title
                )
            } ?: Bukkit.createInventory(
                holder,
                window.size ?: throw IllegalStateException("Invalid window type/size definition.")
            )
        }

        holder.setActiveInventory(inventory)
    }

}