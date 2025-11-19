package com.wolfyscript.viewportl.gui.model

import androidx.compose.runtime.Composable
import com.wolfyscript.scafall.identifier.Key

@Composable
inline fun <reified S: Store> store(
    storeOwner: DataStoreMap = LocalStoreOwner.current,
    key: Key,
    noinline initializer: () -> S
): S {
    val existing = storeOwner.get<S>(key)
    if (existing != null) {
        return existing
    }
    val newStore = initializer()
    storeOwner[key] = newStore
    return newStore
}
