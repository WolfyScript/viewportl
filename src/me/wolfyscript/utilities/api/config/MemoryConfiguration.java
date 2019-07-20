package me.wolfyscript.utilities.api.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public abstract class MemoryConfiguration extends Configuration{

    private HashMap<String, Object> map;

    public MemoryConfiguration(ConfigAPI configAPI, String name, Type type) {
        super(configAPI, name, type);
    }

    public abstract Set<String> getKeys();

    public abstract Map<String, Object> getMap();

}
