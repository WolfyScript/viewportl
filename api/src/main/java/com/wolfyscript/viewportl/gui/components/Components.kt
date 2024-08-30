package com.wolfyscript.viewportl.gui.components

import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.reactivity.ReactiveSource

/**
 * A setup function used to create a component.
 *
 * These components are not actually present in the graph, but instead aggregate [NativeComponents][NativeComponent] and
 * allow for better structuring of ui components and encapsulate signals, memos, effects, etc.
 *
 * In the end only the [NativeComponents][NativeComponent] are present in the component graph,
 * while the component functions setup all reactive nodes for e.g. updating native component properties.
 *
 */
fun component(runtime: ViewRuntime, fn: ComponentScope.() -> Unit) {
    runtime.viewportl.guiFactory.runComponentFunction(runtime, fn)
}

/**
 * The ComponentScope is the scope of a component function in which signals, memos, effects, intervals and more can be created.
 */
interface ComponentScope : ReactiveSource {

    /**
     * Adds a task that is run periodically while the Window is open.
     *
     * @param runnable The task to run, may update signals
     * @param intervalInTicks The interval for the task in ticks
     * @return This builder for chaining
     */
    fun interval(intervalInTicks: Long, runnable: Runnable)

}
