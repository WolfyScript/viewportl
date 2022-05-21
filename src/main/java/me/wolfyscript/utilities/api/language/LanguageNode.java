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

    @Deprecated
    public Component getComponent(boolean translateLegacyColor) {
        return getComponent();
    }

    @Deprecated
    public Component getComponent(boolean translateLegacyColor, TagResolver tagResolver) {
        return getComponent(tagResolver);
    }

    @Deprecated
    public Component getComponent(boolean translateLegacyColor, TagResolver... tagResolvers) {
        return getComponent(tagResolvers);
    }

    /**
     * @deprecated Replaced by {@link #getComponents(TagResolver)}! The translateLegacyColor has no effect anymore!
     *  Plus, converting a list to an array is expensive! You may also use {@link #getComponent(TagResolver)} and {@link TagResolver#resolver(Iterable)}.
     * @param translateLegacyColor translate the legacy color codes in the raw value
     * @return The Component of this node
     */
    @Deprecated
    public Component getComponent(boolean translateLegacyColor, List<? extends TagResolver> tagResolvers) {
        return getComponent(translateLegacyColor, tagResolvers.toArray(new TagResolver[0]));
    }

    abstract public List<Component> getComponents();

    abstract public List<Component> getComponents(TagResolver tagResolver);

    public List<Component> getComponents(TagResolver... tagResolvers) {
        return getComponents(TagResolver.resolver(tagResolvers));
    }

    /**
     * @deprecated Replaced by {@link #getComponents()}! The translateLegacyColor has no effect anymore!
     * @param translateLegacyColor translate the legacy color codes in the raw value
     * @return The list of Components.
     */
    @Deprecated
    public List<Component> getComponents(boolean translateLegacyColor) {
        return getComponents();
    }

    /**
     * @deprecated Replaced by {@link #getComponents(TagResolver)}! The translateLegacyColor has no effect anymore!
     * @param translateLegacyColor translate the legacy color codes in the raw value
     * @return The list of Components.
     */
    @Deprecated
    public List<Component> getComponents(boolean translateLegacyColor, TagResolver tagResolver) {
        return getComponents(tagResolver);
    }

    /**
     * @deprecated Replaced by {@link #getComponents(TagResolver...)}! The translateLegacyColor has no effect anymore!
     * @param translateLegacyColor translate the legacy color codes in the raw value
     * @return The list of Components.
     */
    @Deprecated
    public List<Component> getComponents(boolean translateLegacyColor, TagResolver... tagResolvers) {
        return getComponents(tagResolvers);
    }

    /**
     * @deprecated Replaced by {@link #getComponents(TagResolver...)}! The translateLegacyColor has no effect anymore!
     *  Plus, converting a list to an array is expensive! You may also use {@link #getComponent(TagResolver)} and {@link TagResolver#resolver(Iterable)}.
     * @param translateLegacyColor translate the legacy color codes in the raw value
     * @return The list of Components.
     */
    @Deprecated
    public List<Component> getComponents(boolean translateLegacyColor, List<? extends TagResolver> tagResolvers) {
        return getComponents(translateLegacyColor, tagResolvers.toArray(new TagResolver[0]));
    }

    abstract public String getRaw();

    @JsonValue
    public JsonNode getValue() {
        return value;
    }
}
