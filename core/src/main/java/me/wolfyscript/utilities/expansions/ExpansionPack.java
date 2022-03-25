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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipFile;

public class ExpansionPack {

    static final int VERSION = 1;

    private final WolfyUtilCore core;
    private final PackMetaFile meta;
    private final File file;
    private final List<String> zipEntryNames;
    private final String rootDir;

    ExpansionPack(@NotNull PackMetaFile meta, File file, List<String> entryNames, WolfyUtilCore core) {
        this.file = file;
        this.core = core;
        this.zipEntryNames = entryNames;
        this.rootDir = entryNames.get(0);
        this.meta = meta;
    }

    public void load(RegistryResourceLoader registry, List<NamespacedKey> loaderOrder) throws IOException {
        var zipFile = new ZipFile(file);
        try (zipFile) {
            for (NamespacedKey namespacedKey : loaderOrder) {
                var loader = registry.get(namespacedKey);
                if (loader != null) {
                    String loaderRoot = rootDir + loader.folderPath; //Get the root of the loader
                    int rootIndex = zipEntryNames.indexOf(loaderRoot); // Get the index of that root in the zip file.
                    if (rootIndex == -1) {
                        continue; // The loader root is not in the zip file.
                    }
                    //We start one index later, as the first one is always the root folder
                    for (int i = rootIndex + 1; i < zipEntryNames.size(); i++) {
                        String entryName = zipEntryNames.get(i);
                        if (!entryName.startsWith(loaderRoot)) {
                            break;
                        }
                        var entry = zipFile.getEntry(entryName);
                        if (entry != null) {
                            // Call the loader specific load method
                            try {
                                loader.load(this, zipFile, rootDir, entry, core);
                            } catch (Exception e) {
                                // We don't want a faulty loader to crash the whole loading process and/or plugin!
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    public PackMetaFile getMeta() {
        return meta;
    }

    public File getFile() {
        return file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExpansionPack pack)) return false;
        return meta.getPack().getNamespace().equals(pack.meta.getPack().getNamespace());
    }

    @Override
    public int hashCode() {
        return meta.getPack().getNamespace().hashCode();
    }
}
