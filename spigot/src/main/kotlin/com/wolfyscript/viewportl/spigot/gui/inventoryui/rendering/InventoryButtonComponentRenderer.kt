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

package com.wolfyscript.viewportl.spigot.gui.inventoryui.rendering

import com.wolfyscript.scafall.eval.context.EvalContext
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.common.gui.rendering.ComponentRenderer
import com.wolfyscript.viewportl.gui.ItemStackContext
import com.wolfyscript.viewportl.gui.elements.Button
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver

class InventoryButtonComponentRenderer : ComponentRenderer<Button, SpigotInvUIRenderContext> {

    override fun key(): Key = Key.defaultKey("inventory/button")

    override fun render(context: SpigotInvUIRenderContext, component: Button) {
        context.renderer.renderStack(component.styles.position.slotPositioning()?.slot() ?: context.currentOffset(), component.icon.stack, ItemStackContext(
            TagResolver.empty(), context.renderer.runtime.viewportl.scafall.adventure.miniMsg, EvalContext()))
    }
}