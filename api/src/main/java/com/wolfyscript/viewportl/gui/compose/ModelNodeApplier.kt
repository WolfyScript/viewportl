package com.wolfyscript.viewportl.gui.compose

import androidx.compose.runtime.AbstractApplier

class ModelNodeApplier(root: Node, private val onChanges: () -> Unit = {}) : AbstractApplier<Node>(root) {

    override fun onBeginChanges() {
        super.onBeginChanges()

        onChanges()
    }

    override fun insertTopDown(index: Int, instance: Node) {
        // We are using the Bottom up method to create the tree
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