package com.wolfyscript.viewportl.common.gui.factories

import com.wolfyscript.viewportl.common.gui.compose.SimpleModifierStackBuilder
import com.wolfyscript.viewportl.common.gui.compose.modifier.ClickableModifierDataImpl
import com.wolfyscript.viewportl.common.gui.compose.modifier.DefaultMinSizeModifier
import com.wolfyscript.viewportl.common.gui.compose.modifier.InventoryDrawModifierDataImpl
import com.wolfyscript.viewportl.common.gui.compose.modifier.SizeModifier
import com.wolfyscript.viewportl.gui.compose.layout.Constraints
import com.wolfyscript.viewportl.gui.compose.layout.Size
import com.wolfyscript.viewportl.gui.compose.modifier.InventoryDrawScope
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModification
import com.wolfyscript.viewportl.gui.compose.modifier.LayoutModifyScope
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierData
import com.wolfyscript.viewportl.gui.compose.modifier.ModifierStackBuilder
import com.wolfyscript.viewportl.gui.factories.ModifierFactory

class ModifierFactoryCommon : ModifierFactory {

    override fun createModifierStackBuilder(): ModifierStackBuilder {
        return SimpleModifierStackBuilder()
    }

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

    override fun createDefaultMinSizeModifier(
        minWidth: Size,
        minHeight: Size,
    ): ModifierData<*> {
        return DefaultMinSizeModifier(minWidth, minHeight)
    }

    override fun createFillModifier(): ModifierData<*> {
        TODO("Not yet implemented")
    }

    override fun createLayoutModifier(layoutFn: LayoutModifyScope.(Constraints) -> LayoutModification): ModifierData<*> {
        TODO("Not yet implemented")
    }

    override fun createInventoryDrawModifier(draw: InventoryDrawScope.() -> Unit): ModifierData<*> {
        return InventoryDrawModifierDataImpl(draw)
    }

    override fun createClickableModifier(onClick: () -> Unit): ModifierData<*> {
        return ClickableModifierDataImpl(onClick)
    }
}