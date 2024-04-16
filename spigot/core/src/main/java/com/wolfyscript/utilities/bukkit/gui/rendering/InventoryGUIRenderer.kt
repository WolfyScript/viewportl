package com.wolfyscript.utilities.bukkit.gui.rendering

import com.wolfyscript.utilities.bukkit.WolfyCoreCommon
import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl
import com.wolfyscript.utilities.bukkit.gui.BukkitInventoryGuiHolder
import com.wolfyscript.utilities.bukkit.nms.inventory.InventoryUpdate
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig
import com.wolfyscript.utilities.gui.*
import com.wolfyscript.utilities.gui.components.Button
import com.wolfyscript.utilities.gui.components.ComponentGroup
import com.wolfyscript.utilities.gui.components.StackInputSlot
import com.wolfyscript.utilities.gui.model.UpdateInformation
import com.wolfyscript.utilities.gui.rendering.Renderer
import com.wolfyscript.utilities.gui.rendering.RenderingNode
import com.wolfyscript.utilities.platform.adapters.ItemStack
import com.wolfyscript.utilities.versioning.MinecraftVersion
import com.wolfyscript.utilities.versioning.ServerVersion
import com.wolfyscript.utilities.world.items.ItemStackConfig
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
        val title: net.kyori.adventure.text.Component = window.title()

        inventory = if (window.wolfyUtils.core.platform.type.isPaper()) {
            // Paper has direct Adventure support, so use it for better titles!
            getInventoryType(window).map { inventoryType: InventoryType? ->
                Bukkit.createInventory(holder, inventoryType!!, title)
            }.orElseGet {
                Bukkit.createInventory(holder, window.size.orElseThrow {
                    IllegalStateException("Invalid window type/size definition.")
                }, title)
            }
        } else {
            getInventoryType(window).map { inventoryType: InventoryType? ->
                Bukkit.createInventory(holder, inventoryType!!, BukkitComponentSerializer.legacy().serialize(title))
            }.orElseGet {
                Bukkit.createInventory(holder, window.size.orElseThrow {
                    IllegalStateException("Invalid window type/size definition.")
                }, BukkitComponentSerializer.legacy().serialize(title))
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
        for (child in runtime.renderingGraph.children(parent)) {
            renderChildOf(child, parent, context)
        }
    }

    private fun renderChildOf(child: Long, parent: Long, context: InvGUIRenderContext) {
        runtime.renderingGraph.getNode(child)?.let {
            val position = calculatePosition(it, context)

            // Direct rendering to specific component renderer TODO: Make extensible
            when (val component = it.component) {
                is Button -> InventoryButtonComponentRenderer().render(context, component)
                is ComponentGroup -> InventoryGroupComponentRenderer().render(context, component)
                is StackInputSlot -> {}
            }
            cachedProperties[child] = CachedNodeRenderProperties(position, mutableSetOf(position))
            // Store the slots affected by this node, so the slots can be easily cleared
            cachedProperties[parent]?.slots?.add(position)

            renderChildren(it.id, context)
        }
    }

    private fun calculatePosition(node: RenderingNode, context: InvGUIRenderContext): Int {
        val staticPos = node.component.properties().position().slotPositioning()?.slot() ?: (context.currentOffset() + 1)
        context.setSlotOffset(staticPos)

        cachedProperties[node.id] = CachedNodeRenderProperties(staticPos, mutableSetOf(staticPos))
        return staticPos
    }

    override fun update(information: UpdateInformation) {

        if (information.updateTitle()) {
            runtime.viewers.forEach { viewer ->
                runtime.currentMenu.ifPresent {
                    updateTitle(viewer, it.title())
                }
            }
        }

        val context = InvGUIRenderContext(this)
        for ((_, addedNode) in information.added()) {
            runtime.renderingGraph.getNode(addedNode)?.let { node ->
                val slotPositioning = if (node.component.properties().position().slotPositioning() == null) {
                    // Get offset from parent TODO
                    0
                } else {
                    0
                }
                context.setSlotOffset(slotPositioning)
                val parent = runtime.renderingGraph.parent(addedNode) ?: 0
                renderChildOf(addedNode, parent, context)
            }
        }

        for (updated in information.updated()) {
            runtime.renderingGraph.getNode(updated)?.let { node ->
                val slotPositioning = if (node.component.properties().position().slotPositioning() == null) {
                    // Get offset from parent TODO
                    0
                } else {
                    0
                }
                context.setSlotOffset(slotPositioning)
                val parent = runtime.renderingGraph.parent(updated) ?: 0
                renderChildOf(updated, parent, context)
            }
        }

        for (removedNode in information.removed()) {
            runtime.renderingGraph.getNode(removedNode)?.let {

                // Remove node from cache
                val removedProperties = cachedProperties.remove(removedNode)
                removedProperties?.slots?.forEach {
                    inventory?.clear(it) // clear slots affected by the removed node
                }

                // Does it have a parent? if so unlink it
                val parent = runtime.renderingGraph.parent(removedNode)
                if (parent != null) {
                    cachedProperties[parent]?.let { parentProperties ->
                        removedProperties?.slots?.let {
                            parentProperties.slots.removeAll(it) // Remove unmarked slots from parent
                        }
                    }
                }
            }
        }
    }

    private fun getInventoryType(window: Window): Optional<InventoryType> {
        return window.type.map { type: WindowType? ->
            when (type) {
                WindowType.CUSTOM -> InventoryType.CHEST
                WindowType.HOPPER -> InventoryType.HOPPER
                WindowType.DROPPER -> InventoryType.DROPPER
                WindowType.DISPENSER -> InventoryType.DISPENSER
                null -> InventoryType.CHEST
            }
        }
    }

    private fun updateTitle(
        player: UUID,
        component: net.kyori.adventure.text.Component
    ) {
        Bukkit.getPlayer(player)?.let { bukkitPlayer ->
            if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0))) {
                bukkitPlayer.openInventory.title =
                    net.kyori.adventure.platform.bukkit.BukkitComponentSerializer.legacy().serialize(
                        component
                    )
            } else {
                InventoryUpdate.updateInventory(
                    (runtime.wolfyUtils.core as WolfyCoreCommon).wolfyUtils.plugin,
                    bukkitPlayer,
                    component
                )
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
                BukkitItemStackConfig::class.java.name, itemStackConfig.javaClass.name
            )
        }

        inventory!!.setItem(i, itemStackConfig.constructItemStack().bukkitRef)
    }

    fun renderStack(position: Int, itemStack: ItemStack?) {
        if (itemStack == null) {
            setNativeStack(position, null)
            return
        }
        require(itemStack is ItemStackImpl) {
            String.format(
                "Cannot render stack! Invalid stack config type! Expected '%s' but received '%s'.",
                ItemStackImpl::class.java.name, itemStack.javaClass.name
            )
        }

        setNativeStack(position, itemStack.bukkitRef)
    }

    fun renderStack(position: Int, itemStackConfig: ItemStackConfig, itemStackContext: ItemStackContext) {
        require(itemStackConfig is BukkitItemStackConfig) {
            String.format(
                "Cannot render stack config! Invalid stack config type! Expected '%s' but received '%s'.",
                BukkitItemStackConfig::class.java.name, itemStackConfig.javaClass.name
            )
        }

        setNativeStack(
            position,
            itemStackConfig.constructItemStack(
                null,
                runtime.wolfyUtils.chat.miniMessage,
                itemStackContext.resolvers()
            ).bukkitRef
        )
    }

    fun setNativeStack(i: Int, itemStack: org.bukkit.inventory.ItemStack?) {
        //checkIfSlotInBounds(i);
        if (itemStack == null) {
            inventory!!.setItem(i, null)
            return
        }
        inventory!!.setItem(i, itemStack)
    }

}