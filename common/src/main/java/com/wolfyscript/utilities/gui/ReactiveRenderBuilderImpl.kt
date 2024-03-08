package com.wolfyscript.utilities.gui

import com.google.inject.Binder
import com.google.inject.Guice
import com.google.inject.Module
import com.google.inject.Stage
import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.gui.ReactiveRenderBuilder.ReactiveResult
import com.wolfyscript.utilities.gui.functions.ReceiverConsumer
import com.wolfyscript.utilities.tuple.Pair
import java.util.*
import kotlin.collections.HashMap

class ReactiveRenderBuilderImpl(
    val wolfyUtils: WolfyUtils,
    nonRenderedComponents: Map<ComponentBuilder<*, *>, Position>
) : ReactiveRenderBuilder {
    private val componentBuilderPositions: MutableMap<ComponentBuilder<*, *>, Position> = HashMap()

    private val componentIdAliases: MutableMap<String, Long> = java.util.HashMap()
    private val componentBuilderMap: MutableMap<Long, ComponentBuilder<*, *>> = java.util.HashMap()

    init {
        componentBuilderPositions.putAll(nonRenderedComponents)
    }

    override fun <B : ComponentBuilder<out Component, Component>> component(
        id: String?,
        builderType: Class<B>,
        builderConsumer: ReceiverConsumer<B>
    ): ReactiveResult {
        val numericId = getOrCreateNumericId(id)
        val builderTypeInfo = ComponentUtil.getBuilderType(wolfyUtils, id ?: "internal_${id}", builderType)
        val builder: B = findExistingComponentBuilder(numericId, builderTypeInfo.value, builderTypeInfo.key).orElseGet {
            val builder = instantiateNewBuilder(numericId, Position(Position.Type.RELATIVE, 0) /* TODO */, builderTypeInfo)
            with(builderConsumer) { builder.consume() }
            componentBuilderMap[numericId] = builder
            builder
        }
        with(builderConsumer) { builder.consume() }
        return ReactiveResultImpl(builder)
    }

    private fun <B : ComponentBuilder<out Component, Component>> instantiateNewBuilder(
        numericId: Long,
        position: Position,
        builderTypeInfo: Pair<NamespacedKey, Class<B>>
    ): B {
        val injector = Guice.createInjector(Stage.PRODUCTION, Module { binder: Binder ->
            binder.bind(WolfyUtils::class.java).toInstance(wolfyUtils)
            binder.bind(Long::class.java).toInstance(numericId)
            binder.bind(Position::class.java).toInstance(position)
            // TODO: binder.bind(ReactiveSource::class.java).toInstance(reactiveSource)
        })

        val builder = injector.getInstance(builderTypeInfo.value)
        componentBuilderMap[numericId] = builder!!
        return builder
    }

    private fun getOrCreateNumericId(namedId: String? = null): Long {
        if (namedId != null) {
            if (componentIdAliases.containsKey(namedId)) return componentIdAliases[namedId]!!
            val numericId = ComponentUtil.nextId()
            componentIdAliases[namedId] = numericId
            return numericId
        }
        return ComponentUtil.nextId()
    }

    private fun <B : ComponentBuilder<out Component, Component>> findExistingComponentBuilder(
        id: Long,
        builderImplType: Class<B>,
        builderKey: NamespacedKey
    ): Optional<B> {
        val componentBuilder = componentBuilderMap[id] ?: return Optional.empty()
        if (componentBuilder.type != builderKey) {
            throw IllegalArgumentException("Incompatible Component Builder Type! Expected type '$builderKey' but existing builder is of type '${componentBuilder.type}'!")
        }
        return Optional.of(builderImplType.cast(componentBuilder))
    }

    class ReactiveResultImpl internal constructor(private val builder: ComponentBuilder<*, *>) : ReactiveResult {

        override fun construct(): Component? {
            return builder.create(null)
        }

    }
}
