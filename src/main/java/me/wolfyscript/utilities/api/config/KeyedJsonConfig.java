package me.wolfyscript.utilities.api.config;

import me.wolfyscript.utilities.api.utils.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * This config is plain for utility and might change significantly in future updates.
 * This an it's counterpart {@link JsonConfig} will be changed in the upcoming updates and are planned to be somewhat stable/done in version 1.6.3.x.
 *
 * @param <T> any type you want to save in/load from the config file. See the Jackson documentation for more information about how to use custom serializer.
 */
public class KeyedJsonConfig<T> extends JsonConfig<T> {

    private final NamespacedKey namespacedKey;

    public KeyedJsonConfig(NamespacedKey namespacedKey, @NotNull File file, Function<File, T> rootFunction) {
        super(file, rootFunction);
        this.namespacedKey = namespacedKey;
    }

    public KeyedJsonConfig(NamespacedKey namespacedKey, @Nullable File file, Supplier<T> rootSupplier) {
        super(file, rootSupplier);
        this.namespacedKey = namespacedKey;
    }

    public KeyedJsonConfig(NamespacedKey namespacedKey, @NotNull File file, Class<T> type) {
        super(file, type);
        this.namespacedKey = namespacedKey;
    }

    public KeyedJsonConfig(NamespacedKey namespacedKey, @Nullable File file, Class<T> type, @NotNull String initialValue) {
        super(file, type, initialValue);
        this.namespacedKey = namespacedKey;
    }

    public KeyedJsonConfig(NamespacedKey namespacedKey, Class<T> type, @NotNull String initialValue) {
        super(type, initialValue);
        this.namespacedKey = namespacedKey;
    }

    public KeyedJsonConfig(NamespacedKey namespacedKey, @NotNull T root) {
        super(root);
        this.namespacedKey = namespacedKey;
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }
}
