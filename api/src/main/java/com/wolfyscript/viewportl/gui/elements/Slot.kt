package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable
import com.wolfyscript.scafall.wrappers.world.items.ItemStackLike
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import com.wolfyscript.viewportl.gui.compose.modifier.Modifier
import com.wolfyscript.viewportl.gui.interaction.ClickType

@Composable
fun Slot(
    value: () -> ItemStackLike?,
    onValueChange: ScafallItemStack.() -> Unit = {},
    canPickUpStack: ClickType.(ScafallItemStack) -> Boolean = { true },
) {
    Layout(Modifier, content = { }) { measurable, constraints ->
        // TODO
        layout(constraints.maxWidth, constraints.maxHeight) {

        }
    }
}

data class SlotProperties(
    val value: () -> ItemStackLike?,
    val onValueChange: ScafallItemStack.() -> Unit = { },
    val canPickUpStack: ClickType.(ScafallItemStack) -> Boolean = { true },
)

interface StackInputSlot : Element {

    var value: ScafallItemStack?

    var onValueChange: ScafallItemStack.() -> Unit

    var canPickUpStack: ClickType.(ScafallItemStack) -> Boolean

}
