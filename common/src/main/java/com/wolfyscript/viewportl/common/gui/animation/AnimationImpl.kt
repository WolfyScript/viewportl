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

import com.wolfyscript.viewportl.gui.GuiHolder
import com.wolfyscript.viewportl.gui.ViewRuntime
import com.wolfyscript.viewportl.gui.animation.AnimationFrame
import com.wolfyscript.viewportl.gui.animation.AnimationFrameBuilder
import com.wolfyscript.viewportl.gui.elements.Element
import com.wolfyscript.viewportl.gui.reactivity.Effect
import com.wolfyscript.viewportl.gui.reactivity.Signal
import com.wolfyscript.viewportl.gui.rendering.RenderContext
import java.util.concurrent.atomic.AtomicInteger

class AnimationImpl<F : AnimationFrame> internal constructor(
    owner: Element,
    animationFrameBuilders: List<AnimationFrameBuilder<F>>,
    updateSignal: Signal<*>,
) :
    AnimationCommonImpl<F>(owner, animationFrameBuilders, updateSignal) {

    fun render(viewManager: ViewRuntime, guiHolder: GuiHolder, context: RenderContext) {
        val frameDelay = AtomicInteger(0)
        val frameIndex = AtomicInteger(0)
        viewManager.viewportl.scafall.scheduler
            .task(viewManager.viewportl.scafall.modInfo)
            .execute {
                val delay = frameDelay.getAndIncrement()
                val frame = frameIndex.get()
                if (frames().size <= frame) {
                    cancel()
                    if (owner() is Effect) {
                        // TODO
                    }
                    return@execute
                }

                val frameObj: AnimationFrame = frames()[frame]
                if (delay <= frameObj.duration()) {
                    frameObj.render(viewManager, guiHolder, context)
                    return@execute
                }
                frameIndex.incrementAndGet()
                frameDelay.set(0)
            }
            .interval(1)
            .build()
    }
}
