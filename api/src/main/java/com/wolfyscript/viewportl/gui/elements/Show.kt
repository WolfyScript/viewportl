package com.wolfyscript.viewportl.gui.elements


data class ShowProperties(
    val scope: ComponentScope,
    val condition: () -> Boolean,
    val fallback: ComponentScope.() -> Unit,
    val content: ComponentScope.() -> Unit
)

/**
 * The actual show element
 */
interface Show : Element