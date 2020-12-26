package me.wolfyscript.utilities.api.inventory.custom_items.api_references;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.json.jackson.JacksonUtil;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Objects;

public class VanillaRef extends APIReference {

    private final ItemStack itemStack;

    public VanillaRef(ItemStack itemStack){
        this.itemStack = itemStack;
    }

    @Override
    public ItemStack getLinkedItem() {
        return itemStack;
    }

    @Override
    public ItemStack getIdItem() {
        return itemStack;
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeFieldName("item");
        JacksonUtil.getObjectMapper().writeValue(gen, itemStack);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VanillaRef)) return false;
        if (!super.equals(o)) return false;
        VanillaRef that = (VanillaRef) o;
        return Objects.equals(itemStack, that.itemStack);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), itemStack);
    }
}
