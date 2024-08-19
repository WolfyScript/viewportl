package com.wolfyscript.utilities.bukkit

import com.wolfyscript.scafall.spigot.platform.math.MathUtil
import com.wolfyscript.viewportl.gui.GuiUtilsImpl
import com.wolfyscript.utilities.platform.Platform
import com.wolfyscript.utilities.platform.gui.GuiUtils

class PlatformImpl internal constructor(private val core: WolfyCoreCommon) : Platform {

    override val guiUtils: GuiUtils = com.wolfyscript.viewportl.gui.GuiUtilsImpl()

    fun init() {
    }

    fun unLoad() {
    }

}
