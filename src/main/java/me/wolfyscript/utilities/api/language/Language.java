package me.wolfyscript.utilities.api.language;

import com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Language {

    private final String lang;
    private JsonNode root;

    private final HashMap<String, JsonNode> cachedNodes = new HashMap<>();

    public Language(Plugin plugin, String lang) throws IOException {
        this.lang = lang;
        File file = new File(plugin.getDataFolder(), "lang/" + lang + ".json");
        file.getParentFile().mkdirs();
        this.root = null;
        if (file.exists() || file.createNewFile()) {
            this.root = JacksonUtil.getObjectMapper().readTree(file);
        }
        if (this.root == null) {
            throw new IOException("Couldn't load language from file!");
        }
    }

    JsonNode getNodeAt(String path) {
        if (cachedNodes.containsKey(path)) {
            return cachedNodes.get(path);
        }
        String[] keys = path.split("\\.");
        JsonNode currentNode = this.root;
        for (String key : keys) {
            currentNode = currentNode.path(key);
        }
        cachedNodes.put(path, currentNode);
        return currentNode;
    }

    public String getName() {
        return lang;
    }

    public String getVersion(){
        return this.root.path("version").asText();
    }

    public List<String> getAuthors() {
        List<String> list = new ArrayList<>();
        JsonNode node = this.root.path("author");
        if (node.isTextual()) {
            list.add(node.asText());
        } else {
            node.elements().forEachRemaining(n -> list.add(n.asText()));
        }
        return list;
    }
}
