package com.wolfyscript.viewportl.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Offset

interface MeasureModifyScope {

    fun modifyLayout(modifiedConstraints: Constraints, offset: Offset) : LayoutModification

}