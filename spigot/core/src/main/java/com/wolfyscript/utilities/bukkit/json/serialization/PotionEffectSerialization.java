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

package com.wolfyscript.utilities.bukkit.json.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.wolfyscript.utilities.config.jackson.JacksonUtil;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectSerialization {

    private static final String AMPLIFIER = "amplifier";
    private static final String DURATION = "duration";
    private static final String TYPE = "effect";
    private static final String AMBIENT = "ambient";
    private static final String PARTICLES = "has-particles";
    private static final String ICON = "has-icon";

    public static void create(SimpleModule module) {
        JacksonUtil.addSerializerAndDeserializer(module, PotionEffect.class, (potionEffect, gen, serializerProvider) -> {
            gen.writeStartObject();
            gen.writeNumberField(AMPLIFIER, potionEffect.getAmplifier());
            gen.writeNumberField(DURATION, potionEffect.getDuration());
            gen.writeObjectField(TYPE, potionEffect.getType());
            gen.writeBooleanField(AMBIENT, potionEffect.isAmbient());
            gen.writeBooleanField(PARTICLES, potionEffect.hasParticles());
            gen.writeBooleanField(ICON, potionEffect.hasIcon());
            gen.writeEndObject();
        }, (p, deserializationContext) -> {
            JsonNode node = p.readValueAsTree();
            if (!node.has(TYPE)) return null;
            PotionEffectType type = p.getCodec().treeToValue(node.path(TYPE), PotionEffectType.class);
            boolean particles = node.path(PARTICLES).asBoolean(true);
            boolean icon = node.path(ICON).asBoolean(particles);
            return type == null ? null : new PotionEffect(type, node.path(DURATION).asInt(), node.path(AMPLIFIER).asInt(), node.path(AMBIENT).asBoolean(false), particles, icon);
        });
    }

}
