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

import com.wolfyscript.viewportl.common.gui.BuildContext
import com.wolfyscript.viewportl.gui.animation.Animation
import com.wolfyscript.viewportl.gui.animation.AnimationFrame
import com.wolfyscript.viewportl.gui.animation.AnimationFrameBuilder
import com.wolfyscript.viewportl.gui.components.NativeComponent
import java.util.function.Supplier

class AnimationBuilderImpl<F : AnimationFrame, FB : AnimationFrameBuilder<F>>(
    context: BuildContext,
    frameBuilderSupplier: Supplier<FB>
) :
    AnimationBuilderCommonImpl<F, FB>(context.reactiveSource, frameBuilderSupplier) {
    override fun build(nativeComponent: NativeComponent): Animation<F> {
        if (updateSignal == null) {
//            component.getID() + "_click_animation_handler", Boolean.TYPE,
            updateSignal = reactiveSource.createSignal(Boolean::class.java) { false }
        }

        return AnimationImpl(nativeComponent, frameBuilders, updateSignal!!)
    }
}
