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

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.JsonNode;
import com.wolfyscript.utilities.chat.Chat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.List;

public abstract class LanguageNode {

    private final JsonNode value;
    protected final Language language;
    protected Chat chat;

    protected LanguageNode(Language language, Chat chat, JsonNode value) {
        this.chat = chat;
        this.value = value;
        this.language = language;
    }

    abstract public Component getComponent();

    abstract public Component getComponent(TagResolver tagResolver);

    public Component getComponent(TagResolver... tagResolvers) {
        return getComponent(TagResolver.resolver(tagResolvers));
    }

    abstract public List<Component> getComponents();

    abstract public List<Component> getComponents(TagResolver tagResolver);

    public List<Component> getComponents(TagResolver... tagResolvers) {
        return getComponents(TagResolver.resolver(tagResolvers));
    }

    abstract public String getRaw();

    @JsonValue
    public JsonNode getValue() {
        return value;
    }
}
