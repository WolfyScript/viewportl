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
package com.wolfyscript.viewportl.common.gui.animation

import com.wolfyscript.viewportl.gui.animation.Animation
import com.wolfyscript.viewportl.gui.animation.AnimationFrame
import com.wolfyscript.viewportl.gui.animation.AnimationFrameBuilder
import com.wolfyscript.viewportl.gui.components.Component
import com.wolfyscript.viewportl.gui.reactivity.ReadWriteSignal

abstract class AnimationCommonImpl<F : AnimationFrame?> protected constructor(
    private val owner: Component,
    frameBuilders: List<AnimationFrameBuilder<F>?>,
    private val updateSignal: ReadWriteSignal<*>
) :
    Animation<F> {
    private val frames: List<F> =
        frameBuilders.stream().map { frame: AnimationFrameBuilder<F>? -> frame!!.build(this) }.toList()

    override fun frames(): List<F> {
        return frames
    }

    override fun owner(): Component {
        return owner
    }

    override fun updateSignal(): ReadWriteSignal<*> {
        return updateSignal
    }
}
