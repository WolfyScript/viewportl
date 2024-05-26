package com.wolfyscript.utilities.gui.components

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.inject.Inject
import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.functions.ReceiverBiFunction
import com.wolfyscript.utilities.functions.ReceiverConsumer
import com.wolfyscript.utilities.gui.BuildContext
import com.wolfyscript.utilities.gui.ViewRuntime
import com.wolfyscript.utilities.gui.ViewRuntimeImpl
import com.wolfyscript.utilities.gui.interaction.ClickInteractionDetails
import com.wolfyscript.utilities.gui.interaction.ClickType
import com.wolfyscript.utilities.platform.adapters.ItemStack
import java.util.function.Consumer
import javax.annotation.Nullable

@ComponentImplementation(base = StackInputSlot::class)
@KeyedStaticId(key = "stack_input_slot")
class StackInputSlotImpl @JsonCreator @Inject constructor(
    @JsonProperty("id") id: String,
    @JacksonInject("wolfyUtils") wolfyUtils: WolfyUtils,
    @JacksonInject("context") private val context: BuildContext,
    @Nullable @JacksonInject("parent") parent: Component? = null,
) : AbstractComponentImpl<StackInputSlot>(
    id, wolfyUtils, parent
), StackInputSlot {

    override var onValueChange: Consumer<ItemStack?>? = null
    override var onClick: ReceiverConsumer<ClickInteractionDetails>? = null
    override var acceptStack: ReceiverBiFunction<ClickType, ItemStack, Boolean>? = null
    override var canPickUpStack: ReceiverBiFunction<ClickType, ItemStack, Boolean>? = null
    override var value: ItemStack? = null

    override fun width(): Int {
        return 1
    }

    override fun height(): Int {
        return 1
    }

    override fun remove(runtime: ViewRuntime, nodeId: Long, parentNode: Long) {
        (runtime as ViewRuntimeImpl).renderingGraph.removeNode(nodeId)
    }

    override fun insert(runtime: ViewRuntime, parentNode: Long) {
        runtime as ViewRuntimeImpl
        val id = runtime.renderingGraph.addNode(this)
        runtime.renderingGraph.insertNodeChild(id, parentNode)
    }
}
