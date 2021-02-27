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
