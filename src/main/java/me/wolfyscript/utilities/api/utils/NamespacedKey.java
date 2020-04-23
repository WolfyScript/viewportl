package me.wolfyscript.utilities.api.utils;

import com.google.common.base.Preconditions;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.regex.Pattern;

public class NamespacedKey {

    private static final Pattern VALID_NAMESPACE = Pattern.compile("[a-z0-9._-]+");
    private static final Pattern VALID_KEY = Pattern.compile("[a-z0-9/._-]+");
    private final String namespace;
    private final String key;

    public NamespacedKey(String namespace, String key) {
        Preconditions.checkArgument(namespace != null && VALID_NAMESPACE.matcher(namespace).matches(), "Invalid namespace. Must be [a-z0-9._-]: %s", namespace);
        Preconditions.checkArgument(key != null && VALID_KEY.matcher(key).matches(), "Invalid key. Must be [a-z0-9/._-]: %s", key);
        this.namespace = namespace;
        this.key = key;
        String string = this.toString();
        Preconditions.checkArgument(string.length() < 256, "NamespacedKey must be less than 256 characters", string);
    }

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        } else if (this.getClass() != obj.getClass()) {
            return false;
        } else {
            NamespacedKey other = (NamespacedKey) obj;
            return this.namespace.equals(other.namespace) && this.key.equals(other.key);
        }
    }

    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + this.namespace.hashCode();
        hash = 47 * hash + this.key.hashCode();
        return hash;
    }

    public String toString() {
        return this.namespace + ":" + this.key;
    }
}
