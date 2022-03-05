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

package me.wolfyscript.utilities.util.json.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.api.WolfyUtilCore;
import me.wolfyscript.utilities.util.json.jackson.annotations.OptionalRegexCreator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexDeserializer<T> extends ValueDeserializer<T> {

    private static final Map<Class<?>, Map<OptionalRegexCreator, Pattern>> cachedPatterns = new HashMap<>();

    public RegexDeserializer(Class<T> type) {
        super(type);
        if (!cachedPatterns.containsKey(type)) {
            Map<OptionalRegexCreator, Pattern> subTypePatterns = new HashMap<>();

            for (Class<? extends T> subType : WolfyUtilCore.getInstance().getReflections().getSubTypesOf(type)) {
                OptionalRegexCreator regexCreator = subType.getAnnotation(OptionalRegexCreator.class);
                subTypePatterns.put(regexCreator, Pattern.compile(regexCreator.regex()));
            }
            cachedPatterns.put(type, subTypePatterns);
        }
    }

    @Override
    public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = p.readValueAsTree();
        if (node.isTextual()) {
            String text = node.asText();
            if (!text.isBlank()) {
                for (Map.Entry<OptionalRegexCreator, Pattern> entry : cachedPatterns.get(this.type).entrySet()) {
                    OptionalRegexCreator.RegexCreator<T> regexCreator;
                    try {
                        OptionalRegexCreator.RegexCreator<?> unknownRegexCreator = entry.getKey().creator().getDeclaredConstructor().newInstance();
                        if (this.type.isAssignableFrom(unknownRegexCreator.getType())) {
                            regexCreator = (OptionalRegexCreator.RegexCreator<T>) unknownRegexCreator;
                        } else {
                            throw new IllegalArgumentException("ValueDeserializer of type \"" + unknownRegexCreator.getType().getName() + "\" cannot construct type \"" + this.type.getName() + "\"");
                        }
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        e.printStackTrace();
                        continue;
                    }
                    Matcher matcher = entry.getValue().matcher(text);
                    T value = regexCreator.create(matcher);
                    if (value != null) return value;
                }
            }
        }
        return null;
    }

}
