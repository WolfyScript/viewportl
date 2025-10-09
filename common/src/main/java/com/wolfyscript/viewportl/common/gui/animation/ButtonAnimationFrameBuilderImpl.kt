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
package com.wolfyscript.viewportl.common.gui.animation

import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.wrappers.world.items.ScafallItemStack
import com.wolfyscript.scafall.wrappers.wrap
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.gui.ItemHelper
import com.wolfyscript.viewportl.gui.animation.Animation
import com.wolfyscript.viewportl.gui.animation.ButtonAnimationFrame
import com.wolfyscript.viewportl.gui.animation.ButtonAnimationFrameBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import kotlin.jvm.optionals.getOrNull

class ButtonAnimationFrameBuilderImpl(private val viewportl: Viewportl) : ButtonAnimationFrameBuilder {
    private var duration = 1
    private var stack: ScafallItemStack? = null

    override fun stack(itemId: String, config: ScafallItemStack.() -> Unit): ButtonAnimationFrameBuilder {
        val newStack = BuiltInRegistries.ITEM[ResourceLocation.fromNamespaceAndPath(Key.MINECRAFT_NAMESPACE, itemId)].getOrNull()
        newStack?.let {
            stack = ItemStack(newStack).wrap()
            stack?.let { config(it) }
        }
        return this
    }

    override fun stack(config: ItemHelper.() -> ScafallItemStack): ButtonAnimationFrameBuilder {
        // TODO
        return this
    }

    override fun duration(duration: Int): ButtonAnimationFrameBuilder {
        this.duration = duration
        return this
    }

    override fun build(animation: Animation<ButtonAnimationFrame>): ButtonAnimationFrame {
        return ButtonAnimationFrameImpl(animation, duration, stack!!)
    }
}
