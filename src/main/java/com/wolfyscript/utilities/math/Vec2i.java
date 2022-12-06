package com.wolfyscript.utilities.math;

import com.google.common.base.Objects;
import com.google.common.primitives.Doubles;
import org.jetbrains.annotations.NotNull;

public class Vec2i {

    private int x;
    private int y;

    public Vec2i(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    /**
     * Adds a vector to this one
     *
     * @param vec The other vector
     * @return the same vector
     */
    @NotNull
    public Vec2i add(@NotNull Vec2i vec) {
        x += vec.x;
        y += vec.y;
        return this;
    }

    /**
     * Subtracts a vector from this one.
     *
     * @param vec The other vector
     * @return the same vector
     */
    @NotNull
    public Vec2i subtract(@NotNull Vec2i vec) {
        x -= vec.x;
        y -= vec.y;
        return this;
    }

    /**
     * Multiplies the vector by another.
     *
     * @param vec The other vector
     * @return the same vector
     */
    @NotNull
    public Vec2i multiply(@NotNull Vec2i vec) {
        x *= vec.x;
        y *= vec.y;
        return this;
    }

    /**
     * Divides the vector by another.
     *
     * @param vec The other vector
     * @return the same vector
     */
    @NotNull
    public Vec2i divide(@NotNull Vec2i vec) {
        x /= vec.x;
        y /= vec.y;
        return this;
    }

    /**
     * Copies another vector
     *
     * @param vec The other vector
     * @return the same vector
     */
    @NotNull
    public Vec2i copy(@NotNull Vec2i vec) {
        x = vec.x;
        y = vec.y;
        return this;
    }

    /**
     * Gets the magnitude of the vector, defined as sqrt(x^2+y^2). The
     * value of this method is not cached and uses a costly square-root
     * function, so do not repeatedly call this method to get the vector's
     * magnitude. NaN will be returned if the inner result of the sqrt()
     * function overflows, which will be caused if the length is too long.
     *
     * @return the magnitude
     */
    public double length() {
        return Math.sqrt(lengthSquared());
    }

    /**
     * Gets the magnitude of the vector squared.
     *
     * @return the magnitude
     */
    public int lengthSquared() {
        return x*x+y*y;
    }

    /**
     * Calculates the distance between this vector and another. The value of this
     * method is not cached and uses a costly square-root function, so do not
     * repeatedly call this method to get the vector's magnitude. NaN will be
     * returned if the inner result of the sqrt() function overflows, which
     * will be caused if the distance is too long.
     *
     * @param other The other vector
     * @return the distance
     */
    public double distance(@NotNull Vec2i other) {
        return Math.sqrt(distanceSquared(other));
    }

    /**
     * Get the squared distance between this vector and another.
     *
     * @param other The other vector
     * @return the distance
     */
    public double distanceSquared(@NotNull Vec2i other) {
        int dx = other.x - x;
        int dy = other.y - y;
        return dx*dx + dy*dy;
    }

    /**
     * Gets the angle between this vector and another in radians.
     *
     * @param other The other vector
     * @return angle in radians
     */
    public float angle(@NotNull Vec2i other) {
        double dot = Doubles.constrainToRange(dot(other) / (length() * other.length()), -1.0, 1.0);
        return (float) Math.acos(dot);
    }

    /**
     * Sets this vector to the midpoint between this vector and another.
     *
     * @param other The other vector
     * @return this same vector (now a midpoint)
     */
    @NotNull
    public Vec2i midpoint(@NotNull Vec2i other) {
        x = (x + other.x) / 2;
        y = (y + other.y) / 2;
        return this;
    }

    /**
     * Gets a new midpoint vector between this vector and another.
     *
     * @param other The other vector
     * @return a new midpoint vector
     */
    @NotNull
    public Vec2i getMidpoint(@NotNull Vec2i other) {
        int x = (this.x + other.x) / 2;
        int y = (this.y + other.y) / 2;
        return new Vec2i(x, y);
    }

    /**
     * Performs scalar multiplication, multiplying all components with a
     * scalar.
     *
     * @param m The factor
     * @return the same vector
     */
    @NotNull
    public Vec2i multiply(int m) {
        x *= m;
        y *= m;
        return this;
    }

    /**
     * Performs scalar multiplication, multiplying all components with a
     * scalar.
     *
     * @param m The factor
     * @return the same vector
     */
    @NotNull
    public Vec2i multiply(double m) {
        x *= m;
        y *= m;
        return this;
    }

    /**
     * Performs scalar multiplication, multiplying all components with a
     * scalar.
     *
     * @param m The factor
     * @return the same vector
     */
    @NotNull
    public Vec2i multiply(float m) {
        x *= m;
        y *= m;
        return this;
    }

    /**
     * Calculates the dot product of this vector with another. The dot product
     * is defined as x1*x2+y1*y2+z1*z2. The returned value is a scalar.
     *
     * @param other The other vector
     * @return dot product
     */
    public double dot(@NotNull Vec2i other) {
        return x * other.x + y * other.y;
    }

    /**
     * Converts this vector to a unit vector (a vector with length of 1).
     *
     * @return the same vector
     */
    @NotNull
    public Vec2i normalize() {
        double length = length();
        x /= length;
        y /= length;
        return this;
    }

    /**
     * Zero this vector's components.
     *
     * @return the same vector
     */
    @NotNull
    public Vec2i zero() {
        x = 0;
        y = 0;
        return this;
    }

    /**
     * Converts each component of value <code>-0.0</code> to <code>0.0</code>.
     *
     * @return This vector.
     */
    @NotNull
    Vec2i normalizeZeros() {
        if (x == -0.0D) x = 0;
        if (y == -0.0D) y = 0;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vec2i vec2i = (Vec2i) o;
        return x == vec2i.x && y == vec2i.y;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(x, y);
    }
}
