package com.wolfyscript.viewportl.common.gui.inventoryui.rendering

import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.common.gui.rendering.ComponentRenderer
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.components.*
import com.wolfyscript.viewportl.gui.elements.Element
import com.wolfyscript.viewportl.gui.model.Node
import com.wolfyscript.viewportl.gui.model.NodeAddedEvent
import com.wolfyscript.viewportl.gui.model.NodeRemovedEvent
import com.wolfyscript.viewportl.gui.model.NodeUpdatedEvent
import com.wolfyscript.viewportl.gui.rendering.Renderer
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap

abstract class InvUIRenderer<Self: InvUIRenderer<Self, T>, T : InvUIRenderContext> : Renderer<Self, T> {

    companion object {

        private val componentRenderersForUIRenderer : MutableMap<Class<out InvUIRenderer<*, *>>, MutableMap<Key, ComponentRenderer<*, out InvUIRenderContext>>> = mutableMapOf()

        @Suppress("UNCHECKED_CAST")
        fun <X : InvUIRenderContext, R : InvUIRenderer<*, X>, C : Element> getComponentRenderer(uiRendererType: Class<R>, type: Key): ComponentRenderer<C, X>? {
            val handler: ComponentRenderer<*, out InvUIRenderContext>? = componentRenderersForUIRenderer.getOrPut(uiRendererType) { mutableMapOf() }[type]
            return handler as? ComponentRenderer<C, X>?
        }

        fun <X : InvUIRenderContext, R : InvUIRenderer<*, X>, C : Element> registerComponentRenderer(
            uiRendererType: Class<R>,
            type: Key,
            handler: ComponentRenderer<in C, X>
        ) {
            componentRenderersForUIRenderer.getOrPut(uiRendererType) { mutableMapOf() }[type] = handler
        }

    }

    override lateinit var runtime: ViewRuntime<Self, *>

    override fun init(runtime: ViewRuntime<Self, *>) {
        this.runtime = runtime
    }

    val computed: MutableMap<Long, CachedNodeRenderProperties> = Long2ObjectOpenHashMap()

    fun renderChildren(parent: Long, context: T) {
        for (child in runtime.model.children(parent)) {
            renderChildOf(child, parent, context)
        }
    }

    abstract fun createContext(): T

    abstract fun clearSlots(slots: Collection<Int>)

    private fun renderChildOf(child: Long, parent: Long, context: T) {
        runtime.model.getNode(child)?.let {
            // Direct rendering to specific component renderer
            renderComponent(it.element, it, parent, context)

            renderChildren(it.id, context)
        }
    }

    private inline fun <reified C : Element> renderComponent(component: C, node: Node, parent: Long, context: T) {
        val nextOffset = calculatePosition(node, context)
        val offset = context.currentOffset()

        val componentRenderer : ComponentRenderer<C, T>? = getComponentRenderer(this::class.java, component.type())
        componentRenderer?.let { renderer ->
            renderer.render(context, component)

            computed[node.id] = CachedNodeRenderProperties(offset, mutableSetOf(offset))
            // Store the slots affected by this node, so the slots can be easily cleared
            computed[parent]?.slots?.add(offset)

            context.setSlotOffset(nextOffset)
        }

    }

    /* ************************************************************* *
     *  Listen to changes to the model graph & rerender accordingly  *
     * ************************************************************* */

    private fun calculatePosition(node: Node, context: InvUIRenderContext): Int {
        val nextOffset = node.element.styles.position.slotPositioning()?.let {
            context.setSlotOffset(it.slot())
            return@let it.slot() + 1
        } ?: run {
            return context.currentOffset() + 1
        }
        val offset = context.currentOffset()
        computed[node.id] = CachedNodeRenderProperties(offset, mutableSetOf(offset))
        return nextOffset
    }


    override fun onNodeAdded(event: NodeAddedEvent) {
        val context = createContext()
        val parent = runtime.model.parent(event.node.id)?.let { runtime.model.getNode(it) }
        if (parent != null) {
            context.setSlotOffset(0)
            computed[parent.id]?.let {
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
        val removedProperties = computed.remove(event.node.id)
        clearSlots(removedProperties?.slots ?: emptySet()) // clear slots affected by the removed node

        // Does it have a parent? if so unlink it
        val parent = runtime.model.parent(event.node.id)
        if (parent != null) {
            computed[parent]?.let { parentProperties ->
                removedProperties?.slots?.let {
                    parentProperties.slots.removeAll(it) // Remove unmarked slots from parent
                }
            }
        }
    }

    override fun onNodeUpdated(event: NodeUpdatedEvent) {
        val context = createContext()
        val parent = runtime.model.parent(event.node.id)?.let { runtime.model.getNode(it) }
        if (parent != null) {
            context.setSlotOffset(0)
            computed[parent.id]?.let {
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