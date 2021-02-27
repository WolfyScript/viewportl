package me.wolfyscript.utilities.util;

import java.util.NavigableMap;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.stream.Collector;

public class RandomCollection<E> {

    private final NavigableMap<Double, E> map = new TreeMap<Double, E>();
    private final Random random;
    private double total = 0;

    public RandomCollection() {
        this(new Random());
    }

    public RandomCollection(Random random) {
        this.random = random;
    }

    public RandomCollection<E> add(double weight, E result) {
        if (weight <= 0) return this;
        total += weight;
        map.put(total, result);
        return this;
    }

    public static <T> Collector<T, RandomCollection<T>, RandomCollection<T>> getCollector(BiConsumer<RandomCollection<T>, T> accumulator) {
        return Collector.of(RandomCollection::new, accumulator, RandomCollection::addAll);
    }

    public E next() {
        double value = random.nextDouble() * total;
        return map.higherEntry(value).getValue();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public int size() {
        return map.size();
    }

    public RandomCollection<E> addAll(RandomCollection<E> randomCollection) {
        map.putAll(randomCollection.map);
        return this;
    }
}
