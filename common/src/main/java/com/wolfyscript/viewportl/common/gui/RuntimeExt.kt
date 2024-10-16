package com.wolfyscript.viewportl.common.gui

import com.wolfyscript.viewportl.gui.ViewRuntime

fun ViewRuntime<*,*>.into() : ViewRuntimeImpl<*,*> {
    return this as ViewRuntimeImpl
}
