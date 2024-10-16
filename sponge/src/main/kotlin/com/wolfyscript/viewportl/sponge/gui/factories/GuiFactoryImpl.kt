package com.wolfyscript.viewportl.sponge.gui.factories

import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.common.gui.components.ComponentScopeImpl
import com.wolfyscript.viewportl.common.gui.factories.ComponentFactoryImpl
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.components.ComponentScope
import com.wolfyscript.viewportl.gui.factories.ComponentFactory
import com.wolfyscript.viewportl.gui.factories.GuiFactory
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.gui.rendering.Renderer
import com.wolfyscript.viewportl.sponge.gui.inventoryui.interaction.SpongeUIInteractionHandler
import com.wolfyscript.viewportl.sponge.gui.inventoryui.rendering.SpongeInvUIRenderer
import java.util.*
import java.util.function.Function

class GuiFactoryImpl : GuiFactory {

    override fun createInventoryUIRuntime(
        viewportl: Viewportl,
        callback: Function<ViewRuntime<*, *>, Window>,
        viewers: Set<UUID>
    ): ViewRuntime<*, *> {
        return ViewRuntimeImpl(
            viewportl,
            callback,
            viewers,
            renderer = SpongeInvUIRenderer(),
            interactionHandler = SpongeUIInteractionHandler(),
        )
    }

    override fun createInventoryRenderer(): Renderer<*, *> {
        return SpongeInvUIRenderer()
    }

    override fun createInventoryInteractionHandler(): InteractionHandler<*> {
        return SpongeUIInteractionHandler()
    }

    override fun runComponentFunction(runtime: ViewRuntime<*,*>, scope: ComponentScope?, fn: ComponentScope.() -> Unit) {
        fn(ComponentScopeImpl(runtime as ViewRuntimeImpl, scope))
    }

    override val componentFactory: ComponentFactory = ComponentFactoryImpl()

}