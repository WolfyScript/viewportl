package com.wolfyscript.viewportl.gui.compose.layout

class Size(
    val slot: Slot,
    val dp: Dp,
) {

    companion object {
        val Zero = Size(0.slots, 0.dp)

        val Unspecified = Size(Int.MIN_VALUE.slots, Int.MIN_VALUE.dp)
    }

    operator fun plus(other: Size): Size {
        return Size(
            slot = (slot.value + other.slot.value).slots,
            dp = (dp.value + other.dp.value).dp
        )
    }

    operator fun minus(other: Size): Size {
        return Size(
            slot = (slot.value - other.slot.value).slots,
            dp = (dp.value - other.dp.value).dp
        )
    }

    operator fun div(other: Size): Size {
        return Size(
            slot = (slot.value / other.slot.value).slots,
            dp = (dp.value / other.dp.value).dp
        )
    }

    operator fun div(other: Int): Size {
        return Size(
            slot = (slot.value / other).slots,
            dp = (dp.value / other).dp
        )
    }

    operator fun times(factor: Int): Size {
        return Size(
            slot = (slot.value * factor).slots,
            dp = (dp.value * factor).dp
        )
    }

    operator fun unaryMinus(): Size {
        return Size(
            slot = (-slot.value).slots,
            dp = (-dp.value).dp
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Size) return false

        if (slot != other.slot) return false
        if (dp != other.dp) return false

        return true
    }

    override fun hashCode(): Int {
        var result = slot.hashCode()
        result = 31 * result + dp.hashCode()
        return result
    }

    override fun toString(): String {
        return "(slot=$slot, dp=$dp)"
    }
}

@JvmInline
value class Slot(val value: Int)

@JvmInline
value class Dp(val value: Int)

infix fun Int.slotsDp(dp: Int): Size {
    return Size(Slot(this), Dp(dp))
}

infix fun Slot.or(dp: Dp): Size {
    return Size(this, dp)
}

infix fun Dp.or(slot: Slot): Size {
    return Size(slot, this)
}

val Int.slots get() = Slot(this)

val Int.dp get() = Dp(this)

val Int.slotsSize get() = Size(slot = this.slots, dp = 0.dp)

val Int.dpSize get() = Size(slot = 0.slots, dp = this.dp)