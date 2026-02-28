package com.wolfyscript.viewportl.ui.modifier

import com.wolfyscript.viewportl.ui.layout.Constraints
import com.wolfyscript.viewportl.ui.layout.Offset
import com.wolfyscript.viewportl.ui.layout.Dp
import com.wolfyscript.viewportl.ui.modifier.LayoutModification
import com.wolfyscript.viewportl.ui.modifier.MeasureModification
import com.wolfyscript.viewportl.ui.modifier.MeasureModifyScope

class SimpleLayoutModification(
    override val constraints: Constraints,
    override val measure: MeasureModifyScope.(MeasureModification) -> MeasureModification,
) : LayoutModification

class SimpleMeasureModification(
    override val measuredWidth: Dp,
    override val measuredHeight: Dp,
    override val offset: Offset
) : MeasureModification