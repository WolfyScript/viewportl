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

package me.wolfyscript.utilities.util;

import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.Meta;
import me.wolfyscript.utilities.registry.TypeRegistry;
import me.wolfyscript.utilities.util.particles.animators.Animator;
import me.wolfyscript.utilities.util.particles.timer.Timer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

/**
 * This registry allows you to register classes under NamespacedKeys. <br>
 * It is similar to the {@link Registry}, with the difference that it stores the classes of the type. <br>
 * To get a new instance of an entry you must use {@link #create(NamespacedKey)} or create it manually. <br>
 * <p>
 * Main use case of this registry would be to prevent using the {@link Registry} with default objects <br>
 * and prevents unwanted usage of those values, as this registry enforces to create new instances.
 *
 * @deprecated This interface has been replaced by {@link TypeRegistry}.<br>
 *             Instances of Registries can be accessed using {@link me.wolfyscript.utilities.registry.Registries}.<br>
 *             You can get that instance via {@link WolfyUtilities#getRegistries()}.
 *
 * @param <V> The type of the values.
 */
@Deprecated
public interface ClassRegistry<V extends Keyed> extends Iterable<Class<? extends V>> {

    SimpleClassRegistry<Animator> PARTICLE_ANIMATORS = new ClassRegistry.WrapperClassRegistry<>(() -> WolfyUtilCore.getInstance().getRegistries().getParticleAnimators());
    SimpleClassRegistry<Timer> PARTICLE_TIMER = new ClassRegistry.WrapperClassRegistry<>(() -> WolfyUtilCore.getInstance().getRegistries().getParticleTimer());
    SimpleClassRegistry<Meta> NBT_CHECKS = new ClassRegistry.WrapperClassRegistry<>(() -> WolfyUtilCore.getInstance().getRegistries().getCustomItemNbtChecks());

    /**
     * Get the value of the registry by it's {@link NamespacedKey}
     *
     * @param key The {@link NamespacedKey} of the value.
     * @return The value of the {@link NamespacedKey}.
     */
    @Nullable
    Class<? extends V> get(@Nullable NamespacedKey key);

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
    void register(NamespacedKey key, Class<? extends V> value);

    /**
     * Registers the class of the object to the {@link NamespacedKey} of the object. <br>
     * Values that are already registered to the same {@link NamespacedKey} cannot be overridden!
     *
     * @param value The object to register the class from.
     */
    void register(V value);

    Set<NamespacedKey> keySet();

    Collection<Class<? extends V>> values();

    Set<Entry<NamespacedKey, Class<? extends V>>> entrySet();

    /**
     * A simple registry, used for basic use cases.
     *
     * @param <V> The type of the value.
     */
    @Deprecated
    class SimpleClassRegistry<V extends Keyed> implements ClassRegistry<V> {

        protected final Map<NamespacedKey, Class<? extends V>> map;

        public SimpleClassRegistry() {
            this.map = new HashMap<>();
        }

        @Override
        public @Nullable Class<? extends V> get(@Nullable NamespacedKey key) {
            return map.get(key);
        }

        @Override
        public @Nullable V create(NamespacedKey key) {
            Class<? extends V> clazz = get(key);
            if (clazz != null) {
                try {
                    return clazz.getDeclaredConstructor().newInstance();
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
        public void register(NamespacedKey key, Class<? extends V> value) {
            if (value != null) {
                Objects.requireNonNull(key, "Can't register value " + value.getName() + " because key is null!");
                Preconditions.checkState(!this.map.containsKey(key), "namespaced key '%s' already has an associated value!", key);
                map.put(key, value);
            }
        }

        @Override
        public void register(V value) {
            register(value.getNamespacedKey(), (Class<? extends V>) value.getClass());
        }

        @NotNull
        @Override
        public Iterator<Class<? extends V>> iterator() {
            return map.values().iterator();
        }

        @Override
        public Set<NamespacedKey> keySet() {
            return Collections.unmodifiableSet(this.map.keySet());
        }

        @Override
        public Collection<Class<? extends V>> values() {
            return Collections.unmodifiableCollection(this.map.values());
        }

        @Override
        public Set<Entry<NamespacedKey, Class<? extends V>>> entrySet() {
            return Collections.unmodifiableSet(this.map.entrySet());
        }
    }

    @Deprecated
    class WrapperClassRegistry<V extends Keyed> extends SimpleClassRegistry<V> {

        protected final Supplier<TypeRegistry<V>> wrappedRegistrySupplier;
        private TypeRegistry<V> wrappedRegistry;

        public WrapperClassRegistry(Supplier<TypeRegistry<V>> wrappedRegistry) {
            this.wrappedRegistrySupplier = wrappedRegistry;
            this.wrappedRegistry = wrappedRegistry.get();
        }

        protected TypeRegistry<V> getWrappedRegistry() {
            if (wrappedRegistry == null) {
                this.wrappedRegistry = wrappedRegistrySupplier.get();
            }
            return wrappedRegistry;
        }

        @Override
        public @Nullable Class<? extends V> get(@Nullable NamespacedKey key) {
            return getWrappedRegistry().get(key);
        }

        @Override
        public @Nullable V create(NamespacedKey key) {
            return getWrappedRegistry().create(key);
        }

        @Override
        public void register(NamespacedKey key, V value) {
            getWrappedRegistry().register(key, value);
        }

        @Override
        public void register(NamespacedKey key, Class<? extends V> value) {
            getWrappedRegistry().register(key, value);
        }

        @Override
        public void register(V value) {
            getWrappedRegistry().register(value);
        }

        @NotNull
        @Override
        public Iterator<Class<? extends V>> iterator() {
            return getWrappedRegistry().iterator();
        }

        @Override
        public Set<NamespacedKey> keySet() {
            return getWrappedRegistry().keySet();
        }

        @Override
        public Collection<Class<? extends V>> values() {
            return getWrappedRegistry().values();
        }

        @Override
        public Set<Entry<NamespacedKey, Class<? extends V>>> entrySet() {
            return getWrappedRegistry().entrySet();
        }
    }

}
