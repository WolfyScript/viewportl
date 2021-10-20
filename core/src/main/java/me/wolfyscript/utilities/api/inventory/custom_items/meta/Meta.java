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

package me.wolfyscript.utilities.api.inventory.custom_items.meta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class Meta implements Keyed {

    protected MetaSettings.Option option;

    @JsonIgnore
    private NamespacedKey namespacedKey;
    @JsonIgnore
    private List<MetaSettings.Option> availableOptions;

    public MetaSettings.Option getOption() {
        return option;
    }

    public void setOption(MetaSettings.Option option) {
        this.option = option;
    }

    @JsonIgnore
    public boolean isExact() {
        return option.equals(MetaSettings.Option.EXACT);
    }

    public List<MetaSettings.Option> getAvailableOptions() {
        return availableOptions;
    }

    protected void setAvailableOptions(MetaSettings.Option... options) {
        if (options != null) {
            availableOptions = Arrays.asList(options);
        }
    }

    public abstract boolean check(ItemBuilder itemOther, ItemBuilder item);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meta meta = (Meta) o;
        return Objects.equals(namespacedKey, meta.namespacedKey) && option == meta.option && Objects.equals(availableOptions, meta.availableOptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespacedKey, option, availableOptions);
    }

    @Override
    public final NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    final void setNamespacedKey(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    public static class Provider<M extends Meta> implements Keyed {

        private final NamespacedKey namespacedKey;
        private final Class<M> type;

        public Provider(NamespacedKey namespacedKey, @NotNull Class<M> type) {
            Objects.requireNonNull(type, "Cannot initiate Meta \"" + namespacedKey.toString() + "\" with a null type!");
            this.namespacedKey = namespacedKey;
            this.type = type;
        }

        public NamespacedKey getNamespacedKey() {
            return namespacedKey;
        }

        public M provide() {
            try {
                M meta = type.getDeclaredConstructor().newInstance();
                meta.setNamespacedKey(namespacedKey);
                return meta;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return null;
        }

        public M parse(JsonNode node) {
            M meta = JacksonUtil.getObjectMapper().convertValue(node, type);
            if (meta != null) {
                meta.setNamespacedKey(namespacedKey);
                return meta;
            }
            return null;
        }

    }


}
