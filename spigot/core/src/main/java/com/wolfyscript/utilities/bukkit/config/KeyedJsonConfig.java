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

package com.wolfyscript.utilities.bukkit.config;

import com.wolfyscript.utilities.NamespacedKey;
import java.io.File;
import java.util.function.Function;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * This config is plain for utility and might change significantly in future updates.
 * This an it's counterpart {@link JsonConfig} will be changed in the upcoming updates and are planned to be somewhat stable/done in version 1.6.3.x.
 *
 * @param <T> any type you want to save in/load from the config file. See the Jackson documentation for more information about how to use custom serializer.
 */
@Deprecated
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

    public NamespacedKey key() {
        return namespacedKey;
    }
}
