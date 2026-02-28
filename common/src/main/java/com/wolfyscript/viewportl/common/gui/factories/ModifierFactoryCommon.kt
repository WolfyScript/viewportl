package com.wolfyscript.viewportl.common.gui.factories

import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import com.wolfyscript.viewportl.ui.SimpleModifierStackBuilder
import com.wolfyscript.viewportl.ui.modifier.ClickableModifierDataImpl
import com.wolfyscript.viewportl.ui.modifier.DefaultMinSizeModifier
import com.wolfyscript.viewportl.ui.modifier.FillModifierData
import com.wolfyscript.viewportl.ui.modifier.InventoryDrawModifierDataImpl
import com.wolfyscript.viewportl.ui.modifier.LayoutModifierData
import com.wolfyscript.viewportl.ui.modifier.PaddingModifier
import com.wolfyscript.viewportl.ui.modifier.ScrollSelectableModifierDataImpl
import com.wolfyscript.viewportl.ui.modifier.SizeModifier
import com.wolfyscript.viewportl.ui.modifier.SlotInputModifierImpl
import com.wolfyscript.viewportl.ui.layout.Constraints
import com.wolfyscript.viewportl.ui.layout.Direction
import com.wolfyscript.viewportl.ui.layout.Dp
import com.wolfyscript.viewportl.ui.modifier.InventoryDrawScope
import com.wolfyscript.viewportl.ui.modifier.LayoutModification
import com.wolfyscript.viewportl.ui.modifier.LayoutModifyScope
import com.wolfyscript.viewportl.ui.modifier.ModifierData
import com.wolfyscript.viewportl.ui.modifier.ModifierStackBuilder
import com.wolfyscript.viewportl.gui.factories.ModifierFactory

class ModifierFactoryCommon : ModifierFactory {

    override fun createModifierStackBuilder(): ModifierStackBuilder {
        return SimpleModifierStackBuilder()
    }

    override fun createPaddingModifier(
        start: Dp,
        top: Dp,
        end: Dp,
        bottom: Dp,
    ): ModifierData<*> {
        return PaddingModifier(start, top, end, bottom)
    }

    override fun createSizeModifier(
        minWidth: Dp,
        minHeight: Dp,
        maxWidth: Dp,
        maxHeight: Dp,
        enforceIncoming: Boolean,
    ): ModifierData<*> {
        return SizeModifier(minWidth, minHeight, maxWidth, maxHeight, enforceIncoming)
    }

    override fun createDefaultMinSizeModifier(
        minWidth: Dp,
        minHeight: Dp,
    ): ModifierData<*> {
        return DefaultMinSizeModifier(minWidth, minHeight)
    }

    override fun createFillModifier(direction: Direction, fraction: Float): ModifierData<*> {
        return FillModifierData(direction, fraction)
    }

    override fun createLayoutModifier(layoutFn: LayoutModifyScope.(Constraints) -> LayoutModification): ModifierData<*> {
        return LayoutModifierData(layoutFn)
    }

    override fun createInventoryDrawModifier(draw: InventoryDrawScope.() -> Unit): ModifierData<*> {
        return InventoryDrawModifierDataImpl(draw)
    }

    override fun createClickableModifier(onClick: () -> Unit): ModifierData<*> {
        return ClickableModifierDataImpl(onClick)
    }

    override fun createScrollSelectModifier(onSubmit: (selectedItem: Int) -> Unit): ModifierData<*> {
        return ScrollSelectableModifierDataImpl(onSubmit)
    }

    override fun createSlotInputModifier(
        canPlace: (stack: ItemStackSnapshot) -> Boolean,
        canPickup: (stack: ItemStackSnapshot) -> Boolean,
        onValueChange: (stack: ItemStackSnapshot) -> Unit,
        value: () -> ItemStackSnapshot
    ): ModifierData<*> {
        return SlotInputModifierImpl(canPlace, canPickup, onValueChange, value)
    }
}