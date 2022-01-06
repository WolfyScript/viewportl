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

package me.wolfyscript.utilities.main.resource_loader;

import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.expansions.ExpansionPack;
import me.wolfyscript.utilities.expansions.ResourceLoader;
import me.wolfyscript.utilities.expansions.ResourceLoaderJson;
import me.wolfyscript.utilities.main.WUPlugin;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import me.wolfyscript.utilities.util.particles.ParticleEffect;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ResourceLoaderParticleEffects extends ResourceLoaderJson<ParticleEffect> {

    public ResourceLoaderParticleEffects(WUPlugin plugin) {
        super(plugin, new NamespacedKey(plugin, "particles/effects"), ParticleEffect.class);
    }

    @Override
    public void register(NamespacedKey namespacedKey, ParticleEffect value, WolfyUtilCore core) {
        core.getRegistries().getParticleEffects().register(namespacedKey, value);
    }
}
