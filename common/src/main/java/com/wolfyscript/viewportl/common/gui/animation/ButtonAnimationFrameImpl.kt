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

import com.wolfyscript.scafall.wrappers.world.items.ItemStackConfig
import com.wolfyscript.viewportl.gui.GuiHolder
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.animation.Animation
import com.wolfyscript.viewportl.gui.animation.ButtonAnimationFrame
import com.wolfyscript.viewportl.gui.rendering.RenderContext

class ButtonAnimationFrameImpl(
    private val animation: Animation<ButtonAnimationFrame>,
    private val duration: Int,
    private val stack: ItemStackConfig
) :
    ButtonAnimationFrame {
    override fun duration(): Int {
        return duration
    }

    override fun animation(): Animation<ButtonAnimationFrame> {
        return animation
    }

    override fun render(viewManager: ViewRuntime<*,*>, holder: GuiHolder, context: RenderContext) {
    }

    override fun stack(): ItemStackConfig {
        return stack
    }
}
