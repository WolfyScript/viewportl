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

package me.wolfyscript.utilities.util.json.jackson.annotations;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.registry.Registry;
import me.wolfyscript.utilities.util.Keyed;
import me.wolfyscript.utilities.util.NamespacedKey;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation allows serializing/deserializing an instance of {@link Keyed} by its key or value.
 * The annotated type <b>must</b> have a {@link Registry}, that contains the class of the type.<br>
 * The specified key must exist and be type of {@link NamespacedKey}.<br>
 *
 * <br>Serialization:<br>
 * Serializes the key if available, else if key is null uses default serializer.<br>
 *
 * <br>Deserialization:<br>
 * If the {@link JsonNode} is text it will convert it to {@link NamespacedKey} and look for the value in the {@link Registry} of the objects type.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OptionalKeyReference {

    /**
     * The field name of the {@link NamespacedKey} that identifies the object.
     *
     * @return the field name of the objects' key
     */
    String field() default "key";

    /**
     * Tells the serializer if it should serialize any object of this type as a key reference.<br>
     * On by default, will always serialize as a key reference. This uses the field specified in {@link #field()}!<br>
     * If disabled the value of {@link #field()} is ignored and the object is always serialized using the default serializer.
     *
     * @return If it should be serialized as a key reference.
     */
    boolean serializeAsKey() default true;

    String registryKey() default "";

    final class SerializerModifier extends BeanSerializerModifier {

        @Override
        public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
            var handledType = serializer.handledType();
            var annotation = handledType.getAnnotation(OptionalKeyReference.class);
            if(annotation != null) {
                if(Keyed.class.isAssignableFrom(handledType)) {
                    return new Serializer<>(annotation, (JsonSerializer<? extends Keyed>) serializer);
                }
            }
            return serializer;
        }

        private static class Serializer<T extends Keyed> extends StdSerializer<T> {

            private final OptionalKeyReference reference;
            private final String property;
            private final JsonSerializer<T> defaultSerializer;

            protected Serializer(OptionalKeyReference reference, JsonSerializer<T> defaultSerializer) {
                super(defaultSerializer.handledType());
                this.reference = reference;
                this.property = reference.field();
                this.defaultSerializer = defaultSerializer;
            }

            @Override
            public void serialize(T targetObject, JsonGenerator generator, SerializerProvider provider) throws IOException {
                if (reference.serializeAsKey()) {
                    try {
                        var propertyField = targetObject.getClass().getDeclaredField(property);
                        propertyField.setAccessible(true);
                        var propertyObject = propertyField.get(targetObject);
                        if(propertyObject instanceof NamespacedKey) {
                            generator.writeObject(propertyObject);
                            return;
                        }
                    } catch (NoSuchFieldException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                defaultSerializer.serialize(targetObject, generator, provider);
            }

        }

    }

    final class DeserializerModifier extends BeanDeserializerModifier {

        @Override
        public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
            var handledType = deserializer.handledType();
            var annotation = handledType.getAnnotation(OptionalKeyReference.class);
            if(annotation != null) {
                if(Keyed.class.isAssignableFrom(handledType)) {
                    return new Deserializer<>(annotation, (JsonDeserializer<? extends Keyed>) deserializer);
                }
            }
            return deserializer;
        }

        private static class Deserializer<T extends Keyed> extends StdDeserializer<T> implements ResolvableDeserializer {

            private final Class<T> genericType;
            private final JsonDeserializer<T> defaultDeserializer;
            private final NamespacedKey registryKey;

            protected Deserializer(OptionalKeyReference reference, JsonDeserializer<T> defaultSerializer) {
                super(defaultSerializer.handledType());
                this.registryKey = NamespacedKey.of(reference.registryKey());
                this.genericType = (Class<T>) defaultSerializer.handledType();
                this.defaultDeserializer = defaultSerializer;
            }

            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
                if(p.isExpectedStartObjectToken()) {
                    return defaultDeserializer.deserialize(p, ctxt);
                }
                return getKeyedObject(p);
            }

            @Override
            public void resolve(DeserializationContext ctxt) throws JsonMappingException {
                if (defaultDeserializer instanceof ResolvableDeserializer resolvableDeserializer) {
                    resolvableDeserializer.resolve(ctxt);
                }
            }

            @Override
            public Object deserializeWithType(JsonParser p, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException {
                if (p.isExpectedStartObjectToken()) {
                    return defaultDeserializer.deserializeWithType(p, ctxt, typeDeserializer);
                }
                return getKeyedObject(p);
            }

            private T getKeyedObject(JsonParser p) throws IOException {
                Registry<T> registry;
                if (registryKey != null) {
                    registry = (Registry<T>) WolfyUtilCore.getInstance().getRegistries().getByKey(registryKey);
                } else {
                    registry = WolfyUtilCore.getInstance().getRegistries().getByType(genericType);
                }
                if(registry != null) {
                    String value = p.readValueAs(String.class);
                    return registry.get(NamespacedKey.of(value));
                }
                return null;
            }

        }

    }

}
