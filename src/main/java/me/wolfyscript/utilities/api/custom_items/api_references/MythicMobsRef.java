package me.wolfyscript.utilities.api.custom_items.api_references;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.lumine.xikage.mythicmobs.MythicMobs;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Objects;

public class MythicMobsRef extends APIReference{

    private final String itemName;

    public MythicMobsRef(String itemName){
        this.itemName = itemName;
    }

    @Override
    public ItemStack getLinkedItem() {
        return MythicMobs.inst().getItemManager().getItemStack(itemName);
    }

    @Override
    public ItemStack getIdItem() {
        return getLinkedItem();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MythicMobsRef)) return false;
        if (!super.equals(o)) return false;
        MythicMobsRef that = (MythicMobsRef) o;
        return Objects.equals(itemName, that.itemName);
    }

    @Override
    public void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStringField("mythicmobs", itemName);
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
            JsonNode node = p.readValueAsTree();
            return new MythicMobsRef(node.asText());
        }
    }
}
