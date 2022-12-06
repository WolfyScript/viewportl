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

package com.wolfyscript.utilities.language;

import com.fasterxml.jackson.databind.JsonNode;
import com.wolfyscript.utilities.common.chat.Chat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.List;

public class LanguageNodeText extends LanguageNode {

    private final String raw;

    LanguageNodeText(Language language, Chat chat, JsonNode jsonNode) {
        super(language, chat, jsonNode);
        if (language.usesMiniMessageFormat()) {
            this.raw = jsonNode.asText("");
        } else {
            this.raw = chat.getWolfyUtils().getLanguageAPI().convertLegacyToMiniMessage(jsonNode.asText(""));
        }
    }

    @Override
    public Component getComponent() {
        return chat.getMiniMessage().deserialize(raw);
    }

    @Override
    public Component getComponent(TagResolver tagResolver) {
        return chat.getMiniMessage().deserialize(raw, tagResolver);
    }

    @Override
    public List<Component> getComponents() {
        return List.of(chat.getMiniMessage().deserialize(raw));
    }

    @Override
    public List<Component> getComponents(TagResolver tagResolver) {
        return List.of(chat.getMiniMessage().deserialize(raw, tagResolver));
    }

    @Override
    public String getRaw() {
        return raw;
    }
}
