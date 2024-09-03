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
import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.gui.*
import com.wolfyscript.viewportl.gui.components.Button
import com.wolfyscript.viewportl.gui.components.NativeComponentGroup
import com.wolfyscript.viewportl.gui.components.Outlet
import com.wolfyscript.viewportl.gui.components.StackInputSlot
import com.wolfyscript.viewportl.gui.model.Node
import com.wolfyscript.viewportl.gui.model.NodeAddedEvent
import com.wolfyscript.viewportl.gui.model.NodeRemovedEvent
import com.wolfyscript.viewportl.gui.model.NodeUpdatedEvent
import com.wolfyscript.viewportl.gui.rendering.Renderer
import com.wolfyscript.viewportl.spigot.gui.inventoryui.BukkitInventoryGuiHolder
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.craftbukkit.BukkitComponentSerializer
import org.bukkit.Bukkit
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.Inventory
import java.util.*

class InventoryGUIRenderer(val runtime: ViewRuntimeImpl) : Renderer<InvGUIRenderContext> {

    private var inventory: Inventory? = Bukkit.createInventory(null, 27)
    private val cachedProperties: MutableMap<Long, CachedNodeRenderProperties> = mutableMapOf()

    override fun changeWindow(window: Window) {
        val guiHolder: GuiHolder = GuiHolderImpl(window, runtime, null)
        val holder = BukkitInventoryGuiHolder(runtime, guiHolder)
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

        val context = InvGUIRenderContext(this)
        cachedProperties[0] = CachedNodeRenderProperties(0, mutableSetOf(0))
        context.setSlotOffset(0)

        renderChildren(0, context)

        runtime.viewers.forEach {
            Bukkit.getPlayer(it)?.openInventory(inventory!!)
        }
    }

    private fun renderChildren(parent: Long, context: InvGUIRenderContext) {
        for (child in runtime.model.children(parent)) {
            renderChildOf(child, parent, context)
        }
    }

    private fun renderChildOf(child: Long, parent: Long, context: InvGUIRenderContext) {
        runtime.model.getNode(child)?.let {
            val nextOffset = calculatePosition(it, context)
            val offset = context.currentOffset()

            // Direct rendering to specific component renderer TODO: Make extensible
            when (val component = it.nativeComponent) {
                is Button -> InventoryButtonComponentRenderer().render(context, component)
                is NativeComponentGroup -> InventoryGroupComponentRenderer().render(context, component)
                is Outlet -> {}
                is StackInputSlot -> {}
            }
            cachedProperties[child] = CachedNodeRenderProperties(offset, mutableSetOf(offset))
            // Store the slots affected by this node, so the slots can be easily cleared
            cachedProperties[parent]?.slots?.add(offset)

            context.setSlotOffset(nextOffset)

            renderChildren(it.id, context)
        }
    }

    private fun calculatePosition(node: Node, context: InvGUIRenderContext): Int {
        val nextOffset = node.nativeComponent.styles.position.slotPositioning()?.let {
            context.setSlotOffset(it.slot())
            return@let it.slot() + 1
        } ?: run {
            return context.currentOffset() + 1
        }
        val offset = context.currentOffset()
        cachedProperties[node.id] = CachedNodeRenderProperties(offset, mutableSetOf(offset))
        return nextOffset
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

    override fun onNodeAdded(event: NodeAddedEvent) {
        val context = InvGUIRenderContext(this)
        val parent = runtime.model.parent(event.node.id)?.let { runtime.model.getNode(it) }
        if (parent != null) {
            context.setSlotOffset(0)
            cachedProperties[parent.id]?.let {
                context.setSlotOffset(it.position)
            }
            val nextOffset = calculatePosition(parent, context)
            context.setSlotOffset(nextOffset)
            renderChildOf(event.node.id, parent.id, context)
        } else {
            context.setSlotOffset(0)
            renderChildOf(event.node.id, 0, context)
        }
    }

    override fun onNodeRemoved(event: NodeRemovedEvent) {
        // Remove node from cache
        val removedProperties = cachedProperties.remove(event.node.id)
        removedProperties?.slots?.forEach {
            inventory?.clear(it) // clear slots affected by the removed node
        }

        // Does it have a parent? if so unlink it
        val parent = runtime.model.parent(event.node.id)
        if (parent != null) {
            cachedProperties[parent]?.let { parentProperties ->
                removedProperties?.slots?.let {
                    parentProperties.slots.removeAll(it) // Remove unmarked slots from parent
                }
            }
        }
    }

    override fun onNodeUpdated(event: NodeUpdatedEvent) {
        val context = InvGUIRenderContext(this)
        val parent = runtime.model.parent(event.node.id)?.let { runtime.model.getNode(it) }
        if (parent != null) {
            context.setSlotOffset(0)
            cachedProperties[parent.id]?.let {
                context.setSlotOffset(it.position)
            }
            val nextOffset = calculatePosition(parent, context)
            context.setSlotOffset(nextOffset)
            renderChildOf(event.node.id, parent.id, context)
        } else {
            context.setSlotOffset(0)
            renderChildOf(event.node.id, 0, context)
        }
    }

}