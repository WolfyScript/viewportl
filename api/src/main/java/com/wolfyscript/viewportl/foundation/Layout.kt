package com.wolfyscript.viewportl.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReusableComposeNode
import com.wolfyscript.viewportl.ui.MeasurePolicy
import com.wolfyscript.viewportl.ui.ModelNodeApplier
import com.wolfyscript.viewportl.ui.modifier.ModifierStackBuilder
import com.wolfyscript.viewportl.ui.Node
import com.wolfyscript.viewportl.ui.Node.Companion.SetMeasurePolicy
import com.wolfyscript.viewportl.ui.Node.Companion.SetModifier

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