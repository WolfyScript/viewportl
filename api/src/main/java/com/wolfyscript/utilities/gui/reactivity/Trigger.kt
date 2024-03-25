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

package com.wolfyscript.utilities.gui.reactivity

/**
 * A trigger is a simple reactivity node, that can be tracked, and notify its subscribers.
 * It does not contain a value, so it is for cases where a simple update notification and no value is required.
 *
 * To track the trigger inside an Effect/Memo you need to call the [Trigger.track] method inside it.<br>
 * To notify subscribers call the [Trigger.update] method.
 */
interface Trigger {

    fun track()

    fun update()

}