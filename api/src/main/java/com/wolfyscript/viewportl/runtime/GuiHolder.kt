package com.wolfyscript.viewportl.runtime

import com.wolfyscript.scafall.wrappers.ScafallPlayer

interface GuiHolder {

    val viewManager: UIRuntime

    val currentView: View

    val player: ScafallPlayer?
}