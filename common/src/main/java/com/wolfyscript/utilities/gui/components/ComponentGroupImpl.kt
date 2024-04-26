package com.wolfyscript.utilities.gui.components

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.inject.Inject
import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.gui.*
import com.wolfyscript.utilities.functions.ReceiverConsumer
import java.util.*
import kotlin.math.abs

@ComponentImplementation(base = ComponentGroup::class)
@KeyedStaticId(key = "cluster")
class ComponentGroupImpl @JsonCreator @Inject constructor(
    @JsonProperty("id") id: String,
    @JacksonInject("wolfyUtils") wolfyUtils: WolfyUtils,
    @JacksonInject("context") private val context: BuildContext,
    @javax.annotation.Nullable @JacksonInject("parent") parent: Component? = null,
) :
    AbstractComponentImpl<ComponentGroup>(id, wolfyUtils, parent),
    ComponentGroup,
    ConditionalChildComponentBuilder by ConditionalChildComponentBuilderImpl(context),
    MatchChildComponentBuilder by MatchChildComponentBuilderImpl(context)
{

    private val children: MutableList<Component> = mutableListOf()
    private val width: Int
    private val height: Int

    init {
        val topLeft = 54
        this.width = 1
        this.height = (abs((topLeft / 9).toDouble()) + 1).toInt()
    }

    override fun childComponents(): Set<Component> {
        return HashSet(children)
    }

    override fun getChild(id: String?): Optional<out Component> {
        return Optional.empty()
    }

    override fun outlet(outletConfig: ReceiverConsumer<Outlet>) {
        TODO("Not yet implemented")
    }

    override fun configuredBy(filePath: String) {
        TODO("Not yet implemented")
    }

    override fun width(): Int {
        return width
    }

    override fun height(): Int {
        return height
    }

    override fun <B : Component> component(
        id: String?,
        type: Class<B>,
        configurator: ReceiverConsumer<B>
    ) {
        val component = context.getOrCreateComponent(id, type)
        children.add(component)
        with(configurator) { component.consume() }
        component.finalize()
    }

    override fun remove(runtime: ViewRuntime, nodeId: Long, parentNode: Long) {
        (runtime as ViewRuntimeImpl).renderingGraph.removeNode(nodeId)
    }

    override fun insert(runtime: ViewRuntime, parentNode: Long) {
        runtime as ViewRuntimeImpl
        val id = runtime.renderingGraph.addNode(this)
        runtime.renderingGraph.insertNodeChild(id, parentNode)

        for (child in children) {
            child.insert(runtime, id)
        }
    }

    override fun finalize() {
        buildConditionals(parent)
        buildMatchers(parent)
    }
}
