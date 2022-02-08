/*
 *       WolfyUtilities, APIs and Utilities for Minecraft Spigot plugins
 *                      Copyright (C) 2021  WolfyScript
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.wolfyscript.utilities.util;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.api.WolfyUtilities;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    private final boolean hasPlugin;
    private final String namespace;
    private final Key key;

    /**
     * <b>Only for internal use. Must be converted to a namespaced key with your plugins name as the namespaced key, if you register data in WolfyUtilities!</b>
     *
     * @param namespace The namespace, that fits the pattern [a-z0-9._-]
     * @param key       The key that fits the pattern [a-z0-9/._-]
     */
    public NamespacedKey(String namespace, String key) {
        Preconditions.checkArgument(namespace != null && VALID_NAMESPACE.matcher(namespace).matches(), "Invalid namespace. Must be [a-z0-9._-]: %s", namespace);
        this.key = new Key(key.toLowerCase(Locale.ROOT));
        this.namespace = namespace;
        this.hasPlugin = false;
        var string = this.toString();
        Preconditions.checkArgument(string.length() < 256, "NamespacedKey must be less than 256 characters", string);
    }

    /**
     * @param plugin The plugin that this data belongs to
     * @param key    The key that fits the pattern [a-z0-9/._-]
     */
    public NamespacedKey(@NotNull Plugin plugin, @NotNull String key) {
        Preconditions.checkArgument(plugin != null, "Plugin cannot be null");
        Preconditions.checkArgument(key != null, "Key cannot be null");
        this.hasPlugin = true;
        this.namespace = plugin.getName().toLowerCase(Locale.ROOT);
        Preconditions.checkArgument(VALID_NAMESPACE.matcher(this.namespace).matches(), "Invalid namespace. Must be [a-z0-9._-]: %s", this.namespace);
        this.key = new Key(key.toLowerCase(Locale.ROOT));
        String string = this.toString();
        Preconditions.checkArgument(string.length() < 256, "NamespacedKey must be less than 256 characters (%s)", string);
    }

    /**
     * Gets the namespace of this object.
     *
     * @return The namespace.
     */
    @NotNull
    public String getNamespace() {
        return this.namespace;
    }

    /**
     * Gets the key of this object as a String.
     *
     * @return The key.
     */
    @NotNull
    public String getKey() {
        return this.key.toString();
    }

    /**
     * Gets the key part of this NamespacedKey.
     *
     * @return The key part.
     * @since 3.16.1.0
     */
    @ApiStatus.AvailableSince(value = "3.16.1.0")
    public Key getKeyComponent() {
        return this.key;
    }

    /**
     * @param namespaceKey The String with a column.
     * @return The NamespacedKey of the String or null, if the String doesn't contain a ":".
     */
    @Nullable
    public static NamespacedKey of(@Nullable String namespaceKey) {
        if (namespaceKey == null || namespaceKey.isEmpty()) return null;
        String[] parts = namespaceKey.split(":", 2);
        if (parts.length == 0) return null;
        if (parts.length > 1) {
            return new NamespacedKey(parts[0].toLowerCase(Locale.ROOT), parts[1].toLowerCase(Locale.ROOT));
        } else {
            return wolfyutilties(parts[0]);
        }
    }

    /**
     * Creates the bukkit representation of this object.
     *
     * @return The Bukkit NamespacedKey.
     */
    public org.bukkit.NamespacedKey bukkit() {
        return new org.bukkit.NamespacedKey(this.namespace, this.getKey());
    }

    /**
     * Creates a new NamespacedKey from the specified Bukkit NamespacedKey.<br>
     * <strong>This is not compatible with {@link #toBukkit()} or {@link #toBukkit(Plugin)}! Therefor those are deprecated and {@link #bukkit()} should be used instead!</strong>
     *
     * @param namespacedKey The bukkit NamespacedKey.
     * @return A new NamespacedKey with the same namespace and key as the Bukkit representation.
     */
    public static NamespacedKey fromBukkit(org.bukkit.NamespacedKey namespacedKey) {
        return new NamespacedKey(namespacedKey.getNamespace(), namespacedKey.getKey());
    }

    @Deprecated
    public org.bukkit.NamespacedKey toBukkit(Plugin plugin) {
        return new org.bukkit.NamespacedKey(plugin, this.namespace + BUKKIT_SPLITTER + this.getKey());
    }

    @Deprecated
    public org.bukkit.NamespacedKey toBukkit() {
        return toBukkit(WolfyUtilities.getWUPlugin());
    }

    public static NamespacedKey wolfyutilties(String key) {
        return new NamespacedKey(WolfyUtilCore.getInstance(), key);
    }

    public boolean hasPlugin() {
        return hasPlugin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NamespacedKey that)) return false;
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
        return getNamespace() + split + getKey();
    }

    @Override
    public int compareTo(@NotNull NamespacedKey namespacedKey) {
        int namespaceDifference = getNamespace().compareTo(namespacedKey.getNamespace());
        return namespaceDifference == 0 ? getKey().compareTo(namespacedKey.getKey()) : namespaceDifference;
    }

    /**
     * Represents the key part of the NamespacedKey.
     * The idea behind this component is to make it easier to manage folders specified in the key and provide util methods for it.<br>
     * Such as
     * <pre>
     *     "folder/sub_folder/another/file"</pre>
     * is converted into
     * <pre>
     *     folder:  "folder/sub_folder/another"
     *     key:     "file"
     * </pre>
     */
    @ApiStatus.AvailableSince(value = "3.16.1.0")
    public static final class Key {

        private final String folder;
        private final String key;

        private Key(String keyString) {
            Preconditions.checkArgument(VALID_KEY.matcher(keyString).matches(), "Invalid key. Must be [a-z0-9/._-]: %s", keyString);
            if (keyString.contains("/")) {
                String[] args = keyString.split("/(?!.*/)");
                if (args.length > 1) {
                    this.folder = args[0];
                    this.key = args[1];
                    return;
                } else if (args.length == 1) {
                    this.folder = "";
                    this.key = args[0];
                    return;
                }
            }
            this.key = keyString;
            this.folder = "";
        }

        public String getFolder() {
            return folder;
        }

        public String getKey() {
            return key;
        }

        public String getRoot() {
            return folder.substring(0, folder.indexOf("/"));
        }

        @Override
        public String toString() {
            return toString("/");
        }

        public String toString(String separator) {
            return toString(separator, false);
        }

        public String toString(String separator, boolean forceSeparator) {
            if (separator == null || separator.isEmpty()) {
                separator = "/";
            }
            return folder.isBlank() ? ((forceSeparator ? separator : "") + key) : (folder + separator + key);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key that = (Key) o;
            return Objects.equals(folder, that.folder) && Objects.equals(key, that.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(folder, key);
        }
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
