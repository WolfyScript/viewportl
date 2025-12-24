package com.wolfyscript.viewportl.gui.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

class CloseableCoroutineScope(override val coroutineContext: CoroutineContext) : AutoCloseable, CoroutineScope {

    constructor(scope: CoroutineScope) : this(scope.coroutineContext)

    override fun close() = coroutineContext.cancel()

}

internal fun CoroutineScope.asCloseable() = CloseableCoroutineScope(scope = this)
