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

public final class CompatibilityManagerImpl implements CompatibilityManager {

    private final WolfyUtilCore core;
    private final PluginsImpl pluginsImpl;

    public CompatibilityManagerImpl(WolfyUtilCore core) {
        this.core = core;
        this.pluginsImpl = new PluginsImpl(core);
    }

    public void init() {
        pluginsImpl.init();
    }

    @Override
    public Plugins getPlugins() {
        return pluginsImpl;
    }
}
