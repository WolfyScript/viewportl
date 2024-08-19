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

import com.wolfyscript.scafall.Scafall;
import com.wolfyscript.scafall.identifier.Key;
import com.wolfyscript.scafall.wrappers.world.items.ItemStackConfig;
import com.wolfyscript.viewportl.gui.ItemHelper;
import com.wolfyscript.scafall.function.ReceiverConsumer;
import com.wolfyscript.scafall.function.ReceiverFunction;

public class ButtonAnimationFrameBuilderImpl implements ButtonAnimationFrameBuilder {

    private final Scafall wolfyUtils;
    private int duration;
    private ItemStackConfig stack;

    public ButtonAnimationFrameBuilderImpl(Scafall wolfyUtils) {
        this.duration = 1;
        this.wolfyUtils = wolfyUtils;
    }

    @Override
    public ButtonAnimationFrameBuilder stack(String itemId, ReceiverConsumer<ItemStackConfig> config) {
        this.stack = wolfyUtils.getFactories().getItemsFactory().createStackConfig(Key.Companion.key(Key.MINECRAFT_NAMESPACE, itemId));
        config.consume(stack);
        return this;
    }

    @Override
    public ButtonAnimationFrameBuilder stack(ReceiverFunction<ItemHelper, ItemStackConfig> config) {
        // TODO
        return this;
    }

    @Override
    public ButtonAnimationFrameBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    @Override
    public ButtonAnimationFrame build(Animation<ButtonAnimationFrame> animation) {
        return new ButtonAnimationFrameImpl(animation, duration, stack);
    }


}
