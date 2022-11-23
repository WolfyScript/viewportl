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

package com.wolfyscript.utilities.collection;

import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementation of a weighted random collection.<br>
 * The chance of the values in the collection are based on their individual weight and total weight.<br>
 * <pre>valueChance = valueWeight / totalWeight</pre>
 *
 * @param <E> The type of the values
 */
public class RandomCollection<E> extends TreeMap<Double, E> {

    private double total = 0;
    private final Random random;

    public RandomCollection() {
        this.random = null;
    }

    /**
     * When using this constructor the specified Random will be used whenever an item is selected.
     *
     * @param random The random to use when selecting an item.
     */
    public RandomCollection(Random random) {
        this.random = random;
    }

    /**
     * Adds a new value with the specified weight to the collection.<br>
     *
     * @param weight The weight of the value.
     * @param result The value to store.
     * @return This collection to allow for chaining.
     */
    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) return this;
        total += weight;
        put(total, result);
        return this;
    }

    /**
     * Gets the next value in the map depending on the weight.
     *
     * @return The next random value or null if none can be found.
     */
    @Nullable
    public E next() {
        return next(random != null ? random : ThreadLocalRandom.current());
    }

    /**
     * Gets the next value in the map depending on the weight.
     *
     * @return The next random value or null if none can be found.
     */
    @Nullable
    public E next(Random random) {
        double value = random.nextDouble() * total;
        Map.Entry<Double, E> entry = ceilingEntry(value);
        return entry != null ? entry.getValue() : null;
    }

    public RandomCollection<E> addAll(RandomCollection<E> randomCollection) {
        putAll(randomCollection);
        return this;
    }
}
