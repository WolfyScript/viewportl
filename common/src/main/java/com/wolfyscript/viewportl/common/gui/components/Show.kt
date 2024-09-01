package com.wolfyscript.viewportl.common.gui.components

import com.wolfyscript.scafall.identifier.StaticNamespacedKey
import com.wolfyscript.viewportl.Viewportl
import com.wolfyscript.viewportl.common.gui.BuildContext
import com.wolfyscript.viewportl.gui.components.NativeComponent
import com.wolfyscript.viewportl.gui.components.Show

@StaticNamespacedKey(key = "show")
class ShowImpl(
    parent: NativeComponent?,
    viewportl: Viewportl,
    val context: BuildContext,
) : AbstractNativeComponentImpl<Show>("", viewportl, parent), Show