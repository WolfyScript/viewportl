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

package me.wolfyscript.utilities.util.particles.animators;

import com.google.common.base.Preconditions;
import me.wolfyscript.utilities.util.math.MathUtil;
import me.wolfyscript.utilities.util.NamespacedKey;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ShapeRotation extends Shape {

    public static final NamespacedKey KEY = NamespacedKey.wolfyutilties("complex/rotation");

    private final Vector angleMultiplier;
    private final Vector angle;
    private final Shape shape;

    public ShapeRotation() {
        this(new Vector(), new ShapeCircle());
    }

    public ShapeRotation(@NotNull Shape shape) {
        this(new Vector(), shape);
    }

    public ShapeRotation(Vector angle, @NotNull Shape shape) {
        this(new Vector(), angle, shape);
    }

    public ShapeRotation(Vector angleMultiplier, Vector angle, @NotNull Shape shape) {
        super(KEY);
        Preconditions.checkArgument(shape != null, "Shape cannot be null!");
        Preconditions.checkArgument(angle != null, "Angle cannot be null!");
        Preconditions.checkArgument(angleMultiplier != null, "Angle modifier cannot be null!");
        this.shape = shape;
        this.angleMultiplier = angleMultiplier;
        this.angle = angle;
    }

    @Override
    public void drawVectors(double time, Consumer<Vector> drawVector) {
        var rotation = new Vector(time * angleMultiplier.getX() + angle.getX(), time * angleMultiplier.getY() + angle.getY(), time * angleMultiplier.getZ() + angle.getZ());
        double[][] angles = MathUtil.getRotationAngles(MathUtil.degreeToRadiansVector(rotation));
        shape.drawVectors(time, vec -> {
            MathUtil.rotate(vec, angles);
            drawVector.accept(vec);
        });
    }

}
