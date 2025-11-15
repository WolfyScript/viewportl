package com.wolfyscript.viewportl.gui.compose.modifier

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.viewportl

fun ModifierStackBuilder.modifyLayout(modifyFn: LayoutModifyScope.(Constraints) -> LayoutModification): ModifierStackBuilder =
    push(ScafallProvider.get().viewportl.guiFactory.modifierFactory.createLayoutModifier(modifyFn))
