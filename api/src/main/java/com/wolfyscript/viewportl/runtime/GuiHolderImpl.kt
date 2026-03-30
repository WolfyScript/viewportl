package com.wolfyscript.viewportl.runtime

import com.wolfyscript.scafall.wrappers.ScafallPlayer

class GuiHolderImpl(
    override val currentView: View,
    override val viewManager: UIRuntime,
    override val player: ScafallPlayer? = null
) : GuiHolder