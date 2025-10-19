package com.wolfyscript.viewportl.common.gui.inventoryui.interaction

import com.wolfyscript.viewportl.gui.interaction.ElementInteractionHandler
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.elements.Element
import com.wolfyscript.viewportl.gui.interaction.InteractionContext
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.gui.model.Node
import com.wolfyscript.viewportl.gui.model.NodeAddedEvent
import com.wolfyscript.viewportl.gui.model.NodeRemovedEvent
import com.wolfyscript.viewportl.gui.model.NodeUpdatedEvent

abstract class InvUIInteractionHandler<C: InteractionContext> : InteractionHandler<C> {

    companion object {
        private val elementInteractionHandlers: MutableMap<Class<out Element>, ElementInteractionHandler<*>> = mutableMapOf()

        @Suppress("UNCHECKED_CAST")
        fun <C : Element> getComponentInteractionHandler(type: Class<C>): ElementInteractionHandler<C>? {
            val handler: ElementInteractionHandler<*>? = elementInteractionHandlers[type]
            return handler as ElementInteractionHandler<C>?
        }

        fun <C : Element> registerComponentInteractionHandler(
            type: Class<C>,
            handler: ElementInteractionHandler<in C>
        ) {
            elementInteractionHandlers[type] = handler
        }

    }

    lateinit var runtime: ViewRuntime
    val slotNodes: MutableMap<Int, Long> = mutableMapOf()
    val cachedProperties: MutableMap<Long, CachedNodeInteractProperties> = mutableMapOf()

    override fun init(runtime: ViewRuntime) {
        this.runtime = runtime
    }

    override fun dispose() {

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
            node.element?.let { element ->
                getComponentInteractionHandler(element.javaClass)?.let {
                    val nextOffset = calculatePosition(node, context)
                    val offset = context.currentOffset()
                    slotNodes[offset] = child
                    cachedProperties[child] = CachedNodeInteractProperties(offset, mutableListOf(offset))

                    // Store the position of this node in the parent, so we can easily clean the slot nodes
                    cachedProperties[parent]?.slots?.add(offset)
                    context.setSlotOffset(nextOffset)
                }
            }

            initChildren(node.id, context)
        }
    }

    private fun calculatePosition(node: Node, context: InvUIInteractionContext): Int {
        return 0
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