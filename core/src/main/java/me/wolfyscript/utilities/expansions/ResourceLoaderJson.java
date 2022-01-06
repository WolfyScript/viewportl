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

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.plugin.Plugin;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public abstract class ResourceLoaderJson<T extends Keyed> extends ResourceLoader {

    private final Class<T> type;

    protected ResourceLoaderJson(Plugin plugin, NamespacedKey namespacedKey, Class<T> type) {
        super(plugin, namespacedKey);
        this.type = type;
    }

    public abstract void register(NamespacedKey namespacedKey, T value, WolfyUtilCore core);

    @Override
    public void load(ExpansionPack pack, ZipFile zipFile, String root, ZipEntry entry, WolfyUtilCore core) {
        if (entry.isDirectory()) {
            //We can't read from a directory!
            return;
        }
        String path = entry.getName().replace(root + folderPath, "");
        path = path.substring(0, path.lastIndexOf(".")); // This the key of the effect.
        try (var stream = new BufferedInputStream(zipFile.getInputStream(entry))) {
            var item = JacksonUtil.getObjectMapper().readValue(stream, type);
            if (item != null) {
                this.register(new NamespacedKey(pack.getMeta().getPack().getNamespace(), path), item, core);
            }
        } catch (IOException e) {
            core.getLogger().info("Failed to load animation \"" + path + "\"! Cause:" + e.getMessage());
        }
    }
}
