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

package com.wolfyscript.utilities.platform.scheduler;

import com.wolfyscript.utilities.WolfyUtils;

public interface Scheduler {

    /**
     * Creates a new {@link Task.Builder Task Builder} bound to the specified {@link WolfyUtils}
     *
     * @param wolfyUtils The api to link it to
     * @return The new Task Builder
     */
    Task.Builder task(WolfyUtils wolfyUtils);

    default Task syncTask(WolfyUtils api, Runnable task) {
        return syncTask(api, task, 0);
    }

    Task syncTask(WolfyUtils api, Runnable task, int delay);

    default Task asyncTask(WolfyUtils api, Runnable task) {
        return asyncTask(api, task, 0);
    }

    Task asyncTask(WolfyUtils api, Runnable task, int delay);

    Task syncTimerTask(WolfyUtils api, Runnable task, int delay, int interval);

    Task asyncTimerTask(WolfyUtils api, Runnable task, int delay, int interval);

}
