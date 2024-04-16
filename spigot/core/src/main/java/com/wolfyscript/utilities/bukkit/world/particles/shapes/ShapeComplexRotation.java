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

package com.wolfyscript.utilities.bukkit.world.particles.shapes;

import com.wolfyscript.utilities.bukkit.BukkitNamespacedKey;
import com.wolfyscript.utilities.bukkit.math.MathUtil;
import java.util.Objects;
import java.util.function.Consumer;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

/**
 * A complex shape that rotates the contained shape with the given settings.<br>
 *
 * There is no limit at what the sub shape must be. Any kind of {@link Shape} can be used, including complex shapes like {@link ShapeComplexRotation}, {@link ShapeComplexCompound}, etc.
 */
public class ShapeComplexRotation extends Shape {

    public static final BukkitNamespacedKey KEY = BukkitNamespacedKey.wolfyutilties("complex/rotation");

    private final Vector angleMultiplier;
    private final Vector angle;
    private final Shape shape;

    /**
     * Only used for Jackson deserialization.
     */
    ShapeComplexRotation() {
        this(new Vector(), new ShapeCircle());
    }

    public ShapeComplexRotation(@NotNull Shape shape) {
        this(new Vector(), shape);
    }

    public ShapeComplexRotation(@NotNull Vector angle, @NotNull Shape shape) {
        this(angle, shape, new Vector());
    }

    public ShapeComplexRotation(@NotNull Vector angle, @NotNull Shape shape, @NotNull Vector angleMultiplier) {
        super(KEY);
        this.shape = Objects.requireNonNull(shape, "Shape cannot be null!");
        this.angleMultiplier = Objects.requireNonNull(angleMultiplier, "Angle multiplier cannot be null!");
        this.angle = Objects.requireNonNull(angle, "Angle cannot be null!");
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
