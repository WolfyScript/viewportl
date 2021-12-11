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
import org.bukkit.plugin.Plugin;

/**
 * To add a PluginIntegration you need to extend this class and add the annotation {@link me.wolfyscript.utilities.annotations.WUPluginIntegration} to that class.<br>
 * <br>
 * The constructor must have only one parameter of type {@link WolfyUtilCore}, that is passed to the super class.<br>
 * The constructor must not include any plugin dependent objects, etc.<br>
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
 *      protected MyIntegration(WolfyUtilCore core) {
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
    protected final WolfyUtilCore core;
    private boolean enabled = false;

    /**
     * The main constructor that is called whenever the integration is created.<br>
     * <strong>At this point, it didn't check if the packages of the depending plugin are available yet! So do not use any plugin specific Objects, etc.</strong>
     *
     * @param core The WolfyUtilCore.
     * @param pluginName The name of the associated plugin.
     */
    protected PluginIntegrationAbstract(WolfyUtilCore core, String pluginName) {
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
    public String getAssociatedPlugin() {
        return pluginName;
    }

    public WolfyUtilCore getCore() {
        return core;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    protected void setEnabled() {
        markAsEnabled();
    }

    /**
     * Used to mark this plugin data loading as complete.<br>
     * This is necessary if the plugin is loading data async.
     */
    void markAsEnabled() {
        enabled = true;


    }

}
