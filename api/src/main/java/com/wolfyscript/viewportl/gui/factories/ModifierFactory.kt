package com.wolfyscript.viewportl.gui.factories

import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.viewportl.ui.layout.Constraints
import com.wolfyscript.viewportl.ui.layout.Direction
import com.wolfyscript.viewportl.ui.layout.Dp
import com.wolfyscript.viewportl.ui.modifier.InventoryDrawScope
import com.wolfyscript.viewportl.ui.modifier.LayoutModification
import com.wolfyscript.viewportl.ui.modifier.LayoutModifyScope
import com.wolfyscript.viewportl.ui.modifier.ModifierData
import com.wolfyscript.viewportl.ui.modifier.ModifierStackBuilder

/**
 * Used to create Modifiers.
 *
 * The returned type is not available as it may be different on different platforms.
 */
interface ModifierFactory {

    fun createModifierStackBuilder() : ModifierStackBuilder

    fun createPaddingModifier(
        start: Dp = Dp.Zero,
        top: Dp = Dp.Zero,
        end: Dp = Dp.Zero,
        bottom: Dp = Dp.Zero,
    ): ModifierData<*>

    fun createLayoutModifier(
        layoutFn: LayoutModifyScope.(Constraints) -> LayoutModification
    ): ModifierData<*>

    fun createInventoryDrawModifier(
        draw: InventoryDrawScope.() -> Unit
    ): ModifierData<*>

    fun createClickableModifier(
        onClick: () -> Unit
    ): ModifierData<*>

    fun createScrollSelectModifier(
        onSubmit: (selectedItem: Int) -> Unit
    ): ModifierData<*>

    fun createSlotInputModifier(
        canPlace: (stack: ItemStackSnapshot) -> Boolean,
        canPickup: (stack: ItemStackSnapshot) -> Boolean,
        onValueChange: (stack: ItemStackSnapshot) -> Unit,
        value: () -> ItemStackSnapshot
    ): ModifierData<*>

}