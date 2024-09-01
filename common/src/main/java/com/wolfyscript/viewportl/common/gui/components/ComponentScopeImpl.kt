package com.wolfyscript.viewportl.common.gui.components

import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.gui.components.ComponentScope
import com.wolfyscript.viewportl.gui.reactivity.*

class ComponentScopeImpl(val runtime: ViewRuntimeImpl) : ComponentScope, ReactiveSource by runtime.reactiveSource {

    override fun interval(intervalInTicks: Long, runnable: Runnable) {
        val task = runtime.viewportl.scafall.scheduler.task(runtime.viewportl.scafall.corePlugin)
            .interval(intervalInTicks)
            .delay(1)
            .execute {
                runnable.run()
                runtime.reactiveSource.runEffects()
            }
            .build()

        createCleanup {
            task.cancel()
        }
    }

}