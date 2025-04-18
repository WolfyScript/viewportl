/*
 *     viewportl - multiplatform GUI framework to easily create reactive GUIs
 *     Copyright (C) 2024  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.wolfyscript.viewportl.spigot.gui.factories

import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.common.gui.elements.ComponentScopeImpl
import com.wolfyscript.viewportl.common.gui.factories.ElementFactoryImpl
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.elements.ComponentScope
import com.wolfyscript.viewportl.gui.factories.ElementFactory
import com.wolfyscript.viewportl.gui.factories.GuiFactory
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.gui.rendering.Renderer
import com.wolfyscript.viewportl.spigot.gui.inventoryui.interaction.SpigotInvUIInteractionHandler
import com.wolfyscript.viewportl.spigot.gui.inventoryui.rendering.SpigotInvUIRenderer
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
            renderer = SpigotInvUIRenderer(),
            interactionHandler = SpigotInvUIInteractionHandler(),
        )
    }

    override fun createInventoryRenderer(): Renderer<*, *> {
        return SpigotInvUIRenderer()
    }

    override fun createInventoryInteractionHandler(): InteractionHandler<*> {
        return SpigotInvUIInteractionHandler()
    }

    override fun runComponentFunction(runtime: ViewRuntime<*, *>, scope: ComponentScope?, fn: ComponentScope.() -> Unit) {
        fn(ComponentScopeImpl(runtime as ViewRuntimeImpl, scope))
    }

    override val elementFactory: ElementFactory = ElementFactoryImpl()
}
