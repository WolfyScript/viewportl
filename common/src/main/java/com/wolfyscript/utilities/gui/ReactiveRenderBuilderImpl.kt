package com.wolfyscript.utilities.gui

import com.google.inject.Binder
import com.google.inject.Guice
import com.google.inject.Module
import com.google.inject.Stage
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.gui.ReactiveRenderBuilder.ReactiveResult
import com.wolfyscript.utilities.gui.functions.ReceiverConsumer

class ReactiveRenderBuilderImpl(
    val wolfyUtils: WolfyUtils,
    nonRenderedComponents: Map<ComponentBuilder<*, *>, Position>?
) : ReactiveRenderBuilder {
    private val componentBuilderPositions: MutableMap<ComponentBuilder<*, *>, Position> = HashMap()

    init {
        componentBuilderPositions.putAll(nonRenderedComponents!!)
    }

    override fun <B : ComponentBuilder<out Component, Component>> component(
        position: Position,
        id: String,
        builderType: Class<B>,
        builderConsumer: ReceiverConsumer<B>
    ): ReactiveResult {
        val builderTypeInfo = ComponentUtil.getBuilderType(
            wolfyUtils, id, builderType
        )
        val builder = componentBuilderPositions.keys.stream()
            .filter { it.id() == id && it.type == builderTypeInfo.key }
            .findFirst()
            .map { builderTypeInfo.value.cast(it) }
            .orElseGet {
                val injector = Guice.createInjector(Stage.PRODUCTION, Module { binder: Binder ->
                    binder.bind(WolfyUtils::class.java).toInstance(wolfyUtils)
                    binder.bind(String::class.java).toInstance(id)
                })
                injector.getInstance(builderTypeInfo.value)
            }
        componentBuilderPositions[builder] = position

        with(builderConsumer) { builder.consume() }
        return ReactiveResultImpl(builder)
    }

    override fun <B : ComponentBuilder<out Component, Component>> component(
        id: String,
        builderType: Class<B>,
        builderConsumer: ReceiverConsumer<B>
    ): ReactiveResult {
        val builderTypeInfo = ComponentUtil.getBuilderType(
            wolfyUtils, id, builderType
        )
        val builder = builderTypeInfo.value.cast(componentBuilderPositions.keys.stream()
            .filter { it.id() == id && it.type == builderTypeInfo.key }
            .findFirst()
            .orElseThrow {
                IllegalStateException(
                    String.format(
                        "Failed to link to component '%s'! Cannot find existing placement",
                        id
                    )
                )
            })

        with(builderConsumer) { builder.consume() }
        return ReactiveResultImpl(builder)
    }

    class ReactiveResultImpl internal constructor(private val builder: ComponentBuilder<*, *>) : ReactiveResult {
        override fun construct(): Component? {
            return builder.create(null)
        }
    }
}
