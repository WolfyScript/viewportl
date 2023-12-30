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

import java.util.function.Consumer;

public interface ReactiveRenderBuilder {

    <B extends ComponentBuilder<? extends Component, Component>> ReactiveResult render(String id, Class<B> builderType, Consumer<B> builderConsumer);

    <B extends ComponentBuilder<? extends Component, Component>> ReactiveResult renderAt(Position position, String id, Class<B> builderType, Consumer<B> builderConsumer);

    interface ReactiveResult {

        Component construct();

    }

}
