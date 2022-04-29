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

    LanguageNodeText(Language language, Chat chat, JsonNode jsonNode) {
        super(language, chat, jsonNode);
        if (language.usesMiniMessageFormat()) {
            this.raw = jsonNode.asText("");
        } else {
            this.raw = chat.getWolfyUtils().getLanguageAPI().convertLegacyToMiniMessage(jsonNode.asText(""));
        }
    }

    @Override
    public Component getComponent(boolean translateLegacyColor) {
        return chat.getMiniMessage().deserialize(getText(translateLegacyColor));
    }

    @Override
    public Component getComponent(boolean translateLegacyColor, TagResolver tagResolver) {
        return chat.getMiniMessage().deserialize(getText(translateLegacyColor), tagResolver);
    }

    @Override
    public List<Component> getComponents(boolean translateLegacyColor) {
        return List.of(chat.getMiniMessage().deserialize(getText(translateLegacyColor)));
    }

    @Override
    public List<Component> getComponents(boolean translateLegacyColor, TagResolver tagResolver) {
        return List.of(chat.getMiniMessage().deserialize(getText(translateLegacyColor), tagResolver));
    }

    private String getText(boolean translateLegacyColor) {
        return translateLegacyColor ? rawLegacy : raw;
    }

    @Override
    public String getRaw() {
        return raw;
    }
}
