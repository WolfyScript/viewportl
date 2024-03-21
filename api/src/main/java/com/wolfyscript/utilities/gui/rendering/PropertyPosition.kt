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
package com.wolfyscript.utilities.gui.rendering

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.SerializerProvider
import com.wolfyscript.utilities.config.jackson.OptionalValueDeserializer
import com.wolfyscript.utilities.config.jackson.OptionalValueSerializer
import java.io.IOException

@OptionalValueDeserializer(deserializer = PropertyPosition.ValueDeserializer::class)
@OptionalValueSerializer(serializer = PropertyPosition.ValueSerializer::class)
interface PropertyPosition {

    fun slotPositioning() : SlotPositioning? = null

    fun pixelPositioning() : PixelPositioning? = null

    /**
     * Used for positioning components primarily inside the inventory GUI
     */
    interface SlotPositioning {
        fun slot() : Int
    }

    interface PixelPositioning {

        fun left(): Int?

        fun right(): Int?

        fun top(): Int?

        fun bottom(): Int?

        fun type() : Type

        enum class Type {
            /**
             * Default Positioning that places the component into the next available free slot/space
             */
            DEFAULT,
            /**
             * Specifies the displacement from the default positioning.
             */
            RELATIVE,
            FIXED
        }

    }

    class ValueDeserializer :
        com.wolfyscript.utilities.config.jackson.ValueDeserializer<PropertyPosition>(PropertyPosition::class.java) {
        @Throws(IOException::class, JsonProcessingException::class)
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): PropertyPosition? {
            return when {
                p.currentToken == JsonToken.VALUE_NUMBER_INT -> PropertyPositionImpl(slotPositioning = PropertyPositionImpl.PropertySlotPositionImpl(p.valueAsInt))
                p.isExpectedStartArrayToken -> {
                    // TODO: Use array for quick configuration
                    null
                }
                else -> null
            }
        }
    }

    class ValueSerializer :
        com.wolfyscript.utilities.config.jackson.ValueSerializer<PropertyPosition>(PropertyPosition::class.java) {
        @Throws(IOException::class)
        override fun serialize(
            targetObject: PropertyPosition,
            generator: JsonGenerator,
            provider: SerializerProvider
        ): Boolean {
            if (targetObject.slotPositioning() != null) {
                generator.writeNumber(targetObject.slotPositioning()!!.slot())
                return true
            }
            return false
        }
    }

    companion object {

        fun slot(slot: Int) : PropertyPosition {
            return PropertyPositionImpl(slotPositioning = PropertyPositionImpl.PropertySlotPositionImpl(slot))
        }

        fun def() : PropertyPosition {
            return PropertyPositionImpl()
        }

    }
}
