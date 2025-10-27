package com.wolfyscript.viewportl.common.gui.factories

import com.wolfyscript.viewportl.common.gui.compose.modifier.SizeModifier
import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Size
import com.wolfyscript.viewportl.gui.compose.modifier.InventoryDrawScope
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModification
import com.wolfyscript.viewportl.gui.compose.modifier.MeasureModifyScope
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierData
import com.wolfyscript.viewportl.gui.factories.ModifierFactory

class ModifierFactoryCommon : ModifierFactory {

    override fun createPaddingModifier(
        start: Size,
        top: Size,
        end: Size,
        bottom: Size,
    ): ModifierData<*> {
        TODO("Not yet implemented")
    }

    override fun createSizeModifier(
        minWidth: Size,
        minHeight: Size,
        maxWidth: Size,
        maxHeight: Size,
        enforceIncoming: Boolean,
    ): ModifierData<*> {
        return SizeModifier(minWidth, minHeight, maxWidth, maxHeight, enforceIncoming)
    }

    override fun createFillModifier(): ModifierData<*> {
        TODO("Not yet implemented")
    }

    override fun createLayoutModifier(layoutFn: MeasureModifyScope.(Constraints) -> LayoutModification): ModifierData<*> {
        TODO("Not yet implemented")
    }

    override fun createInventoryDrawModifier(draw: InventoryDrawScope.() -> Unit): ModifierData<*> {
        TODO("Not yet implemented")
    }
}