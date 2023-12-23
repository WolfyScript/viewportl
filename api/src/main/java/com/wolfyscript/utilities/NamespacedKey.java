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

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Namespaced;
import org.intellij.lang.annotations.Pattern;
import org.intellij.lang.annotations.Subst;

/**
 * The NamespacedKey is used to manage and identify resources and prevent conflicts with for example plugins.<br>
 * It consists of a unique namespace and a key. (The same key can exist in different namespaces) <br>
 * <br>
 * Usually the namespace should be the plugins' (lowercase) name and identifies resources as part of that plugin.<br>
 * e.g. when registering data using the Registry, etc.<br>
 * <br>
 * They can however be used inside a plugin itself with non-plugin namespaces, when resources are only accessible internally.<br>
 * e.g. GuiWindow where the namespace is the GuiClusters' id and the key the GuiWindows's id, etc.<br>
 *
 */
public interface NamespacedKey {

    /**
     * Gets the namespace of this object.
     *
     * @return The namespace.
     */
    String getNamespace();

    /**
     * Gets the key of this object as a String.
     *
     * @return The key.
     */
    String getKey();

    /**
     * Gets the key part of this NamespacedKey.
     *
     * @return The key part.
     * @since 3.16.1.0
     */
    Key getKeyComponent();

    String toString(String separator);

    default net.kyori.adventure.key.Key toKyoriKey() {
        return net.kyori.adventure.key.Key.key(toString(":"));
    }

    /**
     * Represents the key part of the NamespacedKey.
     * The idea behind this component is to make it easier to manage folders specified in the key and provide util methods for it.<br>
     * Such as
     * <pre>
     *     "folder/sub_folder/another/file"</pre>
     * is converted into
     * <pre>
     *     folder:  "folder/sub_folder/another"
     *     key:     "file"
     * </pre>
     */
    interface Key {

        /**
         * Gets the folder of the key component.
         *
         * @return The folder part of this component.
         */
        String getFolder();

        /**
         * Gets the object part of this key component.
         *
         * @return The object of this component.
         */
        String getObject();

        /**
         * Gets the root folder of the folder part of this component.<br>
         * In case the folder doesn't exist it returns an empty String.<br>
         * If the folder part exists, but doesn't contain a "/", then the folder part will be returned as is.<br>
         *
         * @return The root folder; otherwise, an empty String if the key has no folder, or the folder itself if the folder contains no subfolder.
         */
        String getRoot();

        /**
         * Gets the string representation of the folder and object part of this component.<br>
         * The folder and object are concat using "/" in between.<br>
         * e.g. <code>folder/subfolder object -> folder/subfolder/object</code><br>
         * If no folder is available it will just return the object part.
         *
         * @return The folder and key part separated by a "/".
         */
        @Override
        String toString();

        /**
         * Gets the string representation of the folder and object part of this component.<br>
         * The folder and object are concat using a specified separator in between.<br>
         * e.g. <code>separator=":" folder/subfolder object -> folder/subfolder:object</code><br>
         * If no folder is available it will just return the object part.
         *
         * @param separator The separator between folder and object.
         * @return The folder and key part separated by the specified separator.
         */
        String toString(String separator);

        /**
         * Gets the string representation of the folder and object part of this component.<br>
         * The folder and object are concat using a specified separator in between.<br>
         * <code>forceSeparator=true|false, separator=":" </code><br>
         * <code>folder/subfolder object -> folder/subfolder:object</code><br>
         * <br>
         * <code>forceSeparator=true, separator="/"</code><br>
         * <code>  object -> /object</code><br>
         *
         * @param separator The separator between folder and object.
         * @param forceSeparator Add the separator in the front of the object, even if no folder exists.
         * @return The folder and key part separated by the specified separator.
         */
        String toString(String separator, boolean forceSeparator);

    }

}
