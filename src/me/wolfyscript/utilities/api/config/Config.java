package me.wolfyscript.utilities.api.config;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Config extends FileConfiguration implements ConfigurationSection {

    public FileConfiguration configuration;

    public Config(ConfigAPI configAPI, String path, String name, String defPath, String defFileName, String fileType, boolean overwrite) {
        super(configAPI, path, name, defPath, defFileName, fileType.equalsIgnoreCase("json") ? Type.JSON : Type.YAML);
        if (getType().equals(Configuration.Type.JSON)) {
            this.configuration = new JsonConfiguration(configAPI, path, name, defPath, defFileName, overwrite);
        } else {
            this.configuration = new YamlConfiguration(configAPI, path, name, defPath, defFileName, overwrite);
        }
        setPathSeparator('.');
    }

    /*
    A Config not linked to a File! Must be json!
     */
    public Config(ConfigAPI configAPI, String name, String defPath, String defFileName){
        super(configAPI, "", name, defPath, defFileName, Type.JSON);
        this.configuration = new JsonConfiguration(configAPI, name, defPath, defFileName);
    }

    @Override
    public void save() {
        configuration.save();
    }

    public void save(boolean prettyPrinting) {
        if (configuration instanceof JsonConfiguration) {
            ((JsonConfiguration) configuration).save(prettyPrinting);
        } else {
            this.save();
        }
    }

    @Override
    public void load() {
        configuration.load();
    }

    @Override
    public void reload() {
        configuration.reload();
    }

    public void reload(boolean prettyPrinting) {
        if (configuration instanceof JsonConfiguration) {
            ((JsonConfiguration) configuration).reload(prettyPrinting);
        } else {
            this.reload();
        }
    }

    @Override
    public void loadDefaults() {
        configuration.loadDefaults();
    }

    @Override
    public void onFirstInit() {
        configuration.onFirstInit();
    }

    @Override
    public void init() {
        configuration.init();
    }

    @Override
    public Set<String> getKeys() {
        return configuration.getKeys();
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        return configuration.getKeys(deep);
    }

    @Override
    public Map<String, Object> getMap() {
        return configuration.getMap();
    }

    @Override
    public boolean hasPathSeparator() {
        return configuration.hasPathSeparator();
    }

    @Override
    public void setPathSeparator(char pathSeparator) {
        configuration.setPathSeparator(pathSeparator);
    }

    @Override
    public char getPathSeparator() {
        return configuration.getPathSeparator();
    }

    @Override
    public void set(String path, Object value) {
        configuration.set(path, value);
    }

    @Override
    public Object get(String path) {
        return configuration.get(path);
    }

    @Override
    public Object get(String path, Object def) {
        return configuration.get(path, def);
    }

    @Override
    public String getString(String path) {
        return configuration.getString(path);
    }

    @Override
    public int getInt(String path) {
        return configuration.getInt(path);
    }

    @Override
    public boolean getBoolean(String path) {
        return configuration.getBoolean(path);
    }

    @Override
    public double getDouble(String path) {
        return configuration.getDouble(path);
    }

    @Override
    public long getLong(String path) {
        return configuration.getLong(path);
    }

    @Override
    public List<?> getList(String path) {
        return configuration.getList(path);
    }

    @Override
    public List<String> getStringList(String path) {
        return configuration.getStringList(path);
    }

    @Override
    public void setItem(String path, ItemStack itemStack) {
        configuration.setItem(path, itemStack);
    }

    @Override
    public void setItem(String path, String name, ItemStack itemStack) {
        configuration.setItem(path, name, itemStack);
    }

    @Deprecated
    @Override
    public void saveItem(String path, ItemStack item) {
        configuration.saveItem(path, item);
    }

    @Deprecated
    @Override
    public void saveItem(String path, String name, ItemStack itemStack) {
        configuration.saveItem(path, name, itemStack);
    }

    @Override
    public ItemStack getItem(String path) {
        return configuration.getItem(path);
    }

    @Override
    public ItemStack getItem(String path, boolean replaceKeys) {
        return configuration.getItem(path, replaceKeys);
    }

    @Override
    public Map<String, Object> getValues(String path) {
        return configuration.getValues(path);
    }
}
