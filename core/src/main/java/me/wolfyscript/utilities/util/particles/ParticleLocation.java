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

package me.wolfyscript.utilities.util.particles;

import me.wolfyscript.utilities.api.inventory.custom_items.ParticleContent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Represents the location at which an Animation/Effect can be spawned at.
 */
public enum ParticleLocation {

    LOCATION(null),
    ENTITY(null),
    BLOCK(null),
    PLAYER(null),
    @Deprecated HAND(ParticleContent.PlayerSettings::setMainHand),
    @Deprecated OFF_HAND(ParticleContent.PlayerSettings::setOffHand),
    @Deprecated FEET(ParticleContent.PlayerSettings::setFeet),
    @Deprecated LEGS(ParticleContent.PlayerSettings::setLegs),
    @Deprecated CHEST(ParticleContent.PlayerSettings::setChest),
    @Deprecated HEAD(ParticleContent.PlayerSettings::setHead);

    final BiConsumer<ParticleContent.PlayerSettings, ParticleAnimation> applyOldPlayerAnimation;

    ParticleLocation(@Nullable BiConsumer<ParticleContent.PlayerSettings, ParticleAnimation> applyOldPlayerAnimation) {
        this.applyOldPlayerAnimation = applyOldPlayerAnimation;
    }

    public void applyOldPlayerAnimation(ParticleContent.PlayerSettings playerSettings, ParticleAnimation animation) {
        if (applyOldPlayerAnimation != null) {
           applyOldPlayerAnimation.accept(playerSettings, animation);
        }
    }

    public boolean isDeprecated() {
        return applyOldPlayerAnimation != null;
    }
}
