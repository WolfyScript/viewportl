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

package me.wolfyscript.utilities.compatibility.plugins.placeholderapi.value_providers;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.compatibility.plugins.PlaceholderAPIIntegration;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.eval.context.EvalContext;
import me.wolfyscript.utilities.util.eval.context.EvalContextPlayer;
import me.wolfyscript.utilities.util.eval.value_providers.AbstractValueProvider;

public abstract class ValueProviderPlaceholderAPI<V> extends AbstractValueProvider<V> {

    @JsonProperty("value")
    protected final String value;

    protected ValueProviderPlaceholderAPI(NamespacedKey key, String value) {
        super(key);
        this.value = value;
    }

    protected String getPlaceholderValue(EvalContext context) {
        if (context instanceof EvalContextPlayer playerContext) {
            var player = playerContext.getPlayer();
            var integration = WolfyUtilCore.getInstance().getCompatibilityManager().getPlugins().getIntegration(PlaceholderAPIIntegration.KEY, PlaceholderAPIIntegration.class);
            if (player != null && integration != null) {
                String result = integration.setPlaceholders(player, value);
                return integration.setBracketPlaceholders(player, result);
            }
        }
        return "";
    }
}
