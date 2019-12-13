package me.wolfyscript.utilities.api.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public abstract class MemoryConfiguration extends Configuration implements ConfigurationSection {

    public HashMap<String, Object> map;

    public MemoryConfiguration(ConfigAPI configAPI, String name, Type type) {
        super(configAPI, name, type);
    }

    public abstract Set<String> getKeys();

    public abstract Set<String> getKeys(boolean deep);

    public abstract Map<String, Object> getMap();

    public abstract boolean hasPathSeparator();

    public abstract void setPathSeparator(char pathSeparator);

    public abstract char getPathSeparator();
}
