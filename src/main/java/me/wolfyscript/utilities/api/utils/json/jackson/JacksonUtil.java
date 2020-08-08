package me.wolfyscript.utilities.api.utils.json.jackson;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JacksonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static ObjectWriter getObjectWriter(boolean prettyPrinting) {
        return objectMapper.writer(prettyPrinting ? new DefaultPrettyPrinter() : null);
    }

    public static void registerModule(Module module) {
        objectMapper.registerModule(module);
    }

}
