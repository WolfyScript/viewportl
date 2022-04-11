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
import com.wolfyscript.utilities.common.chat.Chat;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.List;

public abstract class LanguageNode {

    private final JsonNode value;
    protected Chat chat;

    protected LanguageNode(Chat chat, JsonNode value) {
        this.chat = chat;
        this.value = value;
    }

    abstract public Component getComponent(boolean translateLegacyColor);

    abstract public Component getComponent(boolean translateLegacyColor, TagResolver tagResolver);

    public Component getComponent(boolean translateLegacyColor, TagResolver... tagResolvers) {
        return getComponent(translateLegacyColor, TagResolver.resolver(tagResolvers));
    }

    public Component getComponent(boolean translateLegacyColor, List<? extends TagResolver> tagResolvers) {
        return getComponent(translateLegacyColor, tagResolvers.toArray(new TagResolver[0]));
    }

    abstract public List<Component> getComponents(boolean translateLegacyColor);

    abstract public List<Component> getComponents(boolean translateLegacyColor, TagResolver tagResolver);

    public List<Component> getComponents(boolean translateLegacyColor, TagResolver... tagResolvers) {
        return getComponents(translateLegacyColor, TagResolver.resolver(tagResolvers));
    }

    public List<Component> getComponents(boolean translateLegacyColor, List<? extends TagResolver> tagResolvers) {
        return getComponents(translateLegacyColor, tagResolvers.toArray(new TagResolver[0]));
    }

    abstract public String getRaw();

    @JsonValue
    public JsonNode getValue() {
        return value;
    }
}
