package com.wolfyscript.viewportl.runtime

import com.wolfyscript.scafall.wrappers.ScafallPlayer

interface GuiHolder {

    companion object {

        fun of(runtime: UIRuntime, view: View, player: ScafallPlayer? = null): GuiHolder {
            return GuiHolderImpl(view, runtime, player)
        }

    }

    val runtime: UIRuntime

    val currentView: View

    val player: ScafallPlayer?
}