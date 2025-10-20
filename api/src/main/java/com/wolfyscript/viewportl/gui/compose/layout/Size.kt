package com.wolfyscript.viewportl.gui.compose.layout

class Size(
    val slot: Slot? = null,
    val dp: Dp? = null,
)

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