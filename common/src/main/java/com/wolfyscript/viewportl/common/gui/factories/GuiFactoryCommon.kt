package com.wolfyscript.viewportl.common.gui.factories

import com.wolfyscript.viewportl.common.gui.compose.LayoutNode
import com.wolfyscript.viewportl.gui.compose.Node
import com.wolfyscript.viewportl.gui.factories.ElementFactory
import com.wolfyscript.viewportl.gui.factories.GuiFactory

abstract class GuiFactoryCommon : GuiFactory {

    override val elementFactory: ElementFactory = ElementFactoryImpl()

    override fun createLayoutNode(): Node {
        return LayoutNode()
    }

}