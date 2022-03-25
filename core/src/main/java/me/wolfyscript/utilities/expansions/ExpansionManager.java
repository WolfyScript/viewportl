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
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ExpansionManager {

    private final String LOG_PREFIX = "[Expansions] ";

    private final WolfyUtilCore core;
    private final File packsFolder;

    private List<ExpansionPack> packs;

    public ExpansionManager(WolfyUtilCore core) {
        this.core = core;
        this.packsFolder = new File(core.getDataFolder(), "expansion_packs");
        this.packs = new LinkedList<>();
    }

    public void initPacks() {
        core.getLogger().info(LOG_PREFIX + "Initializing packs...");
        File[] files = packsFolder.listFiles((dir, name) -> name.endsWith(".zip"));
        if (files != null) {
            this.packs = new LinkedList<>();
            for (File file : files) {
                try {
                    var pack = initPack(file);
                    if (pack != null) {
                        packs.add(pack);
                    }
                } catch (IOException e) {
                    core.getLogger().warning(LOG_PREFIX + "Failed to read expansion zip file " + file.getName() + ": " + e.getMessage());
                }
            }
        }
    }

    public void loadPacks() {
        core.getLogger().info(LOG_PREFIX + "Loading packs...");
        var registry = core.getRegistries().getExpansionResourceLoaders();
        List<NamespacedKey> order = registry.getRegisterOrder();
        for (ExpansionPack pack : packs) {
            try {
                pack.load(registry, order);
            } catch (IOException e) {
                core.getLogger().warning(LOG_PREFIX + "Failed to read expansion zip file " + pack.getFile().getName() + ": " + e.getMessage());
            }
        }
    }

    public ExpansionPack initPack(File file) throws IOException {
        ZipFile zipFile = new ZipFile(file);
        try (zipFile) {
            if (zipFile.entries().hasMoreElements()) {
                List<String> entryNames = zipFile.stream().map(ZipEntry::getName).toList();
                ZipEntry packInfoEntry = zipFile.getEntry(entryNames.get(0) + "pack.json");
                if (packInfoEntry != null && !packInfoEntry.isDirectory()) {
                    PackMetaFile metaFile = JacksonUtil.getObjectMapper().readValue(zipFile.getInputStream(packInfoEntry), PackMetaFile.class);
                    if (metaFile.getPack().getVersion() != ExpansionPack.VERSION) {
                        //Invalid version!
                        return null;
                    }
                    ExpansionPack pack = new ExpansionPack(metaFile, file, entryNames, core);
                    if (packs.contains(pack)) {
                        core.getLogger().warning(LOG_PREFIX + "Pack already initialised: \"" + metaFile.getPack().getNamespace() +"\" in file " + file.getName());
                        return null;
                    }
                    return pack;
                }
            }
        }
        return null;
    }

    public List<ExpansionPack> getPacks() {
        return List.copyOf(packs);
    }
}
