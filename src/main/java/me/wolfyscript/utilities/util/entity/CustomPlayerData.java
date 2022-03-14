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

package me.wolfyscript.utilities.util.entity;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializerProvider;
import me.wolfyscript.utilities.api.inventory.custom_items.CustomData;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class CustomPlayerData {

    static Map<NamespacedKey, CustomPlayerData.Provider<?>> providers = new HashMap<>();

    /**
     * Each class that extends this needs to have a default constructor to set the default values.
     * It is required for deserialization of the object from json!
     */
    protected CustomPlayerData() {
    }

    public static void register(CustomPlayerData.Provider<?> provider) {
        providers.put(provider.getNamespacedKey(), provider);
    }

    /**
     * Called whenever this data object is saved to the player store.
     *
     * @param gen      The {@link JsonGenerator} of the serialization.
     * @param provider The {@link SerializerProvider} of the serialization.
     * @throws IOException If there occurs an error while writing it to the config.
     */
    public abstract void writeToJson(JsonGenerator gen, SerializerProvider provider) throws IOException;

    /**
     * Called whenever this data is loaded from the config of the player.
     *
     * @param node    The {@link JsonNode} that is loaded from the config.
     * @param context The {@link DeserializationContext} from the deserialization.
     * @throws IOException If there occurs an error while loading.
     */
    protected abstract void readFromJson(JsonNode node, DeserializationContext context) throws IOException;

    /**
     * The Provider's goal is to create a new instance of the {@link CustomPlayerData} and load it from the player store.
     *
     * @param <T> The {@link CustomPlayerData} type that this Provider is used for.
     */
    public static class Provider<T extends CustomPlayerData> {

        private final NamespacedKey namespacedKey;
        private final Class<T> dataClass;

        /**
         * Creates a new provider for the {@link CustomData} of the specified class.
         * This Provider requires a unique namespace and key, so it doesn't interfere with other data.
         *
         * @param namespacedKey Unique {@link NamespacedKey} to identify this provider and it's data!
         * @param dataClass     Class of the overridden {@link CustomData} class.
         */
        public Provider(NamespacedKey namespacedKey, Class<T> dataClass) {
            this.namespacedKey = namespacedKey;
            this.dataClass = dataClass;
        }

        /**
         * @return The {@link NamespacedKey} of this Provider. This method and the {@link CustomData#getNamespacedKey()} should always return the same value!
         */
        public NamespacedKey getNamespacedKey() {
            return namespacedKey;
        }

        public T loadData(JsonNode node, DeserializationContext context) {
            T instance = createData();
            try {
                instance.readFromJson(node, context);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return instance;
        }

        /**
         * Creates a new default instance of the {@link CustomPlayerData} object.
         *
         * @return The new default instance of the {@link CustomPlayerData} object.
         */
        public T createData() {
            try {
                Constructor<T> constructor = dataClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                return constructor.newInstance();
            } catch (InstantiationException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
