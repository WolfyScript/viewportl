package com.wolfyscript.viewportl.gui.components

import com.wolfyscript.scafall.function.ReceiverBiFunction
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.viewportl.gui.interaction.ClickType
import com.wolfyscript.viewportl.gui.rendering.RenderProperties
import java.util.function.Consumer

data class SlotProperties(
    val scope: ComponentScope,
    val value: () -> ItemStack?,
    val styles: RenderProperties.() -> Unit,
    val onValueChange: Consumer<ItemStack?>? = null,
    val canPickUpStack: ReceiverBiFunction<ClickType, ItemStack, Boolean>? = null,
)

interface StackInputSlot : Element {

    var value: ItemStack?

    var onValueChange: Consumer<ItemStack?>?

    var canPickUpStack : ReceiverBiFunction<ClickType, ItemStack, Boolean>?

}
