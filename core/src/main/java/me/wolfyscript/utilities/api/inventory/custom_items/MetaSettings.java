package me.wolfyscript.utilities.api.inventory.custom_items;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.inventory.custom_items.meta.*;
import me.wolfyscript.utilities.util.inventory.item_builder.ItemBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@JsonSerialize(using = MetaSettings.Serializer.class)
@JsonDeserialize(using = MetaSettings.Deserializer.class)
public class MetaSettings {

    private final HashMap<String, Meta> metas = new HashMap<>();

    //Meta initialization
    public MetaSettings() {
        addMeta(new AttributesModifiersMeta());
        addMeta(new CustomModelDataMeta());
        addMeta(new DamageMeta());
        addMeta(new EnchantMeta());
        addMeta(new FlagsMeta());
        addMeta(new LoreMeta());
        addMeta(new NameMeta());
        addMeta(new PlayerHeadMeta());
        addMeta(new PotionMeta());
        addMeta(new RepairCostMeta());
        addMeta(new UnbreakableMeta());
        addMeta(new CustomDamageMeta());
        addMeta(new CustomDurabilityMeta());
        addMeta(new CustomItemTagMeta());
    }

    @Deprecated
    public MetaSettings(String jsonString) {
        this();
        JSONParser parser = new JSONParser();
        JSONObject obj = null;
        if (!jsonString.isEmpty()) {
            try {
                obj = (JSONObject) parser.parse(jsonString);
            } catch (ParseException e) {
                WolfyUtilities.get(WolfyUtilities.getWUPlugin()).getChat().sendConsoleWarning("Error getting JSONObject from String:");
                WolfyUtilities.get(WolfyUtilities.getWUPlugin()).getChat().sendConsoleWarning("" + jsonString);
            }
        }
        if (obj != null) {
            Set<String> keys = obj.keySet();
            for (String key : keys) {
                String value = (String) obj.get(key);
                getMetaByID(key).parseFromJSON(value);
            }
        }
    }

    public MetaSettings(JsonNode node) {
        this();
        node.fields().forEachRemaining(entry -> getMetaByID(entry.getKey()).readFromJson(entry.getValue()));
    }

    private void addMeta(Meta meta) {
        this.metas.put(meta.getId(), meta);
    }

    public Meta getMetaByID(String id) {
        return metas.get(id);
    }

    public List<String> getMetas() {
        return new ArrayList<>(metas.keySet());
    }

    public boolean checkMeta(ItemBuilder itemOther, ItemBuilder item) {
        return metas.values().stream().allMatch(meta -> meta.check(itemOther, item));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MetaSettings)) return false;
        MetaSettings that = (MetaSettings) o;
        return Objects.equals(metas, that.metas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metas);
    }

    @Deprecated
    @Override
    public String toString() {
        Map<String, String> map = metas.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().toString()));
        JSONObject obj = new JSONObject(map);
        return obj.toString();
    }

    public enum Option {
        EXACT, IGNORE, HIGHER, HIGHER_EXACT, LOWER, LOWER_EXACT, HIGHER_LOWER
    }

    public static class Serializer extends StdSerializer<MetaSettings>{

        public Serializer(){
            super(MetaSettings.class);
        }

        protected Serializer(Class<MetaSettings> t) {
            super(t);
        }

        @Override
        public void serialize(MetaSettings value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();
            for (Map.Entry<String, Meta> entry : value.metas.entrySet()) {
                gen.writeFieldName(entry.getKey());
                entry.getValue().writeToJson(gen);
            }
            gen.writeEndObject();
        }
    }

    public static class Deserializer extends StdDeserializer<MetaSettings> {

        public Deserializer(){
            super(MetaSettings.class);
        }

        @Override
        public MetaSettings deserialize(com.fasterxml.jackson.core.JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.readValueAsTree();
            if (node.isTextual()) {
                //Old String style meta
                return new MetaSettings(node.asText());
            }
            //New Json style meta
            return new MetaSettings(node);
        }

        protected Deserializer(Class<MetaSettings> t) {
            super(t);
        }
    }
}
