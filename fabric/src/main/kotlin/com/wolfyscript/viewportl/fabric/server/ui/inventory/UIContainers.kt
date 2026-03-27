package com.wolfyscript.viewportl.fabric.server.ui.inventory

import com.wolfyscript.viewportl.fabric.server.ui.CustomUIContainer
import net.kyori.adventure.text.Component

fun createInventoryUI(size: Int, title: Component): CustomUIContainer {
    return CustomUIContainer(size, title)
}