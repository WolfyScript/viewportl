package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.viewportl.gui.compose.modifier.drawToSlots

@Composable
fun Icon(stack: ItemStackSnapshot) {
    Box(modifier = {
        drawToSlots {
            drawStack(stack = stack)
        }
    })
}