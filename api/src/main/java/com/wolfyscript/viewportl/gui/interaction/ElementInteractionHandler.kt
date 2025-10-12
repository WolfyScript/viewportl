package com.wolfyscript.viewportl.gui.interaction

import com.wolfyscript.viewportl.gui.elements.Element

/**
 * Handler that adds interactivity to an [Element].
 *
 * Platform-specific interfaces, that extend this, provide native event functions to handle those platform interactions.
 */
interface ElementInteractionHandler<C: Element>