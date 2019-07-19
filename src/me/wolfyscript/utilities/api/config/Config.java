package me.wolfyscript.utilities.api.config;

public interface Config {

    String getName();

    Type getType();

    void load();

    void save();

    void reload();

    enum Type{
        YAML, JSON
    }
}
