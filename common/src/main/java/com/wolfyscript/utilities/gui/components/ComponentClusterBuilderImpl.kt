package com.wolfyscript.utilities.gui.components

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import com.google.inject.Binder
import com.google.inject.Guice
import com.google.inject.Inject
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
class ComponentClusterBuilderImpl @Inject @JsonCreator constructor(
    @JsonProperty("id") id: String,
    @JacksonInject("wolfyUtils") wolfyUtils: WolfyUtils,
    @JsonProperty("position") position: Position?,
    @JacksonInject("context") private val context: BuildContext
) : AbstractComponentBuilderImpl<ComponentCluster, Component>(id, wolfyUtils, position), ComponentClusterBuilder {
    private val componentRenderSet: MutableSet<Long> = HashSet()

    @JsonSetter("placement")
    private fun setPlacement(componentBuilders: List<ComponentBuilder<*, Component>>) {
        for (componentBuilder in componentBuilders) {
            context.registerBuilder(componentBuilder)
        }
    }

    override fun create(parent: Component): ComponentCluster {
        val staticComponents: MutableList<Component> = ArrayList()
        val build = ComponentClusterImpl(id(), wolfyUtils, parent, position(), staticComponents)

        componentRenderSet.map { context.getBuilder(it)?.create(build) }.forEach {
            if (it != null) {
                staticComponents.add(it)
            }
        }
        return build
    }

    override fun <B : ComponentBuilder<out Component, Component>> component(
        id: String?,
        builderType: Class<B>,
        builderConsumer: ReceiverConsumer<B>
    ): ComponentClusterBuilder {
        val numericId = context.getOrCreateNumericId(id)
        val builderTypeInfo = ComponentUtil.getBuilderType(wolfyUtils, id ?: "internal_${id}", builderType)
        val builder = context.findExistingComponentBuilder(numericId, builderTypeInfo.value, builderTypeInfo.key).orElseGet {
            val builderId = context.instantiateNewBuilder(numericId, Position(Position.Type.RELATIVE, 0) /* TODO */, builderTypeInfo)
            componentRenderSet.add(builderId)
            context.getBuilder(builderId, builderTypeInfo.value)
        }
        with(builderConsumer) { builder?.consume() }
        return this
    }

    override fun whenever(condition: SerializableSupplier<Boolean>): ConditionalChildComponentBuilder.When<ComponentClusterBuilder> {
        TODO("Not yet implemented")
    }

}
