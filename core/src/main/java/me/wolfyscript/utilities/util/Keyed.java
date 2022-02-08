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

package me.wolfyscript.utilities.util;

/**
 * Objects that implement this interface are bound to a {@link NamespacedKey} and can be uniquely identified.<br>
 * This interface may only be used for registrable resources and/or uniquely identifiably public resources!
 * <b>This interface may not be used for internal plugin resources!</b><br>
 * Usually this interface is implemented to register data into a kind of {@link me.wolfyscript.utilities.registry.Registry},
 * and allowing third-party plugins to register custom derivations too.
 */
public interface Keyed {

    /**
     * The unique NamespacedKey of the object.
     *
     * @return The NamespacedKey of the object.
     */
    NamespacedKey getNamespacedKey();
}
