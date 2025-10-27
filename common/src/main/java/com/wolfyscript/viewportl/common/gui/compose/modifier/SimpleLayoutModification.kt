package com.wolfyscript.viewportl.common.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Offset
import com.wolfyscript.viewportl.gui.compose.layout.Size
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModification

class SimpleLayoutModification(
    override val constraints: Constraints,
    override val offset: Offset = Offset.Zero
) : LayoutModification