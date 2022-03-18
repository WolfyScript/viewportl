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

import com.fasterxml.jackson.databind.JsonNode;
import com.wolfyscript.utilities.common.chat.Chat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.List;

public class LanguageNodeText extends LanguageNode {

    private final String raw;
    private final String rawLegacy;

    LanguageNodeText(Chat chat, JsonNode jsonNode) {
        super(chat, jsonNode);
        this.raw = jsonNode.asText("");
        this.rawLegacy = convertLegacyToMiniMessage(raw);
    }

    @Override
    public Component getComponent(boolean translateLegacyColor, List<? extends TagResolver> templates) {
        return chat.getMiniMessage().deserialize(translateLegacyColor ? rawLegacy : raw, templates.toArray(TagResolver[]::new));
    }

    @Override
    public List<Component> getComponents(boolean translateLegacyColor, List<? extends TagResolver> templates) {
        return List.of(chat.getMiniMessage().deserialize(translateLegacyColor ? rawLegacy : raw, templates.toArray(TagResolver[]::new)));
    }

    @Override
    public String getRaw() {
        return raw;
    }
}
