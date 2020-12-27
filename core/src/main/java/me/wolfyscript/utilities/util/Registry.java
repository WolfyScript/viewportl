package me.wolfyscript.utilities.util;

import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.api.inventory.custom_items.references.WolfyUtilitiesRef;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public interface Registry<V> extends Iterable<V> {

    Registry<CustomItem> CUSTOM_ITEMS = new CustomItemRegistry();

    @Nullable
    V get(@Nullable NamespacedKey key);

    void register(NamespacedKey key, V value);

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
            Preconditions.checkState(!this.map.containsKey(namespacedKey), "namespaced key '%s' already has an associated value!", namespacedKey);
            map.put(namespacedKey, value);
        }

        @NotNull
        @Override
        public Iterator<V> iterator() {
            return map.values().iterator();
        }

        public Set<NamespacedKey> keySet() {
            return Collections.unmodifiableSet(this.map.keySet());
        }

        public Collection<V> values() {
            return Collections.unmodifiableCollection(this.map.values());
        }

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
         * @return If the CustomItem was added or updated. True if it was successful.
         */
        @Override
        public void register(NamespacedKey namespacedKey, CustomItem item) {
            if (item == null || (item.getApiReference() instanceof WolfyUtilitiesRef && ((WolfyUtilitiesRef) item.getApiReference()).getNamespacedKey().equals(namespacedKey))) {
                return;
            }
            item.setNamespacedKey(namespacedKey);
            super.register(namespacedKey, item);
        }

    }

}
