package com.wolfyscript.viewportl.gui.factories

import com.wolfyscript.viewportl.gui.components.ButtonProperties
import com.wolfyscript.viewportl.gui.components.RouterProperties
import com.wolfyscript.viewportl.gui.components.ShowProperties
import com.wolfyscript.viewportl.gui.components.SlotProperties

interface ComponentFactory {

    fun button(properties: ButtonProperties)

    fun slot(properties: SlotProperties)

    fun router(properties: RouterProperties)

    fun show(properties: ShowProperties)

}