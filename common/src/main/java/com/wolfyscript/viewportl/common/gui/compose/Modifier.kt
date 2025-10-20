package com.wolfyscript.viewportl.common.gui.compose

import com.wolfyscript.viewportl.gui.compose.Modifier

abstract class AbstractModifier : Modifier {

    override fun then(other: Modifier): Modifier {
        return CombinedModifier(this, other)
    }

}

class CombinedModifier(val wrapper: Modifier, val wrapee: Modifier) : AbstractModifier() {

}