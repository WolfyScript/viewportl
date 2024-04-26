package com.wolfyscript.utilities.bukkit.gui.interaction

import com.wolfyscript.utilities.gui.interaction.InteractionResult
import com.wolfyscript.utilities.gui.ViewRuntimeImpl
import com.wolfyscript.utilities.gui.Window
import com.wolfyscript.utilities.gui.components.Button
import com.wolfyscript.utilities.gui.components.StackInputSlot
import com.wolfyscript.utilities.gui.interaction.ClickInteractionDetails
import com.wolfyscript.utilities.gui.interaction.DragInteractionDetails
import com.wolfyscript.utilities.gui.interaction.InteractionDetails
import com.wolfyscript.utilities.gui.interaction.InteractionHandler
import com.wolfyscript.utilities.gui.model.UpdateInformation
import com.wolfyscript.utilities.gui.rendering.RenderingNode

class InventoryGUIInteractionHandler(private val runtime: ViewRuntimeImpl) : InteractionHandler {

    private val slotNodes: MutableMap<Int, Long> = mutableMapOf()
    private val cachedProperties: MutableMap<Long, CachedNodeInteractProperties> = mutableMapOf()

    override fun init(window: Window) {
        val context = InvGUIInteractionContext(this)
        cachedProperties[0] = CachedNodeInteractProperties(0, mutableListOf(0))
        context.setSlotOffset(0)

        initChildren(0, context)
    }

    private fun initChildren(parent: Long, context: InvGUIInteractionContext) {
        for (child in runtime.renderingGraph.children(parent)) {
            initChildOf(child, parent, context)
        }
    }

    private fun initChildOf(child: Long, parent: Long, context: InvGUIInteractionContext) {
        runtime.renderingGraph.getNode(child)?.let {
            val position = calculatePosition(it, context)
            // Mark slot to interact with this node
            slotNodes[position] = child
            cachedProperties[child] = CachedNodeInteractProperties(position, mutableListOf(position))
            // Store the position of this node in the parent, so we can easily clean the slot nodes
            cachedProperties[parent]?.slots?.add(position)

            initChildren(it.id, context)
        }
    }

    private fun calculatePosition(node: RenderingNode, context: InvGUIInteractionContext) : Int {
        val staticPos = node.component.properties.position.slotPositioning()?.slot() ?: (context.currentOffset() + 1)
        context.setSlotOffset(staticPos)

        cachedProperties[node.id] = CachedNodeInteractProperties(staticPos, mutableListOf(staticPos))
        return staticPos
    }

    override fun onInteract(details: InteractionDetails): InteractionResult {
        when (details) {
            is ClickInteractionDetails -> {
                val node = slotNodes[details.slot]?.let { runtime.renderingGraph.getNode(it) }
                if (node != null) {
                    // TODO: Make extensible
                    return when (val component = node.component) {
                        is StackInputSlot -> {
                            InventoryStackSlotInteractionHandler().interact(runtime, component, details)
                        }
                        is Button -> {
                            InventoryButtonInteractionHandler().interact(runtime, component, details)
                        }
                        else -> InteractionResult.cancel(true)
                    }
                }
            }

            is DragInteractionDetails -> {

            }
        }

        return InteractionResult.cancel(true)
    }

    override fun update(info: UpdateInformation) {
        val context = InvGUIInteractionContext(this)

        // Nodes that got added
        for ((sibling, addedNode) in info.added()) {
            runtime.renderingGraph.getNode(addedNode)?.let { node ->
                val slotPositioning = if (node.component.properties.position.slotPositioning() == null) {
                    // Get offset from parent TODO
                    0
                } else {
                    0
                }
                context.setSlotOffset(slotPositioning)
                val parent = runtime.renderingGraph.parent(addedNode) ?: 0
                initChildOf(addedNode, parent, context)
            }
        }

        // Nodes that had their component properties updated
        for (updated in info.updated()) {
            // TODO: Do we need this here?
        }

        // Nodes that got removed
        for (removedNode in info.removed()) {
            runtime.renderingGraph.getNode(removedNode)?.let {

                // Remove node from cache
                val removedProperties = cachedProperties.remove(removedNode)
                removedProperties?.slots?.forEach {
                    slotNodes.remove(it) // Unmark slot, so it no longer interacts with the node
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
}