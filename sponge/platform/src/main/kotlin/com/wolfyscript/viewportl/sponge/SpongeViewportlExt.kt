package com.wolfyscript.viewportl.sponge

import com.wolfyscript.scafall.PluginWrapper
import com.wolfyscript.scafall.Scafall
import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.sponge.init
import com.wolfyscript.viewportl.Viewportl
import org.spongepowered.plugin.PluginContainer

private var viewportlInstance: SpongeViewportl? = null

val Viewportl.Companion.instance: Viewportl
    get() = viewportlInstance ?: throw IllegalStateException("Viewportl is not initialized.")


/**
 * Initiates the Viewportl library and loads all required data.
 *
 * When Scafall wasn't yet initiated this will [init Scafall][Scafall.Companion.init] using the passed [plugin]
 *
 */
fun Viewportl.Companion.init(plugin: PluginContainer) {
    if (!ScafallProvider.registered()) {
        Scafall.init(plugin)
    }
    init()
}

/**
 * Initiates the Viewportl library and loads all required data.
 *
 * The [plugin] param requires [Scafall] to be initiated by default!
 */
fun Viewportl.Companion.init(plugin: PluginWrapper = ScafallProvider.get().corePlugin) {
    viewportlInstance = SpongeViewportl()
    viewportlInstance!!.init()
}
