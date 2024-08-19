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

import com.wolfyscript.scafall.wrappers.world.items.ItemStackConfig;
import com.wolfyscript.viewportl.gui.GuiHolder;
import com.wolfyscript.viewportl.gui.ViewRuntime;
import com.wolfyscript.viewportl.gui.animation.Animation;
import com.wolfyscript.viewportl.gui.animation.ButtonAnimationFrame;
import com.wolfyscript.viewportl.gui.rendering.RenderContext;

public class ButtonAnimationFrameImpl implements ButtonAnimationFrame {

    private final Animation<ButtonAnimationFrame> animation;
    private final ItemStackConfig stack;
    private final int duration;

    protected ButtonAnimationFrameImpl(Animation<ButtonAnimationFrame> animation, int duration, ItemStackConfig stack) {
        this.animation = animation;
        this.duration = duration;
        this.stack = stack;
    }

    @Override
    public int duration() {
        return duration;
    }

    @Override
    public Animation<ButtonAnimationFrame> animation() {
        return animation;
    }

    @Override
    public void render(ViewRuntime viewManager, GuiHolder holder, RenderContext context) {

    }

    @Override
    public ItemStackConfig stack() {
        return stack;
    }

}
