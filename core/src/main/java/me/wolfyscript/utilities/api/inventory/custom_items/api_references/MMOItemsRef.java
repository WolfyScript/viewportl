package me.wolfyscript.utilities.api.inventory.custom_items.api_references;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.inventory.ItemUtils;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class MMOItemsRef extends APIReference{

    private final String itemName;

    public MMOItemsRef(String itemName){
        this.itemName = itemName;
    }

    @Override
    public ItemStack getLinkedItem() {
        //TODO
        return ItemUtils.AIR;
    }

    @Override
    public ItemStack getIdItem() {
        return ItemUtils.AIR;
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {

    }
}
