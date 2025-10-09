package com.wolfyscript.viewportl.gui.elements

import com.wolfyscript.scafall.wrappers.world.items.ItemStackLike
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import com.wolfyscript.viewportl.gui.interaction.ClickType
import com.wolfyscript.viewportl.gui.rendering.RenderProperties

data class SlotProperties(
    val scope: ComponentScope,
    val value: () -> ItemStackLike?,
    val styles: RenderProperties.() -> Unit,
    val onValueChange: (ScafallItemStack.() -> Unit)? = null,
    val canPickUpStack: (ClickType.(ScafallItemStack) -> Boolean)? = null,
)

interface StackInputSlot : Element {

    var value: ScafallItemStack?

    var onValueChange: (ScafallItemStack.() -> Unit)?

    var canPickUpStack : (ClickType.(ScafallItemStack) -> Boolean)?

}
