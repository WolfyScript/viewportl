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
package com.wolfyscript.viewportl.spigotlike.gui.factories

import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.common.gui.factories.ElementFactoryImpl
import com.wolfyscript.viewportl.common.gui.factories.GuiFactoryCommon
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.Window
import com.wolfyscript.viewportl.gui.factories.ElementFactory
import com.wolfyscript.viewportl.gui.factories.GuiFactory
import com.wolfyscript.viewportl.gui.interaction.InteractionHandler
import com.wolfyscript.viewportl.spigotlike.gui.inventoryui.interaction.SpigotLikeInvUIInteractionHandler
import org.bukkit.plugin.Plugin
import java.util.*
import java.util.function.Function

abstract class SpigotLikeGuiFactoryImpl(val bukkitPlugin: Plugin) : GuiFactoryCommon() {

    override fun createInventoryUIRuntime(
        viewportl: Viewportl,
        callback: Function<ViewRuntime, Window>,
        viewers: Set<UUID>
    ): ViewRuntime {
        return ViewRuntimeImpl(
            viewportl,
            callback,
            viewers,
            renderer = createInventoryRenderer(),
            interactionHandler = SpigotLikeInvUIInteractionHandler(bukkitPlugin),
        )
    }

    override fun createInventoryInteractionHandler(): InteractionHandler<*> {
        return SpigotLikeInvUIInteractionHandler(bukkitPlugin)
    }

    override val elementFactory: ElementFactory = ElementFactoryImpl()
}
