package com.wolfyscript.viewportl.fabric.client

import com.wolfyscript.scafall.ScafallProvider
import com.wolfyscript.viewportl.fabric.ViewportlFabric
import com.wolfyscript.viewportl.viewportl
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents

class ViewportlClientFabricMod : ClientModInitializer {

    override fun onInitializeClient() {

        ServerLifecycleEvents.SERVER_STARTING.register { server ->}

        ClientLifecycleEvents.CLIENT_STARTED.register { client ->
            ScafallProvider.whenReady { scafall ->
                scafall.onClientAvailable {
                    (scafall.viewportl as ViewportlFabric).initClient(ViewportlClientFabric(client))
                }
            }
        }

    }

}