package com.wolfyscript.utilities.gui.components

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.gui.*
import com.wolfyscript.utilities.gui.callback.InteractionCallback
import com.wolfyscript.utilities.gui.rendering.PropertyPosition.Companion.def
import com.wolfyscript.utilities.gui.rendering.RenderPropertiesImpl
import com.wolfyscript.utilities.platform.adapters.ItemStack
import java.util.function.Consumer
import java.util.function.Supplier

@KeyedStaticId(key = "stack_input_slot")
@ComponentBuilderSettings(base = StackInputSlotBuilder::class, component = StackInputSlot::class)
class StackInputSlotBuilderImpl @JsonCreator constructor(
    @JsonProperty("id") id: String,
    @JacksonInject("wolfyUtils") wolfyUtils: WolfyUtils,
    @JacksonInject("context") private val context: BuildContext
) : AbstractComponentBuilderImpl<StackInputSlot?, Component?>(
    id, wolfyUtils
), StackInputSlotBuilder {
    private var interactionCallback = InteractionCallback { _, _ -> InteractionResult.cancel(false) }
    private var onValueChange: Consumer<ItemStack?>? = null
    private var valueSupplier: Supplier<ItemStack?>? = null

    override fun onValueChange(onValueChange: Consumer<ItemStack?>): StackInputSlotBuilder {
        this.onValueChange = onValueChange
        return this
    }

    override fun interact(interactionCallback: InteractionCallback): StackInputSlotBuilder {
        this.interactionCallback = interactionCallback
        return this
    }

    override fun value(stackSupplier: Supplier<ItemStack?>): StackInputSlotBuilder {
        this.valueSupplier = stackSupplier
        return this
    }

    override fun create(parent: Component?): StackInputSlot {
        val slot: StackInputSlot = StackInputSlotImpl(
            id(),
            wolfyUtils,
            parent,
            onValueChange,
            interactionCallback,
            position()?.let { RenderPropertiesImpl(it) } ?: RenderPropertiesImpl(def()),
        )

        if (valueSupplier != null) {
            context.reactiveSource.createEffect<Unit> {
                slot.value = valueSupplier!!.get()
            }
        }

        return slot
    }
}
