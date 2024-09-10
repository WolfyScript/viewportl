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
import com.wolfyscript.scafall.platform.PlatformType
import com.wolfyscript.scafall.spigot.api.wrappers.unwrap
import com.wolfyscript.scafall.spigot.api.wrappers.world.items.BukkitItemStackConfig
import com.wolfyscript.scafall.spigot.api.wrappers.world.items.ItemStackImpl
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.scafall.wrappers.world.items.ItemStackConfig
import com.wolfyscript.viewportl.common.gui.GuiHolderImpl
import com.wolfyscript.viewportl.common.gui.components.ButtonImpl
import com.wolfyscript.viewportl.common.gui.components.GroupImpl
import com.wolfyscript.viewportl.common.gui.inventoryui.rendering.CachedNodeRenderProperties
import com.wolfyscript.viewportl.common.gui.inventoryui.rendering.InvUIRenderer
import com.wolfyscript.viewportl.gui.*
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
            registerComponentRenderer(SpigotInvUIRenderer::class.java, ButtonImpl::class.java, InventoryButtonComponentRenderer())
            registerComponentRenderer(SpigotInvUIRenderer::class.java, GroupImpl::class.java, InventoryGroupComponentRenderer())
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

        inventory = if (window.scaffolding.platformType == PlatformType.PAPER) {
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
        cachedProperties[0] = CachedNodeRenderProperties(0, mutableSetOf(0))
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
        require(itemStackConfig is BukkitItemStackConfig) {
            String.format(
                "Cannot render stack config! Invalid stack config type! Expected '%s' but received '%s'.",
                BukkitItemStackConfig::class.java.name,
                itemStackConfig.javaClass.name
            )
        }

        inventory!!.setItem(i, itemStackConfig.constructItemStack()?.unwrap())
    }

    fun renderStack(position: Int, itemStack: ItemStack?) {
        if (itemStack == null) {
            setNativeStack(position, null)
            return
        }
        require(itemStack is ItemStackImpl) {
            String.format(
                "Cannot render stack! Invalid stack config type! Expected '%s' but received '%s'.",
                ItemStackImpl::class.java.name,
                itemStack.javaClass.name
            )
        }

        setNativeStack(position, itemStack.bukkitRef)
    }

    fun renderStack(position: Int, itemStackConfig: ItemStackConfig, itemStackContext: ItemStackContext) {
        require(itemStackConfig is BukkitItemStackConfig) {
            String.format(
                "Cannot render stack config! Invalid stack config type! Expected '%s' but received '%s'.",
                BukkitItemStackConfig::class.java.name,
                itemStackConfig.javaClass.name
            )
        }

        setNativeStack(
            position,
            itemStackConfig.constructItemStack(
                EvalContext(),
                runtime.viewportl.scafall.adventure.miniMsg,
                itemStackContext.resolvers
            )?.bukkitRef
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