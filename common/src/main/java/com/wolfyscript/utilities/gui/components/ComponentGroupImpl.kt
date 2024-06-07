package com.wolfyscript.utilities.gui.components

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.inject.Inject
import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.functions.ReceiverConsumer
import com.wolfyscript.utilities.gui.BuildContext
import com.wolfyscript.utilities.gui.ViewRuntime
import com.wolfyscript.utilities.gui.ViewRuntimeImpl
import com.wolfyscript.utilities.platform.scheduler.Task
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
    MatchChildComponentBuilder by MatchChildComponentBuilderImpl(context) {

    private val children: MutableList<Component> = mutableListOf()
    private val width: Int
    private val height: Int

    private val intervalRunnables: MutableList<Pair<Runnable, Long>> = ArrayList()
    private val intervalTasks: MutableList<Task> = ArrayList()

    init {
        val topLeft = 54
        this.width = 1
        this.height = (abs((topLeft / 9).toDouble()) + 1).toInt()
    }

    override fun childComponents(): Set<Component> {
        return HashSet(children)
    }

    override fun interval(intervalInTicks: Long, runnable: Runnable) {
        intervalRunnables.add(Pair(runnable, intervalInTicks))
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
        val component = context.getOrCreateComponent(this, id, type)
        children.add(component)
        with(configurator) { component.consume() }
        component.finalize()
    }

    override fun remove(runtime: ViewRuntime, nodeId: Long, parentNode: Long) {
        for (intervalTask in intervalTasks) {
            intervalTask.cancel()
        }
        intervalTasks.clear()

        (runtime as ViewRuntimeImpl).modelGraph.removeNode(nodeId)
    }

    override fun insert(runtime: ViewRuntime, parentNode: Long) {
        runtime as ViewRuntimeImpl
        val id = runtime.modelGraph.addNode(this)
        runtime.modelGraph.insertNodeAsChildOf(id, parentNode)

        for (child in children) {
            child.insert(runtime, id)
        }

        // start intervals after the component has been constructed
        for (intervalTask in intervalTasks) {
            intervalTask.cancel()
        }
        intervalTasks.clear()
        for (intervalRunnable in intervalRunnables) {
            val task = wolfyUtils.core.platform.scheduler.task(wolfyUtils)
                .interval(intervalRunnable.second)
                .delay(1)
                .execute(Runnable {
                    intervalRunnable.first.run()
                    context.reactiveSource.runEffects()
                })
                .build()
            intervalTasks.add(task)
        }
    }

    override fun finalize() {
        buildConditionals(parent)
        buildMatchers(parent)
    }
}
