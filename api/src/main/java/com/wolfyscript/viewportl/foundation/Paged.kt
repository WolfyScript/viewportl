package com.wolfyscript.viewportl.foundation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.adventure.vanilla
import com.wolfyscript.scafall.wrappers.minecraft.snapshot
import com.wolfyscript.viewportl.ui.layout.Arrangement
import com.wolfyscript.viewportl.ui.modifier.Modifier
import com.wolfyscript.viewportl.ui.modifier.ModifierStackBuilder
import com.wolfyscript.viewportl.ui.modifier.fillMaxWidth
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items

@Composable
fun Paged(
    modifier: ModifierStackBuilder,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    controlContent: (@Composable () -> Unit)? = null,
    pageContent: @Composable () -> Unit,
) {
    var pageIndex: Int by remember { mutableStateOf(0) }

    Column(modifier) {
        Box(Modifier.weight(1f).fillMaxWidth()) {
            pageContent()
        }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                if (pageIndex > 0) {
                    pageIndex--
                    onPageChange(pageIndex)
                }
            }) {
                Icon(stack = PagedDefaults.IconPreviousPage)
            }
            if (controlContent != null) {
                Row(Modifier.weight(1f, false)) {
                    controlContent()
                }
            }
            Button(onClick = {
                if (pageIndex < totalPages - 1) {
                    pageIndex++
                    onPageChange(pageIndex)
                }
            }) {
                Icon(stack = PagedDefaults.IconNextPage)
            }
        }
    }
}

object PagedDefaults {

    val IconNextPage = ItemStack(Items.GREEN_CONCRETE).apply {
        set(DataComponents.ITEM_NAME, "Next Page".deser().vanilla())
    }.snapshot()

    val IconPreviousPage = ItemStack(Items.RED_CONCRETE).apply {
        set(DataComponents.ITEM_NAME, "Previous Page".deser().vanilla())
    }.snapshot()

}