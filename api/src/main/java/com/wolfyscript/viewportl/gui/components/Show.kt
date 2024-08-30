package com.wolfyscript.viewportl.gui.components

import com.wolfyscript.viewportl.gui.ViewRuntime

fun show(
    runtime: ViewRuntime,
    condition: Boolean,
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
    val condition: Boolean,
    val fallback: ComponentScope.() -> Unit,
    val content: ComponentScope.() -> Unit
)

interface Show {



}