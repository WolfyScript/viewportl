/*
 *     viewportl - multiplatform GUI framework to easily create reactive GUIs
 *     Copyright (C) 2024  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wolfyscript.viewportl.spigot.gui.inventoryui.rendering

import com.wolfyscript.scafall.eval.context.EvalContext
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.scafall.platform.PlatformType
import com.wolfyscript.scafall.spigot.api.wrappers.unwrap
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.scafall.wrappers.world.items.ItemStackConfig
import com.wolfyscript.viewportl.common.gui.GuiHolderImpl
import com.wolfyscript.viewportl.common.gui.elements.ButtonImpl
import com.wolfyscript.viewportl.common.gui.elements.SlotImpl
import com.wolfyscript.viewportl.common.gui.inventoryui.rendering.CachedNodeRenderProperties
import com.wolfyscript.viewportl.common.gui.inventoryui.rendering.InvUIRenderer
import com.wolfyscript.viewportl.gui.GuiHolder
import com.wolfyscript.viewportl.gui.ItemStackContext
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.WindowType
import com.wolfyscript.viewportl.spigot.gui.inventoryui.BukkitInventoryGuiHolder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.craftbukkit.BukkitComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import java.util.*

class SpigotInvUIRenderer : InvUIRenderer<SpigotInvUIRenderer, SpigotInvUIRenderContext>() {

    companion object {

        init {
            registerComponentRenderer(SpigotInvUIRenderer::class.java, Key.parse(StaticNamespacedKey.KeyBuilder.createKeyString(
                ButtonImpl::class.java)), InventoryButtonComponentRenderer())
            registerComponentRenderer(SpigotInvUIRenderer::class.java, Key.parse(StaticNamespacedKey.KeyBuilder.createKeyString(
                SlotImpl::class.java)), InventorySlotComponentRenderer())
        }

    }

    private var inventory: Inventory? = Bukkit.createInventory(null, 27)

    override fun createContext(): SpigotInvUIRenderContext {
        return SpigotInvUIRenderContext(this)
    }

    override fun onWindowOpen(window: Window) {
        val guiHolder: GuiHolder = GuiHolderImpl(window, runtime)
        val holder = BukkitInventoryGuiHolder(guiHolder)
        val title: Component? = window.title

        inventory = if (window.scaffolding.platformManager.platformType == PlatformType.PAPER) {
            // Paper has direct Adventure support, so use it for better titles!
            getInventoryType(window)?.let { inventoryType ->
                title?.let { Bukkit.createInventory(holder, inventoryType, it) } ?: Bukkit.createInventory(
                    holder,
                    inventoryType
                )
            } ?: run {
                if (title != null) {
                    return@run Bukkit.createInventory(
                        holder,
                        window.size ?: throw IllegalStateException("Invalid window type/size definition."),
                        title
                    )
                }
                return@run Bukkit.createInventory(
                    holder,
                    window.size ?: throw IllegalStateException("Invalid window type/size definition.")
                )
            }
        } else {
            getInventoryType(window)?.let { inventoryType ->
                title?.let {
                    Bukkit.createInventory(
                        holder,
                        inventoryType,
                        BukkitComponentSerializer.legacy().serialize(it)
                    )
                } ?: Bukkit.createInventory(holder, inventoryType)
            } ?: run {
                if (title != null) {
                    return@run Bukkit.createInventory(
                        holder,
                        window.size ?: throw IllegalStateException("Invalid window type/size definition."),
                        BukkitComponentSerializer.legacy().serialize(title)
                    )
                }
                return@run Bukkit.createInventory(
                    holder,
                    window.size ?: throw IllegalStateException("Invalid window type/size definition.")
                )
            }
        }
        holder.setActiveInventory(inventory)
    }

    override fun render() {
        if (inventory == null) return

        val context = createContext()
        computed[0] = CachedNodeRenderProperties(0, mutableSetOf(0))
        context.setSlotOffset(0)

        renderChildren(0, context)

        runtime.viewers.forEach {
            Bukkit.getPlayer(it)?.openInventory(inventory!!)
        }
    }

    private fun getInventoryType(window: Window): InventoryType? {
        return window.type?.let { type: WindowType? ->
            when (type) {
                WindowType.CUSTOM -> InventoryType.CHEST
                WindowType.HOPPER -> InventoryType.HOPPER
                WindowType.DROPPER -> InventoryType.DROPPER
                WindowType.DISPENSER -> InventoryType.DISPENSER
                null -> InventoryType.CHEST
            }
        }
    }

    override fun updateTitle(component: Component?) {
        inventory?.viewers?.forEach {
            updateTitle(it.uniqueId, component)
        }
    }

    private fun updateTitle(
        player: UUID,
        component: Component?
    ) {
        Bukkit.getPlayer(player)?.let { bukkitPlayer ->
            if (component == null) {
                bukkitPlayer.openInventory.title = bukkitPlayer.openInventory.originalTitle
            } else {
                bukkitPlayer.openInventory.title = BukkitComponentSerializer.legacy().serialize(component)
            }
        }
    }

    fun setStack(i: Int, itemStackConfig: ItemStackConfig?) {
        if (itemStackConfig == null) {
            inventory!!.setItem(i, null)
            return
        }

        inventory!!.setItem(i, itemStackConfig.constructItemStack()?.unwrap())
    }

    fun renderStack(position: Int, itemStack: ItemStack?) {
        if (itemStack == null) {
            setNativeStack(position, null)
            return
        }
        setNativeStack(position, itemStack.unwrap())
    }

    fun renderStack(position: Int, itemStackConfig: ItemStackConfig, itemStackContext: ItemStackContext) {
        setNativeStack(
            position,
            itemStackConfig.constructItemStack(
                EvalContext(),
                runtime.viewportl.scafall.adventure.miniMsg,
                itemStackContext.resolvers
            )?.unwrap()
        )
    }

    private fun setNativeStack(i: Int, itemStack: org.bukkit.inventory.ItemStack?) {
        //checkIfSlotInBounds(i);
        if (itemStack == null) {
            inventory!!.setItem(i, null)
            return
        }
        inventory!!.setItem(i, itemStack)
    }

    override fun clearSlots(slots: Collection<Int>) {
        for (slot in slots) {
            inventory?.clear(slot)
        }
    }
}