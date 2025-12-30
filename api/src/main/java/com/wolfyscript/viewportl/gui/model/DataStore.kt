package com.wolfyscript.viewportl.gui.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import com.wolfyscript.scafall.identifier.Key
import com.wolfyscript.viewportl.viewportl
import java.util.*

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
inline fun <reified S : Store> sharedStore(
    key: Key,
    storeOwner: StoreOwner = LocalStoreOwner.current,
    noinline initializer: () -> S = { S::class.java.getConstructor().newInstance() },
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
inline fun <reified S : Store> store(
    key: Key,
    storeOwner: StoreOwner = LocalStoreOwner.current,
    noinline initializer: (viewer: UUID) -> S = { S::class.java.getConstructor().newInstance() },
): S {
    val viewer = LocalView.current.viewer
    val store = storeOwner.getViewerStore(viewer)
    val existing = store.get<S>(key)
    if (existing != null) {
        return existing
    }
    val newStore = initializer(viewer)
    store[key] = newStore
    return newStore
}

val ScopedDataStoreMapStoreKey = Key.viewportl("scoped_store_map")

/**
 * Provides a new temporary [LocalStoreOwner] within the scope of [content], that persists until this composable leaves
 * the composition.
 *
 * The [key] serves a unique identifier for the underlying store that is used.
 */
@Composable
fun tempScopedDataStore(key: Key, content: @Composable () -> Unit) {
    val currentViewer = LocalView.current.viewer
    val currentStoreOwner = LocalStoreOwner.current
    val sharedScopedMapStore = sharedStore<ScopedDataStoreMapStore>(
        storeOwner = currentStoreOwner,
        key = ScopedDataStoreMapStoreKey
    )
    val currentViewerScopedMapStore = store<ScopedDataStoreMapStore>(
        storeOwner = currentStoreOwner,
        key = ScopedDataStoreMapStoreKey
    )

    val scopeStoreOwner = remember {
        object : StoreOwner, RememberObserver {

            override val sharedStore: DataStoreMap
                get() {
                    return sharedScopedMapStore.getOrCreateStore(key)
                }

            override fun getViewerStore(viewer: UUID): DataStoreMap {
                if (viewer != currentViewer) {
                    // Requested a store for a viewer outside of this View!
                    // Need to be careful doing so, because this won't get cleared when it leaves the composition.
                    // TODO: Throw an exception instead to disallow accessing other viewers stores?!?
                    val store = currentStoreOwner.getViewerStore(viewer)
                    val existing = store[ScopedDataStoreMapStoreKey]
                        ?: ScopedDataStoreMapStore().also { store[ScopedDataStoreMapStoreKey] = it }
                    return existing.getOrCreateStore(key)
                }
                return currentViewerScopedMapStore.getOrCreateStore(key)
            }

            override fun onRemembered() {
                // TODO
            }

            override fun onForgotten() {
                sharedScopedMapStore.clearStore(key)
                currentViewerScopedMapStore.clearStore(key)
            }

            override fun onAbandoned() {
                sharedScopedMapStore.clearStore(key)
                currentViewerScopedMapStore.clearStore(key)
            }

        }
    }
    CompositionLocalProvider(LocalStoreOwner provides scopeStoreOwner) {
        content()
    }
}
