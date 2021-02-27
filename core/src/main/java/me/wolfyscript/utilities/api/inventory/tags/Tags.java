package me.wolfyscript.utilities.api.inventory.tags;

import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class Tags<T extends Keyed> {

    Map<NamespacedKey, CustomTag<T>> tags;

    @Nullable
    public CustomTag<T> getTag(NamespacedKey namespacedKey) {
        return tags.get(namespacedKey);
    }

}
