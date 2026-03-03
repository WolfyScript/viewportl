package com.wolfyscript.viewportl.ui.modifier

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

    fun minIntrinsicWidth(height: Dp): Dp

    fun minIntrinsicHeight(width: Dp): Dp

    fun maxIntrinsicWidth(height: Dp): Dp

    fun maxIntrinsicHeight(width: Dp): Dp

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

    fun childIntrinsicMinWidth(height: Dp): Dp

    fun childIntrinsicMinHeight(width: Dp): Dp

    fun childIntrinsicMaxWidth(height: Dp): Dp

    fun childIntrinsicMaxHeight(width: Dp): Dp

}
