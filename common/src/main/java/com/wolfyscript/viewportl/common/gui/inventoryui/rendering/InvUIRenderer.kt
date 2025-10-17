package com.wolfyscript.viewportl.common.gui.inventoryui.rendering

import com.wolfyscript.viewportl.gui.elements.Element
import com.wolfyscript.viewportl.gui.model.ModelGraph
import com.wolfyscript.viewportl.gui.model.Node
import com.wolfyscript.viewportl.gui.model.NodeAddedEvent
import com.wolfyscript.viewportl.gui.model.NodeRemovedEvent
import com.wolfyscript.viewportl.gui.model.NodeUpdatedEvent
import com.wolfyscript.viewportl.gui.rendering.ElementRenderer
import com.wolfyscript.viewportl.gui.rendering.Renderer
import com.wolfyscript.viewportl.registry.ViewportlRegistryTypes
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap

abstract class InvUIRenderer<T : InvUIRenderContext>(val contextType: Class<T>) : Renderer<T> {

    private val renderers: Map<Class<out Element>, ElementRenderer<*, *>>

    init {
        val guiElementTypes = ViewportlRegistryTypes.guiElementTypes.resolveOrThrow()
        val invUIRenderers = ViewportlRegistryTypes.inventoryGuiElementRenderers.resolveOrThrow()
        renderers = buildMap {
            for (elementType in guiElementTypes) {
                invUIRenderers.firstOrNull { renderer ->
                    renderer.canRender(elementType, contextType)
                }?.let { renderer ->
                    this[elementType] = renderer
                }
            }
        }
    }

    val computed: MutableMap<Long, CachedNodeRenderProperties> = Long2ObjectOpenHashMap()

    fun renderChildren(model: ModelGraph, parent: Long, context: T) {
        for (child in model.children(parent)) {
            renderChildOf(model, child, parent, context)
        }
    }

    abstract fun createContext(): T

    abstract fun clearSlots(slots: Collection<Int>)

    private fun renderChildOf(model: ModelGraph, child: Long, parent: Long, context: T) {
        model.getNode(child)?.let {
            // Direct rendering to specific component renderer
            renderElement(it.element, it, parent, context)

            renderChildren(model, it.id, context)
        }
    }

    private inline fun <reified C : Element> renderElement(element: C, node: Node, parent: Long, context: T) {
        val nextOffset = calculatePosition(node, context)
        val offset = context.currentOffset()
        val elementRenderer = (renderers[element::class.java] as? ElementRenderer<C, InvUIRenderContext>) ?: return

        elementRenderer.let { renderer ->
            renderer.render(context, element)

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
        val parent = event.model.parent(event.node.id)?.let { event.model.getNode(it) }
        if (parent != null) {
            context.setSlotOffset(0)
            computed[parent.id]?.let {
                context.setSlotOffset(it.position)
            }
            val nextOffset = calculatePosition(parent, context)
            context.setSlotOffset(nextOffset)
            renderChildOf(event.model, event.node.id, parent.id, context)
        } else {
            context.setSlotOffset(0)
            renderChildOf(event.model, event.node.id, 0, context)
        }
    }

    override fun onNodeRemoved(event: NodeRemovedEvent) {
        // Remove node from cache
        val removedProperties = computed.remove(event.node.id)
        clearSlots(removedProperties?.slots ?: emptySet()) // clearSources slots affected by the removed node

        // Does it have a parent? if so unlink it
        val parent = event.model.parent(event.node.id)
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
        val parent = event.model.parent(event.node.id)?.let { event.model.getNode(it) }
        if (parent != null) {
            context.setSlotOffset(0)
            computed[parent.id]?.let {
                context.setSlotOffset(it.position)
            }
            val nextOffset = calculatePosition(parent, context)
            context.setSlotOffset(nextOffset)
            renderChildOf(event.model, event.node.id, parent.id, context)
        } else {
            context.setSlotOffset(0)
            renderChildOf(event.model, event.node.id, 0, context)
        }
    }

}