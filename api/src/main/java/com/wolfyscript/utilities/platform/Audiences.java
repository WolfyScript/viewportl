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

package com.wolfyscript.utilities.platform;

import net.kyori.adventure.audience.Audience;

import java.util.UUID;

/**
 * A wrapper for the Adventure API Audiences.
 *
 * This wrapper tries to provide cross-platform audiences as good as possible.
 * The platform specific implementation must be used in cases where this wrapper may be lacking features or audiences!
 */
public interface Audiences {

    Audience player(UUID uuid);

    Audience all();

    Audience system();

}
