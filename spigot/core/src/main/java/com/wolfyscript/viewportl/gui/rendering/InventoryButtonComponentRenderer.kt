/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
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

package com.wolfyscript.viewportl.gui.rendering

import com.wolfyscript.scafall.eval.context.EvalContext
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.gui.ItemStackContext
import com.wolfyscript.viewportl.gui.components.Button
import com.wolfyscript.viewportl.gui.rendering.ComponentRenderer
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

class InventoryButtonComponentRenderer : ComponentRenderer<Button, com.wolfyscript.viewportl.gui.rendering.InvGUIRenderContext> {

    override fun key(): Key = Key.defaultKey("inventory/button")

    override fun render(context: com.wolfyscript.viewportl.gui.rendering.InvGUIRenderContext, component: Button) {

        context.renderer.renderStack(context.currentOffset(), component.icon.stack, object :
            ItemStackContext {

            override fun resolvers(): TagResolver = component.icon.resolvers

            override fun miniMessage(): MiniMessage = context.renderer.runtime.scaffolding.adventure.miniMsg

            override fun evalContext(): EvalContext = EvalContext()

        })
    }
}