package com.wolfyscript.viewportl.ui.modifier

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.viewportl

fun ModifierStackBuilder.clickable(onClick: () -> Unit): ModifierStackBuilder =
    push(ClickableModifierDataImpl(onClick))

interface PointerEventScope

interface ClickableModifier : ModifierData<ClickableModifierNode> {

    val onClick: () -> Unit

}

interface ClickableModifierNode : ModifierNode {

    val onClick: () -> Unit

    fun PointerEventScope.onClickInteraction(x: Int, y: Int)

}

class ClickableModifierDataImpl(
    override val onClick: () -> Unit
) : ClickableModifier {

    override fun create(): ClickableModifierNode {
        return ClickableModifierNodeImpl(onClick)
    }

    override fun update(node: ClickableModifierNode) {

    }

}

class ClickableModifierNodeImpl(override val onClick: () -> Unit) : ClickableModifierNode {

    override fun onAttach() {

    }

    override fun onDetach() {

    }

    override fun PointerEventScope.onClickInteraction(x: Int, y: Int) {
        onClick()
    }

}

class PointerEventScopeImpl() : PointerEventScope {


}