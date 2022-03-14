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

package me.wolfyscript.utilities.api.chat;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

@Deprecated
public class ClickData {

    private final String message;
    private final ClickAction clickAction;
    private final List<ChatEvent<?, ?>> clickEvents;
    private final boolean discard;

    public ClickData(String message, @Nullable ClickAction clickAction, boolean discard, ChatEvent<?, ?>... clickEvents) {
        this.clickAction = clickAction;
        this.message = message;
        this.clickEvents = Arrays.asList(clickEvents);
        this.discard = discard;
    }

    public ClickData(String message, @Nullable ClickAction clickAction, ChatEvent<?, ?>... clickEvents) {
        this(message, clickAction, false, clickEvents);
    }

    public ClickData(String message, @Nullable ClickAction clickAction) {
        this(message, clickAction, new ChatEvent[]{});
    }

    public ClickAction getClickAction() {
        return clickAction;
    }

    public String getMessage() {
        return message;
    }

    public List<ChatEvent<?, ?>> getChatEvents() {
        return clickEvents;
    }

    public boolean isDiscard() {
        return discard;
    }
}
