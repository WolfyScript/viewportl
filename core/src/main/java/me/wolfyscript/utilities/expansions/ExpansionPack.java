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

import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ExpansionPack {

    private static final int VERSION = 1;

    private final ZipFile file;

    ExtensionPack (ZipFile file) {
        this.file = file;
    }

    public static ExtensionPack readPack(String path) throws IOException {
        File file = new File(path);
        if (file.getName().endsWith(".zip")) {
            ZipFile zipFile = new ZipFile(path);
            ZipEntry packInfoEntry = zipFile.getEntry("pack.json");
            if(packInfoEntry != null && !packInfoEntry.isDirectory()) {
                PackMetaFile metaFile = JacksonUtil.getObjectMapper().readValue(zipFile.getInputStream(packInfoEntry), PackMetaFile.class);
                if (metaFile.getPack().getVersion() != VERSION) {
                   //Invalid version!
                   return null;
                }



                return new ExtensionPack(zipFile);
            }
        }
        return null;
    }

}
