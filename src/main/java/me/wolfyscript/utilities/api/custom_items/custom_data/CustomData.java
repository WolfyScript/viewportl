package me.wolfyscript.utilities.api.custom_items.custom_data;

import java.util.Map;

public abstract class CustomData {

    private String id;

    protected CustomData(String id){
        this.id = id;
    }

    public abstract CustomData getDefaultCopy();

    public abstract Map<String, Object> toMap();

    public abstract CustomData fromMap(Map<String, Object> map);

    public  String getId() {
        return id;
    }
}
