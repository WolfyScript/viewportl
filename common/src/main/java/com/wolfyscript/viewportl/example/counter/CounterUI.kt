package com.wolfyscript.viewportl.example.counter

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.viewportl.gui.elements.*
import com.wolfyscript.viewportl.gui.model.Store
import com.wolfyscript.viewportl.gui.model.store
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

internal class CounterStore : Store {

    var count by mutableStateOf(0)
        private set

    fun increment() {
        count++
    }

    fun decrement() {
        count--
    }

    fun reset() {
        count = 0
    }

}

/**
 * A simple counter example with a count display and increase and decrease button.
 * The Redstone Icon displays the count and is updated whenever the count value changes.
 */
@Composable
fun Counter() {
    val countStore = store(key = Key.key("viewportl", "counter")) { CounterStore() }

    Row { // Horizontally splits the inventory into sections and places children left to right
        Box { // Centers child in first section
            Icon(createCounterStack(countStore.count))
        }
        Column { // Splits other section vertically, and places buttons vertically top to bottom
            Button(onClick = { countStore.increment() }) {
                Icon(stack = ItemStack(Items.GREEN_CONCRETE).apply {
                    set(DataComponents.CUSTOM_NAME, "<b>Increment".deser().vanilla())
                }.snapshot())
            }
            Button(onClick = { countStore.decrement() }) {
                Icon(stack = ItemStack(Items.RED_CONCRETE).apply {
                    set(DataComponents.CUSTOM_NAME, "<b>Decrement".deser().vanilla())
                }.snapshot())
            }
        }
        if (countStore.count != 0) {
            Box {
                Button(onClick = { countStore.reset() }) {
                    Icon(stack = ItemStack(Items.CYAN_CONCRETE).apply {
                        set(DataComponents.CUSTOM_NAME, "<b>Reset".deser().vanilla())
                    }.snapshot())
                }
            }
        }
    }
}

private fun createCounterStack(count: Int): ItemStackSnapshot {
    return ItemStack(Items.REDSTONE).apply {
        set(DataComponents.CUSTOM_NAME, "<b>Count: $count".deser().vanilla())
    }.snapshot()
}