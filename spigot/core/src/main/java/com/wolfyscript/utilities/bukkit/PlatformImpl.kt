package com.wolfyscript.utilities.bukkit

import com.wolfyscript.utilities.bukkit.gui.GuiUtilsImpl
import com.wolfyscript.utilities.bukkit.scheduler.SchedulerImpl
import com.wolfyscript.utilities.bukkit.world.items.ItemsImpl
import com.wolfyscript.utilities.platform.Platform
import com.wolfyscript.utilities.platform.PlatformType
import com.wolfyscript.utilities.platform.gui.GuiUtils
import com.wolfyscript.utilities.platform.scheduler.Scheduler
import com.wolfyscript.utilities.platform.world.items.Items

class PlatformImpl internal constructor(private val core: WolfyCoreCommon, override val type: PlatformType) : Platform {

    override val audiences: AudiencesImpl = AudiencesImpl(core)
    override val guiUtils: GuiUtils = GuiUtilsImpl()
    override val scheduler: Scheduler = SchedulerImpl()
    override val items: Items = ItemsImpl(core)

    fun init() {
        audiences.init()
    }

    fun unLoad() {
        audiences.unLoad()
    }

}
