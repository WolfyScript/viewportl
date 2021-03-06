package me.wolfyscript.utilities.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

@JsonDeserialize(using = NamespacedKey.Deserializer.class, keyUsing = NamespacedKey.KeyDeserializer.class)
public class NamespacedKey implements Comparable<NamespacedKey> {

    public static final String WOLFYUTILITIES = "wolfyutilities";
    private static final String BUKKIT_SPLITTER = "/";

    @JsonIgnore
    private static final Pattern VALID_NAMESPACE = Pattern.compile("[a-z0-9._-]+");
    @JsonIgnore
    private static final Pattern VALID_KEY = Pattern.compile("[a-z0-9/._-]+");
    private final String namespace;
    private final String key;

    /**
     * <b>Only for internal use. Must be converted to a namespaced key with your plugins name as the namespaced key, if you register data in WolfyUtilities!</b>
     *
     * @param namespace The namespace, that fits the pattern [a-z0-9._-]
     * @param key       The key that fits the pattern [a-z0-9/._-]
     */
    public NamespacedKey(String namespace, String key) {
        Preconditions.checkArgument(namespace != null && VALID_NAMESPACE.matcher(namespace).matches(), "Invalid namespace. Must be [a-z0-9._-]: %s", namespace);
        Preconditions.checkArgument(key != null && VALID_KEY.matcher(key).matches(), "Invalid key. Must be [a-z0-9/._-]: %s", key);
        this.namespace = namespace;
        this.key = key;
        String string = this.toString();
        Preconditions.checkArgument(string.length() < 256, "NamespacedKey must be less than 256 characters", string);
    }

    /**
     * @param plugin The plugin that this data belongs to
     * @param key    The key that fits the pattern [a-z0-9/._-]
     */
    public NamespacedKey(@NotNull Plugin plugin, @NotNull String key) {
        Preconditions.checkArgument(plugin != null, "Plugin cannot be null");
        Preconditions.checkArgument(key != null, "Key cannot be null");
        this.namespace = plugin.getName().toLowerCase(Locale.ROOT);
        this.key = key.toLowerCase(Locale.ROOT);
        Preconditions.checkArgument(VALID_NAMESPACE.matcher(this.namespace).matches(), "Invalid namespace. Must be [a-z0-9._-]: %s", this.namespace);
        Preconditions.checkArgument(VALID_KEY.matcher(this.key).matches(), "Invalid key. Must be [a-z0-9/._-]: %s", this.key);
        String string = this.toString();
        Preconditions.checkArgument(string.length() < 256, "NamespacedKey must be less than 256 characters (%s)", string);
    }

    @Nonnull
    public String getNamespace() {
        return this.namespace;
    }

    @Nonnull
    public String getKey() {
        return this.key;
    }

    /**
     * @param namespaceKey The String with a column.
     * @return The NamespacedKey of the String or null, if the String doesn't contain a ":".
     */
    @Nullable
    public static NamespacedKey of(@Nullable String namespaceKey) {
        if (namespaceKey == null || namespaceKey.isEmpty()) return null;
        return new NamespacedKey(namespaceKey.split(":")[0].toLowerCase(Locale.ROOT), namespaceKey.split(":")[1].toLowerCase(Locale.ROOT));
    }

    /**
     * @param namespacedKey The Bukkit NamespacedKey
     * @return the WolfyUtilities copy of the Bukkit namespaced key
     * @deprecated This method does some weird formatting and is messed up. use {@link #fromBukkit(org.bukkit.NamespacedKey)} instead!
     */
    @Deprecated
    public static NamespacedKey of(org.bukkit.NamespacedKey namespacedKey) {
        if (namespacedKey.getNamespace().equalsIgnoreCase(WOLFYUTILITIES)) {
            /*TODO:
               This only works with CutomCrafting... What if there are other keys with "/", but are not an actual namespace that should be used?
               e.g.: an item created via WolfyUtilities "wolfyutilities:group/item" would be converted to "group:item", which is wrong!
               To convert them to bukkit and back you need to use #toBukkit(), which messes up the key: "wolfyutilities:wolfyutilities/group/item"!
               -
               Change the way this is handled!
               E.g. CustomCrafting should also use "customcrafting:<userdefined_namespace>/<userdefined_key>" instead of "<userdefined_namespace>:<userdefined_key>" to detect which plugin the namespacedkey belongs to!
             */
            String[] values = namespacedKey.getKey().split(BUKKIT_SPLITTER, 2);
            if (values.length > 1) {
                return new NamespacedKey(values[0], values[1]);
            }
        }
        return fromBukkit(namespacedKey);
    }

    public static NamespacedKey fromBukkit(org.bukkit.NamespacedKey namespacedKey) {
        return new NamespacedKey(namespacedKey.getNamespace(), namespacedKey.getKey());
    }


    public static NamespacedKey wolfyutilties(String key) {
        return new NamespacedKey(WOLFYUTILITIES, key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamespacedKey)) return false;
        NamespacedKey that = (NamespacedKey) o;
        return Objects.equals(namespace, that.namespace) &&
                Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, key);
    }

    @JsonValue
    @Override
    public String toString() {
        return toString(":");
    }

    public String toString(String split) {
        if (split == null || split.isEmpty()) {
            split = ":";
        }
        return this.namespace + split + this.key;
    }

    public org.bukkit.NamespacedKey toBukkit() {
        return toBukkit(WolfyUtilities.getWUPlugin());
    }

    public org.bukkit.NamespacedKey toBukkit(Plugin plugin) {
        return new org.bukkit.NamespacedKey(plugin, this.namespace + BUKKIT_SPLITTER + this.getKey());
    }

    @Override
    public int compareTo(@NotNull NamespacedKey namespacedKey) {
        int namespaceDifference = getNamespace().compareTo(namespacedKey.getNamespace());
        return namespaceDifference == 0 ? getKey().compareTo(namespacedKey.getKey()) : namespaceDifference;
    }

    static class Deserializer extends JsonDeserializer<NamespacedKey> {

        @Override
        public NamespacedKey deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            return NamespacedKey.of(jsonParser.readValueAs(String.class));
        }
    }

    static class KeyDeserializer extends com.fasterxml.jackson.databind.KeyDeserializer {

        @Override
        public NamespacedKey deserializeKey(String s, DeserializationContext deserializationContext) throws IOException {
            return NamespacedKey.of(s);
        }
    }
}
