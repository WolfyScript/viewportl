package com.wolfyscript.viewportl.common.gui.compose.modifier

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierStackBuilder
import com.wolfyscript.viewportl.gui.compose.modifier.ScrollSelectableModifier
import com.wolfyscript.viewportl.gui.compose.modifier.ScrollSelectableModifierNode
import com.wolfyscript.viewportl.viewportl


class ScrollSelectableModifierDataImpl(
    override val onSubmit: (selectedItem: Int) -> Unit
) : ScrollSelectableModifier {

    override fun create(): ScrollSelectableModifierNode {
        return ScrollSelectableModifierNodeImpl(onSubmit)
    }

    override fun update(node: ScrollSelectableModifierNode) {
    }

}

class ScrollSelectableModifierNodeImpl(override val onSubmit: (selectedItem: Int) -> Unit) : ScrollSelectableModifierNode {

    override fun onAttach() {

    }

    override fun onDetach() {

    }

}
