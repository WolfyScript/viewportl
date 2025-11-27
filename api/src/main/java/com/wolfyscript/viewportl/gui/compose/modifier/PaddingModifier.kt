package com.wolfyscript.viewportl.gui.compose.modifier

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.gui.compose.layout.Dp
import com.wolfyscript.viewportl.viewportl

fun ModifierStackBuilder.padding(
    start: Dp = Dp.Zero,
    top: Dp = Dp.Zero,
    end: Dp = Dp.Zero,
    bottom: Dp = Dp.Zero,
) = push(ScafallProvider.get().viewportl.guiFactory.modifierFactory.createPaddingModifier(start, top, end, bottom))

fun ModifierStackBuilder.padding(
    horizontal: Dp = Dp.Zero,
    vertical: Dp = Dp.Zero,
) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createPaddingModifier(
        horizontal,
        vertical,
        horizontal,
        vertical
    )
)


fun ModifierStackBuilder.padding(
    all: Dp = Dp.Zero,
) = push(ScafallProvider.get().viewportl.guiFactory.modifierFactory.createPaddingModifier(all, all, all, all))
