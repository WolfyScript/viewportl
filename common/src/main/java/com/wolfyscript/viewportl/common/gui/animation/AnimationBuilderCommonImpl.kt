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

import com.wolfyscript.viewportl.gui.animation.AnimationBuilder
import com.wolfyscript.viewportl.gui.animation.AnimationFrame
import com.wolfyscript.viewportl.gui.animation.AnimationFrameBuilder
import com.wolfyscript.viewportl.gui.reactivity.ReactiveSource
import com.wolfyscript.viewportl.gui.reactivity.Signal
import java.util.function.Supplier

abstract class AnimationBuilderCommonImpl<F : AnimationFrame, FB : AnimationFrameBuilder<F>>(
    protected val reactiveSource: ReactiveSource,
    private val frameBuilderSupplier: Supplier<FB>
) :
    AnimationBuilder<F, FB> {
    protected val frameBuilders: MutableList<FB> = ArrayList()
    protected var updateSignal: Signal<*>? = null

    override fun customSignal(signal: Signal<*>): AnimationBuilderCommonImpl<F, FB> {
        this.updateSignal = signal
        return this
    }

    override fun frame(frameBuild: FB.() -> Unit): AnimationBuilder<F, FB> {
        val frameBuilder = frameBuilderSupplier.get()
        frameBuild(frameBuilder)
        frameBuilders.add(frameBuilder)
        return this
    }
}
