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

package me.wolfyscript.utilities.api.language;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.util.chat.ChatColor;
import net.kyori.adventure.platform.bukkit.BukkitComponentSerializer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class LanguageNode {

    private static final Pattern LEGACY_PLACEHOLDER_PATTERN = Pattern.compile("%([^%]+)%");

    private final JsonNode value;
    protected Chat chat;

    protected LanguageNode(Chat chat, JsonNode value) {
        this.chat = chat;
        this.value = value;
    }

    abstract public Component getComponent(boolean translateLegacyColor, List<? extends TagResolver> templates);

    abstract public List<Component> getComponents(boolean translateLegacyColor, List<? extends TagResolver> templates);

    abstract public String getRaw();

    @JsonValue
    public JsonNode getValue() {
        return value;
    }

    protected String convertLegacyToMiniMessage(String legacyText) {
        String rawLegacy = ChatColor.convert(legacyText);
        Matcher matcher = LEGACY_PLACEHOLDER_PATTERN.matcher(rawLegacy);
        while (matcher.find()) {
            //replace the old placeholder with the new converted one.
            rawLegacy = rawLegacy.replace(matcher.group(), "<" + chat.convertOldPlaceholder(matcher.group(1)) + ">");
        }
        if (rawLegacy.contains("§")) {
            return chat.getMiniMessage().serialize(BukkitComponentSerializer.legacy().deserialize(rawLegacy));
        }
        return rawLegacy;
    }
}
