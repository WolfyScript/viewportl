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
import me.wolfyscript.utilities.api.inventory.custom_items.CustomData;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.Meta;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.api.inventory.tags.Tags;
import me.wolfyscript.utilities.registry.Registries;
import me.wolfyscript.utilities.registry.RegistryCustomItem;
import me.wolfyscript.utilities.registry.RegistrySimple;
import me.wolfyscript.utilities.util.particles.ParticleAnimation;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Supplier;

/**
 *
 * @deprecated This interface has been replaced by {@link me.wolfyscript.utilities.registry.Registry}.<br>
 *             Instances of Registries can be accessed using {@link me.wolfyscript.utilities.registry.Registries}.<br>
 *             You can get that instance via {@link WolfyUtilities#getRegistries()}.
 * @param <V>
 */
@Deprecated
public interface Registry<V extends Keyed> extends Iterable<V> {

    /**
     * Gets a Registry by the type it contains.
     * The Registry has to be created with the class of the type (See: {@link WrapperRegistry#WrapperRegistry(Supplier, Class)}).
     *
     * @param type The class of the type the registry contains.
     * @param <V> The type the registry contains.
     * @return The registry of the specific type; or null if not available.
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    static <V extends Keyed> Registry<V> getByType(Class<V> type) {
        return (Registry<V>) SimpleRegistry.REGISTRIES_BY_TYPE.get(type);
    }

    /**
     * The Registry for all of the {@link CustomItem} instances.
     */
    CustomItemRegistry CUSTOM_ITEMS = new CustomItemRegistry();

    /**
     * Contains {@link CustomData.Provider} that can be used in any Custom Item from the point of registration.
     * <br>
     * You can register any CustomData you might want to add to your CustomItems and then save and load it from config too.
     * <br>
     * It allows you to save and load custom data into a CustomItem and makes things a lot easier if you have some items that perform specific actions with the data etc.
     * <br>
     * For example CustomCrafting registers it's own CustomData, that isn't in this core API, for it's Elite Workbenches that open up custom GUIs dependent on their CustomData.
     * And also the Recipe Book uses a CustomData object to store some data.
     */
    Registry<CustomData.Provider<?>> CUSTOM_ITEM_DATA = new WrapperRegistry<>(() -> WolfyUtilCore.getInstance().getRegistries().getCustomItemData());
    /**
     * @deprecated This registry has no longer any functionality. It was replaced by {@link Registries#getCustomItemNbtChecks()}
     */
    @Deprecated(forRemoval = true)
    MetaRegistry META_PROVIDER = new MetaRegistry();
    ParticleRegistry PARTICLE_EFFECTS = new ParticleRegistry();
    ParticleAnimationRegistry PARTICLE_ANIMATIONS = new ParticleAnimationRegistry();

    //Tags
    Tags<CustomItem> ITEM_TAGS = WolfyUtilCore.getInstance().getRegistries().getItemTags();

    /**
     * Get the value of the registry by it's {@link NamespacedKey}
     *
     * @param key The {@link NamespacedKey} of the value.
     * @return The value of the {@link NamespacedKey}.
     */
    @Nullable
    V get(@Nullable NamespacedKey key);

    /**
     * Register a value with a {@link NamespacedKey} to this registry.
     * You can't override values that are already registered under the same {@link NamespacedKey}!
     *
     * @param key   The {@link NamespacedKey} to register it to.
     * @param value The value to register.
     */
    void register(NamespacedKey key, V value);

    void register(V value);

    Set<NamespacedKey> keySet();

    Collection<V> values();

    Set<Entry<NamespacedKey, V>> entrySet();

    /**
     * A simple registry, used for basic use cases.
     *
     * @param <V> The type of the value.
     */
    @Deprecated
    class SimpleRegistry<V extends Keyed> implements Registry<V> {

        private static final Map<Class<? extends Keyed>, Registry<?>> REGISTRIES_BY_TYPE = new HashMap<>();

        protected final Map<NamespacedKey, V> map;
        private final Class<V> type;

        public SimpleRegistry() {
            this.map = new HashMap<>();
            this.type = null;
        }

        public SimpleRegistry(Class<V> type) {
            this.map = new HashMap<>();
            this.type = type;
            REGISTRIES_BY_TYPE.put(type, this);
        }

        private boolean isTypeOf(Class<?> type) {
            return this.type != null && this.type.equals(type);
        }

        @Override
        public @Nullable V get(@Nullable NamespacedKey key) {
            return map.get(key);
        }

        @Override
        public void register(NamespacedKey namespacedKey, V value) {
            if (value != null) {
                Preconditions.checkState(!this.map.containsKey(namespacedKey), "namespaced key '%s' already has an associated value!", namespacedKey);
                map.put(namespacedKey, value);
            }
        }

        @Override
        public void register(V value) {
            register(value.getNamespacedKey(), value);
        }

        @NotNull
        @Override
        public Iterator<V> iterator() {
            return map.values().iterator();
        }

        @Override
        public Set<NamespacedKey> keySet() {
            return Collections.unmodifiableSet(this.map.keySet());
        }

        @Override
        public Collection<V> values() {
            System.out.println("Get values from simple Reg: " + this);
            return Collections.unmodifiableCollection(this.map.values());
        }

        @Override
        public Set<Entry<NamespacedKey, V>> entrySet() {
            return Collections.unmodifiableSet(this.map.entrySet());
        }
    }

    /**
     * Used for backwards compatibility. It wraps around the new registries and acts as a layer in between.
     *
     * @param <V> The type of the value.
     */
    @Deprecated
    class WrapperRegistry<V extends Keyed> implements Registry<V> {

