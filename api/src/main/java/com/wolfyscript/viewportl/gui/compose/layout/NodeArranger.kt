package com.wolfyscript.viewportl.gui.compose.layout

import com.wolfyscript.viewportl.gui.compose.Node

/**
 * Handles the arrangement (size and position) of the wrapped [Node].
 */
interface NodeArranger : Measurable, Placeable {

    /**
     * The [Node] that is handled by this arranger
     */
    val node: Node

    val position: Position

    /**
     * Remeasures the [Node] based on the given [constraints] (if they are different from the previous constraints).
     *
     * @return whether the size of the node has changed or not. true if it has; false otherwise
     */
    fun remeasure(constraints: Constraints): Boolean

    fun layout()

}