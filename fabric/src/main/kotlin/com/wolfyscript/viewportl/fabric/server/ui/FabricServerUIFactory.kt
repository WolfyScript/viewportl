package com.wolfyscript.viewportl.fabric.server.ui

import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.runtime.UIRuntimeImpl
import com.wolfyscript.viewportl.common.gui.factories.GuiFactoryCommon
import com.wolfyscript.viewportl.fabric.server.ui.inventory.FabricInvUIRenderer
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.gui.rendering.Renderer
import com.wolfyscript.viewportl.runtime.UIRuntime
import java.util.*
import kotlin.coroutines.CoroutineContext

class FabricServerUIFactory : GuiFactoryCommon() {

    override fun createUIRuntime(
        viewportl: Viewportl,
        parentCoroutineContext: CoroutineContext,
        viewer: UUID,
    ): UIRuntime {
        return UIRuntimeImpl(
            viewportl,
            parentCoroutineContext,
            viewer,
            renderer = createInventoryRenderer(),
            interactionHandler = createInventoryInteractionHandler()
        )
    }

    override fun createInventoryRenderer(): Renderer<*> {
        return FabricInvUIRenderer()
    }

    override fun createInventoryInteractionHandler(): InteractionHandler<*> {
        TODO("Not yet implemented")
    }

}