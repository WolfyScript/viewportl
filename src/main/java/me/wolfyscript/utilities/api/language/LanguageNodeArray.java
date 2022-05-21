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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class LanguageNodeArray extends LanguageNode {

    private final List<String> raw;
    private final String rawLine;

    LanguageNodeArray(Language language, Chat chat, JsonNode jsonNode) {
        super(language, chat, jsonNode);
        this.raw = new LinkedList<>();
        Iterator<JsonNode> nodeItr = jsonNode.elements();
        while (nodeItr.hasNext()) {
            String value = nodeItr.next().textValue();
            this.raw.add(language.usesMiniMessageFormat() ? value : chat.getWolfyUtils().getLanguageAPI().convertLegacyToMiniMessage(value));
        }
        this.rawLine = raw.stream().reduce("", (s, s2) -> s + " " + s2);
    }

    @Override
    public Component getComponent() {
        return chat.getMiniMessage().deserialize(rawLine);
    }

    @Override
    public Component getComponent(TagResolver tagResolver) {
        return chat.getMiniMessage().deserialize(rawLine, tagResolver);
    }

    @Override
    public List<Component> getComponents() {
        return getComponents(raw);
    }

    @Override
    public List<Component> getComponents(TagResolver tagResolver) {
        return getComponents(raw, tagResolver);
    }

    private List<Component> getComponents(List<String> rawValues) {
        return rawValues.stream().map(s -> chat.getMiniMessage().deserialize(s)).collect(Collectors.toList());
    }

    private List<Component> getComponents(List<String> rawValues, TagResolver tagResolver) {
        return rawValues.stream().map(s -> chat.getMiniMessage().deserialize(s, tagResolver)).collect(Collectors.toList());
    }

    @Override
    public String getRaw() {
        return rawLine;
    }
}
