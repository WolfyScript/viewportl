package com.wolfyscript.utilities.spigot

import com.wolfyscript.utilities.bukkit.PlatformImpl
import com.wolfyscript.utilities.bukkit.WolfyCoreCommon
import com.wolfyscript.utilities.platform.PlatformType

/**
 * The core implementation of WolfyUtils.<br></br>
 * It manages the core plugin of WolfyUtils and there is only one instance of it.<br></br>
 *
 * If you want to use the plugin specific API, see [com.wolfyscript.utilities.WolfyUtils] & [WolfyUtilsBukkit]
 */
class WolfyCoreSpigot(plugin: WolfyCoreSpigotBootstrap?) : WolfyCoreCommon(plugin!!) {

    override val platform: PlatformImpl = PlatformImpl(this, PlatformType.SPIGOT)

    override fun enable() {
        super.enable()
        platform.init()
    }

    override fun disable() {
        super.disable()
        platform.unLoad()
    }
}
