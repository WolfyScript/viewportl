package com.wolfyscript.viewportl.common.gui.inventoryui.interaction

import com.wolfyscript.viewportl.common.gui.interaction.ComponentInteractionHandler
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.components.NativeComponent
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.gui.model.*

abstract class InvUIInteractionHandler<Self: InvUIInteractionHandler<Self>> : InteractionHandler<Self> {

    companion object {
        private val nativeComponentInteractionHandlers: MutableMap<Class<out NativeComponent>, ComponentInteractionHandler<*>> = mutableMapOf()

        @Suppress("UNCHECKED_CAST")
        fun <C : NativeComponent> getComponentInteractionHandler(type: Class<C>): ComponentInteractionHandler<C>? {
            val handler: ComponentInteractionHandler<*>? = nativeComponentInteractionHandlers[type]
            return handler as ComponentInteractionHandler<C>?
        }

        fun <C : NativeComponent> registerComponentInteractionHandler(
            type: Class<C>,
            handler: ComponentInteractionHandler<in C>
        ) {
            nativeComponentInteractionHandlers[type] = handler
        }

    }

    lateinit var runtime: ViewRuntime<*, Self>
    val slotNodes: MutableMap<Int, Long> = mutableMapOf()
    val cachedProperties: MutableMap<Long, CachedNodeInteractProperties> = mutableMapOf()

    override fun init(runtime: ViewRuntime<*, Self>) {
        this.runtime = runtime
    }

    override fun onWindowOpen(window: Window) {
        val context = InvUIInteractionContext(this)
        cachedProperties[0] = CachedNodeInteractProperties(0, mutableListOf(0))
        context.setSlotOffset(0)

        initChildren(0, context)
    }

    private fun initChildren(parent: Long, context: InvUIInteractionContext) {
        for (child in runtime.model.children(parent)) {
            initChildOf(child, parent, context)
        }
    }

    private fun initChildOf(child: Long, parent: Long, context: InvUIInteractionContext) {
        runtime.model.getNode(child)?.let { node ->

            // Mark slot to interact with this node
            // Only mark components that have an interaction handler
            getComponentInteractionHandler(node.nativeComponent.javaClass)?.let {
                val nextOffset = calculatePosition(node, context)
                val offset = context.currentOffset()
                slotNodes[offset] = child
                cachedProperties[child] = CachedNodeInteractProperties(offset, mutableListOf(offset))

                // Store the position of this node in the parent, so we can easily clean the slot nodes
                cachedProperties[parent]?.slots?.add(offset)
                context.setSlotOffset(nextOffset)
            }

            initChildren(node.id, context)
        }
    }

    private fun calculatePosition(node: Node, context: InvUIInteractionContext): Int {
        val nextOffset = node.nativeComponent.styles.position.slotPositioning()?.let {
            context.setSlotOffset(it.slot())
            return@let it.slot() + 1
        } ?: run {
            return context.currentOffset() + 1
        }
        val offset = context.currentOffset()
        cachedProperties[node.id] = CachedNodeInteractProperties(offset, mutableListOf(offset))
        return nextOffset
    }

    /* *********************************************************************** *
     *  Listen to changes to the model graph & update interaction accordingly  *
     * *********************************************************************** */

    override fun onNodeAdded(event: NodeAddedEvent) {
        val context = InvUIInteractionContext(this)
        val parent = runtime.model.parent(event.node.id)?.let { runtime.model.getNode(it) }
        if (parent != null) {
            context.setSlotOffset(0)
            cachedProperties[parent.id]?.let {
                context.setSlotOffset(it.position)
            }
            val nextOffset = calculatePosition(parent, context)
            context.setSlotOffset(nextOffset)
            initChildOf(event.node.id, parent.id, context)
        } else {
            context.setSlotOffset(0)
            initChildOf(event.node.id, 0, context)
        }
    }

    override fun onNodeRemoved(event: NodeRemovedEvent) {
        // Remove node from cache
        val removedProperties = cachedProperties.remove(event.node.id)
        removedProperties?.slots?.forEach {
            slotNodes.remove(it) // Unmark slot, so it no longer interacts with the node
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
        // TODO: Do we need this here?
    }
}