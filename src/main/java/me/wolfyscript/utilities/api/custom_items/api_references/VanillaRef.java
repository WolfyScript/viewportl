package me.wolfyscript.utilities.api.custom_items.api_references;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
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
        ObjectMapper objectMapper = JacksonUtil.getObjectMapper().copy();//.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.EVERYTHING);
        objectMapper.writeValue(gen, itemStack);

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

    public static class Serialization extends StdDeserializer<VanillaRef> {

        public Serialization(){
            super(VanillaRef.class);
        }

        protected Serialization(Class<VanillaRef> t) {
            super(t);
        }

        @Override
        public VanillaRef deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.readValueAsTree();
            ObjectMapper objectMapper = JacksonUtil.getObjectMapper();//.activateDefaultTyping(ptv, ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT);
            return new VanillaRef(objectMapper.convertValue(node, ItemStack.class));
        }
    }
}
