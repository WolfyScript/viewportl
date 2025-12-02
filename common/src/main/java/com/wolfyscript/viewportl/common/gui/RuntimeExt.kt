package com.wolfyscript.viewportl.common.gui

import com.wolfyscript.viewportl.gui.UIRuntime

fun UIRuntime.into() : UIRuntimeImpl {
    return this as UIRuntimeImpl
}
