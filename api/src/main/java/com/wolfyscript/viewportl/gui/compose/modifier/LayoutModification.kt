package com.wolfyscript.viewportl.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.layout.Dp

/**
 * Result of a [LayoutModifierNode] that consists of the modified [constraints]
 * and the function to produce the outgoing [measure] modification.
 */
interface LayoutModification {

    /**
     * The modified [Constraints]
     */
    val constraints: Constraints

    /**
     * The function to produce the [MeasureModification] based on information from the layout modification step (that produced [this][LayoutModification]), plus the
     * child [MeasureModification], which might come from another [LayoutModifierNode] or the [Node][com.wolfyscript.viewportl.gui.compose.Node] itself.
     */
    val measure: MeasureModifyScope.(childMeasurements: MeasureModification) -> MeasureModification

}

/**
 * Result of a [LayoutModifierNode] on its way up modifying the [Measurements][com.wolfyscript.viewportl.gui.compose.layout.Measurements] of a [Node][com.wolfyscript.viewportl.gui.compose.Node]
 * using the [LayoutModification] produced in the pass down.
 */
interface MeasureModification {

    /**
     * The width of this **[LayoutModifierNode]**
     */
    val measuredWidth: Dp

    /**
     * The height of this **[LayoutModifierNode]**
     */
    val measuredHeight: Dp

    /**
     * The additional offset relative to the previous Nodes origin
     */
    val offset: Offset

}