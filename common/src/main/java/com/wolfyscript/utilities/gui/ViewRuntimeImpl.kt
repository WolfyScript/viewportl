package com.wolfyscript.utilities.gui

import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.gui.callback.TextInputCallback
import com.wolfyscript.utilities.gui.callback.TextInputTabCompleteCallback
import com.wolfyscript.utilities.gui.interaction.InteractionHandler
import com.wolfyscript.utilities.gui.model.UpdateInformation
import com.wolfyscript.utilities.gui.reactivity.ReactiveSourceImpl
import com.wolfyscript.utilities.gui.rendering.Renderer
import com.wolfyscript.utilities.gui.rendering.RenderingGraph
import java.util.*
import java.util.function.Function

class ViewRuntimeImpl(
    private val wolfyUtils: WolfyUtils,
    rootRouter: Function<ViewRuntime?, RouterBuilder>,
    private val viewers: Set<UUID>
) : ViewRuntime {
    @JvmField
    val id: Long = NEXT_ID++

    // Create rendering & reactivity trees
    val renderingGraph: RenderingGraph = RenderingGraph(this)
    val reactiveSource: ReactiveSourceImpl = ReactiveSourceImpl(this)

    // Create platform specific handlers that handle rendering and interaction
    val renderer: Renderer<*> = wolfyUtils.core.platform().guiUtils().createRenderer(this)

    val interactionHandler: InteractionHandler = wolfyUtils.core.platform().guiUtils().createInteractionHandler(this)
    // Build the components and init the rendering tree
    private val router = rootRouter.apply(this).create(null)

    private var currentRoot: Window? = null

    private val history: Deque<Window> = ArrayDeque()
    private var textInputCallback: TextInputCallback? = null

    private var textInputTabCompleteCallback: TextInputTabCompleteCallback? = null

    fun incomingUpdate(information: UpdateInformation?) {
        interactionHandler.update(information!!)
        renderer.update(information)
    }

    override fun openNew() {
        openNew(*emptyArray())
    }

    override fun openNew(vararg path: String) {
        open(getRouter().open(this, *path))
    }

    override fun open() {
        if (history.isEmpty()) {
            currentMenu.ifPresent { window: Window -> window.close(this) }
            openNew()
        } else {
            currentMenu.ifPresent { window: Window -> this.open(window) }
        }
    }

    private fun open(window: Window) {
        setCurrentRoot(window)


        renderer.changeWindow(window)
        interactionHandler.init(window)

        wolfyUtils.core.platform().scheduler().syncTask(wolfyUtils) {
            renderer.render()
            reactiveSource.owner()?.update()
        }
    }

    override fun openPrevious() {
        history.poll() // Remove active current menu
        val window = history.peek()
        currentMenu.ifPresent { w: Window -> w.close(this) }
        open(window)
    }

    fun setCurrentRoot(currentRoot: Window?) {
        this.currentRoot = currentRoot
    }

    override fun getCurrentMenu(): Optional<Window> {
        return Optional.ofNullable(currentRoot)
    }

    override fun getWolfyUtils(): WolfyUtils {
        return wolfyUtils
    }

    override fun getRouter(): Router {
        return router
    }

    override fun getViewers(): Set<UUID> {
        return java.util.Set.copyOf(viewers)
    }

    override fun textInputCallback(): Optional<TextInputCallback> {
        return Optional.ofNullable(textInputCallback)
    }

    override fun setTextInputCallback(textInputCallback: TextInputCallback) {
        this.textInputCallback = textInputCallback
    }

    override fun textInputTabCompleteCallback(): Optional<TextInputTabCompleteCallback> {
        return Optional.ofNullable(textInputTabCompleteCallback)
    }

    override fun setTextInputTabCompleteCallback(textInputTabCompleteCallback: TextInputTabCompleteCallback) {
        this.textInputTabCompleteCallback = textInputTabCompleteCallback
    }

    override fun id(): Long {
        return id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as ViewRuntimeImpl
        return id == that.id
    }

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    companion object {
        private var NEXT_ID = Long.MIN_VALUE
    }
}
