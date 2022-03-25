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

package me.wolfyscript.utilities.expansions;

import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.plugin.Plugin;

import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public abstract class ResourceLoader implements Keyed {

    private final Plugin plugin;
    private final NamespacedKey key;
    protected final String folderPath;

    protected ResourceLoader(Plugin plugin, NamespacedKey namespacedKey) {
        this.plugin = plugin;
        String namespace = plugin.getName().toLowerCase(Locale.ROOT);
        Preconditions.checkArgument(namespacedKey.getNamespace().equals(namespace), "The namespace must be equal to your plugin name! Expected " + namespace + " but got " + namespace);
        this.key = namespacedKey;
        this.folderPath = key.toString("/") + "/";
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public abstract void load(ExpansionPack pack, ZipFile zipFile, String root, ZipEntry entry, WolfyUtilCore core);

    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
    }
}
