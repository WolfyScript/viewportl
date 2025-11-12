package com.wolfyscript.viewportl.common.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.layout.Size
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModification
import com.wolfyscript.viewportl.gui.compose.modifier.MeasureModification
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModifyScope
import com.wolfyscript.viewportl.gui.compose.modifier.MeasureModifyScope

class SimpleLayoutModifyScope() : LayoutModifyScope {

    override fun modifyLayout(
        modifiedConstraints: Constraints,
        measure: MeasureModifyScope.(MeasureModification) -> MeasureModification,
    ): LayoutModification {
        return SimpleLayoutModification(modifiedConstraints, measure)
    }

}

class SimpleMeasureModifyScope() : MeasureModifyScope {

    override fun modifyMeasure(
        measuredWidth: Size,
        measuredHeight: Size,
        offset: Offset,
    ): MeasureModification {
        return SimpleMeasureModification(measuredWidth, measuredHeight, offset)
    }

}