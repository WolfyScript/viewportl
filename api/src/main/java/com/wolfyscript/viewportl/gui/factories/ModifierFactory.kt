package com.wolfyscript.viewportl.gui.factories

import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Size
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
        start: Size = Size.Zero,
        top: Size = Size.Zero,
        end: Size = Size.Zero,
        bottom: Size = Size.Zero,
    ): ModifierData<*>

    fun createSizeModifier(
        minWidth: Size = Size.Unspecified,
        minHeight: Size = Size.Unspecified,
        maxWidth: Size = Size.Unspecified,
        maxHeight: Size = Size.Unspecified,
        enforceIncoming: Boolean = true
    ): ModifierData<*>

    fun createFillModifier(): ModifierData<*>

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