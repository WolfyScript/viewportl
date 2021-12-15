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

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Manages compatibility with other plugins. <br>
 * It will load plugin specific integrations that are initialised if the corresponding plugins are enabled.
 */
public interface Plugins {

    /**
     * @param pluginName The name of the plugin to check for
     * @return If the plugin is loaded
     */
    boolean isPluginEnabled(String pluginName);

    boolean hasWorldGuard();

    boolean hasPlotSquared();

    boolean hasLWC();

    boolean hasPlaceHolderAPI();

    boolean hasMcMMO();

    /**
     * Checks if the integration for the specified plugin is available.
     *
     * @param pluginName The name of plugin to look for.
     * @return True if the integration for the plugin is available.
     */
    boolean hasIntegration(String pluginName);

    /**
     * Gets the integration of the specified plugin and type.<br>
     * In case there is no integration available, it returns null.<br>
     *
     * @param pluginName The plugin name to get the integration for.
     * @return The integration from the plugin; null if not available.
     */
    @Nullable PluginIntegration getIntegration(String pluginName);

    /**
     * Gets the integration of the specified plugin and type.<br>
     * In case there is no integration available, it returns null.<br>
     * If it does exist it will try to cast the integration to the specified type; if that fails throws an {@link ClassCastException}.
     *
     * @param pluginName The plugin name to get the integration for.
     * @param type The class of the plugins' integration that extends {@link PluginIntegration}.
     * @param <T> The specified type of the {@link PluginIntegration}
     * @throws ClassCastException if the found {@link PluginIntegration} cannot be cast to {@link T}
     * @return The integration from the plugin of type {@link T}; null if not available.
     */
    @Nullable <T extends PluginIntegration> T getIntegration(String pluginName, Class<T> type);

    /**
     * Runs the specified callback if there is an active PluginIntegration available for that plugin.
     *
     * @param pluginName The plugin name to check for the integration.
     * @param callback The callback to run.
     */
    void runIfAvailable(String pluginName, Consumer<PluginIntegration> callback);

    /**
     * Runs the specified callback if there is an active PluginIntegration available for that plugin and the if the integration is of the type specified.
     *
     * @param pluginName The plugin name to check for the integration.
     * @param type The class that extends {@link PluginIntegration}.
     * @param callback The callback to run.
     * @param <T> The type of {@link PluginIntegration} to check for and use in the callback.
     */
    <T extends PluginIntegration> void runIfAvailable(String pluginName, Class<T> type, Consumer<T> callback);

    /**
     * Evaluates the specified callback if there is an active PluginIntegration available for that plugin and the if the integration is of the type specified.
     *
     * @param pluginName The plugin name to check for the integration.
     * @param callback The callback to run.
     * @return The value of the evaluated callback; or false if the integration doesn't exist.
     */
    boolean evaluateIfAvailable(String pluginName, Function<PluginIntegration, Boolean> callback);

    /**
     * Evaluates the specified callback if there is an active PluginIntegration available for that plugin and the if the integration is of the type specified.
     *
     * @param pluginName The plugin name to check for the integration.
     * @param type The class that extends {@link PluginIntegration}.
     * @param callback The callback to run.
     * @param <T> The type of {@link PluginIntegration} to check for and use in the callback.
     * @throws ClassCastException if the found {@link PluginIntegration} cannot be cast to {@link T}
     * @return The value of the evaluated callback; or false if the integration doesn't exist.
     */
    <T extends PluginIntegration> boolean evaluateIfAvailable(String pluginName, Class<T> type, Function<T, Boolean> callback);

    /**
     * An unmodifiable list of all available PluginIntegrations.
     *
     * @return A list of available PluginIntegrations.
     */
    Collection<PluginIntegration> getPluginIntegrations();

}
