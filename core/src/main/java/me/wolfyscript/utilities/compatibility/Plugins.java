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
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Plugins {

    private WolfyUtilCore core;
    private final Map<String, PluginIntegration> pluginIntegrations = new HashMap<>();

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
        core.getLogger().info("Registering Plugin integrations: ");
        for (Class<?> integrationClass : PluginIntegration.class.getClasses()) {
            WUPluginIntegration annotation = integrationClass.getAnnotation(WUPluginIntegration.class);
            if (annotation != null && integrationClass.isAssignableFrom(PluginIntegration.class)) {
                try {
                    registerIntegration((Class<? extends PluginIntegration>) integrationClass, annotation);
                } catch (IllegalArgumentException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void registerIntegration(Class<? extends PluginIntegration> integrationClass, WUPluginIntegration annotation) throws IllegalArgumentException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        String pluginName = annotation.pluginName();
        if (!pluginIntegrations.containsKey(pluginName)) {
            core.getLogger().info("     - " + pluginName);
            Constructor<? extends PluginIntegration> integrationConstructor = integrationClass.getDeclaredConstructor(WolfyUtilCore.class);
            PluginIntegration integration = integrationConstructor.newInstance(core);
            pluginIntegrations.put(pluginName, integration);
            return;
        }
        throw new IllegalArgumentException("Failed to add Integration! A Plugin Integration for \"" + pluginName + "\" already exists!");
    }

    public Collection<PluginIntegration> getPluginIntegrations() {
        return Collections.unmodifiableCollection(pluginIntegrations.values());
    }



}
