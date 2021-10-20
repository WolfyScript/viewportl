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

package me.wolfyscript.utilities.util.particles.timer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeIdResolver;
import me.wolfyscript.utilities.util.json.jackson.KeyedTypeResolver;

@JsonTypeResolver(KeyedTypeResolver.class)
@JsonTypeIdResolver(KeyedTypeIdResolver.class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "key")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder(value = {"key", "startValue", "stopValue"})
public abstract class Timer implements Keyed {

    private final NamespacedKey key;
    protected double startValue;
    protected double stopValue;

    protected Timer(NamespacedKey namespacedKey) {
        this.key = namespacedKey;
    }

    /**
     * The start value is the initial time value of the supplier.
     * Default value is 0.0.
     *
     * @return The initial time.
     */
    public double getStartValue() {
        return startValue;
    }

    public void setStartValue(double startValue) {
        this.startValue = startValue;
    }

    /**
     * The value at which point the supplier will stop increasing.
     *
     * @return The value at which the supplier stops.
     */
    public double getStopValue() {
        return stopValue;
    }

    public void setStopValue(double stopValue) {
        this.stopValue = stopValue;
    }

    public abstract Runner createRunner();

    @JsonIgnore
    @Override
    public NamespacedKey getNamespacedKey() {
        return key;
    }

    /**
     * This object contains the actual state of the particle effect.
     * Each time an effect is spawned a new Runner is created with the specified start time.
     *
     */
    public abstract class Runner {

        protected double time;

        protected Runner() {
            this.time = getStartValue();
        }

        /**
         * Increases the time of the runner by the specified increment.
         *
         * @return The increased time of the runner.
         */
        public abstract double increase();

        public boolean shouldStop() {
            return time > getStopValue();
        }

    }

}
