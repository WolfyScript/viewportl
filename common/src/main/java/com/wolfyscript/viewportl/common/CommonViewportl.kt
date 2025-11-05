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

package com.wolfyscript.viewportl.common

import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.ViewportlClient
import com.wolfyscript.viewportl.ViewportlServer

abstract class CommonViewportl : Viewportl {

    override var server: ViewportlServer? = null
        set(value) {
            field = value
            if (value != null) {
                serverListeners.forEach { it(value) }
                serverListeners.clear()
            }
        }
    override var client: ViewportlClient? = null
        set(value) {
            field = value
            if (value != null) {
                clientListeners.forEach { it(value) }
                clientListeners.clear()
            }
        }

    private val clientListeners = mutableListOf<(ViewportlClient) -> Unit>()
    private val serverListeners = mutableListOf<(ViewportlServer) -> Unit>()

    override fun onInit() {

    }

    override fun onClientAvailable(fn: (client: ViewportlClient) -> Unit) {
        if (client == null) {
            clientListeners.add(fn)
            return
        }
        fn(client!!)
    }

    override fun onServerAvailable(fn: (server: ViewportlServer) -> Unit) {
        if (server == null) {
            serverListeners.add(fn)
            return
        }
        fn(server!!)
    }

}