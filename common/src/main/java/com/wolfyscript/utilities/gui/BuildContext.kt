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

import com.google.inject.Binder
import com.google.inject.Guice
import com.google.inject.Module
import com.google.inject.Stage
import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.tuple.Pair
import java.util.*

class BuildContext(val reactiveSource: ReactiveSourceImpl, private val wolfyUtils: WolfyUtils) {

    private val componentIdAliases: MutableMap<String, Long> = HashMap()
    private val componentBuilderMap: MutableMap<Long, ComponentBuilder<*, *>> = HashMap()

    fun <B : ComponentBuilder<out Component, Component>> findExistingComponentBuilder(
        id: Long,
        builderImplType: Class<B>,
        builderKey: NamespacedKey
    ): Optional<B> {
        val componentBuilder = componentBuilderMap[id] ?: return Optional.empty()
        if (componentBuilder.type != builderKey) {
            throw IllegalArgumentException("Incompatible Component Builder Type! Expected type '$builderKey' but existing builder is of type '${componentBuilder.type}'!")
        }
        return Optional.of(builderImplType.cast(componentBuilder))
    }

    fun <B : ComponentBuilder<out Component, Component>> instantiateNewBuilder(numericId: Long, position: Position, builderTypeInfo: Pair<NamespacedKey, Class<B>>): Long {
        val injector = Guice.createInjector(Stage.PRODUCTION, Module { binder: Binder ->
            binder.bind(WolfyUtils::class.java).toInstance(wolfyUtils)
            binder.bind(Long::class.java).toInstance(numericId)
            binder.bind(Position::class.java).toInstance(position)
            binder.bind(BuildContext::class.java).toInstance(this)
        })

        val builder = injector.getInstance(builderTypeInfo.value)
        componentBuilderMap[numericId] = builder!!
        return numericId
    }

    fun getOrCreateNumericId(namedId: String? = null): Long {
        if (namedId != null) {
            if (componentIdAliases.containsKey(namedId)) return componentIdAliases[namedId]!!
            val numericId = ComponentUtil.nextId()
            componentIdAliases[namedId] = numericId
            return numericId
        }
        return ComponentUtil.nextId()
    }

    fun registerBuilder(componentBuilder: ComponentBuilder<*,*>) : Long {
        val numericId = ComponentUtil.nextId()
        componentIdAliases[componentBuilder.id()] = numericId
        wolfyUtils.logger.fine("Register Component Builder: " + componentBuilder.id() + "  (" + numericId + ")")
        componentBuilderMap[numericId] = componentBuilder
        return numericId
    }

    fun getBuilder(id: Long) : ComponentBuilder<*,*>? {
        return componentBuilderMap[id]
    }

    fun <B> getBuilder(id: Long, builderType: Class<B>) : B? {
        val untyped = getBuilder(id) ?: return null
        if (!builderType.isInstance(untyped)) throw IllegalArgumentException("Builder with $id is not of type ${builderType.name}")
        return builderType.cast(untyped)
    }

}
