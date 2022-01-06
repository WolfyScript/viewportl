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
import me.wolfyscript.utilities.registry.RegistryResourceLoader;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipFile;

public class ExpansionPack {

    static final int VERSION = 1;

    private final WolfyUtilCore core;
    private final File file;
    private final List<String> zipEntryNames;
    private final String rootDir;

    ExpansionPack(File file, List<String> entryNames, WolfyUtilCore core) {
        this.file = file;
        this.core = core;
        this.zipEntryNames = entryNames;
        this.rootDir = entryNames.get(0);
    }

    public void load(RegistryResourceLoader registry, List<NamespacedKey> loaderOrder) throws IOException {
        var zipFile = new ZipFile(file);
        try (zipFile) {
            for (NamespacedKey namespacedKey : loaderOrder) {
                var loader = registry.get(namespacedKey);
                if (loader != null) {
                    String loaderRoot = rootDir + namespacedKey.getNamespace() + "/" + namespacedKey.getKey() + "/"; //Get the root of the loader
                    int rootIndex = zipEntryNames.indexOf(loaderRoot); // Get the index of that root in the zip file.
                    if (rootIndex == -1) {
                        continue; // The loader root is not in the zip file.
                    }
                    for (int i = rootIndex; i < zipEntryNames.size(); i++) {
                        String entryName = zipEntryNames.get(i);
                        if (!entryName.startsWith(loaderRoot)) {
                            break;
                        }
                        var entry = zipFile.getEntry(entryName);
                        if (entry != null) {
                            // Call the loader specific load method
                            loader.load(entry, core);
                        }
                    }
                }
            }
        }
    }

    public File getFile() {
        return file;
    }
}
