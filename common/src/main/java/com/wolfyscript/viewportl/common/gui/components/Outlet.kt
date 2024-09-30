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

import com.fasterxml.jackson.annotation.JacksonInject
import com.fasterxml.jackson.annotation.JsonProperty
import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.into
import com.wolfyscript.viewportl.gui.components.NativeComponent
import com.wolfyscript.viewportl.gui.components.NativeComponentImplementation
import com.wolfyscript.viewportl.gui.components.Outlet
import com.wolfyscript.viewportl.gui.components.OutletProperties
import javax.annotation.Nullable

internal fun setupOutlet(properties: OutletProperties) {
    val runtime = properties.scope.runtime.into()
    val id = (properties.scope as ComponentScopeImpl).setComponent(OutletImpl("", runtime.viewportl, properties.scope.parent?.component))
}

@NativeComponentImplementation(base = Outlet::class)
@StaticNamespacedKey(key = "outlet")
class OutletImpl(
    @JsonProperty("id") id: String,
    @JacksonInject("viewportl") viewportl: Viewportl,
    @Nullable @JacksonInject("parent") parent: NativeComponent? = null,
) : AbstractNativeComponentImpl<Outlet>(id, viewportl, parent), Outlet