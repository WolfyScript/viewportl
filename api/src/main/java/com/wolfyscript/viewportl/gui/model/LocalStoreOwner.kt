package com.wolfyscript.viewportl.gui.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.compositionLocalOf

object LocalStoreOwner {

    private val LocalStoreOwner = compositionLocalOf<StoreOwner> { error("LocalStoreOwner not initialized") }

    val current: StoreOwner
        @Composable get() = LocalStoreOwner.current

    infix fun provides(
        storeOwner: StoreOwner
    ): ProvidedValue<StoreOwner> {
        return LocalStoreOwner provides storeOwner
    }

}