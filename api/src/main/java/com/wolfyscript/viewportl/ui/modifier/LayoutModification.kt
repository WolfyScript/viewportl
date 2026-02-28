package com.wolfyscript.viewportl.ui.modifier

import com.wolfyscript.viewportl.ui.IntrinsicSize
import com.wolfyscript.viewportl.ui.layout.Constraints
import com.wolfyscript.viewportl.ui.layout.Dp
import com.wolfyscript.viewportl.ui.layout.IntrinsicMeasurable
import com.wolfyscript.viewportl.ui.layout.Offset

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
     * child [MeasureModification], which might come from another [LayoutModifierNode] or the [Node][com.wolfyscript.viewportl.ui.Node] itself.
     */
    val measure: MeasureModifyScope.(childMeasurements: MeasureModification) -> MeasureModification

}

/**
 * Result of a [LayoutModifierNode] on its way up modifying the [Measurements][com.wolfyscript.viewportl.ui.layout.Measurements] of a [Node][com.wolfyscript.viewportl.ui.Node]
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

interface IntrinsicIncomingModification {

    val crossAxisSize: Dp

    val usedChildSize: IntrinsicSize?

    val intrinsicMeasure: (IntrinsicModifyOutgoingScope.(childIntrinsics: Dp) -> Dp)?

}

interface IntrinsicOutgoingModification {

    val intrinsicMainAxisSize: Dp

    val intrinsicCrossAxisSize: Dp

}

internal class SimpleIntrinsicIncomingModification(
    override val crossAxisSize: Dp,
    override val usedChildSize: IntrinsicSize?,
    override val intrinsicMeasure: (IntrinsicModifyOutgoingScope.(Dp) -> Dp)?,
) : IntrinsicIncomingModification

internal class IdentityIntrinsicIncomingModification(
    override val crossAxisSize: Dp,
) : IntrinsicIncomingModification {
    override val usedChildSize: IntrinsicSize? = null
    override val intrinsicMeasure: (IntrinsicModifyOutgoingScope.(childIntrinsics: Dp) -> Dp)? = null
}

internal class MeasurableIntrinsicOutgoingModification(
    val measurable: IntrinsicMeasurable,
    override val intrinsicMainAxisSize: Dp,
    override val intrinsicCrossAxisSize: Dp
) : IntrinsicOutgoingModification {

}

internal class SimpleIntrinsicOutgoingModification(
    override val intrinsicMainAxisSize: Dp,
    override val intrinsicCrossAxisSize: Dp
) : IntrinsicOutgoingModification {

}