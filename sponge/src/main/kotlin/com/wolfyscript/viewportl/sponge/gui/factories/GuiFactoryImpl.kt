package com.wolfyscript.viewportl.sponge.gui.factories

import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.UIRuntimeImpl
import com.wolfyscript.viewportl.common.gui.factories.GuiFactoryCommon
import com.wolfyscript.viewportl.gui.UIRuntime
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.gui.rendering.Renderer
import com.wolfyscript.viewportl.sponge.gui.inventoryui.interaction.SpongeUIInteractionHandler
import com.wolfyscript.viewportl.sponge.gui.inventoryui.rendering.SpongeInvUIRenderer
import java.util.*
import kotlin.coroutines.CoroutineContext

class GuiFactoryImpl : GuiFactoryCommon() {

    override fun createUIRuntime(
        viewportl: Viewportl,
        parentCoroutineContext: CoroutineContext,
        viewer: UUID
    ): UIRuntime {
        return UIRuntimeImpl(
            viewportl,
            parentCoroutineContext,
            viewer,
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

}