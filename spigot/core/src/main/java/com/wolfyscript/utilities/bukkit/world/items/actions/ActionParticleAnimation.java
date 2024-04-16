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

package com.wolfyscript.utilities.bukkit.world.items.actions;

import com.fasterxml.jackson.annotation.JacksonInject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.WolfyUtilsBukkit;
import com.wolfyscript.utilities.bukkit.world.particles.ParticleAnimation;
import com.wolfyscript.utilities.WolfyUtils;

public class ActionParticleAnimation extends Action<DataLocation> {

    public static final BukkitNamespacedKey KEY = BukkitNamespacedKey.wolfyutilties("location/particle_animation");

    private ParticleAnimation animation;

    @JsonCreator
    protected ActionParticleAnimation(@JacksonInject WolfyUtils wolfyUtils) {
        super(wolfyUtils, KEY, DataLocation.class);
    }

    @Override
    public void execute(WolfyUtils core, DataLocation data) {
        if (data instanceof DataPlayer dataPlayer) {
            animation.spawn(dataPlayer.getPlayer());
            return;
        }
        animation.spawn(data.getLocation());
    }

    public void setAnimation(ParticleAnimation animation) {
        this.animation = animation;
    }
}
