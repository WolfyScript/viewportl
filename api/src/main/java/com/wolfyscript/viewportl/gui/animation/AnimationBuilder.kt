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
package com.wolfyscript.viewportl.gui.animation

import com.wolfyscript.scafall.function.ReceiverConsumer
import com.wolfyscript.viewportl.gui.elements.Element
import com.wolfyscript.viewportl.gui.reactivity.Signal

interface AnimationBuilder<F : AnimationFrame, FB : AnimationFrameBuilder<F>> {
    /**
     *
     *
     * @param frameBuild
     * @return
     */
    fun frame(frameBuild: ReceiverConsumer<FB>): AnimationBuilder<F, FB>

    /**
     * Optional: Can be used to manually start the animation.
     * The animation is started whenever the value of the signal changes.
     *
     * @param signal The signal to listen to
     * @return This builder for chaining
     */
    fun customSignal(signal: Signal<*>): AnimationBuilder<F, FB>

    fun build(owner: Element): Animation<F>
}
