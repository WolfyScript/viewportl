package com.wolfyscript.viewportl.common.gui.compose

import com.wolfyscript.viewportl.gui.compose.Modifier
import com.wolfyscript.viewportl.gui.compose.ModifierNode
import com.wolfyscript.viewportl.gui.compose.ModifierStack
import com.wolfyscript.viewportl.gui.compose.ModifierStackScope
import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Measurements

class ModifierStackImpl(private val modifiers: ArrayDeque<ModifierNode>) : ModifierStack {

    override fun measure(constraints: Constraints): Measurements {
        TODO("Not yet implemented")
    }

}

class ModifierStackScopeImpl : ModifierStackScope {

    val stack = ArrayDeque<ModifierNode>()

    override fun push(modifier: Modifier) {
        stack.addFirst(modifier.create())
    }

    fun create(): ModifierStack {
        return ModifierStackImpl(stack)
    }

}