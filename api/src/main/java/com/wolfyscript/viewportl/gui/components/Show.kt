package com.wolfyscript.viewportl.gui.components


data class ShowProperties(
    val scope: ComponentScope,
    val condition: () -> Boolean,
    val fallback: ComponentScope.() -> Unit,
    val content: ComponentScope.() -> Unit
)

/**
 * The actual show component
 */
interface Show : NativeComponent