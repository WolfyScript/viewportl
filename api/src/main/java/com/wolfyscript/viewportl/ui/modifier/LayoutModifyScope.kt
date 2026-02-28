package com.wolfyscript.viewportl.ui.modifier

import com.wolfyscript.viewportl.ui.IntrinsicSize
import com.wolfyscript.viewportl.ui.layout.Constraints
import com.wolfyscript.viewportl.ui.layout.Dp
import com.wolfyscript.viewportl.ui.layout.Offset

/**
 * Scope in which the incoming [Constraints] are modified.
 *
 * The modifications are ran top-down, so that the [com.wolfyscript.viewportl.ui.Node] receives the
 * [LayoutModification] after the last modifier.
 */
interface LayoutModifyScope {

    fun modifyLayout(
        modifiedConstraints: Constraints,
        measure: MeasureModifyScope.(childMeasure: MeasureModification) -> MeasureModification,
    ): LayoutModification

}

/**
 * Scope in which the outgoing layout is modified using the previously calculated [LayoutModification]s.
 *
 * The [LayoutModification]s are ran in reverse order (bottom up), so that the
 * [com.wolfyscript.viewportl.ui.Node]s' [com.wolfyscript.viewportl.ui.layout.Measurements] are calculated first and then passed up the modifier stack.
 */
interface MeasureModifyScope {

    fun modifyMeasure(measuredWidth: Dp, measuredHeight: Dp, offset: Offset): MeasureModification

}

/**
 * Scope in which the incoming cross axis size (width/height) for child intrinsics is modified.
 *
 * These modifications are ran top down, so that
 *
 */
interface IntrinsicModifyIncomingScope {

    /**
     * Modifies the incoming cross axis size (width/height) and specifies the [child size][usedChildSize] used
     * to calculate the final [outgoing] intrinsic size.
     */
    fun modify(
        modifiedCrossAxisSize: Dp,
        usedChildSize: IntrinsicSize,
        outgoing: IntrinsicModifyOutgoingScope.(childIntrinsic: Dp) -> Dp,
    ): IntrinsicIncomingModification

    /**
     * Modifies the incoming cross axis size (width/height), while skipping
     * the child size calculation.
     * Instead, it returns a custom [outgoing] intrinsic size independent of the child size.
     *
     */
    fun custom(
        modifiedCrossAxisSize: Dp,
        outgoing: IntrinsicModifyOutgoingScope.(_: Dp) -> Dp,
    ): IntrinsicIncomingModification

    /**
     * Modifies the incoming cross axis size (width/height), selects the [child size][usedChildSize]
     * and passes the outgoing child intrinsic through without modifying it.
     */
    fun passthrough(
        modifiedCrossAxisSize: Dp,
        usedChildSize: IntrinsicSize,
    ): IntrinsicIncomingModification

}

interface IntrinsicModifyOutgoingScope

internal object SimpleIntrinsicModifyOutgoingScope : IntrinsicModifyOutgoingScope

internal object SimpleIntrinsicModifyIncomingScope : IntrinsicModifyIncomingScope {

    override fun modify(
        modifiedCrossAxisSize: Dp,
        usedChildSize: IntrinsicSize,
        outgoing: IntrinsicModifyOutgoingScope.(childModification: Dp) -> Dp,
    ): IntrinsicIncomingModification = SimpleIntrinsicIncomingModification(
        modifiedCrossAxisSize,
        usedChildSize,
        outgoing
    )

    override fun custom(
        modifiedCrossAxisSize: Dp,
        outgoing: IntrinsicModifyOutgoingScope.(childIntrinsic: Dp) -> Dp,
    ): IntrinsicIncomingModification = SimpleIntrinsicIncomingModification(
        modifiedCrossAxisSize,
        null,
        outgoing
    )

    override fun passthrough(
        modifiedCrossAxisSize: Dp,
        usedChildSize: IntrinsicSize,
    ): IntrinsicIncomingModification = SimpleIntrinsicIncomingModification(
        modifiedCrossAxisSize,
        usedChildSize,
        null
    )

}
