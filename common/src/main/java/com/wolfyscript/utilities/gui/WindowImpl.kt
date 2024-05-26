package com.wolfyscript.utilities.gui

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.common.base.Preconditions
import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.functions.ReceiverConsumer
import com.wolfyscript.utilities.functions.ReceiverFunction
import com.wolfyscript.utilities.gui.callback.TextInputCallback
import com.wolfyscript.utilities.gui.callback.TextInputTabCompleteCallback
import com.wolfyscript.utilities.gui.reactivity.ReactiveSource
import com.wolfyscript.utilities.gui.router.Router
import com.wolfyscript.utilities.gui.router.RouterImpl
import com.wolfyscript.utilities.platform.scheduler.Task
import com.wolfyscript.utilities.tuple.Pair
import net.kyori.adventure.text.Component

@KeyedStaticId(key = "window")
class WindowImpl internal constructor(
    @JsonProperty("id") override val id: String,
    @JsonProperty("size") override var size: Int?,
    @JsonProperty("type") override val type: WindowType? = null,
    @JacksonInject("wolfyutils") override val wolfyUtils: WolfyUtils,
    @JacksonInject("context") private val context: BuildContext,
) :
    Window,
    ReactiveSource by context.reactiveSource {
    override var title: Component? = null
    override val router: Router = RouterImpl(wolfyUtils, context, this)
    override var resourcePath: String? = null

    var textInputCallback: TextInputCallback? = null
    var textInputTabCompleteCallback: TextInputTabCompleteCallback? = null

    // Intervalls
    val intervalRunnables: List<Pair<Runnable, Long>> = ArrayList()
    val intervalTasks: MutableList<Task> = ArrayList()

    init {
        Preconditions.checkArgument(size != null || type != null, "Either type or size must be specified!")
    }

    override fun title(titleUpdate: ReceiverFunction<Component?, Component?>) {
        context.reactiveSource.createEffect<Unit> {
            title = with(titleUpdate) { title.apply() }

            (context.runtime as ViewRuntimeImpl).renderer.updateTitle(title)
        }
    }

    override fun open() {
        for (intervalTask in intervalTasks) {
            intervalTask.cancel()
        }
        intervalTasks.clear()
        for (intervalRunnable in intervalRunnables) {
            val task = wolfyUtils.core.platform.scheduler.task(wolfyUtils)
                .interval(intervalRunnable.value)
                .delay(1).execute(intervalRunnable.key).build()
            intervalTasks.add(task)
        }
        router.open()
    }

    override fun close() {
        for (intervalTask in intervalTasks) {
            intervalTask.cancel()
        }
        intervalTasks.clear()

//        (context.runtime as ViewRuntimeImpl).renderingGraph.removeNode(0)
    }

    override fun routes(routerConfiguration: ReceiverConsumer<Router>) {
        with(routerConfiguration) { router.consume() }
    }

    override fun width(): Int {
        return size?.div(height()) ?: 9
    }

    override fun height(): Int {
        return size?.div(9) ?: 1
    }

    override fun onTextInput(inputCallback: TextInputCallback?) {
        this.textInputCallback = inputCallback
    }

    override fun onTextInputTabComplete(textInputTabCompleteCallback: TextInputTabCompleteCallback?) {
        this.textInputTabCompleteCallback = textInputTabCompleteCallback
    }

}
