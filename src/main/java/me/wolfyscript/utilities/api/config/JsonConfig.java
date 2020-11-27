package me.wolfyscript.utilities.api.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectWriter;
import me.wolfyscript.utilities.api.utils.json.jackson.JacksonUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class JsonConfig {

    private final HashMap<String, JsonNode> cachedNodes = new HashMap<>();
    protected File file;
    protected JsonNode root;

    /**
     * This allows you to configure your own function to load the JsonNode from the file.
     * The function is only called when the file exists.
     * <br/>
     * If you don't want to use a file use {@link #JsonConfig(File, Supplier)} instead.
     *
     * @param file             The file to load the config from. Must not be null!
     * @param jsonNodeFunction The function to load the JsonNode from the file. Only called when the file exists!
     */
    public JsonConfig(@NotNull File file, Function<File, JsonNode> jsonNodeFunction) {
        this.file = file;
        try {
            file.getParentFile().mkdirs();
            if (file.exists() || file.createNewFile()) {
                this.root = jsonNodeFunction.apply(file);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(root, "Can't load config! Root node cannot be null!");
    }

    /**
     * This allows you to configure your own supplier. Other than in {@link #JsonConfig(File, Function)} the supplier is also called when the file is null.
     * If you want to set the file on save or just want it to use for memory only, then you can use this constructor.
     * {@link }
     *
     * @param file             The file to load the config from. Can be null!
     * @param jsonNodeSupplier The supplier that supplies the config with the root JsonNode.
     */
    public JsonConfig(@Nullable File file, Supplier<JsonNode> jsonNodeSupplier) {
        this.file = file;
        this.root = jsonNodeSupplier.get();
        Objects.requireNonNull(root, "Can't load config! Root node cannot be null!");
    }

    /**
     * @param file
     * @throws IOException
     */
    public JsonConfig(@NotNull File file) {
        this(file, file1 -> {
            try {
                return JacksonUtil.getObjectMapper().readTree(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * @param file
     * @param initialValue
     * @throws IOException
     */
    public JsonConfig(@Nullable File file, @NotNull String initialValue) {
        this(file, () -> {
            try {
                return JacksonUtil.getObjectMapper().readTree(initialValue);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    /**
     * @param initialValue
     * @throws IOException
     */
    public JsonConfig(@NotNull String initialValue) {
        this(null, initialValue);
    }

    /**
     * @param root
     * @throws IOException
     */
    public JsonConfig(@NotNull JsonNode root) {
        this(null, () -> root);
    }

    protected JsonNode getNodeAt(String path) {
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

    public void save(File file, ObjectWriter objectWriter) throws IOException {
        if (file != null) {
            if (file.exists() || file.createNewFile()) {
                objectWriter.writeValue(file, this.root);
            } else {
                throw new IOException("Couldn't create config file on save!");
            }
        }
    }

    public void save(ObjectWriter objectWriter) throws IOException {
        save(this.file, objectWriter);
    }

    public void save(File file, boolean prettyPrint) throws IOException {
        save(file, JacksonUtil.getObjectWriter(prettyPrint));
    }

    public void save(boolean prettyPrint) throws IOException {
        save(JacksonUtil.getObjectWriter(prettyPrint));
    }

    public void save(File file) throws IOException {
        save(file, false);
    }

    public void save() throws IOException {
        save(false);
    }

    public void reload() {

    }

}
