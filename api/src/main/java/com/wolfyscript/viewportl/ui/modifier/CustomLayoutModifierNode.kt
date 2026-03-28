package com.wolfyscript.viewportl.ui.modifier

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.ui.layout.Constraints
import com.wolfyscript.viewportl.viewportl

fun ModifierStackBuilder.modifyLayout(modifyFn: LayoutModifyScope.(Constraints) -> LayoutModification): ModifierStackBuilder =
    push(LayoutModifierData(modifyFn))
