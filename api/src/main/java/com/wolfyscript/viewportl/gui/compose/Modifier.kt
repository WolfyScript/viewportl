package com.wolfyscript.viewportl.gui.compose

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Measurements

/**
 * The lightweight Modifier information used to create the underlying [ModifierNode]
 */
interface Modifier {

    fun create(): ModifierNode

}

interface ModifierNode {

    fun onAttach()

    fun onDetach()

}

interface ModifierStack {

    fun measure(constraints: Constraints): Measurements

}

/**
 * Used to build the [ModifierStack] using the provided functions.
 *
 * Custom Modifiers may provide an extension function for this scope to wrap [push].
 */
interface ModifierStackScope {

    fun push(modifier: Modifier)

}

/**
 * Callback that builds the [ModifierStack]
 */
fun interface ModifierStackBuilder {

    fun ModifierStackScope.build()

}