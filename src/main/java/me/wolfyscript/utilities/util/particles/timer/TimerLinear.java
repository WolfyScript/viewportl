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

import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.util.NamespacedKey;

/**
 * Provides a linear increasing value by a specified increment.<br>
 */
public class TimerLinear extends Timer {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("linear");

    private double increment;

    public TimerLinear() {
        this(1, 1);
    }

    public TimerLinear(double increment, double stopValue) {
        super(KEY);
        setIncrement(increment);
        setStopValue(stopValue);
    }

    public double getIncrement() {
        return increment;
    }

    public void setIncrement(double increment) {
        Preconditions.checkArgument(increment != 0, "Cannot divide by 0! Must be less or bigger than 0!");
        this.increment = increment;
    }

    @Override
    public Timer.Runner createRunner() {
        return new Runner();
    }

    private class Runner extends Timer.Runner {

        @Override
        public double increase() {
            time += increment;
            return time;
        }

        @Override
        public boolean shouldStop() {
            if (increment > 0) {
                return time > getStopValue();
            }
            return time < getStopValue();
        }
    }
}
