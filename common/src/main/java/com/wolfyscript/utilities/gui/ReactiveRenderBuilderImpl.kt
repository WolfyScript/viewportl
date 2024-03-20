package com.wolfyscript.utilities.gui

import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.gui.ReactiveRenderBuilder.ReactiveResult
import com.wolfyscript.utilities.gui.components.ComponentUtil
import com.wolfyscript.utilities.gui.functions.ReceiverConsumer
import com.wolfyscript.utilities.gui.rendering.PropertyPosition
import java.util.*

class ReactiveRenderBuilderImpl(
    val wolfyUtils: WolfyUtils,
    private val context: BuildContext
) : ReactiveRenderBuilder {

    private val componentRenderSet: MutableSet<Long> = HashSet()

    override fun <B : ComponentBuilder<out Component, Component>> component(
        id: String?,
        builderType: Class<B>,
        builderConsumer: ReceiverConsumer<B>
    ): ReactiveResult {
        val numericId = context.getOrCreateNumericId(id)
        val builderTypeInfo = ComponentUtil.getBuilderType(wolfyUtils, id ?: "internal_${id}", builderType)
        val builder: B = context.findExistingComponentBuilder(numericId, builderTypeInfo.value, builderTypeInfo.key).orElseGet {
            val builderId = context.instantiateNewBuilder(numericId, PropertyPosition.def(), builderTypeInfo)
            componentRenderSet.add(builderId)
            context.getBuilder(builderId, builderTypeInfo.value)
        }
        with(builderConsumer) { builder.consume() }
        return ReactiveResultImpl(builder)
    }

    class ReactiveResultImpl internal constructor(private val builder: ComponentBuilder<*, *>) : ReactiveResult {

        override fun construct(): Component? {
            return builder.create(null)
        }

    }
}
