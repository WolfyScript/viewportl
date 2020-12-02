package me.wolfyscript.utilities.api.config;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ObjectNode;
import me.wolfyscript.utilities.api.utils.chat.ChatColor;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Deprecated
public class JsonConfiguration extends FileConfiguration {

    private final ObjectMapper mapper = JacksonUtil.getObjectMapper();
    private final HashMap<String, JsonPointer> cachedPointers = new HashMap<>();

    protected JsonNode root;

    private String defJsonString;

    private char pathSeparator = '.';

    /*
        Creates a Json Configuration file. The default is set inside of the jar at the specified path.
        path - the config file path
        filename - config file name without ".json" and the key of this config (if it's registered)
        defPath - file path of the file containing the defaults
        defFileName - file name of the file containing the defaults
        overwrite - set if the existing file should be replaced by the defaults
     */
    public JsonConfiguration(ConfigAPI configAPI, String path, String name, String defPath, String defFileName, boolean overwrite) {
        super(configAPI, path, name, defPath, defFileName, Type.JSON);
        this.map = new HashMap<>();
        this.root = mapper.createObjectNode();
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
        this.root = mapper.createObjectNode();
        this.defJsonString = jsonData;
        try {
            loadFromString(jsonData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /*
        Creates a memory only json config from a default saved in a File!
     */
    public JsonConfiguration(ConfigAPI configAPI, String name, String defPath, String defFileName) {
        this("{}", configAPI, name, defPath, defFileName);
        this.map = new HashMap<>();
        this.root = mapper.createObjectNode();
        loadDefaults();
    }

    /*
        Creates a memory only json config from already existing data and also adds default data if it is missing!
     */
    public JsonConfiguration(String jsonData, ConfigAPI configAPI, String name, String defPath, String defFileName) {
        super(configAPI, "", name, defPath, defFileName, Type.JSON);
        this.map = new HashMap<>();
        this.root = mapper.createObjectNode();
        try {
            loadFromString(jsonData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
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
        //gson.fieldNamingStrategy();

        if (defPath != null && defFileName != null && !defPath.isEmpty() && !defFileName.isEmpty()) {
            if (plugin.getResource(defPath + "/" + defFileName + ".json") != null) {
                HashMap<String, Object> defMap;
                try {
                    JsonNode node = mapper.readTree(new InputStreamReader(plugin.getResource(defPath + "/" + defFileName + ".json"), StandardCharsets.UTF_8));
                    defMap = mapper.readValue(new InputStreamReader(plugin.getResource(defPath + "/" + defFileName + ".json"), StandardCharsets.UTF_8), new TypeReference<HashMap<String, Object>>() {
                    });
                    if (overwrite) {
                        this.map.putAll(defMap);
                        this.root = node;
                    } else {
                        if (node.isObject()) {
                            applyDefaults("", (ObjectNode) node);
                        }
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
    public void applyDefaults(String pathKey, ObjectNode node) {
        node.fields().forEachRemaining(entry -> {
            String subPath = (pathKey.isEmpty() ? "" : (pathKey + getPathSeparator())) + entry.getKey();

            if (entry.getValue().isObject()) {
                applyDefaults(subPath, (ObjectNode) entry.getValue());
            } else {
                if (get(subPath) == null) {
                    set(subPath, entry.getValue());
                }
            }
        });
    }


    public void loadFromString(String jsonString) throws JsonProcessingException {
        root = mapper.readTree(jsonString);
        map = mapper.readValue(jsonString, new TypeReference<HashMap<String, Object>>() {});
    }

    public String toString(boolean prettyPrinting) {
        try {
            return prettyPrinting ? mapper.writerWithDefaultPrettyPrinter().writeValueAsString(root) : mapper.writeValueAsString(root);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public String toString() {
        return toString(false);
    }

    public void load() {
        if (linkedToFile()) {
            JsonNode node = null;
            try {
                node = mapper.readTree(this.configFile);
                this.root = node;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (node == null || node.isNull()) {
                    this.root = mapper.createObjectNode();
                }
            }
        }
    }

    public void save(boolean prettyPrinting) {
        if (linkedToFile()) {
            try {
                ObjectWriter writer = mapper.writer(prettyPrinting ? new DefaultPrettyPrinter() : null);
                writer.writeValue(configFile, root);
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
        this.root.fieldNames().forEachRemaining(keys::add);
        return keys;
    }

    public Set<String> parse(String currentPath, JsonNode jsonNode, Set<String> out) {
        jsonNode.fields().forEachRemaining(e -> {
            String path = (currentPath.isEmpty() ? "" : currentPath + getPathSeparator()) + e.getKey();
            if (e.getValue().isValueNode() || e.getValue().isArray()) {
                out.add(path);
            } else {
                parse(path, e.getValue(), out);
            }
        });
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
        JsonNode node = getJsonNode(path);
        if (node == null || node.isMissingNode() || node.isNull()) {
            return def;
        }
        return mapper.convertValue(node, type);
    }

    /**
     * @param path
     * @param value
     */
    public void set(String path, Object value, boolean useDotSeperator) {
        try {
            setNode(path, JacksonUtil.getObjectMapper().convertValue(value, JsonNode.class), useDotSeperator);
        } catch (IllegalArgumentException ex) {
            System.out.println("Couldn't convert value to JsonNode!");
        }
    }

    /**
     * @param path
     * @param value
     */
    @Override
    public void set(String path, Object value) {
        set(path, value, true);
    }

    public void setPOJO(String path, Object value, boolean useDotSeperator) {
        try {
            if(useDotSeperator){
                path = path.replace(pathSeparator, '/');
            }
            JsonPointer pointer;
            if(cachedPointers.containsKey(path)){
                pointer = cachedPointers.get(path);
            }else{
                pointer = JsonPointer.compile((path.startsWith("/") ? "" : "/")+path);
                cachedPointers.put(path, pointer);
            }
            ObjectNode node = JacksonUtil.getObjectMapper().convertValue(this.root.at(pointer.head()), ObjectNode.class);
            if(node != null && node.isObject()){
                node.putPOJO(pointer.last().toString().substring(1), value);
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("Parent node is not a ObjectNode!");
        }
    }

    public void setNode(String path, JsonNode value){
        setNode(path, value, true);
    }

    public void setNode(String path, JsonNode value, boolean useDotSeperator) {
        try {
            if(useDotSeperator){
                path = path.replace(pathSeparator, '/');
            }
            JsonPointer pointer;
            if(cachedPointers.containsKey(path)){
                pointer = cachedPointers.get(path);
            }else{
                pointer = JsonPointer.compile((path.startsWith("/") ? "" : "/")+path);
                cachedPointers.put(path, pointer);
            }
            JsonPointer head = pointer.head();
            JsonNode testNode;
            do{
                testNode = this.root.at(head);
                head = head.head();
            }while(testNode.isMissingNode());

            ObjectNode node = JacksonUtil.getObjectMapper().convertValue(this.root.at(pointer.head()), ObjectNode.class);
            if(node != null && node.isObject()){
                node.set(pointer.last().toString().substring(1), value);
            }
        } catch (IllegalArgumentException ex) {
            System.out.println("Parent node is not a ObjectNode!");
        }
    }

    /**
     *  This method will replace all occurrences of the {@link #pathSeparator} with '/',<p>
     *  <b>Only use this method when you are sure that the field names
     *  don't contain the character of {@link #pathSeparator}!</b>
     *  <p>
     *  Else use {@link #getJsonNode(String, boolean)} instead!
     *
     * @param path
     * @return
     */
    public JsonNode getJsonNode(String path) {
        return getJsonNode(path, true);
    }

    /**
     * This method will replace all occurrences of the {@link #pathSeparator} with '/',<p>
     * if useDotSeperator is set to true.<p>
     * <b>Only set <a>useDotSeperator</a> to true when you are sure that the field names
     * don't contain the character of {@link #pathSeparator}!</b>
     *
     * @param path the path of the JsonNode
     * @param useDotSeperator use the {@link #pathSeparator} instead of '/'. Replaces all occurrences of {@link #pathSeparator} with '/'
     * @return The JsonNode at the specific path or {@link TreeNode#isMissingNode()}
     */
    public JsonNode getJsonNode(@NotNull String path, boolean useDotSeperator) {
        try {
            if(useDotSeperator){
                path = path.replace(pathSeparator, '/');
            }
            JsonPointer pointer;
            if(cachedPointers.containsKey(path)){
                pointer = cachedPointers.get(path);
            }else{
                pointer = JsonPointer.compile(path.startsWith("/") ? "" : "/"+path);
                cachedPointers.put(path, pointer);
            }
            return JacksonUtil.getObjectMapper().convertValue(this.root.at(pointer), JsonNode.class);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        }
        return mapper.missingNode();
    }

    @Override
    public String getString(String path) {
        return getString(path, "");
    }

    @Override
    public String getString(String path, String def) {
        return getJsonNode(path).asText(def);
    }

    @Override
    public int getInt(String path) {
        return getInt(path, 0);
    }

    @Override
    public int getInt(String path, int def) {
        return getJsonNode(path).asInt(def);
    }

    @Override
    public boolean getBoolean(String path) {
        return getJsonNode(path).booleanValue();
    }

    @Override
    public double getDouble(String path) {

        return getDouble(path, 0.0d);
    }

    @Override
    public double getDouble(String path, double def) {
        return getJsonNode(path).asDouble(def);
    }

    @Override
    public long getLong(String path) {
        return getLong(path, 0);
    }

    @Override
    public long getLong(String path, long def) {
        return getJsonNode(path).asLong(def);
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
                    itemMeta.setDisplayName(ChatColor.convert(replaceKeys && api.getLanguageAPI().getActiveLanguage() != null ? api.getLanguageAPI().replaceKeys(itemMeta.getDisplayName()) : itemMeta.getDisplayName()));
                }
                if (itemMeta.hasLore() && replaceKeys && api.getLanguageAPI().getActiveLanguage() != null) {
                    itemMeta.setLore(itemMeta.getLore().stream().map(ChatColor::convert).collect(Collectors.toList()));
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
