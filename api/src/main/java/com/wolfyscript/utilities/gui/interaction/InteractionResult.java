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

package com.wolfyscript.utilities.gui.interaction;

public class InteractionResult {

    private ResultType type;

    InteractionResult() {
        type = ResultType.DEFAULT;
    }

    InteractionResult(ResultType type) {
        this.type = type;
    }

    public boolean isCancelled() {
        return getType() == ResultType.DENY;
    }

    public void setCancelled(boolean cancel) {
        setType(cancel ? ResultType.DENY : ResultType.DEFAULT);
    }

    public ResultType getType() {
        return type;
    }

    public void setType(ResultType type) {
        this.type = type;
    }

    public static InteractionResult cancel(boolean cancelled) {
        InteractionResult result = new InteractionResult();
        result.setCancelled(cancelled);
        return result;
    }

    public static InteractionResult def() {
        return new InteractionResult();
    }

    public static InteractionResult type(ResultType type) {
        return new InteractionResult(type);
    }

    public enum ResultType {

        /**
         * Deny the event. Depending on the event, the action indicated by the
         * event will either not take place or will be reverted. Some actions
         * may not be denied.
         */
        DENY,
        /**
         * Neither deny nor allow the event. The server will proceed with its
         * normal handling.
         */
        DEFAULT,
        /**
         * Allow / Force the event. The action indicated by the event will
         * take place if possible, even if the server would not normally allow
         * the action. Some actions may not be allowed.
         */
        ALLOW;
    }
}
