package com.wolfyscript.viewportl.example

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.minecraft.snapshot
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.viewportl.ui.layout.Arrangement
import com.wolfyscript.viewportl.ui.layout.slots
import com.wolfyscript.viewportl.ui.modifier.Modifier
import com.wolfyscript.viewportl.ui.modifier.fillMaxHeight
import com.wolfyscript.viewportl.ui.modifier.fillMaxWidth
import com.wolfyscript.viewportl.ui.modifier.height
import com.wolfyscript.viewportl.ui.modifier.width
import com.wolfyscript.viewportl.foundation.Column
import com.wolfyscript.viewportl.foundation.Icon
import com.wolfyscript.viewportl.foundation.Row
import com.wolfyscript.viewportl.foundation.Slot
import com.wolfyscript.viewportl.gui.model.Store
import com.wolfyscript.viewportl.gui.model.store
import com.wolfyscript.viewportl.viewportl
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

class SlotInputTestStore : Store() {

    var stack by mutableStateOf(ItemStack.EMPTY.snapshot())
        private set

    fun updateStack(stack: ItemStackSnapshot) {
        this.stack = stack
    }

}

@Composable
fun SlotInputTest() {
    val store = store(key = Key.viewportl("slot_input_test")) { SlotInputTestStore() }

    Row(Modifier.height(3.slots).fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Column(Modifier.width(3.slots).fillMaxHeight()) {
            Row(Modifier.fillMaxWidth()) {
                Icon(stack = ItemStack(Items.GRAY_STAINED_GLASS_PANE).snapshot())
                Icon(stack = ItemStack(Items.GRAY_STAINED_GLASS_PANE).snapshot())
                Icon(stack = ItemStack(Items.GRAY_STAINED_GLASS_PANE).snapshot())
            }
            Row(Modifier.fillMaxWidth()) {
                Icon(stack = ItemStack(Items.GRAY_STAINED_GLASS_PANE).snapshot())
                Slot(
                    value = { store.stack },
                    onValueChange = { store.updateStack(it) }
                )
                Icon(stack = ItemStack(Items.GRAY_STAINED_GLASS_PANE).snapshot())
            }
            Row(Modifier.fillMaxWidth()) {
                Icon(stack = ItemStack(Items.GRAY_STAINED_GLASS_PANE).snapshot())
                Icon(stack = ItemStack(Items.GRAY_STAINED_GLASS_PANE).snapshot())
                Icon(stack = ItemStack(Items.GRAY_STAINED_GLASS_PANE).snapshot())
            }
        }
    }
}