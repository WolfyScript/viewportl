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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

//Annotation that links a type in json (or TOKEN) to this type
public class LanguageNodeArray extends LanguageNode {

    private final List<String> raw;
    private final List<String> rawLegacy;
    private final String rawLine;
    private final String rawLegacyLine;

    LanguageNodeArray(Chat chat, JsonNode jsonNode) {
        super(chat, jsonNode);
        this.raw = new LinkedList<>();
        this.rawLegacy = new LinkedList<>();
        Iterator<JsonNode> nodeItr = jsonNode.elements();
        while (nodeItr.hasNext()) {
            String value = nodeItr.next().textValue();
            this.raw.add(value);
            this.rawLegacy.add(ChatColor.convert(value));
        }
        this.rawLine = raw.stream().reduce("", (s, s2) -> s + " " + s2);
        this.rawLegacyLine = rawLegacy.stream().reduce("", (s, s2) -> s + " " + s2);
    }

    @Override
    public Component getComponent(boolean translateLegacyColor, List<Template> templates) {
        return chat.getMiniMessage().parse(translateLegacyColor ? rawLegacyLine : rawLine, templates);
    }

    @Override
    public List<Component> getComponents(boolean translateLegacyColor, List<Template> templates) {
        return translateLegacyColor ? getComponents(rawLegacy, templates) : getComponents(raw, templates);
    }

    @Override
    public String getRaw() {
        return rawLine;
    }

    private List<Component> getComponents(List<String> rawValues, List<Template> templates) {
        return rawValues.stream().map(s -> chat.getMiniMessage().parse(s, templates)).collect(Collectors.toList());
    }
}
