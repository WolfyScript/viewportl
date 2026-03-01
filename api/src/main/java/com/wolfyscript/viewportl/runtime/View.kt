package com.wolfyscript.viewportl.runtime

import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.gui.input.TextInputCallback
import com.wolfyscript.viewportl.gui.input.TextInputTabCompleteCallback
import com.wolfyscript.viewportl.gui.rendering.Renderer
import com.wolfyscript.viewportl.ui.ViewProperties
import com.wolfyscript.viewportl.ui.ViewPropertiesOverride
import java.util.UUID

/**
 * Holds the composition of the UI and other properties of the viewport (such as title, background, etc.)
 */
interface View {

    /**
     * Renders this window state using the specified [renderer]
     */
    fun render(renderer: Renderer<*>)

    fun close()

    var resourcePath: String?
    /**
     * The current active properties of this view, such as dimensions of the viewport, or title.
     */
    val properties: ViewProperties

    /**
     * Allows to override the current properties with new values.
     *
     * This should be configured using the [viewProperties][com.wolfyscript.viewportl.ui.viewProperties] fun within a Composition, which
     * allows to modify the properties when a Composable joins the Composition and removing the modification automatically when it leaves.
     *
     * @see com.wolfyscript.viewportl.ui.viewProperties
     */
    fun overrideProperties(key: Key, properties: ViewPropertiesOverride)

    /**
     * Removes the specified properties override.
     *
     * When using the [viewProperties][com.wolfyscript.viewportl.ui.viewProperties] fun within a Composable,
     * then this is done automatically when that Composable leaves the Composition.
     *
     * @see com.wolfyscript.viewportl.ui.viewProperties
     */
    fun removePropertiesOverride(key: Key)

    /**
     * Gets the Viewportl instance this Window belongs to
     *
     * @return The Viewportl instance
     */
    val viewportl: Viewportl

    val viewer: UUID

    var onTextInput: TextInputCallback?

    var onTextInputTabComplete: TextInputTabCompleteCallback?

}