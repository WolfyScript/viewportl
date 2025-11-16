package com.wolfyscript.viewportl.gui.compose.modifier

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.gui.compose.layout.Direction
import com.wolfyscript.viewportl.gui.compose.layout.Size
import com.wolfyscript.viewportl.viewportl

fun ModifierStackBuilder.width(width: Size) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = width,
        maxWidth = width
    )
)

fun ModifierStackBuilder.height(height: Size) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minHeight = height,
        maxHeight = height
    )
)

fun ModifierStackBuilder.size(size: Size) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = size,
        minHeight = size,
        maxWidth = size,
        maxHeight = size
    )
)

fun ModifierStackBuilder.size(width: Size, height: Size) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = width,
        maxWidth = width,
        minHeight = height,
        maxHeight = height
    )
)

fun ModifierStackBuilder.widthIn(min: Size, max: Size) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = min,
        maxWidth = max,
    )
)

fun ModifierStackBuilder.heightIn(min: Size, max: Size) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minHeight = min,
        maxHeight = max,
    )
)

fun ModifierStackBuilder.sizeIn(
    minWidth: Size,
    minHeight: Size,
    maxWidth: Size,
    maxHeight: Size,
) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = minWidth,
        minHeight = minHeight,
        maxWidth = maxWidth,
        maxHeight = maxHeight,
    )
)

fun ModifierStackBuilder.requireWidth(width: Size) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = width,
        maxWidth = width,
        enforceIncoming = false
    )
)

fun ModifierStackBuilder.requireHeight(height: Size) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minHeight = height,
        maxHeight = height,
        enforceIncoming = false
    )
)

fun ModifierStackBuilder.requireSize(size: Size) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = size,
        minHeight = size,
        maxWidth = size,
        maxHeight = size,
        enforceIncoming = false
    )
)

fun ModifierStackBuilder.requireSize(width: Size, height: Size) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = width,
        maxWidth = width,
        minHeight = height,
        maxHeight = height,
        enforceIncoming = false
    )
)

fun ModifierStackBuilder.requireWidthIn(min: Size, max: Size) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = min,
        maxWidth = max,
        enforceIncoming = false
    )
)

fun ModifierStackBuilder.requireHeightIn(min: Size, max: Size) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minHeight = min,
        maxHeight = max,
        enforceIncoming = false
    )
)

fun ModifierStackBuilder.requireSizeIn(
    minWidth: Size,
    minHeight: Size,
    maxWidth: Size,
    maxHeight: Size,
) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = minWidth,
        minHeight = minHeight,
        maxWidth = maxWidth,
        maxHeight = maxHeight,
        enforceIncoming = false
    )
)

/**
 * Specifies a minimum [width][minWidth] or [height][minHeight],
 * that applies when the incoming [minWidth][com.wolfyscript.viewportl.gui.compose.layout.Constraints.minWidth]/[minHeight][com.wolfyscript.viewportl.gui.compose.layout.Constraints.minHeight] is `0`.
 */
fun ModifierStackBuilder.defaultMinSize(minWidth: Size = Size.Unspecified, minHeight: Size = Size.Unspecified) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createDefaultMinSizeModifier(minWidth, minHeight)
)

fun ModifierStackBuilder.fillMaxWidth(fraction: Float = 1f) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createFillModifier(Direction.Horizontal, fraction)
)

fun ModifierStackBuilder.fillMaxHeight(fraction: Float = 1f) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createFillModifier(Direction.Vertical, fraction)
)

fun ModifierStackBuilder.fillMaxSize(fraction: Float = 1f) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createFillModifier(Direction.Both, fraction)
)
