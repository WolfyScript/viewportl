package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable
import com.wolfyscript.viewportl.gui.compose.layout.Position
import com.wolfyscript.viewportl.gui.compose.layout.Size
import com.wolfyscript.viewportl.gui.compose.modifier.modifyLayout

@Composable
fun Box(content: @Composable () -> Unit) {
    Layout({
        modifyLayout { constraints ->

            modifyLayout(constraints, Position(Size(), Size()))
        }
    }, content = content) { measurables, constraints ->

        layout(constraints.maxWidth, constraints.maxHeight) {

        }
    }
}