package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReusableComposeNode
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.wrappers.world.items.ItemStackLike
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import com.wolfyscript.viewportl.gui.compose.ModelNodeApplier
import com.wolfyscript.viewportl.gui.interaction.ClickType
import com.wolfyscript.viewportl.gui.model.Node
import com.wolfyscript.viewportl.viewportl

@Composable
fun Slot(
    value: () -> ItemStackLike?,
    onValueChange: ScafallItemStack.() -> Unit = {},
    canPickUpStack: ClickType.(ScafallItemStack) -> Boolean = { true },
) {
    ReusableComposeNode<Node, ModelNodeApplier>({
        Node(
            element = ScafallProvider.get().viewportl.guiFactory.elementFactory.slot(
                SlotProperties(value, onValueChange, canPickUpStack)
            )
        )
    }) {

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