        protected final Supplier<me.wolfyscript.utilities.registry.Registry<V>> wrappedRegistrySupplier;
        private me.wolfyscript.utilities.registry.Registry<V> wrappedRegistry;

        public WrapperRegistry(Supplier<me.wolfyscript.utilities.registry.Registry<V>> registrySupplier) {
            this.wrappedRegistrySupplier = registrySupplier;
            this.wrappedRegistry = registrySupplier.get();
        }

        public WrapperRegistry(Supplier<me.wolfyscript.utilities.registry.Registry<V>> registrySupplier, Class<V> type) {
            this.wrappedRegistrySupplier = registrySupplier;
            this.wrappedRegistry = registrySupplier.get();
            SimpleRegistry.REGISTRIES_BY_TYPE.put(type, this);
        }

        protected me.wolfyscript.utilities.registry.Registry<V> getWrappedRegistry() {
            if (wrappedRegistry == null) {
                this.wrappedRegistry = wrappedRegistrySupplier.get();
            }
            return wrappedRegistry;
        }

        public Class<V> getType() {
            return getWrappedRegistry() instanceof RegistrySimple<V> wrappedRegistrySimple ? wrappedRegistrySimple.getType() : null;
        }

        @Override
        public @Nullable V get(@Nullable NamespacedKey key) {
            return getWrappedRegistry().get(key);
        }

        @Override
        public void register(NamespacedKey namespacedKey, V value) {
            getWrappedRegistry().register(namespacedKey, value);
        }

        @Override
        public void register(V value) {
            getWrappedRegistry().register(value);
        }

        @NotNull
        @Override
        public Iterator<V> iterator() {
            return getWrappedRegistry().iterator();
        }

        @Override
        public Set<NamespacedKey> keySet() {
            return getWrappedRegistry().keySet();
        }

        @Override
        public Collection<V> values() {
            return getWrappedRegistry().values();
        }

        @Override
        public Set<Entry<NamespacedKey, V>> entrySet() {
            return getWrappedRegistry().entrySet();
        }
    }

    @Deprecated
    class CustomItemRegistry extends WrapperRegistry<CustomItem> {

        public CustomItemRegistry() {
            super(() -> WolfyUtilCore.getInstance().getRegistries().getCustomItems(), CustomItem.class);
        }

        @Deprecated
        public List<String> getNamespaces() {
            return ((RegistryCustomItem) getWrappedRegistry()).getNamespaces();
        }

        /**
         * Get all the items of the specific namespace.
         *
         * @param namespace the namespace you want to get the items from
         * @return A list of all the items of the specific namespace
         */
        @Deprecated
        public List<CustomItem> get(String namespace) {
            return ((RegistryCustomItem) getWrappedRegistry()).get(namespace);
        }

        /**
         * @param namespacedKey NamespacedKey of the item
         * @return true if there is an CustomItem for the NamespacedKey
         */
        @Deprecated
        public boolean has(NamespacedKey namespacedKey) {
            return ((RegistryCustomItem) getWrappedRegistry()).has(namespacedKey);
        }

        /**
         * Removes the CustomItem from the registry.
         * However, this won't delete the config if one exists!
         * If a config exists the item will be reloaded on the next restart.
         *
         * @param namespacedKey The NamespacedKey of the CustomItem
         */
        @Deprecated
        public void remove(NamespacedKey namespacedKey) {
            ((RegistryCustomItem) getWrappedRegistry()).remove(namespacedKey);
        }

        /**
         * Add a CustomItem to the registry or update a existing one and sets the NamespacedKey in the CustomItem object.
         * <br>
         * If the registry already contains a value for the NamespacedKey then the value will be updated with the new one.
         * <br>
         * <b>
         * If the CustomItem is linked with a {@link WolfyUtilitiesRef}, which NamespacedKey is the same as the passed in NamespacedKey, the CustomItem will neither be added or updated!
         * <br>
         * This is to prevent a infinite loop where a reference tries to call itself when it tries to get the values from it's parent item.
         * </b>
         *
         * @param namespacedKey The NamespacedKey the CustomItem will be saved under.
         * @param item          The CustomItem to add or update.
         */
        @Deprecated
        @Override
        public void register(NamespacedKey namespacedKey, CustomItem item) {
            getWrappedRegistry().register(namespacedKey, item);
        }
    }

    /**
     * This no longer serves any purpose. If you try to register something it won't do anything. Getter methods will always return null!
     */
    @Deprecated(forRemoval = true)
    class MetaRegistry implements Registry<Meta.Provider<?>> {

        @Override
        public @Nullable Meta.Provider<?> get(@Nullable NamespacedKey key) {
            return null;
        }

        @Override
        public void register(NamespacedKey key, Meta.Provider<?> value) {}

        @Override
        public void register(Meta.Provider<?> value) {}

        @Override
        public Set<NamespacedKey> keySet() {
            return Collections.emptySet();
        }

        @Override
        public Collection<Meta.Provider<?>> values() {
            return Collections.emptyList();
        }

        @Override
        public Set<Entry<NamespacedKey, Meta.Provider<?>>> entrySet() {
            return Collections.emptySet();
        }

        @NotNull
        @Override
        public Iterator<Meta.Provider<?>> iterator() {
            return Collections.emptyIterator();
        }
    }

    @Deprecated
    class ParticleAnimationRegistry extends WrapperRegistry<ParticleAnimation> {

        public ParticleAnimationRegistry() {
            super(() -> WolfyUtilCore.getInstance().getRegistries().getParticleAnimations(), ParticleAnimation.class);
        }

    }

    @Deprecated
    class ParticleRegistry extends WrapperRegistry<ParticleEffect> {

        public ParticleRegistry() {
            super(() -> WolfyUtilCore.getInstance().getRegistries().getParticleEffects(), ParticleEffect.class);
        }

    }

}
