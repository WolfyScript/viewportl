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
import me.wolfyscript.utilities.api.chat.Chat;
import me.wolfyscript.utilities.util.chat.ChatColor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;

import java.util.List;

public class LanguageNodeText extends LanguageNode {

    private final String raw;
    private final String rawLegacy;

    LanguageNodeText(Chat chat, JsonNode jsonNode) {
        super(chat, jsonNode);
        this.raw = jsonNode.asText("");
        this.rawLegacy = ChatColor.convert(raw);
    }

    @Override
    public Component getComponent(boolean translateLegacyColor, List<Template> templates) {
        return chat.getMiniMessage().parse(translateLegacyColor ? rawLegacy : raw, templates);
    }

    @Override
    public List<Component> getComponents(boolean translateLegacyColor, List<Template> templates) {
        return List.of(chat.getMiniMessage().parse(translateLegacyColor ? rawLegacy : raw, templates));
    }

    @Override
    public String getRaw() {
        return raw;
    }
}
