package com.wolfyscript.utilities.gui.components

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import com.google.common.base.Preconditions
import com.google.inject.Binder
import com.google.inject.Guice
import com.google.inject.Module
import com.google.inject.Stage
import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.gui.Component
import com.wolfyscript.utilities.gui.ComponentBuilder
import com.wolfyscript.utilities.gui.ComponentBuilderSettings
import com.wolfyscript.utilities.gui.ComponentUtil
import com.wolfyscript.utilities.gui.Position
import com.wolfyscript.utilities.gui.functions.ReceiverConsumer
import com.wolfyscript.utilities.tuple.Pair
import java.util.*
import java.util.function.Consumer

@KeyedStaticId(key = "cluster")
@ComponentBuilderSettings(base = ComponentClusterBuilder::class, component = ComponentCluster::class)
class ComponentClusterBuilderImpl @JsonCreator constructor(
    @JsonProperty("id") id: String,
    @JacksonInject("wolfyUtils") wolfyUtils: WolfyUtils,
    @JsonProperty("position") position: Position?
) : AbstractComponentBuilderImpl<ComponentCluster, Component>(id, wolfyUtils, position), ComponentClusterBuilder {
    private val componentBuilderPositions: MutableMap<ComponentBuilder<*, *>, Position> = HashMap()
    private val componentRenderSet: MutableSet<ComponentBuilder<*, *>> = HashSet()

    @JsonSetter("placement")
    private fun setPlacement(componentBuilders: List<ComponentBuilder<*, Component>>) {
        for (componentBuilder in componentBuilders) {
            componentBuilderPositions[componentBuilder] = componentBuilder.position()
        }
    }

    override fun <B : ComponentBuilder<out Component?, Component?>> component(
        id: String,
        builderType: Class<B>,
        builderConsumer: ReceiverConsumer<B>
    ): ComponentClusterBuilder {
        val builderTypeInfo = ComponentUtil.getBuilderType(wolfyUtils, id, builderType)
        val builder = findExistingComponentBuilder<B>(id, builderTypeInfo.value, builderTypeInfo.key)
            .orElseThrow {
                IllegalStateException(
                    String.format(
                        "Failed to link to component '%s'! Cannot find existing placement",
                        id
                    )
                )
            }
        with(builderConsumer) { builder.consume() }
        componentRenderSet.add(builder)
        return this
    }

    override fun <B : ComponentBuilder<out Component?, Component?>> component(
        position: Position,
        id: String,
        builderType: Class<B>,
        builderConsumer: ReceiverConsumer<B>
    ): ComponentClusterBuilder {
        val builderTypeInfo = ComponentUtil.getBuilderType(
            wolfyUtils, id, builderType
        )
        findExistingComponentBuilder<B>(id, builderTypeInfo.value, builderTypeInfo.key).ifPresentOrElse(
            { with(builderConsumer) { it.consume() } }, {
            val injector = Guice.createInjector(Stage.PRODUCTION, Module { binder: Binder ->
                binder.bind(WolfyUtils::class.java).toInstance(wolfyUtils)
                binder.bind(String::class.java).toInstance(id)
            })
            val builder = injector.getInstance(builderTypeInfo.value)
            with( builderConsumer) { builder.consume() }
            componentBuilderPositions[builder] = position
            componentRenderSet.add(builder)
        })
        return this
    }

    private fun <B : ComponentBuilder<out Component?, Component?>> findExistingComponentBuilder(
        id: String,
        builderImplType: Class<B>,
        builderKey: NamespacedKey
    ): Optional<B> {
        return componentBuilderPositions.keys.stream()
            .filter { componentBuilder -> componentBuilder.id() == id && componentBuilder.type == builderKey }
            .findFirst()
            .map { obj -> builderImplType.cast(obj) }
    }

    override fun create(parent: Component): ComponentCluster {
        val staticComponents: MutableList<Component> = ArrayList()
        val nonRenderedComponents: MutableMap<ComponentBuilder<*, *>, Position?> = HashMap()

        val build = ComponentClusterImpl(id(), wolfyUtils, parent, position(), staticComponents)
        for (componentBuilder in componentBuilderPositions.keys) {
            val position = componentBuilderPositions[componentBuilder]

            if (componentRenderSet.contains(componentBuilder)) {
                staticComponents.add(componentBuilder.create(build))
                continue
            }
            nonRenderedComponents[componentBuilder] = position
        }
        return build
    }

}
