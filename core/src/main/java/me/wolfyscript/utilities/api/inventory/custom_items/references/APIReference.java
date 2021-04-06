package me.wolfyscript.utilities.api.inventory.custom_items.references;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public abstract class APIReference {

    protected int amount = 0;

    /**
     * @return the ItemStack of the API
     */
    public abstract ItemStack getLinkedItem();

    /**
     * Use this method inside of GUIs that you need to handle APIs that don't save NamespacedKeys inside the ItemStack PersistentData!
     *
     * @return a ItemStack of the API with additional PersistentDataHolder that contains the NamespacedKey or other value of the API
     */
    public abstract ItemStack getIdItem();

    /**
     * The amount of the reference.
     * <p>
     * If the amount of this {@link APIReference}:
     *     <ul>
     *         <li>is equal or less than 0, then this method will return the amount of the {@link ItemStack#getAmount()} from {@link #getLinkedItem()}.</li>
     *         <li>is greater than 0, then this method will return the amount</li>
     *     </ul>
     * <p>
     *
     * @return The correct amount of this reference or linked item.
     */
    public int getAmount() {
        return amount > 0 ? amount : getLinkedItem().getAmount();
    }

    /**
     * Sets the amount of this APIReference.
     * Note: That a value of 0 or less indicates that the amount of this APIReference is equal to the linked ItemStack.
     *
     * @param amount The amount of this APIReference.
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Used to serialize the APIReferenc Object to Json
     *
     * @param gen the initial JsonGenerator containing the custom amount field
     */
    public abstract void serialize(JsonGenerator gen, SerializerProvider provider) throws IOException;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        APIReference that = (APIReference) o;
        return amount == that.amount;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    public abstract static class Parser<T extends APIReference> implements Comparable<Parser<?>> {

        private final int priority;
        private final String id;
        private final List<String> aliases;

        public Parser(String id) {
            this(id, 0);
        }

        public Parser(String id, int priority) {
            this(id, priority, new String[0]);
        }

        public Parser(String id, String... aliases) {
            this(id, 0, aliases);
        }

        public Parser(String id, int priority, String... aliases) {
            this.id = id;
            this.priority = priority;
            this.aliases = Collections.unmodifiableList(Arrays.asList(aliases));
        }

        public String getId() {
            return id;
        }

        public List<String> getAliases() {
            return aliases;
        }

        @Nullable
        public abstract T construct(ItemStack itemStack);

        @Nullable
        public abstract T parse(JsonNode element);

        @Override
        public int compareTo(@NotNull APIReference.Parser<?> that) {
            return Integer.compare(this.priority, that.priority);
        }

    }

    public abstract static class PluginParser<T extends APIReference> extends Parser<T> {

        private final String pluginName;

        public PluginParser(String pluginName, String id, String... aliases) {
            super(id, aliases);
            this.pluginName = pluginName;
        }

        public String getPluginName() {
            return pluginName;
        }
    }
}
