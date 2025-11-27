package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.viewportl.gui.compose.layout.dp
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierStackBuilder
import com.wolfyscript.viewportl.gui.compose.modifier.defaultMinSize
import com.wolfyscript.viewportl.gui.compose.modifier.drawToSlots

@Composable
fun Icon(modifier: ModifierStackBuilder = Modifier, stack: ItemStackSnapshot) {
    Box(
        modifier = modifier
            .defaultMinSize(18.dp, 18.dp)
            .drawToSlots { drawStack(stack = stack) }
    )
}