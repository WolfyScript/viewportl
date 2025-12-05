package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.requireSize
import com.wolfyscript.viewportl.gui.compose.modifier.slotInput

@Composable
fun Slot(
    value: () -> ItemStackSnapshot,
    onValueChange: (ItemStackSnapshot) -> Unit = {},
    canPlace: (ItemStackSnapshot) -> Boolean = { true },
    canTake: (ItemStackSnapshot) -> Boolean = { true },
) {
    Layout(
        Modifier
            .requireSize(1.slots)
            .slotInput(canPlace, canTake, onValueChange, value),
        content = { }
    ) { measurable, constraints ->
        layout(constraints.maxWidth, constraints.maxHeight) {}
    }
}
