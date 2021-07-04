package me.wolfyscript.utilities.api.inventory.custom_items.references;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import io.th0rgal.oraxen.items.OraxenItems;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

/**
 * Links to Oraxen and saves the specified id of the item.
 */
public class OraxenRef extends APIReference {

    private final String itemID;

    public OraxenRef(String itemID) {
        super();
        this.itemID = itemID;
    }

    public OraxenRef(OraxenRef oraxenRef) {
        super(oraxenRef);
        this.itemID = oraxenRef.itemID;
    }

    @Override
    public ItemStack getLinkedItem() {
        if (OraxenItems.exists(itemID)) {
            return OraxenItems.getItemById(itemID).build();
        }
        return ItemUtils.AIR;
    }

    @Override
    public ItemStack getIdItem() {
        return getLinkedItem();
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        String itemId = OraxenItems.getIdByItem(itemStack);
        if (itemId != null && !itemId.isEmpty()) {
            return Objects.equals(this.itemID, itemId);
        }
        return false;
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStringField("oraxen", itemID);
    }

    public String getItemID() {
        return itemID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OraxenRef oraxenRef)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(itemID, oraxenRef.itemID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), itemID);
    }

    @Override
    public OraxenRef clone() {
        return new OraxenRef(this);
    }

    public static class Parser extends APIReference.PluginParser<OraxenRef> {

        public Parser() {
            super("Oraxen", "oraxen");
        }

        @Override
        public @Nullable OraxenRef construct(ItemStack itemStack) {
            String itemId = OraxenItems.getIdByItem(itemStack);
            if (itemId != null && !itemId.isEmpty()) {
                return new OraxenRef(itemId);
            }
            return null;
        }

        @Override
        public @Nullable OraxenRef parse(JsonNode element) {
            return new OraxenRef(element.asText());
        }
    }
}
