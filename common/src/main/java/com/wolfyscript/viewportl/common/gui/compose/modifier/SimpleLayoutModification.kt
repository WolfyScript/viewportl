package com.wolfyscript.viewportl.common.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.layout.Size
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModification

class SimpleLayoutModification(
    override val constraints: Constraints,
    override val offset: Offset = Offset.Zero,
    override val measuredWidth: Size = constraints.minWidth,
    override val measuredHeight: Size = constraints.minHeight,
) : LayoutModification