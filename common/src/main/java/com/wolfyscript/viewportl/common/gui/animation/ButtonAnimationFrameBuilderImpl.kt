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

import com.wolfyscript.scafall.function.ReceiverConsumer
import com.wolfyscript.scafall.function.ReceiverFunction
import com.wolfyscript.scafall.identifier.Key.Companion.MINECRAFT_NAMESPACE
import com.wolfyscript.scafall.identifier.Key.Companion.key
import com.wolfyscript.scafall.wrappers.world.items.ItemStack
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.gui.ItemHelper
import com.wolfyscript.viewportl.gui.animation.Animation
import com.wolfyscript.viewportl.gui.animation.ButtonAnimationFrame
import com.wolfyscript.viewportl.gui.animation.ButtonAnimationFrameBuilder

class ButtonAnimationFrameBuilderImpl(private val viewportl: Viewportl) : ButtonAnimationFrameBuilder {
    private var duration = 1
    private var stack: ItemStack? = null

    override fun stack(itemId: String, config: ReceiverConsumer<ItemStack>): ButtonAnimationFrameBuilder {
        this.stack = viewportl.scafall.factories.itemsFactory.createStack(key(MINECRAFT_NAMESPACE, itemId))

        with(config) {
            stack!!.consume()
        }
        return this
    }

    override fun stack(config: ReceiverFunction<ItemHelper, ItemStack>): ButtonAnimationFrameBuilder {
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
