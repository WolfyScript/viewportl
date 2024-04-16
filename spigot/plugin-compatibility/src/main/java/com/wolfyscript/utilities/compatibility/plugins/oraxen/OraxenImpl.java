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

package com.wolfyscript.utilities.compatibility.plugins.oraxen;

import com.wolfyscript.utilities.bukkit.WolfyCoreCommon;
import com.wolfyscript.utilities.bukkit.annotations.WUPluginIntegration;
import com.wolfyscript.utilities.bukkit.compatibility.plugins.OraxenIntegration;
import com.wolfyscript.utilities.bukkit.compatibility.PluginIntegrationAbstract;
import org.bukkit.plugin.Plugin;

@WUPluginIntegration(pluginName = OraxenIntegration.KEY)
public class OraxenImpl extends PluginIntegrationAbstract implements OraxenIntegration {

    private final boolean IS_LATEST_API = WolfyCoreCommon.hasClass("io.th0rgal.oraxen.api.OraxenItems");

    protected OraxenImpl(WolfyCoreCommon core) {
        super(core, OraxenIntegration.KEY);
    }

    @Override
    public void init(Plugin plugin) {
        core.getRegistries().getStackIdentifierParsers().register(new OraxenStackIdentifier.Parser());
    }

    public boolean isLatestAPI() {
        return IS_LATEST_API;
    }

    @Override
    public boolean hasAsyncLoading() {
        return false;
    }

}
