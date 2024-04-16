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

package com.wolfyscript.utilities.compatibility.plugins.magic;

import com.elmakers.mine.bukkit.api.event.LoadEvent;
import com.elmakers.mine.bukkit.api.magic.MagicAPI;
import com.google.inject.Inject;
import com.wolfyscript.utilities.spigot.WolfyCoreSpigot;
import com.wolfyscript.utilities.bukkit.annotations.WUPluginIntegration;
import com.wolfyscript.utilities.bukkit.compatibility.PluginIntegrationAbstract;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

@WUPluginIntegration(pluginName = MagicImpl.PLUGIN_NAME)
public class MagicImpl extends PluginIntegrationAbstract implements Listener {

    static final String PLUGIN_NAME = "Magic";

    @Inject
    protected MagicImpl(WolfyCoreSpigot core) {
        super(core, PLUGIN_NAME);
    }

    @Override
    public void init(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, core.getWolfyUtils().getPlugin());
        core.getRegistries().getStackIdentifierParsers().register(new MagicStackIdentifier.Parser(Bukkit.getPluginManager().getPlugin("Magic") instanceof MagicAPI magicAPI ? magicAPI : null));
    }

    @Override
    public boolean hasAsyncLoading() {
        return true;
    }

    @EventHandler
    public void onComplete(LoadEvent event) {
        if (event.getController() != null) { //Makes sure to only mark as done when Magic will actually be enabled!
            markAsDoneLoading();
        } else {
            ignore();
        }
    }
}
