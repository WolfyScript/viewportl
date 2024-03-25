package com.wolfyscript.utilities.gui

import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.gui.ReactiveRenderBuilder.ReactiveResult
import com.wolfyscript.utilities.gui.functions.ReceiverConsumer
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
        val builder: B = context.getOrCreateComponentBuilder(id, builderType) {
            if (!componentRenderSet.contains(it)) {
                componentRenderSet.add(it)
            }
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
