package com.wolfyscript.viewportl.gui.compose.modifier

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.gui.compose.layout.Size
import com.wolfyscript.viewportl.viewportl

fun ModifierStackBuilder.padding(
    start: Size = Size.Zero,
    top: Size = Size.Zero,
    end: Size = Size.Zero,
    bottom: Size = Size.Zero,
) = push(ScafallProvider.get().viewportl.guiFactory.modifierFactory.createPaddingModifier(start, top, end, bottom))

fun ModifierStackBuilder.padding(
    horizontal: Size = Size.Zero,
    vertical: Size = Size.Zero,
) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createPaddingModifier(
        horizontal,
        vertical,
        horizontal,
        vertical
    )
)


fun ModifierStackBuilder.padding(
    all: Size = Size.Zero,
) = push(ScafallProvider.get().viewportl.guiFactory.modifierFactory.createPaddingModifier(all, all, all, all))
