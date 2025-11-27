package com.wolfyscript.viewportl.common.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.layout.Dp
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModification
import com.wolfyscript.viewportl.gui.compose.modifier.MeasureModification
import com.wolfyscript.viewportl.gui.compose.modifier.MeasureModifyScope

class SimpleLayoutModification(
    override val constraints: Constraints,
    override val measure: MeasureModifyScope.(MeasureModification) -> MeasureModification,
) : LayoutModification

class SimpleMeasureModification(
    override val measuredWidth: Dp,
    override val measuredHeight: Dp,
    override val offset: Offset
) : MeasureModification