package com.wolfyscript.viewportl.common.gui.inventoryui.interaction

import com.wolfyscript.viewportl.gui.interaction.ElementInteractionHandler
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.elements.Element
import com.wolfyscript.viewportl.gui.interaction.InteractionContext
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.gui.compose.Node

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
    val slotNodes: MutableMap<Int, Node> = mutableMapOf()
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

        initChildren(Node(), context)
    }

    private fun initChildren(parent: Node, context: InvUIInteractionContext) {
        parent.forEachChild { child ->
            initChildOf(child, parent, context)
        }
    }

    private fun initChildOf(child: Node, parent: Node, context: InvUIInteractionContext) {
            // Mark slot to interact with this node
            // Only mark components that have an interaction handler
            child.element?.let { element ->
                getComponentInteractionHandler(element.javaClass)?.let {
                    val nextOffset = calculatePosition(child, context)
                    val offset = context.currentOffset()
                    slotNodes[offset] = child
                    cachedProperties[child.id] = CachedNodeInteractProperties(offset, mutableListOf(offset))

                    // Store the position of this node in the parent, so we can easily clean the slot nodes
                    cachedProperties[parent.id]?.slots?.add(offset)
                    context.setSlotOffset(nextOffset)
                }
            }

            initChildren(child, context)
    }

    private fun calculatePosition(node: Node, context: InvUIInteractionContext): Int {
        return 0
    }

}