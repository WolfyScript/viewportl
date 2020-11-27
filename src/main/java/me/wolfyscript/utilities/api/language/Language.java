package me.wolfyscript.utilities.api.language;

import com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.api.config.JsonConfig;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Language extends JsonConfig {

    private final String lang;

    public Language(Plugin plugin, String lang) {
        super(new File(plugin.getDataFolder(), "lang/" + lang + ".json"));
        this.lang = lang;
    }

    public String getName() {
        return lang;
    }

    public String getVersion() {
        return root.path("version").asText();
    }

    public List<String> getAuthors() {
        List<String> list = new ArrayList<>();
        JsonNode node = root.path("author");
        if (node.isTextual()) {
            list.add(node.asText());
        } else {
            node.elements().forEachRemaining(n -> list.add(n.asText()));
        }
        return list;
    }
}
