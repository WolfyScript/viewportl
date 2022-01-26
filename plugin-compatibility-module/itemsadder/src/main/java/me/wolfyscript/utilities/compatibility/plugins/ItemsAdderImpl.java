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

package me.wolfyscript.utilities.compatibility.plugins;

import dev.lone.itemsadder.api.Events.ItemsAdderLoadDataEvent;
import me.wolfyscript.utilities.annotations.WUPluginIntegration;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.inventory.custom_items.references.APIReference;
import me.wolfyscript.utilities.compatibility.PluginIntegrationAbstract;
import me.wolfyscript.utilities.compatibility.plugins.itemsadder.ItemsAdderRefImpl;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

@WUPluginIntegration(pluginName = ItemsAdderIntegration.KEY)
public class ItemsAdderImpl extends PluginIntegrationAbstract implements ItemsAdderIntegration, Listener {

    protected ItemsAdderImpl(WolfyUtilCore core) {
        super(core, ItemsAdderIntegration.KEY);
    }

    @Override
    public void init(Plugin plugin) {
        core.registerAPIReference(new ItemsAdderRefImpl.Parser());
        Bukkit.getPluginManager().registerEvents(this, core);
    }

    @Override
    public boolean hasAsyncLoading() {
        return true;
    }

    @Override
    public boolean isAPIReferenceIncluded(APIReference reference) {
        return reference instanceof ItemsAdderRefImpl;
    }

    @EventHandler
    public void onLoaded(ItemsAdderLoadDataEvent event) {
        if (event.getCause().equals(ItemsAdderLoadDataEvent.Cause.FIRST_LOAD)) {
            markAsDoneLoading();
        }
    }

}
