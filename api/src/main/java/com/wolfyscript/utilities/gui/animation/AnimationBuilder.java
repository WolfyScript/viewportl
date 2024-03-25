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

package com.wolfyscript.utilities.gui.animation;

import com.wolfyscript.utilities.gui.Component;
import com.wolfyscript.utilities.gui.functions.ReceiverConsumer;
import com.wolfyscript.utilities.gui.reactivity.Signal;

public interface AnimationBuilder<F extends AnimationFrame, FB extends AnimationFrameBuilder<F>> {

    /**
     *
     *
     * @param frameBuild
     * @return
     */
    AnimationBuilder<F, FB> frame(ReceiverConsumer<FB> frameBuild);

    /**
     * Optional: Can be used to manually start the animation.
     * The animation is started whenever the value of the signal changes.
     *
     * @param signal The signal to listen to
     * @return This builder for chaining
     */
    AnimationBuilder<F, FB> customSignal(Signal<?> signal);

    Animation<F> build(Component owner);

}
