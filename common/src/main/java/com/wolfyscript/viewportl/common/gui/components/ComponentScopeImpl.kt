package com.wolfyscript.viewportl.common.gui.components

import com.wolfyscript.viewportl.common.gui.ViewRuntimeImpl
import com.wolfyscript.viewportl.gui.components.ComponentScope
import com.wolfyscript.viewportl.gui.reactivity.*

class ComponentScopeImpl(runtimeImpl: ViewRuntimeImpl) : ComponentScope, ReactiveSource by runtimeImpl.reactiveSource {

    override fun interval(intervalInTicks: Long, runnable: Runnable) {
        TODO("Not yet implemented")
    }


}