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
@ComponentBuilderSettings(base = ComponentGroupBuilder::class, component = ComponentGroup::class)
class ComponentGroupBuilderImpl @Inject @JsonCreator constructor(
    @JsonProperty("id") id: String,
    @JacksonInject("wolfyUtils") wolfyUtils: WolfyUtils,
    @JsonProperty("position") position: PropertyPosition,
    @JacksonInject("context") private val context: BuildContext
) : AbstractComponentBuilderImpl<ComponentGroup, Component>(id, wolfyUtils, position), ComponentGroupBuilder {
    private val componentRenderSet: MutableSet<Long> = HashSet()
    private val conditionals: MutableList<ConditionalChildComponentBuilderImpl<ComponentGroupBuilder>> =
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
    ): ComponentGroupBuilder {
        val builder = context.getOrCreateComponentBuilder(id, builderType) {
            if (!componentRenderSet.contains(it)) {
                componentRenderSet.add(it)
            }
        }
        with(builderConsumer) { builder.consume() }
        return this
    }

    override fun whenever(condition: SerializableSupplier<Boolean>): ConditionalChildComponentBuilder.When<ComponentGroupBuilder> {
        val builder: ConditionalChildComponentBuilderImpl<ComponentGroupBuilder> =
            ConditionalChildComponentBuilderImpl(this, context)
        conditionals.add(builder)
        return builder.whenever(condition)
    }

    override fun create(parent: Component?): ComponentGroup {
        val staticComponents: MutableList<Component> = ArrayList()
        val build = ComponentGroupImpl(
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
