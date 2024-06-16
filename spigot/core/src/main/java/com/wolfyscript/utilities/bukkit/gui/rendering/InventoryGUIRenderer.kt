package com.wolfyscript.utilities.bukkit.gui.rendering

import com.wolfyscript.utilities.bukkit.WolfyCoreCommon
import com.wolfyscript.utilities.bukkit.adapters.ItemStackImpl
import com.wolfyscript.utilities.bukkit.gui.interaction.BukkitInventoryGuiHolder
import com.wolfyscript.utilities.bukkit.nms.inventory.InventoryUpdate
import com.wolfyscript.utilities.bukkit.world.items.BukkitItemStackConfig
import com.wolfyscript.viewportl.gui.*
import com.wolfyscript.viewportl.gui.components.Button
import com.wolfyscript.viewportl.gui.components.ComponentGroup
import com.wolfyscript.viewportl.gui.components.Outlet
import com.wolfyscript.viewportl.gui.components.StackInputSlot
import com.wolfyscript.viewportl.gui.model.UpdateInformation
import com.wolfyscript.viewportl.gui.rendering.Renderer
import com.wolfyscript.viewportl.gui.rendering.Node
import com.wolfyscript.utilities.platform.adapters.ItemStack
import com.wolfyscript.utilities.versioning.MinecraftVersion
import com.wolfyscript.utilities.versioning.ServerVersion
import com.wolfyscript.utilities.world.items.ItemStackConfig
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

        inventory = if (window.wolfyUtils.core.platform.type.isPaper()) {
            // Paper has direct Adventure support, so use it for better titles!
            getInventoryType(window)?.let { inventoryType ->
                title?.let { Bukkit.createInventory(holder, inventoryType, it) } ?: Bukkit.createInventory(holder, inventoryType)
            } ?: run {
                if (title != null) {
                    return@run Bukkit.createInventory(holder, window.size ?: throw IllegalStateException("Invalid window type/size definition."), title)
                }
                return@run Bukkit.createInventory(holder, window.size ?: throw IllegalStateException("Invalid window type/size definition."))
            }
        } else {
            getInventoryType(window)?.let { inventoryType ->
                title?.let { Bukkit.createInventory(holder, inventoryType, BukkitComponentSerializer.legacy().serialize(it)) } ?: Bukkit.createInventory(holder, inventoryType)
            } ?: run {
                if (title != null) {
                    return@run Bukkit.createInventory(holder, window.size ?: throw IllegalStateException("Invalid window type/size definition."), BukkitComponentSerializer.legacy().serialize(title))
                }
                return@run Bukkit.createInventory(holder, window.size ?: throw IllegalStateException("Invalid window type/size definition."))
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
        for (child in runtime.modelGraph.children(parent)) {
            renderChildOf(child, parent, context)
        }
    }

    private fun renderChildOf(child: Long, parent: Long, context: InvGUIRenderContext) {
        runtime.modelGraph.getNode(child)?.let {
            val nextOffset = calculatePosition(it, context)
            val offset = context.currentOffset()

            // Direct rendering to specific component renderer TODO: Make extensible
            when (val component = it.component) {
                is Button -> InventoryButtonComponentRenderer().render(context, component)
                is ComponentGroup -> InventoryGroupComponentRenderer().render(context, component)
                is Outlet -> component.component?.apply { InventoryGroupComponentRenderer().render(context, this) }
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
        val nextOffset = node.component.styles.position.slotPositioning()?.let {
            context.setSlotOffset(it.slot())
            return@let it.slot() + 1
        } ?: run {
            return context.currentOffset() + 1
        }
        val offset = context.currentOffset()
        cachedProperties[node.id] = CachedNodeRenderProperties(offset, mutableSetOf(offset))
        return nextOffset
    }

    override fun update(information: UpdateInformation) {
        val context = InvGUIRenderContext(this)
        for ((_, addedNode) in information.added()) {
            runtime.modelGraph.getNode(addedNode)?.let { node ->
                val parent = runtime.modelGraph.parent(node.id)?.let { runtime.modelGraph.getNode(it) }
                if (parent != null) {
                    context.setSlotOffset(0)
                    cachedProperties[parent.id]?.let {
                        context.setSlotOffset(it.position)
                    }
                    val nextOffset = calculatePosition(parent, context)
                    context.setSlotOffset(nextOffset)
                    renderChildOf(addedNode, parent.id, context)
                } else {
                    context.setSlotOffset(0)
                    renderChildOf(addedNode, 0, context)
                }
            }
        }

        for (updated in information.updated()) {
            runtime.modelGraph.getNode(updated)?.let { node ->
                val parent = runtime.modelGraph.parent(node.id)?.let { runtime.modelGraph.getNode(it) }
                if (parent != null) {
                    context.setSlotOffset(0)
                    cachedProperties[parent.id]?.let {
                        context.setSlotOffset(it.position)
                    }
                    val nextOffset = calculatePosition(parent, context)
                    context.setSlotOffset(nextOffset)
                    renderChildOf(updated, parent.id, context)
                } else {
                    context.setSlotOffset(0)
                    renderChildOf(updated, 0, context)
                }
            }
        }

        for (removedNode in information.removed()) {
            runtime.modelGraph.getNode(removedNode)?.let {

                // Remove node from cache
                val removedProperties = cachedProperties.remove(removedNode)
                removedProperties?.slots?.forEach {
                    inventory?.clear(it) // clear slots affected by the removed node
                }

                // Does it have a parent? if so unlink it
                val parent = runtime.modelGraph.parent(removedNode)
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
            if (ServerVersion.isAfterOrEq(MinecraftVersion.of(1, 20, 0))) {
                if (component == null) {
                    bukkitPlayer.openInventory.title = bukkitPlayer.openInventory.originalTitle
                } else {
                    bukkitPlayer.openInventory.title = net.kyori.adventure.platform.bukkit.BukkitComponentSerializer.legacy().serialize(component)
                }
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

        inventory!!.setItem(i, itemStackConfig.constructItemStack()?.bukkitRef)
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

}