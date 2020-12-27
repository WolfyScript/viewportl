package me.wolfyscript.utilities.api.inventory.custom_items.references;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import dev.lone.itemsadder.api.ItemsAdder;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Objects;

public class ItemsAdderRef extends APIReference{

    private final String itemName;

    public ItemsAdderRef(String itemName){
        this.itemName = itemName;
    }

    @Override
    public ItemStack getLinkedItem() {
        if (WolfyUtilities.hasPlugin("ItemsAdder")) {
            return ItemsAdder.getCustomItem(itemName);
        }
        return ItemUtils.AIR;
    }

    @Override
    public ItemStack getIdItem() {
        return getLinkedItem();
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStringField("itemsadder", itemName);
    }

    public String getItemName() {
        return itemName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ItemsAdderRef)) return false;
        if (!super.equals(o)) return false;
        ItemsAdderRef that = (ItemsAdderRef) o;
        return Objects.equals(itemName, that.itemName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), itemName);
    }
}
