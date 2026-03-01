package com.wolfyscript.viewportl.common.gui

import com.wolfyscript.viewportl.runtime.UIRuntime

fun UIRuntime.into() : UIRuntimeImpl {
    return this as UIRuntimeImpl
}
