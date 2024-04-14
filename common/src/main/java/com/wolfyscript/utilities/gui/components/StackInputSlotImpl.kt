package com.wolfyscript.utilities.gui.components

import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.gui.Component
import com.wolfyscript.utilities.gui.ViewRuntimeImpl
import com.wolfyscript.utilities.gui.callback.InteractionCallback
import com.wolfyscript.utilities.gui.interaction.Interactable
import com.wolfyscript.utilities.gui.rendering.RenderProperties
import com.wolfyscript.utilities.platform.adapters.ItemStack
import java.util.function.Consumer
import java.util.function.Supplier

@KeyedStaticId(key = "stack_input_slot")
class StackInputSlotImpl(
    internalID: String,
    wolfyUtils: WolfyUtils,
    parent: Component?,
    override val onValueChange: Consumer<ItemStack?>?,
    private val interactionCallback: InteractionCallback,
    properties: RenderProperties?
) : AbstractComponentImpl(
    internalID, wolfyUtils, parent, properties!!
), Interactable, StackInputSlot {
    override var value: ItemStack? = null

    override fun width(): Int {
        return 1
    }

    override fun height(): Int {
        return 1
    }

    override fun interactCallback(): InteractionCallback {
        return interactionCallback
    }

    override fun remove(viewRuntimeImpl: ViewRuntimeImpl, nodeId: Long, parentNode: Long) {
        viewRuntimeImpl.renderingGraph.removeNode(nodeId)
    }

    override fun insert(viewRuntimeImpl: ViewRuntimeImpl, parentNode: Long) {
        val id = viewRuntimeImpl.renderingGraph.addNode(this)
        viewRuntimeImpl.renderingGraph.insertNodeChild(id, parentNode)
    }
}
