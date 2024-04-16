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

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.bukkit.annotations.WUPluginIntegration;
import com.wolfyscript.utilities.bukkit.events.compatibility.PluginIntegrationEnableEvent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * To add a PluginIntegration you need to extend this class and add the annotation {@link WUPluginIntegration} to that class.<br>
 * <br>
 * The constructor must have only one parameter of type {@link WolfyCoreCommon}, that is passed to the super class.<br>
 * <br>
 * To effectively pass the plugin name to the annotation and PluginIntegration it is recommended to create a constant.
 * <br>
 * For example:
 * <pre>
 * <code>
 *
 * {@literal @WUPluginInteration(pluginName = MyIntegration.PLUGIN_NAME)}
 *  public class MyIntegration extends PluginIntegration {
 *
 *      static final String PLUGIN_NAME = "YOUR_PLUGIN_NAME";
 *
 *      protected MyIntegration(WolfyCoreBukkit core) {
 *          super(core, PLUGIN_NAME);
 *      }
 *
 *      ...
 *
 *  }
 * </code>
 * </pre>
 */
public abstract class PluginIntegrationAbstract implements PluginIntegration {

    private final String pluginName;
    protected final WolfyCoreCommon core;
    private boolean enabled = false;
    private boolean ignore = false;

    /**
     * The main constructor that is called whenever the integration is created.<br>
     *
     * @param core The WolfyUtilCore.
     * @param pluginName The name of the associated plugin.
     */
    protected PluginIntegrationAbstract(WolfyCoreCommon core, String pluginName) {
        this.core = core;
        this.pluginName = pluginName;
    }

    /**
     * This method is called if the plugin is enabled.
     * @param plugin The plugin, that was enabled.
     */
    public abstract void init(Plugin plugin);

    /**
     * Checks if the integrated plugin loads data async.
     * 
     * @return True if the integration has an async loader; false otherwise.
     */
    public abstract boolean hasAsyncLoading();

    /**
     * Gets the plugin name of this integration.
     * 
     * @return The name of the plugin associated with this integration.
     */
    public final String getAssociatedPlugin() {
        return pluginName;
    }

    public final WolfyCoreCommon getCore() {
        return core;
    }

    @Override
    public final boolean isDoneLoading() {
        return enabled;
    }

    protected final void enable() {
        if (!this.enabled) {
            this.enabled = true;
            core.getLogger().info("Enabled plugin integration for " + getAssociatedPlugin());
            Bukkit.getPluginManager().callEvent(new PluginIntegrationEnableEvent(core, this));
            ((PluginsBukkit) core.getCompatibilityManager().getPlugins()).checkDependencies();
        }
    }

    final void disable() {
        this.enabled = false;
    }

    @Override
    public final boolean shouldBeIgnored() {
        return ignore;
    }

    /**
     * Marks this integration as done. That tells the system that, the integrations' plugin is done with loading all its data.<br>
     * For example, usually plugins with async data loading will provide a listener that will be called once done. <br>
     * This method can then be used inside that event to mark it as done.
     *
     * @deprecated Use {@link #enable()} instead!
     */
    @Deprecated
    protected final void markAsDoneLoading() {
        enable();
    }

    protected final void ignore() {
        disable();
        this.ignore = true;
    }

    @Override
    public <T extends PluginAdapter> T getAdapter(Class<T> type, NamespacedKey key) {
        return core.getCompatibilityManager().getPlugins().getAdapter(pluginName, type, key);
    }

}
