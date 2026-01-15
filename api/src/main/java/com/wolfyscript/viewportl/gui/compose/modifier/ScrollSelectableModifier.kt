package com.wolfyscript.viewportl.gui.compose.modifier

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.viewportl

/**
 * Handles scroll selects in the UI. In inventory UIs these are based on bundles.
 * They allow you to select an item by scrolling and submit the selection by right-clicking.
 */
fun ModifierStackBuilder.scrollSelect(onSubmit: (selectedItem: Int) -> Unit): ModifierStackBuilder =
    push(ScafallProvider.get().viewportl.guiFactory.modifierFactory.createScrollSelectModifier(onSubmit))

interface ScrollSelectableModifier : ModifierData<ScrollSelectableModifierNode> {

    val onSubmit: (selectedItem: Int) -> Unit

}

interface ScrollSelectableModifierNode : ModifierNode {

    val onSubmit: (selectedItem: Int) -> Unit

}