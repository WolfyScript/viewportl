package com.wolfyscript.viewportl.common.gui.factories

import com.wolfyscript.viewportl.gui.factories.DataStoreFactory
import com.wolfyscript.viewportl.gui.factories.GuiFactory
import com.wolfyscript.viewportl.gui.factories.ModifierFactory

abstract class GuiFactoryCommon : GuiFactory {

    override val modifierFactory: ModifierFactory = ModifierFactoryCommon()

    override val dataStoreFactory: DataStoreFactory = StoreFactoryCommon()

}