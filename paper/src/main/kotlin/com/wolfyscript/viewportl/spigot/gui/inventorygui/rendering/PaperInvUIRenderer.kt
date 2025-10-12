package com.wolfyscript.viewportl.spigot.gui.inventorygui.rendering

import com.wolfyscript.viewportl.common.gui.GuiHolderImpl
import com.wolfyscript.viewportl.gui.GuiHolder
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.spigot.gui.inventorygui.PaperInventoryGuiHolder
import com.wolfyscript.viewportl.spigotlike.gui.inventoryui.rendering.SpigotLikeInvUIRenderer
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit

class PaperInvUIRenderer() : SpigotLikeInvUIRenderer() {

    override fun onWindowOpen(window: Window) {
        val guiHolder: GuiHolder = GuiHolderImpl(window, runtime)
        val holder = PaperInventoryGuiHolder(guiHolder)
        val title: Component? = window.title

        val inventoryType = getInventoryType(window)
        // Paper has direct Adventure support, so use it for better titles!
        inventory = if (inventoryType != null) {
            title?.let {
                Bukkit.createInventory(holder, inventoryType, it)
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