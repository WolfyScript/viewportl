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

import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.annotations.WUPluginIntegration;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.events.DependenciesLoadedEvent;
import me.wolfyscript.utilities.util.NamespacedKey;
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
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Manages compatibility with other plugins. <br>
 * It will load plugin specific integrations that are initialised if the corresponding plugins are enabled.
 */
final class PluginsImpl implements Plugins, Listener {

    private final WolfyUtilCore core;
    private final Map<String, Map<NamespacedKey, PluginAdapter>> pluginAdapters = new HashMap<>();
    private final Map<String, PluginIntegrationAbstract> pluginIntegrations = new HashMap<>();
    private final Map<String, Class<? extends PluginIntegrationAbstract>> pluginIntegrationClasses = new HashMap<>();
    private boolean doneLoading = false;

    PluginsImpl(WolfyUtilCore core) {
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
     * To check if a PluginIntegration is enabled you need to use the {@link PluginIntegration#isDoneLoading()}.<br>
     */
    void init() {
        core.getLogger().info("Loading Plugin integrations: ");
        Bukkit.getPluginManager().registerEvents(this, core);
        for (Class<?> integrationClass : core.getReflections().getTypesAnnotatedWith(WUPluginIntegration.class)) {
            WUPluginIntegration annotation = integrationClass.getAnnotation(WUPluginIntegration.class);
            if (annotation != null && PluginIntegrationAbstract.class.isAssignableFrom(integrationClass)) {
                String pluginName = annotation.pluginName();
                if (Bukkit.getPluginManager().getPlugin(pluginName) != null) { //Only load for plugins that are loaded.
                    core.getLogger().info(" - " + pluginName);
                    if (!pluginIntegrationClasses.containsKey(pluginName)) {
                        pluginIntegrationClasses.put(pluginName, (Class<? extends PluginIntegrationAbstract>) integrationClass);
                    } else {
                        core.getLogger().severe("Failed to add Integration! A Plugin Integration for \"" + pluginName + "\" already exists!");
                    }
                }
            }
        }
        if (!pluginIntegrationClasses.isEmpty()) {
            //Initialize the plugin integrations for that the plugin is already enabled.
            pluginIntegrationClasses.forEach(this::createPluginIntegration);
        } else {
            core.getLogger().info(" - No integrations found");
            doneLoading = true;
        }
    }

    private void createPluginIntegration(String pluginName) {
        createPluginIntegration(pluginName, pluginIntegrationClasses.get(pluginName));
    }

    private void createPluginIntegration(String pluginName, Class<? extends PluginIntegrationAbstract> integrationClass) {
        if (!pluginIntegrations.containsKey(pluginName) && integrationClass != null) {
            try {
                Constructor<? extends PluginIntegrationAbstract> integrationConstructor = integrationClass.getDeclaredConstructor(WolfyUtilCore.class);
                integrationConstructor.setAccessible(true);
                var integration = integrationConstructor.newInstance(core);
                pluginIntegrations.put(pluginName, integration);
                if (isPluginEnabled(pluginName)) {
                    //Only init the integration if the plugin has already been enabled!
                    integration.init(Bukkit.getPluginManager().getPlugin(pluginName));
                    if (!integration.hasAsyncLoading()) {
                        integration.setEnabled(true);
                    }
                    core.getLogger().info(" - " + pluginName + (integration.hasAsyncLoading() ? " [async]" : ""));
                } else {
                    core.getLogger().info(" - " + pluginName);
                }
                checkDependencies();
            } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException | RuntimeException e) {
                core.getLogger().warning("     Failed to initialise integration for " + pluginName + "! Cause: " + e.getMessage());
                pluginIntegrations.remove(pluginName);
            }
        }
    }

    /**
     * Checks if all available integrations are created and enabled.
     * If that is the case it calls the {@link DependenciesLoadedEvent}
     */
    void checkDependencies() {
        int availableIntegrations = pluginIntegrationClasses.size();
        long enabledIntegrations = pluginIntegrations.values().stream().filter(PluginIntegrationAbstract::isDoneLoading).count();
        if (availableIntegrations == enabledIntegrations) {
            doneLoadingAndCallEvent();
        }
    }

    /**
     * Marks this handler as done and calls the {@link DependenciesLoadedEvent}
     */
    void doneLoadingAndCallEvent() {
        doneLoading = true;
        Bukkit.getScheduler().runTaskLater(core, () -> {
            core.getLogger().info("All dependencies are loaded. Calling the DependenciesLoadedEvent to notify other plugins!");
            Bukkit.getPluginManager().callEvent(new DependenciesLoadedEvent(core));
        }, 10);
    }

    @EventHandler
    private void onPluginDisable(PluginDisableEvent event) {
        String pluginName = event.getPlugin().getName();
        pluginIntegrationClasses.remove(pluginName);
        pluginIntegrations.remove(pluginName);
    }

    @EventHandler
    private void onPluginEnable(PluginEnableEvent event) {
        createPluginIntegration(event.getPlugin().getName());
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

    /**
     * Gets the integration of the specified plugin and type.<br>
     * In case there is no integration available, it returns null.<br>
     *
     * @param pluginName The plugin name to get the integration for.
     * @return The integration from the plugin; null if not available.
     */
    @Nullable
    public PluginIntegration getIntegration(String pluginName) {
        return pluginIntegrations.get(pluginName);
    }

    /**
     * Gets the integration of the specified plugin and type.<br>
     * In case there is no integration available, it returns null.<br>
     * If it does exist it will try to cast the integration to the specified type; if that fails throws an {@link ClassCastException}.
     *
     * @param pluginName The plugin name to get the integration for.
     * @param type       The class of the plugins' integration that extends {@link PluginIntegration}.
     * @param <T>        The specified type of the {@link PluginIntegration}
     * @return The integration from the plugin of type {@link T}; null if not available.
     * @throws ClassCastException if the found {@link PluginIntegration} cannot be cast to {@link T}
     */
    @Nullable
    public <T extends PluginIntegration> T getIntegration(String pluginName, Class<T> type) {
        var integration = getIntegration(pluginName);
        try {
            return type.cast(integration);
        } catch (ClassCastException ex) {
            throw new ClassCastException("Cannot cast Integration! The integration of plugin \"" + pluginName + "\" is not of type " + type.getName());
        }
    }

    /**
     * Runs the specified callback if there is an active PluginIntegration available for that plugin.
     *
     * @param pluginName The plugin name to check for the integration.
     * @param callback   The callback to run.
     */
    public void runIfAvailable(String pluginName, Consumer<PluginIntegration> callback) {
        var integration = getIntegration(pluginName);
        if (integration != null) {
            callback.accept(integration);
        }
    }

    /**
     * Runs the specified callback if there is an active PluginIntegration available for that plugin and the if the integration is of the type specified.
     *
     * @param pluginName The plugin name to check for the integration.
     * @param type       The class that extends {@link PluginIntegration}.
     * @param callback   The callback to run.
     * @param <T>        The type of {@link PluginIntegration} to check for and use in the callback.
     */
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

    public Collection<PluginIntegration> getPluginIntegrations() {
        return Collections.unmodifiableCollection(pluginIntegrations.values());
    }

    @Override
    public boolean isDoneLoading() {
        return doneLoading;
    }

    @Override
    public void registerAdapter(String pluginName, Supplier<PluginAdapter> pluginAdapter) {
        Optional.ofNullable(Bukkit.getPluginManager().getPlugin(pluginName)).ifPresent(plugin -> registerAdapter(pluginAdapter.get(), pluginAdapters.computeIfAbsent(pluginName, s -> new HashMap<>())));
    }

    private <T extends PluginAdapter> void registerAdapter(T adapter, Map<NamespacedKey, T> adapters) {
        Preconditions.checkArgument(adapter != null, "The plugin adapter cannot be null!");
        Preconditions.checkArgument(!adapters.containsKey(adapter.getNamespacedKey()), "A plugin adapter with that key was already registered!");
        adapters.putIfAbsent(adapter.getNamespacedKey(), adapter);
    }

    @Override
    public <T extends PluginAdapter> T getAdapter(String pluginName, Class<T> type, NamespacedKey key) {
        Map<NamespacedKey, PluginAdapter> adapters = pluginAdapters.get(pluginName);
        if (adapters != null) {
            var adapter = adapters.get(key);
            if (type.isInstance(adapter)) {
                return type.cast(adapter);
            }
        }
        return null;
    }
}
