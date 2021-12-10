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
import org.jetbrains.annotations.Nullable;

import javax.security.auth.callback.Callback;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public class Plugins {

    private WolfyUtilCore core;
    private final Map<String, PluginIntegrationAbstract> pluginIntegrations = new HashMap<>();
    private final Map<String, Class<? extends PluginIntegrationAbstract>> pluginIntegrationClasses = new HashMap<>();

    Plugins(WolfyUtilCore core) {
        this.core = core;
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

    public boolean hasMythicMobs() {
        return isPluginEnabled("MythicMobs");
    }

    public boolean hasPlaceHolderAPI() {
        return isPluginEnabled("PlaceholderAPI");
    }

    public boolean hasMcMMO() {
        return isPluginEnabled("mcMMO");
    }

    public void init() {
        core.getLogger().info("Loading Plugin integrations: ");
        for (Class<?> integrationClass : core.getReflections().getTypesAnnotatedWith(WUPluginIntegration.class)) {
            WUPluginIntegration annotation = integrationClass.getAnnotation(WUPluginIntegration.class);
            if (annotation != null && PluginIntegrationAbstract.class.isAssignableFrom(integrationClass)) {
                String pluginName = annotation.pluginName();
                core.getLogger().info("     - " + pluginName);
                if (!pluginIntegrationClasses.containsKey(pluginName)) {
                    pluginIntegrationClasses.put(pluginName, (Class<? extends PluginIntegrationAbstract>) integrationClass);
                } else {
                    core.getLogger().info("         ERROR -> Failed to add Integration! A Plugin Integration for \"" + pluginName + "\" already exists!");
                }
            }
        }
        core.getLogger().info("Create & Init Plugin integrations: ");
        //Initialize the plugin integrations for that the plugin is already enabled.
        pluginIntegrationClasses.forEach((pluginName, pluginIntegrationClass) -> {
            if (isPluginEnabled(pluginName)) {
                try {
                    core.getLogger().info("     - " + pluginName);
                    Constructor<? extends PluginIntegrationAbstract> integrationConstructor = pluginIntegrationClass.getDeclaredConstructor(WolfyUtilCore.class);
                    integrationConstructor.setAccessible(true);
                    pluginIntegrations.put(pluginName, integrationConstructor.newInstance(core));
                } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
                    core.getLogger().info("         ERROR -> " + e.getMessage());
                }
            }
        });
        if (pluginIntegrations.isEmpty()) {
            core.getLogger().info("     - No integrations created.");
        }
        core.getLogger().info("Created Plugin integrations of already enabled plugins. Creating other integrations when their associated plugin is enabled.");
    }

    public Collection<PluginIntegrationAbstract> getPluginIntegrations() {
        return Collections.unmodifiableCollection(pluginIntegrations.values());
    }


}
