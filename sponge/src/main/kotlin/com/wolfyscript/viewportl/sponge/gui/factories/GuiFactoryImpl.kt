package com.wolfyscript.viewportl.sponge.gui.factories

import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.common.gui.factories.ElementFactoryImpl
import com.wolfyscript.viewportl.common.gui.factories.GuiFactoryCommon
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.factories.ElementFactory
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.gui.rendering.Renderer
import com.wolfyscript.viewportl.sponge.gui.inventoryui.interaction.SpongeUIInteractionHandler
import com.wolfyscript.viewportl.sponge.gui.inventoryui.rendering.SpongeInvUIRenderer
import java.util.*
import kotlin.coroutines.CoroutineContext

class GuiFactoryImpl : GuiFactoryCommon() {

    override fun createInventoryUIRuntime(viewportl: Viewportl, parentCoroutineContext: CoroutineContext, window: Window, viewers: Set<UUID>): ViewRuntime {
        return ViewRuntimeImpl(
            viewportl,
            parentCoroutineContext,
            window,
            viewers,
            renderer = SpongeInvUIRenderer(),
            interactionHandler = SpongeUIInteractionHandler(),
        )
    }

    override fun createInventoryRenderer(): Renderer<*> {
        return SpongeInvUIRenderer()
    }

    override fun createInventoryInteractionHandler(): InteractionHandler<*> {
        return SpongeUIInteractionHandler()
    }

    override val elementFactory: ElementFactory = ElementFactoryImpl()

}