package com.wolfyscript.viewportl.gui.model

import androidx.compose.runtime.Composable
import com.wolfyscript.scafall.identifier.Key
import java.util.UUID

/**
 * Stores the data in the shared store of the [UIRuntime][com.wolfyscript.viewportl.gui.UIRuntime].
 *
 * This data is shared between different viewers of the same [UIRuntime][com.wolfyscript.viewportl.gui.UIRuntime].
 *
 * @param storeOwner (Optional) the [StoreOwner] that stores the data
 * @param key A unique Key identifying the data in the store
 * @param initializer Initialises the data if it is not yet present in the store
 */
@Composable
inline fun <reified S: Store> sharedStore(
    storeOwner: StoreOwner = LocalStoreOwner.current,
    key: Key,
    noinline initializer: () -> S
): S {
    val existing = storeOwner.sharedStore.get<S>(key)
    if (existing != null) {
        return existing
    }
    val newStore = initializer()
    storeOwner.sharedStore[key] = newStore
    return newStore
}

/**
 * Stores the data in the viewer specific store of the [com.wolfyscript.viewportl.gui.UIRuntime]
 *
 * @param storeOwner (Optional) the [StoreOwner] that stores the data
 * @param key A unique Key identifying the data in the store
 * @param initializer Initialises the data if it is not yet present in the store
 */
@Composable
inline fun <reified S: Store> store(
    storeOwner: StoreOwner = LocalStoreOwner.current,
    key: Key,
    noinline initializer: (viewer: UUID) -> S
): S {
    val view = LocalView.current
    val store = storeOwner.getViewerStore(view.viewer)
    val existing = store.get<S>(key)
    if (existing != null) {
        return existing
    }
    val newStore = initializer(view.viewer)
    store[key] = newStore
    return newStore
}
