package me.wolfyscript.utilities.api.config;

import com.google.gson.*;
import com.google.gson.internal.bind.TypeAdapters;
import me.wolfyscript.utilities.api.WolfyUtilities;
import me.wolfyscript.utilities.api.utils.GsonUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.NumberConversions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class JsonConfiguration extends FileConfiguration {

    private final Gson gson = GsonUtil.getGson();

    private JsonObject root;

    private String defJsonString;

    private char pathSeparator = '.';

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
        this.map = new HashMap<>();
        this.root = new JsonObject();
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdirs();
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            onFirstInit();
        }
        load();
        loadDefaults(overwrite);
        reload();
        init();
    }

    public JsonConfiguration(ConfigAPI configAPI, String fileName) {
        this(configAPI, "{}", fileName);
    }

    /*
        Creates a jsonConfig with a jsonString as the default instead of a File. Saved in a File (path & filename)
        name - config file name without ".json" and the key of this config (if it's registered)
        jsonString - the default json value.
        path - the config file path without ".json" at the end!
        overwrite - set if the existing file should be replaced by the defaults
     */
    public JsonConfiguration(ConfigAPI configAPI, String jsonString, String path, String name, boolean overwrite) {
        this(configAPI, path, name, "", "", overwrite);
        this.defJsonString = jsonString;
    }

    /*
        Creates a memory only json config!
     */
    public JsonConfiguration(ConfigAPI configAPI, String jsonData, String name) {
        super(configAPI, "", name, "", "", Type.JSON);
        this.map = new HashMap<>();
        this.root = new JsonObject();
        this.defJsonString = jsonData;
        loadFromString(jsonData);
    }

    /*
        Creates a memory only json config from a default saved in a File!
     */
    public JsonConfiguration(ConfigAPI configAPI, String name, String defPath, String defFileName) {
        this("{}", configAPI, name, defPath, defFileName);
        this.map = new HashMap<>();
        this.root = new JsonObject();
        loadDefaults();
    }

    /*
        Creates a memory only json config from already existing data and also adds default data if it is missing!
     */
    public JsonConfiguration(String jsonData, ConfigAPI configAPI, String name, String defPath, String defFileName) {
        super(configAPI, "", name, defPath, defFileName, Type.JSON);
        this.map = new HashMap<>();
        this.root = new JsonObject();
        loadFromString(jsonData);
        loadDefaults();
    }

    /*
      Called when the config file didn't exist or whenever the config gets overwritten!
    */
    public void onFirstInit() {

    }

    public void init() {

    }

    public boolean hasPathSeparator() {
        return this.pathSeparator != 0;
    }

    public void setPathSeparator(char pathSeparator) {
        this.pathSeparator = pathSeparator;
    }

    public char getPathSeparator() {
        return pathSeparator;
    }

    public void loadDefaults() {
        loadDefaults(false);
    }

    public void loadDefaults(boolean overwrite) {
        gson.fieldNamingStrategy();
        if (defPath != null && defFileName != null && !defPath.isEmpty() && !defFileName.isEmpty()) {
            if (plugin.getResource(defPath + "/" + defFileName + ".json") != null) {
                HashMap<String, Object> defMap = null;
                try {
                    JsonObject jsonObject = (JsonObject) TypeAdapters.JSON_ELEMENT.fromJson(new InputStreamReader(plugin.getResource(defPath + "/" + defFileName + ".json"), "UTF-8"));
                    defMap = gson.fromJson(new InputStreamReader(plugin.getResource(defPath + "/" + defFileName + ".json"), "UTF-8"), new HashMap<String, JsonElement>().getClass());
                    if (overwrite) {
                        this.map.putAll(defMap);
                        this.root = jsonObject;
                    } else {
                        applyDefaults("", jsonObject);
                        for (Map.Entry<String, Object> entry : defMap.entrySet()) {
                            this.map.putIfAbsent(entry.getKey(), entry.getValue());
                        }
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                PrintWriter pw = new PrintWriter(configFile, "UTF-8");
                pw.write(defJsonString);
                pw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    /*
    {
        "test":{
            "hi": "hi"
            "gg": [

            ]
        },
        "ggF": 45

    }

     */
    public void applyDefaults(String pathKey, JsonObject jsonObject) {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String subPath = (pathKey.isEmpty() ? "" : (pathKey + getPathSeparator())) + entry.getKey();
            if (entry.getValue() instanceof JsonObject) {
                applyDefaults(subPath, (JsonObject) entry.getValue());
            } else {
                if (get(subPath) == null) {
                    set(subPath, entry.getValue());
                }
            }
        }
    }


    public void loadFromString(String json) {
        root = gson.fromJson(json, JsonObject.class);
        map = gson.fromJson(json, new HashMap<String, Object>().getClass());
    }

    public String toString(boolean prettyPrinting) {
        return GsonUtil.getGson(prettyPrinting).toJson(root);
    }

    public String toString() {
        return toString(false);
    }

    public void load() {
        if (linkedToFile()) {
            try {
                try {
                    JsonElement object = gson.fromJson(new InputStreamReader(new FileInputStream(this.configFile), "UTF-8"), JsonElement.class);
                    if (object instanceof JsonObject) {
                        this.root = (JsonObject) object;
                        if (root == null) {
                            this.root = new JsonObject();
                        }
                    }
                } catch (JsonSyntaxException | JsonIOException | IOException ex) {
                    ex.printStackTrace();
                }
                this.map = gson.fromJson(new InputStreamReader(new FileInputStream(this.configFile), "UTF-8"), new HashMap<String, Object>().getClass());
                if (map == null) {
                    this.map = new HashMap<>();
                }
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public void save(boolean prettyPrinting) {
        if (linkedToFile()) {
            final String json = toString(prettyPrinting);
            try {
                PrintWriter pw = new PrintWriter(configFile, "UTF-8");
                pw.close();
            } catch (FileNotFoundException | UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                Files.write(configFile.toPath(), json.getBytes("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.WRITE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        save(configAPI.isPrettyPrinting());
    }

    public void reload() {
        save();
        load();
    }

    public void reload(boolean prettyPrinting) {
        save(prettyPrinting);
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
        return getKeys(false);
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        if (deep) {
            return parse("", this.root, new HashSet<>());
        }
        Set<String> keys = new HashSet<>();
        this.root.entrySet().forEach(entry -> keys.add(entry.getKey()));
        return keys;
    }

    public Set<String> parse(String currentPath, JsonObject jsonObject, Set<String> out) {
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            String path = (currentPath.isEmpty() ? "" : currentPath + getPathSeparator()) + entry.getKey();
            if (entry.getValue() instanceof JsonObject) {
                parse(path, (JsonObject) entry.getValue(), out);
            } else {
                out.add(path);
            }
        }
        return out;
    }

    @Override
    public Map<String, Object> getMap() {
        return map;
    }

    /*
            Gets the Object of the specified path.
            In this example the name would be accessed with get("crafting.workbench.name");
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
    @Override
    public Object get(String path) {
        return get(path, null);
    }

    public <T> T get(Class<T> type, String path) {
        return get(type, path, null);
    }

    public Object get(String path, @Nullable Object def) {
        return get(Object.class, path, def);
    }

    public <T> T get(Class<T> type, String path, @Nullable T def) {
        String[] pathKeys = path.split(pathSeparator == '.' ? "\\." : pathSeparator + "");
        JsonObject jsonObject = root;
        for (int i = 0; i < pathKeys.length; i++) {
            if (jsonObject.has(pathKeys[i])) {
                JsonElement element = jsonObject.get(pathKeys[i]);
                if (i != pathKeys.length - 1) {
                    if (element instanceof JsonObject) {
                        jsonObject = (JsonObject) element;
                    } else {
                        return null;
                    }
                } else {
                    return gson.fromJson(element, type);
                }
            }else{
                return def;
            }
        }
        return def;
    }

    @Override
    public void set(String path, Object value) {
        String[] pathKeys = path.split(pathSeparator == '.' ? "\\." : pathSeparator + "");
        JsonObject jsonObject = root;
        for (int i = 0; i < pathKeys.length; i++) {
            if (!jsonObject.has(pathKeys[i]) && i != pathKeys.length - 1) {
                jsonObject.add(pathKeys[i], new JsonObject());
                jsonObject = jsonObject.getAsJsonObject(pathKeys[i]);
            } else {
                JsonElement element = jsonObject.get(pathKeys[i]);
                if (i == pathKeys.length - 1) {
                    jsonObject.add(pathKeys[i], gson.toJsonTree(value));
                    if (saveAfterValueSet) {
                        reload();
                    }
                } else {
                    if (element instanceof JsonObject) {
                        jsonObject = (JsonObject) element;
                    }
                }
            }
        }
    }

    @Override
    public String getString(String path) {
        return getString(path, "");
    }

    @Override
    public String getString(String path, String def) {
        if (get(path) != null) {
            return get(path).toString();
        }
        return def;
    }

    @Override
    public int getInt(String path) {
        return getInt(path, 0);
    }

    @Override
    public int getInt(String path, int def) {
        Object val = this.get(path);
        return val instanceof Number ? NumberConversions.toInt(get(path)) : def;
    }

    @Override
    public boolean getBoolean(String path) {
        Object val = this.get(path);
        return val instanceof Boolean && (Boolean) val;
    }

    @Override
    public double getDouble(String path) {
        return getDouble(path, 0.0d);
    }

    @Override
    public double getDouble(String path, double def) {
        Object val = this.get(path);
        return val instanceof Number ? NumberConversions.toDouble(val) : def;
    }

    @Override
    public long getLong(String path) {
        return getLong(path, 0);
    }

    @Override
    public long getLong(String path, long def) {
        Object val = this.get(path);
        return val instanceof Number ? NumberConversions.toLong(val) : def;
    }

    @Override
    public List<?> getList(String path) {
        Object val = this.get(path);
        return (List) (val instanceof List ? val : null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getStringList(String path) {
        List<?> list = this.getList(path);
        if (list == null) {
            return new ArrayList();
        } else {
            List<String> result = new ArrayList();
            Iterator var5 = list.iterator();
            while (true) {
                Object object;
                do {
                    if (!var5.hasNext()) {
                        return result;
                    }
                    object = var5.next();
                } while (!(object instanceof String) && !this.isPrimitiveWrapper(object));

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
        set(path, itemStack);
    }

    @Override
    public void setItem(String path, String name, ItemStack itemStack) {
        setItem(path + "." + name, itemStack);
    }

    @Override
    public ItemStack getItem(String path) {
        return getItem(path, true);
    }

    @Nullable
    @Override
    public ItemStack getItem(String path, boolean replaceKeys) {
        ItemStack itemStack = get(ItemStack.class, path);
        if (itemStack != null) {
            if (itemStack.hasItemMeta()) {
                ItemMeta itemMeta = itemStack.getItemMeta();
                if (itemMeta.hasDisplayName()) {
                    String displayName = itemMeta.getDisplayName();
                    if (replaceKeys && api.getLanguageAPI().getActiveLanguage() != null) {
                        displayName = api.getLanguageAPI().getActiveLanguage().replaceKeys(displayName);
                    }
                    itemMeta.setDisplayName(WolfyUtilities.translateColorCodes(displayName));
                }
                if (itemMeta.hasLore() && replaceKeys && api.getLanguageAPI().getActiveLanguage() != null) {
                    List<String> newLore = new ArrayList<>();
                    for (String row : itemMeta.getLore()) {
                        if (row.startsWith("[WU]")) {
                            newLore.add(api.getLanguageAPI().getActiveLanguage().replaceKeys(row.substring("[WU]".length())));
                        } else if (row.startsWith("[WU!]")) {
                            for (String newRow : api.getLanguageAPI().getActiveLanguage().replaceKey(row.substring("[WU!]".length()))) {
                                newLore.add(WolfyUtilities.translateColorCodes(newRow));
                            }
                        } else {
                            newLore.add(row);
                        }
                    }
                    itemMeta.setLore(newLore);
                }
                itemStack.setItemMeta(itemMeta);
            }
            return itemStack;
        }
        return null;
    }

    @Override
    public Map<String, Object> getValues(String path) {
        if (path.isEmpty()) {
            return getValues();
        }
        return (Map<String, Object>) get(path, new HashMap<>());
    }

    protected boolean isPrimitiveWrapper(@Nullable Object input) {
        return input instanceof Integer || input instanceof Boolean || input instanceof Character || input instanceof Byte || input instanceof Short || input instanceof Double || input instanceof Long || input instanceof Float;
    }
}
