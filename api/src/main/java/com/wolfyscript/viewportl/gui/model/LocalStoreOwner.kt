package com.wolfyscript.viewportl.gui.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.compositionLocalOf

object LocalStoreOwner {

    private val LocalStoreOwner = compositionLocalOf<DataStoreMap> { error("LocalStoreOwner not initialized") }

    val current: DataStoreMap
        @Composable get() = LocalStoreOwner.current

    infix fun provides(
        storeOwner: DataStoreMap
    ): ProvidedValue<DataStoreMap> {
        return LocalStoreOwner provides storeOwner
    }

}