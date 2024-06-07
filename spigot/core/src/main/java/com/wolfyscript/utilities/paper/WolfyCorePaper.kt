package com.wolfyscript.utilities.paper

import com.wolfyscript.utilities.bukkit.PlatformImpl
import com.wolfyscript.utilities.bukkit.WolfyCoreCommon
import com.wolfyscript.utilities.platform.Platform
import com.wolfyscript.utilities.platform.PlatformType

class WolfyCorePaper(bootstrap: WolfyCorePaperBootstrap?) : WolfyCoreCommon(bootstrap!!) {

    override val platform: PlatformImpl = PlatformImpl(this, PlatformType.PAPER)

    override fun enable() {
        super.enable()
        platform.init()
    }

    override fun disable() {
        super.disable()
        platform.unLoad()
    }
}
