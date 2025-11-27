package com.wolfyscript.viewportl.gui.compose.layout

import kotlin.math.max
import kotlin.math.roundToInt

@JvmInline
value class Dp(val value: Float) : Comparable<Dp> {

    companion object {

        val Zero = Dp(0f)
        val Unspecified = Dp(Float.NaN)
        val Infinity = Dp(Float.POSITIVE_INFINITY)

    }

    val isSpecified: Boolean get() = !value.isNaN()

    operator fun plus(other: Dp): Dp = Dp(value + other.value)

    operator fun minus(other: Dp): Dp = Dp(value - other.value)

    operator fun div(other: Dp): Dp = Dp(value / other.value)

    operator fun div(other: Int): Dp = Dp(value / other)

    operator fun div(other: Float): Dp = Dp(value / other)

    operator fun times(other: Dp): Dp = Dp(value * other.value)

    operator fun times(other: Float): Dp = Dp(value * other)

    operator fun times(factor: Int): Dp = Dp(value * factor)

    operator fun unaryMinus(): Dp = Dp(-value)

    fun roundToSlots(): Int {
        // 18 pixels is the size of one slot in the default resource pack
        // This makes it possible to combine both slot based UIs and client-side rendered UIs in the future
        return (value / 18f).roundToInt()
    }

    fun roundToPixels(): Int {
        return value.roundToInt()
    }

    override fun compareTo(other: Dp): Int {
        return if (value.isNaN() || other.value.isNaN()) 0 else value.compareTo(other.value)
    }

}

val Int.slots get() = Dp(this * 18f)

val Int.dp get() = Dp(this.toFloat())

val Float.dp get() = Dp(this)

fun Dp.coerceIn(minimumValue: Dp, maximumValue: Dp): Dp {
    return Dp(value.coerceIn(minimumValue.value, maximumValue.value))
}

fun max(a: Dp, b: Dp): Dp {
    return Dp(max(a.value, b.value))
}