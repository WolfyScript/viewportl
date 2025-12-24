package com.wolfyscript.viewportl.example

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.snapshot
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.viewportl.gui.compose.layout.Arrangement
import com.wolfyscript.viewportl.gui.compose.layout.slots
import com.wolfyscript.viewportl.gui.compose.modifier.*
import com.wolfyscript.viewportl.gui.elements.Column
import com.wolfyscript.viewportl.gui.elements.Icon
import com.wolfyscript.viewportl.gui.elements.Row
import com.wolfyscript.viewportl.gui.elements.Slot
import com.wolfyscript.viewportl.gui.model.Store
import com.wolfyscript.viewportl.gui.model.store
import com.wolfyscript.viewportl.viewportl
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class SlotGridTestStore : Store() {

    val grid = mutableStateListOf<ItemStackSnapshot>().apply {
        for (i in 0 until 9) {
            add(ItemStack.EMPTY.snapshot())
        }
    }

    fun updateStack(index: Int, stack: ItemStackSnapshot) {
        grid[index] = stack
    }

    fun getStack(index: Int): ItemStackSnapshot {
        if (index < grid.size) {
            return grid[index]
        }
        return ItemStack.EMPTY.snapshot()
    }

}

@Composable
fun SlotGridTest() {
    val store = store(key = Key.viewportl("slot_grid_test")) { SlotGridTestStore() }

    Row(Modifier.height(3.slots).fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Column(Modifier.width(1.slots).fillMaxHeight()) {
            Icon(stack = ItemStack(Items.GRAY_STAINED_GLASS_PANE).snapshot())
            Icon(stack = ItemStack(Items.GRAY_STAINED_GLASS_PANE).snapshot())
            Icon(stack = ItemStack(Items.GRAY_STAINED_GLASS_PANE).snapshot())
        }
        Column(Modifier.width(3.slots).fillMaxHeight()) {
            for (i in 0 until 3) {
                Row(Modifier.fillMaxWidth()) {
                    for (j in 0 until 3) {
                        Slot(
                            value = { store.getStack(i * 3 + j) },
                            onValueChange = { store.updateStack(i * 3 + j, it) }
                        )
                    }
                }
            }
        }
        Column(Modifier.width(1.slots).fillMaxHeight()) {
            Icon(stack = ItemStack(Items.GRAY_STAINED_GLASS_PANE).snapshot())
            Icon(stack = ItemStack(Items.GRAY_STAINED_GLASS_PANE).snapshot())
            Icon(stack = ItemStack(Items.GRAY_STAINED_GLASS_PANE).snapshot())
        }
    }
}