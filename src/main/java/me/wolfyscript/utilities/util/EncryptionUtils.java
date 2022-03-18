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

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

public class EncryptionUtils {

    /**
     * Encodes String with Base64
     *
     * @param str the String to be encoded
     * @return the Base64 encoded String
     */
    public static String getBase64EncodedString(String str) {
        return Base64.getEncoder().encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    @Deprecated
    public static String getCode() {
        var random = new Random();
        var alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int x = alphabet.length();
        StringBuilder sB = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sB.append(alphabet.charAt(random.nextInt(x)));
        }
        return sB.toString();
    }
}
