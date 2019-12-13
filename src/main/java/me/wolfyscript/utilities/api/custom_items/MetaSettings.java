package me.wolfyscript.utilities.api.custom_items;

import me.wolfyscript.utilities.api.custom_items.meta.*;
import me.wolfyscript.utilities.main.Main;
import org.bukkit.inventory.meta.ItemMeta;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.*;

public class MetaSettings {

    private HashMap<String, Meta> metas = new HashMap<>();

    //Meta initialization
    public MetaSettings(){
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
    }

    public MetaSettings(String jsonString) {
        this();
        JSONParser parser = new JSONParser();
        JSONObject obj = null;
        if (!jsonString.isEmpty()) {
            try {
                obj = (JSONObject) parser.parse(jsonString);
            } catch (ParseException e) {
                Main.getMainUtil().sendConsoleWarning("Error getting JSONObject from String:");
                Main.getMainUtil().sendConsoleWarning("" + jsonString);
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

    private void addMeta(Meta meta){
        this.metas.put(meta.getId(), meta);
    }

    public Meta getMetaByID(String id) {
        return metas.get(id);
    }

    public List<String> getMetas(){
        return new ArrayList<>(metas.keySet());
    }

    public boolean checkMeta(ItemMeta input, ItemMeta customItem) {
        for (Meta meta : metas.values()) {
            if (!meta.check(input, customItem)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        HashMap<String, String> map = new HashMap<>();
        for (String id : metas.keySet()) {
            map.put(id, metas.get(id).toString());
        }
        JSONObject obj = new JSONObject(map);
        return obj.toString();
    }

    public enum Option {
        EXACT, IGNORE, HIGHER, HIGHER_EXACT, LOWER, LOWER_EXACT, HIGHER_LOWER
    }

}
