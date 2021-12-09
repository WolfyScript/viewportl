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

import me.wolfyscript.utilities.annotations.WUPluginIntegration;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.compatibility.plugins.itemsadder.ItemsAdderRef;
import me.wolfyscript.utilities.compatibility.PluginIntegration;

@WUPluginIntegration(pluginName = ItemsAdderImpl.PLUGIN_NAME)
public class ItemsAdderImpl extends PluginIntegration {

    static final String PLUGIN_NAME = "ItemsAdder";

    protected ItemsAdderImpl(WolfyUtilCore core) {
        super(core, PLUGIN_NAME);
    }

    @Override
    public void init() {
        core.registerAPIReference(new ItemsAdderRef.Parser());

    }

    @Override
    public boolean hasAsyncLoading() {
        return true;
    }
}
