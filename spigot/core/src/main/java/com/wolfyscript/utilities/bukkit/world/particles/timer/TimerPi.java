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

package com.wolfyscript.utilities.bukkit.world.particles.timer;

import com.google.common.base.Preconditions;
import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;

/**
 * Provides an increasing value by a fraction of PI.<br>
 * Formula: <br>
 * <code>t += Math.PI / fraction</code>
 */
public class TimerPi extends Timer {

    public static final BukkitNamespacedKey KEY = BukkitNamespacedKey.wolfyutilties("pi_fraction");

    private double fraction;

    public TimerPi() {
        this(1, Math.PI);
    }

    public TimerPi(double fraction, double stopValue) {
        super(KEY);
        this.fraction = fraction;
        this.stopValue = stopValue;
    }

    public double getFraction() {
        return fraction;
    }

    public void setFraction(double fraction) {
        Preconditions.checkArgument(fraction != 0, "Cannot divide by 0! Must be less or bigger than 0!");
        Preconditions.checkArgument(fraction > 0 ? (stopValue > 0) : (stopValue < 0), "Invalid fraction! A fraction of " + fraction + " and a stop value of " + stopValue + " will cause it to increase/decrease indefinitely!");
        this.fraction = fraction;
    }

    @Override
    public void setStopValue(double stopValue) {
        Preconditions.checkArgument(fraction > 0 ? (stopValue > 0) : (stopValue < 0), "Invalid stop time! Value of " + stopValue + " will cause it to increase/decrease indefinitely!");
        super.setStopValue(stopValue);
    }

    @Override
    public Timer.Runner createRunner() {
        return new Runner();
    }

    private class Runner extends Timer.Runner {

        @Override
        public double increase() {
            time += Math.PI / fraction;
            return time;
        }

        @Override
        public boolean shouldStop() {
            if (fraction > 0) {
                return time > getStopValue();
            }
            return time < getStopValue();
        }
    }
}
