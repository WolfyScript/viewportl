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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface KeyedStaticId {

    String value() default "";

    /**
     * @return The namespace of the key
     */
    String namespace() default "wolfyutilities";

    /**
     *
     * @return The key to the object
     */
    String key() default "";

    class KeyBuilder {

        public static String createKeyString(Class<?> annotated) {
            KeyedStaticId annotation = annotated.getAnnotation(KeyedStaticId.class);
            if (annotation != null) {
                if (!annotation.value().isBlank()) {
                    return annotation.value();
                }
                if (!annotation.namespace().isBlank() && !annotation.key().isBlank()) {
                    return annotation.namespace() + ":" + annotation.key();
                }
            }
            throw new IllegalArgumentException("Invalid static id properties! Either use the value, or both the namespace and key options!");
        }

    }

}
