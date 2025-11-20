package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.viewportl.gui.compose.layout.dp
import com.wolfyscript.viewportl.gui.compose.layout.or
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.defaultMinSize
import com.wolfyscript.viewportl.gui.compose.modifier.drawToSlots

@Composable
fun Icon(stack: ItemStackSnapshot) {
    Box(
        modifier = Modifier
            .defaultMinSize(1.slots or 1.dp, 1.slots or 1.dp)
            .drawToSlots { drawStack(stack = stack) }
    )
}