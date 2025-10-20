package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReusableComposeNode
import com.wolfyscript.viewportl.gui.compose.MeasurePolicy
import com.wolfyscript.viewportl.gui.compose.ModelNodeApplier
import com.wolfyscript.viewportl.gui.compose.Modifier
import com.wolfyscript.viewportl.gui.compose.Node

@Composable
fun Layout(modifier: Modifier? = null, content: @Composable () -> Unit, measure: MeasurePolicy) {

    ReusableComposeNode<Node, ModelNodeApplier>(
        factory = { Node() },
        update = {
            // TODO
        },
        content = content
    )
}