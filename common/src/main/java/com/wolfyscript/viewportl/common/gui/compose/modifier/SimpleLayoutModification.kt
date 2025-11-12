package com.wolfyscript.viewportl.common.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.layout.Size
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModification
import com.wolfyscript.viewportl.gui.compose.modifier.MeasureModification
import com.wolfyscript.viewportl.gui.compose.modifier.MeasureModifyScope

class SimpleLayoutModification(
    override val constraints: Constraints,
    override val measure: MeasureModifyScope.(MeasureModification) -> MeasureModification,
) : LayoutModification

class SimpleMeasureModification(
    override val measuredWidth: Size,
    override val measuredHeight: Size,
    override val offset: Offset
) : MeasureModification