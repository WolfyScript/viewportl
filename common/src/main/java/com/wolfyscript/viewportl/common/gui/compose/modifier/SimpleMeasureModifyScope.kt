package com.wolfyscript.viewportl.common.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModification
import com.wolfyscript.viewportl.gui.compose.modifier.MeasureModifyScope

class SimpleMeasureModifyScope() : MeasureModifyScope {

    override fun modifyLayout(
        modifiedConstraints: Constraints,
        offset: Offset,
    ): LayoutModification {
        return SimpleLayoutModification(modifiedConstraints, offset)
    }


}