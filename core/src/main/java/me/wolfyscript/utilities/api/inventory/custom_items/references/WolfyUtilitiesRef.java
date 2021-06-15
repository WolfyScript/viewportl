package me.wolfyscript.utilities.api.inventory.custom_items.references;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomItem;
import me.wolfyscript.utilities.util.NamespacedKey;
import me.wolfyscript.utilities.util.Registry;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

public class WolfyUtilitiesRef extends APIReference {

    private static final org.bukkit.NamespacedKey CUSTOM_ITEM_KEY = new org.bukkit.NamespacedKey(WolfyUtilities.getWUPlugin(), "custom_item");

    private final NamespacedKey namespacedKey;

    public WolfyUtilitiesRef(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    public WolfyUtilitiesRef(WolfyUtilitiesRef wolfyUtilitiesRef) {
        super(wolfyUtilitiesRef);
        this.namespacedKey = wolfyUtilitiesRef.namespacedKey;
    }

    public NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    @Override
    public ItemStack getLinkedItem() {
        CustomItem customItem = Registry.CUSTOM_ITEMS.get(namespacedKey);
        if (customItem != null) {
            return customItem.create();
        }
        System.out.println("Couldn't find CustomItem for " + namespacedKey.toString());
        return null;
    }

    @Override
    public ItemStack getIdItem() {
        ItemStack itemStack = getLinkedItem();
        return itemStack == null ? new ItemStack(Material.AIR) : itemStack;
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        if (itemStack != null) {
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                var container = itemMeta.getPersistentDataContainer();
                if (container.has(CUSTOM_ITEM_KEY, PersistentDataType.STRING)) {
                    return Objects.equals(this.namespacedKey, me.wolfyscript.utilities.util.NamespacedKey.of(container.get(CUSTOM_ITEM_KEY, PersistentDataType.STRING)));
                }
            }
        }
        return false;
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (namespacedKey != null) {
            gen.writeStringField("wolfyutilities", namespacedKey.toString());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WolfyUtilitiesRef that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(namespacedKey, that.namespacedKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), namespacedKey);
    }

    @Override
    public WolfyUtilitiesRef clone() {
        return new WolfyUtilitiesRef(this);
    }

    public static class Parser extends APIReference.Parser<WolfyUtilitiesRef> {

        public Parser() {
            super("wolfyutilities", "item_key");
        }

        @Override
        public @Nullable WolfyUtilitiesRef construct(ItemStack itemStack) {
            if (itemStack == null) return null;
            var itemMeta = itemStack.getItemMeta();
            if (itemMeta != null) {
                var container = itemMeta.getPersistentDataContainer();
                if (container.has(CUSTOM_ITEM_KEY, PersistentDataType.STRING)) {
                    return new WolfyUtilitiesRef(me.wolfyscript.utilities.util.NamespacedKey.of(container.get(CUSTOM_ITEM_KEY, PersistentDataType.STRING)));
                }
            }
            return null;
        }

        @Override
        public @Nullable WolfyUtilitiesRef parse(JsonNode element) {
            return new WolfyUtilitiesRef(NamespacedKey.of(element.asText()));
        }
    }
}
