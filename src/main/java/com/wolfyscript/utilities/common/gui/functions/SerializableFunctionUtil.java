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

package com.wolfyscript.utilities.common.gui.functions;

import com.wolfyscript.utilities.common.gui.signal.Signal;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SerializableFunctionUtil {

    public static Collection<Signal<?>> getUsedSignals(Serializable serializable) {
        try {
            // Using serialized lambda we have access to runtime information, such as which outer variables are captured and used inside the lambda.
            // See: https://stackoverflow.com/a/35223119
            SerializedLambda s = getSerializedLambda(serializable);
            List<Signal<?>> signals = new ArrayList<>(s.getCapturedArgCount());
            for (int i = 0; i < s.getCapturedArgCount(); i++) {
                if (s.getCapturedArg(i) instanceof Signal<?> signal) {
                    signals.add(signal);
                }
            }
            return signals;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return List.of();
    }

    private static SerializedLambda getSerializedLambda(Serializable lambda) throws Exception {
        final Method method = lambda.getClass().getDeclaredMethod("writeReplace");
        method.setAccessible(true);
        return (SerializedLambda) method.invoke(lambda);
    }
}
