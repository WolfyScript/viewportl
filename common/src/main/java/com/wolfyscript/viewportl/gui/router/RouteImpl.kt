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

package com.wolfyscript.viewportl.gui.router

import com.wolfyscript.viewportl.gui.BuildContext
import com.wolfyscript.viewportl.gui.components.ComponentGroup
import com.wolfyscript.viewportl.gui.components.Outlet
import com.wolfyscript.utilities.functions.ReceiverConsumer
import com.wolfyscript.viewportl.gui.reactivity.Memo
import com.wolfyscript.viewportl.gui.reactivity.createMemo

class RouteImpl(
    val context: BuildContext,
    private val router: Router,
    override var path: MatchPath,
    override var view: ReceiverConsumer<ComponentGroup>
) : Route {

    override val routes: MutableList<Route> = mutableListOf()

    override fun route(
        pathConfig: ReceiverConsumer<MatchPath>,
        viewConfig: ReceiverConsumer<ComponentGroup>,
        routeConfig: ReceiverConsumer<Route>
    ) {
        val path = MatchPath()
        with(pathConfig) { path.consume() }

        routes.add(RouteImpl(context, router, path, viewConfig))
    }

    override fun init(outlet: Outlet) {
        val selectedRoute: Memo<Route> = context.reactiveSource.createMemo {
            router.currentPath.get()?.let { path ->
                routes.firstOrNull { route -> route.path.matches(path) }
            }
        }

        context.reactiveSource.createEffect {
            selectedRoute.get()?.let { selectedRoute ->
                val component = context.getOrCreateComponent(type = ComponentGroup::class.java)
                with(selectedRoute.view) { component.consume() }
                component.finalize()
                outlet.component = component

                component.findNextOutlet()?.apply {
                    selectedRoute.init(this)
                }
            }
        }

    }
}