package me.wolfyscript.utilities.api.config;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public interface ConfigurationSection {

    /*
        Sets a value to the path
     */
    void set(String path, Object value);

    Object get(String path);

    Object get(String path, Object def);

    String getString(String path);

    String getString(String path, String def);

    int getInt(String path);

    int getInt(String path, int def);

    boolean getBoolean(String path);

    double getDouble(String path);

    double getDouble(String path, double def);

    long getLong(String path);

    long getLong(String path, long def);

    List<?> getList(String path);

    List<String> getStringList(String path);

    void setItem(String path, ItemStack itemStack);

    void setItem(String path, String name, ItemStack itemStack);

    @Deprecated
    void saveItem(String path, ItemStack item);

    @Deprecated
    void saveItem(String path, String name, ItemStack itemStack);

    ItemStack getItem(String path);

    ItemStack getItem(String path, boolean replaceKeys);

    Map<String, Object> getValues(String path);
}
