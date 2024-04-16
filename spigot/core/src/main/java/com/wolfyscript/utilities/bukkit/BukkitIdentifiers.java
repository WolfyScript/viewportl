package com.wolfyscript.utilities.bukkit;

import com.wolfyscript.utilities.NamespacedKey;
import com.wolfyscript.utilities.Identifiers;
import com.wolfyscript.utilities.WolfyUtils;
import java.util.Locale;

public class BukkitIdentifiers implements Identifiers {

    private final WolfyUtils wolfyUtils;

    BukkitIdentifiers(WolfyUtils wolfyUtils) {
        this.wolfyUtils = wolfyUtils;
    }

    @Override
    public NamespacedKey getNamespaced(String value) {
        return BukkitNamespacedKey.of(value);
    }

    @Override
    public NamespacedKey getNamespaced(String namespace, String key) {
        return new BukkitNamespacedKey(namespace, key);
    }

    @Override
    public NamespacedKey getSelfNamespaced(String key) {
        return getNamespaced(wolfyUtils.getName().toLowerCase(Locale.ROOT).replace(' ', '_'), key);
    }

    @Override
    public NamespacedKey getWolfyUtilsNamespaced(String key) {
        return BukkitNamespacedKey.wolfyutilties(key);
    }
}
