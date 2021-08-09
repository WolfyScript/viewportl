package me.wolfyscript.utilities.util;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;

/**
 * This registry allows you to register classes under NamespacedKeys. <br>
 * It is similar to the {@link Registry}, with the difference that it stores the classes of the type. <br>
 * To get a new instance of an entry you must use {@link #create(NamespacedKey)} or create it manually. <br>
 * <p>
 * Main use case of this registry would be to prevent using the {@link Registry} with default objects <br>
 * and prevents mistakenly usage of those values, as this registry enforces to create new instances.
 *
 * @param <V> The type of the values.
 */
public interface ClassRegistry<V extends Keyed> extends Iterable<Class<V>> {

    /**
     * Get the value of the registry by it's {@link NamespacedKey}
     *
     * @param key The {@link NamespacedKey} of the value.
     * @return The value of the {@link NamespacedKey}.
     */
    @Nullable
    Class<V> get(@Nullable NamespacedKey key);

    /**
     * This method creates a new instance of the specific class, if it is available. <br>
     * If class for the key is not found it will return null. <br>
     * Default implementation looks for the default constructor.
     *
     * @param key The {@link NamespacedKey} of the value.
     * @return A new instance of the class.
     */
    @Nullable
    V create(NamespacedKey key);

    /**
     * Registers the class of the object to the specified {@link NamespacedKey}. <br>
     * Values that are already registered to the same {@link NamespacedKey} cannot be overridden!
     *
     * @param key   The {@link NamespacedKey} to register it to.
     * @param value The object to register the class from.
     */
    void register(NamespacedKey key, V value);

    /**
     * Registers the value to the specified {@link NamespacedKey}. <br>
     * Values that are already registered to the same {@link NamespacedKey} cannot be overridden!
     *
     * @param key   The {@link NamespacedKey} to register it to.
     * @param value The value to register.
     */
    void register(NamespacedKey key, Class<V> value);

    /**
     * Registers the class of the object to the {@link NamespacedKey} of the object. <br>
     * Values that are already registered to the same {@link NamespacedKey} cannot be overridden!
     *
     * @param value The object to register the class from.
     */
    void register(V value);

    Set<NamespacedKey> keySet();

    Collection<Class<V>> values();

    Set<Entry<NamespacedKey, Class<V>>> entrySet();

    /**
     * A simple registry, used for basic use cases.
     *
     * @param <V> The type of the value.
     */
    class SimpleClassRegistry<V extends Keyed> implements ClassRegistry<V> {

        protected final Map<NamespacedKey, Class<V>> map;

        public SimpleClassRegistry() {
            this.map = new HashMap<>();
        }

        @Override
        public @Nullable Class<V> get(@Nullable NamespacedKey key) {
            return map.get(key);
        }

        @Override
        public @Nullable V create(NamespacedKey key) {
            Class<V> clazz = get(key);
            if (clazz != null) {
                try {
                    clazz.getDeclaredConstructor().newInstance();
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        public void register(NamespacedKey key, V value) {
            register(key, (Class<V>) value.getClass());
        }

        @Override
        public void register(NamespacedKey key, Class<V> value) {
            if (value != null) {
                Objects.requireNonNull(key, "Can't register value " + value.getName() + " because key is null!");
                Preconditions.checkState(!this.map.containsKey(key), "namespaced key '%s' already has an associated value!", key);
                map.put(key, value);
            }
        }

        @Override
        public void register(V value) {
            register(value.getNamespacedKey(), (Class<V>) value.getClass());
        }

        @NotNull
        @Override
        public Iterator<Class<V>> iterator() {
            return map.values().iterator();
        }

        @Override
        public Set<NamespacedKey> keySet() {
            return Collections.unmodifiableSet(this.map.keySet());
        }

        @Override
        public Collection<Class<V>> values() {
            return Collections.unmodifiableCollection(this.map.values());
        }

        @Override
        public Set<Entry<NamespacedKey, Class<V>>> entrySet() {
            return Collections.unmodifiableSet(this.map.entrySet());
        }
    }

}
