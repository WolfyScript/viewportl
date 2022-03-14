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

import java.util.Random;

/**
 * Provides a linear increasing value by a specified increment.<br>
 */
public class TimerRandom extends Timer {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("random");

    private final long seed;
    private final double multiplier;

    public TimerRandom() {
        this(1, 1, 1);
    }

    public TimerRandom(long seed, double multiplier, double stopValue) {
        super(KEY);
        this.seed = seed;
        this.multiplier = multiplier;
        setStopValue(stopValue);
    }

    public long getSeed() {
        return seed;
    }

    @Override
    public void setStopValue(double stopValue) {
        Preconditions.checkArgument(stopValue >= 0, "Random time supplier cannot have negative stop value!");
        super.setStopValue(stopValue);
    }

    @Override
    public Timer.Runner createRunner() {
        return new Runner();
    }

    private class Runner extends Timer.Runner {

        private final Random random;
        private int counter = 0;

        private Runner() {
            this.random = seed != -1 ? new Random(seed) : new Random();
        }

        @Override
        public double increase() {
            time += random.nextDouble() * multiplier;
            counter++;
            return time;
        }

        @Override
        public boolean shouldStop() {
            return counter >= getStopValue();
        }
    }
}
