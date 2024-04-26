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
package com.wolfyscript.utilities.compatibility.plugins.placeholderapi.value_providers

import com.fasterxml.jackson.annotation.JsonProperty
import com.wolfyscript.utilities.WolfyCore
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.bukkit.compatibility.plugins.PlaceholderAPIIntegration
import com.wolfyscript.utilities.bukkit.eval.context.EvalContextPlayer
import com.wolfyscript.utilities.eval.context.EvalContext
import com.wolfyscript.utilities.eval.value_provider.AbstractValueProvider
import com.wolfyscript.utilities.spigot.WolfyCoreSpigot
import org.bukkit.entity.Player

abstract class ValueProviderPlaceholderAPI<V> protected constructor(
    @field:JsonProperty("value") val placeholder: String
) : AbstractValueProvider<V>() {
    protected fun getPlaceholderValue(context: EvalContext?): String {
        if (context is EvalContextPlayer) {
            context.player?.let { player ->
                val integration: PlaceholderAPIIntegration? = (WolfyCore.instance as WolfyCoreSpigot).compatibilityManager.getPlugins()
                    .getIntegration(PlaceholderAPIIntegration.KEY, PlaceholderAPIIntegration::class.java)
                if (integration != null) {
                    val result = integration.setPlaceholders(player, placeholder)
                    return integration.setBracketPlaceholders(player, result)
                }
            }
        }
        return ""
    }
}
