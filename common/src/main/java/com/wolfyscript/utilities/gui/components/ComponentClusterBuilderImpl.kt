package com.wolfyscript.utilities.gui.components

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import com.google.inject.Inject
import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.gui.BuildContext
import com.wolfyscript.utilities.gui.Component
import com.wolfyscript.utilities.gui.ComponentBuilder
import com.wolfyscript.utilities.gui.ComponentBuilderSettings
import com.wolfyscript.utilities.gui.functions.ReceiverConsumer
import com.wolfyscript.utilities.gui.functions.SerializableSupplier
import com.wolfyscript.utilities.gui.rendering.PropertyPosition
import com.wolfyscript.utilities.gui.rendering.PropertyPosition.Companion.def
import com.wolfyscript.utilities.gui.rendering.RenderPropertiesImpl

@KeyedStaticId(key = "cluster")
@ComponentBuilderSettings(base = ComponentClusterBuilder::class, component = ComponentCluster::class)
class ComponentClusterBuilderImpl @Inject @JsonCreator constructor(
    @JsonProperty("id") id: String,
    @JacksonInject("wolfyUtils") wolfyUtils: WolfyUtils,
    @JsonProperty("position") position: PropertyPosition,
    @JacksonInject("context") private val context: BuildContext
) : AbstractComponentBuilderImpl<ComponentCluster, Component>(id, wolfyUtils, position), ComponentClusterBuilder {
    private val componentRenderSet: MutableSet<Long> = HashSet()
    private val conditionals: MutableList<ConditionalChildComponentBuilderImpl<ComponentClusterBuilder>> =
        mutableListOf()

    @JsonSetter("placement")
    private fun setPlacement(componentBuilders: List<ComponentBuilder<*, Component>>) {
        for (componentBuilder in componentBuilders) {
            context.registerBuilder(componentBuilder)
        }
    }

    override fun <B : ComponentBuilder<out Component, Component>> component(
        id: String?,
        builderType: Class<B>,
        builderConsumer: ReceiverConsumer<B>
    ): ComponentClusterBuilder {
        val numericId = context.getOrCreateNumericId(id)
        val builderTypeInfo = ComponentUtil.getBuilderType(wolfyUtils, id ?: "internal_${id}", builderType)
        val builder =
            context.findExistingComponentBuilder(numericId, builderTypeInfo.value, builderTypeInfo.key).orElseGet {
                val builderId = context.instantiateNewBuilder(numericId, def(), builderTypeInfo)
                return@orElseGet context.getBuilder(builderId, builderTypeInfo.value)
            }
        if (!componentRenderSet.contains(numericId)) {
            componentRenderSet.add(numericId)
        }
        with(builderConsumer) { builder?.consume() }
        return this
    }

    override fun whenever(condition: SerializableSupplier<Boolean>): ConditionalChildComponentBuilder.When<ComponentClusterBuilder> {
        val builder: ConditionalChildComponentBuilderImpl<ComponentClusterBuilder> =
            ConditionalChildComponentBuilderImpl(this, context)
        conditionals.add(builder)
        return builder.whenever(condition)
    }

    override fun create(parent: Component?): ComponentCluster {
        val staticComponents: MutableList<Component> = ArrayList()
        val build = ComponentClusterImpl(
            id(),
            wolfyUtils,
            parent,
            position()?.let { RenderPropertiesImpl(it) } ?: RenderPropertiesImpl(def()),
            staticComponents)

        componentRenderSet.map { context.getBuilder(it)?.create(build) }.forEach {
            if (it != null) {
                staticComponents.add(it)
            }
        }
        return build
    }

}
