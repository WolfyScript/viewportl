package com.wolfyscript.viewportl.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.layout.Dp

interface LayoutModifyScope {

    fun modifyLayout(
        modifiedConstraints: Constraints,
        measure: MeasureModifyScope.(childMeasure: MeasureModification) -> MeasureModification,
    ): LayoutModification

}

interface MeasureModifyScope {

    fun modifyMeasure(measuredWidth: Dp, measuredHeight: Dp, offset: Offset): MeasureModification

}