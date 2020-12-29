package me.wolfyscript.utilities.util;

import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import me.wolfyscript.utilities.util.particles.ParticleAnimation;
import me.wolfyscript.utilities.util.particles.ParticleEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public interface Registry<V> extends Iterable<V> {

    //WolfyUtilities Registries
    CustomItemRegistry CUSTOM_ITEMS = new CustomItemRegistry();
    ParticleRegistry PARTICLE_EFFECTS = new ParticleRegistry();
    ParticleAnimationRegistry PARTICLE_ANIMATIONS = new ParticleAnimationRegistry();

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

    Set<NamespacedKey> keySet();

    Collection<V> values();

    Set<Entry<NamespacedKey, V>> entrySet();

    /**
     * A simple registry, used for basic use cases.
     *
     * @param <V> The type of the value.
     */
    class SimpleRegistry<V> implements Registry<V> {

        protected final Map<NamespacedKey, V> map;

        public SimpleRegistry() {
            this.map = new HashMap<>();
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
            return Collections.unmodifiableCollection(this.map.values());
        }

        @Override
        public Set<Entry<NamespacedKey, V>> entrySet() {
            return Collections.unmodifiableSet(this.map.entrySet());
        }
    }

    class CustomItemRegistry extends SimpleRegistry<CustomItem> {

        public List<String> getNamespaces() {
            return this.map.keySet().stream().map(NamespacedKey::getNamespace).distinct().collect(Collectors.toList());
        }

        /**
         * Get all the items of the specific namespace.
         *
         * @param namespace the namespace you want to get the items from
         * @return A list of all the items of the specific namespace
         */
        public List<CustomItem> get(String namespace) {
            return this.map.entrySet().stream().filter(entry -> entry.getKey().getNamespace().equals(namespace)).map(Map.Entry::getValue).collect(Collectors.toList());
        }

        /**
         * @param namespacedKey NamespacedKey of the item
         * @return true if there is an CustomItem for the NamespacedKey
         */
        public boolean has(NamespacedKey namespacedKey) {
            return this.map.containsKey(namespacedKey);
        }

        /**
         * Removes the CustomItem from the registry.
         * However, this won't delete the config if one exists!
         * If a config exists the item will be reloaded on the next restart.
         *
         * @param namespacedKey The NamespacedKey of the CustomItem
         */
        public void remove(NamespacedKey namespacedKey) {
            this.map.remove(namespacedKey);
        }

        /**
         * Add a CustomItem to the registry or update a existing one and sets the NamespacedKey in the CustomItem object.
         * <br/>
         * If the registry already contains a value for the NamespacedKey then the value will be updated with the new one.
         * <br/>
         * <b>
         * If the CustomItem is linked with a {@link WolfyUtilitiesRef}, which NamespacedKey is the same as the passed in NamespacedKey, the CustomItem will neither be added or updated!
         * <br/>
         * This is to prevent a infinite loop where a reference tries to call itself when it tries to get the values from it's parent item.
         * <b/>
         *
         * @param namespacedKey The NamespacedKey the CustomItem will be saved under.
         * @param item          The CustomItem to add or update.
         */
        @Override
        public void register(NamespacedKey namespacedKey, CustomItem item) {
            if (item == null || (item.getApiReference() instanceof WolfyUtilitiesRef && ((WolfyUtilitiesRef) item.getApiReference()).getNamespacedKey().equals(namespacedKey))) {
                return;
            }
            item.setNamespacedKey(namespacedKey);
            this.map.put(namespacedKey, item);
        }
    }

    class ParticleRegistry extends SimpleRegistry<ParticleEffect> {

        @Override
        public void register(NamespacedKey namespacedKey, ParticleEffect value) {
            if (value != null) {
                value.setNamespacedKey(namespacedKey);
                super.register(namespacedKey, value);
            }
        }
    }

    class ParticleAnimationRegistry extends SimpleRegistry<ParticleAnimation> {

        @Override
        public void register(NamespacedKey namespacedKey, ParticleAnimation value) {
            if (value != null) {
                value.setNamespacedKey(namespacedKey);
                super.register(namespacedKey, value);
            }
        }
    }

}
