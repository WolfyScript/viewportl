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

import me.wolfyscript.utilities.annotations.WUPluginIntegration;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class Plugins implements Listener {

    private final WolfyUtilCore core;
    private final Map<String, PluginIntegrationAbstract> pluginIntegrations = new HashMap<>();
    private final Map<String, Class<? extends PluginIntegrationAbstract>> pluginIntegrationClasses = new HashMap<>();

    Plugins(WolfyUtilCore core) {
        this.core = core;
    }

    /**
     * Looks for available PluginIntegrations and loads them.<br>
     * <br>
     * The PluginIntegrations, for that the corresponding plugins are already enabled at this point, will be initiated.<br>
     * In case a corresponding plugin is disabled, the loaded class will be removed and the integration won't be initiated!
     * This applies to plugins that are disabled afterwards too!
     * <br>
     * <br>
     * Other PluginIntegrations will be initiated when the corresponding plugins are enabled, or if they are loading data async, when they completely loaded that data.<br>
     * <br>
     * To check if a PluginIntegration is enabled you need to use the {@link PluginIntegration#isEnabled()}.<br>
     */
    public void init() {
        core.getLogger().info("Loading Plugin integrations: ");
        for (Class<?> integrationClass : core.getReflections().getTypesAnnotatedWith(WUPluginIntegration.class)) {
            WUPluginIntegration annotation = integrationClass.getAnnotation(WUPluginIntegration.class);
            if (annotation != null && PluginIntegrationAbstract.class.isAssignableFrom(integrationClass)) {
                String pluginName = annotation.pluginName();
                if (Bukkit.getPluginManager().getPlugin(pluginName) != null) { //Only load for plugins that are loaded.
                    core.getLogger().info("     - " + pluginName);
                    if (!pluginIntegrationClasses.containsKey(pluginName)) {
                        pluginIntegrationClasses.put(pluginName, (Class<? extends PluginIntegrationAbstract>) integrationClass);
                    } else {
                        core.getLogger().info("         ERROR -> Failed to add Integration! A Plugin Integration for \"" + pluginName + "\" already exists!");
                    }
                }
            }
        }
        if (!pluginIntegrationClasses.isEmpty()) {
            core.getLogger().info("Create & Init Plugin integrations: ");
            //Initialize the plugin integrations for that the plugin is already enabled.
            pluginIntegrationClasses.forEach(this::createPluginIntegration);
            if (pluginIntegrations.isEmpty()) {
                core.getLogger().info("     - No integrations created.");
            }
            core.getLogger().info("Created Plugin integrations of already enabled plugins. Creating other integrations when their associated plugin is enabled.");
        } else {
            core.getLogger().info("     - No integrations found for available plugins");
        }
    }

    private void createPluginIntegration(String pluginName, Class<? extends PluginIntegrationAbstract> integrationClass) {
        if (integrationClass != null) {
            try {
                core.getLogger().info("     - " + pluginName);
                Constructor<? extends PluginIntegrationAbstract> integrationConstructor = integrationClass.getDeclaredConstructor(WolfyUtilCore.class);
                integrationConstructor.setAccessible(true);
                pluginIntegrations.put(pluginName, integrationConstructor.newInstance(core));
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                core.getLogger().info("         ERROR -> " + e.getMessage());
            }
        }
    }

    public Collection<PluginIntegrationAbstract> getPluginIntegrations() {
        return Collections.unmodifiableCollection(pluginIntegrations.values());
    }

    @EventHandler
    private void onPluginDisable(PluginDisableEvent event) {
        String pluginName = event.getPlugin().getName();
        pluginIntegrationClasses.remove(pluginName);
        pluginIntegrations.remove(pluginName);
    }

    @EventHandler
    private void onPluginEnable(PluginEnableEvent event) {
        String pluginName = event.getPlugin().getName();
        Class<? extends PluginIntegrationAbstract> integrationClass = pluginIntegrationClasses.get(pluginName);
        if(integrationClass != null) {
            createPluginIntegration(pluginName, integrationClass);
            if (!evaluateIfAvailable(event.getPlugin().getName(), pluginIntegration -> {
                core.getLogger().info("Initiated PluginIntegration for " + pluginIntegration.getAssociatedPlugin());
                if (!pluginIntegration.hasAsyncLoading()) {
                    ((PluginIntegrationAbstract) pluginIntegration).markAsEnabled();
                    return true;
                }
                core.getLogger().info("Integration is async. Enabled once done loading!");
                return true;
            })) {
                core.getLogger().warning("Failed to initiate PluginIntegration for " + pluginName);
            }
        }
    }



    /**
     * @param pluginName The name of the plugin to check for
     * @return If the plugin is loaded
     */
    public boolean isPluginEnabled(String pluginName) {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }

    public boolean hasWorldGuard() {
        return isPluginEnabled("WorldGuard");
    }

    public boolean hasPlotSquared() {
        return isPluginEnabled("PlotSquared");
    }

    public boolean hasLWC() {
        return isPluginEnabled("LWC");
    }

    public boolean hasPlaceHolderAPI() {
        return isPluginEnabled("PlaceholderAPI");
    }

    public boolean hasMcMMO() {
        return isPluginEnabled("mcMMO");
    }

    public boolean hasIntegration(String pluginName) {
        return pluginIntegrations.containsKey(pluginName);
    }

    @Nullable
    public PluginIntegration getIntegration(String pluginName) {
        return pluginIntegrations.get(pluginName);
    }

    @Nullable
    public <T extends PluginIntegration> T getIntegration(String pluginName, Class<T> type) {
        var integration = getIntegration(pluginName);
        if (type.isInstance(integration)) {
            return type.cast(integration);
        }
        if (integration != null) {
            throw new IllegalArgumentException("The integration of plugin \"" + pluginName + "\" is of type " + type.getName());
        }
        return null;
    }

    /**
     * Runs the specified callback if there is an active PluginIntegration available for that plugin.
     *
     * @param pluginName The plugin name to check for the integration.
     * @param callback The callback to run.
     */
    public void runIfAvailable(String pluginName, Consumer<PluginIntegration> callback) {
        var integration = getIntegration(pluginName);
        if (integration != null) {
            callback.accept(integration);
        }
    }

    public <T extends PluginIntegration> void runIfAvailable(String pluginName, Class<T> type, Consumer<T> callback) {
        var integration = getIntegration(pluginName, type);
        if (integration != null) {
            callback.accept(integration);
        }
    }

    public boolean evaluateIfAvailable(String pluginName, Function<PluginIntegration, Boolean> callback) {
        var integration = getIntegration(pluginName);
        if (integration != null) {
            return callback.apply(integration);
        }
        return false;
    }

    public <T extends PluginIntegration> boolean evaluateIfAvailable(String pluginName, Class<T> type, Function<T, Boolean> callback) {
        var integration = getIntegration(pluginName, type);
        if (integration != null) {
            return callback.apply(integration);
        }
        return false;
    }

}
