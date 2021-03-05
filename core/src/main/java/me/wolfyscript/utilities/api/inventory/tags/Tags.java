package me.wolfyscript.utilities.api.inventory.tags;

import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Tags<T extends Keyed> extends Registry.SimpleRegistry<CustomTag<T>> {

    Map<NamespacedKey, CustomTag<T>> tags;

    @Nullable
    public CustomTag<T> getTag(NamespacedKey namespacedKey) {
        return tags.get(namespacedKey);
    }

}
