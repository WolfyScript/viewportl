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
package com.wolfyscript.viewportl.common.gui.router

import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.viewportl.gui.*
import com.wolfyscript.viewportl.gui.components.ComponentGroup
import com.wolfyscript.scafall.function.ReceiverConsumer
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.BuildContext
import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.gui.reactivity.*
import com.wolfyscript.viewportl.gui.router.ActivePath
import com.wolfyscript.viewportl.gui.router.MatchPath
import com.wolfyscript.viewportl.gui.router.Route
import com.wolfyscript.viewportl.gui.router.Router
import java.util.*

@StaticNamespacedKey(key = "router")
class RouterImpl internal constructor(
    val viewportl: Viewportl,
    val context: BuildContext,
    val window: Window
) : Router {

    override val routes: MutableList<Route> = mutableListOf()
    override val history: ReadWriteSignal<Deque<ActivePath>> = context.reactiveSource.createSignal(ArrayDeque<ActivePath>().apply { add(ActivePath()) })
    override val currentPath: ActivePath? by context.reactiveSource.createMemo<ActivePath> { history.get()?.peek() }
    private var currentRootComponent: ComponentGroup? = null

    override fun open() {
        currentRootComponent?.insert(context.runtime as ViewRuntimeImpl, 0)
    }

    override fun openPrevious() {
        history.update {
            it.pop()
            it
        }
    }

    override fun openRoute(path: ReceiverConsumer<ActivePath>) {
        history.update {
            it.apply {
                val newPath = ActivePath()
                with(path) { newPath.consume() }
                if (newPath != it.peek()) {
                    it.push(newPath)
                }
            }
        }
    }

    override fun openSubRoute(path: ReceiverConsumer<ActivePath>) {
        history.update {
            it.apply {
                val copy = it.peek().copy()
                with(path) { copy.consume() }
                if (copy != it.peek()) {
                    it.push(copy)
                }
            }
        }
    }

    init {
        val selectedRoute: Memo<Route> = context.reactiveSource.createMemo {
            currentPath?.let { path ->
                routes.firstOrNull { route -> route.path.matches(path) }
            }
        }

        context.reactiveSource.createEffect {
            val component = selectedRoute.get()?.let { selectedRoute ->
                val component = context.getOrCreateComponent(type = ComponentGroup::class.java)
                with(selectedRoute.view) { component.consume() }
                component.completeBuild()
                currentRootComponent = component
                component.insert(context.runtime, 0)

                component.findNextOutlet()?.apply {
                    selectedRoute.init(this)
                }
                component
            }

            context.reactiveSource.createCleanup {
                component?.remove(context.runtime as ViewRuntimeImpl, component.nodeId(), 0)
                currentRootComponent = null
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
