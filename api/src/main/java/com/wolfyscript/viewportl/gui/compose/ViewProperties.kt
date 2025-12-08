package com.wolfyscript.viewportl.gui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import com.wolfyscript.scafall.adventure.deser
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.gui.View
import com.wolfyscript.viewportl.gui.ViewType
import com.wolfyscript.viewportl.gui.compose.layout.Dp
import com.wolfyscript.viewportl.gui.model.LocalView
import net.kyori.adventure.text.Component
import kotlin.math.min

@Composable
fun viewProperties(key: Key, config: ViewPropertyScope.() -> Unit) {
    val view = LocalView.current
    remember(config, key) { ViewPropertiesModifier(view, key, config) }
}

class ViewProperties(
    val type: Type = Type(ViewType.CUSTOM),
    val dimensions: Dimensions,
    val title: Title,
) {

    class Title(
        val component: Component?,
    )

    class Dimensions(
        val width: Dp,
        val height: Dp,
    ) {
        /*
     * Inventory UI specific properties
     */
        val inventoryWidth
            get() = width.roundToSlots().coerceIn(9, 9)
        val inventoryHeight
            get() = height.roundToSlots().coerceAtMost(6)
        val inventorySize: Int
            get() = min(54, height.roundToSlots() * 9)
    }

    class Type(
        val type: ViewType,
    )

}

class ViewPropertiesOverride(
    val type: ViewProperties.Type? = null,
    val dimensions: ViewProperties.Dimensions? = null,
    val title: ViewProperties.Title? = null,
) {

    fun override(parentOverride: ViewPropertiesOverride?): ViewPropertiesOverride {
        if (parentOverride != null) {
            return ViewPropertiesOverride(
                type = type ?: parentOverride.type,
                dimensions = dimensions ?: parentOverride.dimensions,
                title = title ?: parentOverride.title,
            )
        }
        return this
    }


}

class ViewPropertiesModifier(val view: View, val key: Key, val config: ViewPropertyScope.() -> Unit) :
    RememberObserver {

    override fun onRemembered() {
        val scope = ViewPropertyScopeImpl()
        scope.config()
        view.overrideProperties(key, scope.build())
    }

    override fun onForgotten() {
        view.removePropertiesOverride(key)
    }

    override fun onAbandoned() {
        view.removePropertiesOverride(key)
    }

}

interface ViewPropertyScope {

    fun title(title: String) = title(title.deser())

    fun title(title: Component)

    fun size(width: Dp, height: Dp)

    fun type(type: ViewType)

}

class ViewPropertyScopeImpl : ViewPropertyScope {

    var title: ViewProperties.Title? = null
    var dimensions: ViewProperties.Dimensions? = null
    var type: ViewProperties.Type? = null

    override fun title(title: Component) {
        this.title = ViewProperties.Title(title)
    }

    override fun size(
        width: Dp,
        height: Dp,
    ) {
        this.dimensions = ViewProperties.Dimensions(width, height)
    }

    override fun type(type: ViewType) {
        this.type = ViewProperties.Type(type)
    }

    internal fun build(): ViewPropertiesOverride {
        return ViewPropertiesOverride(type, dimensions, title)
    }

}