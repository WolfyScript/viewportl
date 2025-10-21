/*
 *     viewportl - multiplatform GUI framework to easily create reactive GUIs
 *     Copyright (C) 2024  WolfyScript
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
package com.wolfyscript.viewportl.gui.elements

import androidx.compose.runtime.Composable
import com.wolfyscript.scafall.wrappers.world.items.ItemStackSnapshot
import net.kyori.adventure.sound.Sound

@Composable
fun Button(icon: () -> ItemStackSnapshot, onClick: () -> Unit) {

    Layout({}, content = {}) { measurables, constraints ->

        layout(constraints.maxWidth, constraints.maxHeight) {

        }
    }

}

/**
 * The properties used to create a button implementation
 */
data class ButtonProperties(
    val icon: () -> ItemStackSnapshot,
    val sound: Sound? = null,
    val onClick: () -> Unit = {},
)

/**
 * A simple button that has an icon and a click callback.
 */
interface Button : Element {
    var icon: () -> ItemStackSnapshot
    var onClick: () -> Unit
    var sound: Sound?
}



