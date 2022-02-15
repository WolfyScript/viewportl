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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.Template;

import java.util.List;

//Annotation telling de/serializer that there are other types!
public abstract class LanguageNode {

    private final JsonNode value;
    protected Chat chat;

    protected LanguageNode(Chat chat, JsonNode value) {
        this.chat = chat;
        this.value = value;
    }

    abstract public Component getComponent(boolean translateLegacyColor, List<Template> templates);

    abstract public List<Component> getComponents(boolean translateLegacyColor, List<Template> templates);

    abstract public String getRaw();

    public JsonNode getValue() {
        return value;
    }
}
