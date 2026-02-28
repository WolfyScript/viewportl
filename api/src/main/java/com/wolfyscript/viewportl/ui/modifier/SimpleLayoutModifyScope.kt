package com.wolfyscript.viewportl.ui.modifier

import com.wolfyscript.viewportl.ui.layout.Constraints
import com.wolfyscript.viewportl.ui.layout.Offset
import com.wolfyscript.viewportl.ui.layout.Dp
import com.wolfyscript.viewportl.ui.layout.coerceIn
import com.wolfyscript.viewportl.ui.modifier.LayoutModification
import com.wolfyscript.viewportl.ui.modifier.MeasureModification
import com.wolfyscript.viewportl.ui.modifier.LayoutModifyScope
import com.wolfyscript.viewportl.ui.modifier.MeasureModifyScope

class SimpleLayoutModifyScope() : LayoutModifyScope {

    override fun modifyLayout(
        modifiedConstraints: Constraints,
        measure: MeasureModifyScope.(MeasureModification) -> MeasureModification,
    ): LayoutModification {
        return SimpleLayoutModification(modifiedConstraints, measure)
    }

}

class SimpleMeasureModifyScope(val incomingConstraints: Constraints) : MeasureModifyScope {

    override fun modifyMeasure(
        measuredWidth: Dp,
        measuredHeight: Dp,
        offset: Offset,
    ): MeasureModification {
        // Make sure the defined width is within the constraints & center it if is smaller
        val width = measuredWidth.coerceIn(incomingConstraints.minWidth, incomingConstraints.maxWidth)
        val height = measuredHeight.coerceIn(incomingConstraints.minHeight, incomingConstraints.maxHeight)

        val additionalOffset = Offset((width - measuredWidth) / 2, (height - measuredHeight) / 2)
        return SimpleMeasureModification(measuredWidth, measuredHeight, offset + additionalOffset)
    }

}