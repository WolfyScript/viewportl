/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.wolfyscript.viewportl.common.gui.components

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.inject.Inject
import com.wolfyscript.scafall.scheduling.Task
import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.scafall.function.ReceiverConsumer
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.BuildContext
import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.components.*
import java.util.*
import kotlin.math.abs

@ComponentImplementation(base = ComponentGroup::class)
@StaticNamespacedKey(key = "cluster")
class ComponentGroupImpl @JsonCreator @Inject constructor(
    @JsonProperty("id") id: String,
    @JacksonInject("viewportl") viewportl: Viewportl,
    @JacksonInject("context") private val context: BuildContext,
    @javax.annotation.Nullable @JacksonInject("parent") parent: Component? = null,
) :
    AbstractComponentImpl<ComponentGroup>(id, viewportl, parent),
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
        component.completeBuild()
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
            val task = runtime.viewportl.scafall.scheduler.task(runtime.viewportl.scafall.corePlugin)
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

    override fun completeBuild() {
        buildConditionals(parent)
        buildMatchers(parent)
    }
}
