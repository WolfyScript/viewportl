package com.wolfyscript.utilities.gui.components

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import com.google.inject.Binder
import com.google.inject.Guice
import com.google.inject.Module
import com.google.inject.Stage
import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.gui.*
import com.wolfyscript.utilities.gui.functions.ReceiverConsumer
import com.wolfyscript.utilities.gui.functions.SerializableSupplier
import com.wolfyscript.utilities.tuple.Pair
import java.util.*

@KeyedStaticId(key = "cluster")
@ComponentBuilderSettings(base = ComponentClusterBuilder::class, component = ComponentCluster::class)
class ComponentClusterBuilderImpl @JsonCreator constructor(
    @JsonProperty("id") id: String,
    @JacksonInject("wolfyUtils") wolfyUtils: WolfyUtils,
    @JsonProperty("position") position: Position?
) : AbstractComponentBuilderImpl<ComponentCluster, Component>(id, wolfyUtils, position), ComponentClusterBuilder {
    private val componentIdAliases: MutableMap<String, Long> = HashMap()
    private val componentBuilderMap: MutableMap<Long, ComponentBuilder<*, *>> = HashMap()
    private val componentRenderSet: MutableSet<ComponentBuilder<*, *>> = HashSet()

    @JsonSetter("placement")
    private fun setPlacement(componentBuilders: List<ComponentBuilder<*, Component>>) {
        for (componentBuilder in componentBuilders) {
            val numericId = ComponentUtil.nextId()
            componentIdAliases[componentBuilder.id()] = numericId
            wolfyUtils.logger.info("Load component builder from config: " + componentBuilder.id() + "  (" + numericId + ")")
            componentBuilderMap[numericId] = componentBuilder
        }
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

    override fun create(parent: Component): ComponentCluster {
        val staticComponents: MutableList<Component> = ArrayList()
        val build = ComponentClusterImpl(id(), wolfyUtils, parent, position(), staticComponents)
        componentRenderSet.map { it.create(build) }.forEach { staticComponents.add(it) }
        return build
    }

    override fun <B : ComponentBuilder<out Component, Component>> component(
        id: String?,
        builderType: Class<B>,
        builderConsumer: ReceiverConsumer<B>
    ): ComponentClusterBuilder {
        val numericId = getOrCreateNumericId(id)
        val builderTypeInfo = ComponentUtil.getBuilderType(wolfyUtils, id ?: "internal_${id}", builderType)
        val builder: B = findExistingComponentBuilder(numericId, builderTypeInfo.value, builderTypeInfo.key).orElseGet {
            val builder = instantiateNewBuilder(numericId, Position(Position.Type.RELATIVE, 0) /* TODO */, builderTypeInfo)
            with(builderConsumer) { builder.consume() }
            componentRenderSet.add(builder)
            builder
        }
        with(builderConsumer) { builder.consume() }
        return this
    }

    override fun whenever(condition: SerializableSupplier<Boolean>): ConditionalChildComponentBuilder.When<ComponentClusterBuilder> {
        TODO("Not yet implemented")
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

}
