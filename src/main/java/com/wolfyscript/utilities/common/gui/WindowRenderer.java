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

package com.wolfyscript.utilities.common.gui;

import com.wolfyscript.utilities.common.gui.functions.SerializableConsumer;
import com.wolfyscript.utilities.common.gui.functions.SerializableSupplier;
import java.util.function.Consumer;

public interface WindowRenderer extends Renderer<WindowState> {

    interface Builder extends Renderer.Builder<WindowRenderer> {

        Builder title(SerializableSupplier<net.kyori.adventure.text.Component> titleSupplier);


        /**
         * <p>
         *     Constructs a reactive function to select Components to render.
         *     An empty list or null means it renders nothing.
         * </p>
         * <p>
         *     The function detects all the signals inside the lambda that are from outside the lambda.
         *     Each of these signals will cause the function to re-run on updates.
         * </p>
         *
         * @param reactiveFunction The function to run on signal updates.
         * @return This builder for chaining.
         */
        @Override
        Builder reactive(SerializableConsumer<ReactiveRenderBuilder> reactiveFunction);

        <B extends ComponentBuilder<? extends com.wolfyscript.utilities.common.gui.Component, com.wolfyscript.utilities.common.gui.Component>> Builder position(int slot, String id, Class<B> builderType, Consumer<B> builderConsumer);

        <B extends ComponentBuilder<? extends com.wolfyscript.utilities.common.gui.Component, com.wolfyscript.utilities.common.gui.Component>> Builder render(String id, Class<B> builderType, Consumer<B> builderConsumer);

        <B extends ComponentBuilder<? extends com.wolfyscript.utilities.common.gui.Component, com.wolfyscript.utilities.common.gui.Component>> Builder renderAt(int slot, String id, Class<B> builderType, Consumer<B> builderConsumer);

    }

    interface ReactiveRenderBuilder {

        <B extends ComponentBuilder<? extends Component, Component>> ReactiveRenderBuilder render(String id, Class<B> builderType, Consumer<B> builderConsumer);

        <B extends ComponentBuilder<? extends Component, Component>> ReactiveRenderBuilder renderAt(int slot, String id, Class<B> builderType, Consumer<B> builderConsumer);

    }

}
