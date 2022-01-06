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

package me.wolfyscript.utilities.main.expansions;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.expansions.ExpansionPack;
import me.wolfyscript.utilities.expansions.ResourceLoader;
import me.wolfyscript.utilities.main.WUPlugin;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.util.particles.ParticleAnimation;
import me.wolfyscript.utilities.util.particles.ParticleEffect;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourceLoaderParticleAnimations extends ResourceLoader {

    public ResourceLoaderParticleAnimations(WUPlugin plugin) {
        super(plugin, new NamespacedKey(plugin, "particles/animations"));
    }

    @Override
    public void load(ExpansionPack pack, ZipFile zipFile, String root, ZipEntry entry, WolfyUtilCore core) {
        if (entry.isDirectory()) {
            //We can't read from a directory!
            return;
        }
        String path = entry.getName().replace(root + folderPath, "");
        path = path.substring(0, path.lastIndexOf(".")); // This the key of the effect.
        try (var stream = new BufferedInputStream(zipFile.getInputStream(entry))){
            var effect = JacksonUtil.getObjectMapper().readValue(stream, ParticleAnimation.class);
            if (effect != null) {
                core.getRegistries().getParticleAnimations().register(new NamespacedKey(pack.getMeta().getPack().getNamespace(), path), effect);
            }
        } catch (IOException e) {
            core.getLogger().info("Failed to load animation \"" + path + "\"! Cause:" + e.getMessage());
        }
    }
}
