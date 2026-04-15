package com.wolfyscript.viewportl.runtime

import com.wolfyscript.scafall.wrappers.ScafallPlayer

internal class GuiHolderImpl(
    override val currentView: View,
    override val runtime: UIRuntime,
    override val player: ScafallPlayer? = null
) : GuiHolder