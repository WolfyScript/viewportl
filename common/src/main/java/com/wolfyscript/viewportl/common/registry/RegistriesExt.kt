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

package com.wolfyscript.viewportl.common.registry

import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.scafall.registry.Registries
import com.wolfyscript.viewportl.registry.RegistryGUIElementTypes
import org.jetbrains.annotations.ApiStatus.Internal

private var guiComponentsRegistry: RegistryUIElementImplementations? = null

val Registries.guiComponents : RegistryGUIElementTypes
    get() = guiComponentsRegistry ?: throw RuntimeException("Viewportl Registries not yet initialised!")

@Internal
internal fun Registries.initViewportlRegistries() {

    guiComponentsRegistry = RegistryUIElementImplementations(Key.key(Key.SCAFFOLDING_NAMESPACE, "components/implementations"), this)
}
