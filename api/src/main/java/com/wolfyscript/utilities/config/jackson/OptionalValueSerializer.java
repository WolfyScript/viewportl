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

package com.wolfyscript.utilities.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.wolfyscript.utilities.Keyed;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OptionalValueSerializer {

    Class<? extends ValueSerializer<?>> serializer();

    final class SerializerModifier extends BeanSerializerModifier {

        @Override
        public JsonSerializer<?> modifySerializer(SerializationConfig config, BeanDescription beanDesc, JsonSerializer<?> serializer) {
            var handledType = serializer.handledType();
            var annotation = handledType.getAnnotation(OptionalValueSerializer.class);
            if (annotation != null) {
                try {
                    return new Serializer<>(annotation, (JsonSerializer<? extends Keyed>) serializer);
                } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return serializer;
        }

        private static class Serializer<T extends Keyed> extends StdSerializer<T> {

            private final Class<T> genericType;
            private final ValueSerializer<T> serializer;
            private final JsonSerializer<T> defaultSerializer;

            protected Serializer(OptionalValueSerializer reference, JsonSerializer<T> defaultSerializer) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
                super(defaultSerializer.handledType());
                this.genericType = defaultSerializer.handledType();
                this.defaultSerializer = defaultSerializer;

                ValueSerializer<?> constructedDeserializer = reference.serializer().getDeclaredConstructor().newInstance();
                if (genericType.isAssignableFrom(constructedDeserializer.getType())) {
                    this.serializer = (ValueSerializer<T>) constructedDeserializer;
                } else {
                    throw new IllegalArgumentException("ValueSerializer of type \"" + constructedDeserializer.getType().getName() + "\" cannot handle type \"" + genericType.getName() + "\"");
                }
            }

            @Override
            public void serializeWithType(T value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
               if (!serializer.serialize(value, gen, serializers)) {
                   defaultSerializer.serializeWithType(value, gen, serializers, typeSer);
               }
            }

            @Override
            public void serialize(T targetObject, JsonGenerator generator, SerializerProvider provider) throws IOException {
                if(!serializer.serialize(targetObject, generator, provider)) {
                    defaultSerializer.serialize(targetObject, generator, provider);
                }
            }

        }

    }

}
