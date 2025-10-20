package com.wolfyscript.viewportl.common.gui.inventoryui.rendering

import com.wolfyscript.viewportl.gui.elements.Element
import com.wolfyscript.viewportl.gui.compose.Node
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

    abstract fun createContext(): T

    abstract fun clearSlots(slots: Collection<Int>)

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
        return 0
    }

}