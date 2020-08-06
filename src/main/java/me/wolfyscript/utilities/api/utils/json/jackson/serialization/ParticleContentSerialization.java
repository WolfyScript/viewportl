package me.wolfyscript.utilities.api.utils.json.jackson.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.api.custom_items.ParticleContent;
import me.wolfyscript.utilities.api.utils.NamespacedKey;
import me.wolfyscript.utilities.api.utils.particles.ParticleEffect;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

public class ParticleContentSerialization {

    public static void create(SimpleModule module){
        module.addSerializer(ParticleContent.class, new Serializer());
        module.addDeserializer(ParticleContent.class, new Deserializer());
    }

    public static class Serializer extends StdSerializer<ParticleContent> {

        public Serializer(){
            this(ParticleContent.class);
        }

        protected Serializer(Class<ParticleContent> t) {
            super(t);
        }

        @Override
        public void serialize(ParticleContent particleContent, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            for (Map.Entry<ParticleEffect.Action, NamespacedKey> entry : particleContent.entrySet()) {
                gen.writeObjectFieldStart(entry.getKey().name().toLowerCase(Locale.ROOT));
                gen.writeStringField("effect", entry.getValue().toString());
                gen.writeEndObject();
            }
            gen.writeEndObject();
        }
    }

    public static class Deserializer extends StdDeserializer<ParticleContent> {

        public Deserializer(){
            this(ParticleContent.class);
        }

        protected Deserializer(Class<ParticleContent> t) {
            super(t);
        }

        @Override
        public ParticleContent deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.readValueAsTree();
            if (node.isObject()) {
                ParticleContent particleContent = new ParticleContent();
                for (ParticleEffect.Action action : ParticleEffect.Action.values()) {
                    String id = action.name().toLowerCase(Locale.ROOT);
                    JsonNode particle = node.get(id);
                    if(particle != null && !particle.isNull() && !particle.isMissingNode() && particle.isObject()){
                        if (particle.has("effect")) {
                            String effect = particle.get("effect").asText();
                            NamespacedKey namespacedKey;
                            if(effect.split(":").length > 1){
                                namespacedKey = new NamespacedKey(effect.split(":")[0], effect.split(":")[1]);
                            }else{
                                namespacedKey = new NamespacedKey("wolfyutilities", effect);
                            }
                            particleContent.addParticleEffect(action, namespacedKey);
                        }
                    }
                }
                return particleContent;
            }
            return new ParticleContent();
        }
    }
}
