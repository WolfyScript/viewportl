package com.wolfyscript.viewportl.gui.rendering

import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.gui.components.ComponentGroup
import com.wolfyscript.viewportl.gui.rendering.ComponentRenderer

class InventoryGroupComponentRenderer : ComponentRenderer<ComponentGroup, InvGUIRenderContext> {

    override fun key(): Key = Key.defaultKey("inventory/group")

    override fun render(context: InvGUIRenderContext, component: ComponentGroup) { }
}