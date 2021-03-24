package me.wolfyscript.utilities.util;

import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;

import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.stream.Collector;

public class RandomCollection<E> extends TreeMap<Double, E> {

    private final Random random;
    private double total = 0;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public static Collector<CustomItem, RandomCollection<CustomItem>, RandomCollection<CustomItem>> getCustomItemCollector() {
        return Collector.of(RandomCollection::new, (rdmCollection, customItem) -> rdmCollection.add(customItem.getRarityPercentage(), customItem.clone()), RandomCollection::addAll);
    }

    public static <T> Collector<T, RandomCollection<T>, RandomCollection<T>> getCollector(BiConsumer<RandomCollection<T>, T> accumulator) {
        return Collector.of(RandomCollection::new, accumulator, RandomCollection::addAll);
    }

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
    public E next() {
        double value = random.nextDouble() * total;
        Map.Entry<Double, E> entry = higherEntry(value);
        return entry == null ? null : entry.getValue();
    }

    public RandomCollection<E> addAll(RandomCollection<E> randomCollection) {
        putAll(randomCollection);
        return this;
    }
}
