package me.wolfyscript.utilities.api.config.templates;

import me.wolfyscript.utilities.api.config.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Config extends FileConfiguration implements ConfigurationSection {

    public FileConfiguration configuration;

    public Config(ConfigAPI configAPI, String path, String name, String defPath, String defFileName, String fileType, boolean overwrite) {
        super(configAPI, path, name, defPath, defFileName, fileType.equalsIgnoreCase("yml") ? Configuration.Type.YAML : Configuration.Type.JSON);
        if(getType().equals(Configuration.Type.JSON)){
            this.configuration = new JsonConfiguration(configAPI, path, name, defPath, defFileName, overwrite);
        }else{
            this.configuration = new YamlConfiguration(configAPI, path, name, defPath, defFileName, overwrite);
        }

    }

    @Override
    public void save() {
        configuration.save();
    }

    @Override
    public void load() {
        configuration.load();
    }

    @Override
    public void reload() {
        configuration.reload();
    }

    @Override
    public Set<String> getKeys() {
        return configuration.getKeys();
    }

    @Override
    public Map<String, Object> getMap() {
        return configuration.getMap();
    }

    @Override
    public void set(String path, Object value) {

    }

    @Override
    public Object get(String path) {
        return null;
    }

    @Override
    public Object get(String path, Object def) {
        return null;
    }

    @Override
    public String getString(String path) {
        return null;
    }

    @Override
    public int getInt(String path) {
        return 0;
    }

    @Override
    public boolean getBoolean(String path) {
        return false;
    }

    @Override
    public double getDouble(String path) {
        return 0;
    }

    @Override
    public long getLong(String path) {
        return 0;
    }

    @Override
    public List<?> getList(String path) {
        return null;
    }

    @Override
    public List<String> getStringList(String path) {
        return null;
    }

    @Override
    public void setItem(String path, ItemStack itemStack) {

    }

    @Override
    public void setItem(String path, String name, ItemStack itemStack) {

    }

    @Override
    public void saveItem(String path, ItemStack item) {

    }

    @Override
    public void saveItem(String path, String name, ItemStack itemStack) {

    }

    @Override
    public ItemStack getItem(String path) {
        return null;
    }

    @Override
    public ItemStack getItem(String path, boolean replaceKeys) {
        return null;
    }
}
