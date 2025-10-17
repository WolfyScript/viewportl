package com.wolfyscript.viewportl.gui.compose

import androidx.compose.runtime.AbstractApplier
import com.wolfyscript.viewportl.gui.model.Node

class ModelNodeApplier(root: Node) : AbstractApplier<Node>(root) {

    override fun insertTopDown(index: Int, instance: Node) {
        TODO("Not yet implemented")
    }

    override fun insertBottomUp(index: Int, instance: Node) {
        TODO("Not yet implemented")
    }

    override fun remove(index: Int, count: Int) {
        TODO("Not yet implemented")
    }

    override fun move(from: Int, to: Int, count: Int) {
        TODO("Not yet implemented")
    }

    override fun onClear() {
        TODO("Not yet implemented")
    }
}