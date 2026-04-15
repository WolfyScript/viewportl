package com.wolfyscript.viewportl.runtime

import kotlin.coroutines.CoroutineContext

interface PlayerViewRuntime {

    var activeRuntime: UIRuntime?

    fun getOwn(coroutineContext: CoroutineContext): UIRuntime

}