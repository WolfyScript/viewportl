package com.wolfyscript.viewportl.gui.compose

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Arranger
import com.wolfyscript.viewportl.gui.compose.layout.Measurements
import com.wolfyscript.viewportl.gui.elements.Element
import com.wolfyscript.viewportl.viewportl


/**
 * Represents a Node in the Graph. It consists of the associated id and [Element]
 */
interface Node {

    companion object {
        val Constructor: () -> Node = { ScafallProvider.get().viewportl.guiFactory.createLayoutNode() }

        val SetModifier: Node.(ModifierStackBuilder) -> Unit = { this.modifier = it }
        val SetMeasurePolicy: Node.(MeasurePolicy) -> Unit = { this.measurePolicy = it }
    }

    var parent: Node?
    var modifier: ModifierStackBuilder
    var measurePolicy: MeasurePolicy?
    val arranger: Arranger

    fun insertChildAt(index: Int, child: Node)

    fun removeChildAt(index: Int, count: Int)

    fun moveChildren(from: Int, to: Int, count: Int)

    fun clearChildren()

    fun forEachChild(action: (Node) -> Unit)

    fun remeasure()

    fun measureAndLayout(constraints: Constraints): Measurements

}