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

package com.wolfyscript.utilities.common.adapters;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Entity {

    @NotNull
    Location getLocation();

    @Contract("null -> null; !null -> !null")
    @Nullable
    Location getLocation(Location loc);

    /**
     * Sets this entity's velocity in meters per tick
     *
     * @param velocity New velocity to travel with
     */
    void setVelocity(@NotNull Vector3D velocity);

    /**
     * Gets this entity's current velocity
     *
     * @return Current traveling velocity of this entity
     */
    @NotNull Vector3D getVelocity();

    /**
     * Gets the entity's height
     *
     * @return height of entity
     */
    double getHeight();

    /**
     * Gets the entity's width
     *
     * @return width of entity
     */
    double getWidth();

    /**
     * Returns true if the entity is supported by a block. This value is a
     * state updated by the server and is not recalculated unless the entity
     * moves.
     *
     * @return True if entity is on ground.
     * @see Player#isOnGround()
     */
    boolean isOnGround();

    /**
     * Returns true if the entity is in water.
     *
     * @return <code>true</code> if the entity is in water.
     */
    boolean isInWater();

    /**
     * Gets the current world this entity resides in
     *
     * @return World
     */
    @NotNull
    World getWorld();

    /**
     * Sets the entity's rotation.
     * <p>
     * Note that if the entity is affected by AI, it may override this rotation.
     *
     * @param yaw the yaw
     * @param pitch the pitch
     * @throws UnsupportedOperationException if used for players
     */
    void setRotation(float yaw, float pitch);


}
