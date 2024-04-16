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

package com.wolfyscript.utilities.bukkit.compatibility;

import com.google.common.base.Preconditions;
import com.google.inject.ConfigurationException;
import com.google.inject.CreationException;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.ProvisionException;
import com.google.inject.name.Names;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.bukkit.annotations.WUPluginIntegration;
import com.wolfyscript.utilities.bukkit.events.DependenciesLoadedEvent;
import com.wolfyscript.utilities.WolfyCore;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

/**
 * Manages compatibility with other plugins. <br>
 * It will load plugin specific integrations that are initialised if the corresponding plugins are enabled.
 */
final class PluginsBukkit implements Plugins, Listener {

    private final WolfyCoreCommon core;
    private final Map<String, Map<NamespacedKey, PluginAdapter>> pluginAdapters = new HashMap<>();
    private final Map<String, PluginIntegrationAbstract> pluginIntegrations = new HashMap<>();
    private final Map<String, Class<? extends PluginIntegrationAbstract>> pluginIntegrationClasses = new HashMap<>();
    private boolean doneLoading = false;

    PluginsBukkit(WolfyCoreCommon core) {
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
        Bukkit.getPluginManager().registerEvents(this, core.getWolfyUtils().getPlugin());
        for (Class<?> integrationType : core.getReflections().getTypesAnnotatedWith(WUPluginIntegration.class)) {
            WUPluginIntegration annotation = integrationType.getAnnotation(WUPluginIntegration.class);
            if (annotation != null && PluginIntegrationAbstract.class.isAssignableFrom(integrationType)) {
                String pluginName = annotation.pluginName();
                if (Bukkit.getPluginManager().getPlugin(pluginName) != null) { //Only load for plugins that are loaded.
                    core.getLogger().info(" - " + pluginName);
                    Preconditions.checkArgument(!pluginIntegrationClasses.containsKey(pluginName), "Failed to add Integration! A Plugin Integration for \"" + pluginName + "\" already exists!");
                    pluginIntegrationClasses.put(pluginName, (Class<? extends PluginIntegrationAbstract>) integrationType);
                }
            }
        }
        if (!pluginIntegrationClasses.isEmpty()) {
            core.getLogger().info("Create & Init Plugin integrations: ");
            //Initialize the plugin integrations for that the plugin is already enabled.
            pluginIntegrationClasses.forEach(this::createOrInitPluginIntegration);
            if (pluginIntegrations.isEmpty()) {
                core.getLogger().info(" - No integrations created.");
            }
        } else {
            core.getLogger().info(" - No integrations found");
            doneLoading = true;
        }
    }

    private void createOrInitPluginIntegration(String pluginName, Class<? extends PluginIntegrationAbstract> integrationClass) {
        if (integrationClass != null) {
            var integration = pluginIntegrations.computeIfAbsent(pluginName, (key) -> {
                try {
                    Injector injector = Guice.createInjector(binder -> {
                        binder.bindConstant().annotatedWith(Names.named("pluginName")).to(pluginName);
                        binder.bind(WolfyCore.class).toInstance(core);
                        binder.bind(WolfyCoreCommon.class).toInstance(core);
                    });
                    return injector.getInstance(integrationClass);
                } catch (ConfigurationException | ProvisionException | CreationException e) {
                    core.getLogger().warning("     Failed to initialise integration for " + pluginName + "! Cause: " + e.getMessage());
                    return null;
                }
            });
            if (integration == null) return;
            var plugin = Bukkit.getPluginManager().getPlugin(pluginName);
            if (plugin != null && plugin.isEnabled() && !integration.isDoneLoading()) {
                integration.init(plugin);
                if (!integration.hasAsyncLoading()) {
                    integration.enable();
                }
                checkDependencies();
            }
        }
    }

    private void ignoreIntegrationFor(Plugin plugin) {
        runIfAvailable(plugin.getName(), PluginIntegrationAbstract.class, PluginIntegrationAbstract::ignore);
        checkDependencies();
    }

    void checkDependencies() {
        pluginIntegrations.values().removeIf(pluginIntegrationAbstract -> {
            if (pluginIntegrationAbstract.shouldBeIgnored()) {
                pluginIntegrationClasses.remove(pluginIntegrationAbstract.getAssociatedPlugin());
                return true;
            }
            return false;
        });
        int availableIntegrations = pluginIntegrationClasses.size();
        long enabledIntegrations = pluginIntegrations.values().stream().filter(PluginIntegrationAbstract::isDoneLoading).count();
        if (availableIntegrations == enabledIntegrations) {
            doneLoading = true;
            Bukkit.getScheduler().runTaskLater(core.getWolfyUtils().getPlugin(), () -> {
                core.getLogger().info("Dependencies Loaded. Calling DependenciesLoadedEvent!");
                Bukkit.getPluginManager().callEvent(new DependenciesLoadedEvent(core));
            }, 2);
        }
    }

    @EventHandler
    private void onPluginDisable(PluginDisableEvent event) {
        ignoreIntegrationFor(event.getPlugin());
    }

    @EventHandler
    private void onPluginEnable(PluginEnableEvent event) {
        String pluginName = event.getPlugin().getName();
        Class<? extends PluginIntegrationAbstract> integrationClass = pluginIntegrationClasses.get(pluginName);
        if (integrationClass != null) {
            createOrInitPluginIntegration(pluginName, integrationClass);
            if (!hasIntegration(event.getPlugin().getName())) {
                core.getLogger().warning("Failed to initiate PluginIntegration for " + pluginName);
                ignoreIntegrationFor(event.getPlugin());
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
