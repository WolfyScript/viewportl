package com.wolfyscript.viewportl.common.gui.compose.modifier

import com.wolfyscript.viewportl.gui.compose.modifier.ClickableModifier
import com.wolfyscript.viewportl.gui.compose.modifier.ClickableModifierNode
import com.wolfyscript.viewportl.gui.compose.modifier.PointerEventScope

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