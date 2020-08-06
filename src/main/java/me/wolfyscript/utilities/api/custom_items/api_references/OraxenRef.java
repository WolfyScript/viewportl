package me.wolfyscript.utilities.api.custom_items.api_references;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.th0rgal.oraxen.items.OraxenItems;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Objects;

public class OraxenRef extends APIReference{

    private final String itemID;

    public OraxenRef(String itemID){
        this.itemID = itemID;
    }

    @Override
    public ItemStack getLinkedItem() {
        if(OraxenItems.isAnItem(itemID)){
            return OraxenItems.getItemById(itemID).build();
        }
        return new ItemStack(Material.AIR);
    }

    @Override
    public ItemStack getIdItem() {
        return getLinkedItem();
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
        if (!(o instanceof OraxenRef)) return false;
        if (!super.equals(o)) return false;
        OraxenRef oraxenRef = (OraxenRef) o;
        return Objects.equals(itemID, oraxenRef.itemID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), itemID);
    }

    public static class Serialization extends StdDeserializer<OraxenRef> {

        public Serialization(){
            super(OraxenRef.class);
        }

        protected Serialization(Class<OraxenRef> t) {
            super(t);
        }

        @Override
        public OraxenRef deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.readValueAsTree();
            return new OraxenRef(node.asText());
        }
    }
}
