package com.wolfyscript.viewportl.gui.factories

import com.wolfyscript.viewportl.gui.components.*

interface ComponentFactory {

    fun group(properties: GroupProperties)

    fun button(properties: ButtonProperties)

    fun slot(properties: SlotProperties)

    fun router(properties: RouterProperties)

    fun show(properties: ShowProperties)

    fun outlet(properties: OutletProperties)

}