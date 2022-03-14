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

package me.wolfyscript.utilities.util.scripting;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * @deprecated There is no need to use this util class!
 * You can get the correct script engine easily using the {@link ScriptEngineManager}
 * or use Graal.js via org.graalvm.polyglot.Context.
 */
@Deprecated
public class ScriptUtil {

    private ScriptUtil() {
    }

    /**
     * @return The Nashorn scripting engine.
     * @deprecated Will be removed soon, because it's deprecated in Java 11+ and is no longer used!
     */
    @Deprecated
    public static ScriptEngine getEngine() {
        throw new UnsupportedOperationException();
    }
}
