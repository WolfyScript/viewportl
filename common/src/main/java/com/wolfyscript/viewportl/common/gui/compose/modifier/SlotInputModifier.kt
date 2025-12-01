package com.wolfyscript.viewportl.common.gui.compose.modifier

import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.viewportl.gui.compose.modifier.InventoryDrawScope
import com.wolfyscript.viewportl.gui.compose.modifier.SlotInputEventScope
import com.wolfyscript.viewportl.gui.compose.modifier.SlotInputModifier
import com.wolfyscript.viewportl.gui.compose.modifier.SlotInputModifierNode

class SlotInputModifierImpl(
    override val canPlace: (stack: ItemStackSnapshot) -> Boolean,
    override val canTake: (stack: ItemStackSnapshot) -> Boolean,
    override val onValueChange: (stack: ItemStackSnapshot) -> Unit,
    override val value: () -> ItemStackSnapshot
) : SlotInputModifier<SlotInputModifierNodeImpl> {

    override fun create(): SlotInputModifierNodeImpl {
        return SlotInputModifierNodeImpl(canPlace, canTake, onValueChange, value)
    }

    override fun update(node: SlotInputModifierNodeImpl) {
        node.canPlace = canPlace
        node.canTake = canTake
        node.onValueChange = onValueChange
    }

}

class SlotInputModifierNodeImpl(
    override var canPlace: (stack: ItemStackSnapshot) -> Boolean,
    override var canTake: (stack: ItemStackSnapshot) -> Boolean,
    override var onValueChange: (stack: ItemStackSnapshot) -> Unit,
    override val value: () -> ItemStackSnapshot
) : SlotInputModifierNode {

    override fun SlotInputEventScope.onPlaceStack(
        x: Int,
        y: Int,
        cursor: ItemStackSnapshot,
        currentStack: ItemStackSnapshot,
    ) {
        TODO("Not yet implemented")
    }

    override fun SlotInputEventScope.onTakeStack(
        x: Int,
        y: Int,
        cursor: ItemStackSnapshot,
        currentStack: ItemStackSnapshot,
    ) {
        TODO("Not yet implemented")
    }

    override fun onAttach() {

    }

    override fun onDetach() {

    }

    override fun InventoryDrawScope.draw() {
        // TODO: invalidate rendering
        this.drawStack(stack = value())
    }
}