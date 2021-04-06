package me.wolfyscript.utilities.api.inventory.custom_items.references;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.lone.itemsadder.api.CustomStack;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

public class ItemsAdderRef extends APIReference {

    private final String itemID;

    public ItemsAdderRef(String itemID) {
        this.itemID = itemID;
    }

    @Override
    public ItemStack getLinkedItem() {
        CustomStack customStack = CustomStack.getInstance(itemID);
        if (customStack != null) {
            return customStack.getItemStack();
        }
        return ItemUtils.AIR;
    }

    @Override
    public ItemStack getIdItem() {
        return getLinkedItem();
    }

    @Override
    public boolean isValidItem(ItemStack itemStack) {
        CustomStack customStack = CustomStack.byItemStack(itemStack);
        return customStack != null && Objects.equals(itemID, customStack.getNamespacedID());
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStringField("itemsadder", itemID);
    }

    public String getItemID() {
        return itemID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemsAdderRef)) return false;
        if (!super.equals(o)) return false;
        ItemsAdderRef oraxenRef = (ItemsAdderRef) o;
        return Objects.equals(itemID, oraxenRef.itemID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), itemID);
    }

    public static class Parser extends PluginParser<ItemsAdderRef> {

        public Parser() {
            super("ItemsAdder", "itemsadder");
        }

        @Override
        public @Nullable ItemsAdderRef construct(ItemStack itemStack) {
            CustomStack customStack = CustomStack.byItemStack(itemStack);
            if (customStack != null) {
                return new ItemsAdderRef(customStack.getNamespacedID());
            }
            return null;
        }

        @Override
        public @Nullable ItemsAdderRef parse(JsonNode element) {
            return new ItemsAdderRef(element.asText());
        }
    }
}