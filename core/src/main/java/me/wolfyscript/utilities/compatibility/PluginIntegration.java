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

package me.wolfyscript.utilities.compatibility;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public interface PluginIntegration {

    void init(Plugin plugin);

    /**
     * Checks if the integrated plugin loads data async.
     *
     * @return True if the integration has an async loader; false otherwise.
     */
    boolean hasAsyncLoading();

    /**
     * Gets the plugin name of this integration.
     *
     * @return The name of the plugin associated with this integration.
     */
    String getAssociatedPlugin();

    @Nullable
    default APIReference getAPIReference() { return null; };

    default boolean isAPIReferenceIncluded(APIReference reference) { return false; }

    WolfyUtilCore getCore();

    boolean isDoneLoading();
}
