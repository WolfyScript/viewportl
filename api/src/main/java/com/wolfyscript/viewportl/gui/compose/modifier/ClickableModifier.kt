package com.wolfyscript.viewportl.gui.compose.modifier

fun ModifierStackScope.clickable(onClick: () -> Unit) {

}

interface ClickableModifier : ModifierData<ClickableModifierNode> {

    val onClick: () -> Unit

}

interface ClickableModifierNode : ModifierNode {

    val onClick: () -> Unit

}