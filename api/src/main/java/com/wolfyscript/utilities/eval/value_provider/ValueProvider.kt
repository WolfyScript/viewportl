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
package com.wolfyscript.utilities.eval.value_provider

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver
import com.fasterxml.jackson.databind.annotation.JsonTypeResolver
import com.wolfyscript.utilities.Keyed
import com.wolfyscript.utilities.NamespacedKey
import com.wolfyscript.utilities.config.jackson.*
import com.wolfyscript.utilities.eval.context.EvalContext
import java.io.IOException
import java.util.regex.Pattern

@JsonTypeResolver(KeyedTypeResolver::class)
@JsonTypeIdResolver(
    KeyedTypeIdResolver::class
)
@OptionalValueDeserializer(deserializer = ValueProvider.ValueDeserializer::class)
@OptionalValueSerializer(serializer = ValueProvider.ValueSerializer::class)
@JsonTypeInfo(use = JsonTypeInfo.Id.CUSTOM, property = "key")
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonPropertyOrder(value = ["key"])
interface ValueProvider<V> : Keyed {
    @JsonIgnore
    fun getValue(context: EvalContext?): V

    @get:JsonIgnore
    val value: V
        get() = getValue(EvalContext())

    @JsonGetter("key")
    override fun key(): NamespacedKey

    class ValueDeserializer : com.wolfyscript.utilities.config.jackson.ValueDeserializer<ValueProvider<*>>(ValueProvider::class.java) {
        @Throws(IOException::class, JsonProcessingException::class)
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ValueProvider<*>? {
            if (p.currentToken() == JsonToken.VALUE_STRING) {
                val node = p.readValueAsTree<JsonNode>()
                val text = node.asText()
                if (text.isNotBlank()) {
                    val matcher = NUM_PATTERN.matcher(text)
                    if (matcher.matches()) {
                        val value: String
                        var id = matcher.group(2)
                        if (id != null) {
                            // integer value
                            value = matcher.group(1)
                        } else {
                            // float value
                            id = matcher.group(4)
                            value = matcher.group(3)
                        }
                        try {
                            return when (id!![0]) {
                                's', 'S' -> ValueProviderShortConst(value.toShort())
                                'i', 'I' -> ValueProviderIntegerConst(value.toInt())
                                'l', 'L' -> ValueProviderLongConst(value.toLong())
                                'f', 'F' -> ValueProviderFloatConst(value.toFloat())
                                'd', 'D' -> ValueProviderDoubleConst(value.toDouble())
                                else -> ValueProviderStringConst(text)
                            }
                        } catch (e: NumberFormatException) {
                            // Cannot parse the value. Might a String value!
                        }
                    }
                    return ValueProviderStringConst(text)
                }
            } else if (p.currentToken() == JsonToken.VALUE_NUMBER_INT) {
                return ValueProviderIntegerConst(ctxt.readValue(p, Int::class.java))
            } else if (p.currentToken() == JsonToken.VALUE_NUMBER_FLOAT) {
                return ValueProviderDoubleConst(ctxt.readValue(p, Double::class.java))
            }
            return null
        }

        companion object {
            private val NUM_PATTERN: Pattern = Pattern.compile("([0-9]+)([bBsSiIlL])|([0-9]?\\.?[0-9])+([fFdD])")
        }
    }

    class ValueSerializer : com.wolfyscript.utilities.config.jackson.ValueSerializer<ValueProvider<*>>(ValueProvider::class.java) {
        @Throws(IOException::class)
        override fun serialize(
            valueProvider: ValueProvider<*>,
            generator: JsonGenerator,
            provider: SerializerProvider
        ): Boolean {
            println("Serialize ValueProvider!")

            when (valueProvider) {
                is ValueProviderStringConst -> {
                    generator.writeString(valueProvider.value)
                    return true
                }

                is ValueProviderByteConst -> {
                    generator.writeString("${valueProvider.value}b")
                    return true
                }

                is ValueProviderShortConst -> {
                    generator.writeString("${valueProvider.value.toByte()}s")
                    return true
                }

                is ValueProviderIntegerConst -> {
                    generator.writeNumber(valueProvider.value)
                    return true
                }

                is ValueProviderLongConst -> {
                    generator.writeString("${valueProvider.value}L")
                    return true
                }

                is ValueProviderFloatConst -> {
                    generator.writeString("${valueProvider.value}f")
                    return true
                }

                is ValueProviderDoubleConst -> {
                    generator.writeString("${valueProvider.value}d")
                    return true
                }

                else -> return false
            }
        }
    }
}
