package com.wolfyscript.viewportl.gui.compose.modifier

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.viewportl.viewportl

/**
 *
 * @param canPlace a callback called before the interaction is processed and checks if the viewer is allowed to place the stack into the slot.
 * @param canTake a callback called before the interaction is processed and checks if a viewer is allowed to take the stack out of the slot.
 * @param onValueChange called when the value changed due to an interaction by a viewer.
 * @param value a provider for the value. When a value used within this callback changes it causes it to rerender.
 */
fun ModifierStackBuilder.slotInput(
    canPlace: (stack: ItemStackSnapshot) -> Boolean = { _ -> true },
    canTake: (stack: ItemStackSnapshot) -> Boolean = { _ -> true },
    onValueChange: (stack: ItemStackSnapshot) -> Unit = { _ -> },
    value: () -> ItemStackSnapshot
) = push(
    ScafallProvider.get().viewportl.guiFactory.modifierFactory.createSlotInputModifier(
        canPlace,
        canTake,
        onValueChange,
        value
    )
)

interface SlotInputEventScope {}

interface SlotInputModifier<T: SlotInputModifierNode> : ModifierData<T> {

    val canPlace: (stack: ItemStackSnapshot) -> Boolean
    val canTake: (stack: ItemStackSnapshot) -> Boolean
    val onValueChange: (stack: ItemStackSnapshot) -> Unit
    val value: () -> ItemStackSnapshot

}

interface SlotInputModifierNode : ModifierNode, InventoryDrawModifierNode {

    val canPlace: (stack: ItemStackSnapshot) -> Boolean
    val canTake: (stack: ItemStackSnapshot) -> Boolean
    val onValueChange: (stack: ItemStackSnapshot) -> Unit
    val value: () -> ItemStackSnapshot

    fun SlotInputEventScope.onPlaceStack(x: Int, y: Int, cursor: ItemStackSnapshot, currentStack: ItemStackSnapshot)

    fun SlotInputEventScope.onTakeStack(x: Int, y: Int, cursor: ItemStackSnapshot, currentStack: ItemStackSnapshot)

}