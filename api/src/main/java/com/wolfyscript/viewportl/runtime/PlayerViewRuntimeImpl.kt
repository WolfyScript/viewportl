package com.wolfyscript.viewportl.runtime

import com.wolfyscript.viewportl.Viewportl
import java.util.UUID
import kotlin.coroutines.CoroutineContext

internal class PlayerViewRuntimeImpl(val player: UUID, val viewportl: Viewportl) : PlayerViewRuntime {

    override var activeRuntime: UIRuntime? = null

    private var usedCoroutineContext: CoroutineContext? = null
    private var ownRuntime: UIRuntime? = null

    override fun getOwn(coroutineContext: CoroutineContext): UIRuntime {
        if (viewportl.server == null) {
            throw IllegalStateException("Cannot run on client") // TODO: move to server package
        }
        if (ownRuntime == null || usedCoroutineContext != coroutineContext) {
            ownRuntime = viewportl.server!!.guiFactory.createUIRuntime(viewportl, coroutineContext, player)
            usedCoroutineContext = coroutineContext
        }
        return ownRuntime!!
    }

}