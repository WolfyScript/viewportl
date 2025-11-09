package com.wolfyscript.viewportl.gui.compose.modifier

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.viewportl

fun ModifierStackBuilder.clickable(onClick: () -> Unit): ModifierStackBuilder =
    push(ScafallProvider.get().viewportl.guiFactory.modifierFactory.createClickableModifier(onClick))

interface PointerEventScope

interface ClickableModifier : ModifierData<ClickableModifierNode> {

    val onClick: () -> Unit

}

interface ClickableModifierNode : ModifierNode {

    val onClick: () -> Unit

    fun PointerEventScope.onClickInteraction(x: Int, y: Int)

}