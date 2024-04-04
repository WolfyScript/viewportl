package com.wolfyscript.utilities.gui.components

import com.wolfyscript.utilities.KeyedStaticId
import com.wolfyscript.utilities.WolfyUtils
import com.wolfyscript.utilities.gui.Component
import com.wolfyscript.utilities.gui.Renderable
import com.wolfyscript.utilities.gui.ViewRuntimeImpl
import com.wolfyscript.utilities.gui.rendering.RenderProperties
import java.util.*
import kotlin.math.abs

@KeyedStaticId(key = "cluster")
class ComponentGroupImpl(
    internalID: String,
    wolfyUtils: WolfyUtils,
    parent: Component?,
    properties: RenderProperties,
    private val children: MutableList<Component>
) : AbstractComponentImpl(
    internalID, wolfyUtils, parent, properties
), ComponentGroup {
    private val width: Int
    private val height: Int

    init {
        val topLeft = 54
        this.width = 1
        this.height = (abs((topLeft / 9).toDouble()) + 1).toInt()
    }

    override fun childComponents(): Set<Component> {
        return HashSet(children)
    }

    override fun getChild(id: String): Optional<out Component> {
        return Optional.empty()
    }

    override fun width(): Int {
        return width
    }

    override fun height(): Int {
        return height
    }

    override fun remove(viewRuntimeImpl: ViewRuntimeImpl, nodeId: Long, parentNode: Long) {
        viewRuntimeImpl.renderingGraph.removeNode(nodeId)
    }

    override fun insert(viewRuntimeImpl: ViewRuntimeImpl, parentNode: Long) {
        val id = viewRuntimeImpl.renderingGraph.addNode(this)
        viewRuntimeImpl.renderingGraph.insertNodeChild(id, parentNode)

        for (child in children) {
            if (child is Renderable) {
                child.insert(viewRuntimeImpl, id)
            }
        }
    }
}
