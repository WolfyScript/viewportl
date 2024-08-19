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
package com.wolfyscript.viewportl.gui.router

import com.wolfyscript.viewportl.gui.components.ComponentGroup
import com.wolfyscript.scafall.function.ReceiverConsumer
import com.wolfyscript.viewportl.gui.reactivity.ReadWriteSignal
import com.wolfyscript.viewportl.gui.reactivity.ReadOnlySignal
import java.util.Deque

/**
 *
 */
interface Router {

    val routes: List<Route>
    val currentPath: ActivePath?
    val history : ReadWriteSignal<Deque<ActivePath>>

    fun open()

    fun openPrevious()

    fun openRoute(path: ReceiverConsumer<ActivePath>)

    fun openSubRoute(path: ReceiverConsumer<ActivePath>)

    fun route(pathConfig: ReceiverConsumer<MatchPath>, viewConfig: ReceiverConsumer<ComponentGroup>, routeConfig: ReceiverConsumer<Route>? = null)

}
