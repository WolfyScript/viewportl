package com.wolfyscript.viewportl.gui.compose.modifier

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.gui.compose.layout.Size
import com.wolfyscript.viewportl.viewportl

fun ModifierStackScope.width(width: Size) {
    push(
        ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
            minWidth = width,
            maxWidth = width
        )
    )
}

fun ModifierStackScope.height(height: Size) {
    push(
        ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
            minHeight = height,
            maxHeight = height
        )
    )
}

fun ModifierStackScope.size(size: Size) {
    push(
        ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
            minWidth = size,
            minHeight = size,
            maxWidth = size,
            maxHeight = size
        )
    )
}

fun ModifierStackScope.size(width: Size, height: Size) {
    push(ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = width,
        maxWidth = width,
        minHeight = height,
        maxHeight = height
    ))
}

fun ModifierStackScope.widthIn(min: Size, max: Size) {
    push(ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = min,
        maxWidth = max,
    ))
}

fun ModifierStackScope.heightIn(min: Size, max: Size) {
    push(ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minHeight = min,
        maxHeight = max,
    ))
}

fun ModifierStackScope.sizeIn(
    minWidth: Size,
    minHeight: Size,
    maxWidth: Size,
    maxHeight: Size,
) {
    push(ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = minWidth,
        minHeight = minHeight,
        maxWidth = maxWidth,
        maxHeight = maxHeight,
    ))
}

fun ModifierStackScope.requireWidth(width: Size) {
    push(ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = width,
        maxWidth = width,
        enforceIncoming = false
    ))
}

fun ModifierStackScope.requireHeight(height: Size) {
    push(ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minHeight = height,
        maxHeight = height,
        enforceIncoming = false
    ))
}

fun ModifierStackScope.requireSize(size: Size) {
    push(ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = size,
        minHeight = size,
        maxWidth = size,
        maxHeight = size,
        enforceIncoming = false
    ))
}

fun ModifierStackScope.requireSize(width: Size, height: Size) {
    push(ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = width,
        maxWidth = width,
        minHeight = height,
        maxHeight = height,
        enforceIncoming = false
    ))
}

fun ModifierStackScope.requireWidthIn(min: Size, max: Size) {
    push(ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = min,
        maxWidth = max,
        enforceIncoming = false
    ))
}

fun ModifierStackScope.requireHeightIn(min: Size, max: Size) {
    push(ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minHeight = min,
        maxHeight = max,
        enforceIncoming = false
    ))
}

fun ModifierStackScope.requireSizeIn(
    minWidth: Size,
    minHeight: Size,
    maxWidth: Size,
    maxHeight: Size,
) {
    push(ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSizeModifier(
        minWidth = minWidth,
        minHeight = minHeight,
        maxWidth = maxWidth,
        maxHeight = maxHeight,
        enforceIncoming = false
    ))
}
