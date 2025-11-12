package com.wolfyscript.viewportl.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.layout.Size

interface LayoutModifyScope {

    fun modifyLayout(
        modifiedConstraints: Constraints,
        measure: MeasureModifyScope.(childMeasure: MeasureModification) -> MeasureModification,
    ): LayoutModification

}

interface MeasureModifyScope {

    fun modifyMeasure(measuredWidth: Size, measuredHeight: Size, offset: Offset): MeasureModification

}