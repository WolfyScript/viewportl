package me.wolfyscript.utilities.api.inventory.custom_items.references;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.Type;
import net.mmogroup.mmolib.api.item.NBTItem;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public class MMOItemsRef extends APIReference {

    private final Type itemType;
    private final String itemName;

    public MMOItemsRef(Type itemType, String itemName) {
        this.itemType = itemType;
        this.itemName = itemName;
    }

    @Override
    public ItemStack getLinkedItem() {
        return MMOItems.plugin.getItem(itemType, itemName);
    }

    @Override
    public ItemStack getIdItem() {
        return getLinkedItem();
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject("mmoitems");
        gen.writeStringField("type", itemType.toString());
        gen.writeStringField("name", itemName);
        gen.writeEndObject();
    }

    public static class Parser extends PluginParser<MMOItemsRef> {

        public Parser() {
            super("MMOItems", "mmoitems");
        }

        @Override
        public @Nullable MMOItemsRef construct(ItemStack itemStack) {
            NBTItem nbtItem = NBTItem.get(itemStack);
            if (nbtItem.hasType()) {
                Type type = MMOItems.plugin.getTypes().get(nbtItem.getType());
                String itemId = nbtItem.getString("MMOITEMS_ITEM_ID");
                return new MMOItemsRef(type, itemId);
            }
            return null;
        }

        @Override
        public @Nullable MMOItemsRef parse(JsonNode element) {
            return null;
        }
    }
}
