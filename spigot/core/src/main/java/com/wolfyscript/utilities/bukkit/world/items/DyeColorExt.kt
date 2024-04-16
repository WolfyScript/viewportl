package com.wolfyscript.utilities.bukkit.world.items

import com.wolfyscript.utilities.world.items.DyeColor

fun org.bukkit.DyeColor.toWrapper() : DyeColor {
    return when(this) {
        org.bukkit.DyeColor.WHITE -> DyeColor.WHITE
        org.bukkit.DyeColor.ORANGE -> DyeColor.ORANGE
        org.bukkit.DyeColor.MAGENTA -> DyeColor.MAGENTA
        org.bukkit.DyeColor.LIGHT_BLUE -> DyeColor.LIGHT_BLUE
        org.bukkit.DyeColor.YELLOW -> DyeColor.YELLOW
        org.bukkit.DyeColor.LIME -> DyeColor.LIME
        org.bukkit.DyeColor.PINK -> DyeColor.PINK
        org.bukkit.DyeColor.GRAY -> DyeColor.GRAY
        org.bukkit.DyeColor.LIGHT_GRAY -> DyeColor.LIGHT_GRAY
        org.bukkit.DyeColor.CYAN -> DyeColor.CYAN
        org.bukkit.DyeColor.PURPLE -> DyeColor.PURPLE
        org.bukkit.DyeColor.BLUE -> DyeColor.BLUE
        org.bukkit.DyeColor.BROWN -> DyeColor.BROWN
        org.bukkit.DyeColor.GREEN -> DyeColor.GREEN
        org.bukkit.DyeColor.RED -> DyeColor.RED
        org.bukkit.DyeColor.BLACK -> DyeColor.BLACK
        else -> DyeColor.valueOf(toString())
    }
}

fun DyeColor.toBukkit() : org.bukkit.DyeColor {
    return when(this) {
        DyeColor.WHITE -> org.bukkit.DyeColor.WHITE
        DyeColor.ORANGE -> org.bukkit.DyeColor.ORANGE
        DyeColor.MAGENTA -> org.bukkit.DyeColor.MAGENTA
        DyeColor.LIGHT_BLUE -> org.bukkit.DyeColor.LIGHT_BLUE
        DyeColor.YELLOW -> org.bukkit.DyeColor.YELLOW
        DyeColor.LIME -> org.bukkit.DyeColor.LIME
        DyeColor.PINK -> org.bukkit.DyeColor.PINK
        DyeColor.GRAY -> org.bukkit.DyeColor.GRAY
        DyeColor.LIGHT_GRAY -> org.bukkit.DyeColor.LIGHT_GRAY
        DyeColor.CYAN -> org.bukkit.DyeColor.CYAN
        DyeColor.PURPLE -> org.bukkit.DyeColor.PURPLE
        DyeColor.BLUE -> org.bukkit.DyeColor.BLUE
        DyeColor.BROWN -> org.bukkit.DyeColor.BROWN
        DyeColor.GREEN -> org.bukkit.DyeColor.GREEN
        DyeColor.RED -> org.bukkit.DyeColor.RED
        DyeColor.BLACK -> org.bukkit.DyeColor.BLACK
        else -> org.bukkit.DyeColor.valueOf(toString())
    }
}
