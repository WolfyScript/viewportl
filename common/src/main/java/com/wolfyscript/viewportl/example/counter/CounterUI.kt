package com.wolfyscript.viewportl.example.counter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.viewportl.gui.elements.Box
import com.wolfyscript.viewportl.gui.elements.Button
import com.wolfyscript.viewportl.gui.elements.Column
import com.wolfyscript.viewportl.gui.elements.Icon
import com.wolfyscript.viewportl.gui.elements.Row
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

    LaunchedEffect(Unit) {}

    Row { // Horizontally splits the inventory into sections and places children left to right
        Box { // Centers child in first section
            Icon(ItemStack(Items.REDSTONE).apply {
                set(DataComponents.CUSTOM_NAME, "<b>Count: $count".deser().vanilla())
            }.snapshot())
        }
        Column { // Splits other section vertically, and places buttons vertically top to bottom
            Button(onClick = { count++ }) {
                Icon(stack = ItemStack(Items.GREEN_CONCRETE).snapshot())
            }
            Button(onClick = { count-- }) {
                Icon(stack = ItemStack(Items.RED_CONCRETE).snapshot())
            }
        }
    }

}