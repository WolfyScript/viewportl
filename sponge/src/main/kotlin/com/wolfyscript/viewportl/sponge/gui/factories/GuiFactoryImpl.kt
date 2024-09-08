package com.wolfyscript.viewportl.sponge.gui.factories

import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.common.gui.components.ComponentScopeImpl
import com.wolfyscript.viewportl.common.gui.factories.ComponentFactoryImpl
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.components.ComponentScope
import com.wolfyscript.viewportl.gui.factories.ComponentFactory
import com.wolfyscript.viewportl.gui.factories.GuiFactory
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.gui.rendering.Renderer
import com.wolfyscript.viewportl.sponge.gui.inventoryui.interaction.SpongeUIInteractionHandler
import com.wolfyscript.viewportl.sponge.gui.inventoryui.rendering.SpongeUIRenderer

class GuiFactoryImpl() : GuiFactory {

    override fun createRenderer(runtime: ViewRuntime): Renderer<*> {
        return SpongeUIRenderer((runtime as ViewRuntimeImpl))
    }

    override fun createInteractionHandler(runtime: ViewRuntime): InteractionHandler {
        return SpongeUIInteractionHandler((runtime as ViewRuntimeImpl))
    }

    override fun runComponentFunction(runtime: ViewRuntime, scope: ComponentScope?, fn: ComponentScope.() -> Unit) {
        fn(ComponentScopeImpl(runtime as ViewRuntimeImpl, scope))
    }

    override val componentFactory: ComponentFactory = ComponentFactoryImpl()

}