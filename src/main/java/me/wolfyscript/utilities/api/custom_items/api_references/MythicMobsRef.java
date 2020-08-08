package me.wolfyscript.utilities.api.custom_items.api_references;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;

public class MythicMobsRef extends APIReference{

    private final String itemName;

    public MythicMobsRef(String itemName){
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

    public static class Serialization extends StdDeserializer<MythicMobsRef> {

        public Serialization(){
            super(MythicMobsRef.class);
        }

        protected Serialization(Class<MythicMobsRef> t) {
            super(t);
        }

        @Override
        public MythicMobsRef deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctxt) throws IOException {
            return null;
        }
    }
}
