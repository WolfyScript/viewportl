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

package me.wolfyscript.utilities.util.math;

import org.bukkit.util.Vector;

public class MathUtil {

    private MathUtil() {

    }

    public static Vector rotateAroundAxisX(Vector v, double cos, double sin) {
        double y = v.getY() * cos - v.getZ() * sin;
        double z = v.getY() * sin + v.getZ() * cos;
        return v.setY(y).setZ(z);
    }

    public static Vector rotateAroundAxisY(Vector v, double cos, double sin) {
        double x = v.getX() * cos + v.getZ() * sin;
        double z = v.getX() * -sin + v.getZ() * cos;
        return v.setX(x).setZ(z);
    }

    public static Vector rotateAroundAxisZ(Vector v, double cos, double sin) {
        double x = v.getX() * cos - v.getY() * sin;
        double y = v.getX() * sin + v.getY() * cos;
        return v.setX(x).setY(y);
    }

    public static Vector degreeToRadiansVector(Vector angleVec) {
        angleVec.setX(Math.toRadians(angleVec.getX()));
        angleVec.setY(Math.toRadians(angleVec.getY()));
        angleVec.setZ(Math.toRadians(angleVec.getZ()));
        return angleVec;
    }

    public static Vector rotate(Vector vec, Vector rotationVec) {
        return rotate(vec, getRotationAngles(rotationVec));
    }

    public static Vector rotate(Vector vec, double[][] angles) {
        rotateAroundAxisX(vec, angles[0][0], angles[0][1]);
        rotateAroundAxisY(vec, angles[1][0], angles[1][1]);
        rotateAroundAxisZ(vec, angles[2][0], angles[2][1]);
        return vec;
    }

    public static double[][] getRotationAngles(Vector rotationVec) {
        return new double[][]{
                { Math.cos(rotationVec.getX()), Math.sin(rotationVec.getX()) },
                { Math.cos(-rotationVec.getY()), Math.sin(-rotationVec.getY()) },
                { Math.cos(rotationVec.getZ()), Math.sin(rotationVec.getZ()) }
        };
    }

}
