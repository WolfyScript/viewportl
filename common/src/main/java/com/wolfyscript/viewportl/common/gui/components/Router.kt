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
package com.wolfyscript.viewportl.common.gui.components

import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.viewportl.gui.*
import com.wolfyscript.scafall.function.ReceiverConsumer
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.BuildContext
import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.gui.components.*
import com.wolfyscript.viewportl.gui.reactivity.*
import com.wolfyscript.viewportl.gui.router.ActivePath
import com.wolfyscript.viewportl.gui.router.MatchPath
import java.util.*


@StaticNamespacedKey(key = "router")
class RouterImpl constructor(
    parent: NativeComponent?,
    viewportl: Viewportl,
    val context: BuildContext,
    val window: Window
) : AbstractNativeComponentImpl<Router>("", viewportl, parent), Router {

    override val routes: MutableList<Route> = mutableListOf()
    override val history: ReadWriteSignal<Deque<ActivePath>> = context.reactiveSource.createSignal(ArrayDeque<ActivePath>().apply { add(ActivePath()) })
    override val currentPath: ActivePath? by context.reactiveSource.createMemo<ActivePath> { history.get().peek() }
    private var currentRootComponent: NativeComponentGroup? = null

    override fun open() {
        currentRootComponent?.insert(context.runtime as ViewRuntimeImpl, 0)
    }

    override fun route(
        pathConfig: ReceiverConsumer<MatchPath>,
        viewConfig: ReceiverConsumer<ComponentScope>,
        routeConfig: ReceiverConsumer<Route>?
    ) {
        val path = MatchPath()
        with(pathConfig) { path.consume() }

        routes.add(RouteImpl(context, this, path, viewConfig))
    }

    override fun remove(runtime: ViewRuntime, nodeId: Long, parentNode: Long) {
        TODO("Not yet implemented")
    }

    override fun insert(runtime: ViewRuntime, parentNode: Long) {
        TODO("Not yet implemented")
    }

}

class RouteImpl(
    val context: BuildContext,
    private val router: Router,
    override var path: MatchPath,
    override var view: ReceiverConsumer<ComponentScope>
) : Route {

    override val routes: MutableList<Route> = mutableListOf()

    override fun route(
        pathConfig: ReceiverConsumer<MatchPath>,
        viewConfig: ReceiverConsumer<ComponentScope>,
        routeConfig: ReceiverConsumer<Route>
    ) {
        val path = MatchPath()
        with(pathConfig) { path.consume() }

        routes.add(RouteImpl(context, router, path, viewConfig))
    }

    override fun init(outlet: Outlet) {
        val selectedRoute: Memo<Route> = context.reactiveSource.createMemo {
            router.currentPath?.let { path ->
                routes.firstOrNull { route -> route.path.matches(path) }
            }
        }

        context.reactiveSource.createEffect {
            selectedRoute.get()?.let { selectedRoute ->
                val component = context.getOrCreateComponent(type = NativeComponentGroup::class.java)
                outlet.component = component

                component.findNextOutlet()?.apply {
                    selectedRoute.init(this)
                }
            }
        }

    }
}

class RouterScopeImpl(
    runtime: ViewRuntime,
    override val router: Router,
    val buildContext: BuildContext,
    private val history: ReadWriteSignal<Deque<ActivePath>>
) : RouterScope {

    val routes = ArrayList<Route>()

    override fun route(
        pathConfig: ReceiverConsumer<MatchPath>,
        viewConfig: ReceiverConsumer<ComponentScope>
    ) {
        val path = MatchPath()
        with(pathConfig) { path.consume() }

        routes.add(RouteImpl(buildContext, router, path, viewConfig))
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

}
