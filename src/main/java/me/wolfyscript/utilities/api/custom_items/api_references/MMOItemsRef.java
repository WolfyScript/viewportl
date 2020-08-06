package me.wolfyscript.utilities.api.custom_items.api_references;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
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
        return null;
    }

    @Override
    public ItemStack getIdItem() {
        return null;
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {

    }

    public static class Serialization extends StdDeserializer<MMOItemsRef> {

        public Serialization(){
            super(MMOItemsRef.class);
        }

        protected Serialization(Class<MMOItemsRef> t) {
            super(t);
        }

        @Override
        public MMOItemsRef deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctxt) throws IOException {
            return null;
        }
    }
}
