package com.wolfyscript.viewportl.gui.factories

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Direction
import com.wolfyscript.viewportl.gui.compose.layout.Dp
import com.wolfyscript.viewportl.gui.compose.modifier.InventoryDrawScope
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModification
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModifyScope
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierData
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierStackBuilder

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

    fun createSizeModifier(
        minWidth: Dp = Dp.Unspecified,
        minHeight: Dp = Dp.Unspecified,
        maxWidth: Dp = Dp.Unspecified,
        maxHeight: Dp = Dp.Unspecified,
        enforceIncoming: Boolean = true
    ): ModifierData<*>

    fun createDefaultMinSizeModifier(
        minWidth: Dp = Dp.Unspecified,
        minHeight: Dp = Dp.Unspecified,
    ) : ModifierData<*>

    fun createFillModifier(direction: Direction, fraction: Float): ModifierData<*>

    fun createLayoutModifier(
        layoutFn: LayoutModifyScope.(Constraints) -> LayoutModification
    ): ModifierData<*>

    fun createInventoryDrawModifier(
        draw: InventoryDrawScope.() -> Unit
    ): ModifierData<*>

    fun createClickableModifier(
        onClick: () -> Unit
    ): ModifierData<*>

}