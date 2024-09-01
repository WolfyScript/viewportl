package com.wolfyscript.viewportl.gui.components

import com.wolfyscript.viewportl.gui.ViewRuntime

/**
 * The show component renders the content depending on the condition.
 *
 * When the [condition] evaluates to true then it renders the [content], otherwise renders the [fallback].
 * Both the [content] and [fallback] components are added as children to the show component.
 */
fun show(
    runtime: ViewRuntime,
    condition: () -> Boolean,
    fallback: ComponentScope.() -> Unit = {},
    content: ComponentScope.() -> Unit
) = component(runtime) {
    runtime.viewportl.guiFactory.componentFactory.show(
        ShowProperties(
            runtime, condition, fallback, content
        )
    )
}

data class ShowProperties(
    val runtime: ViewRuntime,
    val condition: () -> Boolean,
    val fallback: ComponentScope.() -> Unit,
    val content: ComponentScope.() -> Unit
)

/**
 * The actual show component
 */
interface Show : NativeComponent