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
package com.wolfyscript.utilities.gui.router

import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.gui.*
import com.wolfyscript.utilities.gui.components.ComponentGroup
import com.wolfyscript.utilities.functions.ReceiverConsumer
import com.wolfyscript.utilities.gui.reactivity.*
import java.util.*

@KeyedStaticId(key = "router")
class RouterImpl internal constructor(
    val wolfyUtils: WolfyUtils,
    val context: BuildContext,
    val window: Window
) : Router {

    override val routes: MutableList<Route> = mutableListOf()
    override val history: Signal<Deque<ActivePath>> = context.reactiveSource.createSignal(ArrayDeque<ActivePath>().apply { add(ActivePath()) })
    override val currentPath: SignalGet<ActivePath> = context.reactiveSource.createMemo {
        history.get()?.peek()
    }

    var currentRootComponent: ComponentGroup? = null

    override fun open() {
        currentRootComponent?.insert(context.runtime as ViewRuntimeImpl, 0)
    }

    init {
        val selectedRoute: Memo<Route> = context.reactiveSource.createMemo {
            currentPath.get()?.let { path ->
                routes.firstOrNull { route -> route.path.matches(path) }
            }
        }

        context.reactiveSource.createEffect<ComponentGroup?> {
            this?.remove(context.runtime as ViewRuntimeImpl, this.nodeId(), 0)

            selectedRoute.get()?.let { selectedRoute ->
                val component = context.getOrCreateComponent(type = ComponentGroup::class.java)
                with(selectedRoute.view) { component.consume() }
                component.finalize()
                currentRootComponent = component
                component.insert(context.runtime, 0)

                component.findNextOutlet()?.apply {
                    selectedRoute.init(this)
                }
                component
            }
        }
    }

    override fun route(
        pathConfig: ReceiverConsumer<MatchPath>,
        viewConfig: ReceiverConsumer<ComponentGroup>,
        routeConfig: ReceiverConsumer<Route>?
    ) {
        val path = MatchPath()
        with(pathConfig) { path.consume() }

        routes.add(RouteImpl(context, this, path, viewConfig))
    }

}
