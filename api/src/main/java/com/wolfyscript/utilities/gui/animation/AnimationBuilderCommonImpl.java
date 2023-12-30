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

import com.wolfyscript.utilities.gui.DynamicConstructor;
import com.wolfyscript.utilities.gui.signal.Signal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class AnimationBuilderCommonImpl<F extends AnimationFrame, FB extends AnimationFrameBuilder<F>> implements AnimationBuilder<F, FB> {

    protected final DynamicConstructor dynamicConstructor;
    protected final List<FB> frameBuilders = new ArrayList<>();
    protected final Supplier<FB> frameBuilderSupplier;
    protected Signal<?> updateSignal;

    public AnimationBuilderCommonImpl(DynamicConstructor dynamicConstructor, Supplier<FB> frameBuilderSupplier) {
        this.dynamicConstructor = dynamicConstructor;
        this.frameBuilderSupplier = frameBuilderSupplier;
    }

    @Override
    public AnimationBuilderCommonImpl<F, FB> customSignal(Signal<?> signal) {
        this.updateSignal = signal;
        return this;
    }

    @Override
    public AnimationBuilderCommonImpl<F, FB> frame(Consumer<FB> frameBuild) {
        var frameBuilder = frameBuilderSupplier.get();
        frameBuild.accept(frameBuilder);
        frameBuilders.add(frameBuilder);
        return this;
    }



}
