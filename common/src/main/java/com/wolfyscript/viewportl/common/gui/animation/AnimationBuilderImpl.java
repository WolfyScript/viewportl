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

package com.wolfyscript.viewportl.common.gui.animation;

import com.wolfyscript.viewportl.gui.BuildContext;
import com.wolfyscript.viewportl.gui.components.Component;
import com.wolfyscript.viewportl.gui.BuildContext;
import com.wolfyscript.viewportl.gui.animation.Animation;
import com.wolfyscript.viewportl.gui.animation.AnimationFrame;
import com.wolfyscript.viewportl.gui.animation.AnimationFrameBuilder;

import java.util.function.Supplier;

public class AnimationBuilderImpl<F extends AnimationFrame, FB extends AnimationFrameBuilder<F>> extends AnimationBuilderCommonImpl<F, FB> {

    public AnimationBuilderImpl(BuildContext context, Supplier<FB> frameBuilderSupplier) {
        super(context.getReactiveSource(), frameBuilderSupplier);
    }

    @Override
    public Animation<F> build(Component component) {
        if (updateSignal == null) {
//            component.getID() + "_click_animation_handler", Boolean.TYPE,
            updateSignal = reactiveSource.createSignal(Boolean.class, r -> false);
        }

        return new AnimationImpl<>(component, frameBuilders, updateSignal);
    }
}
