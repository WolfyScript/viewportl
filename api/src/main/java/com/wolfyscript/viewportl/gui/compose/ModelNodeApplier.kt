package com.wolfyscript.viewportl.gui.compose

import androidx.compose.runtime.AbstractApplier

class ModelNodeApplier(root: Node) : AbstractApplier<Node>(root) {

    override fun insertTopDown(index: Int, instance: Node) {
        //
    }

    override fun insertBottomUp(index: Int, instance: Node) {
        current.insertChildAt(index, instance)
    }

    override fun remove(index: Int, count: Int) {
        current.removeChildrenAt(index, count)
    }

    override fun move(from: Int, to: Int, count: Int) {
        current.moveChildren(from, to, count)
    }

    override fun onClear() {
        current.clearChildren()
    }
}