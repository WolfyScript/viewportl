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

package com.wolfyscript.utilities;

public interface Identifiers {

    /**
     * @param value The NamespacedKey String with a column that separates namespace and key.
     * @return The NamespacedKey of the String or null, if the String doesn't contain a ":".
     */
    NamespacedKey getNamespaced(String value);

    /**
     * Creates a NamespacedKey with a custom namespace and key.<br>
     * This does not uniquely link the resource to your plugin! (Except the namespace is exactly the same as your plugins' lowercase name)<br>
     * <b>Only for internal use. Must be converted to a namespaced key with your plugins' (lowercase) name as the namespace, if you register data in WolfyUtilities!</b>
     *
     * @param namespace The namespace, that fits the pattern [a-z0-9._-]
     * @param key       The key that fits the pattern [a-z0-9/._-]
     */
    NamespacedKey getNamespaced(String namespace, String key);

    default NamespacedKey getNamespaced(Class<?> type) {
        return getNamespaced(KeyedStaticId.KeyBuilder.createKeyString(type));
    }

    /**
     * Creates a new NamespacedKey with the namespace of this API.
     *
     * @param key    The key that fits the pattern [a-z0-9/._-]
     */
    NamespacedKey getSelfNamespaced(String key);

    NamespacedKey getWolfyUtilsNamespaced(String key);

}
