package me.wolfyscript.utilities.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.ItemUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.NumberConversions;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class JsonConfiguration extends FileConfiguration implements ConfigurationSection{

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private HashMap<String, Object> map = new HashMap<>();
    private String defJsonString;

    private char pathSeparator = '#';

    /*
        Creates a json YamlConfiguration file. The default is set inside of the jar at the specified path.
        path - the config file path
        filename - config file name without ".json" and the key of this config (if it's registered)
        defPath - file path of the file containing the defaults
        defFileName - file name of the file containing the defaults
        overwrite - set if the existing file should be replaced by the defaults
     */
    public JsonConfiguration(ConfigAPI configAPI, String path, String name, String defPath, String defFileName, boolean overwrite) {
        super(configAPI, path, name, defPath, defFileName, Type.JSON);

        if (!configFile.exists() || overwrite) {
            configFile.getParentFile().mkdirs();
            loadDefaults(overwrite);
            onFirstInit();
        }
        load();
    }

    public JsonConfiguration(ConfigAPI configAPI, String path, String fileName) {
        this(configAPI, path, fileName, "me/wolfyscript/utilities/api/config/defaults", "defJson", false);
    }

    /*
        Creates a jsonConfig with a jsonString as the default instead of a File. Saved in a File (path & filename)
        name - config file name without ".json" and the key of this config (if it's registered)
        jsonString - the default json value.
        path - the config file path without ".json" at the end!
        overwrite - set if the existing file should be replaced by the defaults
     */
    public JsonConfiguration(ConfigAPI configAPI, String jsonString, String path, String name, boolean overwrite) {
        super(configAPI, path, name, "","", Type.JSON);
        this.configAPI = configAPI;
        this.name = name;
        this.configFile = new File(path + ".json");
        if (!configFile.exists() || overwrite) {
            loadDefaults(overwrite);
            loadFromString(jsonString);
            save();
            onFirstInit();
        }
    }

    /*
        Creates a memory only json config!
     */
    public JsonConfiguration(ConfigAPI configAPI, String jsonString) {
        super(configAPI, "", "", "", "", Type.JSON);
        this.configAPI = configAPI;
        this.defJsonString = jsonString;
        loadFromString(jsonString);
    }

    /*
        Called when the config file didn't exist or whenever the config gets overwritten!
     */
    public void onFirstInit() {

    }

    public void init() {

    }

    public boolean hasPathSeparator(){
        return this.pathSeparator != 0;
    }

    public void setPathSeparator(char pathSeparator){
        this.pathSeparator = pathSeparator;
    }

    public char getPathSeparator() {
        return pathSeparator;
    }

    public void loadDefaults(boolean overwrite) {
        if (defPath != null && defFileName != null) {
            if (!configFile.exists() || overwrite) {
                InputStream initialStream = plugin.getResource(defPath + "/" + defFileName + ".json");
                if (initialStream != null) {
                    try {
                        byte[] buffer = new byte[initialStream.available()];
                        initialStream.read(buffer);
                        OutputStream outStream = new FileOutputStream(configFile);
                        outStream.write(buffer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void loadDefaults() {
        loadDefaults(false);
    }

    public void loadFromString(String json) {
        map = gson.fromJson(json, new HashMap<String, Object>().getClass());
    }

    public String toString(boolean prettyPrinting) {
        Gson gsonBuilder = prettyPrinting ? gson : new GsonBuilder().create();
        return gsonBuilder.toJson(map);
    }

    public String toString() {
        return toString(false);
    }

    public void load() {
        if (linkedToFile()) {
            try {
                map = gson.fromJson(new FileReader(this.configFile), new HashMap<String, Object>().getClass());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void save(boolean prettyPrinting) {
        if (linkedToFile()) {
            final String json = toString(prettyPrinting);
            try {
                PrintWriter pw = new PrintWriter(configFile);
                pw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Files.write(configFile.toPath(), json.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        save(false);
    }

    public void reload() {
        save();
        load();
    }

    public void linkToFile(String path) {
        this.configFile = new File(path + ".json");
        if (!configFile.exists()) {
            configFile.getParentFile().mkdirs();
            loadDefaults();
            onFirstInit();
        }
    }

    public void linkToFile(String path, String fileName) {
        linkToFile(path + "/" + fileName);
    }

    public boolean linkedToFile() {
        return configFile != null;
    }

    public HashMap<String, Object> getValues() {
        return map;
    }

    public Set<String> getKeys() {
        return map.keySet();
    }

    @Override
    public Map<String, Object> getMap() {
        return map;
    }

    @Override
    public Object get(String path) {
        return get(path, null);
    }

    /*
        Gets the Object of the specified path.
        The name would be accessed with getObject("crafting", "workbench", "name");
        The path defines the keys and sub keys.
        {
            "crafting": {
                "workbench": {
                    "name": "&6Advanced Workbench",
                    "lore": [
                        "&7Workbench for advanced crafting"
                    ]
                }
            }
        }
    */
    public Object get(String path, @Nullable Object def) {
        String[] pathKeys = path.split(""+pathSeparator);
        Map<String, Object> currentMap = map;
        for (int i = 0; i < pathKeys.length; i++) {
            Object object = currentMap.get(pathKeys[i]);
            if (i != pathKeys.length - 1) {
                if (object instanceof Map) {
                    currentMap = (Map<String, Object>) object;
                }
            } else {
                return object;
            }
        }
        if(def != null){
            return def;
        }
        return null;
    }

    @Override
    public void set(String path, Object value){
        String[] pathKeys = path.split(""+pathSeparator);
        Map<String, Object> currentMap = map;
        for (int i = 0; i < pathKeys.length; i++) {
            Object object = currentMap.get(pathKeys[i]);
            if (i != pathKeys.length - 1) {
                if (object instanceof Map) {
                    currentMap = (Map<String, Object>) object;
                }
            }else{
                currentMap.put(pathKeys[i], value);
                if (saveAfterValueSet) {
                    reload();
                }
            }
        }
    }

    @Override
    public String getString(String path) {
        if(get(path) != null){
            return get(path).toString();
        }
        return "";
    }

    @Override
    public int getInt(String path) {
        return NumberConversions.toInt(get(path));
    }

    @Override
    public boolean getBoolean(String path) {
        Object val = this.get(path);
        return val instanceof Boolean && (Boolean) val;
    }

    @Override
    public double getDouble(String path) {
        Object val = this.get(path);
        return val instanceof Number ? NumberConversions.toDouble(val) : 0.0D;
    }

    @Override
    public long getLong(String path) {
        Object val = this.get(path);
        return val instanceof Number ? NumberConversions.toLong(val) : 0;
    }

    @Override
    public List<?> getList(String path) {
        Object val = this.get(path);
        return (List)(val instanceof List ? val : null);
    }

    @Override
    public List<String> getStringList(String path) {
        List<?> list = this.getList(path);
        if (list == null) {
            return new ArrayList(0);
        } else {
            List<String> result = new ArrayList();
            Iterator var5 = list.iterator();
            while(true) {
                Object object;
                do {
                    if (!var5.hasNext()) {
                        return result;
                    }
                    object = var5.next();
                } while(!(object instanceof String) && !this.isPrimitiveWrapper(object));

                result.add(String.valueOf(object));
            }
        }
    }

    @Deprecated
    @Override
    public void saveItem(String path, ItemStack item) {
        setItem(path, item);
    }

    @Deprecated
    @Override
    public void saveItem(String path, String name, ItemStack itemStack) {
        setItem(path, name, itemStack);
    }

    @Override
    public void setItem(String path, ItemStack itemStack) {
        set(path, ItemUtils.serializeItemStack(itemStack));
    }

    @Override
    public void setItem(String path, String name, ItemStack itemStack) {
        setItem(path + "." + name, itemStack);
    }

    @Override
    public ItemStack getItem(String path) {
        return getItem(path, true);
    }

    @Override
    public ItemStack getItem(String path, boolean replaceKeys) {
        String data = getString(path);
        if(data != null && !data.isEmpty()){
            ItemStack itemStack = ItemUtils.deserializeItemStack(data);
            if (itemStack.hasItemMeta()) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta.hasDisplayName()) {
                    String displayName = itemMeta.getDisplayName();
                    if (replaceKeys && api.getLanguageAPI().getActiveLanguage() != null) {
                        displayName = api.getLanguageAPI().getActiveLanguage().replaceKeys(displayName);
                        itemMeta.setDisplayName(displayName);
                    }
                }
                if (itemMeta.hasLore()) {
                    List<String> newLore = new ArrayList<>();
                    if(replaceKeys){
                        for (String row : itemMeta.getLore()) {
                            if (api.getLanguageAPI().getActiveLanguage() != null) {
                                if (row.startsWith("[WU]")) {
                                    newLore.add(api.getLanguageAPI().getActiveLanguage().replaceKeys(row.substring("[WU]".length())));
                                } else if (row.startsWith("[WU!]")) {
                                    List<String> rows = api.getLanguageAPI().getActiveLanguage().replaceKey(row.substring("[WU!]".length()));
                                    for (String newRow : rows) {
                                        newLore.add(WolfyUtilities.translateColorCodes(newRow));
                                    }
                                }
                            }
                        }
                    }
                    itemMeta.setLore(newLore);
                }
                itemStack.setItemMeta(itemMeta);
            }
            return itemStack;
        }
        return new ItemStack(Material.STONE);
    }

    protected boolean isPrimitiveWrapper(@Nullable Object input) {
        return input instanceof Integer || input instanceof Boolean || input instanceof Character || input instanceof Byte || input instanceof Short || input instanceof Double || input instanceof Long || input instanceof Float;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return Type.JSON;
    }
}
