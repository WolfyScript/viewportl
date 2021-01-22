package me.wolfyscript.utilities.api.inventory.custom_items;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

public abstract class CustomData {

    private final NamespacedKey namespacedKey;

    /**
     * This is the main constructor of this class and must not be changed in it's parameters!
     * If you do change it you also need to override the {@link Provider#createData()} method as it uses the NamespacedKey constructor by default!
     *
     * @param namespacedKey The {@link NamespacedKey} of this CustomData. Parsed to this class from the {@link Provider}.
     */
    protected CustomData(NamespacedKey namespacedKey) {
        this.namespacedKey = namespacedKey;
    }

    /**
     * Called whenever this data object is saved to the {@link CustomItem} config.
     *
     * @param customItem The {@link CustomItem} that this data is currently saved into.
     * @param gen        The {@link JsonGenerator} of the serialization.
     * @param provider   The {@link SerializerProvider} of the serialization.
     * @throws IOException If there occurs an error while writing it to the config.
     */
    public abstract void writeToJson(CustomItem customItem, JsonGenerator gen, SerializerProvider provider) throws IOException;

    /**
     * Called whenever this data is loaded from the config of the {@link CustomItem}.
     *
     * @param customItem The {@link CustomItem} that this data is currently loaded for and will be added to.
     * @param node       The {@link JsonNode} that is loaded from the config.
     * @param context    The {@link DeserializationContext} from the deserialization.
     * @throws IOException If there occurs an error while loading.
     */
    protected abstract void readFromJson(CustomItem customItem, JsonNode node, DeserializationContext context) throws IOException;

    /**
     * @return The namespacedKey, that is passed to this CustomData by it's {@link Provider}
     */
    public final NamespacedKey getNamespacedKey() {
        return namespacedKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomData that = (CustomData) o;
        return Objects.equals(namespacedKey, that.namespacedKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespacedKey);
    }

    /**
     * The Provider's goal is to create a new instance of the CustomData and load it from config.
     *
     * @param <T> The CustomData type that this Provider is used for.
     */
    public static class Provider<T extends CustomData> implements Keyed {

        private final NamespacedKey namespacedKey;
        private final Class<T> customDataClass;

        /**
         * Creates a new provider for the {@link CustomData} of the specified class.
         * This Provider requires a unique namespace and key, so it doesn't interfere with other data.
         *
         * @param namespacedKey   Unique {@link NamespacedKey} to identify this provider and it's data!
         * @param customDataClass Class of the overridden {@link CustomData} class.
         */
        public Provider(NamespacedKey namespacedKey, Class<T> customDataClass) {
            this.namespacedKey = namespacedKey;
            this.customDataClass = customDataClass;
        }

        /**
         * @return The {@link NamespacedKey} of this Provider. This method and the {@link CustomData#getNamespacedKey()} should always return the same value!
         */
        public NamespacedKey getNamespacedKey() {
            return namespacedKey;
        }

        /**
         * Adds a new instance of the {@link CustomData}, that is loaded from the config, to the {@link CustomItem}.
         *
         * @param customItem The {@link CustomItem} to add the data to.
         * @param node       The {@link JsonNode} loaded from the config.
         * @param context    The {@link DeserializationContext} from the deserialization.
         */
        public void addData(CustomItem customItem, JsonNode node, DeserializationContext context) {
            try {
                T instance = createData();
                instance.readFromJson(customItem, node, context);
                customItem.addCustomData(namespacedKey, instance);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * Creates a new default instance of the {@link CustomData} object.
         *
         * @return The new default instance of the {@link CustomData} object.
         */
        public T createData() {
            try {
                Constructor<T> constructor = customDataClass.getDeclaredConstructor(NamespacedKey.class);
                constructor.setAccessible(true);
                return constructor.newInstance(namespacedKey);
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
