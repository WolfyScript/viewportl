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
import com.wolfyscript.utilities.gui.reactivity.Signal;

import java.util.List;

public abstract class AnimationCommonImpl<F extends AnimationFrame> implements Animation<F> {

    private final Component owner;
    private final List<F> frames;
    private final Signal<?> updateSignal;

    protected AnimationCommonImpl(Component owner, List<? extends AnimationFrameBuilder<F>> frameBuilders, Signal<?> updateSignal) {
        this.owner = owner;
        this.frames = frameBuilders.stream().map(frame -> frame.build(this)).toList();
        this.updateSignal = updateSignal;
    }

    @Override
    public List<F> frames() {
        return frames;
    }

    @Override
    public Component owner() {
        return owner;
    }

    @Override
    public Signal<?> updateSignal() {
        return updateSignal;
    }

}
