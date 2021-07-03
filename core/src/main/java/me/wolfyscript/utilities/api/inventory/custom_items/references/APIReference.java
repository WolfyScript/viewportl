package me.wolfyscript.utilities.api.inventory.custom_items.references;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * This object is a wrapper for items that are part of external APIs or vanilla minecraft. <br>
 * It makes it possible to reference and manage items from possibly multiple plugins at the same time.
 *
 * <p>
 * There are following references available:
 * <ul>
 *     <li>{@link VanillaRef}</li>
 *     <li>{@link WolfyUtilitiesRef}</li>
 *     <li>{@link OraxenRef}</li>
 *     <li>{@link ItemsAdderRef}</li>
 *     <li>{@link MMOItemsRef}</li>
 *     <li>{@link MythicMobsRef}</li>
 * </ul>
 * </p>
 * <p>
 * You can register additional references inside your plugin (onEnable) using {@link me.wolfyscript.utilities.api.inventory.custom_items.CustomItem#registerAPIReferenceParser(Parser)}.
 */
public abstract class APIReference {

    protected int amount;

    protected APIReference() {
        this.amount = 0;
    }

    protected APIReference(APIReference apiReference) {
        this.amount = apiReference.amount;
    }

    /**
     * @return the ItemStack of the API
     */
    public abstract ItemStack getLinkedItem();

    /**
     * Use this method inside of GUIs that you need, to handle APIs that don't save NamespacedKeys inside the ItemStack PersistentData!
     *
     * @return a ItemStack of the API with additional PersistentDataHolder that contains the NamespacedKey or other value of the API
     * @deprecated This method should not be used! It usually returns the same as {@link #getLinkedItem()}!
     */
    @Deprecated
    public abstract ItemStack getIdItem();

    public abstract boolean isValidItem(ItemStack itemStack);

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

    public abstract APIReference clone();

    public abstract static class Parser<T extends APIReference> implements Comparable<Parser<?>> {

        private final int priority;
        private final String id;
        private final List<String> aliases;

        protected Parser(String id) {
            this(id, 0);
        }

        protected Parser(String id, int priority) {
            this(id, priority, new String[0]);
        }

        protected Parser(String id, String... aliases) {
            this(id, 0, aliases);
        }

        protected Parser(String id, int priority, String... aliases) {
            this.id = id;
            this.priority = priority;
            this.aliases = List.of(aliases);
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

        protected PluginParser(String pluginName, String id, String... aliases) {
            super(id, aliases);
            this.pluginName = pluginName;
        }

        public String getPluginName() {
            return pluginName;
        }
    }
}
