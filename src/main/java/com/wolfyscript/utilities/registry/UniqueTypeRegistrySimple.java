package com.wolfyscript.utilities.registry;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.wolfyscript.utilities.Keyed;
import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.common.Registries;

public class UniqueTypeRegistrySimple<V extends Keyed> extends AbstractTypeRegistry<BiMap<NamespacedKey, Class<? extends V>>, V> {

    public UniqueTypeRegistrySimple(NamespacedKey key, Registries registries) {
        super(key, HashBiMap.create(), registries);
    }

    public NamespacedKey getKey(Class<? extends V> value) {
        return map.inverse().get(value);
    }

}
