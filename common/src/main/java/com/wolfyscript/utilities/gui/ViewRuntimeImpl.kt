package com.wolfyscript.utilities.gui

import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.gui.interaction.InteractionHandler
import com.wolfyscript.utilities.gui.model.UpdateInformation
import com.wolfyscript.utilities.gui.reactivity.ReactiveSourceImpl
import com.wolfyscript.utilities.gui.rendering.Renderer
import com.wolfyscript.utilities.gui.rendering.ModelGraph
import java.util.*
import java.util.function.Function

class ViewRuntimeImpl(
    override val wolfyUtils: WolfyUtils,
    rootRouter: Function<ViewRuntime, Window>,
    override val viewers: Set<UUID>,
) : ViewRuntime {
    @JvmField
    val id: Long = NEXT_ID++

    // Create rendering & reactivity trees
    val modelGraph: ModelGraph = ModelGraph(this)
    val reactiveSource: ReactiveSourceImpl = ReactiveSourceImpl(this)

    // Create platform specific handlers that handle rendering and interaction
    val renderer: Renderer<*> = wolfyUtils.core.platform.guiUtils.createRenderer(this)

    val interactionHandler: InteractionHandler = wolfyUtils.core.platform.guiUtils.createInteractionHandler(this)
    // Build the components and init the rendering tree
    private var currentRoot: Window? = rootRouter.apply(this)
    override val currentMenu: Window?
        get() = currentRoot

    private val history: Deque<Window> = ArrayDeque()

    fun incomingUpdate(information: UpdateInformation?) {
        interactionHandler.update(information!!)
        renderer.update(information)
    }

    override fun openNew() {
        openNew(*emptyArray())
    }

    override fun openNew(vararg path: String) {
        currentMenu?.apply { open(this) }
    }

    override fun open() {
        if (history.isEmpty()) {
            currentMenu?.close()
            openNew()
        } else {
            currentMenu?.let { open(it) }
        }
    }

    private fun open(window: Window) {
        setCurrentRoot(window)

        renderer.changeWindow(window)
        interactionHandler.init(window)

        wolfyUtils.core.platform.scheduler.syncTask(wolfyUtils) {
            renderer.render()
            reactiveSource.owner()?.update()
        }
    }

    override fun openPrevious() {
        history.poll() // Remove active current menu
        val window = history.peek()
        currentMenu?.close()
        open(window)
    }

    fun setCurrentRoot(currentRoot: Window?) {
        this.currentRoot = currentRoot
    }

    fun getCurrentMenu(): Optional<Window> {
        return Optional.ofNullable(currentRoot)
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
