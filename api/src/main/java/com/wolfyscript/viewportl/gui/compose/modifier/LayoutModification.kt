package com.wolfyscript.viewportl.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.layout.Size

interface LayoutModification {

    val constraints: Constraints

    val measure: MeasureModifyScope.(MeasureModification) -> MeasureModification

}

interface MeasureModification {

    val measuredWidth: Size

    val measuredHeight: Size

    val offset: Offset

}