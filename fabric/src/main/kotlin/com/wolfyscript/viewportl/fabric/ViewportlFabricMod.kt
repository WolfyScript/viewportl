package com.wolfyscript.viewportl.fabric

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.VIEWPORTL_NAMESPACE
import com.wolfyscript.viewportl.common.commands.ViewportlCommands
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.slf4j.LoggerFactory

class ViewportlFabricMod : ModInitializer {

    companion object {
        private var instance: ViewportlFabric? = null

        val viewportl: ViewportlFabric
            get() = instance!!

    }

    private val logger = LoggerFactory.getLogger(javaClass)
    internal lateinit var viewportl: ViewportlFabric

    init {
        ScafallProvider.whenReady {
            viewportl = it.platformManager.registerModule(Key.scafall(VIEWPORTL_NAMESPACE)) {
                ViewportlFabric(logger)
            }
            instance = viewportl
        }
    }

    override fun onInitialize() {

        ServerLifecycleEvents.SERVER_STARTING.register { mcServer ->
            logger.info("[Viewportl] Server starting")

            ScafallProvider.whenReady {
                it.onServerAvailable {
                    logger.info("[Viewportl] Init viewportl server...")

                    viewportl.initServer(mcServer)

                    viewportl.server?.onLoad()
                }
            }

        }

        CommandRegistrationCallback.EVENT.register { dispatcher, context, selection ->
            ViewportlCommands.register(dispatcher)
        }

        ServerLifecycleEvents.SERVER_STOPPED.register { server ->

            viewportl.server?.onUnload()
        }

    }

}