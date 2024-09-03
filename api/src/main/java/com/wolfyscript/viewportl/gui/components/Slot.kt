package com.wolfyscript.viewportl.gui.components

import com.wolfyscript.scafall.function.ReceiverBiFunction
import com.wolfyscript.scafall.function.ReceiverConsumer
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.viewportl.gui.interaction.ClickTransaction
import com.wolfyscript.viewportl.gui.interaction.ClickType
import com.wolfyscript.viewportl.gui.interaction.DragTransaction
import com.wolfyscript.viewportl.gui.rendering.RenderProperties
import java.util.function.Consumer

data class SlotProperties(
    val scope: ComponentScope,
    val value: () -> ItemStack?,
    val styles: RenderProperties.() -> Unit,
    val onValueChange: Consumer<ItemStack?>? = null,
    val onClick: ReceiverConsumer<ClickTransaction>? = null,
    val onDrag: ReceiverConsumer<DragTransaction>? = null,
    val canPickUpStack: ReceiverBiFunction<ClickType, ItemStack, Boolean>? = null,
)

interface StackInputSlot : NativeComponent {

    var value: ItemStack?

    var onValueChange: Consumer<ItemStack?>?

    var onClick: ReceiverConsumer<ClickTransaction>?

    var onDrag: ReceiverConsumer<DragTransaction>?

    var canPickUpStack : ReceiverBiFunction<ClickType, ItemStack, Boolean>?

}
