package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReusableComposeNode
import com.wolfyscript.viewportl.gui.compose.MeasurePolicy
import com.wolfyscript.viewportl.gui.compose.ModelNodeApplier
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierStackBuilder
import com.wolfyscript.viewportl.gui.compose.Node
import com.wolfyscript.viewportl.gui.compose.Node.Companion.SetMeasurePolicy
import com.wolfyscript.viewportl.gui.compose.Node.Companion.SetModifier

@Composable
fun Layout(modifier: ModifierStackBuilder, content: @Composable () -> Unit, measurePolicy: MeasurePolicy) {
    ReusableComposeNode<Node, ModelNodeApplier>(
        factory = Node.Constructor,
        update = {
            set(measurePolicy, SetMeasurePolicy)
            set(modifier, SetModifier)
        },
        content = content
    )
}