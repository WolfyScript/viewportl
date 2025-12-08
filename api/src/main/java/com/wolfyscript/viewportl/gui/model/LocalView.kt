package com.wolfyscript.viewportl.gui.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.compositionLocalOf
import com.wolfyscript.viewportl.gui.View

object LocalView {

    private val LocalView = compositionLocalOf<View> { error("LocalStoreOwner not initialized") }

    val current: View
        @Composable get() = LocalView.current

    infix fun provides(
        localView: View
    ): ProvidedValue<View> {
        return LocalView provides localView
    }

}