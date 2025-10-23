package com.wolfyscript.viewportl.gui.compose.layout

class Size(
    val slot: Slot = 0.slots,
    val dp: Dp = 0.dp,
) {

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

val Int.slotsSize get() = Size(slot = this.slots)

val Int.dpSize get() = Size(dp = this.dp)