package com.wolfyscript.viewportl.example.counter

import androidx.compose.runtime.*
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.viewportl.gui.elements.*
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

/**
 * A simple counter example with a count display and increase and decrease button.
 * The Redstone Icon displays the count and is updated whenever the count value changes.
 */
@Composable
fun Counter() {
    var count by remember { mutableStateOf(0) }
    Row { // Horizontally splits the inventory into sections and places children left to right
        Box { // Centers child in first section
            Icon(createCounterStack(count))
        }
        Column { // Splits other section vertically, and places buttons vertically top to bottom
            Button(onClick = {
                count++
            }) {
                Icon(stack = ItemStack(Items.GREEN_CONCRETE).apply {
                    set(DataComponents.CUSTOM_NAME, "<b>Increment".deser().vanilla())
                }.snapshot())
            }
            Button(onClick = {
                count--
            }) {
                Icon(stack = ItemStack(Items.RED_CONCRETE).apply {
                    set(DataComponents.CUSTOM_NAME, "<b>Decrement".deser().vanilla())
                }.snapshot())
            }
        }
    }
}

private fun createCounterStack(count: Int): ItemStackSnapshot {
    return ItemStack(Items.REDSTONE).apply {
        set(DataComponents.CUSTOM_NAME, "<b>Count: $count".deser().vanilla())
    }.snapshot()
}