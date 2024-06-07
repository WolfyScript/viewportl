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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.BeanDeserializerModifier;
import com.fasterxml.jackson.databind.deser.ResolvableDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OptionalValueDeserializer {

    Class<? extends ValueDeserializer<?>> deserializer();

    boolean delegateObjectDeserializer() default false;

    final class DeserializerModifier extends BeanDeserializerModifier {

        @Override
        public JsonDeserializer<?> modifyDeserializer(DeserializationConfig config, BeanDescription beanDesc, JsonDeserializer<?> deserializer) {
            var handledType = deserializer.handledType();
            var annotation = handledType.getAnnotation(OptionalValueDeserializer.class);
            if (annotation != null) {
                try {
                    return new Deserializer<>(annotation, deserializer);
                } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
            }
            return deserializer;
        }

        private static class Deserializer<T> extends StdDeserializer<T> implements ResolvableDeserializer {

            private final ValueDeserializer<T> deserializer;
            private final JsonDeserializer<T> defaultDeserializer;
            private final boolean alwaysDelegate;

            protected Deserializer(OptionalValueDeserializer reference, JsonDeserializer<T> defaultSerializer) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
                super(defaultSerializer.handledType());
                Class<T> genericType = (Class<T>) defaultSerializer.handledType();
                this.defaultDeserializer = defaultSerializer;
                this.alwaysDelegate = reference.delegateObjectDeserializer();
                ValueDeserializer<?> constructedDeserializer = reference.deserializer().getDeclaredConstructor().newInstance();
                if (genericType.isAssignableFrom(constructedDeserializer.getType())) {
                    this.deserializer = (ValueDeserializer<T>) constructedDeserializer;
                } else {
                    throw new IllegalArgumentException("ValueDeserializer of type \"" + constructedDeserializer.getType().getName() + "\" cannot construct type \"" + genericType.getName() + "\"");
                }
            }

            @Override
            public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                if (p.isExpectedStartObjectToken()) {
                    if (alwaysDelegate) {
                        var value = deserializer.deserialize(p, ctxt);
                        if (value != null) return value;
                    }
                    return defaultDeserializer.deserialize(p, ctxt);
                }
                return deserializer.deserialize(p, ctxt);
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
                    if (alwaysDelegate) {
                        var value = deserializer.deserialize(p, ctxt);
                        if (value != null) return value;
                    }
                    return defaultDeserializer.deserializeWithType(p, ctxt, typeDeserializer);
                }
                return deserializer.deserialize(p, ctxt);
            }

        }

    }

}
