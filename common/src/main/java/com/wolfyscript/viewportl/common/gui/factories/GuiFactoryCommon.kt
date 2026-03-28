package com.wolfyscript.viewportl.common.gui.factories

import com.wolfyscript.viewportl.gui.factories.DataStoreFactory
import com.wolfyscript.viewportl.gui.factories.GuiFactory

abstract class GuiFactoryCommon : GuiFactory {

    override val dataStoreFactory: DataStoreFactory = StoreFactoryCommon()

}