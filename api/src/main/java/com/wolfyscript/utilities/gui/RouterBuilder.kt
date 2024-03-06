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
package com.wolfyscript.utilities.gui

import com.wolfyscript.utilities.gui.callback.InteractionCallback
import com.wolfyscript.utilities.gui.functions.ReceiverBiConsumer
import java.util.function.BiConsumer

interface RouterBuilder {

    fun interact(interactionCallback: InteractionCallback): RouterBuilder

    fun route(path: String, subRouteBuilder: ReceiverBiConsumer<RouterBuilder, ReactiveSource>): RouterBuilder

    fun window(windowBuilder: ReceiverBiConsumer<WindowBuilder, ReactiveSource>): RouterBuilder

    fun window(): WindowBuilder

    fun create(parent: Router): Router

}